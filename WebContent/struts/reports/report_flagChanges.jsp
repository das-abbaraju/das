<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:property value="reportName" /></title>
<s:include value="reportHeader.jsp" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
<script type="text/javascript">
function approve(id) {
	$.post('ReportFlagChanges.action', {approveID: id});
	$("#row" + id).hide();
}

function size(obj) {
    var size = 0, key;
    for (key in obj) {
        if (obj.hasOwnProperty(key)) size++;
    }
    return size;
}

var tr = $();

var flags = {
		"Red": '<s:property value="@com.picsauditing.jpa.entities.FlagColor@getSmallIcon('Red')" escape="false"/>',
		"Green": '<s:property value="@com.picsauditing.jpa.entities.FlagColor@getSmallIcon('Green')" escape="false"/>',
		"Amber": '<s:property value="@com.picsauditing.jpa.entities.FlagColor@getSmallIcon('Amber')" escape="false"/>'
}
</script>
</head>
<body>
<h1><s:property value="reportName" /></h1>

<s:include value="filters.jsp" />

<s:if test="report.allRows == 0">
	<div class="info">No flag changes to report</div>
</s:if>
<s:else>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
		<tr>
			<th></th>
			<th>Approve</th>
			<th>New</th>
			<th>Old</th>
			<th><a href="?orderBy=a.name,operator.name">Contractor</a></th>
			<pics:permission perm="AllContractors">
				<th><a href="?orderBy=operator.name,a.name">Operator</a></th>
			</pics:permission>
			<th>Last Calc</th>
			<th>Member Since</th>
			<th>Flag Differences</th>
			<th>Useful Links</th>
		</tr>
	</thead>
	<tbody>
	<s:iterator value="data" status="stat">
		<s:set name="gcID" value="get('gcID')"></s:set>
		<tr id="row<s:property value="#gcID"/>">
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td>
				<a href="#<s:property value="#gcID"/>" onclick="approve(<s:property value="#gcID"/>); return false;"
					>Approve <s:property value="get('flag')"/></a>
			</td>
			<td><s:property value="@com.picsauditing.jpa.entities.FlagColor@getSmallIcon(get('flag').toString())" escape="false"/></td>
			<td><s:property value="@com.picsauditing.jpa.entities.FlagColor@getSmallIcon(get('baselineFlag').toString())" escape="false"/></td>
			<td><a href="ContractorView.action?id=<s:property value="get('id')"/>" 
					rel="ContractorQuickAjax.action?id=<s:property value="get('id')"/>" 
					class="contractorQuick account<s:property value="get('status')"/>" title="<s:property value="get('name')"/>"
				><s:property value="get('name')"/></a></td>
			<pics:permission perm="AllContractors">
				<td><a href="OperatorConfiguration.action?id=<s:property value="get('opId')"/>"><s:property value="get('opName')"/></a></td>
			</pics:permission>
			<td><s:property value="get('lastRecalculation')"/> mins ago</td>
			<td><s:property value="get('membershipDate')"/></td>
			<td id="detail_<s:property value="get('id')"/>">
				<script type="text/javascript">
					$(function() {
						var detail = <s:property value="get('flagDetail')" escape="false"/>;
						var baseline = <s:property value="get('baselineFlagDetail')" escape="false"/>;
						var changes = {};
						$.each(detail, function(criteriaID, data) {
							if(!baseline[criteriaID] || baseline[criteriaID].flag != data.flag) {
								changes[criteriaID] = {
										label: data.label,
										detail: data,
										baseline: baseline[criteriaID]
								}; 
							}
						});
						$.each(baseline, function(criteriaID, data){
							if(!detail[criteriaID] || detail[criteriaID].flag != data.flag) {
								if (!changes[criteriaID]) {
									changes[criteriaID] = {
											label: data.label,
											detail: detail[criteriaID],
											baseline: data
									}; 
								}
							}
						});
						if (size(changes) > 0) {
							var output = $('<table class="inner"/>');
							$.each(changes, function(criteriaID, data) {
								var tr = $('<tr><td/><td/></tr>');
								tr.find('td:eq(0)').html(data.label);
								tr.find('td:eq(1)').html((data.detail ? flags[data.detail.flag] : '?') + ' \u2192 ' + (data.baseline ? flags[data.baseline.flag] : '?'));
								output.append(tr);
							});
							$('#detail_<s:property value="get('id')"/>').html(output);
						}
					})
				</script>
			</td>
			<td>
				<a class="file" target="_BLANK" title="Opens in new window"
					href="ContractorFlag.action?id=<s:property value="get('id')"/>&opID=<s:property value="get('opId')"/>">Flag</a>
				<a class="file" target="_BLANK" title="Opens in new window"
					href="ReportActivityWatch.action?conID=<s:property value="get('id')"/>">Activity</a>
				<a class="file" target="_BLANK" title="Opens in new window"
					href="ContractorCron.action?conID=<s:property value="get('id')"/>&steps=All&button=Run">Recalc</a>
			</td>
		</tr>
	</s:iterator>
	</tbody>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</s:else>

</body>
</html>
