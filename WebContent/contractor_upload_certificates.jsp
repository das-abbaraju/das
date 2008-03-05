<%@ page language="java" errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp" %>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<jsp:useBean id="cerBean" class="com.picsauditing.PICS.CertificateBean" scope ="page"/>
<jsp:setProperty name="cerBean" property="*" />
<script src="js/Validate.js"></script>
<script src="js/ValidateForms.js"></script>
<%try{
	SearchFilter filter = new SearchFilter();
	if (!permissions.isContractor())
		permissions.tryPermission(OpPerms.InsuranceCerts,OpType.View);

	boolean canEdit = permissions.hasPermission(OpPerms.InsuranceCerts,OpType.Edit) || permissions.isContractor();
	boolean canDelete = permissions.hasPermission(OpPerms.InsuranceCerts,OpType.Delete) || permissions.isContractor();
	String id = request.getParameter("id");
	if (request.getParameter("action") != null)
		cerBean.processForm(pageContext,permissions);
	ContractorBean cBean = new ContractorBean();
	cBean.setFromDB(id);
	cBean.tryView(permissions);

	filter.set("s_conID",id);
	cerBean.setList(permissions,filter);
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
            <table width="657" border="0" cellpadding="15" cellspacing="1">
              <tr>
                <td align="center" valign="top" class="redMain"> <%=cerBean.getErrorMessages()%> 
<%	if (canEdit){ %>
                  <form name="addForm" method="post" action="contractor_upload_certificates.jsp?id=<%=id%>&action=add" enctype="multipart/form-data">
                    <table cellpadding="2" cellspacing="3" bgcolor="FFFFFF">
                      <tr> 
                        <td class="blueMain">Type&nbsp;&nbsp;
                          <%=Utilities.inputSelect("types","forms","Workers Compensation",cerBean.TYPE_ARRAY)%></td>                        
                        <td class="blueMain">Operator&nbsp;&nbsp;
<%		if (permissions.isOperator()){%>
	                    <input type="hidden" name="operator_id" value="<%=permissions.getAccountId()%>"><%=FACILITIES.getNameFromID(permissions.getAccountIdString())%>
<%		}else{%>
                        <%=new AccountBean().getGeneralSelect3("operator_id","forms",cerBean.operator_id,SearchBean.DONT_LIST_DEFAULT,id) %></td>
<%		}//else%>
                      </tr>
                      <tr> 
                        <td class="blueMain">File (.pdf, .doc or .txt)&nbsp;&nbsp;
                          <input name="certificateFile" type="FILE" class="forms" size="15" required>
                        </td>
                        <td class="blueMain">Expiration&nbsp;&nbsp;<%=Inputs.inputSelect2("expMonth","forms",cerBean.expMonth, Inputs.MONTHS_ARRAY)%>
					      /<%=Inputs.inputSelect("expDay","forms",cerBean.expDay, Inputs.DAYS_ARRAY)%>/<%=Inputs.inputSelect("expYear","forms",cerBean.expYear, Inputs.YEARS_ARRAY)%>
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
<%	}//if %>
                  <table width="657" border="0" cellpadding="1" cellspacing="1" bgcolor="#EEEEEE">
                    <tr class="whiteTitle">
<%	if (canDelete){ %>
                      <td bgcolor="#003366">Delete</td>
<%	}//if%>
                      <td bgcolor="#003366">Type</td>
                      <td bgcolor="#003366">Facility</td>
                      <td bgcolor="#003366">Verified</td>
                      <td bgcolor="#003366">Status</td>
                      <td bgcolor="#003366">Expires</td>
                      <td bgcolor="#003366">Liability</td>
                      <td bgcolor="#003366">Named Ins.</td>
                      <td bgcolor="#003366">Waiver</td>
                      <td width="50" align="center" bgcolor="#993300">File</td>
                    </tr>
<%//	if (permissions.isOperator())
//		cerBean.setList(id,permissions.getAccountIdString());	
//	else
//		cerBean.setList(id);
%>
<%	while (cerBean.isNextRecord()) {%>
                    <tr class="blueMain" <%=Utilities.getBGColor(cerBean.count)%>> 
<%		if (canDelete){ %>
                      <form name="deleteForm" method="post" action="contractor_upload_certificates.jsp?id=<%=id%>&action=delete">
                        <td> <input name="delete_id" type="hidden" value="<%=cerBean.cert_id%>"> 
                          <input name="Submit" type="submit" class="forms" value="Del"  onClick="return confirm('Are you sure you want to delete this file?');"> 
                        </td>
                      </form>
<%		}//if%>
                      <td><%=cerBean.type%></td>
                      <td><%=FACILITIES.getNameFromID(cerBean.operator_id)%></td>
                      <td><%=cerBean.verified%></td>
                      <td><%=cerBean.status%></td>
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