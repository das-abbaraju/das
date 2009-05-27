<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:form id="cao_form%{#cao.id}">
	<s:hidden name="auditID" value="%{#cao.audit.id}"/>
	<s:hidden name="cao.id" value="%{#cao.id}"/>
	<s:if test="!#cao.status.pending">
		<div class="auditHeader">
			<fieldset>
			<ul>
				<li><label>Op Status:</label>
					<span style="cursor: pointer"><s:property value="#cao.status" /></span>
				</li>
					<li><label>By:</label>
						<s:property value="#cao.statusChangedBy.name" /> from <s:property value="#cao.statusChangedBy.account.name" />
					</li>
					<li><label>On:</label>
						<s:property value="#cao.statusChangedDate" />
					</li>
					<s:if test="#cao.flag != null">
						<li><label>Meets Criteria:</label>
							<s:property value="#cao.flag.smallIcon" escape="false"/>
							<s:property value="#cao.flag" />
						</li>
					</s:if>
			</ul>
			</fieldset>
			<div class="clear"></div>
		</div>
	</s:if>
	<div class="question shaded" id="status_<s:property value="#cao.id"/>">
		<label class="policy">Status:</label>
		<s:property value="#cao.status" />
		<s:if test="#cao.flag != null">
			<label class="policy">Meets Criteria:</label>
			<s:property value="#cao.flag.smallIcon" escape="false"/>
			<s:property value="#cao.flag" />
		</s:if>
	</div>
	<div class="question" id="fileQuestion<s:property value="#cao.id"/>">
		<span class="question<s:if test="#cao.certificate == null"> required</s:if>">1.3.1&nbsp;&nbsp;
			Upload a Certificate of Insurance or other supporting documentation
			for this policy.
		</span>
		<div class="answer">
			<s:hidden name="certificate.id" value="%{#cao.certificate != null ? #cao.certificate.id : 0}"/>
			<s:if test="#cao.certificate != null">
				<a href="CertificateUpload.action?id=<s:property value="#cao.audit.contractorAccount.id"/>&certID=<s:property value="#cao.certificate.id"/>&button=download"
					target="_BLANK" class="insurance"><span></span>View File</a>
				&nbsp;&nbsp;&nbsp;&nbsp;
				<a href="#" onclick="if (confirm('Are you sure you want to detach this certificate?')) saveCert(0,'cao_form<s:property value="#cao.id"/>','fileQuestion<s:property value="#cao.id"/>'); return false;" class="remove">Remove</a>
			</s:if> 
			<s:else>
					No File Attached
			</s:else> 
			<br />
			<a href="#" onclick="$('choose_certs<s:property value="#cao.id"/>').toggle(); return false;">Attach File</a>
			<table class="report" style="display:none" id="choose_certs<s:property value="#cao.id"/>">
				<thead>
					<tr>
						<th>Uploaded</thd>
						<th>Certificate</th>
						<th>Operators</th>
					</tr>
				</thead>
				<tbody>
					<s:iterator value="certificates">
						<tr>
							<td><s:date name="creationDate" format="M/d/yy" /></td>
							<td>
								<a class="insurance"
									href="#"
									onclick="saveCert(<s:property value="id"/>,'cao_form<s:property value="#cao.id"/>','fileQuestion<s:property value="#cao.id"/>'); return false;"
									target="_BLANK">
									<span></span><s:property value="description" />
								</a>
							</td>
							<td>
								<table class="inner">
									<s:iterator value="caos">
										<s:if test="!permissions.operatorCorporate || !permissions.insuranceOperatorID == permissions.">
										<tr>
											<td style="font-size:10px"><nobr><s:property value="audit.auditType.auditName"/></nobr></td>
											<td style="font-size:10px"><nobr><s:property value="operator.name"/></td>
											<td style="font-size:10px"><nobr><s:date name="audit.expiresDate" format="M/d/yy"/></nobr></td>
										</tr>
										</s:if>
									</s:iterator>
								</table>
							</td>
						</tr>
					</s:iterator>
					<tr>
						<td colspan="3" class="center"><a href="#" class="add" onclick="showCertUpload(<s:property value="#cao.audit.contractorAccount.id" />, 0); return false;" title="Opens in new window (please disable your popup blocker)">Upload New Certificate</a></td>
					</tr>
				</tbody>
			</table>
		</div>
		<br clear="all"/>
	</div>
	<div class="question shaded" id="aiNameValid<s:property value="#cao.id"/>">
		<span class="question">1.3.2&nbsp;&nbsp;
			Does the additional insured listed in the above certificate match the 
			name (and address) requirements listed in the 
			<a <s:if test="#cao.operator.insuranceForm != null">
					href="forms/<s:property value="#cao.operator.insuranceForm.file"/>" 
				</s:if>
				<s:else>
					href="ContractorForms.action?id=<s:property value="#cao.audit.contractorAccount.id"/>"
				</s:else>
				target="_BLANK">
					<s:property value="#cao.operator.name"/> Insurance Requirements
				</a> document exactly?
		</span>
		<div class="answer">
			<input type="checkbox" name="cao.valid" <s:if test="#cao.valid">checked</s:if> onchange="saveCao('cao_form<s:property value="#cao.id"/>', 'Save', 'aiNameValid<s:property value="#cao.id"/>')" />
		</div>
		<br clear="all"/>
	</div>
	<div class="question">
		<span class="question<s:if test="!#cao.valid && #cao.reason == null"> required</s:if>">
		Contractor Remarks:
		</span>
		<div class="answer">
			<s:textarea name="cao.reason" value="%{#cao.reason}" cols="60" rows="3"/>
		</div>
		<br clear="all"/>
	</div>
	<s:set name="disabled" value="(!#cao.valid && #cao.reason == null) || #cao.certificate == null"/>
	<s:if test="permissions.contractor">
		<div class="buttons">
			<input type="button" class="picsbutton positive" 
				<s:if test="status.rejected">
					value="Resubmit"
				</s:if>
				<s:else>
					value="Submit" 
				</s:else>
				
				<s:if test="#disabled">
					disabled
				</s:if>
				onclick="saveCao('cao_form<s:property value="#cao.id"/>', this.value);return false;"
			/>
		</div>
	</s:if> 
	<s:if test="permissions.admin">
		<div class="buttons">
			<input type="button" class="picsbutton positive" value="Verify" onclick="saveCao('cao_form<s:property value="#cao.id"/>', this.value);return false;"/>
			<input type="button" class="picsbutton negative" value="Reject" onclick="saveCao('cao_form<s:property value="#cao.id"/>', this.value);return false;"/>
		</div>
		<br clear="all"/>
		<label class="policy">Notes:</label><br/>
		<s:textarea name="cao.notes" value="%{#cao.notes}" cols="60" rows="3"/>
	</s:if> 
	<s:if test="permissions.operatorCorporate">
		<div class="buttons">
			<input type="button" class="picsbutton positive" value="Approve" onclick="saveCao('cao_form<s:property value="#cao.id"/>', this.value);return false;"/>
			<input type="button" class="picsbutton negative" value="Reject" onclick="saveCao('cao_form<s:property value="#cao.id"/>', this.value);return false;"/>
			<input type="button" class="picsbutton" value="Not Applicable" onclick="saveCao('cao_form<s:property value="#cao.id"/>', 'NotApplicable');return false;"/>
		</div>
		<br clear="all"/>
		<label class="policy">Notes:</label><br/>
		<s:textarea name="cao.notes" value="%{#cao.notes}" cols="60" rows="3"/>
	</s:if>
	<s:include value="../actionMessages.jsp"/>
	<br clear="all"/>
</s:form>
