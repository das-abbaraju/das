<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<script type="text/javascript" src="js/jquery/jquery.fieldfocus.js"></script>


<s:if test="criteria != null">
<s:include value="../actionMessages.jsp"/>
</s:if>
<a href="#" type="button" class="goback"><< Back</a>
<form id="itemform" method="post">
	<s:hidden name="id"/>
	<fieldset class="form">
		<h2 class="formLegend">General</h2>
		<ol>
			<li>
				<label>ID:</label>
				<s:if test="criteria.id == 0">NEW</s:if>
				<s:else><s:property value="criteria.id"/></s:else>
				<s:if test="criteria.id > 0">
				<s:set name="o" value="criteria"/><s:include value="../who.jsp"/>
				</s:if>
			</li>
			<li>
				<label>Category:</label>
				<s:select list="criteriaCategory" value="criteria.category" name="criteria.category" />
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
		<h2 class="formLegend">Value</h2>
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
	<fieldset class="form">
		<h2 class="formLegend">Audit | Question</h2>
		<ol>
			<li>
				<label>Audit Type:</label>
				<s:select name="auditTypeID" list="{}" headerKey="" headerValue=" - Audit Type - " value="%{criteria.auditType.id}">
					<s:iterator value="auditTypeMap" var="aType">
						<s:optgroup label="%{#aType.key}" list="#aType.value" listKey="id" listValue="auditName"/>
					</s:iterator>
				</s:select>
			</li>
			<li>
				<label>Question:</label>
				<s:select name="questionID" list="{}" headerKey="" headerValue=" - Question - " value="%{criteria.question.id}">
					<s:iterator value="questionMap" var="flagQuestion">
						<s:optgroup label="%{#flagQuestion.key.auditName}" list="#flagQuestion.value" listKey="id" listValue="shortQuestion" />
					</s:iterator>
				</s:select>
			</li>
		</ol>
	</fieldset>
	<fieldset class="form">
		<h2 class="formLegend">OSHA</h2>
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
				<s:select name="criteria.multiYearScope" list="@com.picsauditing.jpa.entities.MultiYearScope@values()" listValue="description" headerKey="" headerValue=" - Multi Year Scope - "/>
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
			<li>
				<label>Flaggable When Missing:</label>
				<s:checkbox name="criteria.flaggableWhenMissing"/>
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
</form>