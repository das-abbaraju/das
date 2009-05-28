<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:form id="cao_form%{#cao.id}">
	<s:hidden name="auditID" value="%{#cao.audit.id}"/>
	<s:hidden name="cao.id" value="%{#cao.id}"/>
	<s:set name="required" value="!#cao.status.approved && !#cao.status.notApplicable"/>
	<s:if test="!#cao.status.pending">
		<div class="auditHeader" id="auditHeader<s:property value="#cao.id"/>">
			<fieldset>
			<ul>
				<li><label>Op Status:</label>
					<span 
						<s:if test="!permissions.contractor">
							style="cursor: pointer;" onclick="$('cao_form<s:property value="#cao.id"/>_cao_status').show(); this.hide();"
						</s:if>
						>
						<s:property value="#cao.status" />
					</span>
					<s:if test="!permissions.contractor">
						<s:select name="cao.status" value="#cao.status" cssStyle="display: none;" onchange="saveCao('cao_form%{#cao.id}', 'Save')"
							 list="@com.picsauditing.jpa.entities.CaoStatus@values()" ></s:select>
					</s:if>   
				</li>
					<li><label>Changed By:</label>
						<s:property value="#cao.statusChangedBy.name" /> from <s:property value="#cao.statusChangedBy.account.name" />
					</li>
					<li><label>Changed On:</label>
						<s:date name="#cao.statusChangedDate" />
					</li>
					<s:if test="#cao.flag != null">
						<li><label>Meets Criteria:</label>
							<s:property value="#cao.flag.smallIcon" escape="false"/>
							<s:property value="#cao.flag" />
						</li>
					</s:if>
			</ul>
			</fieldset>
			<fieldset>
			<ul>
				<s:if test="#cao.notes != null && #cao.notes.length() > 0">
					<li><label>Administrative Notes: </label>
							<s:property value="#cao.notes"/>
					</li>
				</s:if>

				<s:if test="!permissions.contractor">
					<s:if test="#cao.reason != null && #cao.reason.length() > 0">
						<li><label>Contractor Remarks: </label>
								<s:property value="#cao.reason"/>
						</li>
					</s:if>
				</s:if>
			</ul>
			</fieldset>
			<div class="clear"></div>
		</div>
	</s:if>
	<div class="question" id="fileQuestion<s:property value="#cao.id"/>">
		<span class="question<s:if test="#required && #cao.certificate == null"> required</s:if>">1.3.1&nbsp;&nbsp;
			Upload a Certificate of Insurance or other supporting documentation
			for this policy.
		</span>
		<div class="answer">
			<s:hidden name="certificate.id" value="%{#cao.certificate != null ? #cao.certificate.id : 0}"/>
			<s:if test="#cao.certificate != null">
				<s:date name="#cao.certificate.creationDate" format="M/d/yy" /> - <s:property value="#cao.certificate.description" /> <br/>
				<a href="CertificateUpload.action?id=<s:property value="#cao.audit.contractorAccount.id"/>&certID=<s:property value="#cao.certificate.id"/>&button=download"
					target="_BLANK" class="insurance">
					<span></span>
					View
				</a>
				<s:if test="!#cao.status.approved">
					&nbsp;&nbsp;&nbsp;&nbsp;
					<a href="#" 
					onclick="if (confirm('Are you sure you want to detach this certificate?')) saveCert(0,<s:property value="#cao.id"/>); return false;" 
					class="remove">
						Detach
					</a>
				</s:if>
			</s:if> 
			<s:else>
					No File Attached
			</s:else> 
			<br />
			<s:if test="!#cao.status.approved">
				<a href="#" onclick="showCertificates(<s:property value="contractor.id"/>,<s:property value="#cao.id"/>); return false;">Attach File</a>
			</s:if>
		</div>
		<div id="certificates<s:property value="#cao.id"/>" class="left"></div>
		<br clear="all"/>
		<div class="clear"></div>
	</div>
	<div class="question shaded" id="caoValid<s:property value="#cao.id"/>">
		<span class="question<s:if test="#required && #cao.valid == null"> required</s:if>">1.3.2&nbsp;&nbsp;
		
			This insurance policy complies with all additional <s:property value="#cao.operator.name"/> requirements.
			<s:iterator value="#cao.operator.audits">
				<s:if test="#cao.audit.auditType == auditType && help != null && help.length() > 0">
					<s:property value="help"/>
				</s:if>
			</s:iterator>
			If it does NOT comply, please explain below.
			
			<s:if test="#cao.operator.insuranceForms.size > 0">
				<ul style="list-style:none">
					<s:iterator value="#cao.operator.insuranceForms">
						<li><a href="forms/<s:property value="file"/>" target="_BLANK" title="Opens in new Window"><s:property value="formName"/></a></li>
					</s:iterator>
				</ul>
			</s:if>
			
		</span>
		<div class="answer">
			<s:if test="!#cao.status.approved">
				<s:radio list="#{'Yes':'Yes', 'No':'No'}" name="cao.valid" value="%{#cao.valid}" onclick="saveCao('cao_form%{#cao.id}', 'Save', 'caoValid%{#cao.id}')"/>
			</s:if>
			<s:else>
				<s:property value="#cao.valid"/>
			</s:else>
		</div>
		<br clear="all"/>
		<div class="clear"></div>
	</div>
	<s:if test="permissions.contractor">
		<div class="question" id="remarks<s:property value="#cao.id"/>">
			<span class="question<s:if test="#required && !#cao.valid.isTrue() && #cao.reason == null"> required</s:if>">
			1.3.3&nbsp;&nbsp;Contractor Remarks:
			</span>
			<div class="answer">
				<s:textarea name="cao.reason" value="%{#cao.reason}" cols="60" rows="3"
					onchange="saveCao('cao_form%{#cao.id}', 'Save', 'remarks%{#cao.id}')" />
			</div>
			<br clear="all"/>
			<div class="clear"></div>
		</div>
	</s:if>
	<s:if test="permissions.contractor">
		<s:if test="!#cao.status.submitted && !#cao.status.approved">
			<div class="buttons">
				<input type="button" class="picsbutton positive" 
					<s:if test="!#cao.status.pending">
						value="Resubmit"
					</s:if>
					<s:else>
						value="Submit" 
					</s:else>
					
					<s:if test="!#cao.canContractorSubmit">
						disabled
					</s:if>
					onclick="saveCao('cao_form<s:property value="#cao.id"/>', this.value);return false;"
				/>
				<br clear="all"/>
			</div>
		</s:if>
	</s:if> 
	<s:if test="permissions.admin">
		<s:if test="#cao.status.pending || #cao.status.submitted">
			<div class="buttons">
				<input type="button" name="button" class="picsbutton positive" value="Verify" onclick="saveCao('cao_form<s:property value="#cao.id"/>', this.value);return false;"/>
				<input type="button" name="button" class="picsbutton negative" value="Reject" onclick="saveCao('cao_form<s:property value="#cao.id"/>', this.value);return false;"/>
			</div>
			<br clear="all"/>
			<label class="policy">Administrative Notes:</label><br/>
			<s:textarea name="cao.notes" value="%{#cao.notes}" cols="60" rows="3"/>
		</s:if>
	</s:if> 
	<s:if test="permissions.operatorCorporate">
		<s:if test="#cao.status.submitted || #cao.status.verified">
			<div class="buttons">
				<input type="button" class="picsbutton positive" value="Approve" onclick="saveCao('cao_form<s:property value="#cao.id"/>', this.value);return false;"/>
				<input type="button" class="picsbutton negative" value="Reject" onclick="saveCao('cao_form<s:property value="#cao.id"/>', this.value);return false;"/>
				<input type="button" class="picsbutton" value="Not Applicable" onclick="saveCao('cao_form<s:property value="#cao.id"/>', 'NotApplicable');return false;"/>
			</div>
			<br clear="all"/>
			<label class="policy">Administrative Notes:</label><br/>
			<s:textarea name="cao.notes" value="%{#cao.notes}" cols="60" rows="3"/>
		</s:if>
	</s:if>
	<s:include value="../actionMessages.jsp"/>
</s:form>