<%@page language="java" import="com.picsauditing.PICS.*,jxl.*,jxl.write.*,jxl.format.Colour" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope ="page"/>
<jsp:useBean id="sBean" class="com.picsauditing.PICS.SearchBean" scope ="page"/>
<jsp:useBean id="pcBean" class="com.picsauditing.PICS.pqf.CategoryBean" scope ="page"/>
<jsp:useBean id="pqBean" class="com.picsauditing.PICS.pqf.QuestionBean" scope ="page"/>
<%
permissions.tryPermission(OpPerms.ManageAudits);
try{
	String auditType = com.picsauditing.PICS.pqf.Constants.PQF_TYPE;
	int REPEAT_COL_NUM = 20;
	// save matrix
	if ("Save".equals(request.getParameter("action")))
		pcBean.saveMatrix(request.getParameterNames(),auditType);
	pcBean.setCategoryMatrix(auditType);

	java.util.ArrayList operators = oBean.getOperatorsAL();
	int count = 0;

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
<title>Operator Matrix</title>
</head>
<body>
            <%@ include file="includes/nav/editPQFNav.jsp"%>
            <span class="blueHeader">Desktop Matrix</span>
			<br>
           <form action="pqf_desktopMatrix.jsp" method="post">
           <input name="action" type="submit" value="Save"><br><br>
            <table width="657" border=1 cellpadding=0 cellspacing=0 bordercolor=777777>
              <tr>
				<td bgcolor="#993300" align="right">
				  <table><tr class="whiteTitle"><td id="rotated_text" align="right" valign="bottom">Category</td></tr><tr><td class="whiteTitle">Type of Work</td></tr></table>
				</td>
<%	pcBean.setList("number", auditType);
	while (pcBean.isNextRecord()) {
//		sheet.addCell(new Label(colCount++,rowCount,pcBean.category,times16BoldFormat)); //************
%>
                <td <%=Utilities.getBGColor(pcBean.count)%> class="blueSmall" id="rotated_text" style="font-size:10px;"><%=pcBean.category%></td>
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
                <td align="center"><input type="checkbox" name="checked_cID_<%= pcBean.catID %>_qID_<%=pqBean.questionID %>" <%=pcBean.getMatrixChecked(pcBean.catID,pqBean.questionID,auditType)%>></td>
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
                <td <%=Utilities.getBGColor(pcBean.count)%> class="blueSmall" id="rotated_text" style="font-size:10px;"><%=pcBean.category%></td>
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
//			if ("checked".equals(pcBean.getMatrixChecked(pcBean.catID,pqBean.questionID))) //**************
//				sheet.addCell(new Label(colCount,rowCount,"X",times16BoldFormat)); //**********
%>
                <td align="center"><input type="checkbox" name="checked_cID_<%= pcBean.catID %>_qID_<%=pqBean.questionID %>" <%=pcBean.getMatrixChecked(pcBean.catID,pqBean.questionID)%>></td>
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
                <td <%=Utilities.getBGColor(pcBean.count)%> class="blueSmall" id="rotated_text" style="font-size:10px;"><%=pcBean.category%></td>
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
                <td <%=Utilities.getBGColor(pcBean.count)%> class="blueSmall" id="rotated_text" style="font-size:10px;"><%=pcBean.category%></td>
<%			}//while %>
            	 <td><input name="action" type="submit" value="Save"></td>
              </tr>
<%		}//if %>
			  <tr style="font: Verdana, Arial, Helvetica, sans-serif; font-size:12px;" <%=Utilities.getBGColor(pqBean.count)%>>
                <td class="blueSmall" align="right"><nobr><%=pqBean.count%>.<%=pqBean.question%></nobr></td>
<%		pcBean.resetList();
		while (pcBean.isNextRecord()) {
//			colCount++; //*****************
//			if ("checked".equals(pcBean.getMatrixChecked(pcBean.catID,pqBean.questionID))) //**************
//				sheet.addCell(new Label(colCount,rowCount,"X",times16BoldFormat)); //**********
%>
                <td align="center"><input type="checkbox" name="checked_cID_<%= pcBean.catID %>_qID_<%=pqBean.questionID %>" <%=pcBean.getMatrixChecked(pcBean.catID,pqBean.questionID)%>></td>
<%		}//while %>
                <td class="blueSmall" align="left"><nobr><%=pqBean.count%>.<%=pqBean.question%></nobr></td>
              </tr>
<%	}//while 
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