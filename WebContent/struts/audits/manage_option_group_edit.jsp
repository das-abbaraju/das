<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:form id="save">
	<fieldset class="form">
		<h2 class="formLegend">Option Group</h2>
		<ol>
			<li><label>ID:</label><s:property value="group.id" /></li>
			<li><s:textfield theme="formhelp" name="group.name" /></li>
			<li><s:checkbox theme="formhelp" name="group.radio" /></li>
			<s:if test="group.id == 0">
				<li><s:textfield theme="formhelp" name="group.uniqueCode" /></li>
			</s:if>
		</ol>
	</fieldset>
	<fieldset class="form submit">
		<s:submit value="%{getText('button.Save')}" action="ManageOptionGroup!save" cssClass="picsbutton positive" />
		<s:submit value="%{getText('button.Delete')}" action="ManageOptionGroup!delete" cssClass="picsbutton negative" onclick="return confirm('Are you sure you want to delete this option group?');" />
	</fieldset>
</s:form>