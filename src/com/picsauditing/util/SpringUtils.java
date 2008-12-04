package com.picsauditing.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

/**
 * 
 * @author Okan Kahraman
 * 
 */
public class SpringUtils implements ApplicationContextAware {

	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SpringUtils.applicationContext = applicationContext;
	}

	@SuppressWarnings("unchecked")
	public static Object getBean(String beanRefName, Class beanClass) {
		Object object = null;
		Assert.notNull(applicationContext, "ApplicationContext must not be null!");
		try {
			BeanFactory factory = (BeanFactory) applicationContext;
			object = factory.getBean(beanRefName, beanClass);
		} catch (NoSuchBeanDefinitionException ex) {
			// ignore
		}
		return object;
	}

	public static Object getBean(String beanRefName) {
		Object object = null;
		Assert.notNull(applicationContext, "ApplicationContext must not be null!");
		try {
			BeanFactory factory = (BeanFactory) applicationContext;
			object = factory.getBean(beanRefName);
		} catch (NoSuchBeanDefinitionException ex) {
			// ignore
		}
		return object;
	}
}
