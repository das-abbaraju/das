<%@ taglib prefix="s" uri="/struts-tags"%>
<s:set name="divID" value="'q'+#q.id" />

<div class="thinking" id="thinking_<s:property value="#q.id"/>"></div>

<s:set name="questionStillRequired" value="false" />
<s:if test="(#a == null || #a.answer == null || #a.answer.length() < 1)">
	<s:if test="#q.isRequired == 'Yes'">
		<s:set name="questionStillRequired" value="true" />
	</s:if>
	<s:if test="#q.isRequired == 'Depends' && #q.dependsOnQuestion.id > 0">
		<s:set name="dependsAnswer" value="answerMap.get(#q.dependsOnQuestion.id)" />
		<s:if test="#q.dependsOnAnswer == 'NULL' && (#dependsAnswer == null || #dependsAnswer.answer == '')">
        	<% // Policies must have either Policy Expiration Date OR In Good Standing %>
           	<s:set name="questionStillRequired" value="true" />
        </s:if>
		<s:if test="#q.dependsOnAnswer == 'NOTNULL' && #dependsAnswer != null">
        	<% // If dependsOnQuestion is a textfield, textbox or a select box etc where the dependsOnAnswer is not null %>
           	<s:set name="questionStillRequired" value="true" />
        </s:if>
		<s:if test="#dependsAnswer != null && #q.dependsOnAnswer == #dependsAnswer.answer">
			<s:set name="questionStillRequired" value="true" />
		</s:if>
	</s:if>
</s:if>

<s:if test="questionStillRequired">
	<span class="printrequired"><img src="images/yellow_star.gif"></span>
</s:if>

<span class="question <s:if test="#questionStillRequired">required</s:if>">
	<a name="q<s:property value="#q.required"/>"></a>
	<span class="questionNumber"><s:property value="#q.expandedNumber"/></span>
	
	<s:property value="#q.question" escape="false"/>
	<br />
	<s:if test="#q.linkUrl1 != null && #q.linkUrl1.length() > 0"><a href="http://<s:property value="#q.linkUrl1"/>" target="_BLANK" title="opens in new window"><s:property value="#q.linkText1"/></a></s:if>
	<s:if test="#q.linkUrl2 != null && #q.linkUrl2.length() > 0"><a href="http://<s:property value="#q.linkUrl2"/>" target="_BLANK" title="opens in new window"><s:property value="#q.linkText2"/></a></s:if>
	<s:if test="#q.linkUrl3 != null && #q.linkUrl3.length() > 0"><a href="http://<s:property value="#q.linkUrl3"/>" target="_BLANK" title="opens in new window"><s:property value="#q.linkText3"/></a></s:if>
	<s:if test="#q.linkUrl4 != null && #q.linkUrl4.length() > 0"><a href="http://<s:property value="#q.linkUrl4"/>" target="_BLANK" title="opens in new window"><s:property value="#q.linkText4"/></a></s:if>
	<s:if test="#q.linkUrl5 != null && #q.linkUrl5.length() > 0"><a href="http://<s:property value="#q.linkUrl5"/>" target="_BLANK" title="opens in new window"><s:property value="#q.linkText5"/></a></s:if>
	<s:if test="#q.linkUrl6 != null && #q.linkUrl6.length() > 0"><a href="http://<s:property value="#q.linkUrl6"/>" target="_BLANK" title="opens in new window"><s:property value="#q.linkText6"/></a></s:if>
	<s:if test="(#q.id == 3563 || #q.id == 3565 || #q.id == 3566) && #a.answer.length() > 0"><a href="http://www.osha.gov/pls/imis/establishment.inspection_detail?id=<s:property value="#a.answer"/>" target="_BLANK" title="opens in new window">OSHA Citations</a></s:if>
</span>

<s:if test="#q.questionType == 'Text Area'">
	<br clear="all" />
</s:if>

<div class="answer">
	<input type="hidden" id="<s:property value="#q.id"/>_answerID" value="<s:property value="#a.id"/>" />
	<input type="hidden" id="<s:property value="#q.id"/>_questionID" value="<s:property value="#q.id"/>" />
	<s:if test="mode == 'Verify'">
		<s:property value="%{#a.answer}"/>
	</s:if>
	<s:if test="#q.questionType == 'Text Area'">
		<s:textarea cols="70" rows="4" name="answer%{#q.id}" value="%{#a.answer}" cssStyle="margin-left: 80px;"
			onchange="saveAnswer('%{#q.id}', this);">
		</s:textarea>
	</s:if>
	<s:if test="#q.questionType == 'Text'">
		<s:textfield name="answer%{#q.id}" value="%{#a.answer}" size="30" 
			onchange="saveAnswer('%{#q.id}', this);"/>
	</s:if>
	<s:if test="#q.questionType == 'Additional Insured'">
		<s:textfield name="answer%{#q.id}" value="%{#a.answer}" size="30" 
			onchange="saveAnswer('%{#q.id}', this);"/>
	</s:if>
	<s:if test="#q.questionType == 'Date'">
		<s:textfield name="answer%{#q.id}" value="%{#a.answer}" size="8" 
			onchange="saveAnswer('%{#q.id}', this);"/>
			<span style="font-style: italic; font-size: 12px;">example: 12/31/1999</span>
	</s:if>
	<s:if test="#q.questionType == 'License'">
		<s:textfield name="answer%{#q.id}" value="%{#a.answer}" size="30" 
			onchange="saveAnswer('%{#q.id}', this);"/>
		<s:property value="@com.picsauditing.util.Constants@displayStateLink(#q.question, #a.answer)" escape="false" />			
	</s:if>
	<s:if test="#q.questionType == 'Check Box' || #q.questionType == 'Industry' || #q.questionType == 'Main Work'">
		<s:checkbox fieldValue="X" value="#a.id > 0 && #a.answer.length() == 1" name="answer%{#q.id}"
			onclick="saveAnswer('%{#q.id}', this);" />
	</s:if>
	<s:if test="#q.questionType == 'Yes/No'">
		<s:radio theme="pics" cssClass="question_%{#q.id}" list="#{'Yes':'Yes','No':'No'}" name="answer%{#q.id}" value="%{#a.answer}" 
			onclick="$('.question_%{#q.id}').attr({'disabled':'disabled'}); saveAnswer('%{#q.id}', this);"></s:radio>
	</s:if>
	<s:if test="#q.questionType == 'Yes/No/NA'">
		<s:radio theme="pics" cssClass="question_%{#q.id}" list="#{'Yes':'Yes','No':'No','NA':'NA'}" name="answer%{#q.id}" value="%{#a.answer}" 
			onclick="$('.question_%{#q.id}').attr({'disabled':'disabled'}); saveAnswer('%{#q.id}', this);"></s:radio>
	</s:if>
	<s:if test="#q.questionType == 'Office Location'">
		<s:radio theme="pics" cssClass="question_%{#q.id}" list="#{'No':'No','Yes':'Yes','Yes with Office':'Yes with Office'}" 
			name="answer%{#q.id}" value="%{#a.answer}"
			onclick="$('.question_%{#q.id}').attr({'disabled':'disabled'}); saveAnswer('%{#q.id}', this);"></s:radio>
	</s:if>
	<s:if test="#q.questionType == 'State'">
		<!-- TODO remove -->
		<s:set name="state_header" value="#q.question.contains('State') ? #q.question.contains('Province') ? 'State / Province' : 'State' : #q.question.contains('Province') ? 'Province' : 'State' "/>
		<s:select list="getStateList(#q.countries)" value="%{#a.answer}" name="answer%{#q.id}" 
			headerKey="" headerValue=" - %{#state_header} - " listKey="isoCode" listValue="name"
			onchange="saveAnswer('%{#q.id}', this);"></s:select>
	</s:if>
	<s:if test="#q.questionType == 'Country'">
		<s:select list="countryList" value="%{#a.answer}" name="answer%{#q.id}" 
			listKey="isoCode" listValue="english"
			headerKey="" headerValue="- Country -"
			onchange="saveAnswer('%{#q.id}', this);"></s:select>
	</s:if>
	<s:if test="#q.questionType == 'Money' || #q.questionType == 'Number'">
		<s:textfield name="answer%{#q.id}" value="%{#a.answer}" size="19" 
			onchange="saveAnswer('%{#q.id}', this);" cssClass="number" />
	</s:if>
	<s:if test="#q.questionType == 'Decimal Number'">
		<s:textfield name="answer%{#q.id}" value="%{#a.answer}" size="19" 
			onchange="saveAnswer('%{#q.id}', this)" cssClass="number" />
	</s:if>
	<s:if test="#q.questionType == 'Service'">
		<nobr><s:checkbox fieldValue="C" value="%{#a.answer.indexOf('C') != -1}" name="answer%{#q.id}_C" 
			onclick="saveAnswer('%{#q.id}', this);" /> C</nobr>
		<nobr><s:checkbox fieldValue="S" value="%{#a.answer.indexOf('S') != -1}" name="answer%{#q.id}_S" 
			onclick="saveAnswer('%{#q.id}', this);" /> S</nobr>
	</s:if>
	<s:if test="#q.questionType == 'Radio'">
		<s:radio theme="pics" cssClass="question_%{#q.id}" list="#q.optionsVisible" listKey="optionName" listValue="optionName" 
			value="#a.answer" name="answer%{#q.id}" 
			onclick="$('.question_%{#q.id}').attr({'disabled':'disabled'}); saveAnswer('%{#q.id}', this);"/>
	</s:if>
	<s:if test="#q.questionType == 'AMBest'">
		<input type="hidden" id="ambest_naic_code" />
		<s:textfield id="ambest_autocomplete" name="answer%{#q.id}" value="%{#a.answer}" size="75" />
		
		<script type="text/javascript"> 
			$('#ambest_autocomplete').autocomplete('AmBestSuggestAjax.action',
			{
				minChars: 3,
				formatResult: function(data,i,count) {
					return data[1];
				}
			}).result(function(event, data){
					if (data[2]!="UNKNOWN")
						$('#ambest_naic_code').val(data[2]);
					else
						$('#ambest_naic_code').val("");
					saveAnswerComment('<s:property value="%{#q.id}"/>', $('#ambest_autocomplete')[0], $('#ambest_naic_code'));
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
		<s:else>
			
		</s:else>
	</s:if>
	<s:if test="#q.questionType.startsWith('File')">
		<nobr>
			<s:if test="#a.id > 0 && #a.answer.length() > 0">
				<a href="DownloadAuditData.action?auditID=<s:property value="auditID"/>&answer.id=<s:property value="#a.id"/>" 
					target="_BLANK">View File</a>
			</s:if>
			<s:else>File Not Uploaded</s:else>
			<input id="show_button_<s:property value="#q.id"/>" type="button" 
				value="<s:if test="#a.id > 0 && #a.answer.length() > 0">Edit</s:if><s:else>Add</s:else> File" 
				onclick="showFileUpload('<s:property value="#a.id"/>', 
					'<s:property value="#q.id"/>', 
					'<s:property value="#q.id"/>');"
				title="Opens in new window (please disable your popup blocker)" />
		</nobr>
	</s:if>
	
	<s:if test="#a.verified && !#q.hasRequirementB">
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
		<label>Comments:</label>
		<input type="button" class="picsbutton positive" value="Save Comment" onclick="saveAnswerComment('<s:property value="#q.id"/>', null, $('#comment<s:property value="#q.id"/>'));"/>
		<s:textarea name="answer%{#q.id}" id="comment%{#q.id}" cssClass="richText" value="%{#a.comment}" rows="6" cols="80"/>
		<script type="text/javascript">
			$(function() {
				$('#comment<s:property value="#q.id"/>').wysiwyg();
			});
		</script>
	</s:if>
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
	
	<input id="verifyButton_<s:property value="#q.id"/>" type="submit" onclick="return verifyAnswer(<s:property value="#q.id"/>, <s:property value="#a.id"/>);"
	value="<s:property value="#attr.verifyText"/>" />

	<span id="verify_details_<s:property value="#q.id"/>"
	style='display: <s:property value ="#attr.verifyDetailDisplay"/>;'
	class="verified">Verified on <s:date name="#a.dateVerified"
	format="MMM d, yyyy" /> by <s:property value="#a.auditor.name" /></span>
</s:if>
<div class="dependentQuestions hide" ><s:iterator value="#q.dependentQuestions"><s:if test="#q.subCategory.category == subCategory.category">,<s:property value="id"/></s:if></s:iterator></div>
