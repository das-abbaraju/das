<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
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

function run2() {
	var ref = 'AuditPrintAjax.action?mode=ViewAll&auditTypeId=1';
	var params = $("#simulatorForm").serializeArray();
	var i;
	var form = document.createElement('form');
	form.setAttribute("method", "post");
	form.setAttribute("sction", "AuditPrintAjax.action");
	
	for (i=0;i<params.length;i++)
		{
		var name = document.forms['simulatorForm'].elements[i].name;
		var value = document.forms['simulatorForm'].elements[i].value;
		ref = ref + "&" + name + "=" + value;
		}
	window.open(ref, 'preview', 'menubar=0,scrollbars=1,resizable=1,height=700,width=640');
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
		$("#operatorSelect").append('<option value="'+ data[0] +'" selected="selected">'+ data[1] +'</option>');
		$("#operatorSelector").val("");
	});
	
	<s:if test="operators.size > 0">
		<s:iterator value="operators">
			$("#operatorSelect").append('<option value="<s:property value="id" />" selected="selected">(<s:property value="id" />) <s:property value="name" /></option>');
		</s:iterator>
		$("#operatorSelector").val("");
		run();
	</s:if>
});

</script>
<style>
#audits, #categories {
	border: 1px solid #999;
	padding: 5px;
}

#categories ul.categories {
	padding-left: 20px;
}

.ruleDetail {
	font-size: 0.8em;
	line-height: 1em;
	padding-left: 1em;
}

</style>
</head>
<body>
<h1>Contractor Simulator</h1>

<form id="simulatorForm" onsubmit="return false;" name="simForm" method="post" >
<div id="search">
<div>
<button id="searchfilter" type="button" name="button"
	class="picsbutton positive" onclick="run()">Run Simulation</button>
	<input type="hidden" name="mode" value="ViewAll" />
	<input type="hidden" name="auditTypeId" value="1" />
</div>
<div class="filterOption">
	<label>Safety Risk</label>
	<s:select
		list="@com.picsauditing.jpa.entities.LowMedHigh@values()"
		name="contractor.safetyRisk"
		value="'High'"
	/>
</div>
    <div class="filterOption">
        <label>Safety Sensitivity</label>
        <s:select
                list="#{true:'Yes', false:'No', null:'N/A'}"
                name="contractor.safetySensitive"
                value="''"
                />
    </div>
<div class="filterOption">
	<label>Product Risk</label>
	<s:select
		list="@com.picsauditing.jpa.entities.LowMedHigh@values()"
		name="contractor.productRisk"
		value="'High'"
	/>
</div>
<div class="filterOption">
	<label>Transportation Risk</label>
	<s:select
		list="@com.picsauditing.jpa.entities.LowMedHigh@values()"
		name="contractor.transportationRisk"
		value="'High'"
	/>
</div>
<div class="filterOption">
	<label>Account Level</label>
	<s:select
		list="@com.picsauditing.jpa.entities.AccountLevel@values()"
		name="contractor.accountLevel"
		value="'Full'"
	/>
</div>	
<div class="filterOption"><s:checkbox name="contractor.soleProprietor" id="soleProprietor" value="false" /><label for="soleProprietor">Sole Proprietor</label></div>
<div class="filterOption"><s:checkbox name="contractor.onsiteServices" id="onsite" value="true" /><label for="onsite">Onsite Services</label></div>
<div class="filterOption"><s:checkbox name="contractor.offsiteServices" id="offsite" value="true" /><label for="offsite">Offsite Services</label></div>
<div class="filterOption"><s:checkbox name="contractor.materialSupplier" id="supplier" value="true" /><label for="supplier">Material Supplier</label></div>
<div class="filterOption"><s:checkbox name="contractor.transportationServices" id="transportationServices" value="true" /><label for="transportation">Transportation Services</label></div>
<div>
<button id="printPQF" type="button" name="button" value='load'
	class="picsbutton" onclick="this.form.action='AuditPrintAjax.action'; this.form.target='_blank'; this.form.submit(); this.form.action=''; this.form.target='';" >Print PQF</button>
</div>

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