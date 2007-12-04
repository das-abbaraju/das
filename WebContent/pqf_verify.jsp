<%//@ page language="java" import="com.picsauditing.PICS.*,com.picsauditing.PICS.pqf.*" errorPage="exception_handler.jsp"%>
<%@ page language="java" import="com.picsauditing.PICS.*,com.picsauditing.PICS.pqf.*"%>
<%@ include file="utilities/contractor_secure.jsp"%>
<jsp:useBean id="pqBean" class="com.picsauditing.PICS.pqf.QuestionBean" scope ="page"/>
<jsp:useBean id="pcBean" class="com.picsauditing.PICS.pqf.CategoryBean" scope ="page"/>
<jsp:useBean id="psBean" class="com.picsauditing.PICS.pqf.SubCategoryBean" scope ="page"/>
<jsp:useBean id="pdBean" class="com.picsauditing.PICS.pqf.DataBean" scope ="page"/>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope ="page"/>
<%try{
	//3/5/05 if audit has not been submitted (questiosn frozen), the audit data is deleted and inserted rather than updated
	// 12/20/04 jj - added timeOutWarning, timeOut javascripts, timedOut hidden form field
	String auditType = request.getParameter("auditType");
	if (null==auditType || "".equals(auditType))
		auditType = com.picsauditing.PICS.pqf.Constants.PQF_TYPE;
	String conID = request.getParameter("id");
	String id = request.getParameter("id");
	String catID = request.getParameter("catID");
	String action = request.getParameter("action");
	boolean isCategorySelected = (null != catID && !"0".equals(catID));
	boolean isOSHA = pcBean.OSHA_CATEGORY_ID.equals(catID);
	boolean isServices = pcBean.SERVICES_CATEGORY_ID.equals(catID);
	aBean.setFromDB(conID);
	cBean.setFromDB(conID);
	String message = "";
	if ("Save".equals(action)) {
		if (pBean.canVerifyAudit(auditType,conID)) {
			pdBean.saveVerificationNoUpload(request,conID,pBean.userID);
			cBean.setPercentVerified(auditType,pdBean.getPercentVerified(conID,auditType));
			cBean.writeToDB();
			response.sendRedirect("pqf_verify.jsp?auditType="+auditType+"&id="+conID);
			return;
		} else
			message = "You do not have permission to verify this "+auditType+" Audit";
	} // if
	if (isCategorySelected)
		psBean.setPQFSubCategoriesArray(catID);
	boolean generateReqs = ("Generate Reqs".equals(request.getParameter("action")));
	if (generateReqs && pBean.canVerifyAudit(auditType,conID)) {
		if (!"100".equals(cBean.desktopVerifiedPercent))
			message="You must complete verifying all questions to generate requirements";
		else {
//			cBean.generateRequirements(conID, pBean.userName, auditType);
			cBean.writeToDB();
			response.sendRedirect("pqf_editRequirements.jsp?auditType="+auditType+"&id="+conID);
			return;
		}//else
	} // if
%>
<html>
<head>
  <title>PICS - Pacific Industrial Contractor Screening</title>
  <SCRIPT LANGUAGE="JavaScript" SRC="js/CalendarPopup.js"></SCRIPT>
  <SCRIPT LANGUAGE="JavaScript">document.write(getCalendarStyles());</SCRIPT>
  <SCRIPT LANGUAGE="JavaScript" ID="js1">var cal1 = new CalendarPopup();</SCRIPT>
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
			    <td width="676">
                  <%@ include file="includes/nav/secondNav.jsp"%>
				</td>
			  </tr>
    		  <tr align="center" class="blueMain">
                <td class="blueHeader"><%=auditType%> for <%=aBean.name%></td>
    		  </tr>
<%	if (!isCategorySelected) { %>
	  		  <tr align="center">
                <td class="blueMain">Date Submitted: <span class="redMain"><%=cBean.getAuditSubmittedDate(auditType)%></span><br>
<%		if (Constants.DESKTOP_TYPE.equals(auditType)) { %>
                  Percent Verified: <span class="redMain"><%=cBean.desktopVerifiedPercent%>%</span><br>
                  Date RQs Generated: <span class="redMain"><%=cBean.getAuditSubmittedDate(auditType)%></span>
<%		}//if %>
				</td>
    		  </tr>
<%	}//if %>
	  		  <tr align="center">
                <td class="redMain"><strong><%=message%></strong></td>
    		  </tr>
<%/*	  	      <tr align="center">
			    <form name="form1" method="post" action="pqf_verify.jsp">
        	      <td><%=pcBean.getPQFCategorySelectDefaultSubmit("catID","blueMain",catID,auditType)%</td>
			      <input type="hidden" name="id" value="<%=conID%">
			      <input type="hidden" name="auditType" value="<%=auditType%">
				</form>
              </tr>
<%*/if (isCategorySelected) {
		int catCount = 0;
		pcBean.setFromDBWithData(catID,conID);
		if (pBean.isAuditor() || pBean.isAdmin()) {
%>			   		
              <form name="formEdit" method="post" action="pqf_verify.jsp">
			  <tr>
			    <td>
				  <input name="action" type="submit" class="forms" value="Save">
                   Click to save your work. You may still edit your information later.<br>
			  	</td>
			  </tr>
<%		} //if %>	 
			  <tr align="center" class="blueMain">
                <td class="redMain">&nbsp;</td>
   			  </tr>
  			  <tr align="center">
				<td align="left">
  				  <table width="657" border="0" cellpadding="1" cellspacing="1">
                    <tr class="blueMain">
                      <td bgcolor="#003366" colspan="3" align="center"><font color="#FFFFFF"><strong>Category <%=pcBean.number%> - <%=pcBean.category%></strong></font></td>
                    </tr>
<%		int numSections = 0;
		for (java.util.ListIterator li=psBean.subCategories.listIterator();li.hasNext();) {
			numSections++;
			String subCatID = (String)li.next();
			String subCat = (String)li.next();
			pqBean.setSubListWithData("number", subCatID,conID);
//			pqBean.setSubList("number", subCatID);
			if (isOSHA) { %>
                    <%@ include file="includes/pqf/view_OSHA.jsp"%>
<%			} else { %>
                    <tr class="blueMain">
                      <td bgcolor="#003366" colspan="3" align="center"><font color="#FFFFFF"><strong>Sub Category <%=pcBean.number%>.<%=numSections%> - <%=subCat%></strong></font></td>
                    </tr>
<% 				if (pBean.isAuditor() || pBean.isAdmin()) { %>
                    <tr><td bgcolor="#003366" colspan="2">&nbsp;</td>
					<td bgcolor="#003366" class=whiteSmall>Last Action</td>
<%				} // if
				int numQuestions = 0;
				while (pqBean.isNextRecord()) {
					numQuestions++;
%>
                    <%=pqBean.getTitleLine("blueMain")%>
                    <tr <%=pqBean.getGroupBGColor()%> class=blueMain>
                      <td valign="top" width="1%"><%=pcBean.number%>.<%=numSections%>.<%=pqBean.number%></td>
                      <td valign="top"><%=pqBean.question%> <%=pqBean.getLinksWithCommas()%><br>
                        <%=pqBean.getOriginalAnswerView()%>
                      </td>
					  <td><%=pqBean.data.dateVerified%></td>
                    </tr>
                    <tr <%=pqBean.getGroupBGColor()%> class=blueMain>
                      <td></td>
                      <td>
                        Is Original Answer Correct? <%=pqBean.onClickCorrectYesNoRadio(pqBean.questionID,pqBean.questionType,"forms",pqBean.data.isCorrect)%>
                        <br>Verified Answer: <%=pqBean.getVerifiedInputElement()%>
					    <br>Comment: <input type=text name=comment_<%=pqBean.questionID%> class=forms size=70 value="<%=pqBean.data.comment%>">
                        <%//=pqBean.getDateVerifiedView()%>
					  <td>
                        <%//=Inputs.getDateInput2("dateVerified_"+pqBean.questionID,"forms",pdBean.getDateVerified(pqBean.questionID),"formEdit")%>
                        <%//=Inputs.getDateInput2("dateVerified_"+pqBean.questionID,"forms","","formEdit")%>
						<input type=hidden name=pqfQuestionID_<%=pqBean.questionID%> value="<%=pqBean.questionID%>">
						<input type=hidden name=answer_<%=pqBean.questionID%> value="<%=pqBean.data.answer%>">
						<input type=hidden name=oldVerifiedAnswer_<%=pqBean.questionID%> value="<%=pqBean.data.verifiedAnswer%>">
						<input type=hidden name=oldIsCorrect_<%=pqBean.questionID%> value="<%=pqBean.data.isCorrect%>">
						<input type=hidden name=oldComment_<%=pqBean.questionID%> value="<%=pqBean.data.comment%>">
						<input type=hidden name=wasChanged_<%=pqBean.questionID%> value="<%=pqBean.data.wasChanged%>">
                      </td>
					</tr>
<%				} // while
				pqBean.closeList();
			}//else
%>                  <tr class=blueMain>
                      <td colspan=2><br><input name="action" type="submit" class="forms" value="Save">
			              Click to save your work.
                      </td>
					</tr>
<%		}//for %>
                  </table>
		        </td>
		      </tr>
<%		if (isAuditor || isAdmin) { %>			       
			      <input type="hidden" name="catID" value="<%=catID%>">
			      <input type="hidden" name="id" value="<%=conID%>">
			      <input type="hidden" name="auditType" value="<%=auditType%>">
		      </form>
<%		} //if 	 
	} // if
	if (!isCategorySelected) {
//		pdBean.setFilledOut(conID);
%>
			  <tr>
			    <td>
				  <table width="657" border="0" cellpadding="1" cellspacing="1">
                    <tr class="whiteTitle"> 
                      <td bgcolor="#003366" width=1%>Num</td>
                      <td bgcolor="#003366">Category</td>
                      <td bgcolor="#993300">% Verified</td>
                    </tr>
<%		pcBean.setListWithData("number",auditType,conID);
		while (pcBean.isNextRecord(pBean, conID)) {
%>
                    <tr class="blueMain" <%=pcBean.getBGColor()%>>
                      <td align=right><%=pcBean.number%>.</td>
                      <td><a href="pqf_verify.jsp?auditType=<%=auditType%>&catID=<%=pcBean.catID%>&id=<%=conID%>"><%=pcBean.category%></a></td>
                      <td><%=pcBean.getPercentShow(pcBean.percentVerified)%><%=pcBean.getPercentCheck(pcBean.percentVerified)%></td>
                    </tr>
<%		} // while
		pcBean.closeList();
%>
                  </table>
<%		if (Constants.DESKTOP_TYPE.equals(auditType)) { %>
              <br>
			  <form name="form1" method="post" action="pqf_verify.jsp">
			    <input type="hidden" name="id" value="<%=conID%>">
			    <input type="hidden" name="auditType" value="<%=auditType%>">
				<input name="action" type="submit" class="forms" value="Generate Reqs">
              </form>

<%		} // if %>
				</td>
		      </tr>
<%	} // if %>

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
		pqBean.closeList();
		pcBean.closeList();
	}//finally
%>