<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="employee-group" method="delete" var="contractor_group_delete_url">
    <s:param name="id">${id}</s:param>
</s:url>
<s:url action="employee-group" method="editNameSkillsSection" var="contractor_employee_group_url">
    <s:param name="id">${id}</s:param>
</s:url>
<s:url action="employee-group" method="editEmployeesSection" var="contractor_employee_employment_url">
    <s:param name="id">${id}</s:param>
</s:url>

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
	<s:param name="title"><s:text name="CONTRACTOR.GROUP.SHOW.EMPLOYEE_GROUP" /> ${group.name}</s:param>
    <s:param name="actions">
		<button type="button" data-toggle="modal" data-target="#deleteModal" class="btn btn-danger"><s:text name="CONTRACTOR.GROUP.SHOW.DELETE_EMPLOYEE_GROUP" /></button>
    </s:param>
    <s:param name="breadcrumb_name">${group.name}</s:param>
</s:include>

<%-- Delete Confirmation --%>
<s:include value="/struts/employee-guard/_delete-confirmation.jsp">
    <s:param name="delete_url">${contractor_group_delete_url}</s:param>
    <s:param name="modal_title"><s:text name="CONTRACTOR.GROUP.SHOW.DELETE_EMPLOYEE_GROUP" /></s:param>
    <s:param name="modal_message"><s:text name="CONTRACTOR.GROUP.SHOW.DELETE_MESSAGE" /></s:param>
</s:include>

<div class="row">
    <div class="col-md-8">
		<section class="employee-guard-section edit-container" data-url="${contractor_employee_group_url}">
		    <h1>
		        <div class="row">
		            <div class="col-md-9 col-xs-9">
		                <i class="icon-group icon-large"></i> <s:text name="CONTRACTOR.GROUP.SHOW.GROUP" />
		            </div>
		            <div class="col-md-3 col-xs-3 edit">
		                <i class="icon-edit icon-large edit-toggle"></i>
		            </div>
		        </div>
		    </h1>

		    <div class="content">
				<dl class="employee-guard-information edit-display-values">
					<dt class="col-md-3"><s:text name="CONTRACTOR.GROUP.SHOW.NAME" /></dt>
					<dd class="col-md-9">${group.name}</dd>
					<dt class="col-md-3"><s:text name="CONTRACTOR.GROUP.SHOW.REQUIRED_SKILLS" /></dt>
					<dd class="col-md-9">
						<s:set var="contractor_skills" value="group.skills"/>
						<s:include value="/struts/employee-guard/contractor/skill/_list.jsp"/>
					</dd>
                </dl>
			</div>
		</section>
	</div>

	<div class="col-md-4">
		<section class="employee-guard-section edit-container" data-url="${contractor_employee_employment_url}">
		    <h1>
		        <div class="row">
		            <div class="col-md-9 col-xs-9">
		                <i class="icon-user icon-large"></i> <s:text name="CONTRACTOR.GROUP.SHOW.EMPLOYEES" />
		            </div>
		            <div class="col-md-3 col-xs-3 edit">
		                <i class="icon-edit icon-large edit-toggle"></i>
		            </div>
		        </div>
		    </h1>

		    <div class="content">
				<s:set name="contractor_employees" value="group.employees"/>
				<s:include value="/struts/employee-guard/contractor/employee/_list.jsp"/>
			</div>
		</section>
	</div>
</div>