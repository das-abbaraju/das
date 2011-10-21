<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:if test="report.allRows == 0">
	<div class="alert"><s:text name="Report.message.NoRowsFound" /></div>
</s:if>
<s:else>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
		<tr>
			<td><a href="?orderBy=csr ASC">CSR Name</a></td>
			<td><a href="?orderBy=policyStatus, csr ASC"><s:text name="global.Status" /></a></td>
			<td><a href="?orderBy=cnt, csr, policyStatus ASC">Policy Count</a></td>
		</tr>
	</thead>
	<s:iterator value="data">
		<tr>
			<td><s:property value="get('csr')"/></td>
			<td><s:property value="get('policyStatus')"/></td>
			<td><s:property value="get('cnt')"/></td>
		</tr>
	</s:iterator>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</s:else>
