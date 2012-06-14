<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>
<%@ page import="com.picsauditing.util.business.DynamicReportUtil" %>

<head>
    <title><s:property value="report.summary"/></title>
    <link rel="stylesheet" type="text/css" href="css/reports/manage_reports.css">

</head>

<body>
    <h1>Manage Reports</h1>
    <h2>Favorite, move, update, and search for new reports</h2>
    
    <div id="userActions">
        <button name="Favorites" value="Favorites" class="btn info">Favorites</button>
        <button name="Saved" value="Saved">Saved</button>
    </div>
    
    
<ul class="nav nav-pills">
  <li class="active">
    <a href="#">Home</a>
  </li>
  <li><a href="#">...</a></li>
  <li><a href="#">...</a></li>
</ul>    
    
    
	<div id="user_edit">
		<s:include value="../actionMessages.jsp" />
		<fieldset class="form submit">
			<s:if test="user != null">
				<ul class="manage-users-actions">
					<li>
						<a class="btn" href="ManageReports.action?viewType=favorite">
							Favorites
						</a>
					</li>
					<li>
						<a class="btn" href="ManageReports.action?viewType=saved">
							Saved
						</a>
					</li>
				</ul>
			</s:if>
		</fieldset>
	</div>

	<div id="reportTable">
		<table>
			<tr>
				<th></th>
				<th>Report Name</th>
				<th></th>
				<th></th>
				<th></th>
				<th></th>
			</tr>

		    <s:iterator value="userReports">
			    <tr>
			    	<td>
		    			<a href="ManageReports!toggleFavorite.action?reportId=<s:property value="report.id" />">
				    		<s:if test="favorite">
				    			Favorite
				    		</s:if>
				    		<s:else>
				    			Normal
				    		</s:else>
			    		</a>&nbsp;
		    		</td>

			    	<td>
			    		<a href="ReportDynamic.action?report=<s:property value="report" />">
			    			<s:property value="report.name" />
			    		</a>
						<!-- TODO remove this hack after the MVP demo -->
						<s:if test="report.id != 11 && report.id != 12">
				    		Created by <s:property value="report.createdBy" />
				    	</s:if>
				    	<s:else>
				    		Created by PICS
				    	</s:else>
		    		</td>

<!-- 					<td> -->
<!-- 						<a href="javascript:" name="modal">Edit</a>&nbsp; -->
<!-- 						<div id="boxes"> -->
<!-- 						    #customize your modal window here -->
<!-- 						    <div class="ReportDialog window"> -->
<%-- 						        <s:form cssClass="form" id="userSaveReportName"> --%>
<%-- 						    		<s:hidden name="report.id" /> --%>
<!-- 									<fieldset> -->
<!-- 									Change Name -->
<!-- 										<li> -->
<%-- 											<s:textfield name="name" value="%{report.name}" /> --%>
<%-- 											<pics:fieldhelp title="Change Name"> --%>
<!-- 												<p> Change Name </p> -->
<%-- 											</pics:fieldhelp> --%>
<!-- 										</li> -->
<!-- 										Change Description -->
<!-- 										<li> -->
<%-- 											<s:textfield name="description" value="%{report.description}" /> --%>
<%-- 											<pics:fieldhelp title="Change Description"> --%>
<!-- 												<p> Change Description </p> -->
<%-- 											</pics:fieldhelp> --%>
<!-- 										</li> -->
<!-- 									</fieldset> -->
<!-- 									<fieldset class="form submit"> -->
<!-- 										<li> -->
<%-- 										<s:submit value="%{getText('button.Save')}" cssClass="picsbutton positive" method="changeReportName" /> --%>
<!-- 										<a href="#" class="close">Cancel</a> -->
<!-- 										</li> -->
<!-- 									</fieldset> -->
<%-- 								</s:form> --%>
<!-- 						    </div> -->
<!-- 						    Do not remove div#mask, because you'll need it to fill the whole screen -->
<!-- 						    <div id="mask"></div> -->
<!-- 						</div> -->
<!-- 					</td> -->
<!-- 					<td> -->
<%-- 						<a href="ManageReports!copyReport.action?reportId=<s:property value="report.id" />">Copy</a>&nbsp; --%>
<!-- 					</td> -->
					<td>
						<!-- TODO remove this hack after the MVP demo -->
						<s:if test="report.id != 11 && report.id != 12">
							<s:if test="%{@com.picsauditing.util.business.DynamicReportUtil@canUserDelete(permissions.userId, report)}">
								<a href="ManageReports!deleteReport.action?reportId=<s:property value="report.id" />">
									Delete
								</a>
							</s:if>
							<s:else>
								<a href="ManageReports!removeReportUserAssociation.action?reportId=<s:property value="report.id" />">
									Remove
								</a>
							</s:else>
						</s:if>
					</td>
				</tr>
			</s:iterator>
		</table>
	</div>
</body>
