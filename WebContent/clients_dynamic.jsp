<%@ page language="java" import="java.sql.*, java.util.*" errorPage="exception_handler.jsp"%>
<%
	Connection Conn = com.picsauditing.PICS.DBBean.getDBConnection();
	
	Statement SQLStatement = Conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
	String Query = "SELECT * FROM accounts WHERE type = 'Operator' ORDER BY name ASC;";

	ResultSet SQLResult = SQLStatement.executeQuery(Query);
%>
<html>
<head>
<title>PICS - Pacific Industrial Contractors Screening</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="PICS.css" rel="stylesheet" type="text/css">
</head>

<body bgcolor="#EEEEEE" background="images/watermark.gif" vlink="#003366" alink="#003366" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td valign="top">
<table width="100%" border="0" cellpadding="0" cellspacing="0">
        <tr> 
          <td width="50%" bgcolor="#999966">&nbsp;</td>
          <td width="146" rowspan="2" valign="top"><a href="index.jsp"><img src="images/logo.gif" alt="HOME" width="146" height="145" border="0"></a></td>
          <td width="364" bgcolor="#003366"><object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,29,0" width="364" height="72">
              <param name="movie" value="flash/NAV_CLIENTS.swf">
              <param name="quality" value="high">
              <embed src="flash/NAV_CLIENTS.swf" quality="high" pluginspage="http://www.macromedia.com/go/getflashplayer" type="application/x-shockwave-flash" width="364" height="72"></embed></object></td>
          <td width="147" align="left"><img src="images/squaresClients_rightNav.gif" width="147" height="72"></td>
          <td width="50%" align="center" bgcolor="#999966">&nbsp;</td>
        </tr>
        <tr> 
          <td>&nbsp;</td>
          <td valign="top"><img src="images/squaresClients_underNav.gif" width="364" height="73"><br> 
            <img src="images/header_clients.gif" width="107" height="50"> </td>
          <td valign="top"><img src="images/spacer.gif" width="1" height="1"><img src="images/photo4.jpg" width="145" height="146" vspace="1"></td>
          <td>&nbsp;</td>
        </tr>
        <tr> 
          <td>&nbsp;</td>
          <td class="blueMain"><p>&nbsp;</p>
            
          </td>
          <td class="blueMain"><table width="100%" border="0" cellpadding="1" cellspacing="0" class="blueMain">
<%
//	int i = 0;
	SQLResult.beforeFirst();
	while (SQLResult.next()) {
		out.println("<tr><td>");
		String web_URL = SQLResult.getString("web_URL");
		if ((null == web_URL) || ("".equals(web_URL))) {
			out.println(SQLResult.getString("name"));	
		}//if
		else {
			out.println("<a href=\"http://" + web_URL + "\"target=\"_blank\">" + SQLResult.getString("name") + "</a>");
		}//else
		out.println("</td></tr>");
	}//while
	SQLResult.close();
	SQLStatement.close();
	Conn.close();					
%>
<!--
              <tr> 
                <td>BP Carson Refinery</td>
              </tr>
              <tr> 
                <td>Conoco Phillips LAR</td>
              </tr>
              <tr> 
                <td>ExxonMobil Torrance Refinery</td>
              </tr>
              <tr> 
                <td>THUMS Long Beach Company</td>
              </tr>
              <tr> 
                <td>Valero Wilmington Refinery</td>
              </tr>
-->           </table></td>
          <td class="blueMain">&nbsp;</td>
          <td>&nbsp;</td>
        </tr>
      </table>
      <br>
      <br>
      <br>
    </td>
  </tr>
  <tr>
    <td height="72" align="center" bgcolor="#003366" class="copyrightInfo">&copy;2007 
      Pacific Industrial Contractor Screening | site design: <a href="http://www.albumcreative.com" title="Album Creative Studios"><font color="#336699">ACS</font></a></td>
  </tr>
</table>
<%@ include file="includes/statcounter.jsp" %>
</body>
</html>
