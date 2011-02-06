<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Contractor Simulator</title>
<link rel="stylesheet" type="text/css" media="screen"
	href="css/reports.css?v=<s:property value="version"/>" />
<script type="text/javascript">

function run() {
	startThinking({div:'audits', message: "Loading Included Audits", type: "small"});
	var params = $("#simulatorForm").serializeArray();
	$("#audits").load("ContractorSimulatorAjax.action",params);
	$("#categories").html("Included Categories Display Here");
}

function fillCategories(auditTypeID) {
	startThinking({div:'categories', message: "Loading Included Categories", type: "small"});
	var params = $("#simulatorForm").serializeArray();
	$("#categories").load("ContractorSimulatorAjax.action?auditType.id=" + auditTypeID,params);
}

$(function() {
	$('#operatorSelector').autocomplete('OperatorAutocomplete.action', {
		max: 50,
		formatItem : function(data,i,count){
			return data[1];
		},
		formatResult: function(data,i,count){
			return data[1];
		}
	}).result(function(event, data){
		$("#operatorSelect").append('<option value="'+ data[0] +'">'+ data[1] +'</option>');
		$("#operatorSelector").val("");
	});
});

</script>
<style>
#audits, #categories {
	border: 1px solid #999;
	padding: 5px;
}
</style>
</head>
<body>
<h1>Contractor Simulator</h1>

<form id="simulatorForm" onsubmit="return false;">
<div id="search">
<div>
<button id="searchfilter" type="button" name="button"
	class="picsbutton positive" onclick="run()">Run Simulation</button>
</div>
<div class="filterOption"><s:select
	list="#{'Low':'Low Risk','Medium':'Medium Risk','High':'High Risk'}"
	name="contractor.riskLevel" value="'High'" /></div>

<div class="filterOption"><s:checkbox name="contractor.onsiteServices" id="onsite" value="true" /><label for="onsite">Onsite Services</label></div>
<div class="filterOption"><s:checkbox name="contractor.offsiteServices" id="offsite" value="true" /><label for="offsite">Offsite Services</label></div>
<div class="filterOption"><s:checkbox name="contractor.materialSupplier" id="supplier" value="true" /><label for="supplier">Material Supplier</label></div>

<div class="filterOption">Operator(s): <s:textfield id="operatorSelector" size="40" /><br />
<s:select list="#{}" name="operatorIds" multiple="true" size="3" id="operatorSelect"  />
</div>

<br clear="all">
</div>
</form>

<table width="100%" style="padding: 10px">
	<tr>
		<td width="40%"><h4>Audit List</h4></td>
		<td width="20px">&nbsp;</td>
		<td><h4>Categories</h4></td>
	</tr>
	<tr>
		<td id="audits">Included Audits Display Here<br /><br /><br /></td>
		<td>&nbsp;</td>
		<td id="categories"></td>
	</tr>
</table>


</body>
</html>