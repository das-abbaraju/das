<%@ page language="java" errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp" %>
<jsp:useBean id="cerBean" class="com.picsauditing.PICS.CertificateBean" scope ="page"/>
<jsp:setProperty name="cerBean" property="*" />
<script src="js/Validate.js"></script>
<script src="js/ValidateForms.js"></script>
<%
permissions.tryPermission(OpPerms.InsuranceCerts, OpType.Edit);

try{
	String id = request.getParameter("id");
	cerBean.processForm(pageContext);
	ContractorBean cBean = new ContractorBean();
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
 
	<style>
	/*
	 * Validate.js requires us to define styles for the "invalid" class to give
	 * invalid fields a distinct visual appearance that the user will recognize.
	 * We can optionally define styles for valid fields as well.
	 */
	input.invalid { background: #faa; } /* Reddish background for invalid fields */
	input.valid { background: #fff; }   /* Greenish background for valid fields */
	</style>
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
          <td width="147"><img src="images/squares_rightUpperNav.gif" width="147" height="72" border="0"></td>
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
                <td align="left"><%@ include file="includes/nav/secondNav.jsp"%></td>
                </tr>
              </table>
              <table width="657" border="0" cellpadding="15" cellspacing="1" bgcolor="#F8F8F8">
                <tr bgcolor="#FFFFFF"> 
				  
                <td align="center" valign="top" class="redMain"> <%=cerBean.getErrorMessages()%> 
                  <form name="addForm" method="post" action="contractor_upload_certificates.jsp?id=<%=id%>&action=add" enctype="multipart/form-data">
                    <table cellpadding="2" cellspacing="3">
                      <tr> 
                        <td class="blueMain">Type&nbsp;&nbsp;
                        <%=Utilities.inputSelect("types","forms","Workers Compensation",cerBean.TYPE_ARRAY)%></td>                        
                        <td class="blueMain">Operator&nbsp;&nbsp;
<%	if (permissions.isOperator()){%>
	                    <input type="hidden" name="operator_id" value="<%=permissions.getAccountId()%>">
<%	}else{%>
                        <%=new AccountBean().getGeneralSelect3("operator_id","forms",cerBean.operator_id,SearchBean.DONT_LIST_DEFAULT,id) %></td>
<%	}//else%>
                      </tr>
                      <tr> 
                        <td class="blueMain">File (.pdf, .doc or .txt)&nbsp;&nbsp;
                          <input name="certificateFile" type="FILE" class="forms" size="15" required>
                        </td>
                        <td class="blueMain">Expiration&nbsp;&nbsp;<%=Utilities.inputSelect2("expMonth","forms",cerBean.expMonth, CertificateBean.MONTHS_ARRAY)%>
					      /<%=Utilities.inputSelect("expDay","forms",cerBean.expDay, CertificateBean.DAYS_ARRAY)%>/<%=Utilities.inputSelect("expYear","forms",cerBean.expYear, CertificateBean.YEARS_ARRAY)%>
                        </td>
                      </tr>
                      <tr> 
                        <td class="blueMain">Liability Limit&nbsp;&nbsp;
                        <input type="text" name="liabilityLimit" onchange="validateNumber()" class="formsNumber" required value="<%=cerBean.getLiabilityLimit()%>"></td>
                        <td class="blueMain">Additional Named Insured&nbsp;&nbsp;
                          <input type="text" required name="namedInsured" class="forms" value="<%=cerBean.getNamedInsured()%>"></td>
                      </tr>
                       <tr> 
                        <td class="blueMain">Waiver of Subrogation&nbsp;&nbsp;
                        <input type="radio" name="subrogationWaived" CHECKED class="forms" value="No"/>No&nbsp;&nbsp;
                          <input type="radio" name="subrogationWaived" class="forms" value="Yes"/>Yes</td>
                      </tr>                     
					</table>
					<hr>
					  <input name="Submit" type="submit" class="forms" value="Add Certificate">
                    
                  	</form>    
                  <table width="657" border="0" cellpadding="1" cellspacing="1">
                    <tr class="whiteTitle">
                      <td bgcolor="#003366">Delete</td>
                      <td bgcolor="#003366">Type</td>
                      <td bgcolor="#003366">Operator</td>
                      <td bgcolor="#003366">Expires</td>
                      <td bgcolor="#003366">Liability</td>
                      <td bgcolor="#003366">Add'l Named Ins.</td>
                      <td bgcolor="#003366">Waiver</td>
                      <td width="50" align="center" bgcolor="#993300">File</td>
                    </tr>
<%	cerBean.setList(id);
	while (cerBean.isNextRecord(false)) {
%>
                    <tr class="blueMain" <%=Utilities.getBGColor(cerBean.count)%>> 
                      <form name="deleteForm" method="post" action="contractor_upload_certificates.jsp?id=<%=id%>&action=delete">
                        <td> <input name="delete_id" type="hidden" value="<%=cerBean.cert_id%>"> 
                          <input name="Submit" type="submit" class="forms" value="Del"  onClick="return confirm('Are you sure you want to delete this file?');"> 
                        </td>
                      </form>
                      <td><%=cerBean.type%></td>
                      <td><%=cerBean.operator%></td>
                      <td><%=cerBean.getExpDateShow()%></td>
                      <td align="right"><%=java.text.NumberFormat.getInstance().format(cerBean.getLiabilityLimit())%></td>
                      <td><%=cerBean.getNamedInsured()%></td>
                      <td><%=cerBean.getSubrogationWaived()%></td>
                      <td align="center"><a href="<%=cerBean.getDirPath()%>cert_<%=id%>_<%=cerBean.cert_id%>.<%=cerBean.getExt()%>" target="_blank"> 
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