<%@page language="java" import="com.picsauditing.PICS.*,jxl.*,jxl.write.*,jxl.format.Colour" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp"%>
<jsp:useBean id="pcBean" class="com.picsauditing.PICS.pqf.CategoryBean" scope ="page"/>
<jsp:useBean id="pqBean" class="com.picsauditing.PICS.pqf.QuestionBean" scope ="page"/>
<jsp:useBean id="oBean" class="com.picsauditing.PICS.OperatorBean" scope ="page"/>
<%@page import="com.picsauditing.PICS.pqf.Constants" %>
<%
permissions.tryPermission(OpPerms.ManageAudits);
try{	
	String questionType ="Industry";
	String auditType = request.getParameter("auditType");
	int REPEAT_COL_NUM = 20;
	// save matrix
	if ("Save".equals(request.getParameter("action"))) {
		permissions.tryPermission(OpPerms.ManageAudits, OpType.Edit);
		pcBean.saveMatrix(request.getParameterNames(),auditType);
	}
		
	pcBean.setCategoryMatrix(auditType);
	if (com.picsauditing.PICS.pqf.Constants.PQF_TYPE.equals(auditType))
		pcBean.setOpCategoryMatrixRisk();
//*************************************
/*		WritableFont times16BoldFont = new WritableFont(WritableFont.TIMES, 10); 
		WritableCellFormat times16BoldFormat = new WritableCellFormat (times16BoldFont); 

		WritableWorkbook workbook = null;
		WritableSheet sheet = null;
		String rootPath = config.getServletContext().getRealPath("/");
		String fileDir = "tempFiles/";
		String fileName = "desktopMatrix.xls";
		String filePath = fileDir+fileName;
		int rowCount = 0;
		int colCount = 1;
		int count = 1;

		java.io.File outFile = new java.io.File(rootPath+filePath);
		workbook = Workbook.createWorkbook(outFile);

		sheet = workbook.createSheet("Desktop Matrix",0);
*/
%>
<html>
<head>
<title>Audit Desktop Matrix</title>
</head>
<body>
<h1><%=auditType%> Matrix</h1>
<div id="internalnavcontainer">
<ul id="navlist">
	<li><a href="pqf_Matrix.jsp?auditType=PQF">Edit PQF Matrix</a></li>
	<li><a href="pqf_desktopMatrix.jsp?auditType=Desktop" class="current">Edit Desktop Matrix</a></li>
</ul>
</div>
<br>
<form action="pqf_desktopMatrix.jsp?auditType=<%=auditType%>" method="post">
<input name="action" type="submit" value="Save">
<br><br>
<table border="1" cellpadding="0" cellspacing="0" bordercolor="#777777">
<tr>
				<td bgcolor="#993300" align="right">
				  <table><tr class="whiteTitle"><td id="rotated_text" align="right" valign="bottom">Category</td></tr><tr><td class="whiteTitle">Type of Work</td></tr></table>
				</td>
<%	pcBean.setList("number", auditType);
	while (pcBean.isNextRecord()) {
		%>
		<td <%=Utilities.getBGColor(pcBean.count)%> class="blueSmall" id="rotated_text" style="font-size:10px;"><%=pcBean.number%>.<%=pcBean.category%></td>
<%	}//while%>
</tr>

<%	questionType="Main Work";
	pqBean.setList("number", questionType);
//	count = 0; //*********
	while (pqBean.isNextRecord()) {
//		colCount = 0; //*********
//		rowCount++;//*************
//		count++; //*********
//		sheet.addCell(new Label(colCount,rowCount,count+"."+pqBean.question,times16BoldFormat)); //********
%>
			  <tr style="font: Verdana, Arial, Helvetica, sans-serif; font-size:12px;" <%=Utilities.getBGColor(pqBean.count)%>>
                <td class="blueSmall" align="right"><nobr><%=pqBean.count%>.<%=pqBean.question%></nobr></td>
<%		pcBean.resetList();
		while (pcBean.isNextRecord()) {
//			colCount++; //*****************
//			if ("checked".equals(pcBean.getMatrixChecked(pcBean.catID,pqBean.questionID,auditType))) //**************
//				sheet.addCell(new Label(colCount,rowCount,"X",times16BoldFormat)); //**********
%>				
                <td align="center"><input type="checkbox" name="checked_cID_<%=pcBean.catID%>_qID_<%=pqBean.questionID %>" <%=pcBean.getMatrixChecked(pcBean.catID,pqBean.questionID,auditType)%>></td>
<%		}//while %>
                <td class="blueSmall" align="left"><nobr><%=pqBean.count%>.<%=pqBean.question%></nobr></td>
              </tr>
<%	}//while %>
              <tr>
				<td bgcolor="#993300" align="right">
				  <table><tr class="whiteTitle"><td id="rotated_text" align="right" valign="bottom">Category</td></tr><tr><td class="whiteTitle">Industry</td></tr></table>
				</td>
<%	pcBean.resetList();
	while (pcBean.isNextRecord()) {
%>
                <td <%=Utilities.getBGColor(pcBean.count)%> class="blueSmall" id="rotated_text" style="font-size:10px;"><%=pcBean.number%>.<%=pcBean.category%></td>
<%	}//while%>
                <td><input name="action" type="submit" value="Save"></td>
              </tr>

<%	pqBean.closeList();
	questionType="Industry";
	pqBean.setList("number", questionType);
//	rowCount++;//**********************
//	count = 0; //*********
	while (pqBean.isNextRecord()) {
//		colCount = 0; //*********
//		rowCount++;//*************
//		count++; //*********
//		sheet.addCell(new Label(colCount,rowCount,count+"."+pqBean.question,times16BoldFormat)); //********
%>
			  <tr style="font: Verdana, Arial, Helvetica, sans-serif; font-size:12px;" <%=Utilities.getBGColor(pqBean.count)%>>
                <td class="blueSmall" align="right"><nobr><%=pqBean.count%>.<%=pqBean.question%></nobr></td>
<%		pcBean.resetList();
		while (pcBean.isNextRecord()) {
//			colCount++; //*****************
//			if ("checked".equals(pcBean.getMatrixChecked(pcBean.catID,pqBean.questionID,auditType))) //**************
//				sheet.addCell(new Label(colCount,rowCount,"X",times16BoldFormat)); //**********
%>
                <td align="center"><input type="checkbox" name="checked_cID_<%= pcBean.catID %>_qID_<%=pqBean.questionID %>" <%=pcBean.getMatrixChecked(pcBean.catID,pqBean.questionID,auditType)%>></td>
<%		}//while %>
                <td class="blueSmall" align="left"><nobr><%=pqBean.count%>.<%=pqBean.question%></nobr></td>
              </tr>
<%	}//while %>
              <tr>
				<td bgcolor="#993300" align="right">
				  <table><tr class="whiteTitle"><td id="rotated_text" align="right" valign="bottom">Category</td></tr><tr><td class="whiteTitle">Service</td></tr></table>
				</td>
<%	pcBean.resetList();
	while (pcBean.isNextRecord()) {
%>
                <td <%=Utilities.getBGColor(pcBean.count)%> class="blueSmall" id="rotated_text" style="font-size:10px;"><%=pcBean.number%>.<%=pcBean.category%></td>
<%	}//while%>
                <td><input name="action" type="submit" value="Save"></td>
              </tr>

<%	pqBean.closeList();
	questionType="Service";
	pqBean.setList("number", questionType);
//	rowCount++;//**********************
//	count = 0; //*********
	while (pqBean.isNextRecord()) {
//		colCount = 0; //*********
//		rowCount++;//*************
//		count++; //*********
//		sheet.addCell(new Label(colCount,rowCount,count+"."+pqBean.question,times16BoldFormat)); //********
		if (pqBean.count%REPEAT_COL_NUM==0) {
			pcBean.resetList();
%>
              <tr>
			    <td align="right">
                <table>
            	  <tr>
            	    <td><input name="action" type="submit" value="Save"></td>
            	    <td class="blueHeader" id="rotated_text" align="center" valign="middle">Category</td>
            	  </tr>
                </table>
               </td>
<%			while (pcBean.isNextRecord()) {%>
                <td <%=Utilities.getBGColor(pcBean.count)%> class="blueSmall" id="rotated_text" style="font-size:10px;"><%=pcBean.number%>.<%=pcBean.category%></td>
<%			}//while %>
            	 <td><input name="action" type="submit" value="Save"></td>
              </tr>
<%		}//if %>
			  <tr style="font: Verdana, Arial, Helvetica, sans-serif; font-size:12px;" <%=Utilities.getBGColor(pqBean.count)%>>
                <td class="blueSmall" align="right"><nobr><%=pqBean.count%>.<%=pqBean.question%></nobr></td>
<%		pcBean.resetList();
		while (pcBean.isNextRecord()) {
//			colCount++; //*****************
//			if ("checked".equals(pcBean.getMatrixChecked(pcBean.catID,pqBean.questionID,auditType))) //**************
//				sheet.addCell(new Label(colCount,rowCount,"X",times16BoldFormat)); //**********
%>
                <td align="center"><input type="checkbox" name="checked_cID_<%= pcBean.catID %>_qID_<%=pqBean.questionID %>" <%=pcBean.getMatrixChecked(pcBean.catID,pqBean.questionID,auditType)%>></td>
<%		}//while %>
                <td class="blueSmall" align="left"><nobr><%=pqBean.count%>.<%=pqBean.question%></nobr></td>
              </tr>
<%	}//while 
	pqBean.closeList();

//***************************************************
	if (com.picsauditing.PICS.pqf.Constants.PQF_TYPE.equals(auditType)){
%>
              <tr>
				<td bgcolor="#993300" align="right">
				  <table>
				    <tr class="whiteTitle"><td id="rotated_text" align="right" valign="bottom">Category</td></tr>
				    <tr><td class="whiteTitle">Operator</td></tr>
				  </table>
				</td>
<%	pcBean.resetList();
	while (pcBean.isNextRecord()) {
%>
                <td <%=Utilities.getBGColor(pcBean.count)%> class="blueSmall" id="rotated_text" style="font-size:10px;"><%=pcBean.number%>.<%=pcBean.category%></td>
<%	}//while%>
                <td><input name="action" type="submit" value="Save"></td>
              </tr>
<%
		java.util.ArrayList<String> operators = oBean.getOperatorsAL();
		int opCount = 0;
//		rowCount++;//**********************
//		count = 0; //*********
		for (java.util.ListIterator<String> li = operators.listIterator();li.hasNext();){
			String opID = (String)li.next();
			String opName = (String)li.next();
			opCount++;
//			colCount = 0; //*********
//			rowCount++;//*************
//			count++; //*********
//			sheet.addCell(new Label(colCount,rowCount,count+"."+pqBean.question,times16BoldFormat)); //********
			if ((opCount)%REPEAT_COL_NUM==0) {
				pcBean.resetList();
%>
              <tr>
			    <td align="right">
                <table>
            	  <tr>
            	    <td><input name="action" type="submit" value="Save"></td>
	            	    <td class="blueHeader" id="rotated_text" align="center" valign="middle">Category</td>
            	  </tr>
                </table>
               </td>
<%				while (pcBean.isNextRecord()) {%>
                <td <%=Utilities.getBGColor(pcBean.count)%> class="blueSmall" id="rotated_text" style="font-size:10px;"><%=pcBean.number%>.<%=pcBean.category%></td>
<%				}//while %>
            	 <td><input name="action" type="submit" value="Save"></td>
              </tr>
<%			}//if %>
<%			for (int riskLevel=1; riskLevel<=3; riskLevel++){ %>
			  <tr style="font: Verdana, Arial, Helvetica, sans-serif; font-size:12px;" <%=Utilities.getBGColor(opCount)%>>
                <td class="blueSmall" align="right"><nobr><%=opCount%>.<%=opName%> - <strong><%=ContractorBean.RISK_LEVEL_ARRAY[riskLevel-1]%></strong></nobr></td>
<%				pcBean.resetList();
				while (pcBean.isNextRecord()) {
//				colCount++; //*****************
//				if ("checked".equals(pcBean.getMatrixChecked(pcBean.catID,pqBean.questionID,auditType))) //**************
//					sheet.addCell(new Label(colCount,rowCount,"X",times16BoldFormat)); //**********
%>
                <td align="center"><input type="checkbox" name="opChecked_<%=riskLevel%>_cID_<%=pcBean.catID%>_oID_<%=opID%>" <%=pcBean.getOpMatrixRiskChecked(pcBean.catID,opID,riskLevel)%>></td>
<%				}//while %>
                <td class="blueSmall" align="left"><nobr><%=opCount%>.<%=opName%> - <strong><%=ContractorBean.RISK_LEVEL_ARRAY[riskLevel-1]%></strong></nobr></td>
              </tr>
<%			}//for %>
<%		}//while 
	}//if
//*******************************

	pcBean.closeList();
//	workbook.write();//*********8
//	workbook.close();//********
%>
            </table>
            <br><input name="action" type="submit" value="Save">
          </form>	
</body>
</html>
<%	}finally{
		pqBean.closeList();
		pcBean.closeList();
	}//finally
%>