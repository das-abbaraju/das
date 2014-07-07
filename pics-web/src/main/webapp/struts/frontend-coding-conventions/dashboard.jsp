<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">Frontend Coding Conventions</s:param>
    <s:param name="subtitle">PICS Conventions and Best Practices for JavaScript, HTML, and CSS</s:param>
    <s:param name="breadcrumb_name"></s:param>
    <s:param name="breadcrumb_id"></s:param>
    <s:param name="actions">
        <a href="#" class="btn btn-default show-markup">Show Examples</a>
    </s:param>
</s:include>

<div class="row">
    <div class="col-md-3">
        <s:include value="_menu.jsp" />
    </div>
    <div class="col-md-9">
        <s:include value="sections/_sections.jsp" />
    </div>
</div>

<footer></footer>