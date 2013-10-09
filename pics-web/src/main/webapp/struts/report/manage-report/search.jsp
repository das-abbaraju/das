<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set var="search_term_placeholder" value="%{getText('ManageReports.search.search.placeholder')}" />
<s:url action="ManageReports" method="search" var="manage_report_search_url" />

<s:set var="search_term_placeholder" value="%{getText('ManageReports.search.search.placeholder')}" />
<s:url action="ManageReports" method="search" var="manage_report_search_url" />

<s:include value="/struts/layout/_page-header.jsp">
    <s:param name="title"><s:text name="ManageReports.search.pageHeader.title" /></s:param>
    <s:param name="subtitle"><s:text name="ManageReports.search.pageHeader.subtitle" /></s:param>
</s:include>

<s:include value="/struts/report/manage-report/_menu.jsp" />

<s:if test="reportFavoriteList.isEmpty()">
	<div class="row">
		<section id="search_reports_instructions" class="offset3 span6">
			<h1><s:text name="ManageReports.search.instructions.header" /></h1>
			<p><s:text name="ManageReports.search.instructions.primaryInstructions" /></p>
			<p><s:text name="ManageReports.search.instructions.secondaryInstructions" /></p>
		</section>
	</div>
</s:if>
<s:else>
    <h4 class="report-subtitle"><s:text name="ManageReports.search.pageSubheader.subheader" /></h4>
</s:else>
<div id="report_search" class="row">
    <div class="offset4 span4">
        <form id="report_search_form" class="form-inline" action="${manage_report_search_url}">
            <input type="text" name="searchTerm" value="${searchTerm}" placeholder="${search_term_placeholder}" class="search-query span4" autocomplete="off" />
            <i class="icon-search"></i>
        </form>
    </div>
</div>

<div id="search_reports_container">
    <s:include value="/struts/report/manage-report/_search.jsp" />
</div>
