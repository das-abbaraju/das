<%@ page language="java" import="java.util.ArrayList, com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<jsp:useBean id="pBean" class="com.picsauditing.PICS.PermissionsBean" scope ="page"/>
<%
	String sendTo = request.getParameter("sendTo");
	ArrayList<String> toAddresses = new ArrayList<String>();
	if (sendTo.equals("sales")) toAddresses.add("jmoreland@picsauditing.com");
	if (sendTo.equals("sales")) toAddresses.add("jsmith@picsauditing.com");
	if (sendTo.equals("billing")) toAddresses.add("jsmith@picsauditing.com");
	if (sendTo.equals("billing")) toAddresses.add("gjepsen@picsauditing.com");
	if (sendTo.equals("audits")) toAddresses.add("jcota@picsauditing.com");
	if (sendTo.equals("general")) toAddresses.add("jfazeli@picsauditing.com");
	if (sendTo.equals("tech")) toAddresses.add("jfazeli@picsauditing.com");
	if (sendTo.equals("tech")) toAddresses.add("tallred@picsauditing.com");
	if (sendTo.equals("careers")) toAddresses.add("careers@picsauditing.com");
	
	String name = request.getParameter("name");
	String email = request.getParameter("email");
	String phone = request.getParameter("phone");
	String message = "Contact about "+sendTo+". Sent to:\n";
	for(String toAddress: toAddresses)
		message = message + toAddress + "\n";
	message = message + "\nCompany: "+request.getParameter("company")+'\n'+request.getParameter("message");
//	eBean.sendContactUsEmail(name,email,sendTo,message);
	for(String toAddress: toAddresses)
		EmailBean.sendContactUsEmail(name,email,phone,toAddress,message);
%>
<html>
<head>
<title>PICS - Pacific Industrial Contractor Screening</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="PICS.css" rel="stylesheet" type="text/css">

</head>

<body bgcolor="#EEEEEE" vlink="#003366" alink="#003366" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td valign="top"><table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td height="72" bgcolor="#CC6600">&nbsp;</td>
        </tr>
    </table></td>
    <td width="657" valign="top"><table width="100%" border="0" cellpadding="0" cellspacing="0">
      <tr>
        <td valign="top"><form action="login.jsp" method="post">
          <table width="100%" border="0" cellspacing="0" cellpadding="0">
            <tr>
              <td width="146" height="218" align="center" valign="top"><a href="index.jsp"><img src="images/logo.gif" alt="Home" width="146" height="145" border="0"></a><br>
                  <table border="0" cellspacing="0" cellpadding="1">
                    <tr>
                      <td height="5"></td>
                      <td></td>
                    </tr>
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
                  </table>                </td>
              <td valign="top"><table width="511" border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td height="72"><table width="511" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td width="364"><object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,29,0" width="364" height="72">
                        <param name="movie" value="flash/NAV_CONTACT.swf">
                        <param name="quality" value="high">
                        <embed src="flash/NAV_CONTACT.swf" quality="high" pluginspage="http://www.macromedia.com/go/getflashplayer" type="application/x-shockwave-flash" width="364" height="72"></embed>
                      </object><script type="text/javascript" src="js/ieupdate.js"></script></td>
                      <td><img src="images/squares_home.gif" width="147" height="72"></td>
                    </tr>
                  </table></td>
                  </tr>
                <tr>
                  <td height="146"><img src="images/photo_contact.jpg" width="510" height="146"></td>
                  </tr>
              </table></td>
            </tr>
          </table>
        </form></td>
      </tr>
      <tr>
        <td><table width="100%" border="0" cellpadding="0" cellspacing="0">
          <tr>
            <td valign="top"><br>              <br>              <table width="100%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td valign="top" class="blueMainServices">
                      <table border="0" cellpadding="2" cellspacing="0">
                      <tr>
                          <td width="65" class="blueMain">
                          <p></p></td>
                          <td width="264" valign="top" class="blueMain"><p><span class="blueHeader">Thank you <%=name%>,</span><font color="#CC6600"><br>
    We will contact you shortly.</font><br>
    <br>
    <br>
                          </p></td>
                      </tr>
                      <tr>
                        <td class="blueMain">&nbsp;</td>
                        <td valign="top" class="blueSmall"> <strong>Pacific Industrial Contractor Screening</strong><br>
  P.O. Box 51387<br>
  Irvine, CA 92619-1387<br>
  <br>
  <b>Phone:</b> 949.387.1940<br>
  <b>Toll Free:</b> 800.506.PICS (7427)<br>
  <b>Fax:</b> 949.269.9177<br>
  <br>
  <br></td>
                      </tr>
                      </table>
                      </td>
                <td align="right" valign="top"><br>
                  <br /><br />
				  <form method="GET" name="intro" action="http://www.webconference.com/login.asp">
                    <input type="hidden" name="r" value="R239651519718247">
                    <br />
                    <table border="0" cellpadding="2" cellspacing="0">
                    <tr>
                      <td align="right" class="blueMain"><strong>&nbsp; </strong> </td>
                      <td class="blueMain"><strong>Web Conference Login</strong></td>
                    </tr>
                    <tr>
                      <td align="right" valign="middle"><img src="images/login_user.gif" alt="User Name" width="50" height="9"></td>
                      <td align="right" class="blueMain">
                        <input name="u" type="text" class="forms" id="u" size="21">                      </td>
                    </tr>
                    <tr>
                      <td align="right" valign="middle"><img src="images/login_pass.gif" alt="Password" width="50" height="9"></td>
                      <td align="right" class="blueMain">
                        <input name="p" type="text" class="forms" id="p" size="21">                      </td>
                    </tr>
                    <tr>
                      <td>&nbsp;</td>
                      <td class="blueMain"><input name="Submit" type="image" id="Submit" onMouseOver="MM_swapImage('Submit','','images/button_login_o.gif',1)" onMouseOut="MM_swapImgRestore()" src="images/button_login.jpg" width="65" height="28" border="0">
                      </td>
                    </tr>
                  </table>
                  </form></td>
              </tr>
            </table>
              <br>
              <br></td>
          </tr>
        </table></td>
      </tr>
      
    </table></td>
    <td valign="top"><table width="100%" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td height="72" bgcolor="#CC6600">&nbsp;</td>
      </tr>
    </table></td>
  </tr>
  <tr bgcolor="#003366">
    <td height="72">&nbsp;</td>
    <td height="72" align="center" valign="middle" class="footer">&copy; Copyright 2007 Pacific Industrial Contractor Screening | Site by: <a href="http://www.albumcreative.com" target="_blank" class="footer" title="Album Creative Studios">Album</a> </td>
    <td height="72" valign="top">&nbsp;</td>
  </tr>
</table>


</body>
</html>
