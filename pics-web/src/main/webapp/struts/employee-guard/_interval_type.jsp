<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set var="INTERVAL_TYPE">${param.intervalType}</s:set>
<s:if test="#INTERVAL_TYPE !=null && #INTERVAL_TYPE== 'Day'">
    <s:text name="INTERVAL_TYPE_DAY" />
</s:if>
<s:elseif test="#INTERVAL_TYPE !=null && #INTERVAL_TYPE == 'Week'">
    <s:text name="INTERVAL_TYPE_WEEK" />
</s:elseif>
<s:elseif test="#INTERVAL_TYPE !=null && #INTERVAL_TYPE == 'Month'">
    <s:text name="INTERVAL_TYPE_MONTH" />
</s:elseif>
<s:elseif test="#INTERVAL_TYPE !=null && #INTERVAL_TYPE == 'Year'">
    <s:text name="INTERVAL_TYPE_YEAR" />
</s:elseif>
