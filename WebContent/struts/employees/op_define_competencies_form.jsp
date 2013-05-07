<%@ page language="java" errorPage="/exception_handler.jsp" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<form>
	<s:hidden name="operator"/>
	<s:hidden name="competency"/>
	<fieldset class="form">
		<h2 class="formLegend">
			<s:if test="competency.id == 0">
				<s:text name="DefineCompetencies.link.AddHSECompetency"/>
			</s:if>
			<s:else>
				<s:text name="JS.DefineCompetencies.label.EditCompetency"/>
			</s:else>
		</h2>
		<ol>
			<li>
				<label><s:text name="OperatorCompetency.category"/>:</label>
				<s:textfield maxlength="50" id="category_autocomplete" name="competency.category"/>
			</li>
			<li>
				<label><s:text name="OperatorCompetency.label"/>:</label>
				<s:textfield name="competency.label" maxlength="15" size="15"/>
			</li>
			<li>
				<label><s:text name="OperatorCompetency.description"/>:</label>
				<s:textarea name="competency.description" cols="40" rows="5"/>
			</li>
			<li>
				<label><s:text name="OperatorCompetency.courses"/>:</label>
				<select name="courseType">
					<option value="">
						- <s:text name="DefineCompetencies.SelectCourseType"/> -
					</option>
					<s:iterator value="@com.picsauditing.jpa.entities.OperatorCompetencyCourseType@values()"
					            var="course_type">
						<s:set var="course_type_selected" value="%{''}"/>
						<s:if test="#course_type == courseType">
							<s:set var="course_type_selected" value="%{' selected=\"selected\"'}"/>
						</s:if>
						<option value="${course_type.toString()}"${course_type_selected}>
							<s:text name="%{#course_type.i18nKey}"/>
						</option>
					</s:iterator>
				</select>
			</li>
		</ol>
	</fieldset>
	<fieldset class="submit">
		<s:submit method="save" cssClass="picsbutton positive" value="%{getText('button.Save')}"/>
	</fieldset>
</form>