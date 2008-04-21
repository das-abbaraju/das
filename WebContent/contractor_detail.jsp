<%@ page language="java" import="com.picsauditing.PICS.redFlagReport.*" errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp" %>
<%
	AccountBean aBean = new AccountBean();
	ContractorBean cBean = new ContractorBean();
	
	String id = request.getParameter("id");
	cBean.setFromDB(id);
	cBean.tryView(permissions);
	
	aBean.setFromDB(id);
	FlagDO flag = new FlagDO();
%>
<html>
<head>
<title><%= aBean.name %></title>
<meta name="header_gif" content="header_contractorDetails.gif" />
<script language="JavaScript">
<!--
function MM_openBrWindow(theURL,winName,features) { //v2.0
  window.open(theURL,winName,features);
}//MM_openBrWindow

function MM_displayStatusMsg(msgStr) { //v1.0
  status=msgStr;
  document.MM_returnValue = true;
}//MM_displayStatusMsg

//checks logo size, if hieght or width greater than maxedge (240), larger of two is changed to maxedge
//BJ 9-23-04
  var imglogo = new Image();
  imglogo.src = "/logos/<%=cBean.getDisplayLogo_file()%> ";
  var logoheight = imglogo.height;
  var logowidth = imglogo.width;
  var sizetext = ' '; 
  var maxedge = 250;
  if ((logoheight < maxedge) && (logowidth <maxedge)) {
    sizetext = ' height=' + logoheight + ' width=' + logowidth;
  } else {
    if (logoheight > logowidth)
      sizetext = ' height=' + maxedge;
    else
	  sizetext = ' width=' + maxedge;
  }//else
-->
</script>
</head>
<body>
<a href="ContractorView.action?id=<%=id %>">New Page</a>
			<table width="657" border="0" cellpadding="0" cellspacing="0">
              <tr align="center" class="blueMain">
                <td align="left"><%@ include file="includes/nav/secondNav.jsp"%></td>
              </tr>
            </table>
			<table width="657" border="0" cellpadding="15" cellspacing="1" bgcolor="#F8F8F8">
              <tr bgcolor="#FFFFFF">
                <td width="647" bgcolor="#F8F8F8" class="blueMain">
				  <table width="100%" border="0" cellpadding="0" cellspacing="0">
                    <tr valign="top" class="blueMain">
                      <td>
					    <span class="blueHeader"><%=aBean.name%></span><br> 
                        <%=aBean.address%>, <%=aBean.city%>, <%=aBean.state%> <%=aBean.zip%>
						 [<a href="http://www.mapquest.com/maps/map.adp?city=<%=aBean.city%>&state=<%=aBean.state%>&address=<%=aBean.address%>&zip=<%=aBean.zip%>&zoom=5" 
						 	target="_blank" class="redMain">map</a>]<br />
						<span class="redMain">Contact:</span> <%=aBean.contact%><br>
						<span class="redMain">Phone:</span> <%=aBean.phone%> 
						<span class="redMain"><%=aBean.getPhone2Header()%></span> <%=aBean.phone2%><br>
						<%=aBean.getFaxLine()%>
						<span class="redMain">Email:</span> <a href="mailto:<%=aBean.email%>"><%=aBean.email%></a><br>
                        <span class="redMain"><%=aBean.getWeb_URLHeader()%></span> 
                        <a href="http://<%=aBean.web_URL%>" target="_blank"><%=aBean.web_URL%></a><br>
						<%=cBean.getBrochureLink()%>
                      </td>
                      <td>
                        <span class=redMain>PICS Contractor ID:</span> <%= cBean.getLuhnId() %><br />
                        <nobr><span class=redMain>PICS Status:</span>
						<% if (!permissions.isContractor()) { %>
								<span class="<%=cBean.getTextColor(cBean.calcPICSStatus(pBean))%>"><strong><%=cBean.getDetailsStatus(pBean)%></strong></span>
						<% }
						if (!permissions.isOperator()) { %>
								<a class="<%=cBean.getTextColor(cBean.calcPICSStatus(pBean))%>" href="con_selectFacilities.jsp?id=<%=id%>">See Facilities</a>
						<% }
						if (!permissions.isContractor() && cBean.canView(permissions, "notes"))
							out.println(cBean.getNotesIcon());
						%>
						</nobr><br/>
					<%
					if (permissions.isOperator()) {
						%>
	                    <span class="redMain">Flagged Status:</span>
                          <a href="con_redFlags.jsp?id=<%=id%>"><img 
                          	src="images/icon_<%=flag.getFlagStatus(id,pBean.userID).toLowerCase()%>Flag.gif" width="12" height="15" border="0"></a><br>
						<%
					}
%>
                    <a href="ConAuditList.action?id=<%=id%>" class="blueMain">Audits &amp; Evaluations</a><br/>
<%	for(ContractorAudit audit: cBean.getAudits()) {
		if (permissions.canSeeAudit(audit.getAuditType().getAuditTypeID())){
%>
						<nobr><span class=redMain><%=audit.getAuditType().getAuditName()%> Audit:</span>
						<a class="blueMain" href="pqf_editMain.jsp?auditID=<%=audit.getId()%>"><%=audit.getAuditStatus()%></a></nobr><br>
<%
		}//if
	}//for
	if (cBean.isCertRequired() && (pBean.isAdmin() || pBean.isContractor()) ) {
%>
						<a class="<%=cBean.getTextColor(cBean.calcPICSStatus(pBean))%>" 
							href="contractor_upload_certificates.jsp?id=<%=id%>">Insurance Certificates</a><br>
<%
	}//if
%>
                        <span class=redMain>Risk Level:</span> <%= cBean.getRiskLevelShow() %><br />
					</td>
					</tr>
                  </table>
				</td>
              </tr>
              <tr bgcolor="#FFFFFF">
                <td class="blueMain" valign="top">
				  <%
				  	// Uses logo size information from javascript at top
				  %>
				  <script language="JavaScript">
				 document.write('<img '+ sizetext + ' src="/logos/<%=cBean.getDisplayLogo_file()%>" hspace=20 vspace=10 align=right valign=top>');
				  </script>
			       <%= cBean.getDescriptionHTML()%>
                </td>
              </tr>
            </table>
</body>
</html>
