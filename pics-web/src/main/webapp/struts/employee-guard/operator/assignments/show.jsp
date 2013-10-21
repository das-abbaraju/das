<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tw" uri="/WEB-INF/tags/twitter-bootstrap.tld" %>


<%-- Url --%>

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">Assignments: ${project.name}</s:param>
</s:include>

<div class="row">
    <ul class="nav nav-pills nav-stacked col-md-3">
        <li class="active">
            <a href="${operator_project_url}">Required Skills</a>
        </li>
        <li>
            <a href="${operator_project_url}">Front End Developer</a>
        </li>
        <li>
            <a href="${operator_project_url}">Backend Developer</a>
        </li>
        <li>
            <a href="${operator_project_url}">Designer</a>
        </li>
        <li>
            <a href="${operator_project_url}">Interaction Designer</a>
        </li>
    </ul>

<%--     <s:set var="skill_icon">icon-ok-sign</s:set>
    <s:if test="#skill_info.skillStatus.expired" >
        <s:set var="skill_icon">icon-minus-sign-alt</s:set>
    </s:if>
    <s:elseif test="#skill_info.skillStatus.expiring" >
        <s:set var="skill_icon">icon-warning-sign</s:set>
    </s:elseif>
    <s:elseif test="#skill_info.skillStatus.pending" >
        <s:set var="skill_icon">icon-ok-circle</s:set>
    </s:elseif>
    <s:elseif test="#skill_info.skillStatus.complete">
        <s:set var="skill_icon">icon-ok-sign</s:set>
    </s:elseif>

    <a href="${employee_skill_url}" class="list-group-item ${skill_status}">
        <i class="${skill_icon}"></i>${skill_info.name}
    </a> --%>

	<div class="table-responsive col-md-9">
		<table class="table table-striped table-condensed table-hover table-status">
			<thead>
	            <tr>
					<th>Company</th>
					<th>Employee</th>
					<th class="status-title">BASF Orientation Video</th>
					<th class="status-title">Houston Texas Safety Presentation</th>
				</tr>
			</thead>

			<tbody>
					<tr>
						<td>
							<a href="#">PICS</a>
						</td>
						<td>
			                <a href="${operator_project_show_url}">Matt DeSio</a>
			            </td>
						<td class="status complete"><i class="icon-ok-sign icon-large"></i></td>
						<td class="status expired"><i class="icon-minus-sign-alt icon-large"></i></td>
					</tr>
					<tr>
						<td>
							<a href="#">PICS</a>
						</td>
						<td>
			                <a href="${operator_project_show_url}">Wally West</a>
			            </td>
						<td class="status expiring"><i class="icon-warning-sign icon-large"></i></td>
						<td class="status pending"><i class="icon-ok-circle icon-large"></i></td>
					</tr>
					<tr>
						<td>
							<a href="#">PICS</a>
						</td>
						<td>
			                <a href="${operator_project_show_url}">Jay Garrick</a>
			            </td>
						<td class="status complete"><i class="icon-ok-sign icon-large"></i></td>
						<td class="status expiring"><i class="icon-warning-sign icon-large"></i></td>
					</tr>
					<tr>
						<td>
							<a href="#">PICS</a>
						</td>
						<td>
			                <a href="${operator_project_show_url}">Barry Allen</a>
			            </td>
						<td class="status pending"><i class="icon-ok-circle icon-large"></i></td>
						<td class="status expired"><i class="icon-minus-sign-alt icon-large"></i></td>
					</tr>
			</tbody>
		</table>
	</div>
</div>