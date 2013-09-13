package com.picsauditing.strutsutil;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import com.opensymphony.module.sitemesh.Config;
import com.opensymphony.module.sitemesh.Decorator;
import com.opensymphony.module.sitemesh.DecoratorMapper;
import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.mapper.AbstractDecoratorMapper;

public class AjaxDecoratorMapper extends AbstractDecoratorMapper {
	public void init(Config config, Properties properties, DecoratorMapper parent) throws InstantiationException {
		super.init(config, properties, parent);
	}

	public Decorator getDecorator(HttpServletRequest request, Page page) {

		if (AjaxUtils.isAjax(request)) {
			return getNamedDecorator(request, null);
		}
		return super.getDecorator(request, page);
	}
}