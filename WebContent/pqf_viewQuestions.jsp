<%//@ page language="java" errorPage="exception_handler.jsp"%>
<%@ page language="java" %>
<%@ include file="utilities/contractor_secure.jsp"%>
<jsp:useBean id="pqBean" class="com.picsauditing.PICS.pqf.QuestionBean" scope ="page"/>
<jsp:useBean id="pcBean" class="com.picsauditing.PICS.pqf.CategoryBean" scope ="page"/>
<jsp:useBean id="psBean" class="com.picsauditing.PICS.pqf.SubCategoryBean" scope ="page"/>
<jsp:useBean id="pdBean" class="com.picsauditing.PICS.pqf.DataBean" scope ="page"/>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope ="page"/>
<jsp:useBean id="aqBean" class="com.picsauditing.PICS.AccountsQuestionBean" scope ="page"/>
<jsp:useBean id="lBean" class="com.picsauditing.PICS.LightBean" scope ="page"/>
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
	pdBean.setFromDB(conID,catID);
	lBean.setFromDB(conID);
	
	if ("Save".equals(action)) {
		response.sendRedirect("pqf_viewQuestions.jsp?id="+conID);
		String[] green = request.getParameterValues("greenlight");
		String[] yellow = request.getParameterValues("yellowlight");		
		
		if (lBean.green.equals(""))
		lBean.writeNewToDB(conID,green[0],yellow[0]);
		else
		lBean.writeToDB(conID,green[0],yellow[0]);
	}
	
	if (isCategorySelected)
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
          <td valign="top" align="center"><img src="images/header_CategoryList.gif" width="321" height="72" border="0"></td>
          <td valign="top"><%@ include file="utilities/rightLowerNav.jsp"%></td>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <td height="427">&nbsp;</td>
          <td colspan="3" align="center">
			<table border="0" cellspacing="0" cellpadding="1" class="blueMain">
              <tr align="center" class="blueMain">
			    <td width="676"><p><a class=blueMain href=contractor_list.jsp>Return 
                    to Contractors List</a></p>
                  <p>&nbsp;</p></td>
	  		  <tr align="center">
                <td class="blueMain"></td>
    		  </tr>
<%
	if (!isCategorySelected) {
//		pdBean.setFilledOut(conID);
%>
				   <tr>
				     <td>
					  <table width="656" border="0" cellpadding="1" cellspacing="1">
                        <tr class="whiteTitle"> 
                          <td bgcolor="#003366" width=5%>Num</td>
                          <td width="70%" bgcolor="#003366">Category</td>
						  <td width="25%" colspan=3 align="center" bgcolor="#003366"><div align="center"><font color="#FFFFFF"><strong>Red 
                          Flag Questions<strong></font></div></td>
                         </tr>
<%		pcBean.setListWithData("number",auditType,conID);
		int rowCount = 0;
		while (pcBean.isNextRecord(pBean,conID)) {
			if ( !pcBean.number.equals("8") &&  !pcBean.number.equals("7") && !pcBean.number.equals("18") && !pcBean.number.equals("21") && !pcBean.number.equals("22")&& !pcBean.number.equals("23") && !pcBean.number.equals("25"))
			{
%>           <tr class="blueMain" <%=com.picsauditing.PICS.Utilities.getBGColor(rowCount++)%>> 
			  <td align=right><%=pcBean.number%>.</td>
<%				if (!pcBean.number.equals("8")) { %>			  
			  <td><a href="pqf_customized.jsp?auditType=<%=auditType%>&catID=<%=pcBean.catID%>&id=<%=conID%>"><%=pcBean.category%></a></td>
<%                 } else {%>
			  <td><a href="pqf_customized.jsp?auditType=<%=auditType%>&catID=<%=pcBean.catID%>&id=<%=conID%>"><%="SET  HURDLE RATES"%></a></td>                
<%				 }%>

<%						  aqBean.countQuestion(pcBean.catID, conID);
%>			  <td align=center><%=aqBean.count%></td>
<%			}
	String showPercent = "";
	if (com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE.equals(auditType))
		showPercent = pcBean.percentVerified;
	else
		showPercent = pcBean.percentCompleted;
%>               
<!--		 jj 5-9-06				  <td><%//=pdBean.getFilledOut(pcBean.catID)%></td>
-->                        </tr>
<%		}//while
		pcBean.closeList();
%>
        		  </table>
					</td>
		          </tr> 
<%	
	}//if %>
			</table>
			<form name="form2" method="post" action="pqf_viewQuestions.jsp?id=<%=conID%>">
              <table width="654" border="0" cellpadding="1" cellspacing="1">
                <tr class="whiteTitle"> 
                  <td width=29% height="40" class="blueMain">Select your percentages 
                    : </td>
                  <td width="20%" > <div align="center" class="active">Green: 
                      <input name="greenlight" type="text" id="greenlight" size="3" value="<%=lBean.green%>">
                    </div></td>
                  <td width="21%" > <div align="center" class="goldSubheadServices">Yellow: 
                      <input name="yellowlight" type="text" id="yellowlight" size="3" value="<%=lBean.yellow%>">
                    </div></td>
                  <td width="5%" ><div align="center" class="inactive"></div></td>
                  <td width="25%" ><div align="center"> 
                      <input name="action" type="submit" src="images/button_submit.gif" class="forms" value="Save">
                    </div></td>
                </tr>
              </table>
              </form>
            <p>&nbsp;</p></td>
          <td></td>
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