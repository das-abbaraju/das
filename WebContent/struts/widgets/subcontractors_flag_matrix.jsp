<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<head>
	<s:if test="ajax">
		<style type="text/css">
			.subcontractors-flag-matrix
			{
				max-height: 400px;
				overflow: auto;
				width: 100%;
			}
		</style>
	</s:if>
	<s:else>
		<title>
			<s:text name="SubcontractorFlagMatrix.title" />
		</title>
		<s:include value="../reports/reportHeader.jsp" />
	</s:else>
</head>
<body>
	<s:if test="!ajax">
		<h1>
			<s:text name="SubcontractorFlagMatrix.title" />
		</h1>
		<s:include value="../reports/filters.jsp" />
		<pics:permission perm="ContractorDetails">
			<div class="right">
				<a 
					class="excel" <s:if test="report.allRows > 500">onclick="return confirm('<s:text name="JS.ConfirmDownloadAllRows"><s:param value="%{report.allRows}" /></s:text>');"</s:if> 
					href="javascript: download('SubcontractorFlagMatrix');" 
					title="<s:text name="javascript.DownloadAllRows"><s:param value="%{report.allRows}" /></s:text>"
				><s:text name="global.Download" /></a>
			</div>
		</pics:permission>
		
		<div>
			<s:property value="report.pageLinksWithDynamicForm" escape="false" />
		</div>
	</s:if>
	<div class="subcontractors-flag-matrix">
		<s:if test="table.size > 0">
			<table class="report">
				<thead>
					<tr>
						<th>
							<s:text name="GeneralContractor.SubContractor" />
						</th>
						<s:iterator value="distinctOperators" var="gcOp">
							<th><s:property value="#gcOp.name" /></th>
						</s:iterator>
					</tr>
				</thead>
				<s:iterator value="table.keySet()" var="sub">
					<tr>
						<td>
							<a href="ContractorView.action?id=<s:property value="#sub.id" />">
								<s:property value="#sub.name" />
							</a>
							(<a href="SubcontractorFacilities.action?id=<s:property value="#sub.id" />">
								Edit Clients
							</a>)
						</td>
						<s:iterator value="distinctOperators" var="gcOp2">
							<td class="center">
								<s:url var="contractor_flag" action="ContractorFlag">
									<s:param name="id" value="%{#sub.id}" />
									<s:param name="opID" value="%{#gcOp2.id}" />
								</s:url>
								<a href="${contractor_flag}">
									<s:property value="table.get(#sub).get(#gcOp2).smallIcon" escape="false" />
								</a>
							</td>
						</s:iterator>
					</tr>
				</s:iterator>
			</table>
			<s:if test="!ajax">
				<div>
					<s:property value="report.pageLinksWithDynamicForm" escape="false" />
				</div>
			</s:if>
		</s:if>
		<s:else>
			<div class="alert">
				<s:text name="Report.message.NoRowsFound" />
			</div>
		</s:else>
	</div>
</body>