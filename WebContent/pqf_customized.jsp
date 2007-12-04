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
<jsp:useBean id="ctBean" class="com.picsauditing.PICS.CriteriaBean" scope ="page"/>
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
	int max=0;
	boolean isCategorySelected = (null != catID && !"0".equals(catID));
	boolean isOSHA = pcBean.OSHA_CATEGORY_ID.equals(catID);
	boolean isServices = pcBean.SERVICES_CATEGORY_ID.equals(catID);
	aBean.setFromDB(conID);
	cBean.setFromDB(conID);
	pdBean.setFromDB(conID,catID);
	aqBean.getQuestionID(id);
	ctBean.setAllCriterias();
	
	if ("Save".equals(action)) {
		response.sendRedirect("pqf_viewQuestions.jsp?id="+conID);
 		 if (request.getParameter("hightlighted")!=null){
			   String[] hightlighted = request.getParameterValues("hightlighted");
			   String[] criteria = request.getParameterValues("sel_criteria");
			   String[] weight = request.getParameterValues("sel_weight");
			   String[] criteria_text = request.getParameterValues("criteria_text");
			  // System.out.println("Va para el pdecode"+hightlighted.length+" "+conID+" "+criteria.length+" "+weight.length+" "+criteria_text.length);
				aqBean.decode(hightlighted,conID,criteria,weight,criteria_text);
			}  //if 
		}//save  
		
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
          <td valign="top" align="center"><img src="images/header_Questions.gif" width="321" height="72" border="0"></td>
          <td valign="top">
            <%@ include file="utilities/rightLowerNav.jsp"%>
          </td>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td colspan="3" align="center">
			<table border="0" cellspacing="0" cellpadding="1" class="blueMain">
              <%	if (!com.picsauditing.PICS.pqf.Constants.PQF_TYPE.equals(auditType)) { %>
              <%	}//if
	if (isCategorySelected) {
		pcBean.setFromDBWithData(catID,conID);
//		pcBean.setFromDB(catID);
%>
              <tr align="center">
			   
                <td width="676" align="left">
				<form name="form2" method="post" action="pqf_customized.jsp?auditType=<%=auditType%>&catID=<%=catID%>&id=<%=conID%>">
				    <table width="681" border="0" align="center" cellpadding="1" cellspacing="2">
                      <tr class="blueMain"> 
                        <td height="35" colspan="12" align="center"> <div align="center"><span class="redMain"></span> 
                            <span class="redMain"><a href="pqf_viewQuestions.jsp?id=<%=conID%>"><strong>Return 
                            to the Category List</strong></a></span></div></td>
                      </tr>
                      <tr class="blueMain"> 
                        <td colspan="6" align="center">&nbsp;</td>
                      </tr>
                      <tr class="blueMain"> 
                        <td colspan="6" align="center" bgcolor="#003366"><font color="#FFFFFF"><strong> 
                          <%if (!pcBean.number.equals("8")){%>
                          Category <%=pcBean.number%> - <%=pcBean.category%></strong></font><font color="#FFFFFF">&nbsp;</font> 
                          <%}%> </td>
                      </tr>
                      <tr class="blueMain"> 
                        <%		if (com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE.equals(auditType)) {%>
                        <td colspan=6 align="center"> <div align="left"> </div></td>
                        <%		} else {%>
                      </tr>
                      <%		}//else
	if (pcBean.doesCatApply()) {
			int numSections = 0;
			int countingQuestions = 0;
			for (java.util.ListIterator li=psBean.subCategories.listIterator();li.hasNext();) {
				numSections++;
				String subCatID = (String)li.next();
				String subCat = (String)li.next();
				pqBean.setSubListWithData("number",subCatID,conID);
				
				if (isOSHA) { %>
                      <%				} else if (isServices) { %>
                      <%@ include file="../includes/pqf/viewServices.jsp"%>
                      <%				} else {%>
                      <tr class="blueMain"> 
                        <td align="center" bgcolor="#003366"><font color="#FFFFFF"><strong>Num</strong></font></td>
                        <td align="center" bgcolor="#003366"><font color="#FFFFFF"><strong><font color="#FFFFFF">Hightlighted<strong></strong></font></strong></font></td>
                        <td bgcolor="#003366" align="center"><font color="#FFFFFF"><strong>Weight</strong></font></td>
                        <td bgcolor="#003366" align="center"><strong><font color="#FFFFFF"><strong>Criteria</strong></font></strong></td>
                        <td colspan="2" align="center" bgcolor="#003366"><font color="#FFFFFF"><strong>Questions 
                          - <%=subCat%></strong></font><font color="#FFFFFF">&nbsp;</font></td>
                      </tr>
                      <%int numQuestions = 0;
							
					while (pqBean.isNextRecord()) {
							if (aqBean.QuestionSelected("2002",pqBean.questionID))
							{
						numQuestions = numQuestions + 1;
						countingQuestions++;
						max = max + 10;
					%>
                      <tr <%=pqBean.getGroupBGColor()%> class=blueMain> 
                        <td width="5%" height="56" valign="top"><p><%=pcBean.number%> .<%=numSections%> .<%=pqBean.number%> 
                            <input type="hidden" name="CatID" value="<%=pcBean.number%>">
                            <input type="hidden" name="SubCatID" value="<%=numSections%>">
                            <input type="hidden" name="Num" value="<%=pqBean.number%>">
                          </p></td>
                        <%
								  if (!aqBean.QuestionSelected(conID,pqBean.questionID))
								  {	
		%>
                        <td width="13%" valign="top" height="56"> <p align="center"><font color="#000066"> 
                            <input type="checkbox" name="hightlighted" value="<%=pcBean.number%>.<%=numSections%>.<%=countingQuestions%>.<%=pqBean.questionID%>" checked>
                            </font> </p>
                          <p>&nbsp; </p></td>
                        <td width="8%" valign="top"><font color="#000066"> 
                          <select name="sel_weight">
                            <option value="<%=aqBean.weight%>"><%=aqBean.weight%></option>
                            <option value="1">1</option>
                            <option value="2">2</option>
                            <option value="3">3</option>
                            <option value="4">4</option>
                            <option value="5">5</option>
                          </select>
                          </font></td>
                        <%															
										if (pqBean.questionType.equals("Yes/No/NA")|| pqBean.questionType.equals("Yes/No"))
										{
		%>
                        <td width="8%" valign="top"><div align="center"><font color="#000066"> 
                          <select name="sel_criteria">
                            <option value="1">Y</option>
                            <option value="2">N</option>
                          </select> <input name="criteria_text" type="hidden" size="9" value=""> 
                          <%								}else 
										{
		%> 
                        <td width="28%" valign="top"><div align="center"><font color="#000066"> 
                            <%											ctBean.setFromDB(aqBean.criteria_id);
		%>
                            <select name="sel_criteria">
                              <option value="<%=aqBean.criteria_id%>"><%=ctBean.description%></option>
                              <%
													for (java.util.ListIterator x=ctBean.criterias.listIterator();x.hasNext();) {
														String criteriaID = (String)x.next();
														String criteriaDesc = (String)x.next();
			%>
                              <option value=<%=criteriaID%>><%=criteriaDesc%></option>
                              <%											}//for
			%>
                            </select>
                            <input name="criteria_text" type="text" size="9" value="<%=aqBean.criteria%>">
                            </font></div></td>
                        <%								}
		%>
                        <td colspan="2" valign="top"><p><%=pqBean.question%></p></td>
                        <%							}else
									{
		%>
                        <td width="4%" valign="top" height="56"> <p align="center"><font color="#000066"> 
                            <input type="checkbox" name="hightlighted" value="<%=pcBean.number%>.<%=numSections%>.<%=countingQuestions%>.<%=pqBean.questionID%>">
                            </font> </p>
                          <p>&nbsp; </p></td>
                        <td width="7%" valign="top"><font color="#000066"> 
                          <select name="sel_weight">
                            <option value="1">1</option>
                            <option value="2">2</option>
                            <option value="3">3</option>
                            <option value="4">4</option>
                            <option value="5" selected>5</option>
                          </select>
                          </font></td>
                        <%												
										if (pqBean.questionType.equals("Yes/No/NA")|| pqBean.questionType.equals("Yes/No"))
										{
		%>
                        <td width="8%" valign="top"><div align="center"><font color="#000066"> 
                          <select name="sel_criteria">
                            <option value="1">Y</option>
                            <option value="2">N</option>
                          </select> <input name="criteria_text" type="hidden" size="9" value=""> 
                          <%								}else
										{
		%> 
                        <td width="13%" valign="top"><div align="center"><font color="#000066"> 
                            <%											
		%>
                            <select name="sel_criteria">
                              <option value=""></option>
                              <%
													for (java.util.ListIterator x=ctBean.criterias.listIterator();x.hasNext();) {
														String criteriaID = (String)x.next();
														String criteriaDesc = (String)x.next();
			%>
                              <option value=<%=criteriaID%>><%=criteriaDesc%></option>
                              <%											}//for
			%>
                            </select>
                            <input name="criteria_text" type="hidden" value="" size="9">
                            </font></div></td>
                        <%								}
		%>
                        <td width="300" colspan="2" valign="top"><p><%=pqBean.question%> 
                            <%							
							}//else
						}//QuestionSelected "2002"
%>
                          </p></td>
                        <%//=pdBean.getAnswer(pqBean.questionID, pqBean.questionType)%>
                      </tr>
                      <%						if (com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE.equals(auditType) && pqBean.hasReq()){%>
                      <%						}//if
					}//while
				}//else
				pqBean.closeList();					  
			}//for
		}//else
%>
                    </table>
                    <p> 
                      <input name="action" type="submit" src="images/button_submit.gif" class="forms" value="Save">
                    </p>
                    </form>
				  </td>
              </tr>
              <%	}//if
	if (!isCategorySelected) {
//		pdBean.setFilledOut(conID);
%>
              <tr> 
                <td height="111"> <table width="657" border="0" cellpadding="1" cellspacing="1">
                    <%		pcBean.setListWithData("number",auditType,conID);
		while (pcBean.isNextRecord(pBean,conID)) {
%>
                    <tr class="blueMain" <%=pcBean.getBGColor()%>> 
                      <%	String showPercent = "";
	if (com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE.equals(auditType))
		showPercent = pcBean.percentVerified;
	else
		showPercent = pcBean.percentCompleted;
%>
                      
                      <!--		 jj 5-9-06				  <td><%//=pdBean.getFilledOut(pcBean.catID)%></td>
-->
                    </tr>
<%		}//while%>
                  </table>
                  </td>
              </tr>
<%	}//if %>
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