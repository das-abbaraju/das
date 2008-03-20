<html>
<head>
<title>Contact</title>
<meta name="color" content="#CC6600" />
<meta name="flashName" content="CONTACT" />
<meta name="iconName" content="contact" />
</head>
<body>
<table width="100%" border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td valign="top" class="blueMainServices">
		<form action="confirmContact.jsp" method="post" name="emailForm"
			id="emailForm">
		<table border="0" cellpadding="2" cellspacing="0">
			<tr>
				<td width="65" class="blueMain">
				<p></p>
				</td>
				<td valign="top" class="blueSmall"><strong>Mailing
				Address </strong><br>
				P.O. Box 51387<br>
				Irvine, CA 92619-1387<br>
				<br>
				<strong>Office Location </strong><br>
				17701 Cowan Suite 140<br>
				Irvine, CA 92614<br>
				<br>
				<b>Phone:</b> 949.387.1940<br>
				<b>Toll Free:</b> 800.506.PICS (7427)<br>
				<b>Fax:</b> 949.269.9177<br>
				<br>
				</td>
			</tr>
			<tr>
				<td align="right" class="blueMain" valign="top">Send to</td>
				<td class="blueMain"><label><input name="sendTo"
					type="radio" value="general" checked>General Inquiries</label> <label><input
					name="sendTo" type="radio" value="sales">Sales</label> <label><input
					name="sendTo" type="radio" value="billing">Billing</label><br />
				<label><input name="sendTo" type="radio" value="audits">Audits</label>
				<label><input name="sendTo" type="radio" value="tech">Tech
				Support</label> <label><input name="sendTo" type="radio"
					value="careers">Careers</label></td>
			</tr>
			<tr>
				<td align="right" class="blueMain">Name</td>
				<td class="blueMain"><input name="name" type="text"
					class="forms" id="name" size="25"></td>
			</tr>
			<tr>
				<td align="right" class="blueMain">Telephone</td>
				<td class="blueMain"><input name="phone" type="text"
					class="forms" id="phone" size="25"></td>
			</tr>
			<tr>
				<td align="right" class="blueMain">Email</td>
				<td class="blueMain"><input name="email" type="text"
					class="forms" id="email" size="25"></td>
			</tr>
			<tr>
				<td align="right" class="blueMain">Company</td>
				<td class="blueMain"><input name="company" type="text"
					class="forms" id="company" size="25"></td>
			</tr>
			<tr>
				<td align="right" valign="top" class="blueMain">Message</td>
				<td class="blueMain"><textarea name="message" cols="38"
					rows="3" class="forms" id="message"></textarea></td>
			</tr>
			<tr>
				<td class="blueMain">&nbsp;</td>
				<td class="blueMain"><input name="imageField" type="image"
					src="images/button_submit.jpg" width="73" height="27" border="0" /></td>
			</tr>
		</table>
		</form>
		</td>
		<td align="left" valign="top">
		<ul>
			<%@ include file="/includes/webexlivesupport.jsp"%>
			<br /><br /><br /><br />
			<a href="career_opportunity.jsp" class="blueMain">Career Opportunities at PICS</a>
		</ul>
		</td>
	</tr>
</table>
</body>
</html>
