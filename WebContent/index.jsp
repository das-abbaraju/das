<%@ page language="java" errorPage="exception_handler.jsp"%>
<%@page import="com.picsauditing.util.URLUtils"%>
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
AC_FL_RunContent( 'codebase','<%= URLUtils.getProtocol( request ) %>://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=7,0,19,0','width','145','height','130','src','flash/client_logoflash','quality','high','pluginspage','<%= URLUtils.getProtocol( request ) %>://www.macromedia.com/go/getflashplayer','movie','flash/client_logoflash' ); //end AC code
</script><noscript><object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="<%= URLUtils.getProtocol( request ) %>://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=7,0,19,0" width="145" height="130">
                          <param name="movie" value="flash/client_logoflash.swf">
                          <param name="quality" value="high">
                          <embed src="flash/client_logoflash.swf" quality="high" pluginspage="<%= URLUtils.getProtocol( request ) %>://www.macromedia.com/go/getflashplayer" type="application/x-shockwave-flash" width="145" height="130"></embed>
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
                        <td><img src="images/featured_op_Huntsman.jpg" alt="Featured Operator Huntsman" width="215" height="195" border="0" usemap="#Map"></td>
                        <td width="13"><img src="images/spacer.gif" width="13" height="1"></td>
                        <td><img src="images/feature_contractor.jpg" alt="Featured Contractor Bigge" width="215" height="195" border="0" usemap="#Map2"></td>
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
                        	<td valign="top" class="homeNews"><span class="style1"><span class="homeNewsDates">12/01/08</span>BASF Joins PICS</span><br>
                            <br> 
                            	BASF is the world's leading chemical company. Their portfolio ranges from oil and gas to chemicals, plastics, performance products, agricultural products and fine chemicals. As a reliable partner BASF helps its customers in virtually all industries to be more successful. With its high-value products and intelligent solutions, BASF plays an important role in finding answers to global challenges such as climate protection, energy efficiency, nutrition and mobility. BASF has more than 95,000 employees and posted sales of almost &euro;58 billion in 2007.</td></tr>
						<tr>
				  		<tr>
                        	<td valign="top" bgcolor="F8F8F8" class="homeNews"><span class="style1"><span class="homeNewsDates">09/23/08</span>2008 NSC Annual Congress &amp; Expo</span><br>
                            <br> 
								The National Safety Council, founded in 1913, serves as the nation's leading resource on industry trends, professional development, and strategies for advancing safety and health programs and practices. PICS CEO, John Moreland,  gave a demonstration of the free online tool for contractor pre-qualification.</td>
                        </tr>
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
