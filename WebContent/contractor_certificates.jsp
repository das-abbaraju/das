<%@ page language="java" errorPage="exception_handler.jsp"%>
<%//@ page language="java"%>
<%@ include file="utilities/contractor_edit_secure.jsp"%>
<jsp:useBean id="cerBean" class="com.picsauditing.PICS.CertificateBean" scope ="page"/>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope ="page"/>
<%try{
	String id = request.getParameter("id");
	cerBean.processForm(pageContext);
	cBean.setFromDB(id);
%>
<html>
<head>
<title>PICS - Pacific Industrial Contractor Screening</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <META Http-Equiv="Cache-Control" Content="no-cache">
  <META Http-Equiv="Pragma" Content="no-cache">
  <META Http-Equiv="Expires" Content="0">
  <link href="PICS.css" rel="stylesheet" type="text/css">
</head>
<body bgcolor="#EEEEEE" vlink="#003366" alink="#003366" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td valign="top">
      <table width="100%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td width="50%" bgcolor="#993300">&nbsp;</td>
          <td width="146" rowspan="2" valign="top"><a href="index.jsp"><img src="images/logo.gif" alt="HOME" width="146" height="145" border="0"></a></td>
          <td width="364"><%@ include file="utilities/mainNavigation.jsp"%></td>
          <td width="147"><%@ include file="utilities/rightUpperNav.jsp"%></td>
          <td width="50%" bgcolor="#993300">&nbsp;</td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td valign="top" align="center"><img src="images/header_certificates.gif" width="321" height="72" border="0"></td>
          <td valign="top"><%@ include file="utilities/rightLowerNav.jsp"%></td>
          <td>&nbsp;</td>
        </tr>
        <tr> 
            <td>&nbsp;</td>
            <td colspan="3">
              <table width="657" border="0" cellpadding="0" cellspacing="0">
                <tr> 
                <td class="blueMain"> 
		<%@ include file="utilities/adminContractorNav.jsp"%>
				  </td>
                </tr>
              </table>
              <table width="657" border="0" cellpadding="15" cellspacing="1" bgcolor="#F8F8F8">
                <tr bgcolor="#FFFFFF"> 
				  
                <td align="center" valign="top" class="blueMain"> <%=cerBean.getErrorMessages()%> 
                  <form name="addForm" method="post" action="contractor_certificates.jsp?id=<%=id%>&action=add" enctype="multipart/form-data">
                    <table cellpadding="2" cellspacing="0">
                      <tr> 
                        <td align="right" class="blueMain">Type</td>
                        <td class="redMain"> <%=cerBean.getTypeSelect("types","forms","3",cerBean.getTypes())%>                        </td>
                      </tr>
                      <tr> 
                        <td>&nbsp;</td>
                        <td class="redMain">Hold down 'CTRL' key to select multiple</td>
                      </tr>
					  <tr> 
                        <td align="right" class="blueMain">Operator</td>
                        <td class="redMain"> <%=cerBean.getGeneralSelect2("operator_id","forms",cerBean.operator_id)%>
                        </td>
                      </tr>
                      <tr> 
                        <td align="right" class="blueMain">PDF File&nbsp; </td>
                        <td> 
                          <input name="certificateFile" type="FILE" class="forms" size="15">
                        </td>
                      </tr>
                        <td align="right" class="blueMain">Expiration Date</td>
                        <td class="redMain"> <%=cerBean.getMonthSelect("expMonth","forms",cerBean.expMonth)%>
					/<%=cerBean.getDaySelect("expDay","forms",cerBean.expDay)%>/<%=cerBean.getYearSelect("expYear","forms",cerBean.expYear)%>
                        </td>
                      </tr>
                      <tr> 
                        <td align="right"></td>
                        <td><input name="Submit" type="submit" class="forms" value="Add Certificate"></td>
                      </tr>
					</table>
                  	</form>    
                  <table width="500" border="0" cellpadding="1" cellspacing="1">
                    <tr class="whiteTitle">
                      <td bgcolor="#003366">Delete</td>
                      <td bgcolor="#003366">Type</td>
                      <td bgcolor="#003366">Operator</td>
                      <td bgcolor="#003366">Expiration Date</td>
                      <td width="50" align="center" bgcolor="#993300">File</td>
                    </tr>
<%	cerBean.setList(id);
	while (cerBean.isNextRecord(false)) {
%>
                    <tr class="blueMain" <%=cerBean.getBGColor()%>> 
                      <form name="deleteForm" method="post" action="contractor_certificates.jsp?id=<%=id%>&action=delete">
                        <td> <input name="delete_id" type="hidden" value="<%=cerBean.cert_id%>"> 
                          <input name="Submit" type="submit" class="forms" value="Del"  onClick="return confirm('Are you sure you want to delete this file?');"> 
                        </td>
                      </form>
                      <td><%=cerBean.type%></td>
                      <td><%=cerBean.operator%></td>
                      <td><%=cerBean.getExpDateShow()%></td>
                      <td align="center"><a href="/certificates/cert_<%=id%>_<%=cerBean.cert_id%>.pdf" target="_blank"> 
                        <img src="images/icon_insurance.gif" width="20" height="20" border="0"></a> 
                      </td>
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
</body>
</html>
<%	}finally{
		cerBean.closeList();
	}//finally
%>