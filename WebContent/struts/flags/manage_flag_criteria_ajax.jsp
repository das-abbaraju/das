<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:if test="criteria.id != 0">
<s:include value="../actionMessages.jsp"/>
</s:if>
<a href="#" type="button" class="goback"><< Back</a>
<form id="itemform" method="post">
	<s:hidden name="id" value="%{criteria.id}"/>
	<table>
	<tr>
	<td style="vertical-align: top">
	<fieldset class="form">
		<legend><span>General</span></legend>
		<ol>
			<li>
				<label>ID:</label>
				<s:if test="criteria.id == 0">NEW</s:if>
				<s:else><s:property value="criteria.id"/></s:else>
			</li>
			<li>
				<label>Category:</label>
				<s:textfield name="criteria.category"/>
			</li>
			<li>
				<label>Display Order:</label>
				<s:textfield name="criteria.displayOrder"/>
			</li>
			<li>
				<label>Label:</label>
				<s:textfield name="criteria.label"/>
			</li>
			<li>
				<label>Description:</label>
				<s:textarea name="criteria.description" cols="30" rows="4"/>
			</li>
		</ol>
	</fieldset>
	<fieldset class="form">
		<legend><span>Value</span></legend>
		<ol>
			<li>
				<label>Data Type:</label>
				<s:select name="criteria.dataType" list="datatypeList"/>
			</li>
			<li>
				<label>Comparison:</label>
				<s:select name="criteria.comparison" list="comparisonList"/>
			</li>
			<li>
				<label>Default Hurdle:</label>
				<s:textfield name="criteria.defaultValue"/>
			</li>
			<li>
				<label>Allow Custom Hurdle:</label>
				<s:checkbox name="criteria.allowCustomValue"/> <br/> <br/>
			</li>
		</ol>
	</fieldset>
	<fieldset class="form submit">
		<input type="submit" name="button" value="Save" class="picsbutton positive"/>
		<s:if test="criteria.id > 0">
		<input type="submit" name="button" value="Delete" class="picsbutton negative"/>
		</s:if>
		<input type="button" value="Cancel" class="picsbutton goback"/>
	</fieldset>
	</td>
	<td style="width: 20px;"></td>
	<td style="vertical-align: top">
	<fieldset class="form">
		<legend><span>Audit | Question</span></legend>
		<ol>
			<li>
				<label>Audit Type:</label>
				<s:select name="auditTypeID" list="{}" headerKey="0" headerValue=" - Audit Type - " value="%{criteria.auditType.id}">
					<s:iterator value="auditTypeMap" var="aType">
						<s:optgroup label="%{#aType.key}" list="#aType.value" listKey="id" listValue="auditName"/>
					</s:iterator>
				</s:select>
			</li>
			<li>
				<label>Question:</label>
				<s:select name="questionID" list="{}" headerKey="0" headerValue=" - Question - " value="%{criteria.question.id}">
					<s:iterator value="questionMap" var="flagQuestion">
						<s:optgroup label="%{#flagQuestion.key.auditName}" list="#flagQuestion.value" listKey="id" listValue="shortQuestion" />
					</s:iterator>
				</s:select>
			</li>
		</ol>
	</fieldset>
	<fieldset class="form">
		<legend><span>OSHA</span></legend>
		<ol>
			<li>
				<label>Osha Type:</label>
				<s:select name="criteria.oshaType" list="@com.picsauditing.jpa.entities.OshaType@values()" headerKey="" headerValue=" - Osha Type - "/>
			</li>
			<li>
				<label>Osha Rate Type:</label>
				<s:select name="criteria.oshaRateType" list="@com.picsauditing.jpa.entities.OshaRateType@values()" listValue="description" headerKey="" headerValue=" - Osha Rate Type - "/>
			</li>
			<li>
				<label>Multi Year Scope:</label>
				<s:select name="criteria.multiYearScope" list="scopeList" listValue="description" headerKey="" headerValue=" - Multi Year Scope - "/>
			</li>
		</ol>
	</fieldset>
	<fieldset class="form">
		<ol>
			<li>
				<label>Validation Required:</label>
				<s:checkbox name="criteria.validationRequired"/>
			</li>
			<li>
				<label>Insurance Criteria:</label>
				<s:checkbox name="criteria.insurance"/>
			</li>
				<label>Flaggable When Missing:</label>
				<s:checkbox name="criteria.flaggableWhenMissing"/>
			</li>
		</ol>
	</fieldset>
	</td>
	</tr>
	</table>
</form>