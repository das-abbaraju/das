<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<div class="helpOnRight">
These contractors will be deactivated from the PICS system in the next few days. If you expect to do 
work with any of these contractors, please encourage them to renew their 
membership by contacting PICS.
</div>

<table class="report" style="clear : none;">
	<thead>
	<tr>
		<td colspan="2">Contractor Name</td>
		<td>Due Date</td>
		<s:if test="permissions.admin">
			<td>Amount</td>
			<td>Facility Count</td>
		</s:if>
		<td>Days Left</td>
		<s:if test="showContact">
			<td>Primary Contact</td>
			<td>Phone</td>
			<td>Email</td>
			<td>Office Address</td>
			<td><a href="javascript: changeOrderBy('form1','a.city,a.name');">City</a></td>
			<td><a href="javascript: changeOrderBy('form1','a.state,a.name');">State</a></td>
			<td>Zip</td>
			<td>Web_URL</td>
		</s:if>
		<s:if test="showTrade">
			<td>Trade</td>
		</s:if>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td>
				<a href="ContractorView.action?id=<s:property value="[0].get('id')"/>">
				<s:property value="[0].get('name')" /></a>
			</td>
			<td class="center"><s:date name="[0].get('dueDate')" format="M/d/yy" /></td>
			<s:if test="permissions.admin">
				<td><s:property value="get('invoiceAmount')"/></td>
				<td><s:property value="get('facilityCount')"/></td>
			</s:if>
			<td class="center"><s:property value="[0].get('DaysLeft')" /></td>
			<s:if test="showContact">
				<td><s:property value="get('contactname')"/></td>
				<td><s:property value="get('contactphone')"/></td>
				<td><s:property value="get('contactemail')"/></td>
				<td><s:property value="get('address')"/></td>
				<td><s:property value="get('city')"/></td>
				<td><s:property value="get('state')"/></td>
				<td><s:property value="get('zip')"/></td>
				<td><s:property value="get('web_URL')"/></td>
			</s:if>
			<s:if test="showTrade">
				<td><s:property value="get('main_trade')"/></td>
			</s:if>
		</tr>
	</s:iterator>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>