package com.picsauditing.listeners;

import java.util.Date;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;

public class SessionListener implements HttpSessionListener {

	private int sessionCount;
	private Logger log = null;
	
    public SessionListener( ) {
        this.sessionCount = 0;
        log = Logger.getLogger(SessionListener.class);
    }
	
	public void sessionCreated(HttpSessionEvent arg0) {
		 HttpSession session = arg0.getSession( );
	     //session.setMaxInactiveInterval(30);

	      //increment the session count
	      sessionCount++;
	     
	      String id = session.getId( );

	      Date now = new Date( );

	      String message = new StringBuffer("New Session created on ").
	            append(now.toString( )).append("\nID: ").
	              append(id).append("\n").append("There are now ").
	                append(sessionCount).append(
	                  " live sessions in the application."). toString( );
	        
	      log.debug(message);
	}

	public void sessionDestroyed(HttpSessionEvent arg0) {
		 HttpSession session = arg0.getSession( );

	     String id = session.getId( );

	     --sessionCount;

	     String message = new StringBuffer("Session destroyed" + 
	          "\nValue of destroyed session ID is ").
	              append(id).append("\n").append(
	                "There are now ").append(sessionCount).append(
	                  " live sessions in the application.").toString( );

	        log.debug(message);

	}

}
