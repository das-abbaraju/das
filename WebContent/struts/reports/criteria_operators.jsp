<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<div id="table">
	<table id="criteriaoperators" class="report">
		<thead>
			<tr>
				<th>Operator Name</th>
				<th>Status</th>
			</tr>
		</thead>
		<s:iterator value="criteriaOperators">
			<tr>
				<td><a href="FacilitiesEdit.action?id=<s:property value="Operator.id"/>"><s:property value="Operator.FullName"/><a/></td>
				<td><s:property value="Operator.Status"/></td>
			</tr>
		</s:iterator>
	</table>
</div>