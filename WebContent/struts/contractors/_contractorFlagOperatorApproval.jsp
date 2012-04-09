<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:if test="co.operatorAccount.approvesRelationships.toString() == 'Yes'">
	<s:if test="co.workStatusPending">
		<div class="alert">
			<s:text name="ContractorFlag.OperatorHasNotApproved" />
		</div>
	</s:if>

	<s:if test="co.workStatusRejected">
		<div class="alert">
			<s:text name="ContractorFlag.OperatorDidNotApproved" />
		</div>
	</s:if>
</s:if>

<div id="notesList" class="details"">
	<s:include value="../notes/account_notes_embed.jsp" />
</div>