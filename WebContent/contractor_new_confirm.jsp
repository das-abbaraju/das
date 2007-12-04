<%@ page language="java" errorPage="exception_handler.jsp"%>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<%	String cID = request.getParameter("i");
	aBean.setFromDB(cID);
%>
<html>
<head>
  <title>PICS - Pacific Industrial Contractor Screening</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <link href="PICS.css" rel="stylesheet" type="text/css">
</head>
<body bgcolor="#EEEEEE" background="/images/watermark.gif" vlink="#003366" alink="#003366" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td valign="top">
	  <table width="100%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td width="50%" bgcolor="#669966">&nbsp;</td>
          <td width="146" rowspan="2" valign="top"><a href="index.jsp"><img src="images/logo.gif" alt="HOME" width="146" height="145" border="0"></a></td>
          <td width="364" bgcolor="#003366"><object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,29,0" width="364" height="72">
              <param name="movie" value="flash/NAV_REGISTER1.swf">
              <param name="quality" value="high">
              <embed src="flash/NAV_REGISTER1.swf" quality="high" pluginspage="http://www.macromedia.com/go/getflashplayer" type="application/x-shockwave-flash" width="364" height="72"></embed></object>
			  <script type="text/javascript" src="js/ieupdate.js"></script></td>
          <td width="147"><img src="images/squaresContractors_rightNav.gif" width="147" height="72"></td>            
          <td width="50%" align="center" bgcolor="#669966">&nbsp;</td>
        </tr>
        <tr> 
          <td>&nbsp;</td>
          <td align="left" valign="top"><img src="images/squareSignup_2.gif" width="364" height="73"><br><img src="images/header_register.gif" width="321" height="72"></td>
            <td valign="top"><script language="JavaScript">
  var j,d="",l="",m="",p="",q="",z="",list= new Array()
  list[list.length]='images/photo1.jpg';
  list[list.length]='images/photo2.jpg';
  list[list.length]='images/photo3.jpg';
  list[list.length]='images/photo4.jpg';
  list[list.length]='images/photo5.jpg';
  list[list.length]='images/photo6.jpg';
  list[list.length]='images/photo7.jpg';
  list[list.length]='images/photo8.jpg';
  list[list.length]='images/photo9.jpg';
  list[list.length]='images/photo10.jpg';
  list[list.length]='images/photo11.jpg';
  list[list.length]='images/photo12.jpg';
  j=parseInt(Math.random()*list.length);
  j=(isNaN(j))?0:j;
  document.write("<img hspace='1' vspace='1' src='"+list[j]+"'>");</script>
      <map name="Map">
        <area shape="rect" coords="73,4,142,70" href="logout.jsp">
      </map></td>
          <td>&nbsp;</td>
        </tr>
        <tr> 
          <td>&nbsp;</td>
            <td colspan="3" valign="top"><br>
              <table width="657" cellpadding="0" cellspacing="0">
                <tr>                   
                <td width="145" class="blueMain"> <br>
                  </td>
                <td class="blueMain"><p>Congratulations, 
                    your account has been created! <br><span class="redMain">PICS 
                    will review your submission and send you an invoice for $498
                    ($399 for first year membership, plus one-time $99 activation fee) within 
                    48 hours. Those of you that were requested by your Operator to be a 
                    PQF-only contractor (i.e. no audits) will be sent an invoice for $198 
                    ($99 for first year membership, plus one-time $99 activation fee). 
                    This invoice must be paid before your account will be activated</span><br>
                    <br>
                    You must confirm the email address that you provided us before your account
					will be activated. If you have a spam filter, we suggest you add picsauditing.com
					to the safe sender domain list. Otherwise you may not receive our automatically generated
					emails. Following email confirmation you will be able to log in to our website using the following:<br>
                    <br>
                    Username: <b><%=aBean.username%></b><br>
                    Password: <b><%=aBean.password%></b></p>
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
<%@ include file="includes/statcounter.jsp"%>
</body>
</html>
