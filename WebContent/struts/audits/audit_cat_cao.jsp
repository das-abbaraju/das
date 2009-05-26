<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<div class="subCategory">
	<s:include value="../actionMessages.jsp"/>
	<s:iterator value="conAudit.operators" status="stat">
		<div id="cao_<s:property value="id"/>">
			<s:if test="isVisibleTo(permissions)">
				<h3><s:property value="operator.name" /></h3>
				<div class="question shaded" id="status_<s:property value="id"/>">
					<label class="policy">Status:</label>
					<s:property value="status" />
					<s:if test="flag != null">
						<label class="policy">Meets Criteria:</label>
						<s:property value="flag.smallIcon" escape="false"/>
						<s:property value="flag" />
					</s:if> <!--<br /><label class="policy">Notes:</label> Everything is correct but the additional named insured
					<br /><label class="policy">Contractor Remarks:</label> I'm just submitting this for a bid right now.
							-->
				</div>
				<div class="question" id="fileQuestion<s:property value="operator.id"/>">
					<span class="question">1.3.1&nbsp;&nbsp;
						Upload a Certificate of Insurance or other supporting documentation
						for this policy.
					</span>
					<div class="answer">
						<s:if test="certificate != null">
							<a href="CertificateUpload.action?id=<s:property value="audit.contractorAccount.id"/>&certID=<s:property value="certificate.id"/>&button=download"
								target="_BLANK" class="insurance"><span></span>View File</a>
							&nbsp;&nbsp;&nbsp;&nbsp;
							<a href="#" onclick="if (confirm('Are you sure you want to remove this certificate?')) saveCao( 'certificate', null, <s:property value="id"/>,'fileQuestion<s:property value="operator.id"/>'); return false;" class="remove">Remove</a>
						</s:if> 
						<s:else>
								No File Uploaded
						</s:else> 
						<br />
						<a href="#" onclick="$('choose_certs<s:property value="id"/>').toggle(); return false;">Attach File</a>
						<table class="report" style="display:none;width:320px;" id="choose_certs<s:property value="id"/>">
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
												onclick="saveCao('certificate.id', <s:property value="id"/>, <s:property value="[1].id"/>, 'choose_certs<s:property value="id"/>'); return false;"
												target="_BLANK">
												<span></span><s:property value="description" />
											</a>
										</td>
										<td><s:date name="creationDate" format="M/d/yy" /></td>
									</tr>
								</s:iterator>
								<tr>
									<td colspan="3" class="center"><a href="#" class="add" onclick="showCertUpload(<s:property value="conAudit.contractorAccount.id" />, 0); return false;" title="Opens in new window (please disable your popup blocker)">Upload New Certificate</a></td>
								</tr>
							</tbody>
						</table>
					</div>
					<br clear="all"/>
				</div>
				<div class="question shaded" id="aiNameValid<s:property value="id"/>">
					<span class="question">1.3.2&nbsp;&nbsp;
						Does the additional insured listed in the above certificate match the 
						name (and address) requirements listed in the 
						<a <s:if test="operator.insuranceForm != null">
								href="forms/<s:property value="operator.insuranceForm.file"/>" 
							</s:if>
							<s:else>
								href="ContractorForms.action?id=<s:property value="audit.contractorAccount.id"/>"
							</s:else>
							target="_BLANK">
								<s:property value="operator.name"/> Insurance Requirements
							</a> document exactly?
					</span>
					<div class="answer">
						<s:checkbox name="aiNameValid" onchange="saveCao(this.name, this.checked, %{id}, 'aiNameValid%{id}')"></s:checkbox>
					</div>
					<br clear="all"/>
				</div>
				<s:if test="!aiNameValid">
					<div id="aiName<s:property value="id"/>" class="question subquestion">
						<span class="question">1.3.3&nbsp;&nbsp;
							Enter the name listed on the certificate:
						</span>
						<div class="answer"><s:textfield name="aiName" size="50" onchange="saveCao(this.name, this.value, %{id}, 'aiName%{id}')"/></div>
						<br clear="all"/>
					</div>
				</s:if>
				<s:if test="permissions.contractor">
					<div class="buttons">
						<input type="button" class="picsbutton positive" 
							<s:if test="status.rejected">
								value="Resubmit"
							</s:if>
							<s:else>
								value="Submit" 
							</s:else>
							onclick="saveCaoStatus(<s:property value="id"/>,this.value, false); return false;"
						/>
					</div>
					<br clear="all"/>
					<label class="policy">Remarks:</label><br/>
					<s:textarea name="remarks" cols="60" rows="2"/>
				</s:if> 
				<s:if test="permissions.admin">
					<div class="buttons">
						<input type="button" class="picsbutton positive" value="Verify" onclick="saveCaoStatus(<s:property value="id"/>,this.value, true); return false;"/>
						<input type="button" class="picsbutton negative" value="Reject" onclick="saveCaoStatus(<s:property value="id"/>,this.value, true); return false;"/>
					</div>
					<br clear="all"/>
					<label class="policy">Notes:</label><br/>
					<s:textarea name="notes" cols="60" rows="2" id="notes_%{id}"/>
				</s:if> 
				<s:if test="permissions.operatorCorporate">
					<div class="buttons">
						<input type="button" class="picsbutton positive" value="Approve" onclick="saveCaoStatus(<s:property value="id"/>,this.value, true); return false;"/>
						<input type="button" class="picsbutton negative" value="Reject" onclick="saveCaoStatus(<s:property value="id"/>,this.value, true); return false;"/>
						<input type="button" class="picsbutton" value="Not Applicable" onclick="saveCao('status', 'NotApplicable', <s:property value="id"/>, 'status_<s:property value="id"/>'); return false;"/>
					</div>
					<br clear="all"/>
					<label class="policy">Notes:</label><br/>
					<s:textarea name="notes" cols="60" rows="2" id="notes_%{id}"/>
				</s:if>
				<br clear="all"/>
				<br />
			</s:if>
		</div>
	</s:iterator>
</div>
