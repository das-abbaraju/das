<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:form id="save">
<s:hidden name="optionID" value="%{option == null ? 0 : option.id}" />
<s:hidden name="typeID" value="%{type.id}" />
<s:hidden name="questionID" value="%{question == null ? 0 : question.id}" />
<fieldset class="form">
	<h2 class="formLegend">Question Option</h2>
	<ol>
		<li><label>ID:</label><span id="optionIDField"><s:property value="option.id" /></span></li>
		<li><s:textfield theme="formhelp" name="option.name" /></li>
		<li><s:checkbox theme="formhelp" name="option.visible" /></li>
		<li><s:textfield theme="formhelp" name="option.number" /></li>
		<li><s:textfield theme="formhelp" name="option.score" /></li>
		<li><s:textfield theme="formhelp" name="option.uniqueCode" /></li>
	</ol>
</fieldset>
<fieldset class="form submit">
	<s:submit action="ManageQuestionOption!save" cssClass="picsbutton positive" value="%{getText('button.Save')}" />
	<s:submit action="ManageQuestionOption!delete" cssClass="picsbutton negative" 
		onclick="return confirm('Are you sure you want to delete this question option?');"
		value="%{getText('button.Delete')}" />
</fieldset>
</s:form>