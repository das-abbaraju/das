<%@page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<%@include file="utilities/adminGeneral_secure.jsp" %>
<jsp:useBean id="tBean" class="com.picsauditing.PICS.TradesBean" scope ="page"/>
<jsp:useBean id="sBean" class="com.picsauditing.PICS.SearchBean" scope ="page"/>
<%	try{
	String action = request.getParameter("action");
	String[] MONTHS_OPTIONS = {"0","1","2","3","6"};
	sBean.orderBy = request.getParameter("orderBy");
	if (null==sBean.orderBy)
		sBean.orderBy = "name";
	sBean.selected_incompleteAfter = "2";
	if (pBean.isAdmin())
		sBean.doSearch(request, sBean.ACTIVE_AND_NOT, 100, pBean, pBean.userID);
	else
		sBean.doSearch(request, sBean.ONLY_ACTIVE, 100, pBean, pBean.userID);
%>
<%@page import="com.picsauditing.jpa.entities.AuditType"%>
<html>
<head>
  <script language="JavaScript" SRC="js/Search.js"></script>
<title>Contractors with Incomplete Requirements</title>
<script language="JavaScript" SRC="js/ImageSwap.js"></script>
</head>
<body>
            <table border="0" cellpadding="0" cellspacing="0">
              <tr> 
                <td height="70" align="center" class="buttons"> 
                  <%@ include file="includes/selectReport.jsp"%>
                  <form name="form1" method="post" action="report_incompleteAudits.jsp">
	              <span class="blueHeader">Contractors with Incomplete Requirements Report </span><br><br>
                  Shows contractors who are not active <%=Inputs.inputSelect("incompleteAfter","forms",sBean.selected_incompleteAfter,MONTHS_OPTIONS)%> months after their audit.<br>
                  <table border="0" cellpadding="2" cellspacing="0">
                   <tr align="center" >
                     <td><input name="name" type="text" class="forms" value="<%=sBean.selected_name%>" size="20" onFocus="clearText(this)"></td>
                     <td><%=tBean.getTradesSelect("trade", "forms",sBean.selected_trade)%></td>
                     <td><input name="zip" type="text" class="forms" value="<%=sBean.selected_zip%>" size="5" onFocus="clearText(this)"></td>
                     <td><input name="imageField" type="image" src="images/button_search.gif" width="70" height="23" border="0"  onMouseOver="MM_swapImage('imageField','','images/button_search_o.gif',1)" onMouseOut="MM_swapImgRestore()"></td>
                   </tr>
                 </table>
     			  <input type="hidden" name="showPage" value="1"/>
		          <input type="hidden" name="startsWith" value=""/>
		          <input type="hidden" name="orderBy"  value="name"/>
                 </form>
                </td>
              </tr>
              <tr>
                <td align="center"><%=sBean.getLinksWithDynamicForm()%></td>
              </tr>
            </table>
            <table width="657" border="0" cellpadding="1" cellspacing="1">
              <tr bgcolor="#003366" class="whiteTitle">
                <td colspan="2"><a href="?changed=0&showPage=1&orderBy=name" class="whiteTitle">Contractor</a></td>
<%	if (permissions.canSeeAudit(AuditType.DESKTOP)){%>
                <td align="center" bgcolor="#6699CC"><a href="?changed=0&showPage=1&orderBy=desktopSubmittedDate DESC" class="whiteTitle">Desktop Audit</a></td>
<%	}//if
	if (permissions.canSeeAudit(AuditType.DA)){%>
                <td align="center" bgcolor="#6699CC"><a href="?changed=0&showPage=1&orderBy=daSubmittedDate DESC" class="whiteTitle">D&A Audit</a></td>
<%	}//if
	if (permissions.canSeeAudit(AuditType.OFFICE)){
%>                <td align="center" bgcolor="#6699CC"><a href="?changed=0&showPage=1&orderBy=auditCompletedDate DESC" class="whiteTitle">Office Audit</a></td>
<%	}//if%>
              </tr>
<%	while (sBean.isNextRecord()){
		String thisClass = sBean.cBean.getTextColor(sBean.cBean.calcPICSStatusForOperator(pBean.oBean));
%>
              <tr <%=sBean.getBGColor()%> class="<%=thisClass%>">
                <td align="right"><%=sBean.count-1%></td>
                <td><%=sBean.getActiveStar()%>
                  <a href="contractor_detail.jsp?id=<%=sBean.aBean.id%>" title="view <%=sBean.aBean.name%> details" class="<%=thisClass%>"><%=sBean.aBean.name%></a>
				</td>
<%	if (permissions.canSeeAudit(AuditType.DESKTOP)){%>
                <td align="center"><%=sBean.cBean.getDesktopLink(pBean)%></td>
<%	}//if
	if (permissions.canSeeAudit(AuditType.DA)){%>
                 <td align="center"><%=sBean.cBean.getDaLink(pBean)%></td>
<%	}//if
	if (permissions.canSeeAudit(AuditType.OFFICE)){
%>
                <td align="center"><%=sBean.cBean.getOfficeLink(pBean)%></td>
<%	}//if%>
              </tr>
<%	}//while%>
            </table><br>
            <center><%=sBean.getLinksWithDynamicForm()%></center>
<%	sBean.closeSearch(); %>
      <br><center><%@ include file="utilities/contractor_key.jsp"%></center>
</body>
</html>
<%	}finally{
		sBean.closeSearch();
	}//finally
%>