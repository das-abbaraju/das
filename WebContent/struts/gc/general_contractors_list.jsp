<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<head>
	<title><s:text name="GeneralContractorList.title" /></title>
	<s:include value="../reports/reportHeader.jsp" />
</head>
<body>
	<h1><s:text name="GeneralContractorList.title" /></h1>
	
	<div id="report_data">
		<table class="report">
			<thead>
				<tr>
					<th></th>
					<th><s:text name="FacilitiesEdit.GeneralContractor" /></th>
				</tr>
			</thead>
			<tbody>
				<s:iterator value="data" status="stat">
					<tr>
						<td>
							<s:property value="#stat.count" />
						</td>
						<td>
							<a href="SubcontractorFlagMatrix.action?filter.generalContractor=<s:property value="get('id')" />">
								<s:property value="get('name')" />
							</a>
						</td>
					</tr>
				</s:iterator>
			</tbody>
		</table>
	</div>
</body>
