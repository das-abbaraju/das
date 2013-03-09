<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<ul class="report-list unstyled">
    <s:iterator value="#reports" var="report">
        <%-- Url --%>
        <s:url action="Report" var="report_url">
            <s:param name="report">${report.id}</s:param>
        </s:url>

        <li class="report clearfix">
            <div class="summary">
                <div>
                    <a href="${report_url}" class="name">${report.name}</a>
    
                    <s:if test="#report.createdBy.id != permissions.userId">
                        <span class="created-by"><s:text name="ManageReports.report.createdBy" /> ${report.createdBy.name}</span>
                    </s:if>
                </div>

                <p class="description">${report.description}</p>
            </div>

            <span class="number-favorites">
                ${report.numTimesFavorited} <s:text name="ManageReports.report.Favorites" />
            </span>
        </li>
    </s:iterator>
</ul>