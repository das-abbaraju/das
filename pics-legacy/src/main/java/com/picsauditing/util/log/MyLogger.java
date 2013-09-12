package com.picsauditing.util.log;

import java.util.Stack;

public class MyLogger {
	protected Stack<StopWatch> watches = new Stack<StopWatch>();
	
	public void push( StopWatch watch ) {
		watches.push(watch);
	}
	
	public StopWatch pop() {
		if( watches.size() > 0 ) {
			return watches.pop();
		}
		return null;
	}
	
	public StopWatch top() {
		if( watches.size() > 0 ) {
			return watches.peek();
		}
		return null;
	}
}
