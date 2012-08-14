<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<script type="text/javascript">
(function($) {

	<%-- document.ready --%>
	$(function() {

		var cluetipOptions = {
			activation: 'click',
			arrows: true,
			clickThrough: false,
			closeText: "<img src='images/cross.png' width='16' height='16'>",
			cluetipClass: 'jtip',
			sticky: true
		};
		
		<%-- cluetip --%>
		$('#node_<s:property value="#q.id"/> .cluetip').cluetip($.extend({
			local: true
		}, cluetipOptions));
		
		<%-- translation cluetip --%>
		$('#node_<s:property value="#q.id"/> .cluetip-translation').cluetip(cluetipOptions);
	});
})(jQuery);
</script>

<%-- Question required status --%>

<s:set name="questionStillRequired" value="false" />

<s:if test="(#a == null || #a.answer == null || #a.answer.length() < 1)" >
	<s:if test="#q.required">
		<s:set name="questionStillRequired" value="true" />
	</s:if>
	
	<s:if test="#q.requiredQuestion.id > 0">
		<s:set name="dependsAnswer" value="answerMap.get(#q.requiredQuestion.id)" />
		
		<s:if test="#q.requiredAnswer == 'NULL' && (#dependsAnswer == null || #dependsAnswer.answer == '')">
        	<% // Policies must have either Policy Expiration Date OR In Good Standing %>
           	<s:set name="questionStillRequired" value="true" />
        </s:if>
        
		<s:if test="#q.requiredAnswer == 'NOTNULL' && #dependsAnswer != null && #dependsAnswer.answer != ''">
        	<% // If dependsOnQuestion is a textfield, textbox or a select box etc where the dependsOnAnswer is not null %>
           	<s:set name="questionStillRequired" value="true" />
        </s:if>
        
		<s:if test="#dependsAnswer != null && #q.requiredAnswer == #dependsAnswer.answer">
			<s:set name="questionStillRequired" value="true" />
		</s:if>
	</s:if>
</s:if>
<s:elseif test="#q.questionType == 'Tagit' && #a != null && #a.answer == '[]'">
	<s:set name="questionStillRequired" value="true" />
</s:elseif>

<%-- Question auto save disable --%>
<s:if test="#q.questionType == 'ESignature' || #q.questionType == 'Tagit'">
	<s:set name="save" value="%{'save-disable'}" />
</s:if>
<s:else>
	<s:set name="save" value="" />
</s:else>

<%-- Question --%>

<s:set name="hidden" value="!#q.isVisible(answerMap)" />

<div id="node_<s:property value="#q.id"/>" class="clearfix question
	<s:if test="#shaded && #category.columns == 1"> shaded</s:if>
	<s:if test="#hidden"> hide</s:if>
	<s:if test="#q.dependentRequired.size() > 0"> hasDependentRequired</s:if>
	<s:if test="#q.dependentVisible.size() > 0"> hasDependentVisible</s:if>
	<s:if test="#q.auditCategoryRules.size() > 0"> hasDependentRules</s:if>
	<s:if test="affectsAudit"> affectsAudit</s:if>
	<s:if test="#q.functionWatchers.size > 0"> hasFunctions</s:if>
	<s:if test="#questionStillRequired">required</s:if>
	<s:if test="#q.questionType == 'Check Box'">checkbox</s:if>
	${save}
">
	<h3>
		<s:if test="#questionStillRequired">
			<img src="images/star.png" class="required" alt="<s:text name="AuditQuestion.required" />">
		</s:if>
		
		<s:url action="Audit" var="question_number_url">
            <s:param name="auditID">${conAudit.id}</s:param>
        </s:url>
        
        <a href="${question_number_url}#categoryID=${category.topParent.id}&questionID=${q.id}" class="questionNumber"><s:property value="#q.expandedNumber"/></a>
		
		<s:property value="getStrippedHref(#q.name)" escape="false"/>
		
		<s:if test="#q.helpText.exists">
			<a class="cluetip" href="#" rel="#cluetip_<s:property value="#q.id"/>" title="<s:text name="Audit.AdditionalInformation" />">
				<img src="images/help-icon.png" />
			</a>
			
			<div id="cluetip_<s:property value="#q.id"/>" class="cluetipBox">
				<span title="<s:property value="#q.name"/>">
					<s:property value="#q.helpText" escape="false" />
				</span>
			</div>
		</s:if>
		
		<s:if test="permissions.admin">
			<a class="cluetip-translation debug" rel="QuestionTranslationsAjax.action?id=<s:property value="id"/>">
				<img src="images/preview.gif">
			</a>
		</s:if>
		
		<s:if test="permissions.admin">
			<s:url var="editQuestion" action="ManageQuestion">
				<s:param name="id">${id}</s:param>
			</s:url>
			
			<a class="edit-question debug" href="${editQuestion}" target="_blank">
				<img src="images/edit_pencil.png" />
			</a>
		</s:if>
		
		<s:if test="(#q.id == 3563 || #q.id == 3565 || #q.id == 3566) && #a.answer.length() > 0">
			<br />
			<a href="http://www.osha.gov/pls/imis/establishment.inspection_detail?id=<s:property value="#a.answer"/>" target="_BLANK" title="<s:text name="global.NewWindow" />">
				<s:text name="Audit.OshaCitations" />
			</a>
		</s:if>
		<s:if test="(#q.id == 9208) && #a.answer.length() > 0">
			<br />
			<a href="http://safer.fmcsa.dot.gov/query.asp?searchtype=ANY&query_type=queryCarrierSnapshot&query_param=USDOT&query_string=<s:property value="#a.answer"/>" target="_BLANK" title="<s:text name="global.NewWindow" />">
				<s:text name="Audit.USDOT" />
			</a>
		</s:if>
	</h3>

	<%-- Question modes --%>
	
	<s:if test="#mode == 'View' || #mode == 'ViewAll'">
		<s:include value="audit_question_view.jsp"></s:include>
	</s:if>
	<s:elseif test="#mode == 'Edit'">
	 	<s:if test="isCanEditCategory(#category) && #q.questionType != 'Calculation'">
			<s:include value="audit_question_edit.jsp"></s:include>
		</s:if>
		<s:else>
			<s:include value="audit_question_view.jsp"></s:include>
		</s:else>
	</s:elseif>
	<s:elseif test="#mode == 'Verify'">
		<s:include value="audit_question_edit.jsp"></s:include>
	</s:elseif>
</div>