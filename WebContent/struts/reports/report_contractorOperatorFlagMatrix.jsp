<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Contractor Operator Flag Matrix</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1><s:property value="reportName"/></h1>
<s:include value="filters.jsp" />
<div><s:property value="report.pageLinksWithDynamicForm"
	escape="false" /></div>
<s:form id="contractorOperatorFlagMatrix" method="post" cssClass="forms">
<br/><br/>
		<table class="report">
		<thead>
			<tr>
				<th>&nbsp;</th>
				<s:iterator value="operatorList">
					<th><s:property value="name"/></th>
				</s:iterator>
			</tr>
		</thead>
		
		<s:iterator value="reportData.keySet()">
			<s:set name="thisContractor" value="top"/>
		
			<tr>
				<th><s:property value="#attr.thisContractor.name"/></th>
				
				<s:iterator value="operatorList">
					
					<s:set name="thisFlag" value="reportData.get(#attr.thisContractor).get(top)"/>
					<td>
						<s:if test="#attr.thisFlag != null">
							<s:url id="flagUrl" action="ContractorFlag">
								<s:param name="id" value="%{#attr.thisContractor.id}"/>
								<s:param name="opID" value="%{top.id}"/>
							</s:url>
							<s:a href="%{flagUrl}"><img src="images/icon_<s:property value="#attr.thisFlag.toLowerCase()"/>Flag.gif" width="12" height="15"></s:a>
						</s:if>
						<s:else>
							&nbsp;						
						</s:else>
					</td>
				</s:iterator>

			</tr>
		</s:iterator>
	</table>
	
</s:form>
<div><s:property value="report.pageLinksWithDynamicForm"
	escape="false" /></div>
<br clear="both"/>
</body>
</html>
				