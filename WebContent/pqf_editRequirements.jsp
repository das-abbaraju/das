<%@ page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%//@ page language="java" import="com.picsauditing.PICS.*"%>
<%@ include file="utilities/contractor_secure.jsp"%>
<jsp:useBean id="prBean" class="com.picsauditing.PICS.pqf.RequirementBean" scope ="page"/>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope ="page"/>
<%try{
	String auditType = request.getParameter("auditType");
	if (null==auditType || "".equals(auditType))
		auditType = com.picsauditing.PICS.pqf.Constants.PQF_TYPE;
	String conID = request.getParameter("id");
	String id = request.getParameter("id");
	aBean.setFromDB(conID);
	cBean.setFromDB(conID);
	String action = request.getParameter("action");
	String delID = request.getParameter("delID");
	String errorMsg = "";
	if (null != delID) {
		prBean.deleteRequirement(delID);
		prBean.updateNumbering(conID,auditType);
	}//if
	if ("Save".equals(action))
		prBean.updateRequirements(request, id);
	if ("Submit".equals(action)) {
		prBean.updateRequirements(request, id);
		if (request.getParameter("reqCount").equals(prBean.numReqCompleted)) {
			cBean.closeAudit(id, adminName,auditType);
			errorMsg = "All the requirements on this audit have been closed";
		} else
			errorMsg = "Please complete all requirements before submitting";
	//incomplete - record date, that closed
	}//if
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
          <td width="147"><%@ include file="utilities/rightUpperNav.jsp"%></td>
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
			    <td>
  <%@ include file="includes/nav/secondNav.jsp"%>
				</td>
			  </tr>
    		  <tr align="center" class="blueMain">
                <td class="blueHeader"><%=auditType%> for <%=aBean.name%></td>
    		  </tr>
	  		  <tr align="center">
                <td class="redMain"><%=errorMsg%></td>
    		  </tr>
    		  <tr align="center" class="blueMain">
                <td class="redMain">&nbsp;</td>
   			  </tr>
  			  <tr align="center">
				<td align="left">
                <form name="form1" method="post" action="pqf_editRequirements.jsp">
                  <input name="id" type="hidden" value="<%=id%>">
                  <input name="auditType" type="hidden" value="<%=auditType%>">
  				  <table width="657" border="0" cellpadding="1" cellspacing="1">
                    <tr class="whiteTitle"> 
                      <td width="30" bgcolor="#003366">#</td>
                      <td bgcolor="#003366" colspan=2>Requirement</td>
                      <td bgcolor="#003366"></td>
                    </tr>
<%	while (prBean.isNextRecord()) { %>
					<tr <%=prBean.getBGColor()%> class=blueMain>
					  <td></td>
                      <td valign="top">(<%=prBean.pcBean.category%>) <%=prBean.pqBean.question%> <%=prBean.pqBean.getLinks()%></td>
					  <td></td>
					</tr>
					<tr <%=prBean.getBGColor()%> class=blueMain>
                      <td valign="top" width="1%"><%=prBean.count%></td>
					  <td valign="top" class=<%=prBean.getReqStyle()%>>
                        <textarea name="requirement_<%=prBean.rID%>" cols="110" rows="3" class="forms" id="requirement_<%=prBean.rID%>"><%=prBean.requirement1%></textarea>
                 	    <strong>Requirement Closed? </strong>
                        <input name="isReqComplete_<%=prBean.rID%>" type="radio" value="Yes" <%=Inputs.getChecked(prBean.isReq1Complete,"Yes")%> onClick="document.all.reqCompletedDate_<%=prBean.rID%>.value = '<%=DateBean.getTodaysDate()%>';">Yes 
                        <input name="isReqComplete_<%=prBean.rID%>" type="radio" value="No" <%=Inputs.getChecked(prBean.isReq1Complete,"No")%>>No &nbsp;&nbsp; &nbsp;&nbsp; 
                          Date Requirement Closed 
                        <input type="text" size="8" class=forms name="reqCompletedDate_<%=prBean.rID%>" value="<%=prBean.req1CompletedDate%>"></span>
					  </td>
					  <td><a href=pqf_editRequirements.jsp?delID=<%=prBean.rID%>&id=<%=conID%>&auditType=<%=auditType%>>Delete</a></td>
					</tr>
<%	}//while%>
                  <input name=reqCount type=hidden value=<%=prBean.count%>>
<%	prBean.closeList();%>
                  </table>
                  <input name="action" type="submit" class="forms" value="Save">
                    click to save these requirements and edit later <br>
                  <input name="action" type="submit" class="forms" value="Submit">
                    click to close this audit </span> 
				</form>
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