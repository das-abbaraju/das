<%@ page language="java" import="com.picsauditing.PICS.*, com.picsauditing.PICS.redFlagReport.*"%>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope="page"/>
<jsp:useBean id="FACILITIES" class="com.picsauditing.PICS.Facilities" scope="application"/>
<%
// Unsecured (no login required) webpage that can run scheduled tasks on a regular basis

aBean.optimizeDB();
%>AccountBean.optimizeDB();<%

FACILITIES.setFacilitiesFromDB();
%>Facilities.setFacilitiesFromDB();<%

new Billing().updateAllPayingFacilities(FACILITIES, application);
%>Billing.updateAllPayingFacilities();<%

FlagCalculator flagCalculator = new FlagCalculator();
int currentYear = DateBean.getCurrentYear(this.getServletContext());
int currentYearGrace = DateBean.getCurrentYearGrace(this.getServletContext());
flagCalculator.setCurrentYear(currentYear, currentYearGrace);

for (String opID: FACILITIES.nameMap.keySet()) {
	%>FlagCalculator.recalculateFlags(<%=opID%>);<%
	flagCalculator.recalculateFlags(opID);
}

%>