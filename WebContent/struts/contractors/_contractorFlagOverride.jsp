<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<div id="${actionName}_${methodName}_page" class="${actionName}-page page">
	<s:if test="displayTable">
		<table class="report" style="clear: none;">
			<thead>
				<tr>
					<td>
						<s:text name="ContractorFlag.Flag" />
					</td>
					<s:if test="displayCorporate">
						<td>
							<s:text name="ContractorFlag.Operator" />
						</td>
					</s:if>
					<td>
						<s:text name="ContractorFlag.Description" />
					</td>
					<td>
						<s:text name="ContractorFlag.Value" />
					</td>
					<td>
						<s:text name="ContractorFlag.OverrideDetail" />
					</td>
				</tr>
			</thead>
			<tbody>
				<s:iterator id="key" value="flagDataMap.keySet()">
					<s:iterator id="data" value="flagDataMap.get(#key)">
						<s:if test="#data.flag.redAmber || isFlagDataOverride(#data, #data.operator)">
							<s:set name="flagoverride" value="%{isFlagDataOverride(#data, #data.operator)}" />
	
							<tr class="<s:property value="#data.flag" />">
								<td class="center"
									<pics:permission perm="EditForcedFlags">
										<s:if test="permittedToForceFlags" >rowspan="2"</s:if>
									</pics:permission>>
									<s:property value="#data.flag.smallIcon" escape="false" />
								</td>
	
								<s:if test="displayCorporate">
									<td>
										<s:property value="#data.operator.name" />
									</td>
								</s:if>
	
								<!-- Description -->
								<td>
									<s:set name="opCriteria" value="getApplicableOperatorCriteria(#data)" /> 
										<s:if test="#opCriteria != null">
										<s:if test="#data.criteria.oshaType != null || (#data.criteria.question != null && #data.criteria.question.id == 2034)">
											<s:property value="#opCriteria.replaceHurdle" />
											<a
												href="javascript:;"
												class="help cluetip"
												rel="#cluetip<s:property value="#opCriteria.id" />"
												title="<s:text name="ContractorFlag.Statistics" />">
												<div id="cluetip<s:property value="#opCriteria.id" />">
													<s:text name="ContractorFlag.StatusNotPending" />
												</div>
											</a>
										</s:if>
										<s:else>
											<s:property value="#opCriteria.replaceHurdle" />
										</s:else>
									</s:if>
								</td>
	
								<!-- Value -->
								<td>
									<s:if test="#data.criteria.auditType != null && !#data.criteria.auditType.scoreable">
										<s:iterator value="missingAudits.get(#data.criteria.auditType)" var="cao">
											<s:text name="ContractorFlag.ProblemValue">
												<s:param>
													<s:text name="%{status.i18nKey}" />
												</s:param>
												<s:param>
													<a href="Audit.action?auditID=<s:property value="#cao.audit.id" />">
														<s:property value="#data.criteria.auditType.name" />
														<s:if test="#cao.audit.auditType.annualAddendum">
															 <s:property value="#cao.audit.auditFor" />
														</s:if>
														<s:elseif test="!#cao.audit.auditType.classType.isPqf()">
															 '<s:date name="#cao.audit.effectiveDateLabel" format="%{@com.picsauditing.util.PicsDateFormat@TwoDigitYear}" />
														</s:elseif>
														<s:else>
															 <s:property value="#cao.audit.auditFor" />
															 '<s:date name="#cao.audit.effectiveDateLabel" format="%{@com.picsauditing.util.PicsDateFormat@TwoDigitYear}" />
														</s:else>
													</a>
												</s:param>
											</s:text>
											<br />
										</s:iterator>
	
										<s:if test="#data.criteria.auditType.classType.policy">
											<s:set var="cao" value="missingAudits.get(#data.criteria.auditType)" />
	
											<p class="insurance-limits">
												<s:if test="#cao.flag != null">
													<s:text name="ContractorFlag.Recommend" />
													<s:property value="#cao.flag" />
													<br />
												</s:if>
	
												<s:iterator var="insuranceFlagData" value="co.flagDatas">
													<s:if test="#insuranceFlagData.criteria.insurance && #insuranceFlagData.criteria.question.auditType == #data.criteria.auditType">
														<s:iterator id="conCriteria" value="contractor.flagCriteria">
															<s:if test="#insuranceFlagData.criteria.id == #conCriteria.criteria.id">
																<s:property value="getContractorAnswer(#conCriteria, #insuranceFlagData, true)" escape="false" />
																<br />
															</s:if>
														</s:iterator>
													</s:if>
												</s:iterator>
											</p>
										</s:if>
									</s:if> <s:else>
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
										<a
											href="javascript:;"
											class="cluetip help"
											rel="#cluetip<s:property value="#flagoverride.id"/>"
											title="<s:text name="ContractorFlag.Location"></s:text>">
											<s:text name="ContractorFlag.ManualForceFlag" />
										</a>
										<div id="cluetip<s:property value="#flagoverride.id"/>">
											<span title='<s:property value="getTextParameterized('ContractorFlag.ByTitle', #flagoverride.updatedBy.name, #flagoverride.updatedBy.account.name, #flagoverride.updatedBy.account.corporate)" />'>
												<s:text name="ContractorFlag.ForceFlagTo" />
												<s:property value="#flagoverride.forceFlag.smallIcon" escape="false" />
												<s:text name="ContractorFlag.Until" />
												<s:date name="#flagoverride.forceEnd" format="%{@com.picsauditing.util.PicsDateFormat@IsoLongMonth}" />
												<s:url var="contractor_notes" action="ContractorNotes">
													<s:param name="id" value="%{contractor.id}" />
													<s:param name="filter.userID" value="%{#flagoverride.updatedBy.id}" />
													<s:param name="filter.category" value="Flags" />
													<s:param name="filter.keyword" value="Forced" />
												</s:url>
												<a href="${contractor_notes}">
													<s:text name="ContractorFlag.SearchNotes" />
												</a>
											</span>
										</div>
									</s:if>
								</td>
							</tr>
							
							<pics:permission perm="EditForcedFlags">
								<s:if test="permittedToForceFlags" >
									<tr id="<s:property value="%{#data.id}" />_override"
										class="_override_ clickable">
										<td <s:if test="displayCorporate">colspan="4"</s:if><s:else>colspan="3"</s:else>>
											<div class="override_form">
												<form enctype="multipart/form-data" method="POST" id="individual_flag_override_form">
													<s:hidden value="%{#data.id}" name="dataID" id="dataID" />
	
													<s:if test="canForceDataFlag(#flagoverride)">
														<s:if test="#flagoverride!=null">
															<s:text name="ContractorFlag.SiteOverride">
																<s:param>
																	<s:property value="#flagoverride.createdBy.name" />
																</s:param>
															</s:text>
														</s:if>
														<s:else>
															<fieldset class="form">
																<ol>
																	<li>
																		<label>
																			<s:text name="ContractorFlag.ForceFlagTo" />
																		</label>
																		<s:radio
																			id="flag_%{#data.id}"
																			list="getUnusedFlagColors(#data.id)"
																			name="forceFlag"
																			theme="pics"
																			cssClass="inline"
																		/>
																	</li>
																	<li>
																		<label>
																			<s:text name="ContractorFlag.Until" />
																		</label>
																		<input
																			id="forceEnd_<s:property value="%{#data.id}" />"
																			name="forceEnd"
																			size="10"
																			type="text"
																			class="datepicker" />
																	</li>
																	<li class="required">
																		<label>
																			<s:text name="ContractorFlag.Reason" />:
																		</label>
																		<s:textarea
																			name="forceNote"
																			value=""
																			rows="2"
																			cols="30"
																			cssStyle="vertical-align: top;"></s:textarea>
																		<div class="fieldhelp">
																			<h3>
																				<s:text name="ContractorFlag.Reason" />
																			</h3>
																			<s:text name="ContractorFlag.AllFieldsRequired" />
																		</div>
																	</li>
																	<li>
																		<span class="label-txt">
																			<s:text name="ContractorFlag.FileAttachment" />
																		</span>
																		<s:file name="file" id="%{#data.id}_file"></s:file>
																	</li>
																	<li>
																		<s:if test="permissions.corporate && !displayCorporate">
																			<s:checkbox id="overRAll_%{#data.id}" name="overrideAll" />
																			<label for="overRAll_<s:property value="%{#data.id}"/>">
																				<s:text name="ContractorFlag.OverrideForAllSites">
																					<s:param>
																						<s:property value="permissions.accountName" />
																					</s:param>
																				</s:text>
																			</label>
																		</s:if>
																	</li>
																	<li>
																		
																		<s:submit
																			type="button"
																			name="button"
																			method="forceIndividualFlag"
																			value="%{getText('ContractorFlag.ForceIndividualFlag')}"
																			cssClass="picsbutton positive check-reason"
																		/>
																	</li>
																</ol>
															</fieldset>
														</s:else>
													</s:if>
													<s:else>
														<s:if test="#flagoverride.operator.type == 'Corporate'">
															<s:hidden name="overrideAll" value="true" />
	
															<label>
																<s:text name="ContractorFlag.CancelOverrideInfo" />
															</label>
															<br />
														</s:if>
														&nbsp;
														<s:text name="ContractorFlag.ReasonCancelling" />
														<s:textarea
															name="forceNote"
															value=""
															rows="2"
															cols="15"
															cssStyle="vertical-align: top;">
														</s:textarea>
														<s:submit
															type="button"
															name="button"
															method="cancelDataOverride"
															value="%{getText('button.CancelDataOverride')}"
															cssClass="picsbutton positive check-reason"
														/>
													</s:else>
												</form>
	
												<span class="override_wrap override_hide">
													<a href="javascript:;" class="override_wrench">
														<s:text name="ContractorFlag.HideForceLine" />
													</a>
												</span>
											</div>
											<span class="override_wrap">
												<a href="javascript:;" class="override_wrench">
													<s:text name="ContractorFlag.ShowForceLine" />
												</a>
											</span>
										</td>
									</tr>
								</s:if>
							</pics:permission>
						</s:if>
					</s:iterator>
				</s:iterator>
			</tbody>
		</table>
	</s:if>
</div>