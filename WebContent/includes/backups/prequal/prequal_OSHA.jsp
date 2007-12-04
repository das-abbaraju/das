<%//@ page language="java" errorPage="exception_handler.jsp"%>
<%@ page language="java" import="com.picsauditing.PICS.*;"%>
<%@ include file="utilities/contractor_edit_secure.jsp"%>
<jsp:useBean id="oBean" class="com.picsauditing.PICS.OSHABean" scope ="page"/>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.contractorBean" scope ="page"/>
<jsp:useBean id="helper" class="com.picsauditing.servlet.upload.UploadConHelper"/>
<%	
	String PREQUAL_OSHA = "0";
	String edit_id = request.getParameter("id");
	int currentYear = com.picsauditing.PICS.DateBean.getCurrentYear();
	cBean.setFromDB(edit_id);
	if (!"admin".equals(utype) && !cBean.canEditPrequal()) {
		response.sendRedirect("/login.jsp");
		return;
	}//if

	oBean.setFromDB(edit_id);
	if (request.getParameter("action") != null) {
		oBean.setFromRequest(request);
		if (oBean.isOK()) {
			oBean.writeToDB(edit_id);
			response.sendRedirect("prequal_edit.jsp?id=" + edit_id);
			return;
		}//if
	}//if
			
	if ("Upload Files".equals(request.getParameter("submit"))) {
		request.setAttribute("uploader", PREQUAL_OSHA);
		helper.init(request, response);
		oBean.updateFilesDB(request);
		return;
	}//if	
	
	oBean.setShowLinks((String)session.getAttribute("usertype"), (java.util.HashSet)session.getAttribute("canSeeSet"));
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
          <td colspan="3"> <table border="0" cellspacing="0" cellpadding="1" class="blueMain">
              <tr align="center" class="blueMain"> 
                <td colspan="2" class="blueHeader">Injury and Illness Data</td>
              </tr>
              <tr align="center" class="blueMain"> 
                <td colspan="2">Provide the following numbers (excluding subcontractors) 
                  using your OSHA 300 Forms from the past 3 years:</td>
              </tr>
              <tr align="center">
                <td colspan="2"><br> <form name="form1" method="post" action="prequal_OSHA.jsp?id=<%=edit_id%>">
                    <table border="1" cellpadding="5" cellspacing="0" bordercolor="#FFFFFF" class="blueMain">
                      <tr> 
                        <td>OSHA Record for location:<%=Inputs.inputSelect("location","forms",oBean.location,oBean.OSHA_TYPE_ARRAY)%>
						 <input name="description" type="text" class="forms" value="<%=oBean.description%>" size="10"></td>
                        <td colspan="6" class="redMain"><%=oBean.location%>:<%=oBean.description%></td>
                      </tr>
                      <tr> 
                        <td>Year</td>
                        <td colspan="2" class="redMain"><%=currentYear-1%></td>
                        <td colspan="2" class="redMain"><%=currentYear-2%></td>
                        <td colspan="2" class="redMain"><%=currentYear-3%></td>
                      </tr>
                      <tr> 
                        <td class="redMain">Total Man Hours Worked</td>
                        <td colspan="2"><input name="manHours1" type="text" class="forms" value="<%=oBean.formatNumber(oBean.stats[oBean.MAN_HOURS][oBean.YEAR1])%>" size="10"></td>
                        <td colspan="2"><input name="manHours2" type="text" class="forms" value="<%=oBean.formatNumber(oBean.stats[oBean.MAN_HOURS][oBean.YEAR2])%>" size="10"></td>
                        <td colspan="2"><input name="manHours3" type="text" class="forms" value="<%=oBean.formatNumber(oBean.stats[oBean.MAN_HOURS][oBean.YEAR3])%>" size="10"></td>
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
                        <td><input name="fatalities1" type="text" class="forms" value="<%=oBean.stats[oBean.FATALITIES][oBean.YEAR1]%>" size="4"></td>
                        <td><%=oBean.calcRate(oBean.FATALITIES,oBean.YEAR1)%></td>
                        <td><input name="fatalities2" type="text" class="forms" value="<%=oBean.stats[oBean.FATALITIES][oBean.YEAR2]%>" size="4"></td>
                        <td><%=oBean.calcRate(oBean.FATALITIES,oBean.YEAR2)%></td>
                        <td><input name="fatalities3" type="text" class="forms" value="<%=oBean.stats[oBean.FATALITIES][oBean.YEAR3]%>" size="4"></td>
                        <td><%=oBean.calcRate(oBean.FATALITIES,oBean.YEAR3)%></td>
                      </tr>
                      <tr>
                        <td class="redMain">Number of Lost Workday Cases involving only days away from work</td>
                        <td><input name="lostWorkCases1" type="text" class="forms" value="<%=oBean.stats[oBean.LOST_WORK_CASES][oBean.YEAR1]%>" size="4"></td>
                        <td><%=oBean.calcRate(oBean.LOST_WORK_CASES,oBean.YEAR1)%></td>
                        <td><input name="lostWorkCases2" class="forms" type="text" value="<%=oBean.stats[oBean.LOST_WORK_CASES][oBean.YEAR2]%>" size="4"></td>
                        <td><%=oBean.calcRate(oBean.LOST_WORK_CASES,oBean.YEAR2)%></td>
                        <td><input name="lostWorkCases3" class="forms" type="text" value="<%=oBean.stats[oBean.LOST_WORK_CASES][oBean.YEAR3]%>" size="4"></td>
                        <td><%=oBean.calcRate(oBean.LOST_WORK_CASES,oBean.YEAR3)%></td>
                      </tr>
                      <tr> 
                        <td class="redMain">Number of Lost Workday Cases involving days away from work,
						  or days of restricted work activity</td>
                        <td><input name="restrictedWorkCases1" type="text" class="forms" value="<%=oBean.stats[oBean.RESTRICTED_WORK_CASES][oBean.YEAR1]%>" size="4"></td>
                        <td><%=oBean.calcRate(oBean.RESTRICTED_WORK_CASES,oBean.YEAR1)%></td>
                        <td><input name="restrictedWorkCases2" class="forms" type="text" value="<%=oBean.stats[oBean.RESTRICTED_WORK_CASES][oBean.YEAR2]%>" size="4"></td>
                        <td><%=oBean.calcRate(oBean.RESTRICTED_WORK_CASES,oBean.YEAR2)%></td>
                        <td><input name="restrictedWorkCases3" class="forms" type="text" value="<%=oBean.stats[oBean.RESTRICTED_WORK_CASES][oBean.YEAR3]%>" size="4"></td>
                        <td><%=oBean.calcRate(oBean.RESTRICTED_WORK_CASES,oBean.YEAR3)%></td>
                      </tr>
                      <tr> 
                        <td class="redMain">Injury and Illnesses involing medical treatment only 
						  (non-fatal cases without lost work days)</td>
                        <td><input name="treatmentOnlyCases1" class="forms" type="text" value="<%=oBean.stats[oBean.TREATMENT_ONLY_CASES][oBean.YEAR1]%>" size="4"></td>
                        <td><%=oBean.calcRate(oBean.TREATMENT_ONLY_CASES,oBean.YEAR1)%></td>
                        <td><input name="treatmentOnlyCases2" class="forms" type="text" value="<%=oBean.stats[oBean.TREATMENT_ONLY_CASES][oBean.YEAR2]%>" size="4"></td>
                        <td><%=oBean.calcRate(oBean.TREATMENT_ONLY_CASES,oBean.YEAR2)%></td>
                        <td><input name="treatmentOnlyCases3" class="forms" type="text" value="<%=oBean.stats[oBean.TREATMENT_ONLY_CASES][oBean.YEAR3]%>" size="4"></td>
                        <td><%=oBean.calcRate(oBean.TREATMENT_ONLY_CASES,oBean.YEAR3)%></td>
                      </tr>
                      <tr> 
                        <td class="redMain">Total OSHA Recordable Injuries and Illnesses</td>
                        <td><input name="injuryTotal1" class="forms" type="text" value="<%=oBean.stats[oBean.INJURY_TOTAL][oBean.YEAR1]%>" size="4"></td>
                        <td><%=oBean.calcRate(oBean.INJURY_TOTAL,oBean.YEAR1)%></td>
                        <td><input name="injuryTotal2" class="forms" type="text" value="<%=oBean.stats[oBean.INJURY_TOTAL][oBean.YEAR2]%>" size="4"></td>
                        <td><%=oBean.calcRate(oBean.INJURY_TOTAL,oBean.YEAR2)%></td>
                        <td><input name="injuryTotal3" class="forms" type="text" value="<%=oBean.stats[oBean.INJURY_TOTAL][oBean.YEAR3]%>" size="4"></td>
                        <td><%=oBean.calcRate(oBean.INJURY_TOTAL,oBean.YEAR3)%></td>
                      </tr>
                    </table>
                    <br>
                    <input name="action" type="submit" class="forms" value="Submit">
                  </form>
                  <table width="340"><tr><td align="left" class="blueMain">Please upload scanned .pdf OSHA Log Files.
				  If you are unable to do so, you may mail or fax them to us and we can scan them for you
</td></tr></table>
				  <form name="form1" method="post" action="prequal_OSHA.jsp?id=<%=edit_id%>" enctype="multipart/form-data">
                    <table border="1" cellpadding="5" cellspacing="0" bordercolor="#FFFFFF" class="redMain">
                      <tr> 
                        <td align="center" colspan="2">Upload OSHA Log Files (.pdf)</td>
                        <td>Uploaded</td>
                      </tr>
                      <tr> 
                        <td align="right"><%=currentYear-1%> OSHA Logs</td>
                        <td><input name="osha1_file" type="FILE" class="forms" size="15"></td>
                        <td align="center"><%=oBean.getFile1YearAgoLink()%></td>
                      </tr>
                      <tr> 
                        <td align="right"><%=currentYear-2%> OSHA Logs</td>
                        <td><input name="osha2_file" type="FILE" class="forms" size="15"></td>
                        <td align="center"><%=oBean.getFile2YearAgoLink()%></td>
                      </tr>
                      <tr> 
                        <td align="right"><%=currentYear-3%> OSHA Logs</td>
                        <td><input name="osha3_file" type="FILE" class="forms" size="15"></td>
                        <td align="center"><%=oBean.getFile3YearAgoLink()%></td>
                      </tr>
                    </table>
                    <br>
                    <input name="submit" type="submit" class="forms" value="Upload Files">
                  </form></td>
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
                  to maintain OSHA 300 forms, please provide information from 
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
