<%@ page language="java" errorPage="exception_handler.jsp"%>
<%//@ page language="java"%>
<%@ include file="utilities/contractor_secure.jsp"%>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope ="page"/>
<jsp:useBean id="cerBean" class="com.picsauditing.PICS.CertificateBean" scope ="page"/>
<jsp:useBean id="permissions" class="com.picsauditing.access.Permissions" scope="session" />

<%try{
	String id = request.getParameter("id");
	if (permissions.isOperator())
		cerBean.setList(id, permissions.getUsername());
	else
		cerBean.setList(id);
	
	cBean.setFromDB(id);
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
    <td valign="top">
        <table width="100%" border="0" cellpadding="0" cellspacing="0">
          <tr>
          <td width="50%" bgcolor="#993300">&nbsp;</td>
          <td width="146" rowspan="2"><a href="index.jsp"><img src="images/logo.gif" alt="HOME" width="146" height="145" border="0"></a></td>
          <td width="364"><%@ include file="utilities/mainNavigation.jsp"%></td>
          <td width="147"><%@ include file="utilities/rightUpperNav.jsp"%></td>
          <td width="50%" bgcolor="#993300">&nbsp;</td>
        </tr>
        <tr> 
          <td>&nbsp;</td>
          <td valign="top"><div align="center" class="forms"> <img src="images/header_insurance.gif" alt="Back to Contractor List" width="328" height="73" border="0"> 
              <br>
              </div></td>
            <td valign="top"><script language="JavaScript">
  var j,d="",l="",m="",p="",q="",z="",list= new Array()
  list[list.length]='images/squareLogin_1.gif';
  list[list.length]='images/squareLogin_2.gif';
  list[list.length]='images/squareLogin_3.gif';
  list[list.length]='images/squareLogin_4.gif';
  list[list.length]='images/squareLogin_5.gif';
  j=parseInt(Math.random()*list.length);
  j=(isNaN(j))?0:j;
  document.write("<img useMap='#Map' border='0' hspace='1' src='"+list[j]+"'>");</script></td>
          <td>&nbsp;</td>
        </tr>
        <tr> 
            <td>&nbsp;</td>
            <td colspan="3">
<table width="657" border="0" cellpadding="0" cellspacing="0">
                <tr> 
                <td><br>
<%					if (pBean.isContractor() ||	"General".equalsIgnoreCase(pBean.userType) && id.equals(permissions.getUserIdString())) {
%>				<%@ include file="utilities/contractorNav.jsp"%>
<%					}//if
					if (pBean.isAdmin()) {
%>						<%@ include file="utilities/adminContractorNav.jsp"%>
<%					}//if
					if (pBean.isOperator() || ("General".equalsIgnoreCase(pBean.userType) && !id.equals(permissions.getUserIdString()))) {%>
							<%@ include file="utilities/opContractorNav.jsp"%>
<%	}%>
				</td>
                </tr>
              </table>
              
            <table width="657" border="0" cellpadding="15" cellspacing="1" bgcolor="#FFFFFF">
              <tr>
                <td align="center" valign="top" class="redMain"> <%=cerBean.getErrorMessages()%> 
                  <table width="500" border="0" cellpadding="1" cellspacing="1">
                    <tr class="whiteTitle">
                      <td bgcolor="#003366">Type</td>
                      <td bgcolor="#003366">Operator</td>
                      <td bgcolor="#003366">Expires</td>
                      <td bgcolor="#003366">Liability</td>
                      <td bgcolor="#003366">Named Ins.</td>
                      <td bgcolor="#003366">Waiver</td>
<%	if (!permissions.isContractor()) {
%>                    <td width="50" align="center" bgcolor="#993300"><strong><font color="#FFFFFF">File</font></strong></td>
<%	}//if
%>                  </tr>
<%	while (cerBean.isNextRecord(cerBean.DONT_SET_NAME)) {
%>
                    <tr class="blueMain" <%=cerBean.getBGColor()%>> 
                      <td><%=cerBean.type%></td>
                      <td><%=cerBean.operator%></td>
                      <td><%=cerBean.getExpDateShow()%></td>
                      <td align="right"><%=java.text.NumberFormat.getInstance().format(cerBean.getLiabilityLimit())%></td>
                      <td><%=cerBean.getNamedInsured()%></td>
                      <td><%=cerBean.getSubrogationWaived()%></td>
<%	if (!permissions.isContractor()) {
%>                    <td align="center"><a href="<%=cerBean.getDirPath()%>cert_<%=id%>_<%=cerBean.cert_id%>.<%=cerBean.getExt()%>" target="_blank"> 
                        <img src="images/icon_insurance.gif" width="20" height="20" border="0"></a> 
                      </td>
<%	}//if%>
                  </tr>
<%	}//while%>
                  </table>
					<br>
					<br>
				  </td>
                </tr>
              </table></td>
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

<map name="Map">
 <area shape="rect" coords="73,4,142,70" href="logout.jsp">
</map>
	  
<%@ include file="includes/statcounter.jsp" %>
</body>
</html>
<%	}finally{
	    cerBean.closeList();
	}//finally
%>