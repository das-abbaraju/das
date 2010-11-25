<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
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

#tabs ul, #tabs ul li {list-style:none;}

#info-box {float:right;display:block;width:40%;}

.override_form { display: none; }
.override_wrap { float:left;width:100%;text-align:center;display:none }
.clickable .override_wrap { display: inline; }
.override_form .override_wrap { display: inline; }

</style>
<s:include value="../jquery.jsp" />

<script type="text/javascript">
$(function() {
	$('.datepicker').datepicker({
		changeMonth: true,
		changeYear:true,
		yearRange: '1940:'+ (new Date().getFullYear()+5),
		showOn: 'button',
		buttonImage: 'images/icon_calendar.gif',
		buttonImageOnly: true,
		buttonText: 'Choose a date...',
		constrainInput: true,
		showAnim: 'fadeIn'
	});
	$('.cluetip').cluetip({
		arrows: true,
		cluetipClass: 'jtip',
		local: true,
		clickThrough: false
	});		
	$("#tabs").tabs();

	$('a.override_table_link').click(function(e) {
		e.preventDefault();
	});

	$('tr._override_.clickable').live('click', function() {
		$(this).removeClass('clickable').removeClass('tr-hover-clickable').find('div.override_form').fadeIn('fast');
	});

	$('.override_hide').click(function() {
		var me = $(this);
		me.parent().fadeOut('fast',function(){
			me.parents('tr._override_').addClass('clickable');
		});
	});
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
</script>
</head>
<body>
<s:push value="#subHeading='Flag Status'" />
<s:include value="conHeader.jsp" />

<!-- OVERALL FLAG -->

<div id="cluetip2">
		The minimum requirements set by <s:property value="co.operatorAccount.name"/> are listed in this page. 
		If any requirements exceed the acceptable threshold or answer, those requirements will be flagged. The overall flag color is set to Red if any requirement is flagged Red. 
		It is set to Amber if any requirement is flagged Amber. If no requirement is Red or Amber, then the overall flag color will be Green.
</div>
<div id="info-box">
	<div class="info">
		<s:form>
			<s:hidden name="id" />
			<s:hidden name="opID" />
			<s:if test="contractor.lastRecalculation != null">
				<s:if test="permissions.admin || permissions.operatorCorporate">Contractor's flag</s:if>
				<s:else>Flag</s:else>
				last calculated <s:date name="contractor.lastRecalculation" nice="true" />.
			</s:if>
			<s:else>Contractor's flag has not been calculated.</s:else>
			<button class="picsbutton" type="submit" name="button" value="Recalculate Now">Recalculate Now</button>
		</s:form>
	</div>
</div>
	
<div style="width: 50%">
	<div class="panel_placeholder">
		<div class="panel">
			<div class="panel_header">
				Flag Status <span style="float: right;"><a href="#" onclick="return false;" class="cluetip help" rel="#cluetip2" title="Flag Help"></a></span>
			</div>
			<div class="panel_content">
				<div class="bigFlagIcon">
					<s:property	value="co.flagColor.bigIcon" escape="false" />
				</div>
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
					Currently waiting on <b><s:property value="co.waitingOn"/></b>
				</div>
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
							<s:form id="form_override" enctype="multipart/form-data" method="POST">
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
								<li class="required">
									<span class="label-txt">Reason:</span> 
									<s:textarea name="forceNote" value="" rows="2" cols="15" cssStyle="vertical-align: top;"></s:textarea><br />
									<div class="fieldhelp">
										<h3>Reason</h3>
					                    <p class="redMain">* All Fields are required</p>									
									</div>
								</li>
								<li>
									<span class="label-txt">File Attachment:</span>
									<s:file name="file" id="%{id}"></s:file>
								</li>
								<li>
									<button class="picsbutton positive" type="submit" name="button" value="Force Overall Flag">Force Overall Flag</button>
								</li>
								<li>
									<s:if test="permissions.corporate">
										<s:checkbox id="overRAll_main" name="overrideAll"/><label for="overRAll_main">Override the Flag for all <s:property value="permissions.accountName" /> sites </label><br/>
									</s:if>
								</li>
								</ol>
								</fieldset>
							</s:form>
							<a href="#" onclick="$('#override_link').slideDown(); $('#override').slideUp(); return false;">Nevermind</a>
							</div>
							<a id="override_link" class="override_wrench" href="#" onclick="$('#override').slideDown(); $('#override_link').slideUp(); return false;">Force Overall Flag Color</a><br />
						</s:else>
					</pics:permission>		
				</div>	
			</div>
		</div>
	</div>	
</div>
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
					<td>Override Detail</td>
				</tr>
			</thead>
			<tbody>
			<s:iterator id="key" value="flagDataMap.keySet()">
				<s:iterator id="data" value="flagDataMap.get(#key)">
					<s:if test="#data.flag.toString() == 'Red' || #data.flag.toString() == 'Amber' || isFlagDataOverride(#data)">
						<s:set name="flagoverride" value="%{isFlagDataOverride(#data)}"/>
						<tr class="<s:property value="#data.flag" />">
							<td class="center" <pics:permission perm="EditForcedFlags">rowspan="2"</pics:permission>>
								<s:property value="#data.flag.smallIcon" escape="false" />
							</td>
							<!-- Description -->
							<td>
								<s:iterator id="opCriteria" value="co.operatorAccount.flagCriteriaInherited">
									<s:if test="#opCriteria.criteria.id == #data.criteria.id && (#opCriteria.flag == #data.flag || (#flagoverride != null && #opCriteria.flag.toString() == 'Red'))">
										<s:if test="#data.criteria.oshaType != null || (#data.criteria.question != null && #data.criteria.question.id == 2034)">
											<s:property value="#opCriteria.replaceHurdle" /><a href="#" onclick="return false;" class="help cluetip" rel="#cluetip1" title="Statistics"></a>
											<div id="cluetip1">The statistics provided must have a status other than pending to be calculated.</div>
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
									<s:iterator id="audit" value="contractor.audits" status="auditstat">
										<s:if test="#data.criteria.auditType == #audit.auditType">
											<s:if test="!(#audit.expired)">
												<a href="Audit.action?auditID=<s:property value="#audit.id" />"><s:property value="#audit.auditType.auditName" /></a>
												<s:if test="#audit.auditType.auditName != 'Annual Update'">
													<s:iterator value="getCaoStats(permissions).keySet()" id="astat" status="stat">
														<s:property value="getCaoStats(permissions).get(#astat)" />
														<s:property value="#astat" /><s:if test="!#stat.last">, </s:if>
													</s:iterator>
												</s:if>
												<s:else>
													<s:property value="auditFor" />
													<s:iterator value="#audit.operators" id="cao">
														<s:if test="visible && isCanSeeAudit(#cao)">
															<s:property value="status"/><br/>
														</s:if> 
													</s:iterator>
												</s:else>
											</s:if>
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
							<tr id="<s:property value="%{#data.id}" />_override" class="_override_ clickable">
								<td colspan="3">
									<div class="override_form">
										<form enctype="multipart/form-data" method="POST">
											<s:hidden value="%{#data.id}" name="dataID" />
											<s:if test="canForceDataFlag(#flagoverride)">
												<s:if test="#flagoverride!=null">
													There is a site level override in effect by <s:property value="#flagoverride.createdBy.name"/>, 
													to cancel this override you must log in as a site level user.
												</s:if>
												<s:else>												
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
															<li class="required">
																<label>Reason for Forcing:</label>
																<s:textarea name="forceNote" value="" rows="2" cols="30" cssStyle="vertical-align: top;"></s:textarea>
																<div class="fieldhelp">
																	<h3>Reason</h3>
			                     								<p class="redMain">* All Fields are required</p>									
																</div>
															</li>
															<li>
																<span class="label-txt">File Attachment:</span>
																<s:file name="file" id="%{#data.id}_file"></s:file>
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
												</s:else>
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
										<span class="override_wrap override_hide"><a href="#" class="override_wrench override_table_link">Hide Force Line</a></span>
									</div>
									<span class="override_wrap"><a href="#" class="override_wrench override_table_link">Show Force Line</a></span>
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
			<s:iterator id="key" value="flagDataMap.keySet()">
				<td><table class="report">
					<thead>
						<tr>
							<td>Flag</td>
							<td><s:property value="#key"/></td>
						</tr>
					</thead>
					<s:iterator id="data" value="flagDataMap.get(#key)">
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
								
