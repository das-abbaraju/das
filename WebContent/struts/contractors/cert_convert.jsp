<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
</head>
<body>
<s:include value="../actionMessages.jsp" />

<table class="report">
	<thead>
		<tr>
			<td>certID</td>
			<td>conID</td>
			<td>Contractor</td>
			<td>Expiration Date</td>
			<td>File Hash</td>
			<td>CAOs</td>
		</tr>
	</thead>
	<s:iterator value="certs">
		<tr>
			<td><s:property value="id"/></td>
			<td><s:property value="contractor.id"/></td>
			<td><s:property value="contractor.name"/></td>
			<td><s:date name="expirationDate" format="MM/dd/yy"/></td>
			<td style="font-family: monospace;"><s:property value="fileHash"/></td>
			<td>
				<table class="inner">
					<s:iterator value="caos">
						<s:if test="!permissions.operatorCorporate || permissions.insuranceOperatorID == operator.id">
						<tr>
							<td style="font-size:10px"><nobr><s:property value="audit.auditType.auditName"/></nobr></td>
							<td style="font-size:10px"><nobr><s:property value="operator.name"/></nobr></td>
							<td style="font-size:10px"><nobr><s:date name="audit.expiresDate" format="M/d/yy"/></nobr></td>
						</tr>
						</s:if>
					</s:iterator>
				</table>
			</td>
		</tr>
	</s:iterator>
</table>
</body>
</html>