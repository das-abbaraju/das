<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set var="section_id">${param.section_id}</s:set>
<s:set var="header_title">${param.header_title}</s:set>
<s:set var="description">${param.description}</s:set>
<s:set var="example_url">${param.example_url}</s:set>
<s:set var="accordian_parent_id">${param.accordian_parent_id}</s:set>
<s:set var="html_code">${param.html_code}</s:set>
<s:set var="struts_code">${param.struts_code}</s:set>

<section class="employee-guard-section" id="${section_id}">
    <h1 class="heading">
        <div class="row">
            <div class="col-md-9 col-xs-9">
                ${header_title}
            </div>
        </div>
    </h1>

    <p class="description">
        ${description}
    </p>

    <div class="row example">
        <div class="col-md-offset-1 col-md-11">
            <s:include value="%{#example_url}" />
        </div>
    </div>

    <div class="row implementations">
        <div class="col-md-offset-1 col-md-11">
            <s:include value="/struts/frontend-development-guide/components/_implementations.jsp">
                <s:param name="parent_id">${accordian_parent_id}</s:param>
                <s:param name="html_code">${html_code}</s:param>
                <s:param name="html_code">${struts_code}</s:param>
            </s:include>
        </div>
    </div>
</section>