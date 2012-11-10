<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/layout/_page-header.jsp">
    <s:param name="title">Page Layout</s:param>
</s:include>

<s:include value="/struts/frontend-development-guide/_menu.jsp" />

<div class="row">
    <div class="span3">
        <s:include value="_menu.jsp" />
    </div>
    <div class="span9">
        <s:include value="_navigation.jsp" />
        
        <s:include value="_environment.jsp" />
        
        <s:include value="_page-header.jsp" />
        
        <s:include value="_containers.jsp" />
        
        <s:include value="_offsets.jsp" />
    </div>
</div>