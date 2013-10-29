#!/usr/bin/env python
import os, urllib2, time, sys, logging, logging.config, MySQLdb
from datetime import datetime
from threading import Thread, enumerate, Lock
from daemon import Daemon
from Queue import Queue
from locked_iterator import LockedIterator

logging.config.fileConfig("logging.conf")

SERVER = "http://%s/"
SERVERS = ['configjobs.picsorganizer.com']
APPLICATION_SERVER_LOAD_THRESHOLD = '4.0'
CRON_SERVER_LOAD_THRESHOLD = 4.0
MAX_WORKERS = 15
MYSQL_SERVER = 'cobalt.picsauditing.com'
MYSQL_USER = 'pics'
MYSQL_PASSWORD = '8arTyRev'
MYSQL_SCHEMA = 'pics_config'
con_running = set()
running_lock = Lock()
con_q = Queue()
stats_q = Queue()
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
			
			logging.info('starting worker manager thread')
			workerManager = CronWorkerManager(con_q, server_g)
			workerManager.start()
			
			logging.info('starting cache monitor')
			cachemon = CacheMonitor(server_g)
			cachemon.start()
			
			stats = CronStats()
			stats.start()
			
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
		self.url = SERVER+'keepalive.jsp?load_factor='+APPLICATION_SERVER_LOAD_THRESHOLD
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
	"""This thread is responsible for downloading the list of contractors and populating the queue object"""
	def __init__(self, con_q, server_g):
		super(CronPublisher, self).__init__()
		self.con_q = con_q
		self.server_g = server_g
		self.url = SERVER + "ContractorCron!listAjax.action"  
		self.sleeptime = 10
		self.logger = logging.getLogger('publisher')
	def run(self):
		while self.running:
			running_lock.acquire()
			self.logger.info('contractors running: %s' % con_running)
			running_lock.release()
			if self.con_q.qsize() < 5:
				try:
					result = urllib2.urlopen(self.url % self.server_g.next()).read().strip()
					if result:
						running_lock.acquire()
						self.logger.info('contractors running: %s' % con_running)
						try:
							for contractor in result.split(","):
								if contractor not in con_running:
									self.logger.info('putting contractor %s in the queue' % contractor)
									self.con_q.put(contractor)
						finally:
							running_lock.release() # release lock, no matter what
							
						self.logger.debug("Contractors waiting in the queue: %s" % self.con_q.qsize())
				except Exception, e:
					self.logger.error(e)
			else:
				self.logger.info('too many contractors on the dance floor, sleeping for now.')	
			time.sleep(self.sleeptime)

class CronWorkerManager(CronThread):
	"""This thread is responsible for managing the number of CronWorkers according to local system load"""
	def __init__ (self, con_q, server_g):
		super(CronWorkerManager, self).__init__()
		self.con_q = con_q
		self.server_g = server_g
		self.none_in_queue_sleeptime = 6
		self.pause_between_workers_sleeptime = 3
		self.exceeded_load_sleeptime = 15
		# this value needs to be tuned accordingly
		self.logger = logging.getLogger('worker_manager')
	def run(self):
		while self.running:		
			workers = []
			load = os.getloadavg()[0]
			while load < CRON_SERVER_LOAD_THRESHOLD:
				self.logger.info('Local load ok: %5.3f  Attempting to get a contractor from the queue' % load) 
				try:
					id = self.con_q.get(False)
				except Exception, e:	
					self.logger.info('no contractors in queue... waiting to try again')
					time.sleep(self.none_in_queue_sleeptime)
				else:
					self.logger.info('starting worker thread for contractor %s' % id)
					worker = CronWorker(id, self.server_g)
					worker.start()
					workers.append(worker)		
					load = os.getloadavg()[0]
					# Comment out the following line to increase processing speed
					time.sleep(self.pause_between_workers_sleeptime)	
			self.logger.info('exceeded local system load: %5.3f Waiting for load to come down...' % load)
			time.sleep(self.exceeded_load_sleeptime)

class CronWorker(CronThread):
	"""This thread is responsible for calling the ContractorCronAjax.action against the proper server/conID"""
	def __init__ (self, id, server_g):
		super(CronWorker, self).__init__()
		self.id = id
		self.server_g = server_g
		self.url = SERVER+"ContractorCronAjax.action?conID=%s&steps=All&button=Run"
		self.sleeptime = 6
		self.logger = logging.getLogger('worker')
	def run(self):		
		running_lock.acquire()
		try:
			con_running.add(self.id)
		finally:
			running_lock.release() # release lock, no matter what
		start = time.time()
		starttime = datetime.now()
		success = False
		try:
			self.logger.debug('Starting crontractor %s' % self.id)
			cronurl = self.url % (self.server_g.next(), self.id)
			self.logger.debug('using url: %s' % cronurl)
			result = urllib2.urlopen(cronurl).read()
			self.logger.info('Contractor %s finished successfully.' % self.id)
			success = True
		except Exception, e:
			self.logger.error(e)
		finally:
			running_lock.acquire()
			try:
				self.logger.info('removing %s from con_running' % self.id)
				con_running.discard(self.id)
			finally:
				running_lock.release() # release lock, no matter what

		totaltime = time.time() - start
		stats_q.put((self.id, starttime, totaltime, success, cronurl))

class CacheMonitor(CronThread):
	def __init__(self, server_g):
		super(CacheMonitor, self).__init__()
		self.server_g = server_g
		self.url = SERVER + 'ClearCache!monitor.action'
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

class CronStats(CronThread):
	def __init__(self):
		super(CronStats, self).__init__()
		self.sleeptime = 10
		self.logger = logging.getLogger("stats")
	def run(self):
		self.logger.debug("starting CronStats thread")
		while self.running:
			if stats_q.qsize() > 5:
				try:
					self.logger.info("getting database connection")
					records = []
					self.logger.info(stats_q)
					for i in range(stats_q.qsize()):
						records.append(stats_q.get())
					self.logger.info("inserting records: %s", records)
					conn = MySQLdb.connect (host = MYSQL_SERVER, user = MYSQL_USER, passwd = MYSQL_PASSWORD, db = MYSQL_SCHEMA)
					cursor = conn.cursor()
					cursor.executemany("""
						INSERT INTO contractor_cron_log (conID, startDate, runTime, success, server)
						VALUES (%s, %s, %s, %s, %s)
					""", records)
				except Exception, e:
					self.logger.error(e)
				else:
					if cursor:
						cursor.close()
					if conn:
						conn.close()
			else:
				self.logger.info('not enough contractors to run the stats, sleeping for now')
			time.sleep(self.sleeptime)

def main():
	daemon = qcron("/var/run/con_cron.pid")
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
