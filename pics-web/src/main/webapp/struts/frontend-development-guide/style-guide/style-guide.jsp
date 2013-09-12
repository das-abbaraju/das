<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/layout/_page-header.jsp">
    <s:param name="title">PICS Style Guide</s:param>
</s:include>

<s:include value="/struts/frontend-development-guide/_menu.jsp" />

<div class="row">
    <div class="span3 side-bar">
        <s:include value="_menu.jsp" />
    </div>
    <div class="span9">
        <s:include value="_overview.jsp" />
        
        <s:include value="_typography.jsp" />
        
        <s:include value="_colors.jsp" />
    </div>
</div>