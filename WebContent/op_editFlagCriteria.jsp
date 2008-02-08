<%@ page language="java" import="com.picsauditing.PICS.*,com.picsauditing.PICS.redFlagReport.*"  errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp" %>
<%@ include file="utilities/adminGeneral_secure.jsp"%>
<%	HurdleQuestions hurdleQuestions = null;
try{
	com.picsauditing.PICS.redFlagReport.FlagCriteria flagCriteria = new com.picsauditing.PICS.redFlagReport.FlagCriteria();
	boolean isSubmitted = "Save".equals(request.getParameter("action"));
	boolean canEditFlagCriteria = (pBean.isOperator() || pBean.isCorporate()) && pBean.getPermissions().hasPermission(com.picsauditing.access.OpPerms.EditFlagCriteria);
	String flagStatus = request.getParameter("flagStatus");
	if (null==flagStatus)
		flagStatus = "Red";
	flagCriteria.setFromDB(pBean.userID,flagStatus);
	if (isSubmitted){
		flagCriteria.setFromRequest(request);
		if (flagCriteria.isOK()){
			flagCriteria.writeToDB();
			FlagCalculator flagCalculator = new FlagCalculator();
			flagCalculator.recalculateFlags(pBean.userID);
		}//if
	}//if
	boolean isRecalcAll = "Recalculate For All Operators".equals(request.getParameter("action"));
	if (isRecalcAll && pBean.isAdmin()){
		FlagCalculator flagCalculator = new FlagCalculator();
		FACILITIES.setFacilitiesFromDB();
		for(String fname: FACILITIES.nameMap.keySet())
			flagCalculator.recalculateFlags(fname);
	}//if
%>
<html>
<head>
  <title>PICS - Pacific Industrial Contractor Screening</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <META Http-Equiv="Cache-Control" Content="no-cache">
  <META Http-Equiv="Pragma" Content="no-cache">
  <META Http-Equiv="Expires" Content="0">
  <link href="PICS.css" rel="stylesheet" type="text/css">
  <script language="JavaScript" SRC="js/ImageSwap.js"></script>
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
          <td width="147"><%@ include file="utilities/rightUpperNav.jsp"%></td>
          <td width="50%" bgcolor="#993300">&nbsp;</td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td valign="top" align="center"><img src="images/header_reports.gif" width="321" height="72"></td>
          <td valign="top"><%@ include file="utilities/rightLowerNav.jsp"%></td>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td colspan="3" align="center" class="blueMain">
              <%@ include file="includes/selectReport.jsp"%>
              <br><span class="redMain"><%=flagCriteria.getErrorMessages()%></span>
            <form name="changeFlagStatus" method="post" action="op_editFlagCriteria.jsp">
              Edit <%=flagCriteria.getFlagStatusSelect("flagStatus","forms",flagStatus)%> Flag Criteria<br>
            </form>
            <form name="form1" method="post" action="op_editFlagCriteria.jsp">
              <table width="586" border="0" cellpadding="2" cellspacing="1">
                <tr class="whiteTitle" bgcolor="#003366">
                  <td align="center"> <%=flagStatus%> Flag OSHA Questions</td>
                  <td align="center">Cutoff</td>
                  <td align="center">Statistic</td>
                  <td width="62" align="center">Include?</td>
                </tr>
<!--                   <tr class="blueMain" bgcolor="FFFFFF">
                    <td width="263" align="right" bgcolor="FFFFFF"><nobr>Experience Modification Rate (EMR) Cutoff:</nobr></td>
                    <td width="30" bgcolor="FFFFFF"><input name="emrHurdle" type="text" class="forms" size="5" value=<%//=flagCriteria.flagOshaCriteriaDO.emrHurdle%>></td>
                    <td width="67" bgcolor="FFFFFF">Timeframe:</td>
                    <td width="200" bgcolor="FFFFFF"><%//=hurdleRates.getTimeRadio("emrTime","forms",flagCriteria.flagOshaCriteriaDO.emrTime)%></td>
                    <td align="center"><%//=Inputs.getCheckBoxInput("flagEmr","forms",flagCriteria.flagOshaCriteriaDO.flagEmr,"Yes")%></td>
                  </tr>
 -->                <tr class="blueMain" bgcolor="FFFFFF">
                  <td align="right"><nobr>Lost Workdays Case Rate (LWCR):</nobr></td>
                  <td align="center"><input name="lwcrHurdle" type="text" class="forms" size="5" value=<%=flagCriteria.flagOshaCriteriaDO.lwcrHurdle%>></td>
                  <td><nobr><%=flagCriteria.getTimeRadio("lwcrTime","forms",flagCriteria.flagOshaCriteriaDO.lwcrTime)%></nobr></td>
                  <td align="center"><%=Inputs.getCheckBoxInput("flagLwcr","forms",flagCriteria.flagOshaCriteriaDO.flagLwcr,"Yes")%></td>
                </tr>
                <tr class="blueMain">
                  <td align="right">Total Recordable Incident Rate (TRIR):</td>
                  <td align="center"><input name="trirHurdle" type="text" class="forms" size="5" value=<%=flagCriteria.flagOshaCriteriaDO.trirHurdle%>></td>
                  <td><%=flagCriteria.getTimeRadio("trirTime","forms",flagCriteria.flagOshaCriteriaDO.trirTime)%></td>
                  <td align="center"><%=Inputs.getCheckBoxInput("flagTrir","forms",flagCriteria.flagOshaCriteriaDO.flagTrir,"Yes")%></td>
                </tr>
                <tr class="blueMain" bgcolor="FFFFFF">
                  <td align="right">Fatalities:</td>
                  <td align="center"><input name="fatalitiesHurdle" type="text" class="forms" size="5" value=<%=flagCriteria.flagOshaCriteriaDO.fatalitiesHurdle%>></td>
                  <td align="center"><input type=hidden name=fatalitiesTime value=1>Individual Yrs</td>
                  <td align="center"><%=Inputs.getCheckBoxInput("flagFatalities","forms",flagCriteria.flagOshaCriteriaDO.flagFatalities,"Yes")%></td>
                </tr>
              </table>
              <br>
<%	if(canEditFlagCriteria){%>
              <input name="action" type="submit" class="forms" value="Save">
<% 	}//if%>
              <table width="586" border="0" cellpadding="2" cellspacing="1">
                <tr class="whiteTitle" bgcolor="#003366">
                  <td align="center" colspan=2> <%=flagStatus%> Flag PQF Questions</td>
                  <td align="center" valign="middle">Flag Test
                   <a href="help.htm#RFRFlagTest" title="Help" target="_blank">&nbsp;
                     <img src="images/help.gif" alt="Help" width=12 height=12 border=0>
                   </a>
                  </td>
                  <td width="62" align="center">Include?</td>
                </tr>
<%	hurdleQuestions = new HurdleQuestions();
	hurdleQuestions.setEmrAveQuestion();
%>
                <tr class="blueMain" bgcolor="#FFFFFF">
                  <td align="right"></td>
                  <td align="left"><%=hurdleQuestions.question%></td>
                  <td align="right"><nobr>
                    <%=hurdleQuestions.getComparisonInput(flagCriteria.getComparisonFromMap(hurdleQuestions.questionID))%>
                    <%=hurdleQuestions.getValueInput(flagCriteria.getValueFromMap(hurdleQuestions.questionID))%>
                  </nobr></td>
                  <td align="center">
                    <input type=hidden name=hurdleQuestion_<%=hurdleQuestions.questionID%>>
                    <input type=hidden name=hurdleTypeQ_<%=hurdleQuestions.questionID%> value="<%=hurdleQuestions.questionType%>">
                    <%=Inputs.getCheckBoxInput("flagQ_"+hurdleQuestions.questionID,"forms",flagCriteria.getIsCheckedFromMap(hurdleQuestions.questionID),"Yes")%>
                  </td>
                </tr>
<%	hurdleQuestions.setList();
	while (hurdleQuestions.isNext()){
%>
                <tr class="blueMain" <%=Utilities.getBGColor(hurdleQuestions.count)%>>
                  <td align="right"><%=hurdleQuestions.catNum%>.<%=hurdleQuestions.subCatNum%>.<%=hurdleQuestions.questionNum%></td>
                  <td align="left"><%=hurdleQuestions.question%></td>
                  <td align="right"><nobr>
                    <%=hurdleQuestions.getComparisonInput(flagCriteria.getComparisonFromMap(hurdleQuestions.questionID))%>
                    <%=hurdleQuestions.getValueInput(flagCriteria.getValueFromMap(hurdleQuestions.questionID))%>
                  </nobr></td>
                  <td align="center">
                    <input type=hidden name=hurdleQuestion_<%=hurdleQuestions.questionID%>>
                    <input type=hidden name=hurdleTypeQ_<%=hurdleQuestions.questionID%> value="<%=hurdleQuestions.questionType%>">
                    <%=Inputs.getCheckBoxInput("flagQ_"+hurdleQuestions.questionID,"forms",flagCriteria.getIsCheckedFromMap(hurdleQuestions.questionID),"Yes")%>
                  </td>
                </tr>
<%	}//while %>
                <tr class="blueMain">
                  <td align="center" colspan="5">&nbsp;</td>
                </tr>
                <tr class="blueMain">
                  <td align="center" colspan="5">
                    <input name="flagStatus" type="hidden" value="<%=flagStatus%>">
<%	if(canEditFlagCriteria){%>
                     <input name="action" type="submit" class="forms" value="Save">
<% 	}//if%>
                  </td>
                </tr>
              </table>
<%	if(pBean.isAdmin()){%>
              <br><br><input name="action" type="submit" class="forms" value="Recalculate For All Operators">
<% 	}//if%>
            </form>
            <br>
          </td>
          <td>&nbsp;</td>
        </tr>
      </table>
      <br>
    </td>
  </tr>
</table>
</body>
</html>
<%}finally{
	if (null != hurdleQuestions)
		hurdleQuestions.closeList();
}//finally
 %>