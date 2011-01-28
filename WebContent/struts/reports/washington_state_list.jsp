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
</head>
<body>
<h1>Washington State Audit Report</h1>
<s:include value="../actionMessages.jsp" />
<s:include value="filters.jsp" />
<br />
<s:if test="map.keySet().size > 0">
	<table class="report">
		<thead>
			<tr>
				<th>Request Field Audit</th>
				<th></th>
				<s:iterator value="waCategories" var="cat">
					<th><s:property value="#cat.name" /></th>
				</s:iterator>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="map.keySet()" var="con">
				<tr>
					<td class="center">
						<s:if test="previouslyRequested.get(#con) == null">
							<s:form>
								<s:hidden name="conID" value="%{#con.id}" />
								<input type="submit" value="Request" name="button" class="picsbutton" onclick="return request(<s:property value="#con.id" />)" />
							</s:form>
						</s:if>
						<s:else>
							<input type="button" value="Request" class="picsbutton" disabled="disabled" title="You have requested a field audit for this contractor." />
						</s:else>
					</td>
					<td><a href="ContractorView.action?id=<s:property value="#con.id" />"><s:property value="#con.name" /></a></td>
					<s:iterator value="waCategories" var="cat">
						<td class="center"><s:if test="map.get(#con).contains(#cat)"><img src="images/okCheck.gif" alt="OK"s /></s:if></td>
					</s:iterator>
				</tr>
			</s:iterator>
		</tbody>
	</table>
</s:if>
<s:else>
	<div class="alert">No rows found matching the given criteria. Please try again.</div>
</s:else>

</body>
</html>
