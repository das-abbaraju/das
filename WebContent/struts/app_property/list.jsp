<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- URL --%>
<s:url action="ManageAppProperty" method="create" var="app_property_create" />

<title>Manage App Properties</title>

<s:include value="../actionMessages.jsp" />

<h1 class="title">Manage App Properties</h1>

<ul class="nav nav-pills">
	<li class="active">
		<a href="${app_property_create}">Add App Property</a>
	</li>
</ul>

<table class="table table-striped" id="app_property_list">
	<thead>
		<tr>
			<th class="name">Property</th>
			<th class="value">Value</th>
			<th class="actions"></th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="all" var="appProperty">
			<%-- URL --%>
			<s:url action="ManageAppProperty" method="edit" var="app_property_edit">
				<s:param name="property">
					${appProperty.property}
				</s:param>
			</s:url>
			<tr>
				<td class="name">
					${appProperty.property}
				</td>
				<td class="value">
					<p>
						${appProperty.value}
					</p>
				</td>
				<td class="actions">
					<a href="${app_property_edit}" class="btn"><i class="icon-edit"></i> Edit</a>
				</td>
			</tr>
		</s:iterator>
	</tbody>
</table>