<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="role" var="operator_role_list_url" />
<s:url action="role" method="create" var="operator_role_create_url" />

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
	<s:param name="title">Job Roles</s:param>
	<s:param name="actions">
		<a href="${operator_role_create_url}" class="btn btn-default"><i class="icon-plus-sign"></i>Job Roles</a>
	</s:param>
</s:include>

<tw:form formName="operator_role_search" action="${operator_role_list_url}">
    <fieldset>
        <tw:input inputName="search" type="text" class="search-query col-md-4" placeholder="Search by name" />
    </fieldset>
</tw:form>
<table class="table table-striped table-bordered table-condensed">
	<tr>
		<th>#</th>
		<th>Name</th>
		<th>Skills</th>
		<th><i class="icon-user"></i></th>
		<th>Actions</th>
	</tr>

	<s:iterator value="roles" var="role">
		<s:url action="role" method="delete" var="operator_role_delete_url">
			<s:param name="id">${role.id}</s:param>
		</s:url>
		<s:url action="role" method="edit" var="operator_role_edit_url">
			<s:param name="id">${role.id}</s:param>
		</s:url>

		<tr>
			<td>${role.id}</td>
			<td>${role.name}</td>
			<td>
                <s:iterator value="#role.skills" var="skill">
                    <span class="label">${skill.name}</span>
                </s:iterator>
            </td>
			<td><span class="badge badge-info">${role.employees.size()}</span></td>
			<td>
				<a href="${operator_role_edit_url}">Edit</a>
				<a href="${operator_role_delete_url}">Delete</a>
			</td>
		</tr>
	</s:iterator>
</table>
