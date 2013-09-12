<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:include value="../actionMessages.jsp" />

<s:if test="trade.alternates.size() > 0">
	<div id="table">
		<table class="report">
			<thead>
				<tr>
					<th>Alternate</th>
					<th>Category</th>
					<th>Delete</th>
				</tr>
			</thead>
			<tbody>
				<s:iterator value="trade.alternates">
					<tr>
						<td><s:property value="name" /></td>
						<td><s:property value="category" /></td>
						<td><a class="remove" id="delete-alternate" href="TradeTaxonomy!removeAlternateAjax.action?alternate=<s:property value='id' />" /></td>
					</tr>
				</s:iterator>
			</tbody>
		</table>
	</div>
</s:if>
<s:else>
	<div class="info">No alternate names found.</div>
</s:else>