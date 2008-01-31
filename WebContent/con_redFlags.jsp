<%//@ page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@ page language="java" import="com.picsauditing.PICS.*,com.picsauditing.PICS.redFlagReport.*"%>
<%@ include file="utilities/adminGeneral_secure.jsp"%>

<%
Note note = new Note();
HurdleQuestions hurdleQuestions = null;
AccountBean aBean = new AccountBean();
ContractorBean cBean = new ContractorBean();
try{
	String action = request.getParameter("action");
	boolean addNote = "Add Note".equals(action);
	boolean canEditNotes = (pBean.isOperator() || pBean.isCorporate()) && pBean.getPermissions().hasPermission(com.picsauditing.access.OpPerms.EditNotes);
	boolean deleteNote = "DeleteNote".equals(action);
	boolean addToList = "Add".equals(action);
	boolean canEditForcedFlags = (pBean.isOperator() || pBean.isCorporate()) && pBean.getPermissions().hasPermission(com.picsauditing.access.OpPerms.EditForcedFlags);
	boolean removeFromList = "Remove".equals(action);
	String id = request.getParameter("id");
	aBean.setFromDB(id);
	cBean.setFromDB(id);
	int rowCount = 0;
	FlagCalculator flagCalculator = new FlagCalculator();
	ForcedFlagListDO forcedFlagListDO = new ForcedFlagListDO();
	int currentYear = DateBean.getCurrentYear(this.getServletContext());
	int currentYearGrace = DateBean.getCurrentYearGrace(this.getServletContext());
	flagCalculator.setCurrentYear(currentYear, currentYearGrace);

	flagCalculator.setConFlags(id,pBean.userID);

	if (addNote && canEditNotes){
		String newNote = request.getParameter("newNote");
		if (null != newNote && !"".equals(newNote)){
			note = new Note(pBean.userID, id, pBean.uBean.name, newNote);
			note.writeToDB();
			response.sendRedirect("con_redFlags.jsp?id="+id);
			return;
		}//if
	}//if
	if (deleteNote && canEditNotes){
		String deleteID = request.getParameter("dID");
		new Note().deleteNote(deleteID,pBean.uBean.name);
		response.sendRedirect("con_redFlags.jsp?id="+id);
		return;
	}//if
	if (addToList && canEditForcedFlags){
		String flagStatus = request.getParameter("flagStatus");
		String expirationDate = request.getParameter("expirationDate");
		forcedFlagListDO = new ForcedFlagListDO(pBean.userID,id,flagStatus,expirationDate);
		if (forcedFlagListDO.isOK()){
			forcedFlagListDO.writeToDB();
			note = new Note(pBean.userID, id, pBean.uBean.name, aBean.name+" added to Forced "+flagStatus+" Flag List until "+expirationDate);
			note.writeToDB();
			response.sendRedirect("con_redFlags.jsp?id="+id);
			return;
		}//if
	}//if
	if (removeFromList && canEditForcedFlags){
		String flagStatus = request.getParameter("flagStatus");
		forcedFlagListDO.deleteFromDB(pBean.userID,id,flagStatus);
		note = new Note(pBean.userID, id, pBean.uBean.name, aBean.name+" removed from Forced "+flagStatus+" Flag List");
		note.writeToDB();
		response.sendRedirect("con_redFlags.jsp?id="+id);
		return;
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
  <script language="JavaScript" SRC="js/CalendarPopup.js"></script>
  <script language="JavaScript">document.write(getCalendarStyles());</script>
  <script language="JavaScript" id="js1">var cal1 = new CalendarPopup();</script>
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
          <td valign="top" align="center"><img src="images/header_contractorDetails.gif" width="321" height="72" border="0"></td>
          <td valign="top"><%@ include file="utilities/rightLowerNav.jsp"%></td>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td colspan="3" align="center" class="blueMain">
			<table width="657" border="0" cellpadding="0" cellspacing="0">
              <tr align="center" class="blueMain">
<%	if (!pBean.getPermissions().hasPermission(com.picsauditing.access.OpPerms.StatusOnly)){ %>
                <td align="left"><%@ include file="includes/nav/secondNav.jsp"%></td>
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
<%		}//if
	hurdleQuestions.setList();
	while (hurdleQuestions.isNext()){
		if (flagCalculator.qIDToAnswerMap.keySet().contains(hurdleQuestions.questionID)){
%>              <tr class="blueMain" <%=Utilities.getBGColor(rowCount++)%>>
                <td align="right"><%=hurdleQuestions.catNum%>.<%=hurdleQuestions.subCatNum%>.<%=hurdleQuestions.questionNum%></td>
                <td align="left"><%=hurdleQuestions.question%></td>
                <td align="center"><%=flagCalculator.getAnswer(hurdleQuestions.questionID)%></td>
                <td align="center"><%=flagCalculator.getFlagIcon(hurdleQuestions.questionID)%></td>
              </tr>
<%		}//if
	}//while
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
              <tr>
                <td class="whiteTitle" bgcolor="#003366" align="center" colspan=2>Notes
                  <a href="help.htm#RFRNotes" title="Help" target="_blank">&nbsp;
                    <img src="images/help.gif" alt="Help" width=12 height=12 border=0>
                  </a>
                </td>
              </tr>
<%	if(canEditNotes){%>
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
<%	}//if %>
              <tr>
                <td class="redMain">Notes:</td>
                <td></td>
              </tr>
<%	note = new Note();
	note.setList(pBean.userID,id);
	while (note.isNext()){
%>
              <tr <%=Utilities.getBGColor(note.count)%>>
                <td class=blueMain><%=note.getNoteDisplay()%></td>
                <td class=blueMain>
<%		if(canEditNotes){%>
                  <a href="?id=<%=id%>&action=DeleteNote&dID=<%=note.noteID%>">Delete</a>
<%		}//if%>
                </td>
              </tr>
<%	}//while%>
                </td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
      <br>
    </td>
  </tr>
  <tr>
    <td height="72" align="center" bgcolor="#003366" class="copyrightInfo">&copy;2007 
      Pacific Industrial Contractor Screening | site design: <a href="http://www.albumcreative.com" title="Album Creative Studios"><font color="#336699">ACS</font></a>
	</td>
  </tr>
</table>
<%@ include file="includes/statcounter.jsp" %>
</body>
</html>
<%}finally{
	if (null != hurdleQuestions)
		hurdleQuestions.closeList();
	note.closeList();
}//finally
%>