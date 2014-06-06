<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set var="section_title">Alerts</s:set>
<s:set var="section_id_prefix">alerts</s:set>

<s:include value="basic-alert-message/_basic-alert-message.jsp" />
<s:include value="alert-message-with-heading/_alert-message-with-heading.jsp" />
<s:include value="alert-message-with-dismiss-button/_alert-message-with-dismiss-button.jsp" />