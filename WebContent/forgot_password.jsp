<%@ page language="java" errorPage="exception_handler.jsp"%>
<%//@ page language="java"%>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>

<%	String email= request.getParameter("email");
	aBean.sendPasswordEmail(email);
%>
<html>
<head>
<title>PICS - Pacific Industrial Contractor Screening</title>
  <META Http-Equiv="Cache-Control" Content="no-cache">
  <META Http-Equiv="Pragma" Content="no-cache">
  <META Http-Equiv="Expires" Content="0">
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <link href="PICS.css" rel="stylesheet" type="text/css">
</head>
<body bgcolor="#EEEEEE" vlink="#003366" alink="#003366" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onLoad="document.form1.email.focus();">
<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td valign="top"><table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td height="72" bgcolor="#993300">&nbsp;</td>
        </tr>
    </table></td>
    <td width="657" valign="top"><table width="100%" border="0" cellpadding="0" cellspacing="0">
      <tr>
        <td valign="top">
          <table width="100%" border="0" cellspacing="0" cellpadding="0">
            <tr>
              <td width="146" height="218" align="center" valign="top"><a href="index.jsp"><img src="images/logo.gif" alt="Home" width="146" height="145" border="0"></a><br>
                </td>
              <td valign="top"><table width="511" border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td height="72"><table width="511" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td width="364"><object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,29,0" width="364" height="72">
                        <param name="movie" value="flash/NAV_LOGIN.swf">
                        <param name="quality" value="high">
                        <embed src="flash/NAV_LOGIN.swf" quality="high" pluginspage="http://www.macromedia.com/go/getflashplayer" type="application/x-shockwave-flash" width="364" height="72"></embed>
                      </object></td>
                      <td><img src="images/squaresContractors_rightNav.gif" width="147" height="72"></td>
                    </tr>
                  </table></td>
                  </tr>
                <tr>
                  <td height="146"><img src="images/photo_login.jpg" width="510" height="146"></td>
                </tr>
              </table></td>
            </tr>
          </table>
        </td>
      </tr>
      <tr>
        <td><br>
		  <table width="657" cellpadding="0" cellspacing="0">
            <tr>
              <td width="145" class="blueMain"> <br>
              </td>
              <td align="center" valign="top" class="blueMain">
                <form name="form1" method="post" action="forgot_password.jsp">
                  <table width="0" border="0" cellspacing="0" cellpadding="1">
                    <tr class="blueMain">
                      <td colspan="2" class="blueHeader">Forget your password?</td>
                    </tr>
                    <tr>
                      <td colspan="2" class="blueMain"> Enter the email address that you submitted when you created your PICS company profile and we will email you your username and password. If you have any problems, <a href="contact.jsp" title="Contact PICS">contact us</a> directly.</td>
                    </tr>
          <td colspan="2" class="redMain"><br>
                  <%	if (request.getParameter("email") != null)
	  out.println(aBean.getErrorMessages());
%>
            </td>
          </tr>
          <tr>
            <td class="redMain" align="right">Email address&nbsp;</td>
            <td><input name="email" type="text" class="forms" size="25">
                <input name="submit" type="submit" class="forms" value="Send Password"></td>
          </tr>
                  </table>
                </form>
                <br>
                <a href="login.jsp">Return to Login Page</a>
              </td>
              <td width="126" class="blueMain"> </td>
            </tr>
          </table>  
		  <br>
		  <br>		  </td>
      </tr>
      
    </table></td>
    <td valign="top"><table width="100%" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td height="72" bgcolor="#993300">&nbsp;</td>
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