<%//@ page language="java" errorPage="exception_handler.jsp"%>
<%@ page language="java" import="java.util.*, com.picsauditing.domain.CertificateDO"%>
<%@ include file="utilities/adminGeneral_secure.jsp"%>
<jsp:useBean id="cerBean" class="com.picsauditing.PICS.CertificateBean" scope="page" />
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope="page" />
<jsp:useBean id="sBean" class="com.picsauditing.PICS.SearchBean" scope="session" />
<jsp:useBean id="certDO" class="com.picsauditing.domain.CertificateDO"
	scope="page" />
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope="page" />

<%	try{
	String[] statusList = new String[] {"Neither","Requires Action","Approved","Approved","Rejected","Rejected"};
	String status = request.getParameter("status");
	if(status == null)
		status = "Neither";
	
	String id = request.getParameter("operator_id");
	String operator_id = request.getParameter("id");
	if(operator_id != null)
		id = operator_id;
	
	if(pBean.isOperator() || pBean.isCorporate())
		id = pBean.userID;
	
	cerBean.operator_id = id;
        
	cerBean.contractor_name = request.getParameter("name");
	cerBean.setStatus(status);

	if (null != request.getParameter("Submit")){
		if(pBean.isOperator()){			
			List<CertificateDO> list = cerBean.setCertificatesFromCheckList(request);
			cerBean.UpdateCertificates(list);			
			list = cerBean.sendEmailFromCheckList(request);
			cerBean.sendEmail(list);			
		}
	}	
        
	cerBean.contractor_name = request.getParameter("name");
	cerBean.setStatus(status);
	   
    if(pBean.isCorporate())
    	cerBean.setListByFacilities(id);
    else
    	cerBean.setListAll(id);
    	
  	sBean.pageResults(cerBean.getListRS(), 20, request);	
		
%>
<html>
<head>
<title>PICS - Pacific Industrial Contractor Screening</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="PICS.css" rel="stylesheet" type="text/css">
<script language="JavaScript" SRC="js/ImageSwap.js"
	type="text/javascript"></script>
	
<script language="JavaScript">
	    
	function enable(cid){
	    var txt = document.getElementById("reason_" + cid);	    
	    txt.disabled=false;	
	    txt.focus();	    
	    
   	}
   	
   	function disable(cid) {
   	    var txt = document.getElementById("reason_" + cid)
   		txt.disabled = true;
   		txt.blur();
   		
   	}	

</script>
</head>
<body bgcolor="#EEEEEE" vlink="#003366" alink="#003366" leftmargin="0"
	topmargin="0" marginwidth="0" marginheight="0">
<table width="100%" height="100%" border="0" cellpadding="0"
	cellspacing="0">
	<tr>
		<td valign="top">
		<table width="100%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td width="50%" bgcolor="#993300">&nbsp;</td>
				<td width="146" valign="top" rowspan="2"><a href="index.jsp"><img
					src="images/logo.gif" alt="HOME" width="146" height="145"
					border="0"></a></td>
				<td width="364"><%@ include file="utilities/mainNavigation.jsp"%></td>
				<td width="147"><%@ include file="utilities/rightUpperNav.jsp"%></td>
				<td width="50%" bgcolor="#993300">&nbsp;</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td valign="top" align="center"><img
					src="images/header_reports.gif" width="321" height="72" alt=""></td>
				<td valign="top"><%@ include file="utilities/rightLowerNav.jsp"%></td>
				<td>&nbsp;</td>
			</tr>
			<tr>
				<td colspan="5">
				<table width="100%" border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td align="center" valign="top" class="buttons"><%@ include
							file="includes/selectReport.jsp"%> <span
							class="blueHeader">Insurance Certificates Report</span><br>
						<%@ include file="includes/certificatesSearch.jsp"%>
						<form name="emailForm" method="post"
							action="report_certificates.jsp"><br>
						<br>
						<%=sBean.getLinks()%>

						<table ID="certTable" width="100%" border="0" cellpadding="1" cellspacing="1">
							<tr class="whiteTitle">
							<td bgcolor="#003366">&nbsp;</td>
<% if (pBean.isOperator()) { %>
							<td bgcolor="#003366">&nbsp;</td>
<% }//end if %>
<% 	if (pBean.isAdmin()) {  %>								
								<td bgcolor="#003366">Sent</td>
								<td bgcolor="#003366">Last<nobr> Sent</nobr></td>

<% }//end if %>
								<td bgcolor="#003366">Contractor</td>
								<td bgcolor="#003366">Type</td>
<%	if (pBean.isAdmin() || pBean.isCorporate()){ %>
								<td bgcolor="#003366">Operator</td>
<% }//end if %>
								<td bgcolor="#003366">Expires</td>
								<td bgcolor="#003366">Liability</td>
								<td bgcolor="#003366">Add'l Named Ins.</td>
								<td bgcolor="#003366">Waiver</td>
								<td align="center" bgcolor="#993300">File</td>
							</tr>
<%	
	while (sBean.isNextRecord(certDO)) {
%>
							<tr id="<%=certDO.getCert_id()%>" <%=sBean.getBGColor()%> class="<%=sBean.getTextColor()%>">
							
<%    if (pBean.isOperator()) { %>
             <td></td>
             <td>
<% 	    if (status.equals("Neither")) { %>          
			 <%=cerBean.getRadioInputWithOptions("status_" + certDO.getCert_id(), "buttons", certDO.getStatus(), new String[]{"Approved","Rejected"},
					 new String[]{"Accept","Reject"}, new String[]{"disable","enable"}, certDO.getCert_id() )%>
<%	    }//end if %>

<% 	    if (status.equals("Approved")) { %>          
			 <%=cerBean.getRadioInputWithOptions("status_" + certDO.getCert_id(), "buttons", certDO.getStatus(), new String[]{"Rejected"},
					 new String[]{"Reject"}, new String[]{"enable"}, certDO.getCert_id() )%>
<%	    }//end if %>

<% 	    if (status.equals("Rejected")) { %>          
			 <%=cerBean.getRadioInputWithOptions("status_" + certDO.getCert_id(), "buttons", certDO.getStatus(), new String[]{"Approved"},
					 new String[]{"Approve"}, new String[]{"disable"}, certDO.getCert_id() )%>
<%	    }//end if %>

					 
			 &nbsp;&nbsp;Reason:<input type="text" id="reason_<%=certDO.getCert_id()%>" name="reason_<%=certDO.getCert_id()%>" 
			 	class="forms" disabled="true" value="<%=certDO.getReason()%>"/>
			 <input type="hidden" name="operator_id_<%=certDO.getCert_id()%>" value="<%=certDO.getOperator_id()%>"/>
			 <input type="hidden" name="contractor_id_<%=certDO.getCert_id()%>" value="<%=certDO.getContractor_id()%>"/>		
			 <input type="hidden" name="type_<%=certDO.getCert_id()%>" value="<%=certDO.getType()%>"/>		
			 </td> 
			 
<%	  }//end if %>

<%    if (pBean.isAdmin()) { %>
								<td><input name="sendEmail_<%=certDO.getCert_id()%>"
									type="checkbox"></td>
								<td><%=certDO.getSent()%></td>
								<td><%=com.picsauditing.PICS.DateBean.toShowFormat(certDO.getLastSentDate())%></td>


<%     }//end if %>

<% if(pBean.isCorporate()) { %>
								<td>&nbsp;&nbsp;</td>
<%  } //end if %>	
								<td><a
									href="contractor_detail.jsp?id=<%=certDO.getContractor_id()%>"
									class="<%=sBean.getTextColor()%>"><%=certDO.getContractor_name()%></a></td>
								<td><%=certDO.getType()%></td>
<%	if (pBean.isAdmin() || pBean.isCorporate()){ %>
								<td><%=certDO.getOperator()%></td>
<% } //end if %>
								<td><%=com.picsauditing.PICS.DateBean.toShowFormat(certDO.getExpDate())%></td>
								<%@ include file="includes/certificateChecks.jsp"%>
								<td align="center"><a
									href="/certificates/cert_<%=certDO.getContractor_id()%>_<%=certDO.getCert_id()%>.<%=certDO.getExt()%>"
									target="_blank"> <img src="images/icon_insurance.gif"
									width="20" height="20" border="0" alt=""></a></td>
							</tr>
<%  }//end while %>
						</table>
						<br>
						<center><%=sBean.getLinks()%></center>
<%	
cerBean.closeList();
sBean.closeSearch();	
%> 
                       <br>
<% if (pBean.isAdmin()) { %>
						 <input name="Submit" type="submit" class="buttons" value="Send Emails"
							onClick="return confirm('Are you sure you want to send these emails?');" >
<% }//end if %>
<% if (pBean.isOperator()) { %> 
                         <input name="Submit" type="submit" class="buttons" value="Submit" >
<% }//end if %>
						</form>
						</td>
					</tr>
				</table>
				</td>
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
