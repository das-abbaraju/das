<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- URL --%>
<s:url action="ManageAppProperty" var="app_property_list" />
<s:url action="ManageAppProperty" method="edit" var="app_property_edit" />

<s:include value="../actionMessages.jsp" />

<s:include value="/struts/layout/_page-header.jsp">
    <s:param name="title">Edit Application Property</s:param>
</s:include>

<form action="${app_property_edit}" name="app_property_edit_form" id="app_property_edit_form" method="post">
    <s:hidden name="property" value="%{property.property}" />
   
    <label>Property</label> 
    <span class="help-block">${property.property}</span>
    
    <label for="new_value">Value</label>
    <textarea name="newValue" id="new_value" rows="4" class="input-xxlarge">${property.value}</textarea>
    
    <div class="form-actions input-xxlarge">
        <a href="${app_property_list}" class="btn">Cancel</a>
        <button type="submit" class="btn btn-success" name="save">Save</button>
    </div>
</form>