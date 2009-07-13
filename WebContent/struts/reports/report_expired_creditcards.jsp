<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title><s:property value="reportName" /></title>
<s:include value="reportHeader.jsp" />
<script type="text/javascript" src="js/prototype.js"></script>
<script type="text/javascript"
	src="js/scriptaculous/scriptaculous.js?load=effects,controls"></script>
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
		<td>Email</td>
	    <th><a href="javascript: changeOrderBy('form1','a.name');" >Contractor</a></th>
	    <th>Contact</th>
	    <th>Phone Number</th>
	    <th>Active</th>
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
			<td align="center"><s:checkbox name="sendMail" fieldValue="%{get('id')}" /></td>
			<td><a href="ContractorView.action?id=<s:property value="get('id')"/>"><s:property value="get('name')" /></a></td>
			<td class="right"><s:property value="get('billingContact')"/></td>
			<td class="right"><s:property value="get('billingPhone')"/></td>
			<td class="center"><s:property value="get('active')"/></td>
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
