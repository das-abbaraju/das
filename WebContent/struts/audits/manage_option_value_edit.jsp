<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:form id="save">
<s:hidden name="value" value="%{value.id}" />
<s:hidden name="group" value="%{value.group.id}" />
<fieldset class="form">
	<h2 class="formLegend">Question Option</h2>
	<ol>
		<li><label>ID:</label><s:property value="value.id" /></li>
		<li><s:textfield theme="formhelp" name="value.name" /></li>
		<li><s:checkbox theme="formhelp" name="value.visible" /></li>
		<li><s:textfield theme="formhelp" name="value.number" /></li>
		<li><s:textfield theme="formhelp" name="value.score" /></li>
		<li><s:textfield theme="formhelp" name="value.uniqueCode" /></li>
	</ol>
</fieldset>
<fieldset class="form submit">
	<s:submit action="ManageOptionValue!save" cssClass="picsbutton positive" value="%{getText('button.Save')}" />
	<s:submit action="ManageOptionValue!delete" cssClass="picsbutton negative" 
		onclick="return confirm('Are you sure you want to delete this question option?');"
		value="%{getText('button.Delete')}" />
</fieldset>
</s:form>