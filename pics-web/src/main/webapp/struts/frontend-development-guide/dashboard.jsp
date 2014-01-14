<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<style>
    span.title {
        color: #ffffff !important;
        font-weight: normal !important;
    }

    span.value {
        color: #ffffff !important;
    }
</style>

<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">Style Guide</s:param>
    <s:param name="subtitle">PICS Styles and Conventions</s:param>
    <s:param name="breadcrumb_name"></s:param>
    <s:param name="breadcrumb_id"></s:param>
    <s:param name="actions">
        <a href="#" class="btn btn-default show-markup">Show Markup</a>
    </s:param>
</s:include>

<div class="row">
    <div class="col-md-3">
        <s:include value="_components-menu.jsp" />
    </div>
    <div class="col-md-9">
        <s:include value="components/_components.jsp" />
        <s:include value="colors/_colors.jsp" />
        <s:include value="_html-elements.jsp" />
    </div>
</div>

<footer></footer>