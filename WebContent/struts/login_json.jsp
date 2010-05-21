<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<% // This is a blank jsp page created for JSON calls %>

function <s:property value="callback"/>(){
return <s:property value="json" escape="false" />;
}