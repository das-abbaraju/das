<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title><s:property value="reportName" /></title>
<s:include value="reportHeader.jsp" />
<script type="text/javascript">
function setAllChecked(elm) {
	$('.massCheckable').attr({checked: $(elm).is(':checked')});
	return false;
}
</script>
</head>
<body>
<h1><s:property value="reportName" /></h1>

<s:include value="filters.jsp"/>

<div><s:property value="report.pageLinksWithDynamicForm"
	escape="false" /></div>
	
<s:form method="post" cssClass="forms">
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
			<td><a href="ContractorView.action?id=<s:property value="get('id')"/>"><s:property value="get('name')" /></a></td>
			<td class="center"><s:property value="get('status')"/></td>
			<td class="center"><s:date name="get('paymentExpires')" format="M/d/yy"/></td>
			<td class="right"><s:date name="get('ccExpiration')" format="M/yy"/></td>
			<td class="right">$<s:property value="get('balance')"/></td>
			<td class="right"><s:date name="get('lastSent')" format="M/d/yy h:mm" /></td>
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

</body>
</html>
