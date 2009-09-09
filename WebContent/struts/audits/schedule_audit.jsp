<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Schedule Audit</title>
<meta name="help" content="Scheduling_Audits">

<link rel="stylesheet" type="text/css" media="screen"
	href="css/audit.css" />
<link rel="stylesheet" type="text/css" media="screen"
	href="css/forms.css" />
<style type="text/css">
#auditHeader,#auditHeaderNav {
	display: none;
}
</style>

</head>
<body>
<s:include value="../contractors/conHeader.jsp" />

<s:form>
	<s:hidden name="auditID" />
	<fieldset class="form"><legend><span>Contact
	Person</span></legend>
	<ol>
		<li>Please tell us who will be the primary contact for this
		audit.</li>
		<li><label for="u.name">Name:</label> <s:textfield name="u.name" /></li>
		<li><label for="u.email">Phone:</label> <s:textfield
			name="u.email" size="30" /></li>
		<li><label for="u.email">Email:</label> <s:textfield
			name="u.email" size="30" /></li>
	</ol>
	</fieldset>
	<fieldset class="form"><legend><span>Enter
	the Audit Location</span></legend>
	<ol>
		<li>Please tell us the location at which you wish to conduct the
		audit.</li>
		<li><label for="u.name">Use Existing:</label> <s:checkbox
			name="auditID"></s:checkbox></li>
		<li><label for="u.name">Address:</label> <s:textfield
			name="u.name" /></li>
		<li><label for="u.email">Address 2:</label> <s:textfield
			name="u.email" size="30" /></li>
		<li><label for="u.email">City:</label> <s:textfield
			name="u.email" size="30" /></li>
		<li><label for="u.phone">Country:</label> <s:textfield
			name="u.phone" size="20" /></li>
		<li><label for="u.phone">State:</label> <s:textfield
			name="u.phone" size="20" /></li>
		<li><label for="u.fax">Zip:</label> <s:textfield name="u.fax"
			size="20" /></li>
	</ol>
	</fieldset>
	<fieldset class="form submit">
	<div>
	<button id="saveButton" class="picsbutton positive"
		value="Save Profile" name="button" type="submit">Verify
	Address</button>
	</div>
	</fieldset>
	<fieldset class="form"><legend><span>Select
	the Audit Time</span></legend>
	<ol>
		<li>We will be conducting an On Site Audit at 123 Main, Irvine, CA <a href="">Change the Location</a></li>
		<li>We will be conducting a Web Audit. We will ship a webcam to 123 Main, Irvine, CA <a href="">Change the Location</a></li>
		<li>Please select a date and time block for your audit.</li>
		<li>
		<table width="100%">
			<tr>
				<td><b>Tuesday, September 9, 2009</b><br />
				<a href="">7 AM to 9 AM EDT</a><br />
				<a href="">10 AM to 12 PM EDT</a><br />
				<a href="">11 AM to 1 PM</a></td>
				<td><b>Tuesday, September 9, 2009</b><br />
				<a href="">7 AM to 9 AM</a><br />
				<a href="">10 AM to 12 PM</a><br />
				<a href="">11 AM to 1 PM</a></td>
				<td><b>Tuesday, September 9, 2009</b><br />
				<a href="">7 AM to 9 AM</a><br />
				<a href="">10 AM to 12 PM</a><br />
				<a href="">11 AM to 1 PM</a></td>
				<td><b>Tuesday, September 9, 2009</b><br />
				<a href="">7 AM to 9 AM</a><br />
				<a href="">10 AM to 12 PM</a><br />
				<a href="">11 AM to 1 PM</a></td>
			</tr>
			<tr>
				<td><b>Tuesday, September 9, 2009</b><br />
				<a href="">7 AM to 9 AM</a><br />
				<a href="">10 AM to 12 PM</a><br />
				<a href="">11 AM to 1 PM</a></td>
				<td><b>Tuesday, September 9, 2009</b><br />
				<a href="">7 AM to 9 AM</a><br />
				<a href="">10 AM to 12 PM</a><br />
				<a href="">11 AM to 1 PM</a></td>
				<td><b>Tuesday, September 9, 2009</b><br />
				<a href="">7 AM to 9 AM</a><br />
				<a href="">10 AM to 12 PM</a><br />
				<a href="">11 AM to 1 PM</a></td>
				<td><b>Tuesday, September 9, 2009</b><br />
				<a href="">7 AM to 9 AM</a><br />
				<a href="">10 AM to 12 PM</a><br />
				<a href="">11 AM to 1 PM</a></td>
			</tr>
			<tr>
				<td><b>Tuesday, September 9, 2009</b><br />
				<a href="">7 AM to 9 AM</a><br />
				<a href="">10 AM to 12 PM</a><br />
				<a href="">11 AM to 1 PM</a></td>
				<td><b>Tuesday, September 9, 2009</b><br />
				<a href="">7 AM to 9 AM</a><br />
				<a href="">10 AM to 12 PM</a><br />
				<a href="">11 AM to 1 PM</a></td>
				<td><b>Tuesday, September 9, 2009</b><br />
				<a href="">7 AM to 9 AM</a><br />
				<a href="">10 AM to 12 PM</a><br />
				<a href="">11 AM to 1 PM</a></td>
				<td><b>Tuesday, September 9, 2009</b><br />
				<a href="">7 AM to 9 AM</a><br />
				<a href="">10 AM to 12 PM EST</a><br />
				<a href="">11 AM to 1 PM EST</a></td>
			</tr>
		</table>
		</li>
		<li><a href="">Show me more available time slots</a></li>
	</ol>
	</fieldset>
	<fieldset class="form submit">
	<div>
	<button id="saveButton" class="picsbutton positive"
		value="Save Profile" name="button" type="submit">Verify
	Address</button>
	</div>
	</fieldset>
	<fieldset class="form"><legend><span>Confirmation</span></legend>
	<ol>
		<li>Please confirm the information below:</li>
		<li><label>Primary Contact:</label> John McLovin</li>
		<li><label>Method:</label> On Site</li>
		<li><label>Location:</label> 123 Main Street Ste 145, Irvine, CA
		92604</li>

		<li><label>Audit Date:</label> Monday, September 19, 2009</li>
		<li><label>Audit Time:</label> 10:00 AM EST</li>

		<li><label>Auditor Name:</label> Harvey Staal</li>
		<li><label>Auditor Email:</label> hstaal@picsauditing.com</li>
		<li><label>Auditor Phone:</label> (949) 123-4567</li>
		<li><label>Auditor Fax:</label> (949) 123-4567</li>
		<li>If you have any questions about your upcoming audit, please
		contact your assigned auditor directly.</li>
	</ol>
	</fieldset>
	<fieldset class="form submit">
	<div>
	<ol>
		<li><s:checkbox name=""></s:checkbox> By checking this box, I
		understand if I need to reschedule this audit, I must do so before
		Thursday or I will be subject to a $150 rescheduling fee.</li>
	</ol>
	<button id="saveButton" class="picsbutton positive"
		value="Save Profile" name="button" type="submit">Confirm
	Audit</button>
	</div>
	</fieldset>

	<div id="info">Congratulations, your audit is now scheduled. You
	should receive a confirmation email for your records.</div>
</s:form>

</body>
</html>