<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<s:form id="roleForm">
	<s:hidden name="id" value="%{account.id}" />
	<s:hidden name="role" />
	<fieldset class="form">
	<h2 class="formLegend"><s:text name="%{scope}.label.DefineRole" /></h2>
	<ol>
		<li><label><s:text name="%{scope}.label.JobRole" />:</label> <s:textfield id="roleInputBox" name="role.name" size="35"/></li>
		<li><label><s:text name="%{scope}.label.Active" />:</label> <s:checkbox name="role.active" value="role.active" /> </li>
	</ol>
	</fieldset>
	<fieldset class="form submit">
		<s:submit method="save" value="%{getText('button.Save')}" cssClass="picsbutton positive" />
		<input type="button" id="cancelButton" class="picsbutton" value="<s:text name="button.Cancel" />" />
		<s:if test="role.id != 0">
			<s:submit method="delete" id="deleteButton" value="%{getText('button.Delete')}" cssClass="picsbutton negative" />
		</s:if>
	</fieldset>
</s:form>
<div id="jobCompetencyList">
	<s:if test="role.id > 0">
		<s:include value="manage_roles_competencies.jsp" />
	</s:if>
</div>