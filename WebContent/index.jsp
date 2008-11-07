<%@ page language="java" errorPage="exception_handler.jsp"%>
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
<title>Contractor Screening &amp; Contractor Management</title>
<meta name="color" content="#003366" />
<meta name="flashName" content="HOME" />

  <script SRC="js/ImageSwap.js" language="JavaScript"></script>
  <script src="js/AC_RunActiveContent.js" type="text/javascript"></script>
  <style type="text/css">
<!--
.style1 {
	font-size: 11px;
	font-weight: bold;
}
-->
  </style>
</head>
<body>
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
              <td valign="top"><table width="200" border="0" align="right" cellpadding="0" cellspacing="0">
                <tr>
                  <td colspan="3"><img src="images/header_homeNews.jpg" alt="News" width="200" height="24"></td>
                  </tr>
                <tr>
                  <td bgcolor="#CCCCCC"><img src="images/spacer.gif" width="1" height="1"></td>
                  <td valign="top" bgcolor="#FFFFFF">
                  	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
				  		<tr>
                        	<td valign="top" bgcolor="F8F8F8" class="homeNews"><span class="style1"><span class="homeNewsDates">09/01/08</span>PICS EHS/VPP Resident Expert</span><br>
                            <br> 
								Dennis Truitt joins PICS with more than 20 years of managing health and safety in the highly-regulated petrochemical industry and holds a B.A. in Psychology from the University of California, Irvine. Prior to joining PICS, he was the Refinery Safety Manager for the Valero Wilmington Refinery.</td>
                        </tr>
                        <tr>
                        	<td valign="top" class="homeNews"><span class="style1"><span class="homeNewsDates">09/01/08</span>Square D / Schneider Electric joins PICS</span><br>
                            <br> 
                            	Square D  is a market-leading global brand of Schneider Electric for NEMA type electrical distribution and industrial control products, systems and services.  Schneider Electric, headquartered in Paris, France, is a global electrical industry leader with sales of approximately $23.7 billion (U.S.) in 2007.</td></tr>
						<tr>
                        	<td valign="top" bgcolor="F8F8F8" class="homeNews"><span class="style1"><span class="homeNewsDates">08/01/08</span>New Belgium Brewing joins PICS</span><br>
                            <br> 
                            	New Belgium is the nation's third largest mid-sized brewery. From its inception New Belgium has been committed to the preservation of the environment, brewing world class beer, promoting beer culture and having fun.  In 1998 New Belgium became the first wind-powered brewery in the United States.</td></tr>
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

<!-- document.login.username.focus()  -->

<map name="Map"><area shape="rect" coords="5,20,209,191" href="featured_template.jsp" target="_self">
</map>
<map name="Map2">
  <area shape="rect" coords="0,22,216,195" href="featured_contractor.jsp" target="_self">
</map></body>
</html>
