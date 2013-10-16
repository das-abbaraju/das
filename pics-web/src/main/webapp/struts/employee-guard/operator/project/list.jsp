<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>

<%-- Url --%>
<s:url action="project" var="operator_project_list_url" />
<s:url action="project" method="create" var="operator_project_create_url" />

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
	<s:param name="title">Projects</s:param>
	<s:param name="actions">
		<a href="${operator_project_create_url}" class="btn btn-default"><i class="icon-plus-sign"></i> Project</a>
	</s:param>
</s:include>

<tw:form formName="operator_project_search" action="${operator_project_list_url}">
    <fieldset>
        <tw:input inputName="search" type="text" class="search-query col-md-4" placeholder="Search by name" />
    </fieldset>
</tw:form>

<table class="table table-striped table-bordered table-condensed">
	<tr>
		<th>#</th>
		<th>Name</th>
		<th>Location</th>
		<th>Start Date</th>
		<th>End Date</th>
		<th>Actions</th>
	</tr>

	<s:iterator value="projects" var="project">
		<s:url action="project" method="delete" var="operator_project_delete_url">
			<s:param name="id">${project.id}</s:param>
		</s:url>
		<s:url action="project" method="edit" var="operator_project_edit_url">
			<s:param name="id">${project.id}</s:param>
		</s:url>
        <s:url action="project" var="operator_project_show_url">
            <s:param name="id">${project.id}</s:param>
        </s:url>

		<tr>
			<td>${project.id}</td>
			<td>
                <a href="${operator_project_show_url}">${project.name}</a>
            </td>
			<td>${project.location}</td>
			<td>${project.start_date}</td>
            <td>${project.end_date}</td>
			<td>
				<a href="${operator_project_edit_url}">Edit</a>
				<a href="${operator_project_delete_url}">Delete</a>
			</td>
		</tr>
	</s:iterator>
</table>