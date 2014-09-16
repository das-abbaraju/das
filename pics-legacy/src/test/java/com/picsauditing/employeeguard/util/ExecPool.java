package com.picsauditing.employeeguard.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ExecPool {
  private ExecutorService executor;

  private static ExecPool ourInstance = new ExecPool();

  public static ExecPool getInstance() {
    return ourInstance;
  }

  private ExecPool() {
    executor = Executors.newCachedThreadPool();
  }

  public ExecutorService getExecutor() {
    return executor;
  }

	public void shutdown(){
		executor.shutdown();
	}
}
