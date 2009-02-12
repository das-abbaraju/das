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
<s:if test="permissions.contractor && !contractor.activeB">
	<s:include value="registrationHeader.jsp"></s:include>
</s:if>
<s:else>
<s:include value="conHeader.jsp"></s:include>
</s:else>

<s:if test="permissions.contractor"><div id="info">Please specify all facilities at which you work.</div></s:if>
<div id="outermost">
	<div id="left" style="width: 350px; float:left; margin: 0 10px 0 0;">
		<form id="facilitySearch" action="nothing">
			<s:hidden name="id"/>
			<div id="search">
				<div class="buttons">
					<button class="positive" name="button" type="button" 
						onclick="runSearch()">Search</button>
				</div>
				Location: <s:select cssClass="pics" list="stateList" name="state"></s:select> <br />
				Name:<s:textfield name="operator.name"/><br/>
			</div>
		</form>
		<div id="thinkingDiv"></div>
		<div id="results"></div>
	</div>
	<div id="facilities" style="float: left; margin: 0 0 0 10px;">
		<%@ include file="contractor_facilities_assigned.jsp"%><br>
	</div>
</div>

<s:if test="notes.size() > 0">
	<div id="notesList">
		<s:include value="con_notes_embed.jsp"></s:include>
	</div>
</s:if>
<s:else>
	<pics:permission perm="EditNotes" type="Edit">
		<div id="notesList">
			<s:include value="con_notes_embed.jsp"></s:include>
		</div>
	</pics:permission>
</s:else>

<br clear="all" />
<s:if test="permissions.contractor && !contractor.activeB">
	<div class="buttons" style="float: right;">
		<a href="ContractorPaymentOptions.action?id=<s:property value="id" />" class="positive">Next</a>
	</div>
</s:if>
</body>
</html>
