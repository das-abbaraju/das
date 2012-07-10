<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<head>
    <title><s:property value="contractor.name" /> <s:text name="global.Facilities" /></title>
    
    <link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
    <link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
    <link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
    <link rel="stylesheet" type="text/css" media="screen" href="css/notes.css?v=<s:property value="version"/>" />
    
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
    
    <s:include value="../jquery.jsp"/>
    
    <script type="text/javascript">
    	$(function(){
    		runSearch();
    	});
    	
    	function runSearch(button) {
    		if (button === undefined)
    			button = "search";
    		
    		startThinking( {div: 'thinkingSearchDiv', type:'large', message:translate('JS.ContractorFacilities.message.SearchingForMatches') } );
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
    		
    		// Validating listed contractor and operator
    		var validationData = {id: conId, button: 'validateBidOnly', 'operator': opId};
    		$.ajax({
    			url: 'ContractorFacilityAjax.action', 
    			data: validationData,
    			async: false,
    			dataType: "json",
    	        success: function(result) {
    				if(result.isBidOnlyContractor && !result.isBidOnlyOperator)
    					r = confirm(translate("JS.ContractorFacilities.message.UpgradeOffer"));
    	        }
    		});
    
    		// if contractor declined upgrade, then exit w/o adding
    		if(r == false)
    			return;
    
    		startThinking( {div: 'thinkingDiv', message: translate('JS.ContractorFacilities.message.LinkingOperator') } );
    		var data= {id: conId, button: 'addOperator', 'operator': opId, type: $('#results_' + opId + ' input[name="type"]:checked').val()};
    		$.ajax({
    			url: 'ContractorFacilityAjax.action', 
    			data: data, 
    			dataType: "json",
    			success: function(result) {
    				stopThinking( {div: 'thinkingDiv' } );
    				reloadOperators( conId );
//  		  			$('#facilitySearch .clearable').val('');
    				runSearch();
    				refreshNoteCategory(conId, 'OperatorChanges');
    			}
    		});
    		
    		return false;
    	}
    	
    	function removeOperator( conId, opId ) {
    		startThinking( {div: 'thinkingDiv', message: translate('JS.ContractorFacilities.message.UnLinkingOperator') } );
    		var data= {id: conId, button: 'removeOperator', 'operator': opId};
    		$.ajax({
    			url: 'ContractorFacilityAjax.action',
    			data: data, 
    			dataType: "json",
    			success: function(result) {
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
    		startThinking( {div: 'thinkingDiv', message: translate('JS.ContractorFacilities.message.RefreshingList') } );
    		var data= {id: conId, button: 'load'};
    		$('#facilities').load('ContractorFacilityAjax.action', data, function() {
    				stopThinking( {div: 'thinkingDiv' } );
    			}
    		);
    		return false;
    	}
    	
    	function setRequestedBy( conId, opId) {
    		startThinking( {div: 'thinkingDiv', message: translate('JS.ContractorFacilities.message.SavingRequestedBy') } );
    		
    		var data= {id: conId, button: 'request', 'operator': opId};
    		
    		$.ajax({
    			url: 'ContractorFacilityAjax.action', 
    			data: data, 
    			dataType: "json",
    			success: function(result) {
    				stopThinking( {div: 'thinkingDiv' } );
    				reloadOperators( conId );
    				refreshNoteCategory(conId, 'OperatorChanges');
    			}
    		});
    	}
    
    	function changeToTrialAccount(conId) {
    		var r = confirm(translate("JS.ContractorFacilities.message.BidOnly"));
    		
    		if(r == false) {
    			return false;
    		}
    		
    		startThinking( {div: 'thinkingDiv', message: translate('JS.ContractorFacilities.message.SwitchingToTrial') } );
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
<body>
    <s:include value="conHeader.jsp" />
    
    <s:if test="permissions.contractor && contractor.status.pendingDeactivated">
    	<s:if test="msg != null && msg.length() > 0">
    		<div class="error"><s:property value="msg"/></div>
    	</s:if>
    </s:if>
    
    <br clear="all"/>
    
    <table width="100%">
        <tr>
        	<td style="width: 44%; vertical-align: top;">
                <h3><s:text name="ContractorFacilities.ContractorFacilities.SelctedFacilities" /></h3>
        		<div id="thinkingDiv"></div>
        
        		<div id="facilities" >
        			<s:include value="contractor_facilities_assigned.jsp"/>
        		</div>
                
        		<s:if test="permissions.admin">
        			<s:if test="contractor.hasAuditWithOnlyInvisibleCaos()">
        				<div class="alert">
        					This contractor has some audits with no visible caos on them.  When you disassociate a contractor with an operator some data is kept in our system, but is 
        					not visible to external users.  Audits that	fall under this case are marked as such in the audit.
        				</div>		
        			</s:if>
        		</s:if>
                
        		<pics:permission perm="EditNotes" type="Edit">
        			<div id="notesList">
        				<s:include value="../notes/account_notes_embed.jsp"></s:include>
        			</div>
        		</pics:permission>
        	</td>
        	<td style="width: 2%"></td>
        	<td style="width: 44%; vertical-align: top;">
                <h3><s:text name="ContractorFacilities.ContractorFacilities.AddFacilities" /></h3>
        		
                <form id="facilitySearch" onsubmit="runSearch(); return false;">
        			<s:hidden name="id"/>
        			<div id="search">
        				<div class="buttons" style="min-height: 30px;">
        					<button class="picsbutton positive" name="button" type="button" onclick="runSearch()"><s:text name="global.Search" /></button>
        					<nobr>
                                <s:text name="ContractorFacilities.ContractorFacilities.Search.Name" />: 
        						<s:textfield cssClass="forms clearable" name="operator.name" onchange="runSearch()"/>
        					</nobr>
                            
        					<nobr>
                                <s:text name="global.Location" />: 
        						<s:select cssClass="forms clearable" list="getStateList('US|CA')" 
        							onchange="runSearch()" name="state" listKey="isoCode" listValue="english" 
        							headerKey="" headerValue="- %{getText('ContractorFacilities.StateOrProvince')} -"></s:select>
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