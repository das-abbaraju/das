<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:property value="operator.name" /> Configuration</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp" />
</head>
<body>
<fieldset class="form">
	<h2 class="formLegend">General</h2>
	<ol>
		<li><label>Parent Accounts</label>
			<table class="report">
				<tbody>
					<s:iterator value="allParents">
						<tr>
							<td><a href="FacilitiesEdit.action?id=<s:property value="id" />"><s:property value="name" /></a></td>
							<td><a href="#" onclick="return false;" class="remove">Remove</a></td>
						</tr>
					</s:iterator>
				</tbody>
			</table>
		</li>
	</ol>
</fieldset>
</body>
</html>