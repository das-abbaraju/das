<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Assign Webcams</title>
<link rel="stylesheet" href="css/reports.css" />
<link rel="stylesheet" href="css/forms.css" />
<s:include value="../jquery.jsp" />

<s:if test="audit != null">
<script type="text/javascript">
var contractor = {
	'audit.contractorContact': '<s:property value="audit.contractorAccount.contact"/>',
	'audit.address': '<s:property value="audit.contractorAccount.address"/>',
	'audit.city': '<s:property value="audit.contractorAccount.city"/>',
	'audit.state': '<s:property value="audit.contractorAccount.state"/>',
	'audit.zip': '<s:property value="audit.contractorAccount.zip"/>'
};

function useContractor() {
	$.each(contractor, function(k,v) {
		$('form [name='+k+']').val(v);
	});
}
</script>
</s:if>

</head>
<body>
<h1>Assign Webcams</h1>

<s:include value="../actionMessages.jsp" />

<s:if test="audits.size == 0">
<div class="info">There are currently no audits that require webcams.</div>
</s:if>

<s:else>
<div class="info">The following <s:property value="audits.size"/> audits require webcams.</div>

<div class="left" id="audit_list">
<table class="report">
	<thead>
		<tr>
			<td>id</td>
			<td>Contractor</td>
			<td>Type</td>
			<td>Scheduled Date</td>
			<td>Webcam</td>
		</tr>
	</thead>
	<s:iterator value="audits" status="stat">
		<tr>
			<td><a href="?audit.id=<s:property value="id"/>"><s:property value="id"/></a></td>
			<td><s:property value="contractorAccount.name" /></td>
			<td><s:property value="auditType.auditName" /></td>
			<td><s:date name="scheduledDate" format="MM/dd/yyyy hh:mm a"/></td>
			<td class="center"><s:if test="contractorAccount.webcam != null"><img src="images/icon_webcam.png"/></s:if></td>
		</tr>
	</s:iterator>
</table>
</div>

<s:if test="audit != null">
<div>
	<s:form>
		<s:hidden name="audit.id"/>
		<fieldset class="form">
			<legend><span>Contact Info</span></legend>
			<ol>
				<li><label></label><input type="button" value="Use Contractor Contact Info" onclick="useContractor()"/></li>
				<li><label>Contact Name:</label><s:textfield name="audit.contractorContact"/></li>
				<li><label>Address:</label><s:textfield name="audit.address"/></li>
				<li><label>City:</label><s:textfield name="audit.city"/></li>
				<li><label>State:</label><s:select list="@com.picsauditing.jpa.entities.State@getStates(true)" name="audit.state"/></li>
				<li><label>Zip:</label><s:textfield name="audit.zip"/></li>
			</ol>
		</fieldset>
		<fieldset class="form">
			<legend><span>Webcam</span></legend>
			<ol>
				<li>
					<label>Webcam:</label>
					<s:select name="webcam.id" list="webcams" listKey="id" headerKey="0" headerValue="- Select a Webcam -"/>
				</li>
			</ol>
		</fieldset>
		<fieldset class="form submit">
			<input type="submit" class="picsbutton positive" value="Save" name="button"/>
		</fieldset>
	</s:form>
</div>
</s:if>
</s:else>
<br clear="all" />
</body>
</html>