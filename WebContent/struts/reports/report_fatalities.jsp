<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Fatalities</title>
<script src="js/prototype.js" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
</head>
<body>
<h1>Fatalities Report</h1>
	  <s:form id="form1" name="form1" method="post">
		<s:if test="%{value = permissions.corporate}">
          <table border="0" cellpadding="2" cellspacing="0">
            <tr align="center" >
              <td><s:select list="operatorList" cssClass="forms" name="operator" listKey="id" listValue="name" /></td>
              <td><s:submit name="imageField" type="image" src="images/button_search.gif" onclick="runSearch( 'form1')" /></td>
            </tr>
          </table>
			</s:if>
<s:hidden name="showPage" value="1"/>
<s:hidden name="startsWith" />
<s:hidden name="orderBy" />
</s:form>

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
          <table class="report">
            <thead><tr> 
              <td></td>
	    	  <th><a href="?orderBy=a.name" >Contractor</a></th>
              <td><s:property value="year-1" /></td>
              <td><s:property value="year-2" /></td>
              <td><s:property value="year-3" /></td>
  			</tr></thead>
<!--TODO Add in the Contractor FlagColor-->
<s:iterator value="data" status="stat">
			  <tr>
                <td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
				<td>
				<a href="Audit.action?auditID=<s:property value="[0].get('auditID')"/>"	><s:property value="[0].get('name')"/></a></td>
<!--Need to fix this before the year end-->				
				<td class="center"><s:property value="[0].get('fatalities1')" /></td>
				<td class="center"><s:property value="[0].get('fatalities2')" /></td>
				<td class="center"><s:property value="[0].get('fatalities3')" /></td>
		  	  </tr>
</s:iterator>
</table><br>
<center>
<s:property value="report.pageLinksWithDynamicForm" escape="false"/>
</center>
</body>
</html>
  			