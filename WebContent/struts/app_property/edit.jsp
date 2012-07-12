<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- URL --%>
<s:url action="ManageAppProperty" var="app_property_list" />
<s:url action="ManageAppProperty" method="edit" var="app_property_edit" />

<title>Edit App Property</title>

<s:include value="../actionMessages.jsp" />

<h1 class="title">Edit App Property</h1>

<s:form cssClass="well form-horizontal" action="%{#app_property_edit}" name="app_property_edit_form" id="app_property_edit_form">
	<s:hidden name="property" value="%{property.property}" />
	<fieldset>
		<div class="control-group">
			<label class="control-label" for="new_property">Property</label>
			<div class="controls">
				<span class="">${property.property}</span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="new_value">Value</label>
			<div class="controls">
				<textarea name="newValue" id="new_value">${property.value}</textarea>
			</div>
		</div>
		<div class="form-actions">
			<button type="submit" class="btn btn-primary" name="save">Save</button>
			<button type="submit" class="btn" name="save_add">Save and Add</button>
			<a href="${app_property_list}" class="btn">Back to List</a>
	    </div>
	</fieldset>
</s:form>