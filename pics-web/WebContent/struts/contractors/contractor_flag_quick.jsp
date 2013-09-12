<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<html>
	<head>
		<title>
			<s:text name="ContractorFlag.title">
				<s:param value="%{contractor.name}" />
			</s:text>
		</title>
		
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
	</head>
	<body>
		<div id="tabs-1">
			<!-- OVERRIDES -->
			<s:if test="displayTable">
				<table class="report" style="clear: none;">
					<thead>
						<tr>
							<td>
								<s:text name="global.Flag" />
							</td>
							<td>
								<s:text name="global.Description" />
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
													<s:property value="#opCriteria.replaceHurdle" />
													<a href="javascript:;" class="help cluetip" rel="#cluetip<s:property value="#opCriteria.id" />" title="Statistics"></a>
													<div id="cluetip<s:property value="#opCriteria.id" />">
														<s:text name="ContractorFlag.StatusNotPending" />
													</div>
												</s:if>
												<s:else>
													<s:property value="#opCriteria.replaceHurdle" />
												</s:else>
											</s:if>
										</td>

										<!-- flagoverride info -->
										<td>
											<s:if test="#flagoverride != null">
												<span style="display: none;" title="<s:property value="getTextParameterized('ContractorFlag.ByTitle', #flagoverride.updatedBy.name, #flagoverride.updatedBy.account.name, #flagoverride.updatedBy.account.corporate)" />">
													<s:text name="ContractorFlag.ManualForceFlagShort">
														<s:param>
															<s:property value="#flagoverride.forceFlag.smallIcon" escape="false" />
														</s:param>
														<s:param value="%{#flagoverride.forceEnd}" />
													</s:text>
													<a href='ContractorNotes.action?id=<s:property value="contractor.id"/>&amp;filter.userID=<s:property value="#flagoverride.updatedBy.id"/>&amp;filter.category=Flags&amp;filter.keyword=Forced'>View Notes</a>
												</span>
											</s:if>
										</td>
										
										<!-- Value -->
										<td>
											<s:if test="#data.criteria.auditType != null">
												<s:iterator value="missingAudits.get(#data.criteria.auditType)" var="cao">
													<s:property value="status"/>
													<a href="Audit.action?auditID=<s:property value="#cao.audit.id" />"><s:property value="#data.criteria.auditType.name" /> <s:property value="audit.auditFor" /></a>
													<br/>
												</s:iterator>
												
												<s:if test="#data.criteria.auditType.classType.policy">
													<s:set var="cao" value="missingAudits.get(#data.criteria.auditType)" />
													
													<p style="padding-left: 20px; font-size: x-small">
														<s:if test="#cao.flag != null">
															Recommend: <s:property value="#cao.flag"/>
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
												<a href="javascript:;" class="cluetip help" rel="#cluetip<s:property value="#flagoverride.id"/>" title="Location"></a>	
												<div id="cluetip<s:property value="#flagoverride.id"/>">
													<span title='By <s:property value="#flagoverride.updatedBy.name" /> from <s:property value="#flagoverride.updatedBy.account.name"/><s:if test="#flagoverride.updatedBy.account.corporate"> for all the sites</s:if>'>
														Manual Force Flag <s:property value="#flagoverride.forceFlag.smallIcon" escape="false" /> until <s:date name="#flagoverride.forceEnd" format="%{@com.picsauditing.util.PicsDateFormat@IsoLongMonth}" />
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
																			<s:radio
																				id="flag_%{#data.id}"
																				list="getUnusedFlagColors(#data.id)"
																				name="forceFlag"
																				theme="pics"
																				cssClass="inline"
																			/>
																		</li> 
																		<li> 
																			<label>Until:</label> 
																			<input id="forceEnd_<s:property value="%{#data.id}" />" name="forceEnd" size="8" type="text" class="datepicker" />
																		</li>
																		<li class="required">
																			<label>Reason for Forcing:</label>
																			<s:textarea name="forceNote" value="" rows="2" cols="30" cssStyle="vertical-align: top;"></s:textarea>
																			<pics:fieldhelp title="Reason">
						                     									<p class="redMain">* All Fields are required</p>									
																			</pics:fieldhelp>
																		</li>
																		<li>
																			<span class="label-txt">File Attachment:</span>
																			<s:file name="file" id="%{#data.id}_file"></s:file>
																		</li>
																		<li>
																			<button class="picsbutton positive" type="submit" name="button" value="Force Individual Flag" onclick="return checkReason(<s:property value="%{#data.id}" />);">
																				Force Individual Flag
																			</button>
																			
																			<s:if test="permissions.corporate">
																				<s:checkbox id="overRAll_%{#data.id}" name="overrideAll"/>
																				<label for="overRAll_<s:property value="%{#data.id}"/>">Override the Flag for all <s:property value="permissions.accountName" /> sites </label>
																				<br/>														
																			</s:if>
																		</li>
																	</ol>
																</fieldset>
															</s:else>
														</s:if>
														<s:else>
															<s:if test="#flagoverride.operator.type == 'Corporate'">
																<s:hidden name="overrideAll" value="true"/>
																<label>By Clicking on the Cancel Override the force flags at all your facilities will be removed.</label>
																<br/>
															</s:if>
															&nbsp;
															Reason for Cancelling: <s:textarea name="forceNote" value="" rows="2" cols="15" cssStyle="vertical-align: top;"></s:textarea>
															<input type="submit" value="Cancel Data Override" class="picsbutton positive" name="button" onclick="return checkReason(<s:property value="%{#data.id}" />);" />
														</s:else>
													</form>
													
													<span class="override_wrap override_hide">
														<a href="#" class="override_wrench override_table_link">Hide Force Line</a>
													</span>
												</div>
												
												<span class="override_wrap">
													<a href="#" class="override_wrench override_table_link">Show Force Line</a>
												</span>
											</td>
										</tr>
									</pics:permission>
								</s:if>
							</s:iterator>
						</s:iterator>
					</tbody>
				</table>
			</s:if>
			<s:else>
				<s:text name="ContractorFlag.ContractorCompletedAllRequirements" />
			</s:else>
		</div>
	
		<div id="caldiv1" style="position:absolute; visibility:hidden; background-color:white; layer-background-color:white;"></div>
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
	</body>
</html>