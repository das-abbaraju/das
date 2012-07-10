<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Report Contractor Risk</title>
<s:include value="reportHeader.jsp" />
<style type="text/css">
.red {
	color: red;
}
</style>
</head>
<body>
	<h1>Contractor Risk Assessment</h1>
	
	<a href="#" class="cluetip help" rel="#approve_cluetip" title="Approve Risk Level">Approving Product risk levels</a>
	<div id="approve_cluetip">If approved, the highest of the Contractor selected product risk Levels will be 
		set as their new product risk level.</div>
	
	<div id="search">
		<s:form id="form1">
			<s:hidden name="filter.ajax" value="false" />
			<s:hidden name="filter.destinationAction" />
			<s:hidden name="filter.allowMailMerge" />
			<s:hidden name="showPage" value="1" />
			<s:hidden name="filter.startsWith" />
			<s:hidden name="orderBy" />
			
			<div>
				<button id="searchfilter" type="submit" name="button" value="Search" onclick="return clickSearch('form1');" class="picsbutton positive">
					<s:text name="button.Search" />
				</button>
			</div>
			
			<div class="filterOption">
				<a href="#" class="filterBox"><s:text name="global.Operators" /></a> =
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
			
			<div class="filterOption">
				<a href="#" class="filterBox">Risk Type</a>
				<s:select list="riskList" cssClass="forms" name="filter.riskType" />
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
				<td>Last Assessment Date</td>
				<td>Notes</td>
				<td></td>
				<td></td>
			</tr>
		</thead>
		<s:iterator value="data" status="stat">
			<tr>
				<td class="right"><s:property value="#stat.count" /></td>
				<td>
					<a href="ContractorView.action?id=<s:property value="get('id')"/>"
						rel="ContractorQuick.action?id=<s:property value="get('id')"/>"
						class="contractorQuick"
						title="<s:property value="get('name')" />"><s:property value="get('name')" /></a>
				</td>
				<td>
					<s:date name="get('creationDate')" />
				</td>
				<td>
					<s:property value="get('riskType')" />
				</td>
				<td>
					<s:property value="@com.picsauditing.jpa.entities.LowMedHigh@getName(get('risk'))" />
				</td>
				<s:form action="ReportContractorRiskLevel" method="POST">
					<td>
						<s:if test="get('riskType').toString() != 'Transportation'">
							<s:property value="get('answer')" escape="false" />
						</s:if>
						<s:else>
							<s:select 
								list="@com.picsauditing.jpa.entities.LowMedHigh@values()"
								listKey="name()"
								listValue="name()"
								name="manuallySetRisk"
							/>
						</s:else>
					</td>
					<td>
						<s:date name="get('lastVerifiedDate')" />
					</td>
					<td>
						<s:textarea name="auditorNotes" cols="15" rows="4" />
					</td>
					<td>
						<s:if test="get('riskType').toString() != 'Transportation'">
							<s:submit method="reject" cssClass="picsbutton positive" value="%{getText('button.Reject')}" />
						</s:if>
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
</html>
