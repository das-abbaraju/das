<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set var="section_id">${section_id_prefix}_page_header</s:set>
<s:set var="header_title">${section_title}: Best Practices</s:set>

<section class="employee-guard-section" id="${section_id}">
    <h1 class="heading">
        <div class="row">
            <div class="col-md-9 col-xs-9">
                ${header_title}
            </div>
        </div>
    </h1>

    <s:include value="_best-practices-1.jsp" />
    <s:include value="_best-practices-2.jsp" />
</section>