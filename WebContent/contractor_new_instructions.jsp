<%//@ page language="java" errorPage="exception_handler.jsp"%>
<%@ page language="java"%>
<%
	int whichPage = 1;
	boolean submitted = (null != request.getParameter("submit.x"));
	if (submitted) {
		response.sendRedirect("contractor_new.jsp");
		return;
	} // if
%>

<html>
<head>
  <title>PICS - Pacific Industrial Contractor Screening</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <link href="PICS.css" rel="stylesheet" type="text/css">
  <script language="JavaScript" SRC="js/ImageSwap.js"></script>
</head>

<body bgcolor="#EEEEEE" vlink="#003366" alink="#003366" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td valign="top"><table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td height="72" bgcolor="#669966">&nbsp;</td>
        </tr>
    </table></td>
    <td width="657" valign="top"><table width="100%" border="0" cellpadding="0" cellspacing="0">
      <tr>
        <td valign="top"><form action="login.jsp" method="post">
          <table width="100%" border="0" cellspacing="0" cellpadding="0">
            <tr>
              <td width="146" height="218" align="center" valign="top"><a href="index.jsp"><img src="images/logo.gif" alt="Home" width="146" height="145" border="0"></a><br>
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
                      <td width="364"><object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,29,0" width="364" height="72">
                        <param name="movie" value="flash/NAV_REGISTER.swf">
                        <param name="quality" value="high">
                        <embed src="flash/NAV_REGISTER.swf" quality="high" pluginspage="http://www.macromedia.com/go/getflashplayer" type="application/x-shockwave-flash" width="364" height="72"></embed>
                      </object><script type="text/javascript" src="js/ieupdate.js"></script></td>
                      <td><img src="images/squares_home.gif" width="147" height="72"></td>
                    </tr>
                  </table></td>
                  </tr>
                <tr>
                  <td height="146"><img src="images/photo_register.jpg" width="510" height="146"></td>
                </tr>
              </table></td>
            </tr>
          </table>
        </form></td>
      </tr>
      <tr>
        <td><br>
		<form name="form1" method="post" action="contractor_new_instructions.jsp">
        <table width="520" align="center" cellpadding="0" cellspacing="0">
                <tr> 
                  <td valign="top" class="blueMain"> <p align="justify" class="redMain">If 
                      you're an operator and are interested in using our services, 
                      please <a href="contact.jsp" class="redMain">contact us</a> 
                      directly.</p>
                    <table border="0" cellpadding="0" cellspacing="0" class="blueMain">
                      <tr>
                        <td width="245" valign="top" class="blueMainServices">To create a contractor account, you will choose a username and password with which you will log into PICS in the future. You will also be required to enter contact information for your company.</td>
                        <td width="25">&nbsp;</td>
                        <td>By creating a profile, you agree to be bound by our <a href="forms/form2.pdf" title="Terms of use" target="_blank">contractor's agreement</a>. <br>
                          If you agree 
and would like to create an account, click the continue button below.</td>
                      </tr>
                    </table>                   
                    <br>
                    <p align="center"> 
                      <input type="image" name="submit" id="submit" value="Continue" src="images/button_continue.jpg" width="84" height="27" border="0">
                  </p></td>
                </tr>
              </table></form><br><br>
			  </td>
      </tr>
      
    </table></td>
    <td valign="top"><table width="100%" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td height="72" bgcolor="#669966">&nbsp;</td>
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
</body>
</html>
