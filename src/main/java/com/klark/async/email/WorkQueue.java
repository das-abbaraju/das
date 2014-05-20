// =======================================================
// Copyright Mylife.com Inc., 2010. All rights reserved.
//
// =======================================================

package com.klark.async.email;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.klark.util.ExceptionUtil;

/**
 * 
 */
public class WorkQueue {
    protected static final Logger LOGGER = LoggerFactory.getLogger(WorkQueue.class.getName());

    /* instance variables */

    private final int nThreads;
    private String name;
    private final PoolWorker[] threads;
    protected final LinkedList<Runnable> queue;
    protected long insertCount;
    protected Boolean finishIfEmpty = Boolean.FALSE;

    /* subtypes */

    private class LandMine implements Runnable {
        public void run() {
            LOGGER.warn("This should never be running");
            throw new AbstractMethodError();
        }
    }

    private class PoolWorker extends Thread {
        private boolean done;
        private long work_cnt;

        public PoolWorker(String name) {
            setName(name);
        }

        public boolean isDone() {
            return done;
        }

        @Override
        public void run() {
            Runnable r;

            LOGGER.info("PoolWorker" + getName() + ".run: STARTED");

            boolean waitForMore;
            synchronized (finishIfEmpty) {
                synchronized (queue) {
                    waitForMore = !finishIfEmpty.booleanValue() || !queue.isEmpty();
                }
            }
            while (waitForMore) {
                boolean waitToStart = true;
                synchronized (finishIfEmpty) {
                    synchronized (queue) {
                        waitToStart = queue.isEmpty() && !finishIfEmpty.booleanValue();
                    }
                }
                while (waitToStart) {
                    try {
                        synchronized (queue) {
                            queue.wait();
                        }
                    } catch (InterruptedException ignored) {
                        LOGGER.info("PoolWorker" + getName() + ".run: INTERRUPTED");
                    }
                    synchronized (finishIfEmpty) {
                        synchronized (queue) {
                            waitToStart = queue.isEmpty() && !finishIfEmpty.booleanValue();
                        }
                    }
                }
                LOGGER.info("PoolWorker" + getName() + ".run: RUNNING");
                synchronized (queue) {
                    if (!queue.isEmpty()) {
                        r = queue.removeFirst();
                        insertCount--;
                    } else {
                        r = null;
                    }
                }

                // If we don't catch RuntimeException,
                // the pool could leak threads
                try {
                    if (r != null && r instanceof LandMine) {
                        LOGGER.info("PoolWorker" + getName() + ".run: Bombed out");
                        break;
                    } else if (r != null) {
                        r.run();
                        work_cnt++;
                    }
                } catch (RuntimeException e) {
                    LOGGER.warn("PoolWorker" + getName() + ".run: Runtime Exception: " + e + "\ntrace: " + ExceptionUtil.getStackTraceString(e));
                } catch (Error e) {
                    LOGGER.error("PoolWorker" + getName() + ".run: Error: " + e + "\ntrace: " + ExceptionUtil.getStackTraceString(e));
                    done = true;
                    break;
                } catch (Throwable t) {
                    LOGGER.error("PoolWorker" + getName() + ".run: Throwable: " + t + "\ntrace: " + ExceptionUtil.getStackTraceString(t));
                    done = true;
                    break;
                }

                synchronized (finishIfEmpty) {
                    synchronized (queue) {
                        waitForMore = !finishIfEmpty.booleanValue() || !queue.isEmpty();
                    }
                }
            }
            LOGGER.info("PoolWorker" + getName() + ".run: DONE [ " + work_cnt + " ]");
            done = true;
        }
    }

    /* public methods */

    public WorkQueue(int nThreads, String name) {
        if (name != null && name.length() > 0)
            this.name = "(" + name + ")";
        this.nThreads = nThreads;
        queue = new LinkedList();
        threads = new PoolWorker[nThreads];

        for (int i = 0; i < nThreads; i++) {
            threads[i] = new PoolWorker("(" + name + "#" + i + ")");
            threads[i].setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
                public void uncaughtException(Thread thread, Throwable throwable) {
                    LOGGER.error("PoolWorkerUncaughtExceptionHandler caught Throwable: " + throwable.getMessage());
                    StringWriter sw = new StringWriter();
                    throwable.printStackTrace(new PrintWriter(sw));
                    LOGGER.error("PoolWorkerUncaughtExceptionHandler stackTrace:" + sw.toString());
                }
            });
            threads[i].start();
        }
        LOGGER.info("WorkQueue" + name + " spawning " + nThreads + " threads");
    }

    public void scheduleForExecution(Runnable r) {
        synchronized (queue) {
            this.insertCount++;
            queue.addLast(r);
            queue.notify();
        }
    }

    public long getInsertCount() {
        long result = 0;
        synchronized (queue) {
            result = insertCount;
        }
        return result;
    }

    public int size() {
        return queue.size();
    }

    public boolean isDone() {
        boolean done = true;
        String logthis = "WorkQueue" + name + ".isDone:";
        for (int i = 0; i < this.nThreads; i++) {
            boolean thisdone = this.threads[i].isDone();
            logthis += " [" + i + (thisdone ? "-" : "+") + "]";
            done = done && thisdone;
            // do not shortcut the logic so we get a good log message
        }
        LOGGER.info(logthis);
        return done;
    }

    public void setFinishIfEmpty() {
        synchronized (finishIfEmpty) {
            finishIfEmpty = Boolean.TRUE;
        }
    }

    public void waitOnSize(int queueLimit, long aSleepTime) {
        int rqs = size();
        long total = 0;
        while (rqs > queueLimit) {
            try {
                if (total == 0) {
                    LOGGER.info("WorkQueue" + name + ".waitOnSize: size=" + rqs + "(" + queueLimit + ")" + "; sleep=" + aSleepTime + "[ms] at a time");
                }
                Thread.sleep(aSleepTime);
                rqs = size();
                total++;
                if (rqs <= queueLimit) {
                    LOGGER.info("WorkQueue" + name + ".waitOnSize: slept total of " + aSleepTime * total + "[ms]");
                }
            } catch (InterruptedException e) {
                LOGGER.info("WorkQueue" + name + ".waitOnSize: InterruptedException: " + e);
            }
        }
    }

    public void waitForDone(int maxTries, long aSleepTime) {
        if (!finishIfEmpty.booleanValue()) {
            LOGGER.info("WorkQueue" + name + ".waitForDone: Not set to finish if empty");
            return;
        }

        placeLandMines();

        int tries = 0;
        boolean done = isDone();
        while (!done && (maxTries < 0 || tries < maxTries)) {
            try {
                if (tries == 0) {
                    LOGGER.info("WorkQueue" + name + ".waitForDone: sleep=" + aSleepTime + "[ms] at a time");
                }
                Thread.sleep(aSleepTime);
                tries++;
                done = isDone();
                if (done || maxTries > 0 && tries >= maxTries) {
                    LOGGER.info("WorkQueue" + name + ".waitForDone: slept total of " + aSleepTime * tries + "[ms]");
                }
            } catch (InterruptedException e) {
                LOGGER.info("WorkQueue" + name + ".waitForDone: InterruptedException: " + e);
            }
        }
    }

    private void placeLandMines() {
        for (int i = 0; i < nThreads; i++) {
            scheduleForExecution(new LandMine());
            LOGGER.info("WorkQueue" + name + ".placeLandMines: just placed mine#" + i + " out of " + nThreads + " mines");
        }
    }

}