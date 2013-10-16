<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set var="number_of_columns">2</s:set>
<s:set var="span_size">5</s:set>

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title">Employee Groups / Employees Matrix</s:param>
</s:include>



<table id="contractor_roles_to_employees_matrix" class="table table-striped table-bordered table-condensed matrix">
    <tr>
        <th class="col-md-2">Employees</th>
        <th class="col-md-${span_size}">HVAC</th>
        <th class="col-md-${span_size}">Inspection/Testing</th>
    </tr>
    <tr>
        <td>Doe, John</td>
        <td></td>
        <td class="contains">
            <i class="icon-ok icon-large"></i>
        </td>
    </tr>
</table>