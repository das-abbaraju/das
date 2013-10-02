<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set var="callback_function_name">${param.callback}</s:set>
<s:set var="reports" value="reportFavoriteList" />

${callback_function_name}({
"reports":
[
<s:iterator value="#reports" var="report" status="rowstatus">
    {
        "name":"${report.name}",
        "id":${report.id}
    }<s:if test="!#rowstatus.last">,</s:if>
</s:iterator>
]});