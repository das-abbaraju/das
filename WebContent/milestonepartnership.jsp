<%@ page language="java" errorPage="exception_handler.jsp"%>
<%@ page language="java" import="PICS.*;"%>
<%
  	int whichPage = 0;
	boolean isSubmitted = (null != request.getParameter("action") 
			&& request.getParameter("action").equals("rsvp"));

	if (isSubmitted){
		EmailBean.sendSafetyMeetingEmail(request);
	}
	
%>
<html>
<head>
  <title>PICS - Pacific Industrial Contractor Screening</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <link href="PICS.css" rel="stylesheet" type="text/css">
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
input.invalid { background: #faa; }
input.valid { background: #fff; }
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

<body bgcolor="#EEEEEE" vlink="#003366" alink="#003366" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td valign="top"><table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td height="72" bgcolor="#003366">&nbsp;</td>
        </tr>
    </table></td>
    <td width="657" valign="top"><table width="100%" border="0" cellpadding="0" cellspacing="0">
      <tr>
        <td valign="top"><form action="login.jsp" method="post">
          <table width="100%" border="0" cellspacing="0" cellpadding="0">
            <tr>
              <td width="146" height="218" align="center" valign="top"><a href="index.jsp"><img src="images/logo.gif" alt="Pics Logo" width="146" height="145" border="0"></a><br>
                <table border="0" cellspacing="0" cellpadding="1">
                    <tr><td height="5"></td><td></td></tr>
				    <tr>
                      <td align="right" valign="middle"><p><img src="images/login_user.gif" alt="User Name" width="50" height="9">&nbsp;</p></td>
                      <td valign="middle"><p>
                          <input name="username" type="text" class="loginForms" size="9">
                      </p></td>
                    </tr>
                    <tr>
                      <td align="right" valign="middle"><img src="images/login_pass.gif" alt="Password" width="50" height="9">&nbsp;</td>
                      <td valign="middle"><p>
                          <input name="password" type="password" class="loginForms" size="9">
                      </p></td>
                    </tr>
                    <tr>
                      <td>&nbsp;</td>
                      <td>
                              <input name="Submit" type="image" src="images/button_login.jpg" width="65" height="28" border="0">
                      </td>
                    </tr>
                    <tr>
                      <td colspan="2" class="blueMain"></td>
                    </tr>
                  </table>                  </td>
              <td valign="top"><table width="511" border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td height="72"><table width="511" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td width="364" height="72"><script type="text/javascript">
AC_FL_RunContent( 'codebase','http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,29,0','width','364','height','72','src','flash/NAV_HOME','quality','high','pluginspage','http://www.macromedia.com/go/getflashplayer','movie','flash/NAV_HOME' ); //end AC code
</script><noscript><object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,29,0" width="364" height="72">
                        <param name="movie" value="flash/NAV_HOME.swf">
                        <param name="quality" value="high">
                        <embed src="flash/NAV_HOME.swf" quality="high" pluginspage="http://www.macromedia.com/go/getflashplayer" type="application/x-shockwave-flash" width="364" height="72"></embed>
                      </object></noscript><script type="text/javascript" src="js/ieupdate.js"></script></td>
                      <td><img src="images/squares_home.gif" width="147" height="72"></td>
                    </tr>
                  </table></td>
                  </tr>
                <tr>
                  <td height="146"><script type="text/javascript" src="js/ieupdate.js"></script>
                    <img src="images/photo_partner.jpg" width="510" height="146"></td>
                  </tr>
              </table></td>
            </tr>
          </table>
        </form></td>
      </tr>
      <tr>
        <td>
            <br>
            <table width="100%"  border="0" cellpadding="13" cellspacing="0" bgcolor="#FFFFFF">
              <tr>
                <td width="275" align="center"><img src="images/MilestoneLogo.gif" width="228" height="76"></td>
                <td bgcolor="#BB9C6D" class="blueMain">
                <div align="center" class="style3">USE YOUR PICS MEMBERSHIP TO CREATE<BR>
ADDED INSURANCE BENEFITS</div></td>
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
				<div class="Milestoneform">

<%	if (isSubmitted)
		out.println("<span class='redMain'>Thank you for submitting your attendance information</span>");
%>
  
  <form id="safetyForm" action="safety_2007.jsp?action=rsvp" method="post">
  <table class="blueMain" bgcolor="#CBE5FE" width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
      <td height="70" colspan="2" class="blueSafety2">Yes, I am interested to see if I qualify for the PICS Preferred Contractor Rate Program.  Please have a Milestone Representative contact me.</td>
      </tr>
	<tr>
      <td height="30" class="blueSafety"><div align="right">First Name</div></td>
      <td><input name="firstname" type="text" size="24" maxlength="30" required></td>
    </tr>
		<tr>
      <td height="30" class="blueSafety"><div align="right">Last Name</div></td>
      <td><input name="lastname" type="text" size="24" maxlength="30" required></td>
    </tr>
	    <tr>
      <td height="30" class="blueSafety"><div align="right">Company</div></td>
      <td><input name="company" type="text" size="24" maxlength="30" required></td>
    </tr>
	    <tr>
      <td height="30" class="blueSafety"><div align="right">Title</div></td>
      <td><input name="title" type="text" size="24" maxlength="30" required></td>
    </tr>
    <tr>
      <td height="30" class="blueSafety"><div align="right">Phone</div></td>
      <td><input name="phone" type="text" size="24" maxlength="30" required>    
      </tr>	
		<tr>
      <td height="30" class="blueSafety"><div align="right">Email</div></td>
      <td><input name="email" type="text" size="24" maxlength="30" ></td>
    </tr>
    <tr>
      <td height="40">&nbsp;</td>
      <td><div align="right" class="padding">
        <input name="Submit" type="image" class="padding2" src="images/button_submit2.jpg" align="right" width="71" height="23" border="0">
      </div></td>
    </tr>
  </table>
</form></div>
                  <p>We are pleased to announce our partnership with <a href="http://www.milestonepromise.com/">Milestone Risk Management & Insurance Services</a>.  With a unique approach to insurance, Milestone provides highly consultative services in areas of claims management and risk control.</p>
<p>PICS and Milestone have teamed up to offer the “Preferred Contractor Rate Program” for contractors who qualify.  Milestone understands what it takes to successfully complete the PICS auditing process, which is why they are offering PICS Contractors special incentives, including:</p> 
<ul><li>Preferred PICS Contractor rates.  By leveraging your business practices in areas of safety and compliance, Milestone has access to reduced workers compensation and liability rates.</li>
<li>No cost risk management and insurance gap analysis.</li>  
<li>Waived PICS fees.  If you engage in services with Milestone, they will cover your PICS fees at your next renewal.  They will continue to cover your fees as long as you are with them.</li></ul>
Through partnering with companies like Milestone, PICS is able to provide its contractors even more values and benefits.
</p></div>
<div><img src="images/Milestone_casestudy.jpg" width="628" height="146" border="0" usemap="#Map"></div>
<BR><BR>			
</td>
              </tr>
              <tr valign="top">
                <td class="blueHome">&nbsp;</td>
              </tr>
              <tr valign="top">
                <td class="blueHome">&nbsp;</td>
              </tr>
            </table>
            <p><br>
        </p></div></td>
      </tr>
      
    </table>
    <br>
    <br></td>
    <td valign="top"><table width="100%" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td height="72" bgcolor="#003366">&nbsp;</td>
      </tr>
    </table></td>
  </tr>
  <tr bgcolor="#003366">
    <td height="72">&nbsp;</td>
    <td height="72" align="center" valign="middle" class="footer">&copy; Copyright 2007 Pacific Industrial Contractor Screening | Site by: <a href="http://www.albumcreative.com" target="_blank" class="footer" title="Album Creative Studios">Album</a> </td>
    <td height="72" valign="top">&nbsp;</td>
  </tr>
</table>

<%@ include file="includes/statcounter.jsp"%>


<map name="Map">
<area shape="rect" coords="485,93,619,136" href="images/ContractorCase Study.pdf">
</map></body>
</html>
