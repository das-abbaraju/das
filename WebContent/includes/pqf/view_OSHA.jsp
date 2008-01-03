<jsp:useBean id="oBean" class="com.picsauditing.PICS.OSHABean" scope ="page"/>
<%
	String cID = request.getParameter("id");
	if (cID == null || "".equals(cID))
		cID = "0";

//	int curYear = com.picsauditing.PICS.DateBean.getCurrentYear();	
	int curYear = com.picsauditing.PICS.DateBean.getCurrentYear(this.getServletContext().getInitParameter("currentYearStart"));
	oBean.setListFromDB(cID);
	oBean.setShowLinks(pBean);
	int count = 1;
	while (oBean.hasNext() || 1==count) {
	String descriptionText = "Recordable";
	if ("MSHA".equals(oBean.SHAType))
		descriptionText = "Reportable";
%>
  <tr class="blueMain">
    <td bgcolor="#003366" colspan="2" align="center">
      <font color="#FFFFFF"><strong>Sub Category <%=count%> - <%=oBean.SHAType%> Injury and Illness Data for location: <%=oBean.getLocationDescription()%></strong></font>
    </td>
  </tr>
  <tr align="center" class="blueMain"> 
    <td colspan="2">
      <table border="1" cellpadding="5" cellspacing="0" bordercolor="#FFFFFF" class="blueMain">
        <tr>
          <td>
            Location: <strong><%=oBean.getLocationDescription()%></strong><%=oBean.getDateVerifiedView()%>
<%		if (!"".equals(oBean.OID) && (pBean.isAdmin() || pBean.isAuditor())) { %>
            <br/><a href="pqf_OSHA.jsp?id=<%=conID%>&catID=<%=catID%>&oID=<%=oBean.OID%>&action=Verify&auditType=<%=com.picsauditing.PICS.pqf.Constants.PQF_TYPE%>">Click here to certify this data as verified</a>
<%		}//if %>
          </td>
          <td colspan="8" align="center">
<%		if (!"".equals(oBean.OID) && (pBean.isAdmin() || cID.equals(pBean.userID) || (pBean.isAuditor() && pBean.canVerifyAudit(com.picsauditing.PICS.pqf.Constants.PQF_TYPE,cID)))){%>
			<a href="pqf_OSHA.jsp?action=Edit&id=<%=conID%>&catID=<%=catID%>&oID=<%=oBean.OID%>">Click here to edit this location</a>
<%		}//if %>
		  </td>
        </tr>
        <tr>
          <td>Year</td>
          <td colspan="2" class="redMain"><%=curYear-1%></td>
          <td colspan="2" class="redMain"><%=curYear-2%></td>
          <td colspan="2" class="redMain"><%=curYear-3%></td>
          <td colspan="2" class="redMain">3 Yr Average</td>
        </tr>
        <tr>
          <td class="redMain">Total Man Hours Worked</td>
          <td colspan="2"><%=oBean.getStat(oBean.MAN_HOURS, oBean.YEAR1)%></td>
          <td colspan="2"><%=oBean.getStat(oBean.MAN_HOURS, oBean.YEAR2)%></td>
          <td colspan="2"><%=oBean.getStat(oBean.MAN_HOURS, oBean.YEAR3)%></td>
          <td colspan="2"><%=oBean.calcAverageStat(oBean.MAN_HOURS)%></td>
        </tr>
        <tr>
          <td class="redMain">&nbsp;</td>
          <td>No.</td>
          <td>Rate</td>
          <td>No.</td>
          <td>Rate</td>
          <td>No.</td>
          <td>Rate</td>
          <td>No.</td>
          <td>Rate</td>
        </tr>
        <tr>
          <td class="redMain">Number of Fatalities</td>
          <td><%=oBean.getStat(oBean.FATALITIES, oBean.YEAR1)%></td>
          <td><%=oBean.calcRate(oBean.FATALITIES,oBean.YEAR1)%></td>
          <td><%=oBean.getStat(oBean.FATALITIES, oBean.YEAR2)%></td>
          <td><%=oBean.calcRate(oBean.FATALITIES,oBean.YEAR2)%></td>
          <td><%=oBean.getStat(oBean.FATALITIES, oBean.YEAR3)%></td>
          <td><%=oBean.calcRate(oBean.FATALITIES,oBean.YEAR3)%></td>
          <td><%=oBean.calcAverageStat(oBean.FATALITIES)%></td>
          <td><%=oBean.calcAverageRate(oBean.FATALITIES)%></td>
        </tr>
        <tr>
          <td class="redMain">Number of Lost Workday Cases - Has lost days AND is <%=oBean.SHAType%> <%=descriptionText%></td>
          <td><%=oBean.getStat(oBean.LOST_WORK_CASES, oBean.YEAR1)%></td>
          <td><%=oBean.calcRate(oBean.LOST_WORK_CASES,oBean.YEAR1)%></td>
          <td><%=oBean.getStat(oBean.LOST_WORK_CASES, oBean.YEAR2)%></td>
          <td><%=oBean.calcRate(oBean.LOST_WORK_CASES,oBean.YEAR2)%></td>
          <td><%=oBean.getStat(oBean.LOST_WORK_CASES, oBean.YEAR3)%></td>
          <td><%=oBean.calcRate(oBean.LOST_WORK_CASES,oBean.YEAR3)%></td>
          <td><%=oBean.calcAverageStat(oBean.LOST_WORK_CASES)%></td>
          <td><%=oBean.calcAverageRate(oBean.LOST_WORK_CASES)%></td>
        </tr>
        <tr>
          <td class="redMain">Number of Lost Workdays - All lost workdays (regardless of restricted days) AND is <%=oBean.SHAType%> <%=descriptionText%></td>
          <td><%=oBean.getStat(oBean.LOST_WORK_DAYS, oBean.YEAR1)%></td>
          <td><%=oBean.calcRate(oBean.LOST_WORK_DAYS,oBean.YEAR1)%></td>
          <td><%=oBean.getStat(oBean.LOST_WORK_DAYS, oBean.YEAR2)%></td>
          <td><%=oBean.calcRate(oBean.LOST_WORK_DAYS,oBean.YEAR2)%></td>
          <td><%=oBean.getStat(oBean.LOST_WORK_DAYS, oBean.YEAR3)%></td>
          <td><%=oBean.calcRate(oBean.LOST_WORK_DAYS,oBean.YEAR3)%></td>
          <td><%=oBean.calcAverageStat(oBean.LOST_WORK_DAYS)%></td>
          <td><%=oBean.calcAverageRate(oBean.LOST_WORK_DAYS)%></td>
        </tr>
        <tr>
          <td class="redMain">Injury & Illnesses Medical Cases - No lost OR restricted days AND is <%=oBean.SHAType%> <%=descriptionText%> (non-fatal)</td>
          <td><%=oBean.getStat(oBean.INJURY_ILLNESS_CASES, oBean.YEAR1)%></td>
          <td><%=oBean.calcRate(oBean.INJURY_ILLNESS_CASES,oBean.YEAR1)%></td>
          <td><%=oBean.getStat(oBean.INJURY_ILLNESS_CASES, oBean.YEAR2)%></td>
          <td><%=oBean.calcRate(oBean.INJURY_ILLNESS_CASES,oBean.YEAR2)%></td>
          <td><%=oBean.getStat(oBean.INJURY_ILLNESS_CASES, oBean.YEAR3)%></td>
          <td><%=oBean.calcRate(oBean.INJURY_ILLNESS_CASES,oBean.YEAR3)%></td>
          <td><%=oBean.calcAverageStat(oBean.INJURY_ILLNESS_CASES)%></td>
          <td><%=oBean.calcAverageRate(oBean.INJURY_ILLNESS_CASES)%></td>
        </tr>
        <tr>
          <td class="redMain">Restricted Cases - Has restricted days AND no lost days AND is <%=oBean.SHAType%> <%=descriptionText%></td>
          <td><%=oBean.getStat(oBean.RESTRICTED_WORK_CASES, oBean.YEAR1)%></td>
          <td><%=oBean.calcRate(oBean.RESTRICTED_WORK_CASES,oBean.YEAR1)%></td>
          <td><%=oBean.getStat(oBean.RESTRICTED_WORK_CASES, oBean.YEAR2)%></td>
          <td><%=oBean.calcRate(oBean.RESTRICTED_WORK_CASES,oBean.YEAR2)%></td>
          <td><%=oBean.getStat(oBean.RESTRICTED_WORK_CASES, oBean.YEAR3)%></td>
          <td><%=oBean.calcRate(oBean.RESTRICTED_WORK_CASES,oBean.YEAR3)%></td>
          <td><%=oBean.calcAverageStat(oBean.RESTRICTED_WORK_CASES)%></td>
          <td><%=oBean.calcAverageRate(oBean.RESTRICTED_WORK_CASES)%></td>
        </tr>
        <tr>
          <td class="redMain">Total <%=oBean.SHAType%> <%=descriptionText%> Injuries and Illnesses</td>
          <td><%=oBean.getStat(oBean.RECORDABLE_TOTAL, oBean.YEAR1)%></td>
          <td><%=oBean.calcRate(oBean.RECORDABLE_TOTAL,oBean.YEAR1)%></td>
          <td><%=oBean.getStat(oBean.RECORDABLE_TOTAL, oBean.YEAR2)%></td>
          <td><%=oBean.calcRate(oBean.RECORDABLE_TOTAL,oBean.YEAR2)%></td>
          <td><%=oBean.getStat(oBean.RECORDABLE_TOTAL, oBean.YEAR3)%></td>
          <td><%=oBean.calcRate(oBean.RECORDABLE_TOTAL,oBean.YEAR3)%></td>
          <td><%=oBean.calcAverageStat(oBean.RECORDABLE_TOTAL)%></td>
          <td><%=oBean.calcAverageRate(oBean.RECORDABLE_TOTAL)%></td>
        </tr>
        <tr>
        	<td class="redMain">Were you exempt from submitting OSHA Logs?</td>
        	<td colspan="2" align="center"><%=oBean.isNa1()? "Yes" : "No" %></td>
           	<td colspan="2" align="center"><%=oBean.isNa2()? "Yes" : "No" %></td>
            <td colspan="2" align="center"><%=oBean.isNa3()? "Yes" : "No" %></td>
        </tr>
        <tr>
          <td class="redMain"><%=oBean.SHAType%> Log Files
<%		if (!"".equals(oBean.OID) && (pBean.isAdmin() || cID.equals(pBean.userID) || (pBean.isAuditor() && pBean.canVerifyAudit(com.picsauditing.PICS.pqf.Constants.PQF_TYPE,cID)))) { %>
            <a href="pqf_OSHA.jsp?action=Edit&id=<%=conID%>&catID=<%=catID%>&oID=<%=oBean.OID%>">Click here to edit this location</a>
<%		}//if %>		  </td>
          
          <td colspan="2" align="center"><%=oBean.getFile1YearAgoLink()%></td>
          <td colspan="2" align="center"><%=oBean.getFile2YearAgoLink()%></td>
          <td colspan="2" align="center"><%=oBean.getFile3YearAgoLink()%></td>
          <td colspan="2">&nbsp;</td>
        </tr>
      </table>
    </td>
  </tr>
<%	count++;
	}//while%>