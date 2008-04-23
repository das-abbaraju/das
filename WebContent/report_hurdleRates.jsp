<%@page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<%@include file="utilities/adminGeneral_secure.jsp"%>

<jsp:useBean id="sBean" class="com.picsauditing.PICS.SearchBean" scope="page" />
<jsp:useBean id="oBean" class="com.picsauditing.PICS.OperatorBean" scope="page" />

<%	try{
	oBean.setFromDB(pBean.userID);
	boolean isSubmitted = (null != request.getParameter("submitSearch.x"));
	if (isSubmitted){
		oBean.setHurdlesFromRequest(request);
		if (oBean.isHurdlesOK())
			oBean.writeToDB();
	}//if
	sBean.orderBy = "Name";
	sBean.setIsHurdleRatesReport();
	sBean.doSearch(request, SearchBean.ONLY_ACTIVE, 100, pBean,pBean.userID);
	if (!pBean.isAdmin())
		sBean.setCanSeeSet(pBean.canSeeSet);
%>
<html>
<head>
<title>Red Flag Hurdle Rates</title>
<script language="JavaScript" SRC="js/ImageSwap.js"></script>
</head>
<body>
				<table width="657" border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td height="70" colspan="2" align="center"><%@ include
							file="includes/selectReport.jsp"%> <span
							class="blueHeader">Red Flag Hurdle Rates Report</span></td>
					</tr>
				</table>
				<span class="redMain"><%=oBean.getErrorMessages()%></span>
				<form name="form1" method="post" action="report_hurdleRates.jsp">
				<table border="0" cellpadding="2" cellspacing="1">
<%	if (pBean.isCorporate()){%>
					<tr class="blueMain">
						<td colspan="2"><%=pBean.oBean.getFacilitySelect("generalContractorID",
								"forms", sBean.selected_generalContractorID)%></td>
					</tr>
<%	//		out.println(pBean.oBean.getFacilitySelect("generalContractorID","forms",sBean.selected_generalContractorID)+"<br><br>");
	}//if
	int thisYear = com.picsauditing.PICS.DateBean.getCurrentYear(this.getServletContext().getInitParameter("currentYearStart"));
%>
					<tr class="whiteTitle" bgcolor="#003366">
						<td align="center" colspan="2">Question</td>
						<td align="center">Include?</td>
					</tr>
					<tr class="blueMain" bgcolor="FFFFFF">
						<td align="right">Experience Modification Rate (EMR) Cutoff:</td>
						<td><input name="emrHurdle" type="text" size="5"
							value=<%=oBean.emrHurdle%>></td>
						<td><%=Inputs.getYesNoRadio("flagEmr", "forms", oBean.flagEmr)%></td>
					</tr>
					<tr class="blueMain">
						<td align="right">EMR Timeframe:</td>
						<td><input name="emrTime" type="radio" value="1"
							<%=Inputs.getChecked("1",oBean.emrTime)%>>1 Yr <input
							name="emrTime" type="radio" value="3"
							<%=Inputs.getChecked("3",oBean.emrTime)%>>3 Yr Avg</td>
						<td></td>
					</tr>
					<tr class="blueMain" bgcolor="FFFFFF">
						<td align="right">OSHA Lost Workdays Case Rate (LWCR) Cutoff:</td>
						<td><input name="lwcrHurdle" type="text" size="5"
							value=<%=oBean.lwcrHurdle%>></td>
						<td><%=Inputs.getYesNoRadio("flagLwcr", "forms",
									oBean.flagLwcr)%></td>
					</tr>
					<tr class="blueMain">
						<td align="right">OSHA LWCR Timeframe:</td>
						<td><input name="lwcrTime" type="radio" value="1"
							<%=Inputs.getChecked("1",oBean.lwcrTime)%>>1 Yr <input
							name="lwcrTime" type="radio" value="3"
							<%=Inputs.getChecked("3",oBean.lwcrTime)%>>3 Yr Avg</td>
						<td></td>
					</tr>
					<tr class="blueMain" bgcolor="FFFFFF">
						<td align="right">OSHA TRIR Cutoff:</td>
						<td><input name="trirHurdle" type="text" size="5"
							value=<%=oBean.trirHurdle%>></td>
						<td><%=Inputs.getYesNoRadio("flagTrir", "forms",
									oBean.flagTrir)%></td>
					</tr>
					<tr class="blueMain">
						<td align="right">OSHA TRIR Timeframe:</td>
						<td><input name="trirTime" type="radio" value="1"
							<%=Inputs.getChecked("1",oBean.trirTime)%>>1 Yr <input
							name="trirTime" type="radio" value="3"
							<%=Inputs.getChecked("3",oBean.trirTime)%>>3 Yr Avg</td>
						<td></td>
					</tr>
					<tr class="blueMain" bgcolor="FFFFFF">
						<td align="right">3 Yr Fatalities Total Cutoff:</td>
						<td><input name="fatalitiesHurdle" type="text" size="5"
							value=<%=oBean.fatalitiesHurdle%>></td>
						<td><%=Inputs.getYesNoRadio("flagFatalities", "forms",
							oBean.flagFatalities)%></td>
					</tr>
					<tr class="blueMain">
						<td align="right" colspan="2">Flag PQF Question #14.1.1
						(Contractor does not perform drug screening):</td>
						<td><%=Inputs.getYesNoRadio("flagQ318", "forms",
									oBean.flagQ318)%></td>
					</tr>
					<tr class="blueMain" bgcolor="FFFFFF">
						<td align="right" colspan="2">Flag PQF Question #27.1.1
						(Contractor does not perform background checks):</td>
						<td><%=Inputs.getYesNoRadio("flagQ1385", "forms",
							oBean.flagQ1385)%></td>
					</tr>
					<tr class="blueMain">
						<td align="right" colspan="2"><input name="submitSearch"
							type="image" src="images/button_search.gif" width="70"
							height="23" border="0"
							onMouseOver="MM_swapImage('imageField','','images/button_search_o.gif',1)"
							onMouseOut="MM_swapImgRestore()"></td>
					</tr>
				</table>
				</form>
				<%=sBean.getLinks()%>
				<table width="657" border="0" cellpadding="1" cellspacing="1">
					<tr bgcolor="#003366" class="whiteTitle">
						<td></td>
						<td></td>
<%	if (oBean.flagEmr()){
		if (!oBean.isEmrTimeAverage()){
%>
						<td colspan="3" align="center">EMR</td>
<%		}else{%>
						<td colspan="1" align="center">EMR</td>
<%		}//else
	}//if
	if (oBean.flagLwcr()){
		if (!oBean.isLwcrTimeAverage()){
%>
						<td colspan="3" align="center">LWCR</td>
<%		}else{%>
						<td colspan="1" align="center">LWCR</td>
<%		}//else
	}//if
	if (oBean.flagTrir()) {
		if (!oBean.isTrirTimeAverage()) {
%>
						<td colspan="3" align="center">TRIR</td>
<%		} else {
	%>
						<td colspan="1" align="center">TRIR</td>
<%		}//else
	}//if
	if (oBean.flagFatalities()) {
%>
						<td align="center">Fatalities</td>
<%	}//if
	if (oBean.flagQ318()) {
%>
						<td align="center">Q#14.1.1</td>
<%	}//if
	if (oBean.flagQ1385()) {
%>
						<td align="center">Q#27.1.1</td>
<%	}//if%>
					</tr>
					<tr bgcolor="#003366" class="whiteTitle" align="center">
						<td>Contractor</td>
						<td>Location</td>
<%	if (oBean.flagEmr()) {
		if (!oBean.isEmrTimeAverage()) {
%>
						<td><%=thisYear - 1%></td>
						<td><%=thisYear - 2%></td>
						<td><%=thisYear - 3%></td>
<%		} else {%>
						<td><nobr>3 Yr Avg</nobr></td>
<%		}//else
	}//if
	if (oBean.flagLwcr()) {
		if (!oBean.isLwcrTimeAverage()) {
%>
						<td><%=thisYear - 1%></td>
						<td><%=thisYear - 2%></td>
						<td><%=thisYear - 3%></td>
<%		} else {%>
						<td><nobr>3 Yr Avg</nobr></td>
<%		}//else
	}//if
	if (oBean.flagTrir()) {
		if (!oBean.isTrirTimeAverage()) {
%>
						<td><%=thisYear - 1%></td>
						<td><%=thisYear - 2%></td>
						<td><%=thisYear - 3%></td>
<%		} else {%>
						<td><nobr>3 Yr Avg</nobr></td>
<%		}//else
	}//if
	if (oBean.flagFatalities()) {
%>
						<td><nobr>3 Yr Total</nobr></td>
<%	}//if
	if (oBean.flagQ318()) {
%>
						<td>Answer</td>
<%	}//if
	if (oBean.flagQ1385()) {
%>
						<td>Answer</td>
<%	}//if%>
					</tr>
<%	while (sBean.isNextRecord()) {
		String thisClass = ContractorBean.getTextColor(sBean.cBean.calcPICSStatusForOperator(pBean.oBean));
%>
					<tr <%=sBean.getBGColor()%> class="<%=thisClass%>">
						<td><a href="ContractorView.action?id=<%=sBean.aBean.id%>"
							title="view <%=sBean.aBean.name%> details" class="<%=thisClass%>"><%=sBean.aBean.name%></a></td>
						<td><%=sBean.osBean.getLocationDescription()%></td>
<%		if (oBean.flagEmr()) {
			if (!oBean.isEmrTimeAverage()) {
%>
						<td align="center"><%=sBean.oBean.getRedFlagNoZeros(sBean.emr1,oBean.emrHurdle)%></td>
						<td align="center"><%=sBean.oBean.getRedFlagNoZeros(sBean.emr2,oBean.emrHurdle)%></td>
						<td align="center"><%=sBean.oBean.getRedFlagNoZeros(sBean.emr3,oBean.emrHurdle)%></td>
<%			} else {%>
						<td align="center"><%=sBean.oBean.getRedFlagNoZeros(sBean.emrAve,oBean.emrHurdle)%></td>
<%			}//else
		}//if
		if (oBean.flagLwcr()) {
			if (!oBean.isLwcrTimeAverage()) {
%>
						<td align="center"><%=sBean.oBean.getRedFlag(sBean.osBean.calcRate(OSHABean.LOST_WORK_CASES,OSHABean.YEAR1), oBean.lwcrHurdle)%></td>
						<td align="center"><%=sBean.oBean.getRedFlag(sBean.osBean.calcRate(OSHABean.LOST_WORK_CASES,OSHABean.YEAR2), oBean.lwcrHurdle)%></td>
						<td align="center"><%=sBean.oBean.getRedFlag(sBean.osBean.calcRate(OSHABean.LOST_WORK_CASES,OSHABean.YEAR3), oBean.lwcrHurdle)%></td>
<%			} else {%>
						<td align="center"><%=sBean.oBean.getRedFlag(sBean.osBean.calcAverageRate(OSHABean.LOST_WORK_CASES),oBean.lwcrHurdle)%></td>
<%			}//else
		}//if
		if (oBean.flagTrir()) {
			if (!oBean.isTrirTimeAverage()) {
%>
						<td align="center"><%=sBean.oBean.getRedFlag(sBean.osBean.calcRate(OSHABean.RECORDABLE_TOTAL,OSHABean.YEAR1), oBean.trirHurdle)%></td>
						<td align="center"><%=sBean.oBean.getRedFlag(sBean.osBean.calcRate(OSHABean.RECORDABLE_TOTAL,OSHABean.YEAR2), oBean.trirHurdle)%></td>
						<td align="center"><%=sBean.oBean.getRedFlag(sBean.osBean.calcRate(OSHABean.RECORDABLE_TOTAL,OSHABean.YEAR3), oBean.trirHurdle)%></td>
<%			} else {%>
						<td align="center"><%=sBean.oBean.getRedFlag(sBean.osBean.calcAverageRate(OSHABean.RECORDABLE_TOTAL),oBean.trirHurdle)%></td>
<%			}//else
		}//if
		if (oBean.flagFatalities()) {
%>
						<td align="center"><%=sBean.oBean.getRedFlag(sBean.osBean.calcTotalStat(OSHABean.FATALITIES),oBean.fatalitiesHurdle)%></td>
<%		}//if
		if (oBean.flagQ318()) {
%>
						<td align="center"><%=sBean.oBean.getRedFlagAnswerNo(sBean.q318)%></td>
<%		}//if
		if (oBean.flagQ1385()) {
%>
						<td align="center"><%=sBean.oBean.getRedFlagAnswerNo(sBean.q1385)%></td>
<%		}//if%>
					</tr>
<%	} // while%>
				</table>
				<br>
				<center><%=sBean.getLinks()%></center>
<%	sBean.closeSearch();%>
		<center><%@ include file="utilities/contractor_key.jsp"%></center>
</body>
</html>
<%	}finally{
		sBean.closeSearch();
	}//finally
%>