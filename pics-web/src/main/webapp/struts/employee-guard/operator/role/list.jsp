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
		<a href="${operator_role_create_url}" class="btn btn-default"><i class="icon-plus-sign"></i> Job Role</a>
	</s:param>
</s:include>

<%-- Pagination --%>
<s:include value="/struts/employee-guard/_pagination.jsp" />

<tw:form formName="operator_role_search" action="${operator_role_list_url}" class="search-query" role="form">
    <fieldset>
    	<div class="search-wrapper col-md-4">
        	<tw:input inputName="searchTerm" type="text" class="form-control" placeholder="Search by name" value="${searchForm.searchTerm}" />
        	<i class="icon-search"></i>
        	<ul id="operator_role_search" class="search-results"></ul>
        </div>
    </fieldset>
</tw:form>
<div class="table-responsive">
	<table class="table table-striped table-condensed table-hover">
		<thead>
			<tr>
				<th>Job Role</th>
				<th>Required Skills</th>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="roles" var="role">
				<s:url action="role" var="operator_role_show_url">
                	<s:param name="id">${role.id}</s:param>
            	</s:url>
				<tr>
					<td><a href="${operator_role_show_url}">${role.name}</a></td>
					<td>
						<s:set name="operator_skills" value="#role.skills"/>
                    	<s:include value="/struts/employee-guard/operator/skill/_list.jsp"/>
		            </td>
				</tr>
			</s:iterator>
		</tbody>
	</table>
</div>