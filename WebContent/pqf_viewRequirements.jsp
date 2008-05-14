<%@page language="java" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
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
	cBean.tryView(permissions);
	prBean.setList(conID, auditType);
%>
<html>
<head>
<title>Audit Requirements</title>
</head>
<body>
			<table border="0" cellspacing="0" cellpadding="1" class="blueMain">
              <tr align="center" class="blueMain">
			    <td><%@ include file="utilities/adminOperatorContractorNav.jsp"%></td>
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
</body>
</html>
<%	}finally{
		prBean.closeList();
	}//finally
%>