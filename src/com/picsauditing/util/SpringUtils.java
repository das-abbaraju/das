package com.picsauditing.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	
	public static final String PermissionService = "PermissionService";
	public static final String ReportService = "ReportService";

	public static final String BILLING_STATUS_PROVIDER_BEAN_NAME = "BillingStatusProvider";
	public static final String BRAIN_TREE_SERVICE_BEAN_NAME = "BrainTreeService";
	public static final String COUNTRY_DAO_BEAN_NAME = "CountryDAO";
	public static final String FEE_CALCULATOR_BEAN_NAME = "FeeCalculator";
	public static final String INPUT_VALIDATOR_BEAN_NAME = "InputValidator";
	public static final String INVOICE_FEE_DAO_BEAN_NAME = "InvoiceFeeDAO";
	public static final String VAT_VALIDATOR_BEAN_NAME = "VATValidator";

	private static ApplicationContext applicationContext;

	private static final Logger logger = LoggerFactory.getLogger(SpringUtils.class);

	public static void publishEvent(ApplicationEvent event) {
		Assert.notNull(applicationContext, "ApplicationContext must not be null!");
		applicationContext.publishEvent(event);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SpringUtils.applicationContext = applicationContext;
	}

	public static <T> T getBean(String beanRefName, Class<T> beanClass) {
		T bean = null;
		Assert.notNull(applicationContext, "ApplicationContext must not be null!");
		try {
			BeanFactory factory = applicationContext;
			bean = factory.getBean(beanRefName, beanClass);
		} catch (NoSuchBeanDefinitionException ex) {
			logger.error("No bean found with name {}", beanRefName, ex);
		}

		return bean;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBean(String beanRefName) {
		T bean = null;
		Assert.notNull(applicationContext, "ApplicationContext must not be null!");
		try {
			BeanFactory factory = applicationContext;
			bean = (T) factory.getBean(beanRefName);
		} catch (NoSuchBeanDefinitionException ex) {
			logger.error("No bean found with name {}", beanRefName, ex);
		}

		return bean;
	}

}
