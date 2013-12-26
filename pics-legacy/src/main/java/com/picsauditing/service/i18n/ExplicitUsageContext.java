package com.picsauditing.service.i18n;

public class ExplicitUsageContext extends PicsUsageContext {
    private String pageName = DEFAULT_PAGENAME;
    private String pageOrder;

    public ExplicitUsageContext(String pageName, String pageOrder) {
        this.pageName = pageName;
        this.pageOrder = pageOrder;
    }

    @Override
    public String pageName() {
        return pageName;
    }

    @Override
    public String pageOrder() {
        return pageOrder;
    }
}
