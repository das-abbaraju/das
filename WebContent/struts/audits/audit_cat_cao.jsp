<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:include value="../actionMessages.jsp"/>
<s:form id="cao_form%{#cao.id}">
	<s:hidden name="auditID" value="%{#cao.audit.id}"/>
	<s:hidden name="cao.id" value="%{#cao.id}"/>
	<s:if test="#cao.isVisibleTo(permissions)">
		<h3><s:property value="#cao.operator.name" /></h3>
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
			<span class="question">1.3.1&nbsp;&nbsp;
				Upload a Certificate of Insurance or other supporting documentation
				for this policy.
			</span>
			<div class="answer">
				<s:if test="#cao.certificate != null">
					<a href="CertificateUpload.action?id=<s:property value="#cao.audit.contractorAccount.id"/>&certID=<s:property value="#cao.certificate.id"/>&button=download"
						target="_BLANK" class="insurance"><span></span>View File</a>
					&nbsp;&nbsp;&nbsp;&nbsp;
					<a href="#" onclick="if (confirm('Are you sure you want to remove this certificate?')) saveCao('certificate', null, <s:property value="#cao.id"/>,'fileQuestion<s:property value="#cao.operator.id"/>'); return false;" class="remove">Remove</a>
				</s:if> 
				<s:else>
						No File Uploaded
				</s:else> 
				<br />
				<a href="#" onclick="$('choose_certs<s:property value="#cao.id"/>').toggle(); return false;">Attach File</a>
				<table class="report" style="display:none;width:320px;" id="choose_certs<s:property value="#cao.id"/>">
					<thead>
						<tr>
							<td>Certificate</td>
							<td>Uploaded</td>
						</tr>
					</thead>
					<tbody>
						<s:iterator value="certificates">
							<tr>
								<td>
									<a class="insurance"
										href="#"
										onclick="saveCao('certificate.id', <s:property value="id"/>, <s:property value="#cao.id"/>, 'fileQuestion<s:property value="#cao.id"/>'); return false;"
										target="_BLANK">
										<span></span><s:property value="description" />
									</a>
								</td>
								<td><s:date name="creationDate" format="M/d/yy" /></td>
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
				<input type="checkbox" name="cao.valid" <s:if test="#cao.valid">checked</s:if> />
			</div>
			<br clear="all"/>
		</div>
		<div class="question">
			<span class="question"> 1.3.3 &nbsp;&nbsp;
			Contractor Remarks:
			</span>
			<div class="answer">
				<s:textarea name="cao.reason" value="%{#cao.reason}" cols="60" rows="3"/>
			</div>
			<br clear="all"/>
		</div>
		<s:if test="permissions.contractor">
			<div class="buttons">
				<input type="button" class="picsbutton positive" 
					<s:if test="status.rejected">
						value="Resubmit"
					</s:if>
					<s:else>
						value="Submit" 
					</s:else>
					onclick="return false;"
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
		<br clear="all"/>
		<br />
	</s:if>
</s:form>
