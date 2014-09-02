<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set var="section_title">Status Lists</s:set>
<s:set var="section_id_prefix">status_lists</s:set>

<s:include value="interactive-status-list/_interactive-status-list.jsp" />
<s:include value="static-status-list/_static-status-list.jsp" />