<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:if test="report.allRows == 0">
	<div class="alert">No rows found matching the given criteria.
	Please try again.</div>
</s:if>
<s:else>
	<div><s:property value="report.pageLinksWithDynamicForm"
		escape="false" /></div>
	<table class="report">
		<thead>
			<tr>
				<td><a href="?orderBy=flag DESC">Flag</a></td>
				<td>Waiting On</td>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="data" status="stat">
				<tr>
				USE CLICKABLE HERE
					<td class="right"><s:property
						value="#stat.index + report.firstRowNumber" /></td>
					<td><s:property value="get('name')" /></td>
					<pics:permission perm="AllContractors">
						<td><a
							href="ContractorEdit.action?id=<s:property value="get('id')"/>">Edit</a></td>
					</pics:permission>
				</tr>
			</s:iterator>
		</tbody>
	</table>
	<div><s:property value="report.pageLinksWithDynamicForm"
		escape="false" /></div>
</s:else>
