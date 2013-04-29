<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- URL --%>
<s:url action="ManageAppProperty" var="app_property_list" />
<s:url action="ManageAppProperty" method="create" var="app_property_create" />

<s:include value="../actionMessages.jsp" />

<s:include value="/struts/layout/_page-header.jsp">
    <s:param name="title">Create Application Property</s:param>
</s:include>

<form action="${app_property_create}" name="app_property_create_form" id="app_property_create_form" method="post">
    <label for="new_property">Property</label>
    <input type="text" name="newProperty" id="new_property" class="input-xxlarge">
    
    <label for="new_value">Value</label>
    <textarea name="newValue" id="new_value" rows="4" class="input-xxlarge"></textarea>
    
    <div class="form-actions input-xxlarge">
        <a href="${app_property_list}" class="btn">Cancel</a>
        <button type="submit" class="btn btn-success" name="save">Save</button>
    </div>
</form>