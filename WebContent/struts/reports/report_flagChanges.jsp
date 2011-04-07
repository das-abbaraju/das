<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:property value="reportName" /></title>
<s:include value="reportHeader.jsp" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
<script type="text/javascript">
$(function(){
	$('#checkAllAuto').click(
			function()
			{
				$('.approveBox').attr('checked', $('#checkAllAuto').is(':checked'));	
			}
		)
})

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

function includeDetail(x, y) {
	if (x.category == 'Insurance Criteria')
		return false;
	if (!y) {
		return x.flag != 'Green';
	} else {
		return x.flag != y.flag;
	}
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
<s:form>
<div>
	<input type="submit" class="picsbutton positive right" style="margin:7px;" name="button" value="Approve Selected">
	<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
		<tr>
			<th>Flag</th>
			<th>Approve<br/><input type="checkbox" name="checkAllAuto" id="checkAllAuto" />(Select All)</th>
			<th>Flag Differences</th>
			<!-- <th></th> -->
			<th><a href="?orderBy=a.name,operator.name">Contractor</a></th>
			<pics:permission perm="AllContractors">
				<th><a href="?orderBy=operator.name,a.name">Operator</a></th>
			</pics:permission>
			<!-- <th>Last Calc</th> -->
			<!-- <th>Member Since</th> -->
			<th>Useful Links</th>
		</tr>
	</thead>
	<tbody>
	<s:iterator value="data" status="stat">
		<s:set name="gcID" value="get('gcID')"></s:set>
		<tr id="row<s:property value="#gcID"/>">
			<td class="nobr"><a target="_BLANK" title="<s:property value="get(\'name\')" escape="true" /> (Last Calculated: <s:date name="get('lastRecalculation')" format="MM/dd/yyyy" />)"
					rel="ContractorFlagAjax.action?id=<s:property value="get('id')"/>&opID=<s:property value="get('opId')"/>" class="contractorQuick"
					href="ContractorFlag.action?id=<s:property value="get('id')"/>&opID=<s:property value="get('opId')"/>">
						<img src="images/icon_<s:property value="get('baselineFlag').toString().toLowerCase()" />Flag.gif" width="10" height="12" border="0" />
					
				→
				<img src="images/icon_<s:property value="get('flag').toString().toLowerCase()" />Flag.gif" width="10" height="12" border="0" /></a></td>
			<td>
				<span class="nobr">
					<s:checkbox cssClass="approveBox" name="approvedChanges" fieldValue="%{get('gcID')}" />
					<a href="#<s:property value="#gcID"/>" onclick="approve(<s:property value="#gcID"/>); return false;">Approve <s:property value="get('flag')"/></a>
				</span>
			</td>
			<td id="detail_<s:property value="get('gcID')"/>">
				
				<script type="text/javascript">
					$(function() {
						var detail = <s:property value="get('flagDetail')" escape="false"/>;
						var baseline = <s:property value="get('baselineFlagDetail')" escape="false"/>;
						var changes = {};
						$.each(baseline, function(criteriaID, data){
							if (includeDetail(data, detail[criteriaID])) {
								changes[criteriaID] = {
										label: data.label,
										detail: detail[criteriaID],
										baseline: data
								}; 
							}
						});
						$.each(detail, function(criteriaID, data) {
							if (includeDetail(data, baseline[criteriaID])) {
								changes[criteriaID] = {
										label: data.label,
										detail: data,
										baseline: baseline[criteriaID]
								};
							}
						});
						
						if (size(changes) > 0) {
							var output = $('<table class="inner" title="<s:property value="get(\'name\')" escape="true" /> Documents" rel="ContractorQuickDocumentsAjax.action?id=<s:property value="get(\'id\')"/>&opID=<s:property value="get(\'opId\')"/>" />');
							$.each(changes, function(criteriaID, data) {
								var tr = $('<tr><td/><td/></tr>');
								tr.find('td:eq(0)').html(
									(data.baseline ? flags[data.baseline.flag] : '?') +
									' \u2192 ' + 
									(data.detail ? flags[data.detail.flag] : '?') 
								);
								tr.find('td:eq(1)').html(data.label);
								output.append(tr);
							});
							$('#detail_<s:property value="get('gcID')"/>').html(output);
							output.cluetip({sticky : true,
								hoverClass : 'cluetip',
								mouseOutClose : true,
								clickThrough : true,
								ajaxCache : true,
								closeText : "<img src='images/cross.png' width='16' height='16'>",
								hoverIntent : {
									interval : 300
								},
								arrows : true,
								dropShadow : false,
								width : 500,
								cluetipClass : 'jtip',
								ajaxProcess : function(data) {
									data = $(data).not('meta, link, title');
									return data;
								}});
						} else {
							var output = $('<table class="inner" title="<s:property value="get(\'name\')" escape="true" /> Documents" rel="ContractorQuickDocumentsAjax.action?id=<s:property value="get(\'id\')"/>&opID=<s:property value="get(\'opId\')"/>" ><tr><td>?</tr></td></table>');
							$('#detail_<s:property value="get('gcID')"/>').html(output);
							output.cluetip({sticky : true,
								hoverClass : 'cluetip',
								mouseOutClose : true,
								clickThrough : true,
								ajaxCache : true,
								closeText : "<img src='images/cross.png' width='16' height='16'>",
								hoverIntent : {
									interval : 300
								},
								arrows : true,
								dropShadow : false,
								width : 500,
								cluetipClass : 'jtip',
								ajaxProcess : function(data) {
									data = $(data).not('meta, link, title');
									return data;
								}});
						}
					})
				</script>
			</td>
			<!-- <td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td> -->
			<td><a href="ContractorView.action?id=<s:property value="get('id')"/>" 
					rel="ContractorQuickAjax.action?id=<s:property value="get('id')"/>" 
					class="contractorQuick account<s:property value="get('status')"/>" title="<s:property value="get('name')"/>"
				><s:property value="get('name')"/></a></td>
			<pics:permission perm="AllContractors">
				<td><a href="OperatorConfiguration.action?id=<s:property value="get('opId')"/>"><s:property value="get('opName')"/></a></td>
			</pics:permission>
			<!-- <td><s:date name="get('lastRecalculation')" format="MM/dd/yyyy" /></td> -->
			<!-- <td><s:date name="get('membershipDate')" nice="true" /></td> -->
			<td>
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
	<input type="submit" class="picsbutton positive right" style="margin:7px;" name="button" value="Approve Selected">
	<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</s:form>
</s:else>

</body>
</html>
