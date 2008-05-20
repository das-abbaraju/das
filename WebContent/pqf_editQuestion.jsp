<%@page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<jsp:useBean id="pqBean" class="com.picsauditing.PICS.pqf.QuestionBean" scope ="page"/>
<jsp:useBean id="pcBean" class="com.picsauditing.PICS.pqf.CategoryBean" scope ="page"/>
<jsp:useBean id="psBean" class="com.picsauditing.PICS.pqf.SubCategoryBean" scope ="page"/>
<jsp:useBean id="uBean" class="com.picsauditing.PICS.Utilities" scope ="page"/>
<jsp:useBean id="poBean" class="com.picsauditing.PICS.pqf.OptionBean" scope ="page"/>
<jsp:useBean id="action" class="com.picsauditing.actions.auditType.AuditTypeLegacy" scope="page" />
<%
permissions.tryPermission(OpPerms.ManageAudits);
action.setAuditTypeID(request.getParameter("auditTypeID"));
try{
	String actionString = request.getParameter("action");
	String editID = request.getParameter("editID");
	String catID = request.getParameter("catID");
	String subCatID = request.getParameter("subCategoryID");
	String orderBy = request.getParameter("orderBy");
	String newOption = request.getParameter("newOption");
	
	boolean isAddingNew = (null == editID || "null".equals(editID));
	boolean isCategorySelected = (null != catID && !"0".equals(catID));
	boolean isSubCategorySelected = (null != subCatID && !"0".equals(subCatID));
	if (isAddingNew && isSubCategorySelected) {
		pqBean.subCategoryID = subCatID;
		psBean.setFromDB(pqBean.subCategoryID);
		catID=psBean.categoryID;
		isCategorySelected = true;		
	}//if
	if (!isAddingNew) {
		pqBean.setFromDB(editID);
		if (!isCategorySelected) {
			psBean.setFromDB(pqBean.subCategoryID);
			catID=psBean.categoryID;
			isCategorySelected = true;
		}//if
	}//if
	if (null == newOption)
		newOption = "";
	if ("Submit".equals(actionString)) {
		pqBean.setFromRequest(request);
//		if (isTypeSelected)
//			pqBean.questionType = request.getParameter("questionType");
		if (pqBean.isOK()) {
			if (isAddingNew) {
				pqBean.writeNewToDB(pageContext);
				pqBean.renumberPQF(pqBean.subCategoryID,action.getAuditTypeID());
				pcBean.updateNumRequiredCounts(action.getAuditTypeID());
			} else {
				pqBean.writeToDB();
				pqBean.renumberPQF(pqBean.subCategoryID,action.getAuditTypeID());
				pcBean.updateNumRequiredCounts(action.getAuditTypeID());
			}//else
			response.sendRedirect("pqf_editQuestions.jsp?editSubCatID="+pqBean.subCategoryID+"&editCatID="+catID);
			return;
		}//if
	}//if
	if ("Add Option".equals(actionString)) {
		poBean.setFromRequest(request);
		if (poBean.isOK()) {
			poBean.writeNewToDB();
			poBean.renumberPQFOptions(editID);
		}//if
	}//if
	if ("deleteOption".equals(actionString)) {
		String deleteOptionID = request.getParameter("deleteOptionID");
		poBean.deleteOption(deleteOptionID);
		poBean.renumberPQFOptions(editID);
	}//if.
	if ("Change Numbering".equals(actionString)) {
		poBean.updateNumbering(request);
		poBean.renumberPQFOptions(editID);
	}//if
%>

<html>
<head>
<title>Audit Question</title>
</head>
<body>
<h1>Audit Management
<span class="sub"><% 	if (isAddingNew) 		out.print("Add ");
	else				out.print("Edit ");
%>
                    <%=action.getAuditType().getAuditName()%> Question</span>
</h1>
<div><a href="AuditTypeChoose.action">Select a different Audit Type</a></div>

	          <form name="form1" method="post" action="pqf_editQuestion.jsp">
				<input type="hidden" name="auditTypeID" value="<%=action.getAuditTypeID() %>" />
				<input type="hidden" name="editID" value="<%=editID%>">
                <table border="0" cellspacing="0" cellpadding="1" class="blueMain">
                  <tr align="center" class="blueMain">
                    <td class="redMain"><%=pqBean.getErrorMessages()%><%=poBean.getErrorMessages()%></td>
                  </tr>
                  <tr align="center">
				    <td><br>
                      <table border="1" cellpadding="5" cellspacing="0" bordercolor="#FFFFFF" class="blueMain">
                        <tr>
                          <td align="right" class="redMain">Audit Type:</td>
                          <td align="left" class="blueMain"><%=action.getAuditType().getAuditName()%></td>
                        </tr>
                        <tr>
                          <td align="right" class="redMain">Category:</td>
                          <td><%=pcBean.getPqfCategorySelectDefaultSubmit("catID","blueMain",catID,action.getAuditTypeID())%></td>
                        </tr>
<%	if (isCategorySelected) { %>
                        <tr>
                          <td align="right" class="redMain">Sub Category:</td>
                          <td><%=psBean.getPqfSubCategorySelect("subCategoryID","blueMain",pqBean.subCategoryID,catID)%></td>
                        </tr>
                        <tr>
                          <td align="right" class="redMain">Type:</td>
                          <td>
							<%=pqBean.getPQFTypeSelect("questionType","blueMain",pqBean.questionType)%>
		                  </td>
                        </tr>
<%//		if (!(isAddingNew && !isTypeSelected)) {
%>                      <tr>
                          <td align="right" class="redMain">Title:</td>
                          <td><input name="title" type="text" class="forms" value="<%=pqBean.title%>" size="50"></td>
                        </tr>
                        <tr>
                          <td align="right" class="redMain">Number:</td>
                          <td><input name="number" type="text" class="forms" value="<%=pqBean.number%>" size="3"></td>
                        </tr>
                        <tr>
                          <td align="right" class="redMain">Group With Previous:</td>
                          <td><%=Inputs.getYesNoRadio("isGroupedWithPrevious","forms",pqBean.isGroupedWithPrevious)%></td>
                        </tr>
                        <tr>
                          <td align="right" class="redMain">Required:</td>
                          <td><%=Inputs.getRadioInput("isRequired","forms",pqBean.isRequired,pqBean.YES_NO_DEPENDS_OPTIONS_ARRAY)%></td>
                        </tr>
                        <tr>
                          <td align="right" class="redMain">Depends on ID:</td>
                          <td><input name="dependsOnQID" type="text" class="forms" value="<%=pqBean.dependsOnQID%>" size="3">
                            Answer: <%=Inputs.inputSelect2("dependsOnAnswer","forms",pqBean.dependsOnAnswer,pqBean.DEPENDS_ANSWER_ARRAY)%></td>
                        </tr>
                        <tr>
                          <td align="right" class="redMain">Question:</td>
                          <td><textarea name="question" cols="50" rows="5" class="forms"><%=pqBean.question%></textarea></td>
                        </tr>
                        <tr>
                          <td align="right" class="redMain">Has Requirement:</td>
                          <td><%=Inputs.getYesNoRadio("hasRequirement","forms",pqBean.hasRequirement)%></td>
                        </tr>
                        <tr>
                          <td align="right" class="redMain">OK Answer</td>
                          <td>
		                    <input name="okYes" type="checkbox" value="Yes" <%=pqBean.isOKAnswerChecked("Yes")%>>Yes  
		                    <input name="okNo" type="checkbox" value="No" <%=pqBean.isOKAnswerChecked("No")%>>No  
		                    <input name="okNA" type="checkbox" value="NA" <%=pqBean.isOKAnswerChecked("NA")%>>NA  
		                  </td>
                        </tr>
                        <tr>
                          <td align="right" class="redMain">Requirement:</td>
                          <td><textarea name="requirement" cols="50" rows="5" class="forms"><%=pqBean.requirement%></textarea></td>
                        </tr>
<%		for (int whichlink=1;whichlink<=6;whichlink++) { %>
 	 				   <tr>
				         <td align="right" valign="top" class="redMain">Link <%=whichlink%>:</td>
          	             <td>
						   URL:&nbsp;&nbsp;<input name="linkURL<%=whichlink%>" type="text" class="forms" value="<%=pqBean.getLinkURL(whichlink)%>" size="30"><br>
						   Text: <input name="linkText<%=whichlink%>" type="text" class="forms" value="<%=pqBean.getLinkText(whichlink)%>" size="30">
						 </td>
		 	           </tr>
<%		}//for
	}//if
%>
                        <tr>
                          <td align="right" class="redMain">Include on Red Flag Report:</td>
                          <td><%=Inputs.getYesNoRadio("isRedFlagQuestion","forms",pqBean.isRedFlagQuestion)%></td>
                        </tr>
                      </table>
                      <br>
                      <input name="action" type="submit" class="forms" value="Submit">
	                </td>
                  </tr>
                </table>
              </form>
<%	if (!isAddingNew && pqBean.hasOptions()) {
%>
              <table border="0" cellspacing="0" cellpadding="1" class="blueMain">
                <tr align="center">
	              <td>
                    <form name="form1" method="post" action="pqf_editQuestion.jsp">
	  		  	      <table width="657" border="0" cellpadding="1" cellspacing="1" class=blueMain>
                        <tr>
                          <td align="right" class="redMain">New Option:</td>
                          <td><input name="newOption" size=20 class="forms" value="<%=newOption%>">Num:
		  	                <input name="optionNumber" size=4 class="forms">
		  	                <input name="action" type="submit" class="forms" value="Add Option">
		                  </td>
                        </tr>
					  </table>
                      <input name=editID type=hidden value=<%=editID%>>
				    </form>
				  </td>
				</tr>                  
				<tr align="center" class="blueMain">
                  <td class="blueHeader">Edit Question Options</td>
                </tr>
                <tr align="center">
	              <td>
                    <form name="form1" method="post" action="pqf_editQuestion.jsp">	  		  	      
					  <table width="657" border="0" cellpadding="1" cellspacing="1">
                        <tr class="whiteTitle"> 
                          <td bgcolor="#003366"><a href="?orderBy=number&editID=<%=editID%>" class="whiteTitle">Num</a></td>
                          <td bgcolor="#003366"><a href="?orderBy=optionName&editID=<%=editID%>" class="whiteTitle">Option</a></td>
                          <td bgcolor="#993300"></td>
                        </tr>
<%		poBean.setList(orderBy, editID);
		while (poBean.isNextRecord()) {
%>
                        <tr class="blueMain" <%=poBean.getBGColor()%>> 
                          <td><input name="num_<%=poBean.optionID%>" type="text" class="forms" id="num_<%=poBean.optionID%>" value="<%=poBean.number%>" size="3"></td>
                          <td><%=poBean.optionName%></td>
                          <td align="center"><a href="/pqf_editQuestion.jsp?deleteOptionID=<%=poBean.optionID%>&action=deleteOption&editID=<%=editID%>">Del</a></td>
                        </tr>
<%		}//while
		poBean.closeList();
%>
                      </table>
                      <input name="action" type="submit" class="forms" value="Change Numbering">
                      <input name=editID type=hidden value=<%=editID%>>
                      <input name=catID type=hidden value=<%=catID%>>
                      <input name=subCatID type=hidden value=<%=subCatID%>>
                    </form>
                    <br><br>
			      </td>
                </tr>
              </table>
<%	}//if %>
</body>
</html>
<%	}finally{
		poBean.closeList();
	}//finally
%>
