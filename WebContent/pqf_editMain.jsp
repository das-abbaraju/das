<%//@ page language="java" import="com.picsauditing.PICS.*,com.picsauditing.PICS.pqf.*" errorPage="exception_handler.jsp"%>
<%@ page language="java" import="com.picsauditing.PICS.*,com.picsauditing.PICS.pqf.*"%>
<%@ include file="utilities/contractor_secure.jsp"%>
<jsp:useBean id="pcBean" class="com.picsauditing.PICS.pqf.CategoryBean" scope ="page"/>
<jsp:useBean id="pdBean" class="com.picsauditing.PICS.pqf.DataBean" scope ="page"/>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope ="page"/>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>

<%try{
	String auditType = request.getParameter("auditType");
	if (null==auditType || "".equals(auditType))
		throw new Exception("Error: Audit Type not set");
	String orderBy = request.getParameter("orderBy");
	String conID = request.getParameter("id");
	String catID = request.getParameter("catID");
	String id = request.getParameter("id");
	boolean mustFinishPrequal = (request.getParameter("mustFinishPrequal") != null);
	boolean justSubmitted = ("Submit".equals(request.getParameter("action")));
	boolean justUpdated = ("Update".equals(request.getParameter("action")));
	boolean isDesktopReset = ("Reset Desktop".equals(request.getParameter("action")) && pBean.isAdmin());
	boolean isDaReset = ("Reset DA".equals(request.getParameter("action")) && pBean.isAdmin());
	boolean isOfficeReset = ("Reset Office".equals(request.getParameter("action")) && pBean.isAdmin());
	boolean isPQFRegen = ("Regenerate Dynamic PQF".equals(request.getParameter("action")) && pBean.isAdmin());
	boolean isCategorySelected = (null != catID && !"0".equals(catID));
	if (isCategorySelected) {
		response.sendRedirect("pqf_edit.jsp?auditType="+auditType+"&id="+conID+"&catID="+catID);
		return;
	}//if
	String message = "";
	if (justSubmitted){
		if (pdBean.isComplete(conID,auditType)){
			cBean.submitPQF(conID, adminName, auditType);
			if (Constants.PQF_TYPE.equals(auditType))
				message = "Thank you for submitting your PQF.  If this is your first submittal, a PICS representative will be contacting you "+
					"within 7 days to discuss the audit. If you have not heard from someone within this time period feel free to contact our office.";
			else if (Constants.DESKTOP_TYPE.equals(auditType))
				message = "The Desktop Audit has now been submitted.";
			else if (Constants.OFFICE_TYPE.equals(auditType))
				message = "The Office Audit has now been submitted.";
			else if (Constants.DA_TYPE.equals(auditType))
				message = "The D&A Audit has now been submitted.";
		} else
			message = "You have not completed all the required sections.<br>Please fill out the following categories and resubmit:<br>"+pdBean.getErrorMessages();
	}//if
	if (justUpdated){
		if (pdBean.isClosed(conID,auditType)){
			cBean.closeAudit(conID, adminName,auditType);
			message = "All the requirements on this audit have been closed";
		} else
			message = "You have not closed out all the requirements in the following categories:.<br>"+pdBean.getErrorMessages();
	}//if
	cBean.setFromDB(conID);
	if (isDesktopReset){
		pcBean.generateDynamicCategories(conID,com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE, cBean.riskLevel);
		cBean.desktopPercent = "0";
		cBean.desktopVerifiedPercent = "0";
		cBean.desktopSubmittedDate = "";
		cBean.desktopClosedDate = "";
		cBean.hasNCMSDesktop = "No";
		cBean.writeToDB();
	} else if (isDaReset){
		cBean.daSubmittedDate = "";
		cBean.daClosedDate = "";
		cBean.daPercent="0";
		cBean.daVerifiedPercent="0";
		cBean.writeToDB();
	} else if (isOfficeReset){
		cBean.officePercent="0";
		cBean.officeVerifiedPercent="0";
		cBean.writeToDB();
	} else if (isPQFRegen) {
		pcBean.generateDynamicCategories(conID,Constants.PQF_TYPE,cBean.riskLevel);
		cBean.setPercentComplete(Constants.PQF_TYPE, pdBean.getPercentComplete(conID,Constants.PQF_TYPE));
	}
	aBean.setFromDB(conID);
	pdBean.setFilledOut(conID);
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
            <td width="147"><%@ include file="utilities/rightUpperNav.jsp"%></td>
            <td width="50%" bgcolor="#993300">&nbsp;</td>
          </tr>
          <tr> 
            <td>&nbsp;</td>
            <td valign="top" align="center">&nbsp;</td>
            <td valign="top"><%@ include file="utilities/rightLowerNav.jsp"%></td>
            <td>&nbsp;</td>
          </tr>
          <tr> 
            <td>&nbsp;</td>
            <td colspan="3">
              <table border="0" cellspacing="0" cellpadding="1" class="blueMain">
                <tr align="center" class="blueMain">
                  <td align="left"><%@ include file="includes/nav/secondNav.jsp"%></td>
                </tr>
    			<tr align="center" class="blueMain">
                  <td class="blueHeader"><%=auditType%> for <%=aBean.name%></td>
    			</tr>
                  <tr>
                    <td align="center">
<%		if (com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE.equals(auditType) && cBean.AUDIT_STATUS_RQS.equals(cBean.getDesktopStatus()) || 
			com.picsauditing.PICS.pqf.Constants.DA_TYPE.equals(auditType) && cBean.AUDIT_STATUS_RQS.equals(cBean.getDaStatus()) || 
			com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE.equals(auditType) && cBean.AUDIT_STATUS_RQS.equals(cBean.getOfficeStatusNew())) {%>
	                  <form name="form1" method="post" action="pqf_editMain.jsp">
					    <input name="id" type="hidden" value="<%=id%>">
					    <input name="auditType" type="hidden" value="<%=auditType%>">
					    <input name="action" type="submit" class="forms" value="Update">
	                  </form>
                      Percent Closed: <span class="redMain"><%=cBean.getPercentVerified(auditType)%>%</span><br>
                      Date submitted: <span class="redMain"><%=cBean.getAuditSubmittedDate(auditType)%></span>
<%		}else{ %>
	                  <form name="form1" method="post" action="pqf_editMain.jsp">
					    <input name="id" type="hidden" value="<%=id%>">
					    <input name="auditType" type="hidden" value="<%=auditType%>">
<%//					<input name="submit" type="submit" class="forms" onClick="return confirm('Once you submit this information you will not be able to edit it.  Are you sure you want to submit it?');" value="Submit Prequalification">
%>					    <input name="action" type="submit" class="forms" value="Submit">
	                  </form>
                      Percent Complete: <span class="redMain"><%=cBean.getPercentComplete(auditType)%>%</span><br>
                      Date submitted: <span class="redMain"><%=cBean.getAuditSubmittedDate(auditType)%></span>
                      <%=cBean.getValidUntilDate(auditType)%>
<%		}//else%>
                    </td>
                  </tr>
                  <tr>
                    <td align="center">
<%		if (mustFinishPrequal){%>
			<strong>Please update your prequalification with your current information.</strong><br>
<%		}//if%>
            <b>Be sure to submit your information when you have completed filling it out.</b><br>
			<span class="redMain"><strong><%=message%></strong></span><br>
                    </td>
                  </tr>
                  <tr align="left">
				    <td><a href=/pqf_printAll.jsp?id=<%=conID%>&auditType=<%=auditType%> target="_blank">Click here to print the entire <%=auditType%>
<%		if (!com.picsauditing.PICS.pqf.Constants.PQF_TYPE.equals(auditType))
			out.println("Audit");
%>                    </a>
				    </td>
				  </tr>
                  <tr align="center">
				    <td>
					  <table width="657" border="0" cellpadding="1" cellspacing="1">
                        <tr class="whiteTitle"> 
                          <td bgcolor="#003366" width=1%>Num</td>
                          <td bgcolor="#003366">Category</td>
                          <td bgcolor="#993300">% Complete</td>
                        </tr>
<%	pcBean.setListWithData("number",auditType,conID);
	int catCount = 0;
	while (pcBean.isNextRecord()){
		if (!com.picsauditing.PICS.pqf.Constants.PQF_TYPE.equals(auditType) || pBean.isAdmin() || "Yes".equals(pcBean.applies)){
			catCount++;
%>
                        <tr class="blueMain" <%=Utilities.getBGColor(catCount)%>> 
                          <td align=right><%=catCount%>.</td>
                          <td>
                            <a href="<%=request.getContextPath()%>/pqf_edit.jsp?auditType=<%=auditType%>&catID=<%=pcBean.catID%>&id=<%=conID%>"><%=pcBean.category%></a></td>

<%			String showPercent = "";
			if (com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE.equals(auditType) && cBean.isDesktopSubmitted())
				showPercent = pcBean.percentVerified;
			else if (com.picsauditing.PICS.pqf.Constants.DA_TYPE.equals(auditType) && cBean.isDaSubmitted())
				showPercent = pcBean.percentVerified;
			else if (com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE.equals(auditType) && cBean.isOfficeSubmitted())
				showPercent = pcBean.percentVerified;
			else
				showPercent = pcBean.percentCompleted;
%>
                             <td><%=pcBean.getPercentShow(showPercent)%><%=pcBean.getPercentCheck(showPercent)%></td>

                      </tr>
<%		}//if
	}//while
	pcBean.closeList();
%>
                      </table>
<%	if ((com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE.equals(auditType) || com.picsauditing.PICS.pqf.Constants.DA_TYPE.equals(auditType) || com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE.equals(auditType)) && pBean.isAdmin()){%>
                      <form name="form1" method="post" action="pqf_editMain.jsp">
                        <input name="id" type="hidden" value="<%=id%>">
                        <input name="auditType" type="hidden" value="<%=auditType%>">
                        <input name="action" type="submit" class="forms" value="Reset <%=auditType%>" onClick="return confirm('Are you sure you want to reset this audit?  All previously saved information will be lost');">
                      </form>
<%	}//if%>
<%	if (pBean.isAdmin() && (com.picsauditing.PICS.pqf.Constants.PQF_TYPE.equals(auditType))){%>
                      <form name="form1" method="post" action="pqf_editMain.jsp">
                        <input name="id" type="hidden" value="<%=id%>">
                        <input name="auditType" type="hidden" value="<%=auditType%>">
                        <input name="action" type="submit" class="forms" value="Regenerate Dynamic PQF" onClick="return confirm('Are you sure you want to regenerate the pqf categories?');">
                      </form>
<%	}//if%>
	                      <br><br>
					</td>
                  </tr>
                </table>
		    </td>
            <td>&nbsp;</td>
          </tr>
        </table>
        <br><br>
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
		pcBean.closeList();
	}//finally
%>