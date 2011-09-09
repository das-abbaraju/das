<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:text name="ContractorFlag.title"><s:param><s:property value="contractor.name" /></s:param></s:text></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />

<style type="text/css">
.flagCategories td {
	padding-right: 10px;
	vertical-align: top;
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
		showOn: 'both',
		buttonImage: 'images/icon_calendar.gif',
		buttonImageOnly: true,
		buttonText: translate('JS.ContractorFlag.ChooseDate'),
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
		alert(translate("JS.ContractorFlag.FillInReason"));
		return false;
	} else {
		return true;
	}
}
</script>
</head>
<body>
<s:include value="conHeader.jsp" />

<!-- OVERALL FLAG -->

<div id="cluetip2">
<s:text name="ContractorFlag.MinimumOperatorRequirements"><s:param><s:property value="co.operatorAccount.name"/></s:param></s:text>
</div>
<div id="info-box">
	<div class="baseFlag">
		<s:if test="permissions.picsEmployee && (co.flagColor != co.baselineFlag)">
			<div class="alert">
				<s:form>
					<s:hidden name="id" />
					<s:hidden name="opID" />
					<s:text name="ContractorFlag.FlagRecentlyChanged">
					<s:param><s:property value="@com.picsauditing.jpa.entities.FlagColor@getSmallIcon(co.baselineFlag)" escape="false"/></s:param>
					<s:param><s:property value="co.baselineFlag" escape="false"/></s:param>
					<s:param><s:property value="@com.picsauditing.jpa.entities.FlagColor@getSmallIcon(co.flagColor)" escape="false"/></s:param>
					<s:param><s:property value="co.flagColor" escape="false"/></s:param>
					</s:text>
					<s:submit type="button" method="approveFlag" value="%{getText('button.ApproveFlag')}" cssClass="picsbutton positive" />
				</s:form>
			</div>
		</s:if>
	</div>
	<div class="info">
		<s:form>
			<s:hidden name="id" />
			<s:hidden name="opID" />
			<s:if test="contractor.lastRecalculation != null">
				<s:if test="permissions.admin || permissions.operatorCorporate">
				<s:text name="ContractorFlag.ContractorLastCalculated"><s:param><s:date name="contractor.lastRecalculation" nice="true" /></s:param></s:text></s:if>
				<s:else><s:text name="ContractorFlag.FlagLastCalculated"><s:param><s:date name="contractor.lastRecalculation" nice="true" /></s:param></s:text></s:else>
			</s:if>
			<s:else><s:text name="ContractorFlag.NotCalculated"></s:text></s:else>
			<s:submit type="button" name="button" method="recalculate" value="%{getText('button.RecalculateNow')}" cssClass="picsbutton positive" />
		</s:form>
	</div>
</div>
	
<div style="width: 50%">
	<div class="panel_placeholder">
		<div class="panel">
			<div class="panel_header">
				<s:else><s:text name="ContractorFlag.FlagStatus"></s:text></s:else> <span style="float: right;"><a href="#" onclick="return false;" class="cluetip help" rel="#cluetip2" title="<s:text name="ContractorFlag.FlagHelp" />"></a></span>
			</div>
			<div class="panel_content">
				<div class="bigFlagIcon">
					<s:property	value="co.flagColor.bigIcon" escape="false" />
				</div>
				<div class="FlagCriteriaContent">
					<b>
						<pics:permission perm="EditFlagCriteria">
							<a href="ManageFlagCriteriaOperator.action?id=<s:property value="co.operatorAccount.inheritFlagCriteria.id" />" title="<s:text name="ContractorFlag.ViewFlagCriteria" />"><s:property value="co.operatorAccount.name"/></a>		
						</pics:permission>
						<pics:permission perm="EditFlagCriteria" negativeCheck="true">
							<s:property value="co.operatorAccount.name"/>
						</pics:permission>
					</b>
					<br />
					<s:text name="ContractorFlag.CurrentlyWaitingOn"><s:param><s:property value="co.waitingOn"/></s:param></s:text>
				</div>
				<div class="clear"></div>
				<div style="margin-left:10px;">
					<s:if test="co.flagColor.clear" >
						<s:text name="ContractorFlag.NotApplicable">
						<s:param><s:property value="co.contractorAccount.status"/></s:param>
						<s:param><s:if test="co.contractorAccount.acceptsBids"> <s:text name="ContractorFlag.BidOnly"></s:text></s:if></s:param>
						</s:text>
					</s:if>
				</div>
				<div style="margin-left:10px;">
					<s:if test="co.forceOverallFlag != null || getFlagDataOverrides().size() > 0">
						<s:form cssStyle="border: 2px solid #A84D10; background-color: #FFC; padding: 10px;">
							<s:if test="co.forceOverallFlag != null">
							<s:text name="ContractorFlag.ManualForceFlagInfo">
							<s:param><s:property value="co.forceOverallFlag.forceFlag.smallIcon" escape="false" /></s:param>
							<s:param><s:date name="co.forceOverallFlag.forceEnd" format="MMM d, yyyy" /></s:param>
							<s:param><s:property value="co.forceOverallFlag.forcedBy.name" /></s:param>
							<s:param><s:property value="co.forceOverallFlag.forcedBy.account.name"/></s:param>
							<s:param><s:if test="co.forceOverallFlag.operatorAccount.type == 'Corporate'"> <s:text name="ContractorFlag.ForAllSites" /></s:if></s:param>
							</s:text>
							</s:if>
							<s:if test="getFlagDataOverrides().size() > 0">
								<s:iterator id="key" value="flagDataMap.keySet()">
									<s:iterator id="data" value="flagDataMap.get(#key)">
										<s:if test="#data.flag.redAmber || isFlagDataOverride(#data)">
										<s:set name="flagoverride" value="%{isFlagDataOverride(#data)}"/>							
											<s:if test="#flagoverride != null">
											<s:text name="ContractorFlag.ManualIndividualForceFlagInfo">
											<s:param><s:property value="#flagoverride.criteria.label" /></s:param>
											<s:param><s:property value="#flagoverride.forceflag.getSmallIcon()" escape="false" /></s:param>
											<s:param><s:date name="#flagoverride.forceEnd" format="MMM d, yyyy" /></s:param>
											<s:param><s:property value="#flagoverride.updatedBy.name" /></s:param>
											<s:param><s:property value="#flagoverride.updatedBy.account.name"/></s:param>
											<s:param><s:if test="#flagoverride.operator.corporate"> <s:text name="ContractorFlag.ForAllSites" /></s:if></s:param>
											</s:text>
											</s:if>
										</s:if>
									</s:iterator>
								</s:iterator>
							</s:if>
							<a href='ContractorNotes.action?id=<s:property value="contractor.id"/>&amp;filter.category=Flags&amp;filter.keyword=Forced'><s:text name="ContractorFlag.ViewNotes" /></a>
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
									<label><s:text name="ContractorFlag.CancelOverride"></s:text></label><br/>
								</s:if>
								<s:text name="ContractorFlag.Reason"></s:text>:<br><s:textarea name="forceNote" value="" rows="3" cols="15"></s:textarea>
								<div>
									<s:submit type="button" name="button" method="cancelOverride" value="%{getText('button.CancelOverride')}" cssClass="picsbutton positive" />
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
									<span class="label-txt"><s:text name="ContractorFlag.ForceFlagTo"></s:text></span>
									<s:radio id="forceFlag" list="unusedCoFlag" name="forceFlag" theme="pics" />
								</li>
								<li> 
									<span class="label-txt"><s:text name="ContractorFlag.Until"></s:text></span> 
									<input id="forceEnd" name="forceEnd" size="10" type="text" class="datepicker" />
								</li>
								<li class="required">
									<span class="label-txt"><s:text name="ContractorFlag.Reason"></s:text>:</span> 
									<s:textarea name="forceNote" value="" rows="2" cols="15" cssStyle="vertical-align: top;"></s:textarea><br />
									<div class="fieldhelp">
										<h3><s:text name="ContractorFlag.Reason" /></h3>
										<s:text name="ContractorFlag.AllFieldsRequired" />
									</div>
								</li>
								<li>
									<span class="label-txt"><s:text name="ContractorFlag.FileAttachment"></s:text></span>
									<s:file name="file" id="%{id}"></s:file>
								</li>
								<li>
									<s:submit type="button" name="button" method="forceOverallFlag" value="%{getText('button.ForceOverrideFlag')}" cssClass="picsbutton positive" />
								</li>
								<li>
									<s:if test="permissions.corporate">
										<s:checkbox id="overRAll_main" name="overrideAll"/><label for="overRAll_main"><s:text name="ContractorFlag.OverrideForAllSites"><s:param><s:property value="permissions.accountName" /></s:param></s:text></label><br/>
									</s:if>
								</li>
								</ol>
								</fieldset>
							</s:form>
							<a href="#" onclick="$('#override_link').slideDown(); $('#override').slideUp(); return false;"><s:text name="ContractorFlag.Nevermind"></s:text></a>
							</div>
							<a id="override_link" class="override_wrench" href="#" onclick="$('#override').slideDown(); $('#override_link').slideUp(); return false;"><s:text name="ContractorFlag.ForceOverallFlagColor"></s:text></a><br />
						</s:else>
					</pics:permission>		
				</div>	
			</div>
		</div>
	</div>
</div>
<s:if test="co.flagColor.toString() == 'Green' && co.forceOverallFlag == null && flagDataOverrides.keySet().size() == 0">
	<div class="info" style="width: 50%; text-align: left">
		<s:text name="ContractorFlag.Congratulations"><s:param><s:property value="co.operatorAccount.name" /></s:param></s:text>
	</div>
</s:if>

<span id="thinking"></span>

<div class="clear"></div>
<!-- Putting Tabs Here -->
<div id="tabs">
	<ul>
		<li><a href="#tabs-1"><s:text name="ContractorFlag.Problems"></s:text></a></li>
		<li><a href="#tabs-2"><s:text name="ContractorFlag.FlagableData"></s:text></a></li>
		<li><a href="#tabs-3"><s:text name="ContractorFlag.Notes"></s:text></a></li>
	</ul>
	<div id="tabs-1">
		<!-- OVERRIDES -->
		<s:if test="displayTable">
		<table class="report" style="clear: none;">
			<thead>
				<tr>
					<td><s:text name="ContractorFlag.Flag"></s:text></td>
					<td><s:text name="ContractorFlag.Description"></s:text></td>
					<td><s:text name="ContractorFlag.Value"></s:text></td>
					<td><s:text name="ContractorFlag.OverrideDetail"></s:text></td>
				</tr>
			</thead>
			<tbody>
			<s:iterator id="key" value="flagDataMap.keySet()">
				<s:iterator id="data" value="flagDataMap.get(#key)">
					<s:if test="#data.flag.redAmber || isFlagDataOverride(#data)">
						<s:set name="flagoverride" value="%{isFlagDataOverride(#data)}"/>
						<tr class="<s:property value="#data.flag" />">
							<td class="center" <pics:permission perm="EditForcedFlags">rowspan="2"</pics:permission>>
								<s:property value="#data.flag.smallIcon" escape="false" />
							</td>
							<!-- Description -->
							<td>
								<s:set name="opCriteria" value="getApplicableOperatorCriteria(#data)" />
								<s:if test="#opCriteria != null">
									<s:if test="#data.criteria.oshaType != null || (#data.criteria.question != null && #data.criteria.question.id == 2034)">
										<s:property value="#opCriteria.replaceHurdle" /><a href="#" onclick="return false;" class="help cluetip" rel="#cluetip<s:property value="#opCriteria.id" />" title="<s:text name="ContractorFlag.Statistics"></s:text>"></a>
										<div id="cluetip<s:property value="#opCriteria.id" />"><s:text name="ContractorFlag.StatusNotPending"></s:text></div>
									</s:if>
									<s:else>
										<s:property value="#opCriteria.replaceHurdle" />
									</s:else>
								</s:if>
							</td>
							<!-- flagoverride info -->
								<s:if test="#flagoverride != null">
									<span style="display: none;" title='<s:text name="ContractorFlag.ByTitle"><s:param><s:property value="#flagoverride.updatedBy.name" /></s:param><s:param><s:property value="#flagoverride.updatedBy.account.name"/></s:param><s:param><s:if test="#flagoverride.updatedBy.account.corporate"><s:text name="ContractorFlag.ForAllSites" /></s:if></s:param></s:text>'>
										<s:text name="ContractorFlag.ManualForceFlagShort">
											<s:param><s:property value="#flagoverride.forceFlag.smallIcon" escape="false" /></s:param>
											<s:param><s:date name="#flagoverride.forceEnd" format="MMM d, yyyy" /></s:param>
											<s:param></s:param>
											<s:param></s:param>
											<s:param></s:param>
										</s:text>
										<a href='ContractorNotes.action?id=<s:property value="contractor.id"/>&amp;filter.userID=<s:property value="#flagoverride.updatedBy.id"/>&amp;filter.category=Flags&amp;filter.keyword=Forced'><s:text name="ContractorFlag.ViewNotes"></s:text></a>
									</span>
								</s:if>
							<!-- Value -->
							<td>
								<s:if test="#data.criteria.auditType != null">
									<s:iterator value="missingAudits.get(#data.criteria.auditType)" var="cao">
										<s:text name="ContractorFlag.ProblemValue">
											<s:param><s:text name="%{status.i18nKey}"/></s:param>
											<s:param><a href="Audit.action?auditID=<s:property value="#cao.audit.id" />"><s:property value="#data.criteria.auditType.name" /> <s:property value="audit.auditFor" /></a></s:param>
										</s:text>
										<br/>
									</s:iterator>
									<s:if test="#data.criteria.auditType.classType.policy">
										<s:set var="cao" value="missingAudits.get(#data.criteria.auditType)" />
										<p style="padding-left: 20px; font-size: x-small">
										<s:if test="#cao.flag != null">
											<s:text name="ContractorFlag.Recommend"></s:text> <s:property value="#cao.flag"/><br />
										</s:if>
										<s:iterator var="insuranceFlagData" value="co.flagDatas">
											<s:if test="#insuranceFlagData.criteria.insurance && #insuranceFlagData.criteria.question.auditType == #data.criteria.auditType">
												<s:iterator id="conCriteria" value="contractor.flagCriteria">
													<s:if test="#insuranceFlagData.criteria.id == #conCriteria.criteria.id">
														<s:property value="getContractorAnswer(#conCriteria, #insuranceFlagData, true)" escape="false" /><br />
													</s:if>
												</s:iterator>
											</s:if>
										</s:iterator>
										</p>
									</s:if>
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
									<a href="#" onclick="return false;" class="cluetip help" rel="#cluetip<s:property value="#flagoverride.id"/>" title="<s:text name="ContractorFlag.Location"></s:text>"><s:text name="ContractorFlag.ManualForceFlag"></s:text> </a>	
										<div id="cluetip<s:property value="#flagoverride.id"/>">
											<span title='<s:text name="ContractorFlag.ByTitle"><s:param><s:property value="#flagoverride.updatedBy.name" /></s:param><s:param><s:property value="#flagoverride.updatedBy.account.name"/></s:param><s:param><s:if test="#flagoverride.updatedBy.account.corporate"> <s:text name="ContractorFlag.ForAllSites" /></s:if></s:param></s:text>'>
												<s:text name="ContractorFlag.ForceFlagTo"></s:text> <s:property value="#flagoverride.forceFlag.smallIcon" escape="false" /> <s:text name="ContractorFlag.Until"></s:text> <s:date name="#flagoverride.forceEnd" format="MMM d, yyyy" />
												<a href='ContractorNotes.action?id=<s:property value="contractor.id"/>&amp;filter.userID=<s:property value="#flagoverride.updatedBy.id"/>&amp;filter.category=Flags&amp;filter.keyword=Forced'><s:text name="ContractorFlag.SearchNotes"></s:text></a>
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
												<s:text name="ContractorFlag.SiteOverride"><s:param><s:property value="#flagoverride.createdBy.name"/></s:param></s:text>
												</s:if>
												<s:else>												
													<fieldset class="form">
														<ol>							
															<li>
																<label><s:text name="ContractorFlag.ForceFlagTo"></s:text></label>
																<s:radio id="flag_%{#data.id}" list="getUnusedFlagColors(#data.id)" name="forceFlag" theme="pics" />
															</li> 
															<li> 
																<label><s:text name="ContractorFlag.Until"></s:text></label> 
																<input id="forceEnd_<s:property value="%{#data.id}" />" name="forceEnd" size="10" type="text" class="datepicker" />
															</li>
															<li class="required">
																<label><s:text name="ContractorFlag.Reason"></s:text>:</label>
																<s:textarea name="forceNote" value="" rows="2" cols="30" cssStyle="vertical-align: top;"></s:textarea>
																<div class="fieldhelp">
																	<h3><s:text name="ContractorFlag.Reason" /></h3>
																	<s:text name="ContractorFlag.AllFieldsRequired" />
																</div>
															</li>
															<li>
																<span class="label-txt"><s:text name="ContractorFlag.FileAttachment"></s:text></span>
																<s:file name="file" id="%{#data.id}_file"></s:file>
															</li>
															<li>
																<s:submit type="button" name="button" method="forceIndividualFlag"  onclick="return checkReason(%{#data.id})" value="%{getText('ContractorFlag.ForceIndividualFlag')}" cssClass="picsbutton positive" />
																<s:if test="permissions.corporate">
																		<s:checkbox id="overRAll_%{#data.id}" name="overrideAll"/><label for="overRAll_<s:property value="%{#data.id}"/>"><s:text name="ContractorFlag.OverrideForAllSites"><s:param><s:property value="permissions.accountName" /></s:param></s:text></label><br/>														
																</s:if>
																</li>
															</ol>
														</fieldset>
												</s:else>
											</s:if>
											<s:else>
												<s:if test="#flagoverride.operator.type == 'Corporate'">
													<s:hidden name="overrideAll" value="true"/>
													<label><s:text name="ContractorFlag.CancelOverrideInfo"></s:text></label><br/>
												</s:if>
												&nbsp;<s:text name="ContractorFlag.ReasonCancelling"></s:text> <s:textarea name="forceNote" value="" rows="2" cols="15" cssStyle="vertical-align: top;"></s:textarea>
												<s:submit type="button" name="button" method="cancelDataOverride"  onclick="return checkReason(%{#data.id})" value="%{getText('button.CancelDataOverride')}" cssClass="picsbutton positive" />
											</s:else>
										</form>
										<span class="override_wrap override_hide"><a href="#" class="override_wrench override_table_link"><s:text name="ContractorFlag.HideForceLine"></s:text></a></span>
									</div>
									<span class="override_wrap"><a href="#" class="override_wrench override_table_link"><s:text name="ContractorFlag.ShowForceLine"></s:text></a></span>
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
			<s:iterator id="flagData" value="flagDataMap">
				<td><table class="report">
					<thead>
						<tr>
							<td><s:text name="ContractorFlag.Flag"></s:text></td>
							<td><s:property value="#flagData.key"/></td>
						</tr>
					</thead>
					<s:iterator id="data" value="#flagData.value">
						<tr>
							<td class="center">
								<s:property value="flag.smallIcon" escape="false"/>
							</td>
							<td>
								<s:if test="criteria.auditType != null">
									<s:property value="criteria.auditType.name" />
								</s:if>
								<s:else>
									<s:property value="getContractorAnswer(#data, true)" escape="false"/>
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
				<div class="alert"><s:text name="ContractorFlag.OperatorHasNotApproved"></s:text></div>
			</s:if>
			<s:if test="co.workStatusRejected">
				<div class="alert"><s:text name="ContractorFlag.OperatorDidNotApproved"></s:text></div>
			</s:if>
		</s:if>
		
		<div id="notesList" class="details""><s:include value="../notes/account_notes_embed.jsp"></s:include></div>
	</div>	
</div>

<div id="caldiv1" style="position:absolute; visibility:hidden; background-color:white; layer-background-color:white;"></div>

</body>
</html>
								
