<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp" pageEncoding="UTF-8"%>

<s:if test="conAudit.auditType.scoreable">
	<div id="audit_score_and_last_updated">
		<div class="auditHeader auditScore">
			<span class="score-text"><s:text name="ContractorAccount.score" />:</span>
			<s:if test="conAudit.auditType.classType.im">
				<div id="auditScore"><s:property value="conAudit.printableScore"/></div>
			</s:if>
			<s:else>
				<span id="auditScore" class="score-text"><s:property value="conAudit.score"/></span>
				<s:if test="getScoreLastUpdated() != ''">
					<div id="last_updated_date">Last updated <span><s:property value="getScoreLastUpdated()" /></span></div>
				</s:if>
			</s:else>
		</div>
	</div>
</s:if>
