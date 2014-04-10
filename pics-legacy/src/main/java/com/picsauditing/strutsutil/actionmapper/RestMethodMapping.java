package com.picsauditing.strutsutil.actionmapper;

import com.picsauditing.strutsutil.HttpUtil;
import com.picsauditing.util.Strings;

import javax.servlet.http.HttpServletRequest;

final class RestMethodMapping {

    /**
     * This will determine the method name from the request
     *
     * @param request
     * @param idParameter
     * @param parsedUrlWrapper
     * @return
     */
    public String getMethodName(HttpServletRequest request,
								String idParameter,
								ParsedUrlWrapper parsedUrlWrapper,
								RestMethodMapperConfig methodMapping) {

        if (Strings.isEmpty(idParameter)) {

            // Index e.g. foo/
            if (HttpUtil.isGet(request)) {
                return methodMapping.getIndexMethodName();

                // Creating a new entry on POST e.g. foo/
            } else if (HttpUtil.isPost(request)) {
                return methodMapping.getPostMethodName();
            }

        } else {
            // Viewing the form to create a new item e.g. foo/create
            if (HttpUtil.isGet(request) && methodMapping.getNewMethodName().equals(idParameter)) {
                return methodMapping.getNewMethodName();

                // Viewing an item e.g. foo/1
            } else if (HttpUtil.isGet(request) && parsedUrlWrapper.isNextValue(methodMapping.getEditMethodName())) {
                return methodMapping.getEditMethodName();

                // Removing an item e.g. foo/1
            } else if (HttpUtil.isDelete(request)) {
                return methodMapping.getDeleteMethodName();

                // Updating an item e.g. foo/1
            } else if (HttpUtil.isPut(request)) {
                return methodMapping.getPutMethodName();

                // Insert a new item e.g. foo/create
            } else if (HttpUtil.isPost(request) && methodMapping.getNewMethodName().equals(idParameter)) {
                return methodMapping.getPostMethodName();

                // Update an item e.g. foo/1
            } else if (HttpUtil.isPost(request) && parsedUrlWrapper.isNextValue(methodMapping.getEditMethodName())) {
                return methodMapping.getPutMethodName();

                // Execute a dynamic method item e.g. foo/1/{methodName}
            } else if ((HttpUtil.isGet(request) || HttpUtil.isPost(request)) && parsedUrlWrapper.iterator().hasNext()) {
                return parsedUrlWrapper.iterator().next();

                // Viewing an item e.g. foo/1
            } else if (HttpUtil.isGet(request)) {
                return methodMapping.getGetMethodName();
            }
        }

        return null;
    }

}
