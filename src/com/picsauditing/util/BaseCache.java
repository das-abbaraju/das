package com.picsauditing.util;

import javax.servlet.ServletContext;

import org.apache.struts2.ServletActionContext;

public class BaseCache 
{
	protected ServletContext getContext()
	{
		return ServletActionContext.getServletContext();
	}
}
