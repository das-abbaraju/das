<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:form id="save">
	<fieldset class="form">
		<s:hidden name="typeID" value="%{type.id}" />
		<h2 class="formLegend">Option Type</h2>
		<ol>
			<li><label>ID:</label><span id="typeIDField"><s:property value="type.id" /></span></li>
			<li><s:textfield theme="formhelp" name="type.name" id="typeName" /></li>
			<li><s:checkbox theme="formhelp" name="type.radio" id="typeRadio" /></li>
			<s:if test="type.id == 0">
				<li><s:textfield theme="formhelp" name="type.uniqueCode" id="typeUniqueCode" /></li>
			</s:if>
		</ol>
	</fieldset>
	<fieldset class="form submit">
		<s:submit value="%{getText('button.Save')}" action="ManageOptionType!save" cssClass="picsbutton positive" />
		<s:submit value="%{getText('button.Delete')}" action="ManageOptionType!delete" cssClass="picsbutton negative" onclick="return confirm('Are you sure you want to delete this option type?');" />
	</fieldset>
</s:form>