<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:property value="contractor.name" /> Billing Detail</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />

<s:include value="../jquery.jsp"/>

<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css?v=<s:property value="version"/>" />

<script type="text/javascript">
	function runSearch() {
		startThinking( {div: 'thinkingSearchDiv', type:'large', message: 'Searching for matching facilities' } );
		$('#results').empty();
		var data= $('#facilitySearch').serialize();
		data += "&button=search";
		$.post('ContractorFacilityAjax.action', data, function(text, status) {
				stopThinking( {div: 'thinkingSearchDiv' } );
				$('#results').html(text);
			}
		);
		
		return false;
	}
	function addOperator( conId, opId) {
		startThinking( {div: 'thinkingDiv', message: 'Linking contractor and operator' } );
		var data= {id: conId, button: 'addOperator', 'operator.id': opId};
		$.ajax({
			url: 'ContractorFacilityAjax.action', 
			data: data, 
			complete: function() {
				stopThinking( {div: 'thinkingDiv' } );
				$('#results_' + opId).fadeOut();
				reloadOperators( conId );
				refreshNoteCategory(conId, 'OperatorChanges');
			}
		});
		return false;
	}
	function removeOperator( conId, opId ) {
		startThinking( {div: 'thinkingDiv', message: 'Unlinking contractor and operator' } );
		var data= {id: conId, button: 'removeOperator', 'operator.id': opId};
		$.ajax({
			url: 'ContractorFacilityAjax.action', 
			data: data, 
			complete: function() {
				stopThinking( {div: 'thinkingDiv' } );
				$('#operator_' + opId).fadeOut();
				runSearch();
				reloadOperators( conId );
				refreshNoteCategory(conId, 'OperatorChanges');
			}
		});
		return false;
	}
	function reloadOperators( conId ) {
		startThinking( {div: 'thinkingDiv', message: 'Refreshing Operator List' } );
		var data= {id: conId, button: 'load'};
		$('#facilities').load('ContractorFacilityAjax.action', data, function() {
				stopThinking( {div: 'thinkingDiv' } );
			}
		);
		return false;
	}
	function setRequestedBy( conId, opId) {
		startThinking( {div: 'thinkingDiv', message: 'Saving Requested Operator Account' } );
		var data= {id: conId, button: 'request', 'operator.id': opId};
		$.ajax({
			url: 'ContractorFacilityAjax.action', 
			data: data, 
			complete: function() {
				stopThinking( {div: 'thinkingDiv' } );
				$('#next_button').show('slow');
				reloadOperators( conId );
				refreshNoteCategory(conId, 'OperatorChanges');
			}
		});
		return false;
	}

	function changeToTrialAccount(conId) {
		var r = confirm("Are you sure you need to switch to a BID-ONLY account? With a bid only account you will only be able to complete the process for the facilities/operators that you are bidding for 90 days.")
		if(r == false) {
			return false;
		}
		startThinking( {div: 'thinkingDiv', message: 'Switching to Bid Only Account' } );
		var data= {id: conId, button: 'SwitchToTrialAccount'};
		$.ajax({
			url: 'ContractorFacilityAjax.action', 
			data: data, 
			complete: function() {
				stopThinking( {div: 'thinkingDiv' } );
				$('#next_button').show();
				reloadOperators( conId );
				runSearch();
			}
		});
		return false;
	}	
</script>

</head>
<body onload="runSearch();">
<s:if test="permissions.contractor && !contractor.status.activeDemo">
	<s:include value="registrationHeader.jsp"></s:include>
</s:if>
<s:else>
<s:include value="conHeader.jsp"></s:include>
</s:else>

<s:if test="permissions.contractor && contractor.status.pendingDeactivated">
	<s:if test="msg != null && msg.length() > 0">
		<div class="error"><s:property value="msg"/></div>
	</s:if>
	<div id="next_button" class="buttons" style="float: left;">
		<a href="ContractorPaymentOptions.action?id=<s:property value="id" />" class="picsbutton positive">Next &gt;&gt;</a>
		<div class="clear"></div>
	</div>
</s:if>
<br clear="all"/>

<table width="100%">
<tr>
	<td style="width: 45%; vertical-align: top;">
	<h3>Selected Facilities</h3>
		<div id="thinkingDiv"></div>

		<div id="facilities" >
			<s:include value="contractor_facilities_assigned.jsp"/>
		</div>
		
		<pics:permission perm="EditNotes" type="Edit">
			<div id="notesList">
				<s:include value="../notes/account_notes_embed.jsp"></s:include>
			</div>
		</pics:permission>
	</td>
	<td style="width: 45%; vertical-align: top;">
	<h3>Add Facilities</h3>
		<s:if test="permissions.contractor || permissions.admin">
			<div class="info">Please specify all facilities at which you work.<br/>
			The pricing is based on the number of facilities you select below.
			<br>
			<a onClick="window.open('con_pricing.jsp','name','toolbar=0,scrollbars=0,location=0,statusbar=0,menubar=0,resizable=1,width=300,height=420'); return false;"
				href="#" title="opens in new window">Click here to view pricing</a>
		</div></s:if>
		<form id="facilitySearch" onsubmit="runSearch(); return false;">
			<s:hidden name="id"/>
			<div id="search">
				<div class="buttons" style="min-height: 30px;">
					<button class="picsbutton positive" name="button" type="button" 
						onclick="runSearch()">Search</button>
					<nobr>Name: <s:textfield cssClass="forms" name="operator.name"/></nobr>
					<nobr>Location: <s:select cssClass="forms" list="stateList" onchange="runSearch()" name="state" headerKey="" headerValue="- State -"></s:select></nobr>
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
