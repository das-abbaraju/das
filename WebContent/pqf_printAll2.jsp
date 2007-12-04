<%@ page language="java" errorPage="exception_handler.jsp"%><%//@ page language="java" %>
<%@ include file="utilities/contractor_secure.jsp"%>
<jsp:useBean id="pqBean" class="com.picsauditing.PICS.pqf.QuestionBean" scope ="page"/>
<jsp:useBean id="psBean" class="com.picsauditing.PICS.pqf.SubCategoryBean" scope ="page"/>
<jsp:useBean id="pcBean" class="com.picsauditing.PICS.pqf.CategoryBean" scope ="page"/>
<jsp:useBean id="pdBean" class="com.picsauditing.PICS.pqf.DataBean" scope ="page"/>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope ="page"/>
<%try{
	boolean isViewAll = true;
	int numQuestions = 0;
	int numSections = 0;
	String conID = request.getParameter("id");
	String id = request.getParameter("id");

%>

<html>
<head>
  <title>PICS - Pacific Industrial Contractor Screening</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <link href="PICS.css" rel="stylesheet" type="text/css">
</head>
<body bgcolor="#EEEEEE" background="images/watermark.gif" vlink="#003366" alink="#003366" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onLoad="window.print();">
<table border="0" cellspacing="0" cellpadding="1" class="blueMain">
  <tr align="center" class="blueMain">
    <td class="blueHeader">PQF for PICS</td>
  </tr>
<%
	pcBean.setList("number");
	while (pcBean.isNextRecord()) {
		numSections++;
		String catID = pcBean.catID;
		psBean.setPQFSubCategoriesArray(catID);
		pdBean.setFromDB(conID,catID);
		boolean isOSHA = pcBean.OSHA_CATEGORY_ID.equals(catID);
%>
  <tr align="center">
    <td align="left">
	  <table width="657" bordercolor=003366 border="1" cellpadding="1" cellspacing="0" class="break">
        <tr class="blueMain">
          <td bgcolor="#003366" colspan="3" align="center"><font color="#FFFFFF"><strong>Category <%=pcBean.number%> - <%=pcBean.category%></strong></font></td>
        </tr>
<%		numSections = 0;
		for (java.util.ListIterator li=psBean.subCategories.listIterator();li.hasNext();) {
			numSections++;
			String subCatID = (String)li.next();
			String subCat = (String)li.next();
			pqBean.setSubListWithData("number", subCatID);
			if (isOSHA) { %>
                    <tr>
					  <td colspan="3" align="center"><br><%@ include file="includes/pqf/view_OSHA.jsp"%></td>
					</tr>
<%			} else {
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
          <td valign="top" width="1%"><%=pcBean.number%>.<%=numSections%>.<%=pqBean.number%></td>
          <td valign="top"><%=pqBean.question%> <%=pqBean.getLinks()%><br>
            <%=pqBean.getOriginalAnswerView()%>
            <%=pqBean.getVerifiedAnswerView()%>
            <%=pqBean.getCommentView()%>
        </tr>
<%			}//while
		}//else
		pqBean.closeList();
%>
        <br>
<%		}//for %>
	  </table>
	</td>
  </tr>
<%	}//while
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