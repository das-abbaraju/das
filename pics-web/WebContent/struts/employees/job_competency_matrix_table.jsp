<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<table class="report">
	<thead>
		<tr>
			<th colspan="2">
				<s:text name="JobCompetencyMatrix.header.HSECompetency" />
			</th>
			<s:iterator value="matrixTable.rowKeySet()" var="role">
				<th>
					${role.name}
				</th>
			</s:iterator>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="matrixTable.columnKeySet()" var="competency">
			<tr>
				<td>
					${competency.label}
				</td>
				<td>
					<img
						src="images/help.gif"
						alt="${competency.label}"
						title="${competency.category}: ${competency.description}"
					/>
				</td>
				<s:iterator value="matrixTable.rowKeySet()" id="role">
					<s:if test="matrixTable.get(#role, #competency)">
						<td class="selected">
							<img src="images/checkBoxTrue.gif" alt="X" />
						</td>
					</s:if>
					<s:else>
						<td></td>
					</s:else>
				</s:iterator>
			</tr>
		</s:iterator>
	</tbody>
</table>