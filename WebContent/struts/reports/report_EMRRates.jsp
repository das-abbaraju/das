<%@ taglib prefix="s" uri="/struts-tags"%>
<jsp:useBean id="sBean" class="com.picsauditing.PICS.SearchBean" scope ="page"/>
<jsp:useBean id="pqBean" class="com.picsauditing.PICS.pqf.QuestionBean" scope ="page"/>
<%
int thisYear = com.picsauditing.PICS.DateBean.getCurrentYear(this.getServletContext().getInitParameter("currentYearStart"));
%>
<html>
<head>
<title>EMR Rates</title>
<script src="js/prototype.js" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
</head>
<body>
<h1>EMR Rates Report</h1>
<s:form id="form1" name="form1" method="post">
<s:if test="%{value = permissions.corporate}">
<s:select list="operatorList" cssClass="forms" name="operator" listKey="id" listValue="name" />
</s:if>
<table border="0" cellpadding="2" cellspacing="0">
<tr class="blueMain">
<td align="right">EMR Rate Cutoff:</td>
<td>
<input name="searchEMRRate" type="text" size="5" value=<%=sBean.searchEMRRate%>></td>
<td>
<s:submit name="imageField" type="image" src="images/button_search.gif" onclick="runSearch( 'form1')" />
</td>
</tr>
<s:hidden name="showPage" value="1"/>
<s:hidden name="startsWith" />
<s:hidden name="orderBy" />
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
          <table class="report">
            <thead><tr>
              <td colspan="2">Contractor</td>
              <td><%=thisYear-1%><input name="searchYear1" type="checkbox" value="Y" <%=com.picsauditing.PICS.Utilities.checkedBox(sBean.selected_searchYear1)%> checked></td>
              <td><%=thisYear-2%><input name="searchYear2" type="checkbox" value="Y" <%=com.picsauditing.PICS.Utilities.checkedBox(sBean.selected_searchYear2)%> checked></td>
              <td><%=thisYear-3%><input name="searchYear3" type="checkbox" value="Y" <%=com.picsauditing.PICS.Utilities.checkedBox(sBean.selected_searchYear3)%> checked></td>
  			</tr></thead>
			<s:iterator value="data" status="stat">
             <tr><td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
                <td>
                  <a href="Audit.action?auditID=<s:property value="[0].get('auditID')"/>&catID=10">
                  <s:property value="[0].get('name')"/></a>
                </td>
<%		if (!"".equals(sBean.getPQFQuestionID())) {%>
                <td>
<%			if (null != sBean.selected_searchYear1){%>
                  <%=sBean.oBean.getRedFlagNoZeros(sBean.emr1,sBean.searchEMRRate)%>
<%			}//if%>
                </td>
                <td>
<%			if (null != sBean.selected_searchYear2){%>
				  <%=sBean.oBean.getRedFlagNoZeros(sBean.emr2,sBean.searchEMRRate)%>
<%			}//if%>
                </td>
				<td>
<%			if (null != sBean.selected_searchYear3){%>
				  <%=sBean.oBean.getRedFlagNoZeros(sBean.emr3,sBean.searchEMRRate)%>
<%			}//if%>
                </td>
<%		}//if%>
		  	  </tr>
</s:iterator>
</table><br>
</s:form>
<center>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div></center>
</body>
</html>
