<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<head>
	<title>
		<s:text name="SubcontractorFlagMatrix.title" />
	</title>
	<s:include value="../reports/reportHeader.jsp" />
</head>
<body>
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
	<div class="subcontractors-flag-matrix">
		<s:if test="table.size() > 0">
			<table class="report">
				<thead>
					<tr>
						<th></th>
						<th>
							<s:text name="GeneralContractor.SubContractor" />
						</th>
						<s:if test="!permissions.generalContractor">
							<th>
								<s:text name="global.Flag" />
							</th>
						</s:if>
						<s:iterator value="distinctOperators" var="gcOp">
							<th><s:property value="#gcOp.name" /></th>
						</s:iterator>
					</tr>
				</thead>
				<s:iterator value="table.rowKeySet()" var="sub" status="stat">
					<tr>
						<td class="right">
							${stat.count}
						</td>
						<td>
							<s:url action="ContractorView" var="contractor_view">
								<s:param name="id">
									${sub.id}
								</s:param>
							</s:url>
							<a href="${contractor_view}">
								<s:property value="#sub.name" />
							</a>
							<s:if test="permissions.generalContractor">
								<s:url action="SubcontractorFacilities" var="subcontractor_facilities">
									<s:param name="id">
										${sub.id}
									</s:param>
								</s:url>
								(<a href="${subcontractor_facilities}">Edit Clients</a>)
							</s:if>
						</td>
						<s:if test="!permissions.generalContractor">
							<td class="center">
								<s:url var="contractor_flag" action="ContractorFlag">
									<s:param name="id">
										${sub.id}
									</s:param>
								</s:url>
								<a href="${contractor_flag}">
									<s:property value="clientFlags.get(#sub).smallIcon" escape="false" />
								</a>
							</td>
						</s:if>
						<s:iterator value="distinctOperators" var="gcOp2">
							<td class="center">
								<s:if test="permissions.generalContractor">
									<s:url var="contractor_flag_gc" action="ContractorFlag">
										<s:param name="id">
											${sub.id}
										</s:param>
										<s:param name="opID">
											${gcOp2.id}
										</s:param>
									</s:url>
									<a href="${contractor_flag_gc}">
										<s:property value="table.get(#sub, #gcOp2).smallIcon" escape="false" />
									</a>
								</s:if>
								<s:elseif test="table.get(#sub, #gcOp2) != null">
									<span class="okayCheck"></span>
								</s:elseif>
							</td>
						</s:iterator>
					</tr>
				</s:iterator>
			</table>
		</s:if>
		<s:else>
			<div class="alert">
				<s:text name="Report.message.NoRowsFound" />
			</div>
		</s:else>
	</div>
</body>