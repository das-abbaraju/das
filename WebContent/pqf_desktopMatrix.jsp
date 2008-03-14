<%@page language="java" import="com.picsauditing.PICS.*,jxl.*,jxl.write.*,jxl.format.Colour" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp"%>
<jsp:useBean id="pcBean" class="com.picsauditing.PICS.pqf.CategoryBean" scope ="page"/>
<jsp:useBean id="pqBean" class="com.picsauditing.PICS.pqf.QuestionBean" scope ="page"/>
<jsp:useBean id="oBean" class="com.picsauditing.PICS.OperatorBean" scope ="page"/>
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
	if (com.picsauditing.PICS.pqf.Constants.PQF_TYPE.equals(auditType)){
		pcBean.setOpCategoryMatrixLowRisk();
		pcBean.setOpCategoryMatrixHighRisk();
	}//if
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

<title>PICS - Pacific Industrial Contractor Screening</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <META Http-Equiv="Cache-Control" Content="no-cache">
  <META Http-Equiv="Pragma" Content="no-cache">
  <META Http-Equiv="Expires" Content="0">
  <link href="PICS.css" rel="stylesheet" type="text/css">
</head>

<body bgcolor="#EEEEEE" vlink="#003366" alink="#003366" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td valign="top" class="buttons"> 
      <table width="100%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td width="50%" bgcolor="#993300">&nbsp;</td>
          <td width="146" valign="top" rowspan="2"><a href="index.jsp"><img src="images/logo.gif" alt="HOME" width="146" height="145" border="0"></a></td>
          <td width="364"><%@ include file="utilities/mainNavigation.jsp"%></td>
          <td width="147"><img src="images/squares_rightUpperNav.gif" width="147" height="72" border="0"></td>
          <td width="50%" bgcolor="#993300">&nbsp;</td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td valign="top" align="center"><img src="images/header_reports.gif" width="321" height="72"></td>
          <td valign="top"><%@ include file="utilities/rightLowerNav.jsp"%></td>
          <td>&nbsp;</td>
        </tr>
        <tr align="center">
          <td colspan="5" align="center">
            <%@ include file="includes/nav/editPQFNav.jsp"%>
            <span class="blueHeader"><%=auditType%> Matrix</span>
			<br>
           <form action="pqf_desktopMatrix.jsp?auditType=<%=auditType%>" method="post">
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
			  <tr style="font: Verdana, Arial, Helvetica, sans-serif; font-size:12px;" <%=Utilities.getBGColor(opCount)%>>
                <td class="blueSmall" align="right"><nobr><%=opCount%>.<%=opName%> - <strong><%=ContractorBean.RISK_LEVEL_ARRAY[1]%></strong></nobr></td>
<%			pcBean.resetList();
			while (pcBean.isNextRecord()) {
//				colCount++; //*****************
//				if ("checked".equals(pcBean.getMatrixChecked(pcBean.catID,pqBean.questionID,auditType))) //**************
//					sheet.addCell(new Label(colCount,rowCount,"X",times16BoldFormat)); //**********
%>
                <td align="center"><input type="checkbox" name="opChecked_<%=ContractorBean.RISK_LEVEL_VALUES_ARRAY[1]%>_cID_<%=pcBean.catID%>_oID_<%=opID%>" <%=pcBean.getOpMatrixHighRiskChecked(pcBean.catID,opID)%>></td>
<%			}//while %>
                <td class="blueSmall" align="left"><nobr><%=opCount%>.<%=opName%></nobr></td>
              </tr>
			  <tr style="font: Verdana, Arial, Helvetica, sans-serif; font-size:12px;" <%=Utilities.getBGColor(opCount)%>>
                <td class="blueSmall" align="right"><nobr><%=opCount%>.<%=opName%> - <strong><%=ContractorBean.RISK_LEVEL_ARRAY[0]%></strong></nobr></td>
<%			pcBean.resetList();
			while (pcBean.isNextRecord()) {
//				colCount++; //*****************
//				if ("checked".equals(pcBean.getMatrixChecked(pcBean.catID,pqBean.questionID,auditType))) //**************
//					sheet.addCell(new Label(colCount,rowCount,"X",times16BoldFormat)); //**********
%>
                <td align="center"><input type="checkbox" name="opChecked_<%=ContractorBean.RISK_LEVEL_VALUES_ARRAY[0]%>_cID_<%=pcBean.catID%>_oID_<%=opID%>" <%=pcBean.getOpMatrixLowRiskChecked(pcBean.catID,opID)%>></td>
<%			}//while %>
                <td class="blueSmall" align="left"><nobr><%=opCount%>.<%=opName%></nobr></td>
              </tr>
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
          </td>
        </tr>
      </table><br><br><br>
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