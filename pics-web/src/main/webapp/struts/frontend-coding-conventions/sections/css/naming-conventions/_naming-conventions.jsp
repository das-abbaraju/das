<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set var="section_id">${section_id_prefix}_naming_conventions</s:set>
<s:set var="header_title">${section_title}: Naming Conventions</s:set>

<section class="employee-guard-section" id="${section_id}">
    <h1 class="heading">
        <div class="row">
            <div class="col-md-9 col-xs-9">
                ${header_title}
            </div>
        </div>
    </h1>

    <s:include value="_naming-conventions-1.jsp" />
    <s:include value="_naming-conventions-2.jsp" />
    <s:include value="_naming-conventions-3.jsp" />
</section>