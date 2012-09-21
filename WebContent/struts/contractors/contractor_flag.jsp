<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<head>
	<title>
		<s:text name="ContractorFlag.title">
			<s:param>
				<s:property value="contractor.name" />
			</s:param>
		</s:text>
	</title>
	
	<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css?v=${version}" />
	<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=${version}" />
	<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=${version}" />
	<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=${version}" />
	
	<link rel="stylesheet" type="text/css" media="screen" href="css/contractor_flag.css?v=${version}" />
</head>
<body>
	<s:include value="conHeader.jsp" />
	
	<div id="${actionName}_${methodName}_page" class="${actionName}-page page">
		<!-- OVERALL FLAG -->
		<div id="cluetip2">
			<s:text name="ContractorFlag.MinimumOperatorRequirements">
				<s:param>
					<s:property value="co.operatorAccount.name" />
				</s:param>
			</s:text>
		</div>
		<div id="info-box">
			<div class="baseFlag">
				<s:if test="permissions.picsEmployee && (co.flagColor != co.baselineFlag)">
					<div class="alert">
						<s:form>
							<s:hidden name="id" />
							<s:hidden name="opID" />
	
							<s:text name="ContractorFlag.FlagRecentlyChanged">
								<s:param>
									<s:property value="@com.picsauditing.jpa.entities.FlagColor@getSmallIcon(co.baselineFlag)" escape="false" />
								</s:param>
								<s:param>
									<s:property value="co.baselineFlag" escape="false" />
								</s:param>
								<s:param>
									<s:property value="@com.picsauditing.jpa.entities.FlagColor@getSmallIcon(co.flagColor)" escape="false" />
								</s:param>
								<s:param>
									<s:property value="co.flagColor" escape="false" />
								</s:param>
							</s:text>
	
							<s:submit
								type="button"
								method="approveFlag"
								value="%{getText('button.ApproveFlag')}"
								cssClass="picsbutton positive"
							/>
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
							<s:text name="ContractorFlag.ContractorLastCalculated">
								<s:param>
									<s:date name="contractor.lastRecalculation" nice="true" />
								</s:param>
							</s:text>
						</s:if>
						<s:else>
							<s:text name="ContractorFlag.FlagLastCalculated">
								<s:param>
									<s:date name="contractor.lastRecalculation" nice="true" />
								</s:param>
							</s:text>
						</s:else>
					</s:if>
					<s:else>
						<s:text name="ContractorFlag.NotCalculated"></s:text>
					</s:else>
	
					<s:submit
						type="button"
						name="button"
						method="recalculate"
						value="%{getText('button.RecalculateNow')}"
						cssClass="picsbutton positive"
					/>
				</s:form>
			</div>
		</div>
	
		<div style="width: 50%">
			<div class="panel_placeholder">
				<div class="panel">
					<div class="panel_header">
						<s:text name="ContractorFlag.FlagStatus" />
						<span style="float: right;">
							<a
								href="javascript:;"
								class="cluetip help"
								rel="#cluetip2"
								title="<s:text name="ContractorFlag.FlagHelp" />"></a>
						</span>
					</div>
					<div class="panel_content">
						<div class="bigFlagIcon">
							<s:property value="co.flagColor.bigIcon" escape="false" />
						</div>
						<div class="FlagCriteriaContent">
							<b>
								<pics:permission perm="EditFlagCriteria">
									<s:url var="manage_flag_criteria_operator" action="ManageFlagCriteriaOperator">
										<s:param name="id" value="%{co.operatorAccount.inheritFlagCriteria.id}" />
									</s:url>
									<a
										href="${manage_flag_criteria_operator}"
										title="<s:text name="ContractorFlag.ViewFlagCriteria" />">
										<s:property value="co.operatorAccount.name" />
									</a>
								</pics:permission>
								<pics:permission perm="EditFlagCriteria" negativeCheck="true">
									<s:property value="co.operatorAccount.name" />
								</pics:permission>
							</b>
							<br />
							<s:text name="ContractorFlag.CurrentlyWaitingOn">
								<s:param>
									<s:property value="co.waitingOn" />
								</s:param>
							</s:text>
							<s:if test="displayCorporate">
								<p>
									<s:text name="global.Locations" />:
									<s:property value="activeOperators.size()"/>
									
									<s:if test="flagCounts.size() > 0">
										(<s:iterator value="flagCounts" status="stat">	
											<s:property value="value"/>
											<s:property value="key.smallIcon" escape="false"/><s:if test="!#stat.last">, </s:if>
										</s:iterator>)
									</s:if>
								</p>
							</s:if>
						</div>
						<div class="clear"></div>
						<div style="margin-left: 10px;">
							<s:if test="co.flagColor.clear">
								<s:text name="ContractorFlag.NotApplicable">
									<s:param>
										<s:property value="co.contractorAccount.status" />
									</s:param>
									<s:param>
										<s:if test="co.contractorAccount.accountLevel.bidOnly">
											<s:text name="ContractorFlag.BidOnly" />
										</s:if>
									</s:param>
								</s:text>
							</s:if>
						</div>
						<div style="margin-left: 10px;">
							<s:if test="co.forceOverallFlag != null || getFlagDataOverrides(co.operatorAccount).size() > 0">
								<s:form cssStyle="border: 2px solid #A84D10; background-color: #FFC; padding: 10px;">
									<s:if test="co.forceOverallFlag != null">
										<s:text name="ContractorFlag.ManualForceFlagInfo">
											<s:param>
												<s:property value="co.forceOverallFlag.forceFlag.smallIcon" escape="false" />
											</s:param>
											<s:param>
												<s:date name="co.forceOverallFlag.forceEnd" format="%{@com.picsauditing.util.PicsDateFormat@IsoLongMonth}" />
											</s:param>
											<s:param>
												<s:property value="co.forceOverallFlag.forcedBy.name" />
											</s:param>
											<s:param>
												<s:property value="co.forceOverallFlag.forcedBy.account.name" />
											</s:param>
											<s:param>
												<s:if test="co.forceOverallFlag.operatorAccount.type == 'Corporate'">
													<s:text name="ContractorFlag.ForAllSites" />
												</s:if>
											</s:param>
										</s:text>
									</s:if>
									
									<s:if test="displayCorporate" >
										<s:iterator value="corporateOverrides" var="currentOp">
											<s:if test="#currentOp.forceOverallFlag != null" >
												<s:text name="ContractorFlag.ManualForceFlagInfo">
													<s:param>
														<s:property value="#currentOp.forceOverallFlag.forceFlag.smallIcon" escape="false" />
													</s:param>
													<s:param>
														<s:date name="#currentOp.forceOverallFlag.forceEnd" format="%{@com.picsauditing.util.PicsDateFormat@IsoLongMonth}" />
													</s:param>
													<s:param>
														<s:property value="#currentOp.forceOverallFlag.forcedBy.name" />
													</s:param>
													<s:param>
														<s:property value="#currentOp.forceOverallFlag.forcedBy.account.name" />
													</s:param>
													<s:param>
														<s:if test="#currentOp.forceOverallFlag.operatorAccount.type == 'Corporate'">
															<s:text name="ContractorFlag.ForAllSites" />
														</s:if>
													</s:param>
										</s:text>
											</s:if>
										</s:iterator>
									</s:if>
	
									<s:if test="getFlagDataOverrides(co.operatorAccount).size() > 0 && !displayCorporate">
										<s:iterator id="key" value="flagDataMap.keySet()">
											<s:iterator id="data" value="flagDataMap.get(#key)">
												<s:if test="#data.flag.redAmber || isFlagDataOverride(#data, #data.operator)">
													<s:set name="flagoverride" value="%{isFlagDataOverride(#data, #data.operator)}" />
	
													<s:if test="#flagoverride != null">
														<s:text name="ContractorFlag.ManualIndividualForceFlagInfo">
															<s:param>
																<s:property value="#flagoverride.criteria.label" />
															</s:param>
															<s:param>
																<s:property value="#flagoverride.forceflag.getSmallIcon()" escape="false" />
															</s:param>
															<s:param>
																<s:date name="#flagoverride.forceEnd" format="%{@com.picsauditing.util.PicsDateFormat@IsoLongMonth}" />
															</s:param>
															<s:param>
																<s:property value="#flagoverride.updatedBy.name" />
															</s:param>
															<s:param>
																<s:property value="#flagoverride.updatedBy.account.name" />
															</s:param>
															<s:param>
																<s:if test="#flagoverride.operator.corporate">
																	<s:text name="ContractorFlag.ForAllSites" />
																</s:if>
															</s:param>
														</s:text>
													</s:if>
												</s:if>
											</s:iterator>
										</s:iterator>
									</s:if>
									<s:if test="getFlagDataOverrides(co.operatorAccount).size() > 0 && displayCorporate">
										<s:iterator id="key" value="flagDataMap.keySet()">
											<s:iterator id="data" value="flagDataMap.get(#key)">
												<s:if test="#data.flag.redAmber || isFlagDataOverride(#data, #data.operator)">
													<s:set name="flagoverride" value="%{isFlagDataOverride(#data, #data.operator)}" />
	
													<s:if test="#flagoverride != null">
														<s:text name="ContractorFlag.ManualIndividualForceFlagInfo">
															<s:param>
																<s:property value="#flagoverride.criteria.label" />
															</s:param>
															<s:param>
																<s:property value="#flagoverride.forceflag.getSmallIcon()" escape="false" />
															</s:param>
															<s:param>
																<s:date name="#flagoverride.forceEnd" format="%{@com.picsauditing.util.PicsDateFormat@IsoLongMonth}" />
															</s:param>
															<s:param>
																<s:property value="#flagoverride.updatedBy.name" />
															</s:param>
															<s:param>
																<s:property value="#flagoverride.updatedBy.account.name" />
															</s:param>
															<s:param>
																<s:if test="#flagoverride.operator.corporate">
																	<s:text name="ContractorFlag.ForAllSites" />
																</s:if>
															</s:param>
														</s:text>
													</s:if>
												</s:if>
											</s:iterator>
										</s:iterator>
									</s:if>
									<s:url var="contractor_notes" action="ContractorNotes">
										<s:param name="id" value="%{contractor.id}" />
										<s:param name="filter.category" value="Flags" />
										<s:param name="filter.keyword" value="Forced" />
									</s:url>
									<a href="${contractor_notes}">
										<s:text name="ContractorFlag.ViewNotes" />
									</a>
								</s:form>
							</s:if>
							<br />
	
							<pics:permission perm="EditForcedFlags">
								<s:if test="canForceOverallFlag(co.forceOverallFlag)">
									<s:form
										cssStyle="border: 2px solid #A84D10; background-color: #FFC; padding: 10px;">
										<br />
										<s:hidden name="id" />
										<s:hidden name="opID" />
	
										<s:if test="co.forceOverallFlag.operatorAccount.type == 'Corporate'">
											<s:hidden name="overrideAll" value="true" />
											<label><s:text name="ContractorFlag.CancelOverride"></s:text>
											</label>
											<br />
										</s:if>
	
										<s:text name="ContractorFlag.Reason"></s:text>:
											<br />
										<s:textarea name="forceNote" value="" rows="3" cols="15"></s:textarea>
	
										<div>
											<s:submit
												type="button"
												name="button"
												method="cancelOverride"
												value="%{getText('button.CancelOverride')}"
												cssClass="picsbutton positive" />
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
														<span class="label-txt">
															<s:text name="ContractorFlag.ForceFlagTo"></s:text>
														</span>
														<s:radio
															id="forceFlag"
															list="unusedCoFlag"
															name="forceFlag"
															theme="pics"
															cssClass="inline"
														/>
													</li>
													<li>
														<span class="label-txt">
															<s:text name="ContractorFlag.Until" />
														</span>
														<input
															id="forceEnd"
															name="forceEnd"
															size="10"
															type="text"
															class="datepicker"
														/>
													</li>
													<li class="required">
														<span class="label-txt">
															<s:text name="ContractorFlag.Reason" />:
														</span>
														<s:textarea
															name="forceNote"
															value=""
															rows="2"
															cols="15"
															cssStyle="vertical-align: top;"></s:textarea>
														<br />
														<div class="fieldhelp">
															<h3>
																<s:text name="ContractorFlag.Reason" />
															</h3>
															<s:text name="ContractorFlag.AllFieldsRequired" />
														</div></li>
													<li>
														<span class="label-txt">
															<s:text name="ContractorFlag.FileAttachment" />
														</span>
														<s:file name="file" id="%{id}"></s:file>
													</li>
													<li>
														<s:if test="permissions.corporate && !displayCorporate">
															<s:checkbox id="overRAll_main" name="overrideAll" />
															<label for="overRAll_main">
																<s:text name="ContractorFlag.OverrideForAllSites">
																	<s:param>
																		<s:property value="permissions.accountName" />
																	</s:param>
																</s:text>
															</label>
															<br />
														</s:if>
													</li>
													<li>
														<s:submit
															type="button"
															name="button"
															method="forceOverallFlag"
															value="%{getText('button.ForceOverrideFlag')}"
															cssClass="picsbutton positive"
														/>
													</li>
												</ol>
											</fieldset>
										</s:form>
	
										<a href="javascript:;" class="toggle-override">
											<s:text name="ContractorFlag.Nevermind"></s:text>
										</a>
									</div>
	
									<s:if test="permittedToForceFlags" >
										<a id="override_link" class="override_wrench toggle-override" href="javascript:;">
											<s:text name="ContractorFlag.ForceOverallFlagColor" />
										</a>
									</s:if>
									<br />
								</s:else>
							</pics:permission>
						</div>
					</div>
				</div>
			</div>
		</div>
	
		<s:if test="co.flagColor.toString() == 'Green' && co.forceOverallFlag == null && flagDataOverrides.keySet().size() == 0">
			<div class="info" style="width: 50%; text-align: left">
				<s:text name="ContractorFlag.Congratulations">
					<s:param>
						<s:property value="co.operatorAccount.name" />
					</s:param>
				</s:text>
			</div>
		</s:if>
	
		<span id="thinking"></span>
	
		<div class="clear"></div>
	
		<!-- Putting Tabs Here -->
		<div id="tabs">
			<ul>
				<li>
					<a href="#tabs-1">
						<s:text name="ContractorFlag.Problems" />
					</a>
				</li>
				<li>
					<a href="#tabs-2">
						<s:text name="ContractorFlag.FlagableData" />
					</a>
				</li>
				<li>
					<a href="#tabs-3">
						<s:text name="ContractorFlag.Notes" />
					</a>
				</li>
			</ul>
	
			<div id="tabs-1">
				<!-- OVERRIDES -->
				<s:include value="_contractorFlagOverride.jsp" />
			</div>
			<div id="tabs-2">
				<!-- ALL FLAGS -->
				<s:include value="_contractorFlagAllFlags.jsp" />
			</div>
			<div id="tabs-3">
				<s:include value="_contractorFlagOperatorApproval.jsp" />
			</div>
		</div>
	</div>
</body>