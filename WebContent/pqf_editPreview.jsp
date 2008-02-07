<%@ page language="java" errorPage="exception_handler.jsp"%>
<%//@ page language="java" %>
<%@ include file="utilities/contractor_secure.jsp"%>
<%@ include file="includes/auditTypeSelected.jsp"%>
<jsp:useBean id="pqBean" class="com.picsauditing.PICS.pqf.QuestionBean" scope ="page"/>
<jsp:useBean id="psBean" class="com.picsauditing.PICS.pqf.SubCategoryBean" scope ="page"/>
<jsp:useBean id="pcBean" class="com.picsauditing.PICS.pqf.CategoryBean" scope ="page"/>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope ="page"/>
<jsp:useBean id="permissions" class="com.picsauditing.access.Permissions" scope="session"/>
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
  <title>PICS - Pacific Industrial Contractor Screening</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <link href="PICS.css" rel="stylesheet" type="text/css">
  <SCRIPT LANGUAGE="JavaScript" SRC="js/CalendarPopup.js"></SCRIPT>
  <SCRIPT LANGUAGE="JavaScript">document.write(getCalendarStyles());</SCRIPT>
  <SCRIPT LANGUAGE="JavaScript" ID="js1">var cal1 = new CalendarPopup();</SCRIPT>
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
        	        <td><%=pcBean.getPQFCategorySelectDefaultSubmit("catID","blueMain",catID,auditType)%></td>
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
<p>&nbsp;</p></body>
</html>
<%	}finally{
		pqBean.closeList();
		pcBean.closeList();
	}//finally
%>