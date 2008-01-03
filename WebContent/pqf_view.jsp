<%//@ page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@ page language="java" import="com.picsauditing.PICS.*"%>
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
	String catID = request.getParameter("catID");
	boolean isCategorySelected = (null != catID && !"0".equals(catID));
	boolean isOSHA = pcBean.OSHA_CATEGORY_ID.equals(catID);
	boolean isServices = pcBean.SERVICES_CATEGORY_ID.equals(catID);
	aBean.setFromDB(conID);
	cBean.setFromDB(conID);
	//temporary to forward them to ncms imported data if it is linked up
	if (com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE.equals(auditType) && "Yes".equals(cBean.hasNCMSDesktop)) {
		response.sendRedirect("pqf_viewNCMS.jsp?id="+conID+"&auditType="+auditType);
		return;
	}//if	 
//	pqBean.setSubList("number", catID);
	pdBean.setFromDB(conID,catID);
	if (isCategorySelected)
		psBean.setPQFSubCategoriesArray(catID);
		int catCount = 0;
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
                <td width="676"><%@ include file="includes/nav/secondNav.jsp"%></td>
              </tr>
              <tr align="center" class="blueMain">
                <td class="blueHeader"><%=auditType%> for <%=aBean.name%></td>
              </tr>
              <tr align="center">
                <td class="blueMain">Date Submitted: <span class="redMain"><strong><%=cBean.getAuditSubmittedDate(auditType)%></strong></span></td>
              </tr>
<%	if (com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE.equals(auditType) || com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE.equals(auditType)) {%>
			  <tr align="center">
                <td class="blueMain">Safety Manual: <span class="redMain"><%=pdBean.getUploadLink()%>
                        </span></td>
              </tr>
              <tr align="center">
                <td class="blueMain">Date Closed: <span class="redMain"><strong><%=cBean.getAuditClosedDate(auditType)%></strong></span>
                <%=cBean.getValidUntilDate(auditType)%>
                </td>
              </tr>
<%	}//if%>
<%	if (!com.picsauditing.PICS.pqf.Constants.PQF_TYPE.equals(auditType)) { %>
              <tr align="center">
                <form name="form1" method="post" action="pqf_view.jsp">
                  <td><%=pcBean.getPQFCategorySelectDefaultSubmit("catID","blueMain",catID,auditType)%></td>
                  <input type="hidden" name="id" value="<%=conID%>">
                  <input type="hidden" name="auditType" value="<%=auditType%>">
                </form>
              </tr>
<%	}//if
	if (isCategorySelected) {
		pcBean.setFromDBWithData(catID,conID);
//		pcBean.setFromDB(catID);
%>
              <tr align="center">
                <td class="blueMain"><%//include category specific links here
		if ("18".equals(pcBean.number)) { %>
                  <a href="con_stateLicenses.jsp?id=<%=conID%>">Check Licenses</a>
<%		}//if %>
                </td>
              </tr>
              <tr align="center">
                <td align="left">
                  <table width="657" border="0" cellpadding="1" cellspacing="1">
                    <tr class="blueMain">
                      <td bgcolor="#003366" colspan=3 align="center"><font color="#FFFFFF"><strong>Category <%=pcBean.number%> - <%=pcBean.category%></strong></font></td>
                    </tr>
                    <tr class="blueMain">
<%		if (com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE.equals(auditType) || com.picsauditing.PICS.pqf.Constants.DA_TYPE.equals(auditType) || com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE.equals(auditType)) {%>
                      <td colspan=3 align="center">Percent Complete: <%=pcBean.getPercentShow(pcBean.percentVerified)%><%=pcBean.getPercentCheck(pcBean.percentVerified)%></td>
<%		} else {%>
                      <td colspan=3 align="center">Percent Complete: <%=pcBean.getPercentShow(pcBean.percentCompleted)%><%=pcBean.getPercentCheck(pcBean.percentCompleted)%></td>
                    </tr>
<%		}//else
		if ("Yes".equals(pcBean.applies)){
			int numSections = 0;
			for (java.util.ListIterator li=psBean.subCategories.listIterator();li.hasNext();) {
				numSections++;
				String subCatID = (String)li.next();
				String subCat = (String)li.next();
				pqBean.setSubListWithData("number",subCatID,conID);
				if (isOSHA) { %>
                    <%@ include file="includes/pqf/view_OSHA.jsp"%>
<%				} else if (isServices) { %>
                    <tr class="blueMain">
                      <td bgcolor="#003366" colspan="3" align="center"><font color="#FFFFFF"><strong>Sub Category <%=catCount%>.<%=numSections%> - <%=subCat%></strong></font></td>
                    </tr>
                    <%@ include file="includes/pqf/viewServices.jsp"%>
<%				} else {%>
                    <tr class="blueMain">
                      <td bgcolor="#003366" colspan="3" align="center"><font color="#FFFFFF"><strong>Sub Category <%=pcBean.number%>.<%=numSections%> - <%=subCat%></strong></font></td>
                    </tr>
<%					int numQuestions = 0;
					while (pqBean.isNextRecord()) {
						numQuestions = numQuestions + 1;
%>
                    <%=pqBean.getTitleLine("blueMain")%>
                    <tr <%=pqBean.getGroupBGColor()%> class=blueMain>
                      <td valign="top" width="1%"><%=pcBean.number%>.<%=numSections%>.<%=pqBean.number%></td>
                      <td valign="top"><%=pqBean.question%><%=pqBean.getLinksWithCommas()%><br>
                        <%=pqBean.getOriginalAnswerView()%>
                        <%=pqBean.getVerifiedAnswerView()%>
                        <%=pqBean.getCommentView()%>					  </td>
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
		}//else
%>
                  </table>
                </td>
              </tr>
<%	}//if
	if (!isCategorySelected) {
//		pdBean.setFilledOut(conID);
%>
              <tr>
                <td>
                  <table width="657" border="0" cellpadding="1" cellspacing="1">
                    <tr class="whiteTitle"> 
                      <td bgcolor="#003366" width=1%>Num</td>
                      <td bgcolor="#003366">Category</td>
                      <td bgcolor="#993300">% Complete</td>
                    </tr>
<%		pcBean.setListWithData("number",auditType,conID);
		while (pcBean.isNextRecord(pBean,conID)){
			if ((!com.picsauditing.PICS.pqf.Constants.PQF_TYPE.equals(auditType) || pBean.isAdmin() || "Yes".equals(pcBean.applies)) &&
					(!com.picsauditing.PICS.pqf.Constants.PQF_TYPE.equals(auditType) || !(pBean.isOperator() || pBean.isCorporate()) || pBean.oBean.PQFCatIDsAL.contains(pcBean.catID))){
				catCount++;
%>
                    <tr class="blueMain" <%=Utilities.getBGColor(catCount)%>>
                      <td align=right><%=catCount%>.</td>
                      <td><a href="pqf_view.jsp?auditType=<%=auditType%>&catID=<%=pcBean.catID%>&id=<%=conID%>"><%=pcBean.category%></a></td>
<%				String showPercent = "";
				if (com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE.equals(auditType) || com.picsauditing.PICS.pqf.Constants.DA_TYPE.equals(auditType) || com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE.equals(auditType))
					showPercent = pcBean.percentVerified;
				else
					showPercent = pcBean.percentCompleted;
%>
                      <td><%=pcBean.getPercentShow(showPercent)%><%=pcBean.getPercentCheck(showPercent)%></td>
                    </tr>
<%			}//if
		}//while
		pcBean.closeList();
%>
                  </table>					
                </td>
              </tr> 
<%	
	}//if %>
            </table>
          </td>
          <td>&nbsp;</td>
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