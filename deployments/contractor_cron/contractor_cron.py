#!/usr/bin/env python
import urllib2, time, sys, logging, logging.config
from threading import Thread, enumerate, Lock
from daemon import Daemon
from Queue import Queue
from locked_iterator import LockedIterator

logging.config.fileConfig("logging.conf")

SERVER = "http://%s.picsorganizer.com/"
SERVERS = ['web1','web2','web3']
con_running = set()
running_lock = Lock()
con_q = Queue()
active_servers = dict((s,False) for s in SERVERS)
logging.info('default server setup : %s', active_servers)

class qcron(Daemon):
	def run(self):
		try:
			logging.info('creating server generator')
			server_g = server_generator(active_servers)
			
			logging.info('starting monitor thread')
			monitor = CronMonitor(active_servers)
			monitor.start()
			
			logging.info('starting publisher thread')
			publisher = CronPublisher(con_q,server_g)
			publisher.start()
			
			workers = []
			for i in range(len(SERVERS)):
				logging.info('starting worker thread %d' % i)
				worker = CronWorker(i, con_q, server_g)
				worker.start()
				workers.append(worker)

			logging.info('starting cache monitor')
			cachemon = CacheMonitor(server_g)
			cachemon.start()
			
			theadlog = logging.getLogger('thread')
			while True:
				theadlog.info(enumerate())
				time.sleep(10)
		except Exception, e:
			logging.error(e)
		# this is for clearing the cache on all 3 servers
		#logging.info('starting cache monitor')
		#cachemon = CacheMonitor(server_g)
		#cachemon.start()

class CronThread(Thread):
	def __init__(self):
		super(CronThread, self).__init__()
		self.running = True
		self.logger = logging.getLogger('root')
	def stop(self):
		self.running = False
		self.logger.info('stopping %s' % self)

def server_generator(active_servers):
	count = 0
	logger = logging.getLogger('generator')
	while True:
		logging.info('getting server attempt %d' % count)
		for server in SERVERS:
			count += 1
			if active_servers[server]:
				count = 0
				yield server
		if count > 10:
			logger.info('servers not responding, sleeping for a while.')
			time.sleep(5)
			count = 0

# Monitor the servers to see if they are available
class CronMonitor(CronThread):
	"""This thread monitors the servers defined in the global SERVERS list and checks their status using the keepalive.jsp page."""
	def __init__(self, active_servers):
		super(CronMonitor, self).__init__()
		# initially mark all servers off
		self.active_servers = active_servers
		self.url = SERVER+'keepalive.jsp'
		self.sleeptime = 5
		self.logger = logging.getLogger('monitor')
	def run(self):
		while self.running:
			for server in SERVERS:
				result = ""
				try:
					result = urllib2.urlopen(self.url % server).read().strip()
					if result == 'SYSTEM OK':
						self.active_servers[server] = True
					elif "Load" in result:
						self.logger.info('%s is under load' % server)
						self.active_servers[server] = False
					else:
						self.logger.info('%s is not available' % server)
						self.active_servers[server] = False
				except Exception, e:
					self.logger.error(e)
			self.logger.debug("Active servers %s" % self.active_servers)
			time.sleep(self.sleeptime)

class CronPublisher(CronThread):
	"""This thread is responsible for downloading the list of contractors and populating the queue object"""
	def __init__(self, con_q,server_g):
		super(CronPublisher, self).__init__()
		self.con_q = con_q
		self.server_g = server_g
		self.url = SERVER + "PyCron.action"  
		self.sleeptime = 10
		self.logger = logging.getLogger('publisher')
	def run(self):
		while self.running:
			if self.con_q.qsize() < 5:
				try:
					result = urllib2.urlopen(self.url % self.server_g.next()).read().strip()
					if result:
						running_lock.acquire()
						self.logger.info('contractors running: %s' % con_running)
						try:
							for contractor in result.split(","):
								if contractor not in con_running:
									self.con_q.put(contractor)
						finally:
							running_lock.release() # release lock, no matter what
							
						self.logger.debug("Contractors waiting in the queue: %s" % self.con_q.qsize())
				except Exception, e:
					self.logger.error(e)
			else:
				self.logger.info('too many contractors on the dance floor, sleeping for now.')	
			time.sleep(self.sleeptime)

class CronWorker(CronThread):
	"""This thread is responsible for calling the ContractorCron.action against the proper server/conID"""
	def __init__ (self, i, con_q, server_g):
		super(CronWorker, self).__init__()
		self.thread_id = i
		self.con_q = con_q
		self.server_g = server_g
		self.url = SERVER+"ContractorCron.action?conID=%s&steps=All&button=Run"
		self.sleeptime = 1
		self.logger = logging.getLogger('worker')
	def run(self):
		while self.running:
			id = self.con_q.get()
			running_lock.acquire()
			try:
				con_running.add(id)
			finally:
				running_lock.release() # release lock, no matter what
			
			try:
				
				self.logger.debug('thread #%d starting crontractor %s' % (self.thread_id,id))
				cronurl = self.url % (self.server_g.next(), id)
				self.logger.debug('using url: %s' % cronurl)
				result = urllib2.urlopen(cronurl).read()
				if "Complete" in result:
					self.logger.info('Contractor %s finished successfully.' % id)
				else:
					self.logger.warning('Error with contractor %s' % id)
			except Exception, e:
				self.logger.error(e)
			else:
				time.sleep(self.sleeptime)
			
			running_lock.acquire()
			try:
				con_running.discard(id)
			finally:
				running_lock.release() # release lock, no matter what

class CacheMonitor(CronThread):
	def __init__(self, server_g):
		super(CacheMonitor, self).__init__()
		self.server_g = server_g
		self.url = SERVER + 'clearcache.jsp'
		self.sleeptime = 60
		self.logger = logging.getLogger('cache')
	def run(self):
		# Monitor the cache to see if it needs to be cleared
		self.logger.debug("Starting CacheMonitor")
		while self.running:
			result = ""
			try:
				result = urllib2.urlopen(self.url % self.server_g.next()).read().strip()
				self.logger.debug('"%s" returned from cache' % result)
				if result == 'CLEAR':
					self.logger.debug("clearing cache");
					for server in SERVERS:
						try:
							clearurl = (SERVER+"ClearCache.action") % server
							result = urllib2.urlopen(clearurl).read()
							if "Cleared" in result:
								self.logger.info('cleared %s' % server)
							else:
								self.logger.warning('cache on %s may not have been cleared' % server)
						except Exception, e:
							self.logger.error(e)
			except Exception, e:
				self.logger.error(e)
			time.sleep(self.sleeptime)

def main():
	daemon = qcron("/tmp/con_cron.pid")
	if len(sys.argv) == 2 :
		if 'start' == sys.argv[1]:
			daemon.start()
		elif 'stop' == sys.argv[1]:
			daemon.stop()
		elif 'restart' == sys.argv[1]:
			daemon.restart()
		else:
			print "Unknown command"
			sys.exit(2)
	else:
		print "usage: %s start|stop|restart" % sys.argv[0]
		sys.exit(2)

if __name__ == "__main__":
	main()
