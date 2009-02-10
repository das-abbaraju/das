<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title><s:property value="contractor.name" /> Billing Detail</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />

<script src="js/prototype.js" type="text/javascript"></script>

<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css" />
<script src="js/notes.js" type="text/javascript"></script>

<script type="text/javascript">
	function runSearch() {
		startThinking( {div: 'thinkingDiv', type:'large' } );
		var pars= $('facilitySearch').serialize();
		var myAjax = new Ajax.Updater($('results'),'ContractorFacilitiesSearchAjax.action', 
		{
			method: 'post', 
			parameters: pars,
			onComplete: function(transport) {
				stopThinking( {div: 'thinkingDiv' } );
			}
		});
	
		return false;
	}
	function addOperator( conId, opId ) {
		startThinking( {div: 'thinkingDiv', type:'large' } );
		var pars= 'id=' + conId + '&operator.id=' + opId; 
		var myAjax = new Ajax.Updater('','ContractorFacilityAddAjax.action', 
		{
			method: 'post', 
			parameters: pars,
			onComplete: function(transport) {
				stopThinking( {div: 'thinkingDiv' } );
				reloadOperators( conId );
				$('results_' + opId).hide();
			}
		});
		return false;
	}
	function removeOperator( conId, opId ) {
		startThinking( {div: 'thinkingDiv', type:'large' } );
		var pars= 'id=' + conId + '&operator.id=' + opId; 
		var myAjax = new Ajax.Updater('','ContractorFacilityRemoveAjax.action', 
		{
			method: 'post', 
			parameters: pars,
			onComplete: function(transport) {
				stopThinking( {div: 'thinkingDiv' } );
				reloadOperators( conId );
			}
		});
		return false;
	}
	function reloadOperators( conId ) {
		startThinking( {div: 'thinkingDiv', type:'large' } );
		var pars= 'id=' + conId; 
		var myAjax = new Ajax.Updater($('facilities'),'ContractorFacilityLoadAjax.action', 
		{
			method: 'post', 
			parameters: pars,
			onComplete: function(transport) {
				stopThinking( {div: 'thinkingDiv' } );
			}
		});
		return false;
	}
</script>

</head>
<body>
<s:include value="conHeader.jsp"></s:include>

<s:if test="permissions.contractor">Please specify all facilities where you work:</s:if>
<div id="outermost">
	<s:if test="permissions.contractor || permissions.seesAllContractors()">
	<div id="left" style="width: 350px; float:left; margin: 0 10px 0 0;">
		<form id="facilitySearch" action="nothing">
			<s:hidden name="id"/>
			<div id="search">
				<div class="buttons">
					<button class="positive" name="button" type="button" 
						onclick="runSearch()">Search</button>
				</div>
				Location: <s:select list="stateList" name="state"></s:select> <br />
				Name:<s:textfield name="operator.name"/><br/>
			</div>
		</form>
		<div id="thinkingDiv"></div>
		<div id="results"></div>
	</div>
	</s:if>
	<s:if test="permissions.contractor || permissions.seesAllContractors()">
		<div id="pricing" style="float: right; width: 250px; border: 1px solid black; margin: 0 10px 0 10px;">
			<%@ include file="/includes/pricing_matrix.jsp"%><br>		
		</div>
	</s:if>
	
	<div id="facilities" style="float: left; margin: 0 0 0 10px;">
		<%@ include file="contractor_facilities_assigned.jsp"%><br>
	</div>
</div>


<div id="notesList">
<s:include value="con_notes_embed.jsp"></s:include>
</div>

<br clear="all" />
</body>
</html>
