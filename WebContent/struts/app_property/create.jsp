<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- URL --%>
<s:url action="ManageAppProperty" var="app_property_list" />
<s:url action="ManageAppProperty" method="create" var="app_property_create" />

<title>Create App Property</title>

<s:include value="../actionMessages.jsp" />

<h1 class="title">Create App Property</h1>

<s:form cssClass="well form-horizontal" action="%{#app_property_create}" name="app_property_create_form" id="app_property_create_form">
	<fieldset>
		<div class="control-group">
			<label class="control-label" for="new_property">Property</label>
			<div class="controls">
				<input type="text" name="newProperty" id="new_property">
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="new_value">Value</label>
			<div class="controls">
				<input type="text" name="newValue" id="new_value">
			</div>
		</div>
		<div class="form-actions">
			<button type="submit" class="btn btn-primary" name="save">Save</button>
			<button type="submit" class="btn" name="save_add">Save and Add</button>
			<a href="${app_property_list}" class="btn">Back to List</a>
	    </div>
	</fieldset>
</s:form>