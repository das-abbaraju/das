<%@ page language="java" import="com.picsauditing.PICS.*,com.picsauditing.PICS.pqf.*" errorPage="exception_handler.jsp"%>
<%//@ page language="java" import="com.picsauditing.PICS.*,com.picsauditing.PICS.pqf.*"%>
<%@ include file="utilities/contractor_secure.jsp"%>
<jsp:useBean id="pqBean" class="com.picsauditing.PICS.pqf.QuestionBean" scope ="page"/>
<jsp:useBean id="psBean" class="com.picsauditing.PICS.pqf.SubCategoryBean" scope ="page"/>
<jsp:useBean id="pcBean" class="com.picsauditing.PICS.pqf.CategoryBean" scope ="page"/>
<jsp:useBean id="pdBean" class="com.picsauditing.PICS.pqf.DataBean" scope ="page"/>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope ="page"/>
<%try{
	// 12/20/04 jj - 
	String auditType = request.getParameter("auditType");
	if (null==auditType || "".equals(auditType))
		auditType = com.picsauditing.PICS.pqf.Constants.PQF_TYPE;
	String conID = request.getParameter("id");
	String id = request.getParameter("id");
	String catID = request.getParameter("catID");
	String action = request.getParameter("action");
	boolean isOSHA = pcBean.OSHA_CATEGORY_ID.equals(catID);
	//boolean isFileUpload = Utilities.arrayContains(com.picsauditing.PICS.pqf.Constants.UPLOAD_CAT_IDS,catID);
	boolean isFileUpload = pdBean.isFileUpload(catID);
	boolean canVerify = (isAdmin || isAuditor);
	if (isFileUpload){
		response.sendRedirect("pqf_uploadFile.jsp?auditType="+auditType+"&catID="+catID+"&id="+conID);
		return;
	}//if
	if (isOSHA) {
		OSHABean oBean = new OSHABean();
		oBean.setListFromDB(conID);
		if (oBean.isInDB)
			response.sendRedirect("pqf_viewOSHA.jsp?id="+conID+"&catID="+catID);
		else
			response.sendRedirect("pqf_OSHA.jsp?action=Edit&oID=New&id="+conID+"&catID="+catID);
		return;
	}//if
	boolean isCategorySelected = (null != catID && !"0".equals(catID));
	if (!isCategorySelected) {
		response.sendRedirect("pqf_editMain.jsp?auditType="+auditType+"&id="+conID);
		return;
	}//if
	int numQuestions = 0;
	int numSections = 0;
	int requiredCount = 0;
	String errorMsg = "";
	aBean.setFromDB(conID);
	cBean.setFromDB(conID);
	if ("Save".equals(action)) {
		pdBean.savePQF(request,conID,catID,auditType,pBean.userID);
//		if (canVerify)
//			pdBean.saveVerification(request,conID);
		cBean.setPercentComplete(auditType,pdBean.getPercentComplete(conID,auditType));
		cBean.setPercentVerified(auditType,pdBean.getPercentVerified(conID,auditType));
		cBean.writeToDB();
		pdBean.updatePercentageCompleted(conID,catID,auditType);
		response.sendRedirect("pqf_editMain.jsp?auditType="+auditType+"&id="+conID);
		return;
	}//if
	psBean.setPQFSubCategoriesArray(catID);
	pdBean.setFromDB(conID,catID);
	pqBean.highlightRequired = pdBean.alreadySavedCat;
%>

<html>
<head>
  <title>PICS - Pacific Industrial Contractor Screening</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <link href="PICS.css" rel="stylesheet" type="text/css">
  <script language="JavaScript" src="js/TimeOutWarning.js"></script>
  <SCRIPT LANGUAGE="JavaScript" SRC="js/CalendarPopup.js"></SCRIPT>
  <SCRIPT LANGUAGE="JavaScript">document.write(getCalendarStyles());</SCRIPT>
  <SCRIPT LANGUAGE="JavaScript" ID="js1">var cal1 = new CalendarPopup();</SCRIPT>
  <SCRIPT LANGUAGE="JavaScript" SRC="js/validateForms.js"></SCRIPT>
</head>
<body bgcolor="#EEEEEE" background="images/watermark.gif" vlink="#003366" alink="#003366" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0"  onload="return window_onload();">
  <table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
    <tr>
      <td valign="top">
        <table width="100%" border="0" cellpadding="0" cellspacing="0">
          <tr> 
            <td width="50%" bgcolor="#993300">&nbsp;</td>
            <td width="146" valign="top" rowspan="2"><a href="index.jsp"><img src="images/logo.gif" alt="HOME" width="146" height="145" border="0"></a></td>
            <td width="364"><%@ include file="utilities/mainNavigation.jsp"%></td>
            <td><%@ include file="utilities/rightUpperNav.jsp"%></td>
            <td width="147">&nbsp;</td>
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
              <form name="formEdit" method="post" action="pqf_edit.jsp">
                <tr align="left" class="blueMain">
                  <td><%@ include file="includes/nav/secondNav.jsp"%></td>
                </tr>
                <tr align="center" class="blueMain">
                  <td class="blueHeader"><%=auditType%> for <%=aBean.name%></td>
                </tr>
                <tr align="center">
                  <td class="redmain"><strong><%=errorMsg%></strong></td>
    			</tr>
                <tr>
                  <td>&nbsp;</td>
                </tr>
<%	if (isCategorySelected) {
		pcBean.setFromDB(catID);
%>
                <tr align="center">
                  <td align="left">
<%		if (pBean.isAdmin() || pBean.isAuditor()) { %>
                      <center><input type="checkbox" name="catDoesNotApply" value="Yes" <%=Inputs.getChecked("Yes",pdBean.catDoesNotApply)%>>
					   Check here if this entire category does not apply</center><br>
<%		}//if %>					  <input name="action" type="submit" class="forms" value="Save">
                    Click to save your work. You may still edit your information 
                    later.<br>
<%		if (pqBean.highlightRequired) { %>
					<span class="redMain"><strong>* Red questions are required and must be answered to be able to submit your PQF</strong></span>
<%		}//if %>
                    <table width="657" border="0" cellpadding="1" cellspacing="0">
                      <tr class="blueMain">
                        <td bgcolor="#003366" colspan="3" align="center"><font color="#FFFFFF"><strong>Category <%=pcBean.number%> - <%=pcBean.category%></strong></font></td>
                      </tr>
<%		numSections = 0;
		for (java.util.ListIterator li=psBean.subCategories.listIterator();li.hasNext();) {
			numSections++;
			String subCatID = (String)li.next();
			String subCat = (String)li.next();
//			pqBean.setSubList("number", subCatID);
			pqBean.setSubListWithData("number",subCatID,conID);
%>					  
                      <tr class="blueMain">
                        <td bgcolor="#003366" colspan="3" align="center"><font color="#FFFFFF"><strong>Sub Category <%=pcBean.number%>.<%=numSections%> - <%=subCat%></strong></font></td>
                      </tr>
<%			numQuestions = 0;
			while (pqBean.isNextRecord()) {
				if (pqBean.calcIsRequired(pdBean))
					requiredCount++;
				numQuestions++;
%>
                      <%=pqBean.getTitleLine("blueMain")%>
                      <tr <%=pqBean.getGroupBGColor()%> class=blueMain>
                        <td valign="top" <%=pqBean.getClassAttribute(pdBean)%>><%=pqBean.number%>.</td>
                        <td valign="top" <%=pqBean.getClassAttribute(pdBean)%>><%=pqBean.question%> <%=pqBean.getLinksWithCommas()%></td>
                        <td width=50 valign="bottom"><%=pqBean.getInputElement()%>
                          <input type=hidden name=pqfQuestionID_<%=pqBean.questionID%> value="<%=pqBean.questionID%>">
                          <input type=hidden name=isRequired_<%=pqBean.questionID%> value="<%=pqBean.isRequired%>">
                          <input type=hidden name=oldAnswer_<%=pqBean.questionID%> value="<%=pqBean.data.answer%>">
                          <input type=hidden name=oldComment_<%=pqBean.questionID%> value="<%=pqBean.data.comment%>">
                          <input type=hidden name=oldDateVerified_<%=pqBean.questionID%> value="<%=pqBean.data.dateVerified%>">
                          <input type=hidden name=oldAuditorID_<%=pqBean.questionID%> value="<%=pqBean.data.auditorID%>">
                          <input type=hidden name=wasChanged_<%=pqBean.questionID%> value="<%=pqBean.data.wasChanged%>">
<%				if ("Depends".equals(pqBean.isRequired)) { %>
                          <input type=hidden name=dependsOnQID_<%=pqBean.questionID%> value="<%=pqBean.dependsOnQID%>">
                          <input type=hidden name=dependsOnAnswer_<%=pqBean.questionID%> value="<%=pqBean.dependsOnAnswer%>">
<%				}//if %>
                          <input type="hidden" name="pqfQuestionType_<%=pqBean.questionID%>" value="<%=pqBean.questionType%>">
                        </td>
                      </tr>
<%			}//while
			pqBean.closeList();
		}//for
%>
                    </table>
					<br>
					<input name="action" type="submit" class="forms" value="Save"> Click to save your work. You may still edit your information later.
                  </td>
                </tr>
                <input type="hidden" name="catID" value="<%=catID%>">
                <input type="hidden" name="id" value="<%=conID%>">
                <input type="hidden" name="requiredCount" value="<%=requiredCount%>">
                <input type="hidden" name="auditType" value="<%=auditType%>">
              </form>
<%	}//if %>
              </table>
            </td>
            <td>&nbsp;</td>
          </tr>
        </table>
        <br><br>
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