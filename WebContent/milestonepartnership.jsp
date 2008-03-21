<%@ page language="java" errorPage="exception_handler.jsp"%>
<%@page import="com.picsauditing.PICS.EmailBean;"%>
<%
	boolean isSubmitted = (null != request.getParameter("action") && request.getParameter("action").equals(
			"rsvp"));
	if (isSubmitted) {
		String to = "bescoubas@milestonepromise.com,jjacobs@milestonepromise.com,srahnama@milestonepromise.com,jsmith@picsauditing.com,jmoreland@picsauditing.com";
		String cc = "";
		String from = request.getParameter("email");
		String subject = "PICS Milestone referral";

		StringBuffer message = new StringBuffer();
		message.append("Info:\n");
		message.append("  First Name: ").append(request.getParameter("firstname")).append("\n");
		message.append("  Last Name:  ").append(request.getParameter("lastname")).append("\n");
		message.append("  Company:    ").append(request.getParameter("company")).append("\n");
		message.append("  Title:      ").append(request.getParameter("title")).append("\n");
		message.append("  Phone:      ").append(request.getParameter("phone")).append("\n");
		message.append("  Email:      ").append(request.getParameter("email")).append("\n");

		EmailBean.sendEmails(from, to, cc, subject, message.toString());

		to = request.getParameter("email");
		from = "info@picsauditing.com";
		subject = "Confirmation of request for contact";
		message = new StringBuffer();
		message.append("Hi ").append(request.getParameter("firstname")).append("\n\n");
		message.append("Thanks for requesting more information on the PICS Preferred Contractor Rate Program. ");
		message.append("A representatve from Milestone will contact you shortly. ");
		message.append("Additionally, if you would like to contact Milestone directly, please give them a call at (949)852-0909.");
		message.append("\n\nThanks,\nPICS");

		EmailBean.sendEmails(from, to, cc, subject, message.toString());
	}
%>
<html>
<head>
<title>Milestone</title>
<meta name="color" content="#003366" />
<meta name="flashName" content="HOME" />
<meta name="iconName" content="partner" />
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
	color: #FFFFFF;
}

-->
input.invalid {
	background: #faa;
}

input.valid {
	background: #fff;
}

.Milestoneform {
	width: 300px;
	float: right;
	padding-top: 0px;
	padding-right: 0px;
	padding-bottom: 0px;
	padding-left: 10px;
}

.testimonial {
	width: 300px;
	float: right;
	padding-top: 20px;
	padding-right: 0px;
	padding-bottom: 0px;
	padding-left: 0px;
}

.blueSafety2 {
	padding-top: 0px;
	padding-right: 0px;
	padding-bottom: 0px;
	padding-left: 10px;
	font-family: Verdana, Arial, Helvetica, sans-serif;
	font-size: 11px;
	line-height: 16px;
	color: #003366;
}

.padding2 {
	padding-right: 25px;
	padding-top: 0px;
	padding-bottom: 0px;
	padding-left: 0px;
	vertical-align: middle;
}
</style>
</head>
<body>
<table width="100%" border="0" cellpadding="13" cellspacing="0"
	bgcolor="#FFFFFF">
	<tr>
		<td width="275" align="center"><img
			src="images/MilestoneLogo.gif" width="228" height="76"></td>
		<td bgcolor="#BB9C6D" class="blueMain">
		<div align="center" class="style3">USE YOUR PICS MEMBERSHIP TO
		CREATE<BR>
		ADDED INSURANCE BENEFITS</div>
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
		<div class="Milestoneform">
		<%
			if (isSubmitted)
				out.println("<span class='redMain'>Thank you for submitting your contact information</span>");
		%>

		<form id="safetyForm" action="milestonepartnership.jsp?action=rsvp"
			method="post">
		<table class="blueMain" bgcolor="#CBE5FE" width="100%" border="0"
			cellspacing="0" cellpadding="0">
			<tr>
				<td height="70" colspan="2" class="blueSafety2">Yes, I am
				interested to see if I qualify for the PICS Preferred Contractor
				Rate Program. Please have a Milestone Representative contact me.</td>
			</tr>
			<tr>
				<td height="30" class="blueSafety">
				<div align="right">First Name</div>
				</td>
				<td><input name="firstname" type="text" size="24"
					maxlength="30" required></td>
			</tr>
			<tr>
				<td height="30" class="blueSafety">
				<div align="right">Last Name</div>
				</td>
				<td><input name="lastname" type="text" size="24" maxlength="30"
					required></td>
			</tr>
			<tr>
				<td height="30" class="blueSafety">
				<div align="right">Company</div>
				</td>
				<td><input name="company" type="text" size="24" maxlength="30"
					required></td>
			</tr>
			<tr>
				<td height="30" class="blueSafety">
				<div align="right">Title</div>
				</td>
				<td><input name="title" type="text" size="24" maxlength="30"
					required></td>
			</tr>
			<tr>
				<td height="30" class="blueSafety">
				<div align="right">Phone</div>
				</td>
				<td><input name="phone" type="text" size="24" maxlength="30"
					required>
			</tr>
			<tr>
				<td height="30" class="blueSafety">
				<div align="right">Email</div>
				</td>
				<td><input name="email" type="text" size="24" maxlength="30"></td>
			</tr>
			<tr>
				<td height="40">&nbsp;</td>
				<td>
				<div align="right" class="padding"><input name="Submit"
					type="image" class="padding2" src="images/button_submit2.jpg"
					align="right" width="71" height="23" border="0"></div>
				</td>
			</tr>
		</table>
		</form>
		</div>
		<p>We are pleased to announce our partnership with <a
			href="http://www.milestonepromise.com/">Milestone Risk Management
		& Insurance Services</a>. With a unique approach to insurance, Milestone
		provides highly consultative services in areas of claims management
		and risk control.</p>
		<p>PICS and Milestone have teamed up to offer the “Preferred
		Contractor Rate Program” for contractors who qualify. Milestone
		understands what it takes to successfully complete the PICS auditing
		process, which is why they are offering PICS Contractors special
		incentives, including:</p>
		<ul>
			<li>Preferred PICS Contractor rates. By leveraging your business
			practices in areas of safety and compliance, Milestone has access to
			reduced workers compensation and liability rates.</li>
			<li>No cost risk management and insurance gap analysis.</li>
			<li>Waived PICS fees. If you engage in services with Milestone,
			they will cover your PICS fees at your next renewal. They will
			continue to cover your fees as long as you are with them.</li>
		</ul>
		<p>
		Through partnering with companies like Milestone, PICS is able to
		provide its contractors even more values and benefits.
		</p>
		</div>
		<div><img src="images/Milestone_casestudy.jpg" width="628"
			height="146" border="0" usemap="#Map"></div>
		<BR>
		<BR>
		</td>
	</tr>
	<tr valign="top">
		<td class="blueHome">&nbsp;</td>
	</tr>
	<tr valign="top">
		<td class="blueHome">&nbsp;</td>
	</tr>
</table>

<map name="Map">
	<area shape="rect" coords="485,93,619,136"
		href="images/ContractorCase Study.pdf">
</map>
</body>
</html>
