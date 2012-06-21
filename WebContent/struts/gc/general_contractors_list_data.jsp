<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<s:if test="report.allRows == 0">
	<div class="alert">
		<s:text name="Report.message.NoRowsFound" />
	</div>
</s:if>
<s:else>
	<div class="right">
		<a class="excel" <s:if test="report.allRows > 500">onclick="return confirm('<s:text name="JS.ConfirmDownloadAllRows"><s:param value="%{report.allRows}" /></s:text>');"</s:if> 
			href="javascript: download('GeneralContractorsList');" 
			title="<s:text name="javascript.DownloadAllRows"><s:param value="%{report.allRows}" /></s:text>"
		><s:text name="global.Download" /></a>
	</div>

	<div>
		<s:property value="report.pageLinksWithDynamicForm" escape="false" />
	</div>
	<table class="report">
		<thead>
			<tr>
				<th></th>
				<th><s:text name="FacilitiesEdit.GeneralContractor" /></th>
				<th><s:text name="GeneralContractorList.SharedSubcontractors" /></th>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="data" status="stat">
				<tr>
					<td>
						<s:property value="#stat.index + report.firstRowNumber" />
					</td>
					<td>
						<a href="SubcontractorFlagMatrix.action?filter.generalContractor=<s:property value="get('id')" />">
							<s:property value="get('name')" />
						</a>
					</td>
					<td class="right">
						<a href="SubcontractorFlagMatrix.action?filter.generalContractor=<s:property value="get('id')" />">
							<s:property value="get('subsShared')" />
						</a>
					</td>
				</tr>
			</s:iterator>
		</tbody>
	</table>
	<div>
		<s:property value="report.pageLinksWithDynamicForm" escape="false" />
	</div>
</s:else>