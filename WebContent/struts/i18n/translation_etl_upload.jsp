<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Import/Export Translations</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
</head>
<body>
<s:include value="../actionMessages.jsp" />
<s:form enctype="multipart/form-data" method="post">
	<table class="report">
		<thead>
			<tr>
				<s:iterator value="allLocales">
					<td colspan="3"><s:property /></td>
				</s:iterator>
			</tr>
			<tr>
				<s:iterator value="allLocales">
					<td>Key</td>
					<td>New</td>
					<td>Old</td>
				</s:iterator>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="allKeys" id="key">
				<tr>
					<s:iterator value="allLocales" id="locale">
						<td><s:property value="importedTranslations.get(#key, #locale).get(0).key" /></td>
						<td><s:property value="importedTranslations.get(#key, #locale).get(0).value" /></td>
						<td><s:property value="importedTranslations.get(#key, #locale).get(1).value" /></td>
					</s:iterator>
				</tr>
			</s:iterator>
			<tr>
				<td colspan="<s:property value="allLocales.size * 3" />" class="center" style="font-weight: bold;">
					<s:property value="allKeys.size" /> total changes
				</td>
			</tr>
		</tbody>
	</table>
	
	<s:submit action="TranslationETL!save" value="Save" cssClass="picsbutton positive" />
</s:form>
</body>
</html>