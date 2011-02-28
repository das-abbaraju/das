<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:property value="contractor.name" /> Facilities</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />

<s:include value="../jquery.jsp"/>

<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css?v=<s:property value="version"/>" />

<script type="text/javascript">
	function runSearch(button) {
		if (button === undefined)
			button = "search";
		
		startThinking( {div: 'thinkingSearchDiv', type:'large', message: 'Searching for matching facilities' } );
		$('#results').empty();
		var data= $('#facilitySearch').serialize();
		data += "&button=" + button;
		$.post('ContractorFacilityAjax.action', data, function(text, status) {
				stopThinking( {div: 'thinkingSearchDiv' } );
				$('#results').html(text);
				if (button != "search" && button!="searchShowAll") {
					reloadOperators(<s:property value="contractor.id" />);
					runSearch();
				}
				if(button == "searchShowAll"){
					$('#showAllLink').hide();
					$('#help').hide();
				}
			}
		);
		
		return false;
	}
	function showAllOperators () {
		// Assuming that the search results are still valid...
		// Do we need to check for this?
		runSearch("searchShowAll");
	}
	function addOperator( conId, opId ) {
		// Changes status if contractor refuses bid only upgrade
		var r = true;
		
		// Validating bid-only contractor and operator
		var validationData = {id: conId, button: 'validateBidOnly', 'operator.id': opId};
		$.ajax({
			url: 'ContractorFacilityAjax.action', 
			data: validationData,
			async: false,
			dataType: "json",
	        success: function(result) {
				if(result.isBidOnlyContractor && !result.isBidOnlyOperator)
					r = confirm("The Operator you have selected does not accept Bid-Only Contractors. Would you like to Upgrade this Account to a Regular Account and Add this Operator?\n\nNote: There will be a fee upgrade when changing from a Bid Only account to a Regular Account");
	        }
		});

		// if contractor declined upgrade, then exit w/o adding
		if(r == false)
			return;

		startThinking( {div: 'thinkingDiv', message: 'Linking contractor and operator' } );
		var data= {id: conId, button: 'addOperator', 'operator.id': opId, type: $('#results_' + opId + ' input[name=type]:checked').val()};
		$.ajax({
			url: 'ContractorFacilityAjax.action', 
			data: data, 
			complete: function() {
				stopThinking( {div: 'thinkingDiv' } );
				reloadOperators( conId );
				$('#facilitySearch .clearable').val('');
				runSearch();
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
				$('#facilitySearch .clearable').val('');
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

<style>
.operatorlocation {
	padding-left: 10px;
	font-size: x-small;
	color: gray;
}
#results {
	padding-top: 10px;
}
</style>

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
		<a id="next_link" href="ContractorPaymentOptions.action?id=<s:property value="id" />" class="picsbutton positive">Next &gt;&gt;</a>
		<div class="clear"></div>
	</div>
</s:if>
<br clear="all"/>

<table width="100%">
<tr>
	<td style="width: 44%; vertical-align: top;">
	<h3><s:text name="%{scope}.ContractorFacilities.SelctedFacilities" /></h3>
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
	<td style="width: 2%"></td>
	<td style="width: 44%; vertical-align: top;">
	<h3><s:text name="%{scope}.ContractorFacilities.AddFacilities" /></h3>
		<s:if test="permissions.contractor || permissions.admin">
			<div class="info"><s:text name="%{scope}.ContractorFacilities.AddFacilitiesInfo" />
			<a onClick="window.open('con_pricing.jsp','name','toolbar=0,scrollbars=0,location=0,statusbar=0,menubar=0,resizable=1,width=300,height=420'); return false;"
				href="#" title="opens in new window"><s:text name="%{scope}.ContractorFacilities.ViewPricing" /></a>
		</div></s:if>
		<form id="facilitySearch" onsubmit="runSearch(); return false;">
			<s:hidden name="id"/>
			<div id="search">
				<div class="buttons" style="min-height: 30px;">
					<button class="picsbutton positive" name="button" type="button" 
						onclick="runSearch()">Search</button>
					<nobr><s:text name="%{scope}.ContractorFacilities.Search.Name" />: 
						<s:textfield cssClass="forms clearable" name="operator.name" onchange="runSearch()"/>
					</nobr>
					<nobr><s:text name="%{scope}.ContractorFacilities.Search.Location" />: 
						<s:select cssClass="forms clearable" list="stateList" 
							onchange="runSearch()" name="state" listKey="isoCode" listValue="english" 
							headerKey="" headerValue="- State or Province -"></s:select>
					</nobr>
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
