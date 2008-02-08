<%@ page language="java" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<%@ include file="utilities/contractor_secure.jsp"%>
<jsp:useBean id="pqBean" class="com.picsauditing.PICS.pqf.QuestionBean" scope ="page"/>
<jsp:useBean id="pcBean" class="com.picsauditing.PICS.pqf.CategoryBean" scope ="page"/>
<jsp:useBean id="psBean" class="com.picsauditing.PICS.pqf.SubCategoryBean" scope ="page"/>
<jsp:useBean id="pdBean" class="com.picsauditing.PICS.pqf.DataBean" scope ="page"/>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope ="page"/>
<%try{
	//3/5/05 if audit has not been submitted (questiosn frozen), the audit data is deleted and inserted rather than updated
	// 12/20/04 jj - added timeOutWarning, timeOut javascripts, timedOut hidden form field
	String auditType = request.getParameter("auditType");
	if (null==auditType || "".equals(auditType))
		auditType = com.picsauditing.PICS.pqf.Constants.PQF_TYPE;
	String conID = request.getParameter("id");
	String id = request.getParameter("id");
	aBean.setFromDB(conID);
	cBean.setFromDB(conID);
	//temporary to forward them to ncms imported data if it is linked up
	if (com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE.equals(auditType) && "Yes".equals(cBean.hasNCMSDesktop)) {
		response.sendRedirect("pqf_viewNCMS.jsp?id="+conID+"&auditType="+auditType);
		return;
	}//if	 
	pdBean.setFilledOut(conID);
//	pqBean.setSubList("number", catID);
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
                <td><%@ include file="includes/nav/secondNav.jsp"%></td>
              </tr>
    		  <tr align="center" class="blueMain">
                <td class="blueHeader"><%=auditType%> for <%=aBean.name%></td>
    		  </tr>
              <tr align="center">
                <td class="blueMain">Date Submitted: <span class="redMain"><strong><%=cBean.getAuditSubmittedDate(auditType)%></strong></span></td>
              </tr>
<%	if (com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE.equals(auditType) || com.picsauditing.PICS.pqf.Constants.DA_TYPE.equals(auditType) || com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE.equals(auditType)) {%>
              <tr align="center">
                <td class="blueMain">Date Closed: <span class="redMain"><strong><%=cBean.getAuditClosedDate(auditType)%></strong></span></td>
              </tr>
<%	}//if%>
              <tr align="center">
                <td align="left">
<%//	pcBean.setPQFCategoriesArray(auditType);
	pcBean.setListWithData("number",auditType,conID);
	int catCount = 0;
	while (pcBean.isNextRecord(pBean,conID)) {
		if ("Yes".equals(pcBean.applies) && (!com.picsauditing.PICS.pqf.Constants.PQF_TYPE.equals(auditType) ||
				!(pBean.isOperator() || pBean.isCorporate()) || pBean.oBean.PQFCatIDsAL.contains(pcBean.catID))){
			catCount++;
			psBean.setPQFSubCategoriesArray(pcBean.catID);
			pdBean.setFromDB(conID,pcBean.catID);
			boolean isOSHA = pcBean.OSHA_CATEGORY_ID.equals(pcBean.catID);
			boolean isServices = pcBean.SERVICES_CATEGORY_ID.equals(pcBean.catID);
%>
                  <table width="657" border="0" cellpadding="1" cellspacing="1">
                    <tr class="blueMain">
                      <td bgcolor="#003366" colspan=3 align="center"><font color="#FFFFFF"><strong>Category <%=catCount%> - <%=pcBean.category%></strong></font></td>
                    </tr>
                    <tr class="blueMain">
<%			if (com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE.equals(auditType) || com.picsauditing.PICS.pqf.Constants.DA_TYPE.equals(auditType) || com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE.equals(auditType)) {%>
                      <td colspan=3 align="center">Percent Complete: <%=pcBean.getPercentShow(pcBean.percentVerified)%><%=pcBean.getPercentCheck(pcBean.percentVerified)%></td>
<%			} else {%>
                      <td colspan=3 align="center">Percent Complete: <%=pcBean.getPercentShow(pcBean.percentCompleted)%><%=pcBean.getPercentCheck(pcBean.percentCompleted)%></td>
                    </tr>
<%			}//else
			int numSections = 0;
			for (java.util.ListIterator li=psBean.subCategories.listIterator();li.hasNext();) {
				numSections++;
				String catID = pcBean.catID;
				String subCatID = (String)li.next();
				String subCat = (String)li.next();
				pqBean.setSubListWithData("number", subCatID, conID);
%>					  
                    <tr class="blueMain">
                      <td bgcolor="#003366" colspan="3" align="center"><font color="#FFFFFF"><strong>Sub Category <%=catCount%>.<%=numSections%> - <%=subCat%></strong></font></td>
                    </tr>
<%				if (isOSHA) { %>
                    <tr><td colspan="3" align="center"><table><%@ include file="includes/pqf/view_OSHA.jsp"%></table></td></tr>
<%				} else if (isServices) { %>
                    <tr><td colspan="3" align="center"><table><%@ include file="includes/pqf/viewServices.jsp"%></table></td></tr>
<%				} else {
					int numQuestions = 0;
					while (pqBean.isNextRecord()) {
						numQuestions = numQuestions + 1;
%>
                    <%=pqBean.getTitleLine("blueMain")%>
                    <tr <%=pqBean.getGroupBGColor()%> class=blueMain>
                      <td valign="top" width="1%"><%=catCount%>.<%=numSections%>.<%=pqBean.number%></td>
                      <td valign="top"><%=pqBean.question%> <%=pqBean.getLinks()%><br>
                        <%=pqBean.getOriginalAnswerView()%>
                        <%=pqBean.getVerifiedAnswerView()%>
                        <%=pqBean.getCommentView()%>
                      </td>
                      <td></td>
                      <%//=pdBean.getAnswer(pqBean.questionID, pqBean.questionType)%>
                    </tr>
<%						if ((com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE.equals(auditType) || com.picsauditing.PICS.pqf.Constants.DA_TYPE.equals(auditType) || com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE.equals(auditType)) && pqBean.hasReq()){%>
                    <tr <%=pqBean.getGroupBGColor()%> class=blueMain>
                      <td valign="top">Req:</td>
                      <td valign="top"><%=pqBean.getRequirementShow()%></td>
                      <td></td>
                    </tr>
<%						}//if
					}//while
				}//else
				pqBean.closeList();					  
			}//for
		}//if
	}//while
	pcBean.closeList();
%>
                  </table>
                </td>
              </tr>
			</table>
          </td>
          <td>&nbsp;</td>
        </tr>
      </table>
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