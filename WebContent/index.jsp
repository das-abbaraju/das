<%@ page language="java" errorPage="exception_handler.jsp"%>
<%int whichPage = 0;%>
<%
String url = request.getRequestURL().toString();
if (url.startsWith("http://pics")) {
	url = url.replaceFirst("http://pics", "http://www.pics");
	response.sendRedirect(url);
	return;
}
%>
<html>
<head>
  <title>PICS - Pacific Industrial Contractor Screening</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <link href="PICS.css" rel="stylesheet" type="text/css">
  <script language="JavaScript" SRC="js/ImageSwap.js"></script>
  <style type="text/css">
<!--
.style1 {
	font-size: 11px;
	font-weight: bold;
}
-->
  </style>
  <script src="js/AC_RunActiveContent.js" type="text/javascript"></script>
</head>

<body bgcolor="#EEEEEE" vlink="#003366" alink="#003366" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onLoad="document.login.username.focus();">	
<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td valign="top"><table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td height="72" bgcolor="#003366">&nbsp;</td>
        </tr>
    </table></td>
    <td width="657" valign="top"><table width="100%" border="0" cellpadding="0" cellspacing="0">
      <tr>
        <td valign="top"><form action="login.jsp" method="post" name="login" id="login">
          <table width="100%" border="0" cellspacing="0" cellpadding="0">
            <tr>
              <td width="146" height="218" align="center" valign="top"><img src="images/logo.gif" alt="Pics Logo" width="146" height="145"><br>
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
                  </table>
                  </td>
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
                  <td height="146"><script type="text/javascript">
AC_FL_RunContent( 'codebase','http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,29,0','width','511','height','146','src','flash/home_feature','quality','high','pluginspage','http://www.macromedia.com/go/getflashplayer','movie','flash/home_feature' ); //end AC code
</script><noscript><object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,29,0" width="511" height="146">
                    <param name="movie" value="flash/home_feature.swf">
                    <param name="quality" value="high">
                    <embed src="flash/home_feature.swf" quality="high" pluginspage="http://www.macromedia.com/go/getflashplayer" type="application/x-shockwave-flash" width="511" height="146"></embed>
                  </object></noscript><script type="text/javascript" src="js/ieupdate.js"></script></td>
                  </tr>
              </table></td>
            </tr>
          </table>
        </form></td>
      </tr>
      <tr>
        <td><br>
        <table width="100%" border="0" cellpadding="0" cellspacing="0">
          <tr>
            <td width="200" valign="top"><table width="100%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td><table border="0" cellspacing="0" cellpadding="0">
                      <tr>
                        <td><script type="text/javascript">
AC_FL_RunContent( 'codebase','http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=7,0,19,0','width','145','height','130','src','flash/client_logoflash','quality','high','pluginspage','http://www.macromedia.com/go/getflashplayer','movie','flash/client_logoflash' ); //end AC code
</script><noscript><object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=7,0,19,0" width="145" height="130">
                          <param name="movie" value="flash/client_logoflash.swf">
                          <param name="quality" value="high">
                          <embed src="flash/client_logoflash.swf" quality="high" pluginspage="http://www.macromedia.com/go/getflashplayer" type="application/x-shockwave-flash" width="145" height="130"></embed>
                        </object></noscript></td>
                        <td class="blueHome">PICS was established to assist companies with a thorough audit program for all contractors working at their facilities. PICS 



sets the highest standards possible in regard to contractor qualifications as they relate to federally or state-mandated regulations and/or specific operator requirements.</td>
                      </tr>
                    </table>
                    </td>
                </tr>
                <tr>
                  <td><br>
                    <table border="0" cellpadding="0" cellspacing="0">
                      <tr>
                        <td><img src="images/feature_operator.jpg" alt="Featured Operator" width="215" height="195" border="0" usemap="#Map"></td>
                        <td width="13"><img src="images/spacer.gif" width="13" height="1"></td>
                        <td><img src="images/feature_contractor.jpg" alt="Featured Contractor" width="215" height="195" border="0" usemap="#Map2"></td>
                        <td width="13"><img src="images/spacer.gif" width="1" height="1"></td>
                      </tr>
                      </table>
                  </td></tr>
                </table></td>
              <td valign="top">              <table width="200" border="0" align="right" cellpadding="0" cellspacing="0">
                <tr>
                  <td colspan="3"><img src="images/header_homeNews.jpg" alt="News" width="200" height="24"></td>
                  </tr>
                <tr>
                  <td bgcolor="#CCCCCC"><img src="images/spacer.gif" width="1" height="1"></td>
                  <td valign="top" bgcolor="#FFFFFF"><table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
					                                        <tr>
                                          <td valign="top" bgcolor="F8F8F8" class="homeNews"><span class="style1"><span class="homeNewsDates">12/01/07</span>Suncor Energy USA joins PICS.</span><br>
                                            <br> 
                                          Suncor Energy USA operates a 90,000 barrel per day oil refinery in Commerce
City, Colorado.    Suncor Energy has expanded both production and refining
capacity recently, and markets its refined product through Sunoco and the
Phillips 66 branded stations.</td></tr>
                                        <tr>
                                          <td valign="top" class="homeNews"><span class="style1"><span class="homeNewsDates">12/01/07</span>BP Toledo joins PICS.</span><br>
                                            <br> 
                                          BP Toledo refinery joins many other BP refineries and business groups who
use PICS to supply an integrated, standardized solution to prequalify BP
contractors.  The Toledo facility can process 160,000 barrels per day, and
BP's strong presence in Ohio has made BP the number one fuel retailer in the
state.</td>
                                        </tr>
					<tr>
					<tr>
                                          <td valign="top" bgcolor="F8F8F8" class="homeNews"><span class="style1"><span class="homeNewsDates">11/01/07</span>Huntsman International partners with PICS</span><br>
                                            <br> 
                                          Huntsman Chemical partners with PICS to prequalify contractors at all 14
production facilities throughout the US.  Huntsman has over 13,000 employees
worldwide and has grown to revenues of over $13 billion in just 35 years;
thousands of contractors perform various services on site each year.</td></tr>
                                        <tr>
                      <td align="center" class="blueHome">&nbsp;<a href="featured_newsarchive.jsp" target="_self"><img src="images/NEWSARCHIVE_button3.gif" width="111" height="27" hspace="5" border="0"></a></td>
                    </tr>
                    </table></td>
                  <td bgcolor="#CCCCCC"><img src="images/spacer.gif" width="1" height="1"></td>
                </tr>
                <tr>
                  <td height="7" colspan="3"><img src="images/footer_homeNews.gif" width="200" height="7"></td>
                  </tr>
                </table></td>
          </tr>
        </table>
        <br>
        <br></td>
      </tr>
      
    </table></td>
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


<map name="Map"><area shape="rect" coords="5,20,209,191" href="featured_template.jsp" target="_self">
</map>
<map name="Map2">
  <area shape="rect" coords="0,22,216,195" href="featured_contractor.jsp" target="_self">
</map></body>
</html>
