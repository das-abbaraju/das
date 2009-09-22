<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" errorPage="../../exception_handler.jsp"%>
<table class="cal_availability">
	<s:iterator value="nextAvailable.rows">
		<tr>
			<s:iterator value="days">
				<td>
				<h4><s:date name="key" format="EEEE, MMM d" /></h4>
				<s:iterator value="value">
					<br />
					<a
						href="ScheduleAudit.action?auditID=<s:property value="conAudit.id"/>&button=select&timeSelected=<s:date 
										name="startDate" format="%{@com.picsauditing.actions.audits.ScheduleAudit@DATE_FORMAT}" />"><s:property
						value="formatDate(startDate, 'h:mm a')" /> to <s:property
						value="formatDate(endDate, 'h:mm a z')" /></a>
				</s:iterator></td>
			</s:iterator>
		</tr>
	</s:iterator>
</table>
