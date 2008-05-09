<%@ page language="java" import="java.util.*, com.picsauditing.domain.CertificateDO, com.picsauditing.PICS.Inputs" errorPage="exception_handler.jsp"%>
<%@page import="com.picsauditing.access.*"%>
<%@page import="com.picsauditing.PICS.*"%>
<%@include file="includes/main.jsp"%>
<jsp:useBean id="cerBean" class="com.picsauditing.PICS.CertificateBean" scope="page" />
<jsp:useBean id="sBean" class="com.picsauditing.PICS.SearchBean" scope="page" />
<jsp:useBean id="certDO" class="com.picsauditing.domain.CertificateDO" scope="page" />
<%
try{
	permissions.tryPermission(OpPerms.InsuranceVerification);
	boolean canEdit = permissions.hasPermission(OpPerms.InsuranceVerification,OpType.Edit);

	SearchFilter filter = new SearchFilter();
	filter.setParams(Utilities.requestParamsToMap(request));
	if(!filter.has("orderBy"))
		filter.set("orderBy","name");

	if (canEdit && null != request.getParameter("Submit")){
		List<CertificateDO> list = cerBean.setCertificatesFromVerifiedList(Utilities.requestParamsToMap(request));
		cerBean.updateVerifiedCertificates(list);
		list = cerBean.setCertificatesFromEditList(Utilities.requestParamsToMap(request));
		cerBean.UpdateEditedCertificates(list);
	}
	filter.set("s_certVerified","No");
	cerBean.setList(permissions,filter);
	sBean.pageResults(cerBean.getListRS(), 50, request);
%>
<html>

<head>
<title>Verify Insurance</title>
<script src="js/prototype.js" type="text/javascript"></script>
<script src="js/scriptaculous/scriptaculous.js?load=effects" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
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
<body>
<h1>Verify Insurance Certificates
<span class="sub">InsureGuard</span></h1>
<div id="search">
<div id="showSearch"><a href="#" onclick="showSearch()">Show
Filter Options</a></div>
<div id="hideSearch" style="display: none"><a href="#"
onclick="hideSearch()">Hide Filter Options</a></div>
                  <form id="form1" name="form1" method="post" action="verify_insurance.jsp" style="display: none">
                  <table border="0" cellpadding="2" cellspacing="0">
                    <tr> 
                      <td><input name="s_accountName" type="text" class="forms" value="<%=filter.getInputValue("s_accountName")%>" size="20" onFocus="clearText(this)"></td>
<%	if(pBean.isAdmin()){%>
                          <td><%=new AccountBean().getGeneralSelect3("s_opID","forms",filter.getInputValue("s_opID"),SearchBean.LIST_DEFAULT,"")%></td>
<%	}//if%>
                      <td><input name="imageField" type="image" src="images/button_search.gif" width="70" height="23" border="0"></td>
                    </tr>
                  </table>
            <input type="hidden" name="actionID" value="0">
			<input type="hidden" name="action" value="">
			<input type="hidden" name="showPage" value="1"/>
			<input type="hidden" name="startsWith" value="<%=sBean.selected_startsWith == null ? "" : sBean.selected_startsWith %>"/>
			<input type="hidden" name="orderBy"  value="<%=sBean.orderBy == null ? "dateCreated DESC" : sBean.orderBy %>"/>
<div class="alphapaging">
<%=sBean.getStartsWithLinksWithDynamicForm()%>
</div>           
  </form>
</div>  
<div>
<%=sBean.getLinksWithDynamicForm()%>
</div>

            <form name="form2" method="post" action="verify_insurance.jsp?id=<%=pBean.userID %>">
              <table ID="certTable" class="report">
                <thead><tr>
                  <td bgcolor="#003366">Num</td>
<%		if(canEdit){ %>
                  <td bgcolor="#003366">Verify</td>
                  <td bgcolor="#003366"></td>
<%		}//if %>
                  <td bgcolor="#003366">Contractor</td>
                  <td bgcolor="#003366">Type</td>
                  <td bgcolor="#003366">Operator</td>
                  <td bgcolor="#003366" width="80px">Expires</td>
                  <td bgcolor="#003366">Liability</td>
                  <td bgcolor="#003366">Add'l Named Ins.</td>
                  <td bgcolor="#003366">Waiver</td>								
                  <td align="center" bgcolor="#993300">File</td>
                </tr></thead>
<%	while (sBean.isNextRecord(certDO)) {%>
                <tr id="<%=certDO.getCert_id()%>" <%=sBean.getBGColor()%> class="<%=sBean.getTextColor()%>">    
                          <td align="right"><%=sBean.count-1%></td>
<%		if(canEdit){ %>
                  <td align="center"><%=Inputs.getCheckBoxInput("verified_" + certDO.getCert_id(), "forms", "", certDO.getVerified())%></td>
                  <td><input type="button" class="forms" name="editCertificate_<%=certDO.getCert_id() %>" value="Edit" onclick="editCert(<%=certDO.getCert_id()%>)"/>
                    <input type="hidden" name="oktoedit_<%=certDO.getCert_id()%>" id="oktoedit_<%=certDO.getCert_id()%>"></td>
<%		}//if %>
                  <td><a href="contractor_upload_certificates.jsp?id=<%=certDO.getContractor_id()%>" class="<%=sBean.getTextColor()%>"><%=certDO.getContractor_name()%></a></td>
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
              <center><%=sBean.getLinksWithDynamicForm()%></center>
              <br>
<%	if(canEdit){ %>
              <center><input name="Submit" type="submit" class="buttons" value="Submit" ></center>
<%	}//if %>
              </form>
              <br><br>
              <center><span class="blueMain"> You must have <a href="http://www.adobe.com/products/acrobat/readstep2.html" target="_blank">Adobe
                Reader 6.0</a> or later to view the documents above.</span> 
              </center>         
</body>
</html>
<%	}finally{
		cerBean.closeList();
		sBean.closeSearch();	
	}//finally
%>