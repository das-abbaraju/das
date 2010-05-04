<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page import="com.picsauditing.PICS.DateBean"%>
<html>
<head>
<title>Schedule &amp; Assign Audits</title>
<s:include value="reportHeader.jsp" />
<SCRIPT LANGUAGE="JavaScript">
	var cal1 = new CalendarPopup('caldiv1');
	cal1.offsetY = -110;
	cal1.setCssPrefix("PICS");
	cal1.addDisabledDates(null,'<%= DateBean.getTodaysDate()%>');
</SCRIPT>
</head>
<body>
<h1>Pending PQF Audits</h1>
<s:include value="filters.jsp" />
<div><s:property value="report.pageLinksWithDynamicForm"
	escape="false" /></div>
<s:form id="assignScheduleAuditsForm" method="post" cssClass="forms">
	<div>
		<button class="picsbutton positive" type="submit" name="button" value="SendEmail">Send Email</button>
	</div>
	<table class="report">
		<thead>
			<tr>
				<td></td>
				<td>Email</td>
				<td><a href="javascript: changeOrderBy('form1','a.name');">Contractor</a></td>
				<td><a href="javascript: changeOrderBy('form1','auditName');">Audit</a></td>
				<td align="center"><a href="javascript: changeOrderBy('form1','createdDate ASC');">Created</a></td>
				<td align="center"><a href="javascript: changeOrderBy('form1','percentComplete ASC');">%Complete</a></td>
				<td>Contacted</td>
				<td><a href="javascript: changeOrderBy('form1','scheduledDate ASC');">FollowUp</a></td>
			</tr>
		</thead>
		<s:iterator value="data" status="stat">
			<tr id="audit_<s:property value="get('auditID')"/>">
				<td class="right"><s:property
					value="#stat.index + report.firstRowNumber" /></td>
				<td align="center"><s:checkbox name="sendMail" fieldValue="%{get('auditID')}" /></td>
				<td><a href="ContractorView.action?id=<s:property value="get('id')"/>"><s:property value="[0].get('name')"/></a>
				</td>
				<td><a href="Audit.action?auditID=<s:property value="[0].get('auditID')"/>"><s:property value="[0].get('auditName')"/></a></td>
				<td class="reportDate"><s:date name="get('createdDate')"
					format="M/d/yy" /></td>
				<td align="center"><s:property value="get('percentComplete')"/></td>
				<td><s:property value="get('followUp')"/></td>
				<td><input class="blueMain" size="6" type="text"
					name="scheduledDate[<s:property value="[0].get('auditID')"/>]"
					id="scheduledDate[<s:property value="[0].get('auditID')"/>]"
					value="<s:property value="getBetterDate( [0].get('scheduledDate'), 'MM/dd/yy')"/>" />
				<a id="anchor<s:property value="[0].get('auditID')"/>" name="anchor<s:property value="[0].get('auditID')"/>" href="#"
				onclick="cal1.select($('scheduledDate[<s:property value="[0].get('auditID')"/>]'),'anchor<s:property value="[0].get('auditID')"/>','M/d/yy'); return false;"
				><img src="images/icon_calendar.gif" width="18" height="15" border="0" /></a>
				</td>
			</tr>
		</s:iterator>
	</table>
	<div>
		<button class="picsbutton positive" type="submit" name="button" value="SendEmail">Send Email</button>
	</div>
	
</s:form>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<div id="caldiv1" style="position:absolute; visibility:hidden; background-color:white; layer-background-color:white;"></div>
</body>
</html>
