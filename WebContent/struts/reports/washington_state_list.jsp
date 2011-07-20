<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Washington State Audit Report</title>
<s:include value="reportHeader.jsp" />
<script type="text/javascript">
function request(conID) {
	var check = confirm('This action cannot be undone. Are you sure you want to request a field audit for this contractor?');

	if (check) {
		$('#form1').append('<input type="hidden" value="Request" name="button" /><input type="hidden" value="' + conID + '" name="conID" />');
		$('#form1').submit();
	}

	return false;
}
</script>
<style type="text/css">
table.report {
	clear: none;
	position: static;
}

table.report td {
	height: 30px !important;
}

table.report th {
	height: 100px !important;
}
</style>
</head>
<body>
<h1>Washington State Audit Report</h1>
<s:include value="../actionMessages.jsp" />
<s:include value="filters.jsp" />
<s:if test="map.keySet().size > 0">
	<div class="right" style="line-height: 30px;">Requested field audits for <strong><s:property value="previouslyRequested.keySet().size" /></strong> contractors</div>
	<div><s:property value="report.pageLinksWithDynamicForm" escape="false" /></div>
	<div style="position: relative;">
		<table class="report" style="position: absolute; z-index: 1000; border-right: none;">
			<thead>
				<tr>
					<th class="fixed">Request Field Audit</th>
					<th class="fixed">Contractor</th>
				</tr>
			</thead>
			<tbody>
				<s:iterator value="data">
					<tr>
						<td class="center fixed">
							<s:if test="previouslyRequested.get(get('id')) == null">
								<s:form>
									<s:hidden name="conID" value="%{get('id')}" />
									<input type="submit" value="Request" name="button" class="picsbutton" onclick="return request(<s:property value="get('id')" />)" />
								</s:form>
							</s:if>
							<s:else>
								<input type="button" value="Request" class="picsbutton" disabled="disabled" title="You have requested a field audit for this contractor." />
							</s:else>
						</td>
						<td class="fixed"><a href="Audit.action?auditID=<s:property value="get('auditID')" />"><s:property value="get('name')" /></a></td>
					</tr>
				</s:iterator>
			</tbody>
		</table>
		<div style="width: 100%; overflow-x: auto; padding-bottom: 10px;">
			<table class="report">
				<thead>
					<tr>
						<th><nobr>Request Field Audit</nobr></th>
						<th>Contractor</th>
						<s:iterator value="waCategories" var="cat">
							<th><s:property value="#cat.name" /></th>
						</s:iterator>
					</tr>
				</thead>
				<tbody>
					<s:iterator value="data">
						<tr>
							<td class="center fixed">
								<s:if test="previouslyRequested.get(get('id')) == null">
									<s:form>
										<s:hidden name="conID" value="%{get('id')}" />
										<input type="submit" value="Request" name="button" class="picsbutton" onclick="return request(<s:property value="get('id')" />)" />
									</s:form>
								</s:if>
								<s:else>
									<input type="button" value="Request" class="picsbutton" disabled="disabled" title="You have requested a field audit for this contractor." />
								</s:else>
							</td>
							<td class="fixed"><nobr><a href="Audit.action?auditID=<s:property value="get('auditID')" />"><s:property value="get('name')" /></a></nobr></td>
							<s:iterator value="waCategories" var="cat">
								<td class="center"><s:if test="map.get(get('id')).contains(#cat.id)"><img src="images/okCheck.gif" alt="OK" /></s:if></td>
							</s:iterator>
						</tr>
					</s:iterator>
				</tbody>
			</table>
		</div>
	</div>
	<div><s:property value="report.pageLinksWithDynamicForm" escape="false" /></div>
</s:if>
<s:else>
	<div class="alert"><s:text name="Report.message.NoRowsFound" /></div>
</s:else>
</body>
</html>
