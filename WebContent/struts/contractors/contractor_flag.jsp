<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Flag Status for <s:property value="contractor.name" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />

<style type="text/css">
table.report a {
	text-decoration: underline;
}

.hide {
	display: none;
}

.hover {
	margin-left: 10px;
	display: none;
}
small {
	font-size: x-small;
}
.flagCategories td {
	padding-right: 10px;
	vertical-align: top;
}

.ffTo {
	padding: 2px 0 4px 4px;
	display:block;
	float:left;
}

.ffDate {
	padding: 2px 0 4px 4px;
	display:block;
	float:left;
	margin-left:20px;
}

.ffReason {
	padding: 2px 15px 4px 4px;
	margin-left:20px;
}

ffCorporate {
clear:left;
}
.ffLeft {
	float: left;
}
.ffRight {
	float: right;
}

fieldset.form {
	margin: 0 0 0 0;
	padding: 0 0 10px 0;
	border-style: none;
	border-top: none;
	background-color: transparent;
	width: 100%;
	float: none;
	clear: both;
	position: relative;
	color: #404245;
}


.label-txt {
	float: none;
	width: 100%;
	display: block;
	margin: 0 0 5px 0;
	text-align: left;
	font-size: 15px;
	font-weight: bold;
	color: #003768;
	line-height: 15px;
	white-space: nowrap;
}

fieldset.form ol li.fieldhelp-focused .fieldhelp {position:absolute;z-index:5000;}



#info-box {float:right;display:block;width:40%;}


</style>
<s:include value="../jquery.jsp" />
<script type="text/javascript" src="js/jquery/jquery.fieldfocus.js"></script>

<script type="text/javascript">
$(function() {
	$('.datepicker').datepicker({
		changeMonth: true,
		changeYear:true,
		yearRange: '1940:2010',
		showOn: 'button',
		buttonImage: 'images/icon_calendar.gif',
		buttonImageOnly: true,
		buttonText: 'Choose a date...',
		constrainInput: true,
		showAnim: 'fadeIn'
	});
	$('.cluetip').cluetip({
		closeText: "<img src='images/cross.png' width='16' height='16'>",
		arrows: true,
		cluetipClass: 'jtip',
		local: true,
		sticky: true,
		mouseOutClose: true,
		clickThrough: false
	});		
	$("#tabs").tabs();	
});

function checkReason(id) {
	var text = $('#' + id + '_override').find('[name="forceNote"]').val();

	if (text == null || text == '') {
		alert("Please fill in reason");
		return false;
	} else {
		return true;
	}
}

function openOverride(id){
	if($('#'+id+'_override').is(":visible")){
		$('#'+id+'_override_link_text').show();
		$('#'+id+'_override_link_hide').hide();
		$('._override_').hide();
		return;
	}
	$('._override_:visible').hide();
	$('#'+id+'_override').show();
	$('.override_link_hide').hide()
	$('.override_link_show').show();
	$('#'+id+'_override_link_hide').show();
	$('#'+id+'_override_link_text').hide();
}
</script>
</head>
<body>

<s:push value="#subHeading='Flag Status'" />
<s:include value="conHeader.jsp" />

<!-- OVERALL FLAG -->

<s:if test="permissions.contractor">
<div class="helpOnRight" style="clear: right;">
		The minimum requirements set by <s:property value="co.operatorAccount.name"/> are listed in this page. 
		If any requirements exceed the acceptable threshold or answer, those requirements will be flagged. The overall flag color is set to Red if any requirement is flagged Red. 
		It is set to Amber if any requirement is flagged Amber. If no requirement is Red or Amber, then the overall flag color will be Green.
</div>
</s:if>

		<div id="info-box">
		<div class="info">
			<s:form>
				<s:hidden name="id" />
				<s:hidden name="opID" />
				<s:if test="contractor.lastRecalculation != null"><s:if test="permissions.admin || permissions.operatorCorporate">Contractor's flag</s:if><s:else>Flag</s:else> last calculated <s:date name="contractor.lastRecalculation" nice="true" />.<br /></s:if>
				<s:else>Contractor's flag has not been calculated.<br /></s:else>
				<button class="picsbutton" type="submit" name="button" value="Recalculate Now">Recalculate Now</button>
			</s:form>
		</div></div>

<table>
	<tr>
		<td style="vertical-align:top;">
		<div class="panel_placeholder">
		<div class="panel" style="min-width:680px;">
			<div class="panel_header">
				Flag Status
			</div>
			<div class="panel_content">
			<div class="bigFlagIcon">
			<s:property	value="co.flagColor.bigIcon" escape="false" /></div>
			<div class="FlagCriteriaContent">
			<b>
			<pics:permission perm="EditFlagCriteria">
				<a href="ManageFlagCriteriaOperator.action?id=<s:property value="co.operatorAccount.inheritFlagCriteria.id" />" title="View Flag Criteria"><s:property value="co.operatorAccount.name"/></a>		
			</pics:permission>
			<pics:permission perm="EditFlagCriteria" negativeCheck="true">
				<s:property value="co.operatorAccount.name"/>
			</pics:permission>
			</b>
			<br />
	Currently waiting on <b><s:property value="co.waitingOn"/></b></div>
	<div class="clear"></div>
	<div style="margin-left:10px;">
		<s:if test="co.flagColor.clear" >
			This <s:property value="co.contractorAccount.status"/> 
			<s:if test="co.contractorAccount.acceptsBids"> bid-only </s:if>
			contractor has a "Not Applicable" flag color.
			The old way of representing with Green/Red flag color was 
			misleading as the contractor has not yet completed all the operator requirements.
		</s:if>
	</div>
	<div style="margin-left:10px;">
		<s:if test="co.forceOverallFlag != null">
			<s:form cssStyle="border: 2px solid #A84D10; background-color: #FFC; padding: 10px;">
				Manual Force Flag <s:property value="co.forceOverallFlag.forceFlag.smallIcon" escape="false" /><br/> until <s:date name="co.forceOverallFlag.forceEnd" format="MMM d, yyyy" /> 
				by <s:property value="co.forceOverallFlag.forcedBy.name" /> from <s:property value="co.forceOverallFlag.forcedBy.account.name"/> 
				<s:if test="co.forceOverallFlag.operatorAccount.type == 'Corporate'"> for all the sites</s:if>.
				<s:if test="co.forceOverallFlag.forcedBy != null">
					<br/>
					<a href="ContractorNotes.action?id=<s:property value="contractor.id"/>&filter.userID=<s:property value="co.forceOverallFlag.forcedBy.id"/>&filter.category=Flags&filter.keyword=Forced">View Notes</a>
				</s:if>
			</s:form>
		</s:if>
		<br/>
		<pics:permission perm="EditForcedFlags">
			<s:if test="canForceOverallFlag(co.forceOverallFlag)">
				<s:form cssStyle="border: 2px solid #A84D10; background-color: #FFC; padding: 10px;">
					<br/>
					<s:hidden name="id" />
					<s:hidden name="opID" />
					<s:if test="co.forceOverallFlag.operatorAccount.type == 'Corporate'">
						<s:hidden name="overrideAll" value="true"/>
						<label>By Clicking on the Cancel Override the force flags at all your facilities will be removed.</label><br/>
					</s:if>
					Reason:<br><s:textarea name="forceNote" value="" rows="3" cols="15"></s:textarea>
					<div>
						<button class="picsbutton positive" type="submit" name="button" value="Cancel Override">Cancel Override</button>
					</div>
				</s:form>
			</s:if>
			<s:else>
				<div id="override" style="display: none">
				<s:form id="form_override">
					<s:hidden name="id" />
					<s:hidden name="opID" />
					<fieldset class="form">
					<ol>
					<li>
						<span class="label-txt">Force Flag to:</span>
						<s:radio id="forceFlag" list="unusedCoFlag" name="forceFlag" theme="pics" />
					</li>
					<li> 
						<span class="label-txt">Until:</span> 
						<input id="forceEnd" name="forceEnd" size="8" type="text" class="datepicker" />
					</li>
					<li>
						<span class="label-txt">Reason:</span> 
						<s:textarea name="forceNote" value="" rows="2" cols="15" cssStyle="vertical-align: top;"></s:textarea><br />
						<div class="fieldhelp">
						<h3>Reason</h3>
                     <p class="redMain">* All Fields are required</p>									
					</div>
					</li>	
					
					<li>
						<button class="picsbutton positive" type="submit" name="button" value="Force Overall Flag">Force Overall Flag</button>
					</li>
					<s:if test="permissions.corporate">
						<s:checkbox id="overRAll_main" name="overrideAll"/><label for="overRAll_main">Override the Flag for all <s:property value="permissions.accountName" /> sites </label><br/>
					</s:if>
					</ol>
					</fieldset>
				</s:form>
				<a href="#" onclick="$('#override_link').slideDown(); $('#override').slideUp(); return false;">Nevermind</a>
				</div>
				<a id="override_link" href="#" onclick="$('#override').slideDown(); $('#override_link').slideUp(); return false;">Force Overall Flag Color</a><br />
			</s:else>
		</pics:permission>		
	</div>	
</div>
</div>
</div>	
</td>
	</tr>
</table>
<s:if test="co.flagColor.toString() == 'Green' && co.forceOverallFlag == null && flagDataOverrides.keySet().size() == 0">
	<div class="info" style="width: 50%; text-align: left">
		Congratulations, you've completed all the requirements for <s:property value="co.operatorAccount.name" />.<br />We will email you if there are any changes.
	</div>
</s:if>

<span id="thinking"></span>

<!-- Putting Tabs Here -->
<div id="tabs">
	<ul>
		<li><a href="#tabs-1">Problems</a></li>
		<li><a href="#tabs-2">Flagable Data</a></li>
		<li><a href="#tabs-3">Notes</a></li>
	</ul>
	<div id="tabs-1">
		<!-- OVERRIDES -->
		<s:if test="displayTable">
		<table class="report" style="clear: none;">
			<thead>
				<tr>
					<td>Flag</td>
					<td>Description</td>
					<td>Value</td>
					<pics:permission perm="EditForcedFlags">
						<td>Override</td>
					</pics:permission>
					<td>Override Detail</td>
				</tr>
			</thead>
			<tbody>
			<s:iterator id="key" value="getflagDataMap().keySet()">
				<s:iterator id="data" value="getflagDataMap().get(#key)">
					<s:if test="#data.flag.toString() == 'Red' || #data.flag.toString() == 'Amber' || isFlagDataOverride(#data)">
					<s:set name="flagoverride" value="%{isFlagDataOverride(#data)}"/>
						<tr class="<s:property value="#data.flag" />">
							<td class="center">
								<s:property value="#data.flag.smallIcon" escape="false" />
							</td>
							<!-- Description -->
							<td>
								<s:iterator id="opCriteria" value="co.operatorAccount.flagCriteriaInherited">
									<s:if test="#opCriteria.criteria.id == #data.criteria.id && (#opCriteria.flag == #data.flag || (#flagoverride != null && #opCriteria.flag.toString() == 'Red'))">
										<s:if test="#data.criteria.oshaType != null || (#data.criteria.question != null && #data.criteria.question.id == 2034)">
											<span title="The statistics provided must have a status other than pending to be calculated.">
												<s:property value="#opCriteria.replaceHurdle" />
											</span>
										</s:if>
										<s:else>
											<s:property value="#opCriteria.replaceHurdle" />
										</s:else>
									</s:if>
								</s:iterator>
							</td>
							<!-- flagoverride info -->
								<s:if test="#flagoverride != null">
									<span style="display: none;" title='By <s:property value="#flagoverride.updatedBy.name" /> from <s:property value="#flagoverride.updatedBy.account.name"/><s:if test="#flagoverride.updatedBy.account.corporate"> for all the sites</s:if>'>
										Manual Force Flag <s:property value="#flagoverride.forceFlag.smallIcon" escape="false" /> until <s:date name="#flagoverride.forceEnd" format="MMM d, yyyy" />
										<a href='ContractorNotes.action?id=<s:property value="contractor.id"/>&amp;filter.userID=<s:property value="#flagoverride.updatedBy.id"/>&amp;filter.category=Flags&amp;filter.keyword=Forced'>View Notes</a>
									</span>
								</s:if>
							<!-- Value -->
							<td>
								<s:if test="#data.criteria.auditType != null">
									<s:iterator id="audit" value="contractor.audits">
										<s:if test="#data.criteria.auditType == #audit.auditType">
											<s:if test="#data.criteria.auditType.classType.policy && !(#audit.auditStatus.expired)">
												<s:iterator value="#audit.operators">
													<s:if test="visible && (co.operatorAccount.inheritInsurance == operator)">
														<s:if test="isCanSeeAudit(#audit.auditType)">
															<a href="Audit.action?auditID=<s:property value="#audit.id" />"><s:property value="#audit.auditType.auditName" /></a>
															<s:property value="status"/><br/>
														</s:if>
													</s:if> 
												</s:iterator>
											</s:if>
											<s:else>
												<s:if test="#audit.auditStatus.pendingSubmitted || #audit.auditStatus.incomplete">
													<s:if test="isCanSeeAudit(#audit.auditType)">
														<a href="Audit.action?auditID=<s:property value="#audit.id" />"><s:property value="#audit.auditFor" /> <s:property value="#audit.auditType.auditName" /></a>
													</s:if>
													<s:property value="#audit.auditStatus" /><br />
												</s:if>
											</s:else>
										</s:if>
									</s:iterator>
								</s:if>
								<s:else>
									<s:iterator id="conCriteria" value="contractor.flagCriteria">					
										<s:if test="#data.criteria.id == #conCriteria.criteria.id">
											<s:property value="getContractorAnswer(#conCriteria, #data, false)" escape="false" />
										</s:if>
									</s:iterator>
								</s:else>
							</td>
							<!-- Override -->
							<pics:permission perm="EditForcedFlags">
								<td class="center">
									<a id="override_link_flagdata_<s:property value="%{#data.id}" />" href="#" 
										onclick="openOverride(<s:property value="%{#data.id}"/>); return false;">
										<s:if test="canForceDataFlag(#flagoverride)">
											<div class="override_link_show" id="<s:property value="%{#data.id}" />_override_link_text">Force Line</div>
											<div class="override_link_hide"  style="display: none;" id="<s:property value="%{#data.id}" />_override_link_hide">Hide</div>
										</s:if>
										<s:else>
											<div class="override_link_show" id="<s:property value="%{#data.id}" />_override_link_text">Flag has been forced</div>
											<div class="override_link_hide" style="display: none;" id="<s:property value="%{#data.id}" />_override_link_hide">Hide</div>
										</s:else>
									</a>
								</td>
							</pics:permission>
							<!-- Override Detail -->
							<td class="center">
								<s:if test="#flagoverride != null">
									<a href="#" onclick="return false;" class="cluetip help" rel="#cluetip<s:property value="#flagoverride.id"/>" title="Location"></a>	
										<div id="cluetip<s:property value="#flagoverride.id"/>">
											<span title='By <s:property value="#flagoverride.updatedBy.name" /> from <s:property value="#flagoverride.updatedBy.account.name"/><s:if test="#flagoverride.updatedBy.account.corporate"> for all the sites</s:if>'>
												Manual Force Flag <s:property value="#flagoverride.forceFlag.smallIcon" escape="false" /> until <s:date name="#flagoverride.forceEnd" format="MMM d, yyyy" />
												<a href='ContractorNotes.action?id=<s:property value="contractor.id"/>&amp;filter.userID=<s:property value="#flagoverride.updatedBy.id"/>&amp;filter.category=Flags&amp;filter.keyword=Forced'>Search For Related Notes</a>
											</span>
										</div>						
								</s:if>
							</td>
						</tr>
						<pics:permission perm="EditForcedFlags">
							<tr id="<s:property value="%{#data.id}" />_override" class="_override_" style="display: none">
								<td colspan="5">
									<form method="post">
										<s:hidden value="%{#data.id}" name="dataID" />
										<s:if test="canForceDataFlag(#flagoverride)">
										<fieldset class="form">
											<ol>							
												<li>
													<label>Force Flag to:</label>
													<s:radio id="flag_%{#data.id}" list="getUnusedFlagColors(#data.id)" name="forceFlag" theme="pics" />
												</li> 
												<li> 
													<label>Until:</label> 
													<input id="forceEnd_<s:property value="%{#data.id}" />" name="forceEnd" size="8" type="text" class="datepicker" />
											</li>
											<li>
													<label>Reason for Forcing:</label>
													<s:textarea name="forceNote" value="" rows="2" cols="30" cssStyle="vertical-align: top;"></s:textarea>
													<div class="fieldhelp">
														<h3>Reason</h3>
                     								<p class="redMain">* All Fields are required</p>									
													</div>
											</li>	
											<li>
													<button class="picsbutton positive" type="submit" name="button" value="Force Individual Flag"
														onclick="return checkReason(<s:property value="%{#data.id}" />);">Force Individual Flag</button>
													<s:if test="permissions.corporate">
															<s:checkbox id="overRAll_%{#data.id}" name="overrideAll"/><label for="overRAll_<s:property value="%{#data.id}"/>">Override the Flag for all <s:property value="permissions.accountName" /> sites </label><br/>														
													</s:if>
													</li>
											</ol>
											</fieldset>
										</s:if>
										<s:else>
											<s:if test="#flagoverride.operator.type == 'Corporate'">
												<s:hidden name="overrideAll" value="true"/>
												<label>By Clicking on the Cancel Override the force flags at all your facilities will be removed.</label><br/>
											</s:if>
											&nbsp;Reason for Cancelling: <s:textarea name="forceNote" value="" rows="2" cols="15" cssStyle="vertical-align: top;"></s:textarea>
											<input type="submit" value="Cancel Data Override" class="picsbutton positive" name="button" 
												onclick="return checkReason(<s:property value="%{#data.id}" />);" />
										</s:else>
									</form>
								</td>
							</tr>
						</pics:permission>
					</s:if>
				</s:iterator>
			</s:iterator>
			</tbody>
		</table>
		</s:if>
	</div>
	<div id="tabs-2">
		<!-- ALL FLAGS -->
		<table class="flagCategories details">
			<tr>
			<s:iterator id="key" value="getflagDataMap().keySet()">
				<td><table class="report">
					<thead>
						<tr>
							<td>Flag</td>
							<td><s:property value="#key"/></td>
						</tr>
					</thead>
					<s:iterator id="data" value="getflagDataMap().get(#key)">
						<tr>
							<td class="center">
								<s:property value="flag.smallIcon" escape="false"/>
							</td>
							<td>
								<s:if test="criteria.auditType != null">
									<s:property value="criteria.auditType.auditName" />
								</s:if>
								<s:else>
									<s:iterator id="conCriteria" value="contractor.flagCriteria">					
										<s:if test="#data.criteria.id == #conCriteria.criteria.id">
											<s:property value="getContractorAnswer(#conCriteria, #data, true)" escape="false" />
										</s:if>	
									</s:iterator>
								</s:else>
							</td>			
						</tr>
					</s:iterator>
				</table></td>
			</s:iterator>
			</tr>
		</table>
	</div>
	<div id="tabs-3">		
		<s:if test="co.operatorAccount.approvesRelationships.toString() == 'Yes'">
			<s:if test="co.workStatusPending">
				<div class="alert">The operator has not approved this contractor yet.</div>
			</s:if>
			<s:if test="co.workStatusRejected">
				<div class="alert">The operator did not approve this contractor.</div>
			</s:if>
		</s:if>
		
		<div id="notesList" class="details""><s:include value="../notes/account_notes_embed.jsp"></s:include></div>
	</div>	
</div>

<div id="caldiv1" style="position:absolute; visibility:hidden; background-color:white; layer-background-color:white;"></div>

</body>
</html>
								
