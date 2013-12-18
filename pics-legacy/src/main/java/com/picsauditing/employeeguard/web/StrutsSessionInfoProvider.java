package com.picsauditing.employeeguard.web;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.access.Permissions;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Map;

public class StrutsSessionInfoProvider implements SessionInfoProvider {

    private static final Permissions EMPTY_PERMISSIONS = new Permissions();

    public static final String REST_ID_PARAM_KEY = "id"; // FIXME: Get this from the container

    @Override
    public int getUserId() {
        return getPermissions().getUserId();
    }

    @Override
    public int getAccountId() {
        return getPermissions().getAccountId();
    }

    @Override
    public int getId() {
        Map<String, Object> params = getRequestParams();
        if (MapUtils.isEmpty(params) || !params.containsKey(REST_ID_PARAM_KEY)) {
            return 0;
        }

        return NumberUtils.toInt((String) params.get(REST_ID_PARAM_KEY));
    }

    private Permissions getPermissions() {
        if (ActionContext.getContext() == null || ActionContext.getContext().getSession() == null) {
            return EMPTY_PERMISSIONS;
        }

        return (Permissions) ActionContext.getContext().getSession()
                .get(Permissions.SESSION_PERMISSIONS_COOKIE_KEY);
    }

    private Map<String, Object> getRequestParams() {
        return ActionContext.getContext().getParameters();
    }
}
