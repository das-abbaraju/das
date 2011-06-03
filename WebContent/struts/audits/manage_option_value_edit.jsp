<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:form id="save">
	<s:hidden name="value" />
	<s:hidden name="group" />
	<s:hidden name="question" />
	<fieldset class="form">
		<h2 class="formLegend">Option Value</h2>
		<ol>
			<li><label>ID:</label><s:property value="value.id > 0 ? value.id : 'NEW'" /></li>
			<li><s:textfield theme="formhelp" name="value.name" /></li>
			<li><s:checkbox theme="formhelp" name="value.visible" /></li>
			<li><s:textfield theme="formhelp" name="value.number" value="%{value.id > 0 ? value.number : nextNumber}" /></li>
			<li><s:textfield theme="formhelp" name="value.score" /></li>
			<s:if test="value.id > 0">
				<li><label><s:text name="AuditOptionValue.uniqueCode" />:</label>
					<s:if test="!isStringEmpty(value.uniqueCode)">
						<s:property value="value.uniqueCode" />
					</s:if>
					<s:else>
						<i>(None)</i>
					</s:else>
				</li>
			</s:if>
			<s:else>
				<li><s:textfield theme="formhelp" name="value.uniqueCode" /></li>
			</s:else>
		</ol>
	</fieldset>
	<fieldset class="form submit">
		<s:submit action="ManageOptionValue!save" cssClass="picsbutton positive" value="%{getText('button.Save')}" />
		<s:if test="value.id > 0">
			<pics:permission perm="ManageAudits" type="Delete">
				<s:submit action="ManageOptionValue!delete" cssClass="picsbutton negative" value="%{getText('button.Delete')}"
					onclick="return confirm('Are you sure you want to delete this question option?');" />
			</pics:permission>
		</s:if>
	</fieldset>
</s:form>