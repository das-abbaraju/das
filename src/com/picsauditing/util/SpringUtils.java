package com.picsauditing.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.util.Assert;

/**
 * 
 * @author Okan Kahraman
 * 
 */
public class SpringUtils implements ApplicationContextAware {

	private static ApplicationContext applicationContext;

	public static void publishEvent(ApplicationEvent event) {
		Assert.notNull(applicationContext, "ApplicationContext must not be null!");
		applicationContext.publishEvent(event);
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SpringUtils.applicationContext = applicationContext;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBean(String beanRefName, Class<T> beanClass) {
		T bean = null;
		Assert.notNull(applicationContext, "ApplicationContext must not be null!");
		try {
			BeanFactory factory = (BeanFactory) applicationContext;
			bean = (T) factory.getBean(beanRefName, beanClass);
		} catch (NoSuchBeanDefinitionException ex) {
			// ignore
		}
		
		return bean;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBean(String beanRefName) {
		T bean = null;
		Assert.notNull(applicationContext, "ApplicationContext must not be null!");
		try {
			BeanFactory factory = (BeanFactory) applicationContext;
			bean = (T) factory.getBean(beanRefName);
		} catch (NoSuchBeanDefinitionException ex) {
			// ignore
		}
		
		return bean;
	}
	
}
