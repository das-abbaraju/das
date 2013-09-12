<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:form id="save">
	<s:hidden name="question" />
	<s:hidden name="group" />
	<fieldset class="form">
		<h2 class="formLegend">Option Group</h2>
		<ol>
			<li><label>ID:</label><s:property value="group.id > 0 ? group.id : 'NEW'" /></li>
			<li><s:textfield theme="formhelp" name="group.name" /></li>
			<li><s:checkbox theme="formhelp" name="group.radio" /></li>
			<s:if test="group.id == 0">
				<li><s:textfield theme="formhelp" name="group.uniqueCode" /></li>
			</s:if>
			<s:else>
				<li><label><s:text name="AuditOptionGroup.uniqueCode" />:</label>
					<s:if test="isStringEmpty(group.uniqueCode)">
						<i>(None)</i>
					</s:if>
					<s:else>
						<s:property value="group.uniqueCode" />
					</s:else>
				</li>
			</s:else>
			<li>
				<label>
					<s:text name="ManageAuditType.RequiredLanguages" />:
				</label>
				<s:optiontransferselect
					name="requiredLanguagesName"
					list="availableLocales"
					listKey="language"
					listValue="%{displayLanguage + (displayCountry == '' ? '' : ' (' + displayCountry + ')')}"
					doubleName="group.languages"
					doubleList="selectedLocales"
					doubleListKey="language"
					doubleListValue="%{displayLanguage + (displayCountry == '' ? '' : ' (' + displayCountry + ')')}"
					leftTitle="%{getText('ManageAuditType.AvailableLanguages')}"
					rightTitle="%{getText('ManageAuditType.SelectedLanguages')}"
					addToLeftLabel="%{getText('button.Remove')}"
					addToRightLabel="%{getText('button.Add')}"
					allowAddAllToLeft="false"
					allowAddAllToRight="false"
					allowSelectAll="false"
					allowUpDownOnLeft="false"
					allowUpDownOnRight="false"
					buttonCssClass="arrow"
					theme="pics"
				>
					<s:param name="sort" value="'false'" />
				</s:optiontransferselect>
			</li>
		</ol>
	</fieldset>
	<fieldset class="form submit">
		<s:submit value="%{getText('button.Save')}" action="ManageOptionGroup!save" cssClass="picsbutton positive" />
		<s:if test="group.id > 0">
			<pics:permission perm="ManageAudits" type="Delete">
				<s:submit value="%{getText('button.Delete')}" action="ManageOptionGroup!delete" cssClass="picsbutton negative"
					onclick="return confirm('Are you sure you want to delete this option group?');" />
			</pics:permission>
		</s:if>
	</fieldset>
</s:form>