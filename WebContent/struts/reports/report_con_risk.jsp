<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<head>
	<title>Report Contractor Risk</title>
		<s:include value="reportHeader.jsp" />
		<style type="text/css">
			.red
			{
				color: red;
			}
		</style>
	</head>
<body>
	<h1>Contractor Risk Assessment</h1>
	
	<div id="search">
		<s:form id="form1">
			<s:hidden name="filter.ajax" value="false" />
			<s:hidden name="filter.destinationAction" />
			<s:hidden name="filter.allowMailMerge" />
			<s:hidden name="showPage" value="1" />
			<s:hidden name="filter.startsWith" />
			<s:hidden name="orderBy" />
			
			<div>
				<button
					id="searchfilter"
					type="submit"
					name="button"
					value="Search"
					onclick="return clickSearch('form1');"
					class="picsbutton positive">
					<s:text name="button.Search" />
				</button>
			</div>
			
			<div class="filterOption">
				<a href="#" class="filterBox">
					<s:text name="global.Operators" />
				</a> =
				<span class="q_status"><s:text name="JS.Filters.status.All" /></span>
				<br />
				
				<span class="clearLink q_box select">
					<s:textfield rel="Operator" name="filter.operator" cssClass="tokenAuto" />
					<a class="clearLink" href="#"><s:text name="Filters.status.Clear" /></a>
					<s:radio 
						list="#{'false':getTextNullSafe('JS.Filters.status.All'),'true':getTextNullSafe('Filters.status.Any')}" 
						name="filter.showAnyOperator"
						theme="pics"
						cssClass="inline"
					/>
				</span>
			</div>
		</s:form>
		<div class="clear"></div>
	</div>
	
	<div>
		<s:property value="report.pageLinksWithDynamicForm" escape="false" />
	</div>
	
	<table class="report" style="clear: none;">
		<thead>
			<tr>
				<td></td>
				<td><a href="javascript: changeOrderBy('form1','a.name');"><s:text name="global.ContractorName" /></a></td>
				<td><a href="javascript: changeOrderBy('form1','a.creationDate');">Registration Date</a></td>
				<td>Type</td>
				<td>Calculated Risk</td>
				<td>Contractor Risk</td>
				<td>Notes</td>
				<td></td>
				<td></td>
			</tr>
		</thead>
		<s:iterator value="data" status="stat" var="contractor">
			<tr>
				<td class="right"><s:property value="#stat.count" /></td>
				<td>
					<s:url action="ContractorView" var="contractor_view">
						<s:param name="id">
							${contractor.get('id')}
						</s:param>
					</s:url>
					<s:url action="ContractorQuick" var="contractor_quick">
						<s:param name="id">
							${contractor.get('id')}
						</s:param>
					</s:url>
					
					<a href="${contractor_view}"
						rel="${contractor_quick}"
						class="contractorQuick"
						title="${contractor.get('name')}">
						${contractor.get('name')}
					</a>
				</td>
				<td>
					<s:date name="get('creationDate')" />
				</td>
				<td>
					${contractor.get('riskType')}
				</td>
				<td>
					<s:property value="@com.picsauditing.jpa.entities.LowMedHigh@getName(get('risk'))" />
				</td>
				<s:form action="ReportContractorRiskLevel" method="POST">
					<td>
						<s:property value="get('answer')" escape="false" />
					</td>
					<td>
						<s:textarea name="auditorNotes" cols="15" rows="4" />
					</td>
					<td>
						<s:submit method="reject" cssClass="picsbutton positive" value="%{getText('button.Reject')}" />
					</td>
					<td>
						<s:hidden value="%{get('id')}" name="conID" />
						<s:hidden value="%{get('riskType')}" name="type" />
						<s:submit method="accept" cssClass="picsbutton negative" value="%{getText('button.Accept')}" />
					</td>
				</s:form>
			</tr>
		</s:iterator>
	</table>
</body>