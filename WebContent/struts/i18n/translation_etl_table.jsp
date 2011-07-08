<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<table class="report">
	<thead>
		<tr>
			<td>Key</td>
			<td>Locale</td>
			<td>New</td>
			<td>Old</td>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="allKeys" id="key">
			<s:iterator value="allLocales" id="locale">
				<s:if test="importedTranslations.get(#key, #locale) != null">
					<tr>
						<td><s:property value="#key" /></td>
						<td><s:property value="#locale" /></td>
						<td><s:property value="importedTranslations.get(#key, #locale).get(0).value" /></td>
						<td><s:property value="importedTranslations.get(#key, #locale).get(1).value" /></td>
					</tr>
				</s:if>
			</s:iterator>
		</s:iterator>
		<s:if test="allKeys.size > 250">
			<tr>
				<td colspan="4" class="center">...</td>
			</tr>
		</s:if>
		<tr>
			<td colspan="4" class="center" style="font-weight: bold;">
				<s:property value="allKeys.size" /> total changes
			</td>
		</tr>
	</tbody>
</table>

<s:submit action="TranslationETL!save" value="Save" cssClass="picsbutton positive" />