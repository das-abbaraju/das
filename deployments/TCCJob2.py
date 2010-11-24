import urllib2
from time import time
import sys
from threading import Thread
from threading import Timer

class CCron(Thread):
    starturl = "http://web2.picsorganizer.com/ContractorCron.action?conID="
    endurl = "&steps=All&__multiselect_steps=&button=Run"
    def __init__ (self, ids):
        Thread.__init__(self)
        self.ids = ids
        self.finids = []
        self.badids = []
        self.time = 0
    def run(self):
        start = time()
        for id in self.ids:
            try:    
                fullurl = CCron.starturl+str(id)+CCron.endurl
                res = urllib2.urlopen(fullurl).read()
                fin = res[str.find(res, "Completed"):str.find(res, "<br />")]
                if fin:
                    self.finids.append(id)
            except:
                self.badids.append(id)
        self.time = str(time()-start)
        
def runCC(tcount):
    ids = getIds()
    idslist = []
    finlist = []

    for i in range(tcount):
        c = []
        idslist.append(c)

    for i in range(len(ids)):
        idslist[i%tcount].append(ids[i])

    for clist in idslist:
        if clist:
            print "Starting %s" % (clist)
            current = CCron(clist)
            finlist.append(current)
            current.start()

    for crons in finlist:
        crons.join()
        print "Finished!"
        print "Bad ids: %s\nGood ids: %s\nTook: %s" % (crons.badids,crons.finids,crons.time)

    t = Timer(60.0, runCC(3))
    t.start()

def getIds():
    daourl = "http://web2.picsorganizer.com/PyCron.action"
    res = urllib2.urlopen(daourl).read()
    conIds = res[str.find(res,"Start")+len("Start"):str.find(res,"End")]
    if conIds:
        return str.split(conIds,",")
    else:
        return []
        
                
