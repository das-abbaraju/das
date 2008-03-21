<%@page language="java" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<jsp:useBean id="pqBean" class="com.picsauditing.PICS.pqf.QuestionBean" scope ="page"/>
<jsp:useBean id="psBean" class="com.picsauditing.PICS.pqf.SubCategoryBean" scope ="page"/>
<jsp:useBean id="pcBean" class="com.picsauditing.PICS.pqf.CategoryBean" scope ="page"/>
<jsp:useBean id="pdBean" class="com.picsauditing.PICS.pqf.DataBean" scope ="page"/>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope ="page"/>
<%try{
	String auditType = request.getParameter("auditType");
	if (null==auditType || "".equals(auditType))
		auditType = com.picsauditing.PICS.pqf.Constants.PQF_TYPE;
	boolean isViewAll = true;
	String conID = request.getParameter("id");
	String id = request.getParameter("id");
	aBean.setFromDB(conID);
	cBean.setFromDB(conID);
	cBean.tryView(permissions);
%>

<html>
<head>
  <title>Print PQF</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <link href="PICS.css" rel="stylesheet" type="text/css">
</head>
<body bgcolor="#EEEEEE" background="images/watermark.gif" vlink="#003366" alink="#003366" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onLoad="window.print();">
<table border="0" cellspacing="0" cellpadding="1" class="blueMain">
  <tr align="center" class="blueMain">
    <td class="blueHeader"><%=auditType%> for <%=aBean.name%></td>
  </tr>
<%
	pcBean.setListWithData("number",auditType,conID);
	int catCount = 0;
	while (pcBean.isNextRecord(pBean,conID)){
		if ("Yes".equals(pcBean.applies) && (!com.picsauditing.PICS.pqf.Constants.PQF_TYPE.equals(auditType) ||
				!(pBean.isOperator() || pBean.isCorporate()) || pBean.oBean.PQFCatIDsAL.contains(pcBean.catID))){
			catCount++;
			String catID = pcBean.catID;
			psBean.setPQFSubCategoriesArray(catID);
			if (conID == null || "".equals(conID))
				conID = "0";
			pdBean.setFromDB(conID,catID);
			boolean isOSHA = pcBean.OSHA_CATEGORY_ID.equals(catID);
			boolean isServices = pcBean.SERVICES_CATEGORY_ID.equals(pcBean.catID);
%>
  <tr align="center">
    <td align="left">
      <table width="657" bordercolor=003366 border="1" cellpadding="1" cellspacing="0" class="break">
        <tr class="blueMain">
          <td bgcolor="#003366" colspan="3" align="center"><font color="#FFFFFF"><strong>Category <%=catCount%> - <%=pcBean.category%></strong></font></td>
        </tr>
<%			int numSections = 0;
			for (java.util.ListIterator li=psBean.subCategories.listIterator();li.hasNext();) {
				numSections++;
				String subCatID = (String)li.next();
				String subCat = (String)li.next();
				pqBean.setSubListWithData("number", subCatID,conID);
				if (isOSHA) {
%>
        <tr><td colspan="3" align="center"><table><%@ include file="includes/pqf/view_OSHA.jsp"%></table></td></tr>
<%				} else if (isServices) { %>
        <tr class="blueMain">
          <td bgcolor="#003366" colspan="3" align="center"><font color="#FFFFFF"><strong>Sub Category <%=catCount%>.<%=numSections%> - <%=subCat%></strong></font></td>
        </tr>
        <tr><td colspan="3" align="center"><table><%@ include file="includes/pqf/viewServices.jsp"%></table></td></tr>
<%				} else {%>
        <tr class="blueMain">
          <td bgcolor="#003366" colspan="3" align="center"><font color="#FFFFFF"><strong>Sub Category <%=catCount%>.<%=numSections%> - <%=subCat%></strong></font></td>
        </tr>
<%					int numQuestions = 0;
					while (pqBean.isNextRecord()) {
						numQuestions++;
%>
	    <%=pqBean.getTitleLine("blueMain")%>
        <tr <%=pqBean.getGroupBGColor()%> class=blueMain>
          <td valign="top" width="1%"><%=catCount%>.<%=numSections%>.<%=pqBean.number%></td>
          <td valign="top"><%=pqBean.question%> <%=pqBean.getLinks()%><br>
            <%=pqBean.getOriginalAnswerView()%> <%=pqBean.getVerifiedAnswerView()%> <%=pqBean.getCommentView()%>          </td>
          <td></td>
        </tr>
<%					}//while
				}//else
				pqBean.closeList();
			}//for %>
	  </table>
	</td>
  </tr>
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