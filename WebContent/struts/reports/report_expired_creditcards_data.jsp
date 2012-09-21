<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<div><s:property value="report.pageLinksWithDynamicForm"
	escape="false" /></div>
	
<s:form method="post" action="ReportExpiredCreditCards" cssClass="forms">
<div>
	<input type="submit" class="picsbutton positive" name="button" value="Send Email"/>
</div>

<table class="report">
	<thead>
	<tr>
		<td></td>
		<td>Email <br/>
			<input title="Check all" type="checkbox" onclick="setAllChecked(this);"/>
		</td>
	    <th><a href="javascript: changeOrderBy('form1','a.name');" >Contractor</a></th>
	    <th>Status</th>
	    <th>Payment Expires</th>
	    <th>CC Expiration</th>
	    <th>Balance</th>
	    <th>Email Sent</th>
	</tr>
	</thead>
	<tbody>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td align="center"><s:checkbox name="sendMail" cssClass="massCheckable" fieldValue="%{get('id')}" /></td>
			<td><a target="_BLANK" href="ContractorPaymentOptions.action?id=<s:property value="get('id')"/>"><s:property value="get('name')" /></a></td>
			<td class="center"><s:property value="get('status')"/></td>
			<td class="center"><s:date name="get('paymentExpires')" format="%{@com.picsauditing.util.PicsDateFormat@Iso}"/></td>
			<td class="right"><s:date name="get('ccExpiration')" format="%{@com.picsauditing.util.PicsDateFormat@MonthAndYear}"/></td>
			<td class="right">$<s:property value="get('balance')"/></td>
			<td class="right"><s:date name="get('lastSent')" format="%{@com.picsauditing.util.PicsDateFormat@Datetime}" /></td>
		</tr>
	</s:iterator>
	</tbody>
</table>

<div>
	<input type="submit" class="picsbutton positive" name="button" value="Send Email"/>
</div>
</s:form>

<div><s:property value="report.pageLinksWithDynamicForm"
	escape="false" /></div>