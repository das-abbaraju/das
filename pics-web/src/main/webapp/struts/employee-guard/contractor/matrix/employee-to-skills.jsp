<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
	<s:param name="title">Employee / Skills Matrix</s:param>
</s:include>



<table id="contractor_employees_to_skills_matrix" class="table table-striped table-bordered table-condensed matrix">
	<tr>
		<th class="col-md-2">
			Skill
		</th>
		<th>
			Certified
		</th>
	</tr>
	<tr>
		<td>
			Forklift
		</td>
		<td class="complete">
            <i class="icon-ok-sign icon-large"></i>
		</td>
	</tr>
	<tr>
		<td>
			H2S Hazards
		</td>
		<td class="incomplete">
			<i class="icon-minus-sign-alt icon-large"></i>
		</td>
	</tr>
	<tr>
		<td>
			Rigging
		</td>
		<td class="incomplete">
			<i class="icon-minus-sign-alt icon-large"></i>
		</td>
	</tr>
</table>