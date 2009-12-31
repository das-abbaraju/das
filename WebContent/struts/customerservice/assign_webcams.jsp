<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Assign Webcams</title>
<link rel="stylesheet" href="css/reports.css?v=20091231" />
<link rel="stylesheet" href="css/forms.css?v=20091231" />
<s:include value="../jquery.jsp" />

<s:if test="audit != null">
<script type="text/javascript">
var contractor = {
	'audit.contractorContact': '<s:property value="audit.contractorAccount.contact"/>',
	'audit.address': '<s:property value="audit.contractorAccount.address"/>',
	'audit.address2': '<s:property value="audit.contractorAccount.address2"/>',
	'audit.city': '<s:property value="audit.contractorAccount.city"/>',
	'audit.country': '<s:property value="audit.contractorAccount.country"/>',
	'audit.state': '<s:property value="audit.contractorAccount.state"/>',
	'audit.zip': '<s:property value="audit.contractorAccount.zip"/>',
	'audit.phone': '<s:property value="audit.contractorAccount.phone"/>',
	'audit.phone2': '<s:property value="audit.contractorAccount.email"/>'
};

function useContractor() {
	$.each(contractor, function(k,v) {
		$('form [name='+k+']').parent().find('span').fadeOut().remove();
		$('form [name='+k+']').val(v);
	});
	changeState(contractor['audit.country']);
}

function showContractor() {
	$.each(contractor, function(k,v) {
		$('form [name='+k+']').parent().find('span').fadeOut().remove();
		$('form [name='+k+']').parent().append($('<span style="float:right">'+v+'</span>').hide()).find('span').fadeIn();
	});
}

function changeState(state) {
	if (state == 'US' || state == 'CA') {
		$('#state_sel').show('slow');
	} else {
		$('#state_sel').hide('slow');
	}
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
			<td>Scheduled Date</td>
			<td>Webcam</td>
		</tr>
	</thead>
	<s:iterator value="audits" status="stat">
		<tr class="clickable" onclick="location.href='?audit.id=<s:property value="id"/>'">
			<td><a href="?audit.id=<s:property value="id"/>"><s:property value="id"/></a></td>
			<td><s:property value="contractorAccount.name" /></td>
			<td><s:date name="scheduledDate" format="MM/dd/yyyy hh:mm a"/></td>
			<td class="center"><s:if test="contractorAccount.webcam != null"><img src="images/icon_webcam.png"/></s:if></td>
		</tr>
	</s:iterator>
</table>
</div>

<s:if test="audit != null">
<div style="float:left">
	<s:form>
		<s:hidden name="audit.id"/>
		<fieldset class="form">
			<legend><span><s:property value="audit.contractorAccount.name"/></span></legend>
			<ol>
				<li><label></label><input type="button" value="Same as Primary" onclick="useContractor()"/>
					<input type="button" value="Show Primary Info" onclick="showContractor()"/></li>
				<li><label>Contact Name:</label><s:textfield name="audit.contractorContact"/></li>
				<li><label>Address:</label><s:textfield name="audit.address"/></li>
				<li><label>Address2:</label><s:textfield name="audit.address2"/></li>
				<li><label>City:</label><s:textfield name="audit.city"/></li>
				<li>
					<label>Country:</label>
					<s:select list="countryList" name="audit.country"
							headerKey="" headerValue="- Country -"
							listKey="isoCode" listValue="name"
							onchange="changeState(this.value);"/>
				</li>
				<li id="state_sel"><label>State:</label>
					<s:select list="stateList" name="audit.state" 
						headerKey="" headerValue="- State -" listKey="isoCode" listValue="name"/>
				</li>
				<li><label>Zip:</label><s:textfield name="audit.zip"/></li>
				<li><label>Phone:</label><s:textfield name="audit.phone"/></li>
				<li><label>Email:</label><s:textfield name="audit.phone2"/></li>
			</ol>
		</fieldset>
		<fieldset class="form">
			<legend><span>Webcam</span></legend>
			<ol>
				<li>
					<label>Webcam:</label>
					<s:select name="webcam.id" list="webcams" listKey="id" headerKey="0" headerValue="- Select a Webcam -"/>
				</li>
				<li><label>Shipping Method:</label><s:select list="{'Ground','Two Day','Express Saver','Overnight'}" name="webcam.shippingMethod"/></li>
				<li><label>Tracking Number:</label><s:textfield name="webcam.trackingNumber"/>
					<s:if test="webcam.trackingNumber.trim().length() > 0">
						<a href="http://www.fedex.com/Tracking?tracknumber_list=<s:property value="webcam.trackingNumber"/>" target="_blank">Track Webcam</a>
					</s:if>
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