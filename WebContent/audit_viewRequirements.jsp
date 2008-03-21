<%@ page language="java" errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp" %>
<jsp:useBean id="adBean" class="com.picsauditing.PICS.AuditDataBean" scope ="page"/>
<jsp:useBean id="aqBean" class="com.picsauditing.PICS.AuditQuestionBean" scope ="page"/>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope ="page"/>

<%try{
	String id = request.getParameter("id");
	String action = request.getParameter("action");
	String orderby = request.getParameter("orderby");
	String ses_id = (String)session.getAttribute("userid");
	String reqStyleClass = "blueMain";
	int numReq = 0;
	if (orderby == null)
		orderby = "num";
	adBean.setFromDB(id);
	adBean.setList(id);
	aBean.setFromDB(id);
	cBean.setFromDB(id);
	cBean.tryView(permissions);
%>
<html>
<head>
<title>Office Audit Requirements</title>
</head>
<body>
  			<table border="0" cellspacing="0" cellpadding="1" class="blueMain">
              <tr align="center" class="blueMain">
                <td align="left"><%@ include file="includes/nav/secondNav.jsp"%></td>
			  </tr>
              <tr align="center" class="blueMain">
                <td class="blueHeader">Office Audit Requirements for <%=aBean.name%></td>
              </tr>
              <tr align="center" class="blueMain">
                <td class="blueMain">Audit Performed: <b><%=cBean.getAuditCompletedDateShow()%></b><br>Requirements Closed: <b><%=cBean.getAuditClosedDateShow()%></b></td>
              </tr>
              <tr align="center">
			    <td>
				  <table width="657" border="0" cellpadding="1" cellspacing="1">
                    <tr class="whiteTitle"> 
                      <td width="30" bgcolor="#003366">#</td>
                      <td bgcolor="#003366" colspan=2>Requirement</td>
                    </tr>

<%	aqBean.setOKMapFromDB();
	aqBean.setList(orderby, "Office");
	while (adBean.isNextRecord()) {
		if (adBean.ok.equals("No")) {
			numReq = numReq + 1;
			if (!"".equals(adBean.checkedReqComplete(adBean.id,"No"))) 
				reqStyleClass = "redMain";
			else 
				reqStyleClass = "blueMain";
%>
              <tr class="blueMain" <%=com.picsauditing.PICS.Utilities.getBGColor(numReq)%>>
                <td valign="top"><nobr>Req <%=numReq%>:</nobr></td>
                <td colspan="2">
<%			if ("NA".equals(adBean.whichreq) || "None".equals(adBean.whichreq) ||  "".equals(adBean.whichreq)) {
%>
				<span class="<%=reqStyleClass%>"><strong><%=adBean.requirement%></strong><br>
<%				if ("Yes".equals(adBean.getReqCompleteFromID(adBean.id)))
					out.println("Closed on " + adBean.getDateReqCompleteFromID(adBean.id));
				else
					out.println("Open");
			} else if ("Class".equals(adBean.whichreq) || "Both".equals(adBean.whichreq)) {
				if (!"".equals(adBean.checkedClassComplete(adBean.id,"Yes")))
					reqStyleClass = "blueMain";
				else
					reqStyleClass = "redMain";
%>
							<span class="<%=reqStyleClass%>">
							<strong>Class: <%=adBean.reqclass%></strong><br>
<%				if ("Yes".equals(adBean.getClassCompleteFromID(adBean.id)))
					out.println("Closed on " + adBean.getDateClassCompleteFromID(adBean.id) + "<br>");
				else
					out.println("Open" + "<br>");
			}//if ("NA".equals(adBean.whichreq)
			if ("Program".equals(adBean.whichreq) || "Both".equals(adBean.whichreq)) {
				if (!"".equals(adBean.checkedProgramComplete(adBean.id,"Yes"))) 
					reqStyleClass = "blueMain";
				else
					reqStyleClass = "redMain";%>
						  <span class="<%=reqStyleClass%>">
							<strong>Program: <%=adBean.reqprogram%></strong><br> 
<%				if ("Yes".equals(adBean.getProgramCompleteFromID(adBean.id)))
					out.println("Closed on " + adBean.getDateProgramCompleteFromID(adBean.id)+ "<br>");
				else
					out.println("Open"+ "<br>"); 
			}//if program/both
%>
						  </span>
						</td>
                      </tr>
<%		}//if not Ok
	}//while
%>
                    </table>
                  <br><br><br>
				</td>
              </tr>
            </table>
</body>
</html>
<%	}finally{
		aqBean.closeList();
		adBean.closeList();
	}//finally
%>