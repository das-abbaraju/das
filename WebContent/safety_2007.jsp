<%@ page language="java" errorPage="exception_handler.jsp"%>
<%@include file="includes/userGroups.jsp" %>
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
input.invalid { background: #faa; }
input.valid { background: #fff; }
  </style>
</head>
<body>
            <table width="100%"  border="0" cellpadding="13" cellspacing="0" bgcolor="#FFFFFF">
              <tr>
                <td width="328" class="blueMain" align="center"><div align="center" class="style1"><span class="style3">MANAGING CONTRACTOR SAFETY</span><br>2007 USER GROUP MEETING
                  </div></td>
                <td bgcolor="#CBE5FE" class="blueMain"><div align="center">JULY 19 - 20, 2007</div></td>
              </tr>
            </table>
			            <table width="100%" border="0" cellspacing="0" cellpadding="0">
			  <tr>
                <td height="30"></td>
              </tr>
            </table>
            <table width="100%" border="0" cellpadding="0" cellspacing="0">
              <tr valign="top">
                <td width="627" bgcolor="#F8F8F8" class="blueHome"><div align="justify" class="blueHome">
<div class="RSVPform">

<%	if (isSubmitted)
		out.println("<span class='redMain'>Thank you for submitting your attendance information</span>");
%>
  
  <form id="safetyForm" action="safety_2007.jsp?action=rsvp" method="post">
  <table class="blueMain" bgcolor="#CBE5FE" width="100%" border="0" cellspacing="0" cellpadding="0">
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
      <td><input name="email" type="text" size="20" ></td>
    </tr>
    <tr>
      <td class="blueSafety">Organization</td>
      <td><input name="organization" type="text" size="15" required></td>
    </tr>
	  <tr>
      <td class="blueSafety">How Many People</td>
      <td><label>
       <select name="howmany" size="1">
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
        </select>
                          </label></td>
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
      <td><div align="right" class="padding">
        <input name="Submit" type="image" src="images/button_submit2.jpg" align="right" width="71" height="23" border="0">
      </div></td>
    </tr>
  </table>
</form></div>
				<br>
                  <p><span class="style2">Date:</span><br>
July 19, 8:00 am - 4:00 pm<br>
July 20, 8:00 am - 12:00 pm</p>
                  <br>
<p><span class="style2">Topics:</span><br>
How Process Safety Management applies to Contractors<br>
Performing Effective Field Audits<br>
Regulatory Updates<br>
And More...</p><br><br>
<p>Please mark your calendar to attend PICS' 2007
User Group Meeting:  Managing Contractor Safety.  This is a great
opportunity to meet the PICS team, compare safety management processes,
define contractor auditing strategies, listen to regulatory updates, network
with other organizations, collaborate on industry standards
</p><br><br>
<div class="Safetypics"><img src="images/hiltonpic3.jpg" width="100" height="125"/></div>
<div class="Safetypics"><img src="images/hiltonpic2.jpg" width="100" height="125"/></div>
<div class="Safetypics"><img src="images/hiltonpic1.jpg" width="100" height="125"/></div>
<p><span class="style2">Location:</span><br>
Hilton Waterfront Resort<br>
21100 Pacific Coast Highway<br>
Huntington Beach, CA 92648<br>
<a href="http://www.hilton.com/en/hi/groups/personalized/hubwhhf_pic/index.jhtml" target="_self">Click here to make an online reservation.</a></p>
<br>
<p><span class="style2">We are looking forward to see you in Huntington Beach, California.</span></p>
                </div><br><br></td>
              </tr>
              <tr valign="top">
                <td class="blueHome">&nbsp;</td>
              </tr>
              <tr valign="top">
                <td class="blueHome">&nbsp;</td>
              </tr>
              <tr valign="top">
                <td class="blueHome">&nbsp;</td>
              </tr>
              <tr valign="top">
                <td class="blueHome">&nbsp;</td>
              </tr>
            </table>
</body>
</html>
