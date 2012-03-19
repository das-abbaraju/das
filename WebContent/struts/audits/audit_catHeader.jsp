<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<s:if test="auditID > 0">
	<s:include value="../contractors/conHeader.jsp" />
	
	<s:if test="conAudit.auditType.classType.policy">
		<s:if test="conAudit.hasCaoStatusAfter('Pending') && conAudit.willExpireSoon() && !conAudit.expired">
			<div class="alert"><s:text name="Audit.message.Locked" /> 
				<s:iterator value="conAudit.contractorAccount.audits" id="newPending">
					<s:if test="#newPending.auditType.classType.policy && conAudit.id != #newPending.id">
						<s:if test="conAudit.auditType == #newPending.auditType && #newPending.auditStatus.pending">
							<a href="Audit.action?auditID=<s:property value="#newPending.id"/>"><s:property value="#newPending.auditType.name"/></a>
						</s:if>
					</s:if>
				</s:iterator>
			</div>
		</s:if>
	</s:if>
	
	<s:if test="invoiceOverdue">
		<div class="alert"><s:text name="Audit.message.OverdueInvoice" /></div>
	</s:if>
	<s:if test="willExpireSoon">
		<div class="alert">
			<s:text name="Audit.message.WillExpireSoon" >
				<s:param><s:text name="conAudit.contractorAccount.paymentExpires" /></s:param>
			</s:text>
		</div>
	</s:if>
</s:if>

<s:if test="permissions.contractor">
	<s:if test="conAudit.auditStatus.activeSubmitted || conAudit.auditStatus.activeResubmittedExempt">
		<s:if test="activePendingEditableAudits.size > 0">
			<div class="alert">
				<s:text name="Audit.message.ActivePendingEditable"><s:param value="activePendingEditableAudits.size" /></s:text>
				<ul>
					<s:iterator value="activePendingEditableAudits">
						<li>
							<s:text name="Audit.message.PleaseCompleteAudit">
								<s:param value="id" />
								<s:param value="auditFor" />
								<s:param><s:text name="%{auditType.getI18nKey('name')}" /></s:param>
							</s:text>
						</li>
					</s:iterator>
				</ul>
			</div>
			<div class="buttons">
				<a href="Audit.action?auditID=<s:property value="activePendingEditableAudits.get(0).getId()"/>" class="picsbutton"><s:text name="Audit.button.NextAudit" /> &gt;</a>
			</div>
		</s:if>
	</s:if>
	<s:if test="conAudit.auditType.classType.pqf && conAudit.auditStatus.activeSubmitted && conAudit.aboutToExpire">
		<div class="alert">
			<s:text name="Audit.message.AboutToExpire"><s:param><s:text name="%{conAudit.auditType.getI18nKey('name')}" /></s:param></s:text>
		</div>
	</s:if>
	<s:if test="conAudit.auditType.hasRequirements && conAudit.auditStatus.submitted && conAudit.percentVerified < 100" >
		<div class="info">
			<s:text name="Audit.message.OpenRequirements">
				<s:param><s:text name="%{conAudit.auditType.getI18nKey('name')}" /></s:param>
				<s:param value="auditID" />
			</s:text>
		</div>
	</s:if>
	<s:if test="conAudit.auditStatus.name() == 'Pending' && !conAudit.contractorAccount.paymentMethodStatusValid && conAudit.contractorAccount.mustPayB">
		<div class="info">
			<s:text name="Audit.message.UpdatePaymentMethod">
				<s:param value="conAudit.contractorAccount.id" />
			</s:text>
		</div>		
	</s:if>
	<s:if test="conAudit.contractorAccount.eligibleForImportPQF && needsImportPQFQuestion">
		<div class="info">
			<s:text name="Audit.message.ImportPQF">
				<s:param value="'importPQFCluetip'" />
				<s:param value="'importPQFCluetipLink'" />
			</s:text>
			<s:form id="importPQF">
				<s:hidden name="auditID" />
				<s:submit action="Audit!importPQFYes" cssClass="picsbutton positive" value="%{getText('Audit.button.ImportPQFYes')}" />
				<s:submit action="Audit!importPQFNo" cssClass="picsbutton negative" value="%{getText('Audit.button.ImportPQFNo')}" />
			</s:form>
			<div id="importPQFCluetip">
				<table>
					<tr>
						<td style="padding-right: 10px; vertical-align: middle;"><img src="images/help/import_pqf_sml.jpg" alt="Import PQF" /></td>
						<td>
							<s:text name="Audit.message.ImportPQF.cluetip">
								<s:param value="%{contractor.country.currency.symbol + importPQFFeeAmount.toPlainString()}" />
							</s:text>
						</td>
					</tr>
				</table>
			</div>
		</div>		
	</s:if>
</s:if>

<s:form>
	<s:hidden name="auditID" />
	<s:if test="!conAudit.auditType.classType.policy">
		<s:if test="canSubmit">
			<div class="alert" class="buttons" style="">
				<s:if test="conAudit.auditStatus.pending">
					<s:submit id="submit" value="Submit" name="button" cssStyle="font-size: 16px; padding: 8px; margin: 5px; color: darkgreen; font-weight: bold;"></s:submit>
					<s:if test="conAudit.auditType.pqf"><br /><s:text name="Audit.message.PQFCheck" /></s:if>
					<s:else>
						<br /><s:text name="Audit.message.FinalizeAudit">
							<s:param><s:text name="AuditStatus.Submitted.button" /></s:param>
							<s:param><s:text name="%{conAudit.auditType.getI18nKey('name')}" /></s:param>
						</s:text>
					</s:else>
				</s:if>
				<s:elseif test="conAudit.auditStatus.incomplete">
					<s:submit id="submit" value="Submit" name="button" cssStyle="font-size: 16px; padding: 8px; margin: 5px; color: darkgreen; font-weight: bold;"></s:submit>
					<br/><s:text name="Audit.message.CompleteRequirements"><s:param><s:text name="AuditStatus.Submitted.button" /></s:param></s:text>
				</s:elseif>
				<s:else>
					<s:checkbox name="''" onchange="resubmitPqf(this);"/>
					<s:text name="Audit.message.ConfirmData" /><br/>
					<s:submit id="submit" value="Submit" name="button" disabled="true"></s:submit>
				</s:else>
			</div>
		</s:if>
	</s:if>
	<s:if test="canClose">
		<div class="alert" class="buttons" style="">
			<s:hidden name="auditStatus" value="Active" />
			<s:submit value="%{'Close '.concat(conAudit.auditType.name)}"/>
		</div>
	</s:if>
</s:form>
