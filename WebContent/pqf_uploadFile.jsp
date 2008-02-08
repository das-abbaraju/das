<%@page language="java" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<%@include file="utilities/contractor_secure.jsp"%>
<jsp:useBean id="pqBean" class="com.picsauditing.PICS.pqf.QuestionBean" scope="page"/>
<jsp:useBean id="psBean" class="com.picsauditing.PICS.pqf.SubCategoryBean" scope="page"/>
<jsp:useBean id="pcBean" class="com.picsauditing.PICS.pqf.CategoryBean" scope="page"/>
<jsp:useBean id="pdBean" class="com.picsauditing.PICS.pqf.DataBean" scope="page"/>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope="page"/>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope="page"/>
<%try{
	// 12/20/04 jj - added timeOutWarning, timeOut javascripts, timedOut hidden form field
	String conID = request.getParameter("id");
	String id = request.getParameter("id");
	String catID = request.getParameter("catID");
	String action = request.getParameter("action");
	String auditType = request.getParameter("auditType");
	boolean isCategorySelected = (null != catID && !"0".equals(catID));
	if (!isCategorySelected){
		response.sendRedirect("pqf_editMain.jsp?id="+conID+"&auditType="+com.picsauditing.PICS.pqf.Constants.PQF_TYPE);
		return;
	}//if
	int numQuestions = 0;
	int numSections = 0;
	aBean.setFromDB(conID);
	cBean.setFromDB(conID);
	if ("Save".equals(action)){
		pdBean.uploadPQFFile(pageContext,conID,catID);		
		if (pdBean.isOK()) {
			pdBean.savePQFUpload(request,conID,catID,auditType,pBean.userID);
			pdBean.updatePercentageCompleted(conID,catID,auditType);
			cBean.setPercentComplete(com.picsauditing.PICS.pqf.Constants.PQF_TYPE,pdBean.getPercentComplete(conID,com.picsauditing.PICS.pqf.Constants.PQF_TYPE));
			cBean.setPercentVerified(auditType,pdBean.getPercentVerified(conID,auditType));			
			cBean.writeToDB();
			response.sendRedirect("pqf_editMain.jsp?id="+conID+"&auditType="+com.picsauditing.PICS.pqf.Constants.PQF_TYPE);
			return;
		}//if
	}//if
	if ("Delete".equals(action)){
		String qID = request.getParameter("qID");
		String ext = request.getParameter("ext");
		pdBean.deletePQFFile(config,conID,qID,ext);
	}
	
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
			    <form name="formEdit" method="post" action="pqf_uploadFile.jsp?auditType=<%=auditType%>&catID=<%=catID%>&id=<%=id%>&action=Save" enctype="multipart/form-data">
                <tr align="left" class="blueMain">
                  <td><%@ include file="includes/nav/secondNav.jsp"%></td>
				</tr>
    			<tr align="center" class="blueMain">
                  <td class="blueHeader">PQF for <%=aBean.name%></td>
    			</tr>
	  			<tr align="center">
                  <td class="redmain"><strong><%=pdBean.getErrorMessages()%></strong></td>
    			</tr>
	  			<tr align="center">
      			</tr>
    			<tr>
                  <td>&nbsp;</td>
    			</tr>
<%	if (isCategorySelected) {
		pcBean.setFromDB(catID);
%>  				  <tr align="center">
				    <td align="left">
<%		if (pBean.isAdmin() || pBean.isAuditor()) { %>
                      <center><input type="checkbox" name="catDoesNotApply" value="Yes" <%=com.picsauditing.PICS.Inputs.getChecked("Yes",pdBean.catDoesNotApply)%>> 
					   Check here if this entire category does not apply</center><br>
<%		}//if %>					  <input name="action" type="submit" class="forms" value="Save">
                    Click to save your work. You may still edit your information 
                    later.<br>
<% if (pqBean.highlightRequired) { %>
					<span class="redMain"><strong>* Red questions are required and must be answered to be able to submit your PQF</strong></span>
					<br><span class="redMain">&nbsp;&nbsp;(File uploads must be in .pdf, .doc, .txt, .xls or .jpg format.)</strong></span>
<%	}//if %>
                      <table width="657" border="0" cellpadding="1" cellspacing="0">
                        <tr class="blueMain">
                          <td bgcolor="#003366" colspan="3" align="center"><font color="#FFFFFF"><strong>Category <%=pcBean.number%> - <%=pcBean.category%></strong></font></td>
                        </tr>
                        <tr>
                          <td colspan="3"></td>
                        </tr>
<%		numSections = 0;
		for (java.util.ListIterator li=psBean.subCategories.listIterator();li.hasNext();) {
			numSections++;
			String subCatID = (String)li.next();
			String subCat = (String)li.next();
			pqBean.setSubListWithData("number",subCatID,conID);
%>					  
                        <tr class="blueMain">
                          <td bgcolor="#003366" colspan="3" align="center"><font color="#FFFFFF"><strong>Sub Category <%=numSections%> - <%=subCat%></strong></font></td>
                        </tr>
<%			numQuestions = 0;
			while (pqBean.isNextRecord()) {
				numQuestions++;
%>
					    <%=pqBean.getTitleLine("blueMain")%>
					    <tr <%=pqBean.getGroupBGColor()%> class=blueMain>
                          <td valign="top" <%=pqBean.getClassAttribute(pdBean)%>><%=pqBean.number%>.</td>
						  <td valign="top" <%=pqBean.getClassAttribute(pdBean)%>><%=pqBean.question%> <%=pqBean.getLinksWithCommas()%></td>
						  <td width=200 valign="bottom"><%=pqBean.getInputElement()%> <%=pqBean.getOriginalAnswerView()%>
	<%				if (!"".equals(pqBean.data.answer)){
					if(pBean.isAdmin()){%>
                            <span align=right>&nbsp;&nbsp;&nbsp;<a href="pqf_uploadFile.jsp?action=Delete&qID=<%=pqBean.questionID%>&ext=<%=pqBean.data.answer%>&catID=<%=catID%>&id=<%=conID%>&auditType=<%=auditType%>">Delete</a></span>
<%					}//if%>
					        <input type=hidden name=isUploaded_<%=pqBean.questionID%> value=Uploaded>

<%//							<br><a href=files/pqf/<%=pqBean.questionID%_<%=conID%.pdf target=_blank\>View File</a>		
				}//if %>
						    <input type=hidden name=pqfQuestionID_<%=pqBean.questionID%> value=<%=pqBean.questionID%>>
					        <input type=hidden name=isRequired_<%=pqBean.questionID%> value="<%=pqBean.calcIsRequired(pdBean)%>">
<%/*						    <input type="hidden" name="pqfQuestion_<%=pqBean.questionID" value="<%=pqBean.question">
						    <input type="hidden" name="pqfQuestionNum_<%=pqBean.questionID" value="<%=pqBean.number">
*/%>					 	    <input type="hidden" name="pqfQuestionType_<%=pqBean.questionID%>" value="<%=pqBean.questionType%>">
						  </td>
                        </tr>
<%			}//while
			pqBean.closeList();
		}//for
%>
					  </table><br>
					  <input name="action" type="submit" class="forms" value="Save">
                    Click to save your work. You may still edit your information 
                    later. </td>
				  </tr>
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
	}//finally
%>