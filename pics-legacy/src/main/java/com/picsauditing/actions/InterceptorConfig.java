package com.picsauditing.actions;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.interceptors.PicsInterceptor;

@SuppressWarnings("serial")
public class InterceptorConfig extends PicsActionSupport {

	private String uri = null;
	private final Logger logger = LoggerFactory.getLogger(InterceptorConfig.class);
	@Override
	public String execute() throws Exception {
		if (!ServletActionContext.getRequest().getRemoteAddr()
				.equals("127.0.0.1")) {
			addActionError("Nice try buddy");
			return BLANK;
		}

		output = "";
		if (button == null || "list".equalsIgnoreCase(button)) {

			logger.info("Interceptor status: {}", System.getProperty("interceptor.enabled"));

			logger.info("Pages which require SSL");
			for (String s : PicsInterceptor.securePages) {
				logger.info(s);
			}

		} else if ("addSecure".equals(button)) {
			if (uri != null && uri.length() != 0) {
				PicsInterceptor.securePages.add(uri);
			}
		} else if ("deleteSecure".equals(button)) {
			if (uri != null && uri.length() != 0) {
				PicsInterceptor.securePages.remove(uri);
			}
		} else if ("clearSecure".equals(button)) {
			PicsInterceptor.securePages.clear();
		} else if (button.equals("enable")) {
			System.setProperty("interceptor.enabled", "enabled");
		} else if (button.equals("disable")) {
			System.setProperty("interceptor.enabled", "disabled");
		}

		return SUCCESS;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
}
