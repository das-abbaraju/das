<%//@ page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@ page language="java" import="com.picsauditing.PICS.*"%>
<%@ include file="utilities/contractor_secure.jsp"%>
<jsp:useBean id="pqBean" class="com.picsauditing.PICS.pqf.QuestionBean" scope ="page"/>
<jsp:useBean id="pcBean" class="com.picsauditing.PICS.pqf.CategoryBean" scope ="page"/>
<jsp:useBean id="psBean" class="com.picsauditing.PICS.pqf.SubCategoryBean" scope ="page"/>
<jsp:useBean id="pdBean" class="com.picsauditing.PICS.pqf.DataBean" scope ="page"/>
<jsp:useBean id="pconBean" class="com.picsauditing.PICS.pqf.Constants" scope ="page"/>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope ="page"/>
<jsp:useBean id="permissions" class="com.picsauditing.access.Permissions" scope="session" />
<%try{
	String conID = request.getParameter("id");
	String id = request.getParameter("id");
	String catID = "27";
	String licenseNum = "";
	String state = "";
	aBean.setFromDB(conID);
	cBean.setFromDB(conID);
	pdBean.setFromDB(conID,catID);
	psBean.setPQFSubCategoriesArray(catID);
%>
<html>
<head>
  <title>PICS - Pacific Industrial Contractor Screening</title>
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
<%	if (pBean.isAdmin()) { %>						
	 		    <%@ include file="utilities/adminContractorNav.jsp"%>
<%	} else if (pBean.isAuditor()) { %>
			    <%@ include file="utilities/auditorContractorNav.jsp"%>					
<%	} //if%>
				</td>
			  </tr>
<%	pcBean.setFromDB(catID); %>
    		  <tr align="center" class="blueMain">
                <td class="redMain">&nbsp;</td>
   			  </tr>
  			  <tr align="center">
				<td align="left">
  				  <table width="657" border="0" cellpadding=0 cellspacing=0>
                    <tr class="blueMain">
                      <td bgcolor="#003366" colspan=2 align="center" class="whiteTitle">STATES LICENSED IN (CONTRACTORS LICENSE)</td>
                    </tr>
<%	for (java.util.ListIterator li=psBean.subCategories.listIterator();li.hasNext();) {
		String subCatID = (String)li.next();
		String subCat = (String)li.next();
		pqBean.setSubListWithData("number",subCatID,conID);
		int stateCount = 0;
		while (pqBean.isNextRecord()) {
			if (!"".equals(pqBean.getOriginalAnswerView())) {
				out.println(pqBean.getTitleLine("blueMain"));
				if (!"Expiration Date".equals(pqBean.question))
					stateCount++;
%>
				  <tr <%=pconBean.getBGColor(stateCount)%> class=blueMain>
                    <td valign="top" width=1%><nobr><%=pqBean.question%>
					  <%=pqBean.getOriginalAnswerView()%>
					  <%=pqBean.getVerifiedAnswerView()%>
					  <%=pqBean.getCommentView()%>
                    </td>
<%				licenseNum = pqBean.data.answer;
				state = pqBean.question;
				if (!"Expiration Date".equals(pqBean.question)) { 
%>
				  <td valign="top"><%=pconBean.displayStateLink(state, licenseNum)%></td>
<%				}else{%>
				  <td ></td>
<%				}//else%>
				</tr>
<%			} //if answer
		}//while
	}//for
%> 
			      </table>
		        </td>
                <td>&nbsp;</td>
              </tr>
            </table>
	      </td>
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td height="72" align="center" bgcolor="#003366" class="copyrightInfo">&copy;2007 
      Pacific Industrial Contractor Screening | site design: <a href="http://www.albumcreative.com" title="Album Creative Studios"><font color="#336699">ACS</font></a></td>
  </tr>
</table>
<%@ include file="includes/statcounter.jsp" %>
</body>
</html>
<%	}finally{
		pqBean.closeList();					  
	}//finally
%>