<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<head>
	<title>
		<s:property value="contractor.name" />
		<s:text name="Audit.header.Documents" />
	</title>
</head>
<body>
	<div id="tabs">
		<div id="tabs-audits">
			<table class="report">
				<thead>
					<tr>
						<th>
							<s:text name="ContractorQuickDocuments.GoverningOperator" />
						</th>
						<th>
							<s:text name="Audit.header.Documents" />
						</th>
						<th>
							<s:text name="ContractorQuickDocuments.PreviousStatus" />
						</th>
						<th>
							<s:text name="ContractorQuickDocuments.CurrentStatus" />
						</th>
						<th>
							<s:text name="ContractorQuickDocuments.ChangedOn" />
						</th>
					</tr>
				</thead>
				<s:iterator value="getCaoStats(opID).keySet()" id="status">
					<tr>
						<td>
							<s:property value="#status.operator.name"/>
						</td>
						<td>
							<a href="Audit.action?auditID=<s:property value="audit.id" />">
								<s:if test="audit.auditFor.length() > 0">
									<s:property value="audit.auditFor" />
								</s:if>
								<s:property value="audit.auditType.name" />
							</a>
						</td>
						<td>
							<s:property value="getCaoStats(opID).get(#status)" />
						</td>
						<td>
							<s:text name="%{#status.status.i18nKey}" />
						</td>
						<td>
							<s:date name="#status.statusChangedDate" />
						</td>
					</tr>
				</s:iterator>
			</table>
		</div>
	</div>
</body>