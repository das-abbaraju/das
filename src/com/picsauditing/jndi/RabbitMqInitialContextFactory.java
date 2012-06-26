package com.picsauditing.jndi;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.spi.ObjectFactory;

import org.apache.naming.ResourceRef;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;

public class RabbitMqInitialContextFactory implements ObjectFactory {
	private static final String ACCEPTED_CLASS = "org.springframework.amqp.rabbit.connection.CachingConnectionFactory";

	@Override
	public Object getObjectInstance(Object arg0, Name arg1, Context arg2, Hashtable<?, ?> arg3) throws Exception {
		ResourceRef resources = (ResourceRef)arg0;
		String className = resources.getClassName();
		if (!ACCEPTED_CLASS.equals(className)) {
			throw new NamingException("This ObjectFactory is only for "+ACCEPTED_CLASS);
		}
		RefAddr host = resources.get("host");
		RefAddr username = resources.get("username");
		RefAddr password = resources.get("password");
		CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
		cachingConnectionFactory.setHost(host.getContent().toString());
		cachingConnectionFactory.setUsername(username.getContent().toString());
		cachingConnectionFactory.setPassword(password.getContent().toString());
		return cachingConnectionFactory;
	}

}
