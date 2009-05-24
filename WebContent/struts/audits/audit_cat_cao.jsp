<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<div class="subCategory">
	<s:iterator value="conAudit.operators" status="stat">
		<s:if test="visible">
			<h3><s:property value="operator.name" /></h3>
			<div class="question shaded">
				<label class="policy">Status:</label>
				<s:property value="status" />
				<s:if test="flagColor != null">
					<label class="policy">Meets Criteria:</label>
					<s:property value="flagColor.smallIcon" />
					<s:property value="flagColor" />
				</s:if> <!--<br /><label class="policy">Notes:</label> Everything is correct but the additional named insured
				<br /><label class="policy">Contractor Remarks:</label> I'm just submitting this for a bid right now.
						-->
			</div>
			<div class="question" id="fileQuestion<s:property value="operator.id"/>">
				<span class="question">1.3.1&nbsp;&nbsp;
					Upload a Certificate of Insurance or other supporting documentation
					for this policy. If you selected "All" above, please make sure the
					Certificate does not list any Operators by name.
				</span>
				<div class="answer">
					<s:if test="certificate != null">
						<a href="CertificateUpload.action?id=<s:property value="audit.contractorAccount.id"/>&certID=<s:property value="certificate.id"/>&button=download"
							target="_BLANK" class="insurance"><span></span>View File</a>
						&nbsp;&nbsp;&nbsp;&nbsp;
						<a href="#" onclick="if (confirm('Are you sure you want to remove this certificate?')) saveCao(<s:property value="operator.id"/>, <s:property value="certificate.id"/>, 'fileQuestion<s:property value="operator.id"/>', 'Remove'); return false;" class="remove">Remove</a>
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
									<td><a class="insurance"
										href="#"
										onclick="saveCao(<s:property value="operator.id"/>, <s:property value="id"/>, 'fileQuestion<s:property value="operator.id"/>', 'Attach'); return false;"
										target="_BLANK"><span></span><s:property value="description" /></a></td>
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
					Is the additional insured listed on the above certificate
					EXACTLY <strong><s:property value="operator.name"/></strong>?
				</span>
				<div class="answer">
					<s:checkbox name="aiNameValid" onchange="saveCao(this.name, this.checked, %{id}, 'aiNameValid%{id}')"></s:checkbox>
				</div>
				<br clear="all"/>
			</div>
			<div id="aiName<s:property value="id"/>" class="question subquestion" <s:if test="aiNameValid">style="display:none"</s:if>>
				<span class="question">1.3.3&nbsp;&nbsp;
					Enter the name listed on the certificate:
				</span>
				<div class="answer"><s:textfield name="aiName"/></div>
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
						/>
					</div>
					<br clear="all"/>
					<label class="policy">Remarks:</label><br/>
					<s:textarea name="remarks" cols="60" rows="2"/>
				</s:if> 
				<s:if test="permissions.admin">
					<div class="buttons">
						<input type="button" class="picsbutton positive" value="Verify" />
						<input type="button" class="picsbutton negative" value="Reject" />
					</div>
					<br clear="all"/>
					<label class="policy">Notes:</label><br/>
					<s:textarea name="notes" cols="60" rows="2"/>
				</s:if> 
				<s:if test="permission.operatorCorporate">
					<div class="buttons">
						<input type="button" class="picsbutton positive" value="Approve" />
						<input type="button" class="picsbutton negative" value="Reject" />
						<input type="button" class="picsbutton" value="Not Applicable" />
					</div>
					<br clear="all"/>
					<label class="policy">Notes:</label><br/>
					<s:textarea name="notes" cols="60" rows="2"/>
				</s:if>
			<br clear="all"/>
			<br />
		</s:if>
	</s:iterator>
</div>