<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<head>
	<link rel="stylesheet" href="css/reports.css" />
</head>
<body>
	<div>
		<table class="report">
			<tr>
				<th><s:text name="GeneralContractor.SubContractor" /></th>
				<s:iterator value="gcContractorOperators" var="gcOp">
					<th><s:property value="#gcOp.name" /></th>
				</s:iterator>
			</tr>
			<s:iterator value="subcontractors" var="sub">
				<tr>
					<td><s:property value="#sub.name" /></td>
					<s:iterator value="gcContractorOperators" var="gcOp2">
						<td><s:property value="#sub.getContractorOperatorForOperator(#gcOp2).flagColor.smallIcon" escape="false" /></td>
					</s:iterator>
				</tr>
			</s:iterator>	
		</table>
	</div>
</body>