<%@ page language="java" errorPage="/exception_handler.jsp" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<s:set name="divID" value="'q'+#q.id" />
<div class="thinking" id="thinking_<s:property value="#q.id"/>"></div>

<script type="text/javascript">
$(function() {
	$('#node_<s:property value="#q.id"/> .cluetip').cluetip({
		arrows: true,
		cluetipClass: 'jtip',
		local: true,
		clickThrough: false,
		activation: 'click',
		sticky: true,
		showTitle: false,
		closeText: "<img src='images/cross.png' width='16' height='16'>"
	});
});
</script>

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

<s:if test="questionStillRequired">
	<span class="printrequired"><img src="images/yellow_star.gif"></span>
</s:if>

<span class="question <s:if test="#questionStillRequired">required</s:if>">
	<span class="questionNumber"><s:property value="#q.expandedNumber"/>
		<s:if test="!#q.helpText.empty">
			<br />
			<a class="cluetip helpBig" rel="#cluetip_<s:property value="#q.id"/>" title="Additional Information"></a>
			<div id="cluetip_<s:property value="#q.id"/>" class="cluetipBox">
				<span title="<s:property value="#q.name"/>">
					<s:property value="#q.helpText" escape="false" />
				</span>
			</div>
		</s:if>
	</span>
	<s:property value="#q.name" escape="false"/>
	<br />
	<s:if test="(#q.id == 3563 || #q.id == 3565 || #q.id == 3566) && #a.answer.length() > 0"><a href="http://www.osha.gov/pls/imis/establishment.inspection_detail?id=<s:property value="#a.answer"/>" target="_BLANK" title="opens in new window">OSHA Citations</a></s:if>
</span>

<div class="answer">
	<s:form cssClass="qform" id="qform%{#q.id}" onsubmit="return false;">
		<s:hidden name="categoryID" value="%{#q.category.id}"/>
		<s:if test="auditData.audit != null">
			<s:hidden name="auditData.audit.id"/>
		</s:if>
		<s:else>
			<s:hidden name="auditData.audit.id" value="%{conAudit.id}"/>
		</s:else>
		<s:hidden name="auditData.question.id" value="%{#q.id}"/>
		<s:hidden name="mode"/>
		<s:if test="mode == 'Verify'">
			<s:property value="#a.answer"/>
		</s:if>
		<!-- Radios/Check boxes -->
		<s:if test="#q.questionType == 'Check Box' || #q.questionType == 'Main Work' || #q.questionType == 'Industry'">
			<s:checkbox fieldValue="X" name="auditData.answer" value="#a.answer == \"X\""/>
		</s:if>
		<s:if test="#q.questionType == 'Yes/No'">
			<s:radio theme="audits" list="#{'Yes':'Yes','No':'No'}" name="auditData.answer" value="%{#a.answer}"></s:radio>
			<s:if test="#q.auditType.policy">
				<s:set name="op" value="%{getOperatorByName(#q.category.name)}" />
				<s:if test="#op != null && #op.id > 0">	
					<div class="clearfix question shaded">
						If it does NOT comply, please explain below.
						<s:if test="#op.insuranceForms.size > 0">
							<ul style="list-style:none">
								<s:iterator value="#op.insuranceForms">
									<li><a href="forms/<s:property value="file"/>" target="_BLANK" title="Opens in new Window"><s:property value="formName"/></a></li>
								</s:iterator>
							</ul>
						</s:if>
						<br clear="all"/>
						<div class="clear"></div>
					</div>
				</s:if>
			</s:if>
		</s:if>
		<s:if test="#q.questionType == 'Yes/No/NA'">
			<s:radio theme="audits" list="#{'Yes':'Yes','No':'No','NA':'NA'}" name="auditData.answer" value="%{#a.answer}"></s:radio>
		</s:if>
		<s:if test="#q.questionType == 'Office Location'">
			<s:radio theme="audits" list="#{'No':'No','Yes':'Yes','Yes with Office':'Yes with Office'}" name="auditData.answer" value="%{#a.answer}"></s:radio>
		</s:if>
		<s:if test="#q.questionType == 'Radio'">
			<s:radio theme="audits" list="#q.optionsVisible" listKey="optionName" listValue="optionName" 
				name="auditData.answer" value="#a.answer" />
		</s:if>
		<s:if test="#q.questionType == 'Rating 1-5'">
			<s:select list="{1,2,3,4,5}" name="auditData.answer" value="#a.answer" headerKey="" headerValue=""/>
		</s:if>
		<!-- Non checkboxes/radio -->
		<s:if test="#q.questionType == 'Text Area'">
			<s:textarea rows="4" cols="70" name="auditData.answer" value="%{#a.answer}"></s:textarea>
		</s:if>
		<s:if test="#q.questionType == 'Text'">
			<s:textfield name="auditData.answer" value="%{#a.answer}" size="30"/>
		</s:if>
		<s:if test="#q.questionType == 'Additional Insured'">
			<s:textfield name="auditData.answer" value="%{#a.answer}" size="30"/>
		</s:if>
		<s:if test="#q.questionType == 'Date'">
			<s:textfield name="auditData.answer" value="%{#a.answer}" size="30"/>
			<span style="font-style: italic; font-size: 12px;">example: 12/31/1999</span>
		</s:if>
		<s:if test="#q.questionType == 'License'">
			<s:textfield name="auditData.answer" value="%{#a.answer}" size="30"/>
			<s:property value="@com.picsauditing.util.Constants@displayStateLink(#q.question, #a.answer)" escape="false" />
		</s:if>
		<s:if test="#q.questionType == 'State'">
			<!-- TODO remove -->
			<s:select list="stateList" value="%{#a.answer}" name="auditData.answer" headerKey="" headerValue="- State -" listKey="isoCode" listValue="name"></s:select>
		</s:if>
		<s:if test="#q.questionType == 'Country'">
			<s:select list="countryList" name="auditData.answer" value="%{#a.answer}"
				headerKey="" headerValue="- Country -" listKey="isoCode" listValue="english">
			</s:select>
		</s:if>
		<s:if test="#q.questionType == 'Money' || #q.questionType == 'Number' || #q.questionType == 'Decimal Number'">
			<s:textfield name="auditData.answer" value="%{#a.answer}" cssClass="number" size="30"/>
		</s:if>
		<s:if test="#q.questionType == 'Service'">
			<s:hidden name="auditData.answer" value="%{#a.answer}"/>
			<nobr><s:checkbox fieldValue="C" name="multiAnswer" value="%{#a.answer.indexOf('C') != -1}" /> C </nobr>
			<nobr><s:checkbox fieldValue="S" name="multiAnswer" value="%{#a.answer.indexOf('S') != -1}" /> S </nobr>
		</s:if>
		<s:if test="#q.questionType == 'AMBest'">
			<s:hidden name="auditData.comment" value="%{#a.comment}"/>
			<s:textfield id="ambest" name="auditData.answer" value="%{#a.answer}" size="30"/>
			
			<script type="text/javascript">
			$(function() {
				$('#ambest').autocomplete('AmBestSuggestAjax.action',
				{
					minChars: 3,
					formatResult: function(data,i,count) {
						return data[1];
					}
				}).change(function(e) {
					if ($(this).blank())
						$(this).closest('div.question').find('[name=auditData.comment]').val('');
				}).result(function(event, data){
					var div = $(this).closest('div.question');
					if (data[2]!="UNKNOWN")
						div.find('[name=auditData.comment]').val(data[2])
					else
						div.find('[name=auditData.comment]').val('');
					$(this).trigger('change');
				});
			});
			</script>
			
			<s:if test="#a.commentLength">
				<s:set name="ambest" value="@com.picsauditing.dao.AmBestDAO@getAmBest(#a.comment)" />
				<br>
				NAIC#: <s:property value="#a.comment" />
				<s:if test="#ambest.amBestId > 0">
					AM Best Rating: <s:property value="#ambest.ratingAlpha" /> /
					Class: <s:property value="#ambest.financialAlpha" />
				</s:if>
				<br>
			</s:if>
		</s:if>
		<s:if test="#q.questionType == 'File'">
			<nobr>
				<s:if test="#a.id > 0 && #a.answer.length() > 0">
					<a href="DownloadAuditData.action?auditID=<s:property value="auditID"/>&auditData.question.id=<s:property value="#q.id"/>" 
						target="_BLANK">View File</a>
				</s:if>
				<s:else>File Not Uploaded</s:else>
				<input id="show_button_<s:property value="#q.id"/>" type="button" 
					value="<s:if test="#a.id > 0 && #a.answer.length() > 0">Edit</s:if><s:else>Add</s:else> File"
					class="fileUpload" title="Opens in new window (please disable your popup blocker)" />
			</nobr>
		</s:if>
		<s:if test="#q.questionType == 'FileCertificate'">
			<s:include value="audit_question_cert_load.jsp" />
		</s:if>
		
		<s:if test="#a.verified && !#q.hasRequirement">
			<span class="verified">
				Answer verified on <s:date name="#a.dateVerified" format="MMM d, yyyy" />
			</span>
		</s:if>
		<s:if test="#a.hasRequirements">
			<br />
			<span class="requirement">
				<label>Requirement:</label>
				<s:if test="#a.requirementOpen">
					<span class="unverified">Open</span>
				</s:if>
				<s:elseif test="#a.wasChangedB">
					<span class="verified">Closed on <s:date name="#a.dateVerified" format="MMM d, yyyy" /></span>
				</s:elseif>
			</span>
		</s:if>
		<s:if test="#q.showComment || mode == 'Verify'">
			<br/>
			<label>Comments:</label> <br/>
			<s:textarea name="auditData.comment" value="%{#a.comment}" rows="6" cols="70" />
		</s:if>
	</s:form>
</div>

<s:if test="#a.hasRequirements && #a.requirementOpen">
	<br clear="all"/>
	<div class="error"><s:property value="#q.requirement" escape="false"/></div>
</s:if>

<s:include value="../actionMessages.jsp" />

<s:if test="mode == 'Verify'">
	<s:if test="#a.verified == true">
		<s:set name="verifyText" value="'Unverify'" />
		<s:set name="verifyDetailDisplay" value="'inline'" />
	</s:if>
	<s:else>
		<s:set name="verifyText" value="'Verify'" />
		<s:set name="verifyDetailDisplay" value="'none'" />
	</s:else>
	
	<input class="verify" id="verifyButton_<s:property value="#q.id"/>" type="submit" value="<s:property value="#attr.verifyText"/>" />

	<span id="verify_details_<s:property value="#q.id"/>" style='display: <s:property value ="#attr.verifyDetailDisplay"/>;' class="verified">
		Verified on <s:date name="#a.dateVerified" format="MMM d, yyyy" /> by <s:property value="#a.auditor.name" />
	</span>
</s:if>
<div class="dependentRequired hide"><s:iterator value="#q.dependentRequired" status="s"><s:property value="id"/><s:if test="!#s.last">,</s:if></s:iterator></div>
<div class="dependentVisible hide"><s:iterator value="#q.getDependentVisible(#a.answer)" status="s"><s:property value="id"/><s:if test="!#s.last">,</s:if></s:iterator></div>
<div class="dependentVisibleHide hide"><s:iterator value="#q.getDependentVisibleHide(#a.answer)" status="s"><s:property value="id"/><s:if test="!#s.last">,</s:if></s:iterator></div>
