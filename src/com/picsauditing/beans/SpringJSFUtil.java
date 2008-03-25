package com.picsauditing.beans;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class SpringJSFUtil 
{
	public synchronized static ApplicationContext getSpringContext()
	{
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ServletContext servletContext = (ServletContext) facesContext.getExternalContext().getContext();
		return WebApplicationContextUtils.getWebApplicationContext(servletContext);
	}
}
