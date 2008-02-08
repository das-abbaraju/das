<%@ page language="java" import="com.picsauditing.PICS.redFlagReport.*" errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp" %>
<%@ include file="utilities/contractor_secure.jsp"%>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope ="page"/>
<%
	FlagDO flag = new FlagDO();
	String id = request.getParameter("id");
	aBean.setFromDB(id);
	cBean.setFromDB(id);
	if (!pBean.isAuditor())
		cBean.setShowLinks(pBean);
%>
<html>
<head>
  <title>PICS - Pacific Industrial Contractor Screening</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <link href="PICS.css" rel="stylesheet" type="text/css">
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

<body bgcolor="#EEEEEE" vlink="#003366" alink="#003366" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
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
          <td valign="top" align="center"><img src="images/header_contractorDetails.gif" width="321" height="72" border="0"></td>
          <td valign="top"><%@ include file="utilities/rightLowerNav.jsp"%></td>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td colspan="3">
			<table width="657" border="0" cellpadding="0" cellspacing="0">
              <tr align="center" class="blueMain">
                <td align="left"><%@ include file="includes/nav/secondNav.jsp"%></td>
              </tr>
            </table>
			<table width="657" border="0" cellpadding="15" cellspacing="1" bgcolor="#F8F8F8">
              <tr bgcolor="#FFFFFF">
                <td width="647" bgcolor="#F8F8F8" class="blueMain">
				  <table width="100%" border="0" cellpadding="0" cellspacing="0">
                    <tr>
                      <td width="453" valign="top" class="blueMain">
					    <span class="blueHeader"><%=aBean.name%></span><br> 
                        <%=aBean.address%>, <%=aBean.city%>, <%=aBean.state%> <%=aBean.zip%>
						 | <a href="http://www.mapquest.com/maps/map.adp?city=<%=aBean.city%>&state=<%=aBean.state%>&address=<%=aBean.address%>&zip=<%=aBean.zip%>&zoom=5" target=_blank class=redMain>map</a><br>
						<span class="redMain">Contact:</span> <%=aBean.contact%><br>
						<span class="redMain">Phone:</span> <%=aBean.phone%> 
						<span class="redMain"><%=aBean.getPhone2Header()%></span> <%=aBean.phone2%><br>
						<%=aBean.getFaxLine()%>
						<span class="redMain">Email:</span> <a href="mailto:<%=aBean.email%>"><%=aBean.email%></a><br>
                        <span class="redMain"><%=aBean.getWeb_URLHeader()%></span> 
                        <a href="http://<%=aBean.web_URL%>" target="_blank"><%=aBean.web_URL%></a><br>
						<%=cBean.getBrochureLink()%>
                      </td>
                      <td width="172" valign="top" class="blueMain">
                        <nobr><span class=redMain>PICS Status:</span>
<%
	if (pBean.isAdmin() || pBean.isAuditor() || pBean.isCorporate()
			|| pBean.isOperator()) {
%>
						<span class="<%=cBean.getTextColor(cBean.calcPICSStatus(pBean))%>"><strong><%=cBean.getDetailsStatus(pBean)%></strong></span>
<%
	}//if
	if (pBean.isContractor() || pBean.isAuditor()) {
%>
						<a class="<%=cBean.getTextColor(cBean.calcPICSStatus(pBean))%>" href="con_selectFacilities.jsp?id=<%=id%>">See Facilities</a>
<%
	}//if
	if (pBean.isAdmin() || pBean.isAuditor()
			|| (!pBean.isContractor() && pBean.canSeeSet.contains(id)))
		out.println(cBean.getNotesIcon());
%>
						</nobr><br>
<%
	if ((pBean.isOperator() || pBean.isCorporate())
			&& pBean.canSeeSet.contains(id)) {
%>
	                    <span class=redMain>Flagged Status:</span>
                          <a href=con_redFlags.jsp?id=<%=id%>>
                            <img src=images/icon_<%=flag.getFlagStatus(id,pBean.userID).toLowerCase()%>Flag.gif width=12 height=15 border=0>
                          </a><br>
<%
	}//if
%>
						<span class=redMain>Pre-Qualification:</span> <%=cBean.getPQFLink(pBean)%><br>
<%
	if (!pBean.isContractor() || cBean.isDesktopRequired()) {
%>
						<nobr><span class=redMain>Desktop Audit:</span> <%=cBean.getDesktopLink(pBean)%></nobr><br>
<%
	}//if
	if (!pBean.isContractor() || cBean.isDARequired()) {
%>
                         <nobr><span class=redMain>D&amp;A Audit:</span> <%=cBean.getDaLink(pBean)%></nobr><br>
<%
	}//if
	if (!pBean.isContractor() || cBean.isOfficeRequired()) {
%>
						<nobr><span class=redMain>Office Audit:</span> <%=cBean.getOfficeLink(pBean)%></nobr><br>
<%
	}//if
%>
<%
	if (pBean.isOperator() || pBean.isCorporate()) {
%>
						<span class=redMain>Field Audit:</span> Contact PICS<br>
<%
	}
%>

<%
	if (pBean.isAdmin() && cBean.isCertRequired()) {
%>
						<a class="<%=cBean.getTextColor(cBean.calcPICSStatus(pBean))%>" href="<%=request.getContextPath()%>/contractor_upload_certificates.jsp?id=<%=id%>">Insurance Certificates</a>

<%
	}//if
%>
<%
	if (pBean.isContractor() && cBean.isCertRequired()) {
%>
						<a class="<%=cBean.getTextColor(cBean.calcPICSStatus(pBean))%>" href="<%=request.getContextPath()%>/contractor_upload_certificates.jsp?id=<%=id%>">Insurance Certificates</a>

<%
	}//if
%>
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
//				  document.write('<img '+ sizetext + ' width=' + logowidth + ' src= "logos/<%=cBean.getDisplayLogo_file()%>" hspace="20" vspace="10" align="right" valign="top">');
				  document.write('<img '+ sizetext + ' src="/logos/<%=cBean.getDisplayLogo_file()%>" hspace=20 vspace=10 align=right valign=top>');
				  </script>
			       <%=cBean.getDescriptionHTML()%>
                </td>
              </tr>
            </table>
		  </td>
          <td>&nbsp;</td>
        </tr>
      </table>
      <br>
      <br>
    </td>
  </tr>
  <tr>
    <td height="72" align="center" bgcolor="#003366" class="copyrightInfo">&copy;2007 
      Pacific Industrial Contractor Screening | site design: <a href="http://www.albumcreative.com" title="Album Creative Studios"><font color="#336699">ACS</font></a></td>
  </tr>
</table>
</body>
</html>
