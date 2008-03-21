<%@ page language="java" errorPage="exception_handler.jsp"%>
<%@ page language="java" import="com.picsauditing.PICS.*"%>
<%
	boolean isSubmitted = (null != request.getParameter("action") && request.getParameter("action").equals(
			"rsvp"));

	if (isSubmitted) {
		EmailBean.sendSafetyMeetingEmail(request);
	}
%>
<html>
<head>
<title>2008 Users Group</title>
<meta name="iconName" content="event" />
<script language="JavaScript" SRC="js/ImageSwap.js"></script>
<script src="js/AC_RunActiveContent.js" type="text/javascript"></script>
<script src="js/Validate.js"></script>
<style type="text/css">
<!--
.RSVPform {
	width: 260px;
	float: right;
	padding-top: 20px;
	padding-right: 0px;
	padding-bottom: 0px;
	padding-left: 0px;
}

.Safetypics {
	width: 100px;
	float: right;
	height: 125px;
	padding-top: 0px;
	padding-right: 5px;
	padding-bottom: 0px;
	padding-left: 5px;
}

.blueSafety {
	padding-top: 0px;
	padding-right: 0px;
	padding-bottom: 0px;
	padding-left: 10px;
	font-family: Verdana, Arial, Helvetica, sans-serif;
	font-size: 11px;
	line-height: 23px;
	color: #003366;
}

.style1 {
	font-size: 13px;
	font-weight: bold;
}

.padding {
	padding-right: 10px;
}

.style2 {
	font-size: 12px;
	font-weight: bold;
	color: #336699;
}

.style3 {
	font-size: 15px;
	color: #336699;
}

-->
input.invalid {
	background: #faa;
}

input.valid {
	background: #fff;
}
</style>
</head>
<body>
<table width="100%" border="0" cellpadding="13" cellspacing="0"
	bgcolor="#FFFFFF">
	<tr>
		<td width="375" class="blueMain" align="center">
		<div align="center" class="style1"><span class="style3">PICS
		CONTRACTOR USER GROUP MEETING </span><br>
		Long Beach, California</div>
		</td>
		<td bgcolor="#CBE5FE" class="blueMain">
		<div align="center">FEBRUARY 7, 2008</div>
		</td>
	</tr>
</table>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td height="30"></td>
	</tr>
</table>
<table width="100%" border="0" cellpadding="0" cellspacing="0">
	<tr valign="top">
		<td width="627" bgcolor="#F8F8F8" class="blueHome">
		<div align="justify" class="blueHome">
		<div class="RSVPform">
		<%
			if (isSubmitted)
				out.println("<span class='redMain'>Thank you for submitting your attendance information</span>");
		%>

		<form id="safetyForm" action="Usergroup_2008.jsp?action=rsvp"
			method="post">
		<table class="blueMain" bgcolor="#CBE5FE" width="100%" border="0"
			cellspacing="0" cellpadding="0">
			<tr>
				<td class="blueSafety"><strong>RSVP</strong></td>
				<td></td>
			</tr>
			<tr>
				<td class="blueSafety">Name</td>
				<td><input name="name" type="text" size="15" required></td>
			</tr>
			<tr>
				<td class="blueSafety">Phone</td>
				<td><input name="phone" type="text" size="13" required>
			</tr>
			<tr>
				<td class="blueSafety">Ext</td>
				<td><input name="ext" type="text" size="5"></td>
			</tr>
			<tr>
				<td class="blueSafety">Email</td>
				<td><input name="email" type="text" size="20"></td>
			</tr>
			<tr>
				<td class="blueSafety">Organization</td>
				<td><input name="organization" type="text" size="15" required></td>
			</tr>
			<tr>
				<td class="blueSafety">How Many People</td>
				<td><label> <select name="howmany" size="1">
					<option value="01" selected>01</option>
					<option value="02">02</option>
					<option value="03">03</option>
					<option value="04">04</option>
					<option value="05">05</option>
					<option value="06">06</option>
					<option value="07">07</option>
					<option value="08">08</option>
					<option value="09">09</option>
					<option value="10">10</option>
				</select> </label></td>
			</tr>
			<tr>
				<td class="blueSafety">List Attendees</td>
				<td><textarea name="attendees" cols="15" rows="3"></textarea></td>
			</tr>
			<tr>
				<td class="blueSafety">Special Needs</td>
				<td><textarea name="specialneeds" cols="15" rows="3"></textarea></td>
			</tr>
			<tr>
				<td height="30">&nbsp;</td>
				<td>
				<div align="right" class="padding"><input name="Submit"
					type="image" src="images/button_submit2.jpg" align="right"
					width="71" height="23" border="0"></div>
				</td>
			</tr>
		</table>
		</form>
		</div>
		<br>
		<p><span class="style2">Date:</span><br>
		FEB. 7, 7:45 am - 1:30 pm</p>
		<br>
		<p><span class="style2">Topics:</span><br>
		Review of the PICS Process<br>
		How to Prepare for PICS Audits<br>
		Insurance Benefits by Going Through PICS<br>
		And More...</p>
		<br>
		<br>
		<p>Please mark your calendar to attend the PICS Contractor User
		Group Meeting in Long Beach, CA. This is a great opportunity to meet
		the PICS team, review Desktop and Office Audit procedures, take part
		in a website Q&A, and provide feedback on the topics discussed.</p>
		<br>
		<br>
		<div class="Safetypics"><img src="images/hiltonpic3.jpg"
			width="100" height="125" /></div>
		<div class="Safetypics"><img src="images/hiltonpic2.jpg"
			width="100" height="125" /></div>
		<div class="Safetypics"><img src="images/hiltonpic1.jpg"
			width="100" height="125" /></div>
		<p><span class="style2">Location:</span><br>
		Holiday Inn Long Beach <br>
		Long Beach - Airport<br>
		2640 N. Lakewood Blvd. <br>
		Long Beach, CA 90815<br>
		</p>
		<p><span class="style2">We are look forward to seeing you
		in Long Beach, California.</span></p>
		</div>
		<br>
		<br>
		</td>
	</tr>
</table>
</body>
</html>
