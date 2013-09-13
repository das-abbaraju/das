<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page import="com.picsauditing.PICS.DateBean"%>
<html>
	<head>
		<title>Schedule &amp; Assign Audits</title>
		<s:include value="reportHeader.jsp" />
		<script type="text/javascript">
			$(function() {
				$('.datepicker').datepicker({
					changeMonth : true,
					changeYear : true,
					yearRange : '1940:2039',
					showOn : 'button',
					buttonImage : 'images/icon_calendar.gif',
					buttonImageOnly : true,
					buttonText : translate('JS.ChooseADate'),
					constrainInput : true,
					showAnim : 'fadeIn'
				});
				
				$("#checkAll").click(function() {
					if ($(this).is(":checked")) {
						$(".checkEmail").attr("checked", true);
					} else {
						$(".checkEmail").attr("checked", false);
					}
				});
			});
		</script>
	</head>
	<body>
		<h1>Pending PQF Audits</h1>
		<s:include value="filters.jsp" />
		<div>
			<s:property value="report.pageLinksWithDynamicForm" escape="false" />
		</div>
		<s:form id="assignScheduleAuditsForm" method="post" cssClass="forms">
			<div>
				<s:submit cssClass="picsbutton positive" method="sendEmail" value="Send Email" />
			</div>
			<div>
				<input type="checkbox" id="checkAll" /> Check All
			</div>
			<table class="report">
				<thead>
					<tr>
						<td></td>
						<td>
							<s:text name="User.email" />
						</td>
						<td>
							<a href="javascript: changeOrderBy('form1','a.name');">Contractor</a>
						</td>
						<td>
							Audit
						</td>
						<td align="center">
							<a href="javascript: changeOrderBy('form1','createdDate ASC');">Created</a>
						</td>
						<td align="center">
							<a href="javascript: changeOrderBy('form1','cao.percentComplete ASC');">%Complete</a>
						</td>
						<td>
							Contacted
						</td>
						<td>
							<a href="javascript: changeOrderBy('form1','scheduledDate ASC');">FollowUp</a>
						</td>
					</tr>
				</thead>
				<s:iterator value="data" status="stat">
					<tr id="audit_<s:property value="get('auditID')"/>">
						<td class="right">
							<s:property value="#stat.index + report.firstRowNumber" />
						</td>
						<td align="center">
							<s:checkbox name="sendMail" fieldValue="%{get('auditID')}" cssClass="checkEmail" />
						</td>
						<td>
							<a href="ContractorView.action?id=<s:property value="get('id')"/>">
								<s:property value="[0].get('name')" />
							</a>
						</td>
						<td>
							<a href="Audit.action?auditID=<s:property value="[0].get('auditID')"/>">
								<s:text name="%{[0].get('atype.name')}" />
							</a>
						</td>
						<td class="reportDate">
							<s:date name="get('createdDate')" format="%{@com.picsauditing.util.PicsDateFormat@Iso}" />
						</td>
						<td align="center">
							<s:property value="get('percentComplete')" />
						</td>
						<td>
							<s:property value="get('followUp')" />
						</td>
						<td>
							<input class="datepicker" size="6" type="text"
								name="scheduledDate[<s:property value="[0].get('auditID')"/>]"
								value="<s:property value="getBetterDate( [0].get('scheduledDate'), @com.picsauditing.util.PicsDateFormat@American)"/>" />
						</td>
					</tr>
				</s:iterator>
			</table>
			<div>
				<s:submit cssClass="picsbutton positive" method="sendEmail" value="Send Email" />
			</div>
		</s:form>
		<div>
			<s:property value="report.pageLinksWithDynamicForm" escape="false" />
		</div>
	</body>
</html>
