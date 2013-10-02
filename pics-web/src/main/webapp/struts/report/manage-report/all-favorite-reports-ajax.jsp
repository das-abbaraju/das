<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set var="reports" value="reportFavoriteList" />

{
"reports":
[
<s:iterator value="#reports" var="report" status="rowstatus">
    {
        "name":"${report.name}",
        "id":${report.id}
    }<s:if test="!#rowstatus.last">,</s:if>
</s:iterator>
]}