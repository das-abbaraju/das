<%@page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<jsp:useBean id="pqBean" class="com.picsauditing.PICS.pqf.QuestionBean" scope ="page"/>
<jsp:useBean id="pcBean" class="com.picsauditing.PICS.pqf.CategoryBean" scope ="page"/>
<jsp:useBean id="psBean" class="com.picsauditing.PICS.pqf.SubCategoryBean" scope ="page"/>
<jsp:useBean id="pdBean" class="com.picsauditing.PICS.pqf.DataBean" scope ="page"/>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope ="page"/>
<%@page import="com.picsauditing.actions.audits.ContractorAuditLegacy"%>
<%
ContractorAuditLegacy action = new ContractorAuditLegacy();
action.setAuditID(request.getParameter("auditID"));
String auditType = action.getAudit().getAuditType().getLegacyCode();
String conID = ((Integer) action.getAudit().getContractorAccount().getId()).toString();
String id = conID;
try {
	String catID = request.getParameter("catID");
	boolean isCategorySelected = (null != catID && !"0".equals(catID));
	boolean isOSHA = pcBean.OSHA_CATEGORY_ID.equals(catID);
	boolean isServices = pcBean.SERVICES_CATEGORY_ID.equals(catID);
	aBean.setFromDB(conID);
	cBean.setFromDB(conID);
	cBean.tryView(permissions);
	Set<String> showCategoryIDs = null;
	if (permissions.isOperator()){
		showCategoryIDs = pcBean.getCategoryForOpRiskLevel(permissions.getAccountIdString(),cBean.riskLevel);
	}
	//temporary to forward them to ncms imported data if it is linked up
	if (com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE.equals(auditType) && "Yes".equals(cBean.hasNCMSDesktop)) {
		response.sendRedirect("pqf_viewNCMS.jsp?id="+conID+"&auditType="+auditType);
		return;
	}//if	 
	pdBean.setFromDB(action.getAuditID(), conID, catID);
	if (isCategorySelected)
		psBean.setPQFSubCategoriesArray(catID);
		int catCount = 0;
%>
<%@page import="java.util.Set"%>
<html>
<head>
<title>PQF for <%=aBean.name %></title>
<meta name="header_gif" content="header_prequalification.gif" />
</head>
<body>
<%@ include file="includes/nav/pqfHeader.jsp"%>

<table border="0" cellspacing="0" cellpadding="1" class="blueMain">
<%
	if (com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE.equals(auditType) || com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE.equals(auditType)) { %>
			  <tr align="center">
                <td class="blueMain">Safety Manual: <span class="redMain"><%=pdBean.getUploadLink()%>
                        </span></td>
              </tr>
<%	}//if%>
<%	if (!com.picsauditing.PICS.pqf.Constants.PQF_TYPE.equals(auditType)) { %>
              <tr align="center">
                <td><form name="form1" method="post" action="pqf_view.jsp">
                  <%=pcBean.getPqfCategorySelectDefaultSubmit("catID","blueMain",catID, action.getAudit().getAuditType().getAuditTypeID())%>
                  <input type="hidden" name="auditID" value="<%=action.getAuditID()%>">
                </form>
                </td>
              </tr>
<%	}//if
	if (isCategorySelected) {
		pcBean.setFromDBWithData(catID, action.getAuditID());
%>
              <tr align="center">
                <td class="blueMain">
                <%//include category specific links here
				if (pcBean.showLicenses()) { %>
                  <a href="con_stateLicenses.jsp?id=<%=conID%>">Check Licenses</a>
				<% } %>
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
<%	pcBean.setListWithData("number",auditType,conID);
		while (pcBean.isNextRecord(pBean,conID)){
			if ((!com.picsauditing.PICS.pqf.Constants.PQF_TYPE.equals(auditType) || pBean.isAdmin() || "Yes".equals(pcBean.applies)) &&
					(!com.picsauditing.PICS.pqf.Constants.PQF_TYPE.equals(auditType) || !(pBean.isOperator() || pBean.isCorporate()) || 
							(permissions.isCorporate() && pBean.oBean.PQFCatIDsAL.contains(pcBean.catID)) ||
							(permissions.isOperator() && showCategoryIDs.contains(pcBean.catID)))){
				catCount++;
%>
                    <tr class="blueMain" <%=Utilities.getBGColor(catCount)%>>
                      <td align=right><%=catCount%>.</td>
                      <td><a href="pqf_view.jsp?auditID=<%=action.getAuditID()%>&catID=<%=pcBean.catID%>"><%=pcBean.category%></a></td>
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
<%	}finally{
		pqBean.closeList();
		pcBean.closeList();
	}//finally
%>
</body>
</html>
