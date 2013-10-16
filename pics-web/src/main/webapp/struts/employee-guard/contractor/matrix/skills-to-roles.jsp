<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
	<s:param name="title">Skills / Employee Groups Matrix</s:param>
</s:include>



<table id="contractor_skills_to_roles_matrix" class="table table-striped table-bordered table-condensed matrix">
	<tr>
		<th class="col-md-2">
			Skills
		</th>
		<th class="col-md-2">
			HVAC
		</th>
		<th class="col-md-2">
			Inspection/Testing
		</th>
		<th class="col-md-2">
			Safety
		</th>
		<th class="col-md-2">
			Transportation
		</th>
		<th class="col-md-2">
			Welding
		</th>
	</tr>
	<tr>
		<td class="col-md-2">
			Forklift
		</td>
		<td class="col-md-2">
		</td>
		<td class="col-md-2 contains">
            <i class="icon-ok icon-large"></i>
		</td>
		<td class="col-md-2 contains">
			<i class="icon-ok icon-large"></i>
		</td>
		<td class="col-md-2">
		</td>
		<td class="col-md-2">
		</td>
	</tr>
	<tr>
		<td class="col-md-2">
			Gas Freeing
		</td>
		<td class="col-md-2 contains">
			<i class="icon-ok icon-large"></i>
		</td>
		<td class="col-md-2">
		</td>
		<td class="col-md-2">
		</td>
		<td class="col-md-2">
		</td>
		<td class="col-md-2">
		</td>
	</tr>
	<tr>
		<td class="col-md-2">
			H2S Hazards
		</td>
		<td class="col-md-2">
		</td>
		<td class="col-md-2">
		</td>
		<td class="col-md-2 contains">
			<i class="icon-ok icon-large"></i>
		</td>
        <td class="col-md-2 contains">
            <i class="icon-ok icon-large"></i>
        </td>
		<td class="col-md-2">
		</td>
	</tr>
	<tr>
		<td class="col-md-2">
			Rigging
		</td>
		<td class="col-md-2">
		</td>
		<td class="col-md-2">
		</td>
		<td class="col-md-2 contains">
			<i class="icon-ok icon-large"></i>
		</td>
		<td class="col-md-2">
		</td>
		<td class="col-md-2">
		</td>
	</tr>
	<tr>
		<td class="col-md-2">
			Startup Equipment
		</td>
		<td class="col-md-2 contains">
			<i class="icon-ok icon-large"></i>
		</td>
		<td class="col-md-2">
		</td>
		<td class="col-md-2">
		</td>
		<td class="col-md-2">
		</td>
		<td class="col-md-2 contains">
			<i class="icon-ok icon-large"></i>
		</td>
	</tr>
</table>