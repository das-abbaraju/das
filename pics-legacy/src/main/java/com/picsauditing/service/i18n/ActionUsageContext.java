package com.picsauditing.service.i18n;

import com.picsauditing.util.Strings;
import org.apache.struts2.ServletActionContext;

public class ActionUsageContext extends PicsUsageContext {
    public static final String DEFAULT_PAGENAME = "UNKNOWN";
    public static final String PAGE_NAME_PARAMETER_KEY = "pageName";
    public static final String PAGE_ORDER_PARAMETER_KEY = "pageOrder";

    @Override
    public String pageName() {
        try {
            String pageName = pageNameFromContextParameterOverride();
            if (Strings.isEmpty(pageName)) {
                pageName = pageNameFromParameterOverride();
            }
            if (Strings.isEmpty(pageName)) {
                pageName = pageNameFromActionContext();
            }
            if (Strings.isEmpty(pageName)) {
                pageName = DEFAULT_PAGENAME;
            }
            return pageName;
        } catch (Exception e) {
            return DEFAULT_PAGENAME;
        }
    }

    private String pageNameFromActionContext() {
        return ServletActionContext.getContext().getName();
    }

    private String pageNameFromParameterOverride() {
        try {
            return ServletActionContext.getRequest().getParameter(PAGE_NAME_PARAMETER_KEY);
        } catch (Exception e) {
            return null;
        }
    }

    private String pageNameFromContextParameterOverride() {
        try {
            return ServletActionContext.getContext().getParameters().get(PAGE_NAME_PARAMETER_KEY).toString();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String pageOrder() {
        try {
            return ServletActionContext.getRequest().getParameter(PAGE_ORDER_PARAMETER_KEY);
        } catch (Exception e) {
            return null;
        }
    }

}
