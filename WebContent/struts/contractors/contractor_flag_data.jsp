<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<script type="text/javascript">
$(function() {
	$('.datepicker').datepicker();
})
</script>

<table class="report" style="clear: none;">
	<thead>
		<tr>
			<td>Flag</td>
			<td>Description</td>
			<td>Value</td>
		</tr>
	</thead>
	<s:iterator id="data" value="flagData">
		<s:if test="#data.flag.toString() == 'Red' || #data.flag.toString() == 'Amber' || isFlagDataOverride(#data)">
		<s:set name="flagoverride" value="%{isFlagDataOverride(#data)}"/>
			<tr class="<s:property value="#data.flag" />">
				<td>
					<s:property value="#data.flag.smallIcon" escape="false" />
					<s:if test="opID == permissions.getAccountId() || permissions.corporate">	
						<s:if test="#flagoverride != null">
							Manual Force Flag <s:property value="#flagoverride.forceflag.smallIcon" escape="false" /> until <s:date name="#flagoverride.forceEnd" format="MMM d, yyyy" />
							<br/>
							<pics:permission perm="EditForcedFlags">
								<s:if test="permissions.corporate">
									<s:checkbox name="overrideAll"/><label>Check to Cancel the Force Flag Color at all your Facilities in your database</label><br/>
								</s:if>
								<div>
									<button class="picsbutton positive" name="button" value="Cancel Data Override"
										onclick="checkSubmit(this.value, <s:property value="%{#data.id}" />);">Cancel Data Override</button>
								</div>
								<br />
							</pics:permission>
						</s:if>
						<s:else>
							<pics:permission perm="EditForcedFlags">
								<div id="override_flagdata_<s:property value="%{#data.id}" />" style="display: none">
									<s:select list="flagList" name="forceFlag" />
									until 
									<input id="forceEnd1_<s:property value="%{#data.id}" />" name="forceEnd" size="8" type="text" class="datepicker" onclick="$(this).datepicker();" />
									<br/>
									<s:if test="permissions.corporate">
										<s:checkbox name="overrideAll"/><label>Check to Force the Flag Color for all your Facilities in your database</label><br/>
									</s:if>
									<div>
										<button class="picsbutton positive" type="submit" name="button" value="Force Data Override"
											onclick="checkSubmit(this.value, <s:property value="%{#data.id}" />);">Force Data Override</button>
									</div>
									<a href="#" onclick="$('#override_link_flagdata_<s:property value="%{#data.id}" />').show(); $('#override_flagdata_<s:property value="%{#data.id}" />').hide(); return false;">Nevermind</a>
								</div>
								<a id="override_link_flagdata_<s:property value="%{#data.id}" />" href="#" 
									onclick="$('#override_flagdata_<s:property value="%{#data.id}" />').show(); $('#override_link_flagdata_<s:property value="%{#data.id}" />').hide(); return false;">Manually Force Flag Color</a>
							</pics:permission>
						</s:else>
					</s:if>
					<s:else>
						<s:if test="#flagoverride != null">
							Manual Force Flag <s:property value="#flagoverride.forceFlag.smallIcon" escape="false" /> until <s:date name="#flagoverride.forceEnd" format="MMM d, yyyy" />
						</s:if>
					</s:else>
				</td>
				<td>
					<s:iterator id="opCriteria" value="co.operatorAccount.flagCriteriaInherited">
						<s:if test="#opCriteria.criteria == #data.criteria && #opCriteria.flag == #data.flag">
							<s:property value="#opCriteria.replaceHurdle"/>
						</s:if>
					</s:iterator>
				</td>
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
							<s:if test="#data.criteria == #conCriteria.criteria">
								<s:if test="#data.criteria.dataType == 'number'">
									<s:property value="format(#conCriteria.answer)" />
								</s:if>
								<s:else>
									<s:property value="#conCriteria.answer" />
								</s:else>
								<s:if test="#conCriteria.answer2.length() > 0">
									<br /><s:property value="#data.answer2" escape="true"/>
								</s:if>
							</s:if>
						</s:iterator>
					</s:else>
				</td>
			</tr>
		</s:if>
	</s:iterator>
		<tr><td colspan="3" class="center">
			<pics:permission perm="ManageOperators">
				To Edit the Criteria <br />
				[<a href="AuditOperator.action?oID=<s:property value="co.operatorAccount.inheritAudits.id" />">For Audits</a>]
				[<a href="AuditOperator.action?oID=<s:property value="co.operatorAccount.inheritInsurance.id" />">For Policies</a>]
			
			</pics:permission>
			<pics:permission perm="EditFlagCriteria">
				[<a 
				href="ManageFlagCriteriaOperator.action?id=<s:property value="co.operatorAccount.inheritFlagCriteria.id" />">For Flag Criteria</a>]		
			</pics:permission>
		</td></tr>
</table>

<div style="width: 66%;">
	<s:if test="auditCriteria.size() > 0">
		<div class="flaggedCriteria"><table class="report">
			<thead>
				<tr>
					<td>Flag</td>
					<td>Audits</td>
				</tr>
			</thead>
			<s:iterator id="data" value="auditCriteria">
				<s:set name="flagoverride" value="%{isFlagDataOverride(#data)}" />
				<tr>
					<td class="center">
						<s:if test="#flagoverride != null">
							<s:property value="#flagoverride.forceflag.smallIcon" escape="false"/>
						</s:if>
						<s:else>
							<s:property value="flag.smallIcon" escape="false"/>
						</s:else>
					</td>
					<td><s:property value="criteria.auditType.auditName" /></td>			
				</tr>
			</s:iterator>
		</table></div>
	</s:if>
	<s:if test="safetyCriteria.size() > 0">
		<div class="flaggedCriteria"><table class="report">
			<thead>
				<tr>
					<td>Flag</td>
					<td>Safety</td>
				</tr>
			</thead>
			<s:iterator id="data" value="safetyCriteria">
				<s:set name="flagoverride" value="%{isFlagDataOverride(#data)}" />
					<tr>
						<td class="center">
							<s:if test="#flagoverride != null">
								<s:property value="#flagoverride.forceflag.smallIcon" escape="false"/>
							</s:if>
							<s:else>
								<s:property value="flag.smallIcon" escape="false"/>
							</s:else>
						</td>
						<td>
							<s:iterator id="conCriteria" value="contractor.flagCriteria">					
								<s:if test="#data.criteria == #conCriteria.criteria">
								<s:set name="conVerified" value="#conCriteria.verified"></s:set>
									<s:if test="criteria.dataType == 'number'">
										<s:property value="format(#conCriteria.answer)" />
									</s:if>
									<s:else>
										<s:property value="#conCriteria.answer" />
									</s:else>
								</s:if>	
							</s:iterator>
							- <s:property value="criteria.label" />
							<s:if test="#conCriteria.answer2.length() > 0">
								<br /><s:property value="#data.answer2" escape="true"/>
							</s:if>
						</td>			
					</tr>
			</s:iterator>
		</table></div>
	</s:if>
	<s:if test="insuranceCriteria.size() > 0">
		<div class="flaggedCriteria"><table class="report">
			<thead>
				<tr>
					<td>Flag</td>
					<td>Insurance</td>
				</tr>
			</thead>
			<s:iterator id="data" value="insuranceCriteria">
				<s:set name="flagoverride" value="%{isFlagDataOverride(#data)}" />
				<tr>
					<td class="center">
						<s:if test="#flagoverride != null">
							<s:property value="#flagoverride.forceflag.smallIcon" escape="false"/>
						</s:if>
						<s:else>
							<s:property value="flag.smallIcon" escape="false"/>
						</s:else>
					</td>
					<td><s:property value="criteria.auditType.auditName" /></td>			
				</tr>
			</s:iterator>
		</table></div>
	</s:if>
</div>