<%@ page language="java" errorPage="exception_handler.jsp"%>
<%//@ page language="java"%>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>

<%	String email= request.getParameter("email");
	aBean.sendPasswordEmail(email);
%>
<html>
<head>
  <title>PICS - Pacific Industrial Contractor Screening</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <link href="PICS.css" rel="stylesheet" type="text/css">
</head>

<body bgcolor="#EEEEEE" background="images/watermark.gif" vlink="#003366" alink="#003366" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td valign="top">
	<table width="100%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td width="50%" bgcolor="#993300">&nbsp;</td>
          <td width="146" rowspan="2" valign="top"><a href="index.jsp"><img src="images/logo.gif" alt="HOME" width="146" height="145" border="0"></a></td>
          <td width="364">
		    <object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,29,0" width="364" height="72">
            <param name="movie" value="flash/NAV_CONTRACTORS.swf">
            <param name="quality" value="high">
            <embed src="NAV_CONTRACTORS" quality="high" pluginspage="http://www.macromedia.com/go/getflashplayer" type="application/x-shockwave-flash" width="364" height="72"></embed></object>
          </td>
          <td width="147"><img src="images/squaresContractors_rightNav.gif" width="147" height="72"></td>
          <td width="50%" bgcolor="#993300">&nbsp;</td>
        </tr>
        <tr> 
          <td>&nbsp;</td>
          <td valign="top"><img src="images/squaresLogin_underNav.gif" width="145" height="73"></td>
          <td valign="top"><img src="images/spacer.gif" width="1" height="1"><img src="images/photo2.jpg" width="145" height="146" vspace="1"></td>
          <td>&nbsp;</td>
        </tr>
        <tr> 
          <td>&nbsp;</td>
            <td colspan="3">
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
                        <td colspan="2" class="blueMain"> Enter the email address 
                          that you submitted when you created your PICS company 
                          profile and we will email you your username and password. 
                          If you have any problems, <a href="contact.htm" title="Contact PICS">contact 
                          us</a> directly.</td>
                      </tr>
                        <td colspan="2" class="redMain"><br> 
<%	if (request.getParameter("email") != null)
	  out.println(aBean.getErrorMessages());
%>						</td>
                      </tr>
                      <tr> 
                        <td class="redMain" align="right">Email address&nbsp;</td>
                        <td><input name="email" type="text" class="forms" size="25">
                        <input name="submit" type="submit" class="forms" value="submit"></td>
                      </tr>
                    </table>
					</form>
                    <br>
                  </td>
                  <td width="126" class="blueMain"> </td>
                </tr>
              </table></td>
          <td>&nbsp;</td>
        </tr>
      </table>
      <br>
      <br>
    </td>
  </tr>
  <tr>
    <td height="72" align="center" bgcolor="#003366" class="copyrightInfo">&copy;2007 
      Pacific Industrial Contractor Screening | site design: <a href="http://www.albumcreative.com" title="Album Creative Studios"><font color="#336699">ACS</font></a></td>
  </tr>
</table>
</body>
</html>
