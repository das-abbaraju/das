<%@ page language="java" errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp" %>
<%@ include file="includes/auditTypeSelected.jsp"%>
<jsp:useBean id="pqBean" class="com.picsauditing.PICS.pqf.QuestionBean" scope ="page"/>
<jsp:useBean id="pcBean" class="com.picsauditing.PICS.pqf.CategoryBean" scope ="page"/>
<jsp:useBean id="psBean" class="com.picsauditing.PICS.pqf.SubCategoryBean" scope ="page"/>
<jsp:useBean id="pdBean" class="com.picsauditing.PICS.pqf.DataBean" scope ="page"/>
<%try{
	String conID = "0";
	String tempR = request.getParameter("showReqs");
	boolean showReqs =(null==tempR || "true".equals(tempR));
%>
<html>
<head>
<title>Preview Entire Audit</title>
<meta name="header_gif" content="header_prequalification.gif" />
</head>
<body>
			<table border="0" cellspacing="0" cellpadding="1" class="blueMain">
              <tr align="center" class="blueMain">
			    <td>
				  <%@ include file="includes/nav/editPQFNav.jsp"%>
				</td>
			  </tr>
    		  <tr align="center" class="blueMain">
                <td class="blueHeader"><%=auditType%> Audit for Super Contractor<br>
<%	if (showReqs) { %>
	    <a href="?showReqs=false" class="redmain">Hide Requirements</a>
<%	} else { %>	
		<a href="?showReqs=true" class="redmain">Show Requirements</a>
<%	}//else %>
				</td>
    		  </tr>
  			  <tr align="center">
				<td align="left">
<%	pcBean.setPQFCategoriesArray(auditType);
	pcBean.setList("number",auditType);
	int catCount = 0;
	while (pcBean.isNextRecord()) {
		catCount++;
		String catID = pcBean.catID;
		psBean.setPQFSubCategoriesArray(catID);
		pdBean.setFromDB(conID,catID);
		boolean isOSHA = pcBean.OSHA_CATEGORY_ID.equals(catID);
		boolean isServices = pcBean.SERVICES_CATEGORY_ID.equals(catID);
%>
  				  <table width="657" border="0" cellpadding="1" cellspacing="1">
                    <tr class="blueMain">
                      <td bgcolor="#003366" colspan=3 align="center"><font color="#FFFFFF"><strong>Category <%=pcBean.number%> - <%=pcBean.getCategoryName(catID,auditType)%></strong></font></td>
                    </tr>
<%		int numSections = 0;
		for (java.util.ListIterator li=psBean.subCategories.listIterator();li.hasNext();) {
			numSections++;
			String subCatID = (String)li.next();
			String subCat = (String)li.next();
			pqBean.setSubList("number", subCatID);
%>					  
                    <tr class="blueMain">
                      <td bgcolor="#003366" colspan="3" align="center"><font color="#FFFFFF"><strong>Sub Category <%=numSections%> - <%=subCat%></strong></font></td>
                    </tr>
<%			if (isOSHA) { %>
                    <tr><td colspan="3" align="center"><table><%@ include file="includes/pqf/view_OSHA.jsp"%></table></td></tr>
<%			} else if (isServices) { %>
                    <tr><td colspan="3" align="center"><table><%@ include file="includes/pqf/viewServices.jsp"%></table></td></tr>
<%			} else {
				int numQuestions = 0;
				while (pqBean.isNextRecord()) {
					numQuestions=numQuestions++;
%>
					<%=pqBean.getTitleLine("blueMain")%>
					<tr <%=pqBean.getGroupBGColor()%> class=blueMain>
                      <td valign="top" width="1%"><%=pcBean.number%>.<%=numSections%>.<%=pqBean.number%></td>
					  <td valign="top"><%=pqBean.question%><%=pqBean.getLinksWithCommas()%><br>
					    <%=pqBean.getOriginalAnswerView()%>
					    <%=pqBean.getVerifiedAnswerView()%>
					    <%=pqBean.getCommentView()%>
					  </td>
					  <td></td>
					  <%//=pdBean.getAnswer(pqBean.questionID, pqBean.questionType)%>
                    </tr>
<%					if (showReqs && !"".equals(pqBean.requirement)) {%>
					<tr <%=pqBean.getGroupBGColor()%> class=redMain>
                      <td valign="top" width="1%">Req:</td>
					  <td valign="top"><%=pqBean.requirement%></td>
					  <td></td>
					</tr>

<%					}//if
				}//while
			}//else
			pqBean.closeList();					  
		}//for
	}//while
	pcBean.closeList();
%>
                  </table>
				</td>
		      </tr>
			</table>
<%	}finally{
		pqBean.closeList();
		pcBean.closeList();
	}//finally
%>
</body>
</html>
