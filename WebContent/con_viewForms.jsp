<%@ page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp" %>
<%@ include file="utilities/contractor_secure.jsp"%>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope ="page"/>
<jsp:useBean id="oBean" class="com.picsauditing.PICS.OperatorBean" scope ="page"/>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<jsp:useBean id="fBean" class="com.picsauditing.PICS.FormBean" scope ="page"/>
<%	String id = request.getParameter("id");
	cBean.setFromDB(id);
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
          <td width="146" valign="top" rowspan="2"><a href="index.jsp"><img src="images/logo.gif" alt="HOME" width="146" height="145" border="0"></a></td>
          <td width="364"><%@ include file="utilities/mainNavigation.jsp"%></td>
          <td width="147"><img src="images/squares_rightUpperNav.gif" width="147" height="72" border="0"></td>
          <td width="50%" bgcolor="#993300">&nbsp;</td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td valign="top" align="center"><img src="images/header_editAccount.gif" width="321" height="72"></td>
          <td valign="top"><%@ include file="utilities/rightLowerNav.jsp"%></td>
          <td>&nbsp;</td>
        </tr>
		<tr> 
          <td>&nbsp;</td>
          <td colspan="3" align="center"> 
			<table width="657" border="0" cellpadding="0" cellspacing="0">
              <tr align="center" class="blueMain">
                <td align="left"><%@ include file="includes/nav/secondNav.jsp"%></td>
              </tr>
              <tr>
                <td align="center" class="blueHeader">Forms & Documents</td>
              </tr>
              <tr>
                <td align="center" class="blueHeader">&nbsp;</td>
              </tr>
            </table>
            <table width="0" border="0" cellspacing="0" cellpadding="1">
              <tr>
                <td class="redMain" align="center">
                  <table border="0" cellpadding="1" cellspacing="1">
                    <tr class="whiteTitle">
                      <td bgcolor="#003366" align="center" colspan="2">Form</td>
                      <td bgcolor="#993300" align="center">Facility</td>
                    </tr>
<%	fBean.setList();
	while (fBean.isNextForm(pBean)){
%>
                    <tr class=blueMain align="center" <%=Utilities.getBGColor(fBean.count)%>>
                      <td align="right"><%=fBean.count%></td>
                      <td align="left"><a href='/forms/<%=fBean.file%>' target=_blank><%=fBean.formName%></a></td>
                      <td align="left"><%=fBean.opName%></td>
                    </tr>
<%	}//while%>
                  </table>
				</td>
              </tr>
              <tr> 
                <td>&nbsp;</td>
              </tr>
            </table>
          </td>
          <td>&nbsp;</td>
        </tr>
      </table>
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
