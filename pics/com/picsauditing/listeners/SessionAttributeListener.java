package com.picsauditing.listeners;


import javax.servlet.http.*;

import org.apache.log4j.Logger;

public class SessionAttributeListener implements HttpSessionAttributeListener {

   private Logger log = null;
   
    public SessionAttributeListener( ) {
        log = Logger.getLogger(SessionAttributeListener.class);
        log.debug(getClass( ).getName( ));
    }
    
   public void attributeAdded(HttpSessionBindingEvent se) {
       
        HttpSession session = se.getSession( );

        String id = session.getId( );
        String name = se.getName( );
        String value =  se.getValue( ).toString();
        String source = se.getSource( ).getClass( ).getName( );

        String message = new StringBuffer(
         "Attribute bound to session in ").append(source).
           append("\nThe attribute name: ").append(name).
             append("\n").append("The attribute value:").
               append(value).append("\n").
                 append("The session ID: ").
                   append(id).toString( );

        log.debug(message);
     }
   
     public void attributeRemoved(HttpSessionBindingEvent se) {
         
         HttpSession session = se.getSession( );

         String id = session.getId( );

         String name = se.getName( );

         if(name == null)
             name = "Unknown";

         String value = se.getValue( ).toString();

         String source = se.getSource( ).getClass( ).getName( );

         String message = new StringBuffer(
           "Attribute unbound from session in ").append(source).
              append("\nThe attribute name: ").append(name).
                append("\n").append("The attribute value: ").
                  append(value).append("\n").append(
                    "The session ID: ").append(id).toString( );

         log.debug(message);
     }
   
     public void attributeReplaced(HttpSessionBindingEvent se) {
         
          String source = se.getSource( ).getClass( ).getName( );

          String message = new StringBuffer(
            "Attribute replaced in session  ").
              append(source).toString( );

          log.debug(message);
     }
}