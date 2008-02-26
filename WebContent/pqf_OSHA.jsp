<%@page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<jsp:useBean id="pcBean" class="com.picsauditing.PICS.pqf.CategoryBean" scope ="page"/>
<jsp:useBean id="oBean" class="com.picsauditing.PICS.OSHABean" scope ="page"/>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope ="page"/>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<jsp:useBean id="pdBean" class="com.picsauditing.PICS.pqf.DataBean" scope ="page"/>
<jsp:useBean id="helper" class="com.picsauditing.servlet.upload.UploadConHelper"/>
<script language="javascript" type="text/javascript">
<!--
function popitup(url) {
	window.open(url,'name','resizable=1,scrollbars=1,width=800,height=600');
	return false;
}

// -->
</script>
<%
	String auditType = com.picsauditing.PICS.pqf.Constants.PQF_TYPE;
	String id = request.getParameter("id");
	String conID = request.getParameter("id");
	String catID = request.getParameter("catID");
	String oID = request.getParameter("oID");
	//int currentYear = com.picsauditing.PICS.DateBean.getCurrentYear();
	int currentYear = com.picsauditing.PICS.DateBean.getCurrentYear(this.getServletContext().getInitParameter("currentYearStart"));
	boolean isNew = "New".equals(oID);
	cBean.setFromDB(conID);
	cBean.tryView(permissions);
	
	aBean.setFromDB(conID);
	oBean.setFromDB(oID);
	oBean.setShowLinks(pBean);
	if (!permissions.isAdmin() && !cBean.canEditPrequal() && !pBean.canVerifyAudit(auditType,conID)) {
		response.sendRedirect("login.jsp");
		return;
	}//if
	if ("Update".equals(request.getParameter("action"))) {
		oBean.setFromRequest(request);
		if (oBean.isOK()) {
			oBean.writeToDB(conID);
			oBean.updateNumRequired(conID);
			cBean.setPercentComplete(com.picsauditing.PICS.pqf.Constants.PQF_TYPE,pdBean.getPercentComplete(conID,com.picsauditing.PICS.pqf.Constants.PQF_TYPE));
			cBean.writeToDB();
			response.sendRedirect("pqf_editMain.jsp?id="+conID+"&auditType="+com.picsauditing.PICS.pqf.Constants.PQF_TYPE);
			return;
		}//if
	} else if ("Verify".equals(request.getParameter("action")) && pBean.canVerifyAudit(auditType,conID)) {
		oBean.auditorID=pBean.userID;
		oBean.verifiedDate = com.picsauditing.PICS.DateBean.getTodaysDate();
		oBean.writeToDB(conID);
		pdBean.saveVerificationNoUpload(request,conID,pBean.userID);
		cBean.setPercentVerified(auditType,pdBean.getPercentVerified(conID,auditType));
		cBean.writeToDB();
		response.sendRedirect("pqf_verify.jsp?id="+conID+"&auditType="+com.picsauditing.PICS.pqf.Constants.PQF_TYPE);
		return;
	} else if ("Delete".equals(request.getParameter("action"))) {
		oBean.deleteLocation(oID, application.getInitParameter("FTP_DIR"));
		oBean.updateNumRequired(conID);
		response.sendRedirect("pqf_viewOSHA.jsp?id="+conID+"&catID="+catID+"&auditType="+com.picsauditing.PICS.pqf.Constants.PQF_TYPE);
		return;

	}else if("Edit".equals(request.getParameter("action"))) {
		// do nothing
	}else{
				
		if ("add".equals(request.getParameter("action"))) {
			request.setAttribute("uploader", String.valueOf(com.picsauditing.servlet.upload.UploadProcessorFactory.PREQUAL_OSHA));
			request.setAttribute("directory", "files");
			request.setAttribute("exts","pdf,doc,jpg,txt,xls");
			helper.init(request, response);
			String errorMsg = (String)request.getAttribute("error");
			if(errorMsg != null && errorMsg != "")
				oBean.getErrors().addElement(errorMsg);
			else{
				oBean.updateFilesDB(request);
				oBean.updateNumRequired(conID);
				cBean.setPercentComplete(com.picsauditing.PICS.pqf.Constants.PQF_TYPE,pdBean.getPercentComplete(conID,com.picsauditing.PICS.pqf.Constants.PQF_TYPE));
				cBean.writeToDB();
				response.sendRedirect("pqf_editMain.jsp?id="+conID+"&auditType="+com.picsauditing.PICS.pqf.Constants.PQF_TYPE);
				return;
			}
	
		}//if
	}
	
	int count = 1;
	String SHAType = oBean.SHAType;
	if (oBean.SHA_TYPE_DEFAULT.equals(SHAType))
		SHAType = "OSHA";
	String descriptionText = "Recordable";
	if ("MSHA".equals(SHAType))
		descriptionText = "Reportable";

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
    <td valign="top"> <table width="100%" border="0" cellpadding="0" cellspacing="0">
        <tr> 
          <td width="50%" bgcolor="#993300">&nbsp;</td>
          <td width="146" valign="top" rowspan="2"><a href="index.jsp"><img src="images/logo.gif" alt="HOME" width="146" height="145" border="0"></a></td>
          <td width="364"><%@ include file="utilities/mainNavigation.jsp"%></td>
          <td width="147"><img src="images/squares_rightUpperNav.gif" width="147" height="72" border="0"></td>
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
          <td colspan="3">
		    <table border="0" cellspacing="0" cellpadding="1" class="blueMain">
                <tr align="left" class="blueMain">
				  <td colspan=2><%@ include file="includes/nav/secondNav.jsp"%></td>
				</tr>
    			<tr align="center" class="blueMain">
                  <td class="blueHeader" colspan=2>PQF for <%=aBean.name%></td>
    			</tr>
	  			<tr align="center">
                  <td colspan=2 class="redMain">
                    You must input at least your corporate statistics.  To further assist your clients, please <br>
					enter additional locations that you maintain OSHA/MSHA logs for that may be needed by your clients
				  </td>
    			</tr>
<!--	  			<tr align="center" colspan=2>
			      <form name="form1" method="post" action="pqf_edit.jsp">
        	        <td colspan=2><%=pcBean.getPQFCategorySelectDefaultSubmit("catID","blueMain",catID,auditType)%></td>
			      <input type="hidden" name="id" value="<%=conID%>">
				  </form>
      			</tr>
-->             <tr align="center" class="blueMain"> 
                <td colspan="2">Provide the following numbers (excluding subcontractors) 
                  using your OSHA/MSHA 300 Forms from the past 3 years:</td>
              </tr>
            <tr align="center" class="redMain"> 
                <td colspan="2"><strong><%=oBean.getErrorMessages()%></strong></td>
              </tr>
              <tr align="center">
                <td colspan="2">
				  <form name="form1" method="post" action="pqf_OSHA.jsp?id=<%=conID%>&catID=<%=catID%>&oID=<%=oID%>">
                    <table border="1" cellpadding="5" cellspacing="0" bordercolor="#FFFFFF" class="blueMain">
                      <tr> 
                        <td><nobr><%=oBean.getSHATypeSelect("SHAType","forms",oBean.SHAType)%> Record for location: <%=Inputs.inputSelect("location","forms",oBean.location,oBean.OSHA_TYPE_ARRAY)%>
						 <input name="description" type="text" class="forms" value="<%=oBean.description%>" size="17"></nobr></td>
                        <td colspan="6" class="redMain"><%=oBean.getLocationDescription()%></td>
                      </tr>
                      <tr> 
                        <td>Year</td>
                        <td colspan="2" class="redMain"><%=currentYear-1%></td>
                        <td colspan="2" class="redMain"><%=currentYear-2%></td>
                        <td colspan="2" class="redMain"><%=currentYear-3%></td>
                      </tr>
                      <tr> 
                        <td class="redMain">Total Man Hours Worked</td>
                        <td colspan="2"><input name="manHours1" type="text" class="forms" value="<%=oBean.getStat(oBean.MAN_HOURS, oBean.YEAR1)%>" size="10"></td>
                        <td colspan="2"><input name="manHours2" type="text" class="forms" value="<%=oBean.getStat(oBean.MAN_HOURS, oBean.YEAR2)%>" size="10"></td>
                        <td colspan="2"><input name="manHours3" type="text" class="forms" value="<%=oBean.getStat(oBean.MAN_HOURS, oBean.YEAR3)%>" size="10"></td>
                      </tr>
                      <tr> 
                        <td class="redMain">&nbsp;</td>
                        <td>No.</td>
                        <td>Rate</td>
                        <td>No.</td>
                        <td>Rate</td>
                        <td>No.</td>
                        <td>Rate</td>
                      </tr>
                      <tr>
                        <td class="redMain">Fatalities</td>
                        <td><input name="fatalities1" type="text" class="forms" value="<%=oBean.getStat(oBean.FATALITIES, oBean.YEAR1)%>" size="4"></td>
                        <td><%=oBean.calcRate(oBean.FATALITIES,oBean.YEAR1)%></td>
                        <td><input name="fatalities2" type="text" class="forms" value="<%=oBean.getStat(oBean.FATALITIES, oBean.YEAR2)%>" size="4"></td>
                        <td><%=oBean.calcRate(oBean.FATALITIES,oBean.YEAR2)%></td>
                        <td><input name="fatalities3" type="text" class="forms" value="<%=oBean.getStat(oBean.FATALITIES, oBean.YEAR3)%>" size="4"></td>
                        <td><%=oBean.calcRate(oBean.FATALITIES,oBean.YEAR3)%></td>
                      </tr>
                      <tr>
                        <td class="redMain">Number of Lost Workday Cases - Has lost days AND is <%=SHAType%> <%=descriptionText%></td>
                        <td><input name="lostWorkCases1" type="text" class="forms" value="<%=oBean.getStat(oBean.LOST_WORK_CASES, oBean.YEAR1)%>" size="4"></td>
                        <td><%=oBean.calcRate(oBean.LOST_WORK_CASES,oBean.YEAR1)%></td>
                        <td><input name="lostWorkCases2" class="forms" type="text" value="<%=oBean.getStat(oBean.LOST_WORK_CASES, oBean.YEAR2)%>" size="4"></td>
                        <td><%=oBean.calcRate(oBean.LOST_WORK_CASES,oBean.YEAR2)%></td>
                        <td><input name="lostWorkCases3" class="forms" type="text" value="<%=oBean.getStat(oBean.LOST_WORK_CASES, oBean.YEAR3)%>" size="4"></td>
                        <td><%=oBean.calcRate(oBean.LOST_WORK_CASES,oBean.YEAR3)%></td>
                      </tr>
                      <tr> 
                        <td class="redMain">Number of Lost Workdays - All lost workdays (regardless of restricted days) AND is <%=SHAType%> <%=descriptionText%></td>
                        <td><input name="lostWorkDays1" type="text" class="forms" value="<%=oBean.getStat(oBean.LOST_WORK_DAYS, oBean.YEAR1)%>" size="4"></td>
                        <td><%=oBean.calcRate(oBean.LOST_WORK_DAYS,oBean.YEAR1)%></td>
                        <td><input name="lostWorkDays2" class="forms" type="text" value="<%=oBean.getStat(oBean.LOST_WORK_DAYS, oBean.YEAR2)%>" size="4"></td>
                        <td><%=oBean.calcRate(oBean.LOST_WORK_DAYS,oBean.YEAR2)%></td>
                        <td><input name="lostWorkDays3" class="forms" type="text" value="<%=oBean.getStat(oBean.LOST_WORK_DAYS, oBean.YEAR3)%>" size="4"></td>
                        <td><%=oBean.calcRate(oBean.LOST_WORK_DAYS,oBean.YEAR3)%></td>
                      </tr>
                      <tr> 
                        <td class="redMain">Injury & Illnesses Medical Cases - No lost OR restricted days AND is <%=SHAType%> <%=descriptionText%> (non-fatal)</td>
                        <td><input name="injuryIllnessCases1" class="forms" type="text" value="<%=oBean.getStat(oBean.INJURY_ILLNESS_CASES, oBean.YEAR1)%>" size="4"></td>
                        <td><%=oBean.calcRate(oBean.INJURY_ILLNESS_CASES,oBean.YEAR1)%></td>
                        <td><input name="injuryIllnessCases2" class="forms" type="text" value="<%=oBean.getStat(oBean.INJURY_ILLNESS_CASES, oBean.YEAR2)%>" size="4"></td>
                        <td><%=oBean.calcRate(oBean.INJURY_ILLNESS_CASES,oBean.YEAR2)%></td>
                        <td><input name="injuryIllnessCases3" class="forms" type="text" value="<%=oBean.getStat(oBean.INJURY_ILLNESS_CASES, oBean.YEAR3)%>" size="4"></td>
                        <td><%=oBean.calcRate(oBean.INJURY_ILLNESS_CASES,oBean.YEAR3)%></td>
                      </tr>
                      <tr> 
                        <td class="redMain">Restricted Cases - Has restricted days AND no lost days AND is <%=SHAType%> <%=descriptionText%></td>
                        <td><input name="restrictedWorkCases1" class="forms" type="text" value="<%=oBean.getStat(oBean.RESTRICTED_WORK_CASES, oBean.YEAR1)%>" size="4"></td>
                        <td><%=oBean.calcRate(oBean.RESTRICTED_WORK_CASES,oBean.YEAR1)%></td>
                        <td><input name="restrictedWorkCases2" class="forms" type="text" value="<%=oBean.getStat(oBean.RESTRICTED_WORK_CASES, oBean.YEAR2)%>" size="4"></td>
                        <td><%=oBean.calcRate(oBean.RESTRICTED_WORK_CASES,oBean.YEAR2)%></td>
                        <td><input name="restrictedWorkCases3" class="forms" type="text" value="<%=oBean.getStat(oBean.RESTRICTED_WORK_CASES, oBean.YEAR3)%>" size="4"></td>
                        <td><%=oBean.calcRate(oBean.RESTRICTED_WORK_CASES,oBean.YEAR3)%></td>
                      </tr>
                      <tr> 
                        <td class="redMain">Total <%=SHAType%> <%=descriptionText%> Injuries and Illnesses</td>
                        <td><input name="recordableTotal1" class="forms" type="text" value="<%=oBean.getStat(oBean.RECORDABLE_TOTAL, oBean.YEAR1)%>" size="4"></td>
                        <td><%=oBean.calcRate(oBean.RECORDABLE_TOTAL,oBean.YEAR1)%></td>
                        <td><input name="recordableTotal2" class="forms" type="text" value="<%=oBean.getStat(oBean.RECORDABLE_TOTAL, oBean.YEAR2)%>" size="4"></td>
                        <td><%=oBean.calcRate(oBean.RECORDABLE_TOTAL,oBean.YEAR2)%></td>
                        <td><input name="recordableTotal3" class="forms" type="text" value="<%=oBean.getStat(oBean.RECORDABLE_TOTAL, oBean.YEAR3)%>" size="4"></td>
                        <td><%=oBean.calcRate(oBean.RECORDABLE_TOTAL,oBean.YEAR3)%></td>
                      </tr>
                      <tr> 
                        <td class="redMain">Were you exempt from submitting OSHA Logs?&nbsp;&nbsp;<a href="reasons.html" onclick="return popitup('reasons.html')">Valid exemptions</a></td>
                        <td colspan="2"><%=Utilities.getYesNoRadio("na1",oBean.convertNA(oBean.isNa1()))%></td>
                       	<td colspan="2"><%=Utilities.getYesNoRadio("na2",oBean.convertNA(oBean.isNa2()))%></td>
                        <td colspan="2"><%=Utilities.getYesNoRadio("na3",oBean.convertNA(oBean.isNa3()))%></td>
                      </tr>
                      <tr> 
                        <td align="center" colspan="7">
                          <input name="OID" type="hidden" value="<%=oID%>">
                          <input name="action" type="submit" class="forms" value="Update">
	 	  				  <% if (!isNew && permissions.isAdmin()){ %>
						  	<input name="action" type="submit" class="forms" value="Delete" onClick="return confirm('Are you sure you want to delete this location?');">
						  <% }//if %>
						</td>
                      </tr>
                    </table>
                  </form>
                  <table><tr><td align="left" class="blueMain">Please upload scanned .pdf <%=SHAType%> Log Files.
				    If you are unable to do so, you may mail or fax them to us and we can scan them for you
                  </td></tr></table>
				  <form name="form1" method="post" action="pqf_OSHA.jsp?id=<%=conID%>&oID=<%=oID%>&action=add" enctype="multipart/form-data">
                    <table border="1" cellpadding="5" cellspacing="0" bordercolor="#FFFFFF" class="redMain">
                      <tr> 
                        <td align="center" colspan="2">Upload <%=SHAType%> Log Files (.pdf, .doc, .txt, .xls or .jpg)</td>
                        <td>Uploaded</td>,
                      </tr>
                      <tr> 
                        <td align="right"><%=currentYear-1%> <%=SHAType%> Logs</td>
                        <td><input name="osha1_file" type="FILE" class="forms" size="15"></td>
                        <td align="center"><%=oBean.getFile1YearAgoLink()%></td>
                      </tr>
                      <tr> 
                        <td align="right"><%=currentYear-2%> <%=SHAType%> Logs</td>
                        <td><input name="osha2_file" type="FILE" class="forms" size="15"></td>
                        <td align="center"><%=oBean.getFile2YearAgoLink()%></td>
                      </tr>
                      <tr> 
                        <td align="right"><%=currentYear-3%> <%=SHAType%> Logs</td>
                        <td><input name="osha3_file" type="FILE" class="forms" size="15"></td>
                        <td align="center"><%=oBean.getFile3YearAgoLink()%></td>
                      </tr>
                      <tr> 
                        <td colspan="3" align="center">
                          <input name="OID" type="hidden" value="<%=oID%>">
                          <input name="submit" type="submit" class="forms" value="Upload Files">
						</td>
                      </tr>
                    </table>
                  </form>
				</td>
              </tr>
              <tr> 
                <td align="right" class="redMain">Notes:&nbsp;</td>
                <td class="buttons">(1)&nbsp;&nbsp; Data should be for the entire 
                  company. Facilities may request additional regional statistics 
                  later.</td>
              </tr>
              <tr> 
                <td>&nbsp;</td>
                <td class="buttons">(2)&nbsp;&nbsp; If your company is not required 
                  to maintain <%=SHAType%> 300 forms, please provide information from 
                  your Worker&#8217;s Compensation insurance carrier itemizing 
                  all claims for the last three years.</td>
              </tr>
            </table></td>
          <td>&nbsp;</td>
        </tr>
      </table>
      <br> <br> </td>
  </tr>
  <tr> 
    <td height="72" align="center" bgcolor="#003366" class="copyrightInfo">&copy;2007 
      Pacific Industrial Contractor Screening | site design: <a href="http://www.albumcreative.com" title="Album Creative Studios"><font color="#336699">ACS</font></a></td>
  </tr>
</table>
</body>
</html>
