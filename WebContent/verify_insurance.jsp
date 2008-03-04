<%@ page language="java" import="java.util.*, com.picsauditing.domain.CertificateDO, com.picsauditing.PICS.Inputs" errorPage="exception_handler.jsp"%>
<%@page import="com.picsauditing.access.*"%>
<%@page import="com.picsauditing.PICS.*"%>
<%@include file="includes/main.jsp"%>
<jsp:useBean id="cerBean" class="com.picsauditing.PICS.CertificateBean" scope="page" />
<jsp:useBean id="sBean" class="com.picsauditing.PICS.SearchBean" scope="page" />
<jsp:useBean id="certDO" class="com.picsauditing.domain.CertificateDO" scope="page" />
<%
try{
	SearchFilter filter = new SearchFilter();
	filter.setParams(Utilities.requestParamsToMap(request));

	permissions.tryPermission(OpPerms.InsuranceCerts);
	if (null != request.getParameter("Submit")){
		List<CertificateDO> list = cerBean.setCertificatesFromVerifiedList(Utilities.requestParamsToMap(request));
		cerBean.updateVerifiedCertificates(list);
		list = cerBean.setCertificatesFromEditList(Utilities.requestParamsToMap(request));
		cerBean.UpdateEditedCertificates(list);
	}
	filter.set("s_certVerified","No");
	cerBean.setList(permissions,filter);
	sBean.pageResults(cerBean.getListRS(), 20, request);
%>
<html>

<head>
<title>PICS - Pacific Industrial Contractor Screening</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="PICS.css" rel="stylesheet" type="text/css">
<script language="JavaScript" SRC="js/ImageSwap.js"
	type="text/javascript"></script>
<script language="JavaScript" SRC="js/DHTMLUtils.js"
	type="text/javascript"></script>
<script language="javascript">
	function editCert(cid){
		var tr = document.getElementById(cid);
		var spans = getElements(null, "span", tr);
		var elems = getElementsByName(spans, "noedit");
		for(var i = 0; i < elems.length; i++)
			elems[i].className="display_off"
		elems = getElementsByName(spans, "editme");
		for(var i = 0; i < elems.length; i++)
			elems[i].className="display_on";
		document.getElementById("oktoedit_" + cid).value="ok";		
	}
</script> 
</head>
<body bgcolor="#EEEEEE" vlink="#003366" alink="#003366" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td valign="top">
      <table width="100%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td width="50%" bgcolor="#993300">&nbsp;</td>
          <td width="146" valign="top" rowspan="2">
            <a href="index.jsp"><img src="images/logo.gif" alt="HOME" width="146" height="145" border="0"></a>
          </td>
          <td width="364"><%@ include file="utilities/mainNavigation.jsp"%></td>
          <td width="147"><img src="images/squares_rightUpperNav.gif" width="147" height="72" border="0"></td>
          <td width="50%" bgcolor="#993300">&nbsp;</td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td valign="top" align="center"><img src="images/header_approvedContractors.gif" width="321" height="72"></td>
          <td valign="top"><%@ include file="utilities/rightLowerNav.jsp"%></td>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <td colspan="5">
            <table width="100%" border="0" cellpadding="0" cellspacing="0">
              <tr> 
                <td height="70" colspan="2" align="center"> 
                  <form name="form1" method="post" action="verify_insurance.jsp">
                  <table border="0" cellpadding="2" cellspacing="0">
                    <tr align="center"> 
                      <td><input name="s_accountName" type="text" class="forms" value="<%=filter.getInputValue("s_accountName")%>" size="20" onFocus="clearText(this)"></td>
<%	if(pBean.isAdmin()){%>
                          <td><%=new AccountBean().getGeneralSelect3("s_opID","forms",filter.getInputValue("s_opID"),SearchBean.LIST_DEFAULT,"")%></td>
<%	}//if%>
                      <td><input name="imageField" type="image" src="images/button_search.gif" width="70" height="23" border="0"></td>
                    </tr>
                  </table>
                  </form>
                </td>
              </tr>
              <tr>
                <td></td>
                <td>
                  <span align="center" class="blueMain">You have <strong><%=sBean.getNumResults()%></strong> contractors to audit | &nbsp;</span><%=sBean.getLinks(filter.getURLQuery())%>
                </td>
              </tr>
              <tr>
                <td colspan="2">&nbsp;</td>
              </tr>
            </table>
            <form name="form2" method="post" action="verify_insurance.jsp?id=<%=pBean.userID %>">
              <table ID="certTable" width="100%" border="0" cellpadding="1" cellspacing="1">
                <tr class="whiteTitle">
                  <td bgcolor="#003366">Verify</td>
                  <td bgcolor="#003366"></td>
                  <td bgcolor="#003366">Contractor</td>
                  <td bgcolor="#003366">Type</td>
                  <td bgcolor="#003366">Operator</td>
                  <td bgcolor="#003366" width="80px">Expires</td>
                  <td bgcolor="#003366">Liability</td>
                  <td bgcolor="#003366">Add'l Named Ins.</td>
                  <td bgcolor="#003366">Waiver</td>								
                  <td align="center" bgcolor="#993300">File</td>
                </tr>
<%	while (sBean.isNextRecord(certDO)) {%>
                <tr id="<%=certDO.getCert_id()%>" <%=sBean.getBGColor()%> class="<%=sBean.getTextColor()%>">    
                  <td align="center"><%=Inputs.getCheckBoxInput("verified_" + certDO.getCert_id(), "forms", "", certDO.getVerified())%></td>
                  <td><input type="button" class="forms" name="editCertificate_<%=certDO.getCert_id() %>" value="Edit" onclick="editCert(<%=certDO.getCert_id()%>)"/>
                    <input type="hidden" name="oktoedit_<%=certDO.getCert_id()%>" id="oktoedit_<%=certDO.getCert_id()%>"></td>
                  <td>
                    <a href="contractor_detail.jsp?id=<%=certDO.getContractor_id()%>" class="<%=sBean.getTextColor()%>"><%=certDO.getContractor_name()%></a>
                  </td>
                  <td><%=certDO.getType()%></td>
                  <td><%=certDO.getOperator()%></td>
                  <td><span name="noedit"><%=com.picsauditing.PICS.DateBean.toShowFormat(certDO.getExpDate())%></span>
                    <span name="editme" class="display_off"><%=Utilities.inputSelect2("expMonth_" + certDO.getCert_id(),"forms",certDO.getExpMonth(),Inputs.MONTHS_ARRAY)%>
                    /<%=Utilities.inputSelect("expDay_" + certDO.getCert_id(),"forms",certDO.getExpDay(),Inputs.DAYS_ARRAY)%>/<%=Utilities.inputSelect("expYear_" + certDO.getCert_id(),"forms",certDO.getExpYear(),Inputs.YEARS_ARRAY)%></span></td>								
                  <td align="right"><span name="noedit"><%=java.text.NumberFormat.getInstance().format(certDO.getLiabilityLimit())%></span>
                    <span name="editme" class="display_off"><input type="text" name="liabilityLimit_<%=certDO.getCert_id() %>" class="formsNumber" value="<%=java.text.NumberFormat.getInstance().format(certDO.getLiabilityLimit())%>"></span></td>             
                  <td><span name="noedit"><%=com.picsauditing.PICS.Utilities.convertNullString(certDO.getNamedInsured(), "None")%></span>
                    <span name="editme" class="display_off"><input name="namedInsured_<%=certDO.getCert_id()%>" value="<%=com.picsauditing.PICS.Utilities.convertNullString(certDO.getNamedInsured(), "None")%>" class="forms"></span></td>
                  <td><span name="noedit"><%=certDO.getSubrogationWaived()%></span>
                    <span name="editme" class="display_off"><%=Inputs.getYesNoRadio("subrogationWaived_" + certDO.getCert_id(), "forms", certDO.getSubrogationWaived())%></span></td>
                  <td align="center">
                    <a href="/certificates/cert_<%=certDO.getContractor_id()%>_<%=certDO.getCert_id()%>.<%=certDO.getExt()%>" target="_blank">
                      <img src="images/icon_insurance.gif" width="20" height="20" border="0" alt=""></a>
                  </td>
                </tr>
<%  }//end while %>
              </table>
              <br>
              <center><%=sBean.getLinks(filter.getURLQuery())%></center>
              <br><center><input name="Submit" type="submit" class="buttons" value="Submit" ></center>
              </form>
              <br><br>
              <center><span class="blueMain"> You must have <a href="http://www.adobe.com/products/acrobat/readstep2.html" target="_blank">Adobe
                Reader 6.0</a> or later to view the documents above.</span> 
              </center>         
            </td>
            <td>&nbsp;</td>
          </tr>
        </table>
		<br><br><br>
      </td>
    </tr>
    <tr>
		<td height="72" align="center" bgcolor="#003366" class="copyrightInfo">&copy;2007
		Pacific Industrial Contractor Screening | site design: <a
			href="http://www.albumcreative.com" title="Album Creative Studios"><font
			color="#336699">ACS</font></a></td>
    </tr>
  </table>
</body>
</html>
<%	}finally{
		cerBean.closeList();
		sBean.closeSearch();	
	}//finally
%>