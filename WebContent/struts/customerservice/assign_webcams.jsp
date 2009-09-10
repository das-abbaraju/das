<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Assign Webcams</title>
<link rel="stylesheet" href="css/reports.css" />
<link rel="stylesheet" href="css/forms.css" />
<s:include value="../jquery.jsp" />
<script type="text/javascript">
$(function(){
	$.gritter.add({title:'Webcam Notification',text:'The following <s:property value="audits.size"/> audits require webcams.'});
});
</script>
</head>
<body>
<h1>Assign Webcams</h1>

<s:include value="../actionMessages.jsp" />

<div class="left" id="audit_list">
<table class="report">
	<thead>
		<tr>
			<td>id</td>
			<td>Contractor</td>
			<td>Type</td>
			<td>Scheduled Date</td>
		</tr>
	</thead>
	<s:iterator value="audits" status="stat">
		<tr>
			<td><s:property value="id" /></td>
			<td><s:property value="contractorAccount.name" /></td>
			<td><s:property value="auditType.auditName" /></td>
			<td><s:date name="scheduledDate" format="MM/dd/yyyy hh:mm a"/></td>
		</tr>
	</s:iterator>
</table>
</div>

<s:if test="audit != null">
<div>
	<s:form>
		<fieldset class="form">
			<legend><span>Contact Info</span></legend>
			<ol>
				<li><label>Contact Name:</label><s:textfield name="audit.contractorContact"/></li>
				<li><label>Address:</label><s:textfield name="address"/></li>
				<li><label>City:</label><s:textfield name="city"/></li>
				<li><label>State:</label><s:textfield name="state"/></li>
				<li><label>Zip:</label><s:textfield name="zip"/></li>
			</ol>
		</fieldset>
	</s:form>
</div>
</s:if>

<br clear="all" />
</body>
</html>