<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<script type="text/javascript">
(function($) {

	<%-- document.ready --%>
	$(function() {
		
		<%-- cluetip --%>
		$('#node_<s:property value="#q.id"/> .cluetip').cluetip({
			arrows: true,
			cluetipClass: 'jtip',
			clickThrough: false,
			activation: 'click',
			sticky: true,
			showTitle: false,
			closeText: "<img src='images/cross.png' width='16' height='16'>"
		});

		<%-- ambest --%>
		var ambest = $('#ambest');
		
		if (ambest.length) {
			ambest.autocomplete('AmBestSuggestAjax.action', {
				minChars: 3,
				formatResult: function(data,i,count) {
					return data[1];
				}
			}).change(function(e) {
				if ($(this).blank())
					$(this).closest('div.question').find('[name="auditData.comment"]').val('');
			}).result(function(event, data){
				var div = $(this).closest('div.question');
				if (data[2]!="UNKNOWN")
					div.find('[name="auditData.comment"]').val(data[2])
				else
					div.find('[name="auditData.comment"]').val('');
				$(this).trigger('change');
			});			
		}
	});
})(jQuery);
</script>

<%-- Question required status --%>

<s:set name="questionStillRequired" value="false" />

<s:if test="(#a == null || #a.answer == null || #a.answer.length() < 1)">
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

<%-- Question auto save disable --%>
<s:if test="#q.questionType == 'ESignature'">
	<s:set name="save" value="%{'save-disable'}" />
</s:if>
<s:else>
	<s:set name="save" value="" />
</s:else>

<%-- Question --%>

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
		
		<span class="questionNumber">
			<s:property value="#q.expandedNumber"/>
			
			<s:if test="!isStringEmpty(#q.helpText)">
				<br />
				<a class="cluetip helpBig" rel="#cluetip_<s:property value="#q.id"/>" title="<s:text name="Audit.AdditionalInformation" />"></a>
				<div id="cluetip_<s:property value="#q.id"/>" class="cluetipBox">
					<span title="<s:property value="#q.name"/>">
						<s:property value="#q.helpText" escape="false" />
					</span>
				</div>
			</s:if>
		</span>
		
		<s:property value="#q.name" escape="false"/>
		
		<s:if test="permissions.admin">
			<a name="qTranslations" class="cluetip debug" rel="QuestionTranslationsAjax.action?id=<s:property value="id"/>">
				<img src="images/preview.gif">
			</a>
		</s:if>
		
		<s:if test="(#q.id == 3563 || #q.id == 3565 || #q.id == 3566) && #a.answer.length() > 0">
			<br />
			<a href="http://www.osha.gov/pls/imis/establishment.inspection_detail?id=<s:property value="#a.answer"/>" target="_BLANK" title="<s:text name="global.NewWindow" />">
				<s:text name="Audit.OshaCitations" />
			</a>
		</s:if>
	</h3>

	<%-- Question modes --%>
	
	<s:if test="#mode == 'View' || #mode == 'ViewAll'">
		<s:include value="audit_question_view.jsp"></s:include>
	</s:if>
	<s:elseif test="#mode == 'Edit'">
	 	<s:if test="isCanEditCategory(#category) || #q.questionType != 'Calculation'">
			<s:include value="audit_question_edit.jsp"></s:include>
		</s:if><s:else>
			<s:include value="audit_question_view.jsp"></s:include>
		</s:else>
	</s:elseif>
	<s:elseif test="#mode == 'Verify'">
		<s:include value="audit_question_edit.jsp"></s:include>
	</s:elseif>
</div>