<%@ page language="java" import="com.picsauditing.PICS.*,com.picsauditing.PICS.redFlagReport.*" errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp" %>
<%@page import="java.util.List"%>
<%@page import="org.apache.commons.beanutils.BasicDynaBean"%>
<%
Note note = new Note();
HurdleQuestions hurdleQuestions = null;
AccountBean aBean = new AccountBean();
ContractorBean cBean = new ContractorBean();
OperatorBean oBean = new OperatorBean();
try{
	String action = request.getParameter("action");
	boolean addNote = "Add Note".equals(action);
	boolean deleteNote = "DeleteNote".equals(action);
	boolean addToList = "Add".equals(action);
	boolean removeFromList = "Remove".equals(action);
	
	boolean canEditNotes = permissions.hasPermission(com.picsauditing.access.OpPerms.EditNotes);
	boolean canEditForcedFlags = permissions.hasPermission(com.picsauditing.access.OpPerms.EditForcedFlags);
	String id = request.getParameter("id");
	String opID = request.getParameter("opID");
	if (null == opID)
		opID = permissions.getAccountIdString();
	oBean.setFromDB(opID);
	aBean.setFromDB(id);
	cBean.setFromDB(id);
	cBean.tryView(permissions);
	int rowCount = 0;
	FlagCalculator flagCalculator = new FlagCalculator();
	ForcedFlagListDO forcedFlagListDO = new ForcedFlagListDO();
	int currentYear = DateBean.getCurrentYear(this.getServletContext());
	int currentYearGrace = DateBean.getCurrentYearGrace(this.getServletContext());
	flagCalculator.setCurrentYear(currentYear, currentYearGrace);

	flagCalculator.setConFlags(id,opID);

	if (addNote && canEditNotes){
		String newNote = request.getParameter("newNote");
		if (null != newNote && !"".equals(newNote)){
			note = new Note(permissions.getAccountIdString(), id, permissions.getUserIdString(), permissions.getName(), newNote);
			note.writeToDB();
			response.sendRedirect("con_redFlags.jsp?id="+id);
			return;
		}//if
	}//if
	if (deleteNote && canEditNotes){
		String deleteID = request.getParameter("dID");
		new Note().deleteNote(deleteID,permissions);
		response.sendRedirect("con_redFlags.jsp?id="+id);
		return;
	}//if
	if (addToList && canEditForcedFlags){
		String flagStatus = request.getParameter("flagStatus");
		String expirationDate = request.getParameter("expirationDate");
		forcedFlagListDO = new ForcedFlagListDO(pBean.userID,id,flagStatus,expirationDate);
		if (forcedFlagListDO.isOK()){
			forcedFlagListDO.writeToDB();
			note = new Note(pBean.userID, id, permissions.getUserIdString(), permissions.getName(), aBean.name+" added to Forced "+flagStatus+" Flag List until "+expirationDate);
			note.writeToDB();
			response.sendRedirect("con_redFlags.jsp?id="+id);
			return;
		}//if
	}//if
	if (removeFromList && canEditForcedFlags){
		String flagStatus = request.getParameter("flagStatus");
		forcedFlagListDO.deleteFromDB(pBean.userID,id,flagStatus);
		note = new Note(pBean.userID, id, permissions.getUserIdString(), permissions.getName(), aBean.name+" removed from Forced "+flagStatus+" Flag List");
		note.writeToDB();
		response.sendRedirect("con_redFlags.jsp?id="+id);
		return;
	}//if
%>
<html>
<head>
<title>Red Flags</title>
  <script language="JavaScript" SRC="js/ImageSwap.js"></script>
  <script language="JavaScript" SRC="js/CalendarPopup.js"></script>
  <script language="JavaScript">document.write(getCalendarStyles());</script>
  <script language="JavaScript" id="js1">var cal1 = new CalendarPopup();</script>
</head>
<body>
			<table width="657" border="0" cellpadding="0" cellspacing="0">
              <tr align="center" class="blueMain">
<%	if (!permissions.hasPermission(com.picsauditing.access.OpPerms.StatusOnly)){ %>
                <td align="left">
                <h1><%=aBean.getName(id)%><span class="sub">Red Flags</span></h1>
                <%@ include file="utilities/adminOperatorContractorNav.jsp"%></td>
<%	}%>
              </tr>
            </table>
            Overall Status for <%=aBean.name%>: <%=flagCalculator.getFlagIcon()%>
<%	if(flagCalculator.isGreenFlagListed){ %>
            <br><span class="greenMain">This contractor is currently on the <strong>Forced Green Flag List</strong> until <strong><%=flagCalculator.dateExpires%></strong></span><br>
<%	}else if (flagCalculator.isAmberFlagListed){ %>
            <br><span class="amberMain">This contractor is currently on the <strong>Forced Amber Flag List</strong> until <strong><%=flagCalculator.dateExpires%></strong></span><br>
<%	}else if (flagCalculator.isRedFlagListed){ %>
<br><span class="redMain">This contractor is currently on the <strong>Forced Red Flag List</strong> until <strong><%=flagCalculator.dateExpires%></strong></span><br>
<%	}//if
	OSHABean osBean = new OSHABean();
	osBean.setListFromDB(id);
	osBean.setDuringGracePeriod(currentYear!=currentYearGrace);
	int count = 1;
	while (osBean.hasNext() || 1==count){
		count++;
%>
            <table width="657" border="0" cellpadding="2" cellspacing="1">
              <tr class="whiteTitle" bgcolor="#003366">
                <td align="center"><%=osBean.SHAType%> Statistic for location: <%=osBean.getLocationDescription()%></td>
                <td align="center" colspan=2><%=currentYearGrace - 1 %></td>
                <td align="center" colspan=2><%=currentYearGrace - 2 %></td>
                <td align="center" colspan=2><%=currentYearGrace - 3 %></td>
                <td align="center" colspan=2>3 Yr Avg</td>
              </tr>
<%		if (flagCalculator.redFlagOshaCriteriaDO.flagLwcr() || flagCalculator.amberFlagOshaCriteriaDO.flagLwcr()){%>
              <tr class="blueMain" <%=Utilities.getBGColor(rowCount++)%>>
                <td align="right"><nobr><%=osBean.SHAType%> Lost Workdays Case Rate (LWCR)</nobr></td>
                <td align="center"><%=flagCalculator.getOshaFlag(OSHABean.LOST_WORK_CASES,osBean.calcRate(OSHABean.LOST_WORK_CASES,OSHABean.YEAR1),"1")%></td>
                <td align="center"><%=flagCalculator.getOshaFlag(OSHABean.LOST_WORK_CASES,osBean.calcRate(OSHABean.LOST_WORK_CASES,OSHABean.YEAR2),"1")%></td>
                <td align="center"><%=flagCalculator.getOshaFlag(OSHABean.LOST_WORK_CASES,osBean.calcRate(OSHABean.LOST_WORK_CASES,OSHABean.YEAR3),"1")%></td>
                <td align="center"><%=flagCalculator.getOshaFlag(OSHABean.LOST_WORK_CASES,osBean.calcAverageRate(OSHABean.LOST_WORK_CASES),"3")%></td>
              </tr>
<%		}//if
		if (flagCalculator.redFlagOshaCriteriaDO.flagTrir() || flagCalculator.amberFlagOshaCriteriaDO.flagTrir()){
%>
              <tr class="blueMain" <%=Utilities.getBGColor(rowCount++)%>>
                <td align="right"><nobr><%=osBean.SHAType%> TRIR</nobr></td>
                <td align="center"><%=flagCalculator.getOshaFlag(OSHABean.RECORDABLE_TOTAL,osBean.calcRate(OSHABean.RECORDABLE_TOTAL,OSHABean.YEAR1),"1")%></td>
                <td align="center"><%=flagCalculator.getOshaFlag(OSHABean.RECORDABLE_TOTAL,osBean.calcRate(OSHABean.RECORDABLE_TOTAL,OSHABean.YEAR2),"1")%></td>
                <td align="center"><%=flagCalculator.getOshaFlag(OSHABean.RECORDABLE_TOTAL,osBean.calcRate(OSHABean.RECORDABLE_TOTAL,OSHABean.YEAR3),"1")%></td>
                <td align="center"><%=flagCalculator.getOshaFlag(OSHABean.RECORDABLE_TOTAL,osBean.calcAverageRate(OSHABean.RECORDABLE_TOTAL),"3")%></td>
              </tr>
<%		}//if
		if (flagCalculator.redFlagOshaCriteriaDO.flagFatalities() || flagCalculator.amberFlagOshaCriteriaDO.flagFatalities()){
%>
              <tr class="blueMain" <%=Utilities.getBGColor(rowCount++)%>>
                <td align="right"><nobr>Fatalities</nobr></td>
                <td align="center"><%=flagCalculator.getOshaFlag(OSHABean.FATALITIES,osBean.getStat(OSHABean.FATALITIES, OSHABean.YEAR1),"1")%></td>
                <td align="center"><%=flagCalculator.getOshaFlag(OSHABean.FATALITIES,osBean.getStat(OSHABean.FATALITIES, OSHABean.YEAR2),"1")%></td>
                <td align="center"><%=flagCalculator.getOshaFlag(OSHABean.FATALITIES,osBean.getStat(OSHABean.FATALITIES, OSHABean.YEAR3),"1")%></td>
                <td></td>
                <td></td>
              </tr>
<%		}//if%>
            </table><br>
<%	}//while%>
            <table width="657" border="0" cellpadding="2" cellspacing="1">
              <tr class="whiteTitle" bgcolor="#003366">
                <td width="498" align="center" colspan=2>Question</td>
                <td align="center" colspan=2>Answer</td>
              </tr>
<%	hurdleQuestions = new HurdleQuestions();
	hurdleQuestions.setEmrAveQuestion();
	flagCalculator.qIDToAnswerMap.keySet();
	if (flagCalculator.qIDToAnswerMap.keySet().contains(hurdleQuestions.questionID)){
%>
              <tr class="blueMain" <%=Utilities.getBGColor(rowCount++)%>>
                <td></td>
                <td align="left"><%=hurdleQuestions.question%></td>
                <td align="center"><%=flagCalculator.getAnswer(hurdleQuestions.questionID)%></td>
                <td align="center"><%=flagCalculator.getFlagIcon(hurdleQuestions.questionID)%></td>
              </tr>
<%	}//if
	hurdleQuestions.setList();
	while (hurdleQuestions.isNext()){
		if (flagCalculator.qIDToAnswerMap.keySet().contains(hurdleQuestions.questionID)){
%>              <tr class="blueMain" <%=Utilities.getBGColor(rowCount++)%>>
                <td align="right"><%=hurdleQuestions.catNum%>.<%=hurdleQuestions.subCatNum%>.<%=hurdleQuestions.questionNum%></td>
                <td align="left"><%=hurdleQuestions.question%></td>
                <td align="center"><%=flagCalculator.getAnswer(hurdleQuestions.questionID)%></td>
                <td align="center"><%=flagCalculator.getFlagIcon(hurdleQuestions.questionID)%></td>
              </tr>
<%		}
	}
%>
              <tr class="blueMain">
                <td align="right">&nbsp;</td>
              </tr>
            </table>
<%	if(canEditForcedFlags){%>
            <a name="forceFlagPart"></a>
            <br><br><strong><span class="redMain"><%=forcedFlagListDO.getErrorMessages()%></span></strong>
            <table width="657" border="0" cellpadding="2" cellspacing="1">
              <tr class="whiteTitle" bgcolor="#003366">
                <td colspan="5" align="center">Forced Flag List
                  <a href="help.htm#RFRForcedFlag" title="Help" target="_blank">&nbsp;
                    <img src="images/help.gif" alt="Help" width=12 height=12 border=0>
                  </a>                
                </td>
              </tr>
              <tr class="whiteTitle" bgcolor="#003366">
                <td align="center">List</td>
                <td align="center">Status</td>
                <td align="center">Expiration Date</td>
                <td></td>
                <td></td>
              </tr>
              <tr class="blueMain" bgcolor="FFFFFF">
              <form action="con_redFlags.jsp#forceFlagPart" method="post" name="greenListForm" id="greenListForm">
                <td align="left" bgcolor="FFFFFF">Forced <span class="greenMain">Green</span> Flag List:</td>
                <td align="left" bgcolor="FFFFFF" class="greenMain"><nobr>
<%		if(flagCalculator.isGreenFlagListed)
			out.println("On Forced Green Flag List until <strong>"+flagCalculator.dateExpires+"</strong>");
%>                </nobr></td>
                <td align="center">
                  <nobr><input id="greenExpirationDate" size="8" name="expirationDate" class="forms" />
                  <a href="#forceFlagPart" onClick="cal1.select(document.getElementById('greenExpirationDate'),'greenExpirationDate','M/d/yy',''); return false;"><img src="images/icon_calendar.gif" width="18" height="15" border="0" /></a>
                  </nobr>
                </td>
                <td align="center"><input class="forms" type="submit" name="action" value="Add"></td>
                <td align="center"><input class="forms" type="submit" name="action" value="Remove"></td>
                <input type="hidden" name="flagStatus" value="Green">
                <input type="hidden" name="id" value="<%=id%>">
              </form>
              </tr>
              <tr class="blueMain">
                <form action="con_redFlags.jsp#forceFlagPart" method="post" name="amberListForm" id="amberListForm">
                <td align="left">Forced <span class="amberMain">Amber</span> Flag List:</td>
                <td align="left" class="amberMain"><nobr>
<%		if(flagCalculator.isAmberFlagListed)
			out.println("On Forced Amber Flag List until <strong>"+flagCalculator.dateExpires+"</strong>");
%>                </nobr></td>
                <td align="center">
                  <nobr><input id="amberExpirationDate" size="8" name="expirationDate" class="forms">
                  <a href="#forceFlagPart" onClick="cal1.select(document.getElementById('amberExpirationDate'),'amberExpirationDate','M/d/yy',''); return false;"><img src="images/icon_calendar.gif" width="18" height="15" border="0" /></a>
                  </nobr>
                </td>
                <td align="center"><input class="forms" type="submit" name="action" value="Add"></td>
                <td align="center"><input class="forms" type="submit" name="action" value="Remove"></td>
                <input type="hidden" name="flagStatus" value="Amber">
                <input type="hidden" name="id" value="<%=id%>">
                </form>
              </tr>
              <tr class="blueMain" bgcolor="FFFFFF">
                <form action="con_redFlags.jsp#forceFlagPart" method="post" name="redListForm" id="redListForm">
                <td align="left">Forced <span class="redMain">Red</span> Flag List:</td>
                <td align="left" class="redMain">
<%		if(flagCalculator.isRedFlagListed)
			out.println("On Forced Red Flag List until <strong>"+flagCalculator.dateExpires+"</strong>");
%>                </td>
                <td align="center">
                  <input id="redExpirationDate" size="8" name="expirationDate" class="forms">
                  <a href="#" onClick="cal1.select(document.getElementById('redExpirationDate'),'redExpirationDate','M/d/yy',''); return false;"><img src="images/icon_calendar.gif" width="18" height="15" border="0" /></a>
                </td>
                <td align="center"><input class="forms" type="submit" name="action" value="Add"></td>
                <td align="center"><input class="forms" type="submit" name="action" value="Remove"></td>
                <input type="hidden" name="flagStatus" value="Red">
                <input type="hidden" name="id" value="<%=id%>">
                </form>
              </tr>
            </table>
<% 	}//if%>
            <a name="notesPart"></a>
            <br>
            <table width="657" border="0" cellpadding="2" cellspacing="1" bordercolor="FFFFFF">
<% 	if (!permissions.isContractor()){%>
              <tr>
                <td class="whiteTitle" bgcolor="#003366" align="center" colspan=2>Notes
                  <a href="help.htm#RFRNotes" title="Help" target="_blank">&nbsp;
                    <img src="images/help.gif" alt="Help" width=12 height=12 border=0>
                  </a>
                </td>
              </tr>
<%	}//if
	if(canEditNotes){
%>
              <tr>
                <td valign="top" colspan=2>
                  <form action="con_redFlags.jsp#notesPart" method="post" name="newNoteForm" id="newNoteForm">
                    <table border="0" cellspacing="1" cellpadding="1">
                      <tr>
                        <td class="redMain" align="left"><b>Enter a new note about <%=aBean.name%></b></td>
                      </tr>
                      <tr>
                        <td class="redMain" align="left">Date: <span class="blueMain"><%=com.picsauditing.PICS.DateBean.getTodaysDate()%></span></td>
                      </tr>
                      <tr>
                        <td class="redMain" align="left" valign="top">
                          New Note: <textarea name="newNote" cols="70" rows="5" class="forms" id="notes"></textarea>
                        </td>
                      </tr>
                      <tr>
                        <td align=right>
                          <input name="action" type="submit" class="buttons" value="Add Note">
                          <input name="id" type="hidden" value="<%=id%>">
                        </td>
                      </tr>
                    </table>
                  </form>
                </td>
              </tr>
<%	}//if
	if (!permissions.isContractor()){
%>
              <tr>
                <td class="redMain">Notes:</td>
                <td></td>
              </tr>
<%		List<BasicDynaBean> notesList = note.getContractorNotes(id,permissions);
		count = 0;
		for (BasicDynaBean row : notesList){
			count++;
%>
          <tr <%=Utilities.getBGColor(count)%>>
            <td class=blueMain><%=row.get("formattedDate")%> 
              (<%=row.get("whoIs")+","+FACILITIES.getNameFromID(row.get("opID").toString())%>)
              :<%=row.get("note")%>
            </td>
            <td class=blueMain>
<%			if(canEditNotes && row.get("opID").equals(permissions.getAccountIdString())){%>
              <a href="?id=<%=id%>&action=DeleteNote&dID=<%=row.get("noteID")%>">Delete</a>
<%			}//if%>
                </td>
              </tr>
<%		}//for
	}//if
%>
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