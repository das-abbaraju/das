<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>

<s:form id="new_project_form">
	<s:hidden name="employee" />
	<fieldset class="form">
		<ol>
			<s:if test="permissions.admin && employee.account.contractor">
				<li>
					<label><s:text name="global.Operator" />:</label>
					<s:select
						list="allOqOperators"
						listKey="id"
						listValue="name"
						name="operator" />
				</li>
			</s:if>
			<s:else>
				<s:hidden name="operator" value="%{employee.account.id}" />
			</s:else>
			<li class="required">
				<label for="new_project_label">
					<s:text name="JobSite.label" />:
				</label>
				<s:textfield name="jobSite.label" maxlength="15" id="new_project_label" />
			</li>
			<li class="required">
				<label for="new_project_name">
					<s:text name="JobSite.name" />:
				</label>
				<s:textfield name="jobSite.name" maxlength="255" id="new_project_name" />
			</li>
			<li>
				<label for="new_project_start">
					<s:text name="JobSite.projectStart" />:
				</label>
				<s:textfield
					name="jobSite.projectStart"
					cssClass="datepicker"
					value="%{today}"
					id="new_project_start" />
			</li>
			<li>
				<label for="new_project_stop">
					<s:text name="JobSite.projectStop" />:
				</label>
				<s:textfield
					name="jobSite.projectStop"
					cssClass="datepicker"
					value="%{expirationDate}"
					id="new_project_stop" />
			</li>
		</ol>
	</fieldset>
</s:form>