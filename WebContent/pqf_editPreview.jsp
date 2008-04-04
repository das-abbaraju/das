<%@page language="java" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<%@include file="includes/auditTypeSelected.jsp"%>
<jsp:useBean id="pqBean" class="com.picsauditing.PICS.pqf.QuestionBean" scope ="page"/>
<jsp:useBean id="psBean" class="com.picsauditing.PICS.pqf.SubCategoryBean" scope ="page"/>
<jsp:useBean id="pcBean" class="com.picsauditing.PICS.pqf.CategoryBean" scope ="page"/>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope ="page"/>
<%try{
	boolean isViewAll = (null != request.getParameter("viewAll"));
	String catID = request.getParameter("catID");
	boolean isCategorySelected = (null != catID);
	int numQuestions = 0;
	int numSections = 0;
	if (isCategorySelected)
		psBean.setPQFSubCategoriesArray(catID);
//	pdBean.setFromDB(conID);
//	pqBean.highlightRequired=true;
	String 	orderby = "num";
%>

<html>
<head>
  <title>Audit Preview</title>
  <SCRIPT LANGUAGE="JavaScript" SRC="js/CalendarPopup.js"></SCRIPT>
  <SCRIPT LANGUAGE="JavaScript">document.write(getCalendarStyles());</SCRIPT>
  <SCRIPT LANGUAGE="JavaScript" ID="js1">var cal1 = new CalendarPopup();</SCRIPT>
</head>
<body>
			  <table border="0" cellspacing="0" cellpadding="1" class="blueMain">
                <tr align="center" class="blueMain"><td>
				   <%@ include file="includes/nav/editPQFNav.jsp"%>
				  </td>
				</tr>
    			<tr align="center" class="blueMain">
                  <td class="blueHeader"><%=auditType%> for Super Contractors Inc.</td>
    			</tr>
	  			<tr align="center">
                  <td class="redmain"></td>
    			</tr>
	  			<tr align="center">
			      <form name="form1" method="post" action="">
        	        <td><%=pcBean.getPqfCategorySelectDefaultSubmit("catID","blueMain",catID,auditType)%></td>
				  </form>
      			</tr>
    			<tr>
                  <td>&nbsp;</td>
    			</tr>
<%	pcBean.setList("number",auditType);
	while (pcBean.isNextRecord()) {
		if (isViewAll || (isCategorySelected && catID.equals(pcBean.catID))) {
			psBean.setPQFSubCategoriesArray(pcBean.catID);

%>			    <form name="formEdit" method="post" action="">
  				  <tr align="center">
				    <td align="left">
<!--					  <input name="action" type="submit" class="forms" value="Save"> click to save your work. You may still edit your info later.<br>
-->					  <table width="657" border="0" cellpadding="1" cellspacing="1">
                        <tr class="blueMain">
                          <td bgcolor="#003366" colspan="3" align="left"><font color="#FFFFFF"><strong>Category <%=pcBean.number%> - <%=pcBean.category%></strong></font></td>
                        </tr>
<%			numSections = 0;
			for (java.util.ListIterator li=psBean.subCategories.listIterator();li.hasNext();) {
				numSections++;
				String subCatID = (String)li.next();
				String subCat = (String)li.next();
				pqBean.setSubList("number", subCatID);
%>
                        <tr class="blueMain">
                          <td bgcolor="#003366" colspan="3" align="center"><font color="#FFFFFF"><strong>Sub Category <%=numSections%> - <%=subCat%></strong></font></td>
                        </tr>
<%				numQuestions = 0;
				while (pqBean.isNextRecord()) {
					numQuestions++;
%>                      
					    <%=pqBean.getTitleLine("blueMain")%>
					    <tr <%=pqBean.getGroupBGColor()%> class=blueMain>
                          <td valign="top" <%=pqBean.getClassAttribute(null)%>>
						    <a href="pqf_editQuestion.jsp?editID=<%=pqBean.questionID%>"><%=pcBean.number%>.<%=numSections%>.<%=pqBean.number%></a></td>
						  <td valign="top" <%=pqBean.getClassAttribute(null)%>><%=pqBean.question%> <%=pqBean.getLinksWithCommas()%></td>
						  <td width=50 valign="bottom"><%=pqBean.getInputElement()%>
						  </td>
                        </tr>
<%				}//while
				pqBean.closeList();
%>
                      <br>
<%			}//for %>
					  </table>
<!--					  <input name="action" type="submit" class="forms" value="Save"> click to save your work. You may still edit your info later.
-->				    </td>
				  </tr>
			      <input type="hidden" name="numQuestions" value=<%=numQuestions%>>
			    </form>
<%		}//if
	}//while
	pcBean.closeList();
%>
			  </table>
</body>
</html>
<%	}finally{
		pqBean.closeList();
		pcBean.closeList();
	}//finally
%>