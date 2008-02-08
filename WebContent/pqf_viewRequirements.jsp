<%@page language="java" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<%@include file="utilities/contractor_secure.jsp"%>
<jsp:useBean id="prBean" class="com.picsauditing.PICS.pqf.RequirementBean" scope ="page"/>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope ="page"/>
<%try{
	Stringt auditType = request.getParameter("auditType");
	if (null==auditType || "".equals(auditType))
		auditType = com.picsauditing.PICS.pqf.Constants.PQF_TYPE;
	String conID = request.getParameter("id");
	String id = request.getParameter("id");
	aBean.setFromDB(conID);
	cBean.setFromDB(conID);
	prBean.setList(conID, auditType);
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
    <td valign="top">
	  <table width="100%" border="0" cellpadding="0" cellspacing="0">
        <tr> 
          <td width="50%" bgcolor="#993300">&nbsp;</td>
          <td width="146" valign="top" rowspan="2"><a href="index.jsp"><img src="images/logo.gif" alt="HOME" width="146" height="145" border="0"></a></td>
          <td width="364"><%@ include file="utilities/mainNavigation.jsp"%></td>
          <td width="147"><img src="images/squares_rightUpperNav.gif" width="147" height="72" border="0"></td>
          <td width="50%" bgcolor="#993300">&nbsp;</td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td valign="top" align="center">&nbsp;</td>
          <td valign="top"><%@ include file="utilities/rightLowerNav.jsp"%></td>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td colspan="3" align="center">
			<table border="0" cellspacing="0" cellpadding="1" class="blueMain">
              <tr align="center" class="blueMain">
			    <td><%@ include file="includes/nav/secondNav.jsp"%></td>
			  </tr>
    		  <tr align="center" class="blueMain">
                <td class="blueHeader"><%=auditType%> for <%=aBean.name%></td>
    		  </tr>
	  		  <tr align="center">
                <td></td>
    		  </tr>
    		  <tr align="center" class="blueMain">
                <td class="redMain">&nbsp;</td>
   			  </tr>
  			  <tr align="center">
				<td align="left">
  				  <table width="657" border="0" cellpadding="1" cellspacing="1">
                    <tr class="whiteTitle"> 
                      <td width="30" bgcolor="#003366">#</td>
                      <td bgcolor="#003366">Requirement</td>
                    </tr>
<%	while (prBean.isNextRecord()) { %>
					<tr <%=prBean.getBGColor()%> class=blueMain>
                      <td valign=top align=right>Number:</td>
                      <td valign=top><%=prBean.count%></td>
					</tr>
					<tr <%=prBean.getBGColor()%> class=blueMain>
                      <td valign=top align=right>Category:</td>
                      <td valign=top><%=prBean.pcBean.category%></td>
					</tr>
					<tr <%=prBean.getBGColor()%> class=blueMain>
                      <td valign=top align=right>Req:</td>
                      <td valign=top class=<%=prBean.getReqStyle()%>><strong><%=prBean.requirement1%></strong></td>
					</tr>
					<tr <%=prBean.getBGColor()%> class=blueMain>
                      <td valign=top align=right>Links:</td>
                      <td valign=top><%=prBean.pqBean.getLinks()%></td>
					</tr>
					<tr <%=prBean.getBGColor()%> class=blueMain>
                      <td valign=top align=right>Status:</td>
                      <td valign=top><%=prBean.getStatus()%></td>
					</tr>
<%	}//while
	prBean.closeList();					  
%>
                  </table>
				</td>
		      </tr>
			</table>
		  </td>
          <td>&nbsp;</td>
        </tr>
      </table>
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
<%	}finally{
		prBean.closeList();
	}//finally
%>