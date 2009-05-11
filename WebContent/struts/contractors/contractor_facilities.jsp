<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
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
		startThinking( {div: 'thinkingSearchDiv', type:'large', message: 'Searching for matching facilities' } );
		$('results').innerHtml = "";
		var pars= $('facilitySearch').serialize();
		pars += '&button=search';
		var myAjax = new Ajax.Updater($('results'),'ContractorFacilityAjax.action', 
		{
			method: 'post', 
			parameters: pars,
			onComplete: function(transport) {
				stopThinking( {div: 'thinkingSearchDiv' } );
			}
		});
		
		return false;
	}
	function addOperator( conId, opId ) {
		startThinking( {div: 'thinkingDiv', message: 'Linking contractor and operator' } );
		var pars= 'id=' + conId + '&button=addOperator&operator.id=' + opId; 
		var myAjax = new Ajax.Updater('','ContractorFacilityAjax.action', 
		{
			method: 'post', 
			parameters: pars,
			onComplete: function(transport) {
				stopThinking( {div: 'thinkingDiv' } );
				$('results_' + opId).hide();
				reloadOperators( conId );
				refreshNoteCategory(conId, 'OperatorChanges');
			}
		});
		return false;
	}
	function removeOperator( conId, opId ) {
		startThinking( {div: 'thinkingDiv', message: 'Unlinking contractor and operator' } );
		var pars= 'id=' + conId + '&button=removeOperator&operator.id=' + opId; 
		var myAjax = new Ajax.Updater('','ContractorFacilityAjax.action', 
		{
			method: 'post', 
			parameters: pars,
			onComplete: function(transport) {
				stopThinking( {div: 'thinkingDiv' } );
				reloadOperators( conId );
				refreshNoteCategory(conId, 'OperatorChanges');
			}
		});
		return false;
	}
	function reloadOperators( conId ) {
		startThinking( {div: 'thinkingDiv', message: 'Refreshing Operator List' } );
		var pars= 'id=' + conId + '&button=load';
		var myAjax = new Ajax.Updater($('facilities'),'ContractorFacilityAjax.action', 
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
<body onload="runSearch();">
<s:if test="permissions.contractor && !contractor.activeB">
	<s:include value="registrationHeader.jsp"></s:include>
</s:if>
<s:else>
<s:include value="conHeader.jsp"></s:include>
</s:else>

<s:if test="permissions.contractor && !contractor.activeB">
	<div class="buttons" style="float: left;">
		<a href="ContractorPaymentOptions.action?id=<s:property value="id" />" class="positive">Next</a>
	</div>
</s:if>

<table width="100%">
<tr>
	<td style="width: 45%; vertical-align: top;">
		<div id="thinkingDiv"></div>
		
		<div id="facilities" >
			<%@ include file="contractor_facilities_assigned.jsp"%>
		</div>
		
		<pics:permission perm="EditNotes" type="Edit">
			<div id="notesList">
				<s:include value="../notes/account_notes_embed.jsp"></s:include>
			</div>
		</pics:permission>
	</td>
	<td style="width: 45%; vertical-align: top;">
		<s:if test="permissions.contractor || permissions.admin">
			<div id="info">Please specify all facilities at which you work.<br/>
			The pricing is based on the number of facilities you select below. 
			<a href="#" onClick="window.open('con_pricing.jsp','name','toolbar=0,scrollbars=0,location=0,statusbar=0,menubar=0,resizable=1,width=300,height=420'); return false;">
			Click here to view pricing (opens in new window)</a>	
		</div></s:if>
		<form id="facilitySearch" onsubmit="runSearch(); return false;">
			<s:hidden name="id"/>
			<div id="search">
				<div class="buttons" style="min-height: 30px;">
					<button class="positive" name="button" type="button" 
						onclick="runSearch()">Search</button>
					<nobr>Name: <s:textfield cssClass="forms" name="operator.name" onkeypress="return false();"/></nobr>
					<nobr>Location: <s:select cssClass="forms" list="stateList" onchange="runSearch()" name="state"></s:select></nobr>
				</div>
			</div>
		</form>
		<div id="thinkingSearchDiv"></div>
		<div id="results"></div>
	</td>
</tr>
</table>

<br clear="all" />
</body>
</html>
