<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<table class="report">
	<thead>
		<tr>
			<th>Locale</th>
			<th>Translation</th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="values">
			<tr>
				<td>
					<s:property value="getLocale()" />
				</td>
				<td>
					<s:property value="getValue()" />
				</td>
			</tr>
		</s:iterator>
	</tbody>
</table>

<a class="edit translate" href="ManageTranslations.action?button=Search&key=AuditQuestion.<s:property value="id"/>." target="_BLANK">Manage Translations</a>