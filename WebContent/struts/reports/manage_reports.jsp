<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page import="com.picsauditing.util.business.DynamicReportUtil" %>

<title><s:property value="report.summary"/></title>

<s:include value="../actionMessages.jsp" />

<h1>Manage Reports</h1>
<h2>Favorite, move, update, and search for new reports</h2>

<div id="user_edit">
    <ul class="nav nav-pills">
        <li <s:if test="viewType.equals('favorite')">class="active"</s:if>>
            <a href="ManageReports.action?viewType=favorite">Favorites</a>
        </li>
        <li <s:if test="viewType.equals('saved')">class="active"</s:if>>
            <a href="ManageReports.action?viewType=saved">Saved</a>
        </li>
	</ul>
</div>

<h3>These reports will show in your Reports menu dropdown</h3>

<ul id="reportList">
    <s:iterator value="userReports">
	    <li>
			<a href="ManageReports!toggleFavorite.action?reportId=<s:property value="report.id" />">
			    <s:if test="favorite">
                    <i class="icon-star icon-large favorite"></i>
			    </s:if>
			    <s:else>
                    <i class="icon-star icon-large"></i>
                </s:else>
			</a>
			<a class="report_name" href="ReportDynamic.action?report=<s:property value="report" />">
			    <s:property value="report.name" />
			</a>

			<!-- TODO remove this hack after the MVP demo -->
			<s:if test="report.id != 11 && report.id != 12">
			    <span class="report_created_by">Created by <s:property value="report.createdBy.name" /></span>
			</s:if>
			<s:else>
			    <span class="report_created_by">Created by PICS</span>
			</s:else>
	        <s:if test="report.id != 11 && report.id != 12">
				<s:if test="%{@com.picsauditing.util.business.DynamicReportUtil@canUserDelete(permissions.userId, report)}">
				    <a class="report_actions" href="ManageReports!deleteReport.action?reportId=<s:property value="report.id" />">Delete</a>
				</s:if>
				<s:else>
				    <a class="report_actions" href="ManageReports!removeReportUserAssociation.action?reportId=<s:property value="report.id" />">Remove</a>
				</s:else>
	        </s:if>

	    </li>
    </s:iterator>
</ul>