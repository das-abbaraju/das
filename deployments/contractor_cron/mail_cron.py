#!/usr/bin/env python
import urllib2, time, sys, logging, logging.config
from datetime import datetime
from threading import Thread, enumerate, Lock
from daemon import Daemon
from Queue import Queue
from locked_iterator import LockedIterator

logging.config.fileConfig("logging-mail.conf")

SERVER = "http://%s/"
SERVERS = ['organizer1']
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
			
			theadlog = logging.getLogger('thread')
			while True:
				theadlog.info(enumerate())
				time.sleep(10)
		except Exception, e:
			logging.error(e)

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
		self.url = SERVER+'keepalive.jsp?load_factor=2.0'
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
					self.active_servers[server] = False
			self.logger.info("Active servers %s" % self.active_servers)
			time.sleep(self.sleeptime)

class CronPublisher(CronThread):
	"""This thread is responsible for downloading the list of Subscriptions and populating the queue object"""
	def __init__(self, con_q,server_g):
		super(CronPublisher, self).__init__()
		self.con_q = con_q
		self.server_g = server_g
		self.url = SERVER + "MailCron!listAjax.action"  
		self.sleeptime = 10
		self.logger = logging.getLogger('publisher')
	def run(self):
		while self.running:
			running_lock.acquire()
			self.logger.info('Subscriptions running: %s' % con_running)
			running_lock.release()
			if self.con_q.empty():
				try:
					result = urllib2.urlopen(self.url % self.server_g.next()).read().strip()
					if result:
						running_lock.acquire()
						try:
							for contractor in result.split(","):
								if contractor not in con_running:
									self.con_q.put(contractor)
						finally:
							running_lock.release() # release lock, no matter what
							
						self.logger.debug("Subscriptions waiting in the queue: %s" % self.con_q.qsize())
				except Exception, e:
					self.logger.error(e)
			else:
				self.logger.info('too many Subscriptions on the dance floor, sleeping for now.')	
			time.sleep(self.sleeptime)

class CronWorker(CronThread):
	"""This thread is responsible for calling the MailCronAjax.action against the proper server/conID"""
	def __init__ (self, i, con_q, server_g):
		super(CronWorker, self).__init__()
		self.thread_id = i
		self.con_q = con_q
		self.server_g = server_g
		self.url = SERVER+"MailCron.action?subscriptionID=%s"
		self.sleeptime = 6
		self.logger = logging.getLogger('worker')
	def run(self):
		while self.running:
			if not self.con_q.empty():
				id = self.con_q.get()
				running_lock.acquire()
				try:
					con_running.add(id)
				finally:
					running_lock.release() # release lock, no matter what
				start = time.time()
				starttime = datetime.now()
				success = False
				try:
					self.logger.debug('thread #%d starting subscription %s' % (self.thread_id,id))
					cronurl = self.url % (self.server_g.next(), id)
					self.logger.debug('using url: %s' % cronurl)
					result = urllib2.urlopen(cronurl).read()
					self.logger.debug('Result from MailCron.action for subscription %s = %s' % (id,result))
					success = True
					if success:
						self.logger.info('Subscription %s finished successfully.' % id)
					else:
						self.logger.warning('Error with Subscription %s' % id)
				except Exception, e:
					self.logger.error(e)
				else:
					time.sleep(self.sleeptime)
				totaltime = time.time() - start
				
				running_lock.acquire()
				try:
					con_running.discard(id)
				finally:
					running_lock.release() # release lock, no matter what

def main():
	daemon = qcron("/tmp/mail_cron.pid")
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
