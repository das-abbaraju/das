<%@ page language="java" errorPage="exception_handler.jsp"%>
<%@ include file="utilities/contractor_edit_secure.jsp"%>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.contractorBean" scope ="page"/>
<jsp:useBean id="vBean" class="com.picsauditing.PICS.VerifyPDFsBean" scope ="page"/>
<%
	String id = request.getParameter("id");
	String ses_id = (String)session.getAttribute("userid");
	boolean mustFinishPrequal = (request.getParameter("mustFinishPrequal") != null);
	boolean justSubmitted = (request.getParameter("submit") != null);
	cBean.setFromDB(id);
	if (!isAdmin && !cBean.canEditPrequal()) {
		response.sendRedirect("/login.jsp");
		return;
	}//if
	vBean.verifyForms(id, config);
	if (justSubmitted && vBean.isInfoOK)
		cBean.submitPrequal(id, adminName);
%>
<html>
<head>
<title>PICS - Pacific Industrial Contractor Screening</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <META Http-Equiv="Cache-Control" Content="no-cache">
  <META Http-Equiv="Pragma" Content="no-cache">
  <META Http-Equiv="Expires" Content="0">
  <link href="PICS.css" rel="stylesheet" type="text/css">
<script language="JavaScript">
function MM_openBrWindow(theURL,winName,features) { //v2.0
  window.open(theURL,winName,features);
}

function MM_displayStatusMsg(msgStr) { //v1.0
  status=msgStr;
  document.MM_returnValue = true;
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
          <td width="146" valign="top" rowspan="2"><a href="index.jsp"><img src="images/logo.gif" alt="HOME" width="146" height="145" border="0"></a></td>
          <td width="364"><%@ include file="utilities/mainNavigation.jsp"%></td>
          <td width="147"><%@ include file="utilities/rightUpperNav.jsp"%></td>
          <td width="50%" bgcolor="#993300">&nbsp;</td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td valign="top" align="center"><img src="images/header_prequalification.gif" width="321" height="72"></td>
          <td valign="top"><%@ include file="utilities/rightLowerNav.jsp"%></td>
          <td>&nbsp;</td>
        </tr>
        <tr> 
          <td>&nbsp;</td>
		<td colspan="3" align="center" class="blueMain">
          <br><center>
<%					if (isContractor || isGeneral && id.equals(ses_id)) {
%>					
		<%@ include file="utilities/contractorNav.jsp"%>
<%					}//if
					if (isAdmin) {
%>                 		<%@ include file="utilities/adminContractorNav.jsp"%>
<%					}//if
%>                </center><br>
            Date submitted: <span class="redMain"><%=cBean.prequalDate%></span><br><br>

<%	if (justSubmitted && vBean.isInfoOK && !isAdmin) {
%>
            <strong>Your information has been submitted. PICS will review it and<br>
            contact you soon to schedule the onsite portion of the audit process.</strong><br>
            <form name="form1" method="post" action="contractor_detail.jsp?id=<%=id%>">
					<input name="submit" type="submit" value="Continue">
	            </form>
<%	} else {
		if (justSubmitted && !vBean.isInfoOK) {
%>            <strong><font class="redMain">Your information is incomplete.</font><br>
            Your safety audit cannot be scheduled until you have completely filled out <br>
			your pre-qualification.  Please provide the following required information:</strong><br>
            <br>
            <%=vBean.message%><br><br>
<%		}//if
	if (!isAdmin) {
%>	            <form name="form1" method="post" action="prequal_edit.jsp">
					<input name="id" type="hidden" value="<%=id%>">
<%//					<input name="submit" type="submit" class="forms" onClick="return confirm('Once you submit this information you will not be able to edit it.  Are you sure you want to submit it?');" value="Submit Prequalification">
%>					<input name="submit" type="submit" class="forms" value="Submit Prequalification">
	            </form>
<%		if (mustFinishPrequal) {
			if (cBean.AUDIT_STATUS_PDF.equals(cBean.auditStatus) && !"No".equals(cBean.prequal_file)) {
%>          <strong>Your safety audit cannot be scheduled until you have completely filled<br>
					out your pre-qualification.  Please fill out the following forms.</strong><br>
<%			} else {
%>			<strong>Please update your prequalification with your current information.</strong><br>
<%			}//else
		}//if
%>            <b>Be sure to sumbit your info when you have completed filling it out.</b><br><br>
<%	}//if
%>          <table border="1" cellpadding="1" cellspacing="0" bordercolor="#FFFFFF">
            <tr class="blueMain">
              <td class="blueMain"><strong>Form</strong></td>
              <td class="blueMain"><strong>Description</strong></td>
            </tr>
            <tr> 
              <td class="blueMain"><a href="/servlet/loadPDF?id=<%=id%>&form=prequalA" target="_blank">Form A</a></td>
              <td class="redMain">PDF Form* - General Info, Officers, Organization,
                Work History, EMR Data </td>
            </tr>
            <tr>
              <td class="blueMain"><a href="/servlet/loadPDF?id=<%=id%>&form=prequalS" target="_blank">Form
                S</a></td>
              <td class="redMain">PDF Form* - Health, Safety, Environment, Training</td>
            </tr>
            <tr>
              <td class="blueMain"><a href="/pqf_viewOSHA.jsp?id=<%=id%>&catID=29">Form
                O</a></td>
              <td class="redMain">OSHA Injury and Illness Data </td>
            </tr>
            <tr>
              <td class="blueMain"><a href="/prequal_trades.jsp?id=<%=id%>">Services </a></td>
              <td class="redMain">The services that your company provides </td>
            </tr>

  <tr>
    <td></td>
                <td class="blueMain">Note:&nbsp;Be sure to save your changes. 
                  They will not be saved if you just close the browser window 
                  after making edits. Also, for security reasons, your browser 
                  session will time out after 30 minutes, so please save your 
                  work regularly or it won't be saved.</td>
  </tr>
 
</table>
<% }//else %>
            <br>
            <span class="redMain">* You must have <a href="http://www.adobe.com/products/acrobat/readstep2.html" target="_blank">Adobe
            Reader 6.0</a> or later to view and edit the PDF documents.</span>
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
      Pacific Industrial Contractor Screening | site design: <a href="http://www.albumcreative.com" title="Album Creative Studios"><font color="#336699">ACS</font></a></td>
  </tr>
</table>
</body>
</html>
