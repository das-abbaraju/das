<%@ taglib prefix="s" uri="/struts-tags"%>
<s:if test="#a.id > 0">
	<s:set name="divID" value="'a'+#a.id" />
</s:if>
<s:else>
	<s:if test="#parentAnswer.id > 0">
		<s:set name="divID" value="'q'+#q.id + 'p'+#parentAnswer.id" />
	</s:if>
	<s:else>
		<s:set name="divID" value="'q'+#q.id" />
	</s:else>
</s:else>

<s:if test="#q.allowMultipleAnswers">
	<s:if test="#a.id > 0">
		<a href="javascript: removeTuple(<s:property value="#a.id"/>);" class="remove right" 
			style="font-size: 14px; font-weight: normal; text-decoration: none; position: relative; top: 5px;">Remove</a>
	</s:if>
	<s:else>
		<a href="javascript: addTuple(<s:property value="#q.id"/>);" class="edit right" 
			style="font-size: 14px; font-weight: normal; text-decoration: none; position: relative; top: 5px;">Add</a>
	</s:else>
	<h4 class="groupTitle">
		<s:property value="#a.answer"/>
	</h4>
</s:if>

<div class="thinking" id="thinking_<s:property value="#divID"/>"></div>

<s:set name="questionStillRequired" value="false" />
<s:if test="(#a == null || #a.answer == null || #a.answer.length() < 1)">
	<s:if test="#q.isRequired == 'Yes'">
		<s:set name="questionStillRequired" value="true" />
	</s:if>
	<s:if test="#q.isRequired == 'Depends' && #q.dependsOnQuestion.id > 0">
		<s:if test="#parentAnswer == null">
			<s:set name="dependsAnswer" value="answerMap.get(#q.dependsOnQuestion.id)" />
		</s:if>
		<s:else>
			<s:set name="dependsAnswer" value="answerMap.get(#q.dependsOnQuestion.id, #parentAnswer.id)" />
		</s:else>
		<s:if test="#q.dependsOnAnswer == #dependsAnswer.answer">
			<s:set name="questionStillRequired" value="true" />
		</s:if>
	</s:if>
</s:if>
<s:if test="#q.allowMultipleAnswers">
	<s:set name="questionStillRequired" value="false" />
</s:if>

<span class="question <s:if test="#questionStillRequired">required</s:if>">
	<a name="q<s:property value="#q.required"/>"></a>
	<s:property value="#q.subCategory.category.number"/>.<s:property value="#q.subCategory.number"/>.<s:property value="#q.number"/>&nbsp;&nbsp;
	
	<s:property value="#q.question" escape="false"/>
	<br />
	<s:if test="#q.linkUrl1.length() > 0"><a href="http://<s:property value="#q.linkUrl1"/>" target="_BLANK" title="opens in new window"><s:property value="linkText1"/></a></s:if>
	<s:if test="#q.linkUrl2.length() > 0"><a href="http://<s:property value="#q.linkUrl2"/>" target="_BLANK" title="opens in new window"><s:property value="linkText2"/></a></s:if>
	<s:if test="#q.linkUrl3.length() > 0"><a href="http://<s:property value="#q.linkUrl3"/>" target="_BLANK" title="opens in new window"><s:property value="linkText3"/></a></s:if>
	<s:if test="#q.linkUrl4.length() > 0"><a href="http://<s:property value="#q.linkUrl4"/>" target="_BLANK" title="opens in new window"><s:property value="linkText4"/></a></s:if>
	<s:if test="#q.linkUrl5.length() > 0"><a href="http://<s:property value="#q.linkUrl5"/>" target="_BLANK" title="opens in new window"><s:property value="linkText5"/></a></s:if>
	<s:if test="#q.linkUrl6.length() > 0"><a href="http://<s:property value="#q.linkUrl6"/>" target="_BLANK" title="opens in new window"><s:property value="linkText6"/></a></s:if>
</span>

<s:if test="#q.questionType == 'Text Area'">
	<br clear="all" />
</s:if>

<div class="answer">
	<input type="hidden" id="<s:property value="#divID"/>_answerID" value="<s:property value="#a.id"/>" />
	<input type="hidden" id="<s:property value="#divID"/>_questionID" value="<s:property value="#q.id"/>" />
	
	<s:if test="#parentAnswer != null && #parentAnswer.id != null">
		<input type="hidden" id="<s:property value="#divID"/>_parentAnswerID" value="<s:property value="#parentAnswer.id"/>" />
	</s:if>
	<s:else>
		<input type="hidden" id="<s:property value="#divID"/>_parentAnswerID" value="0" />
	</s:else>
	
	<input type="hidden" id="<s:property value="#divID"/>_multiple" value="<s:property value="#q.allowMultipleAnswers"/>" />
	<s:if test="#q.questionType == 'Text Area'">
		<s:textarea cols="70" rows="4" name="answer%{#divID}" value="%{#a.answer}" cssStyle="margin-left: 80px; width: 100%;"
			onchange="saveAnswer('%{#divID}', this);">
		</s:textarea>
	</s:if>
	<s:if test="#q.questionType == 'Text'">
		<s:textfield name="answer%{#divID}" value="%{#a.answer}" size="30" 
			onchange="saveAnswer('%{#divID}', this);"/>
	</s:if>
	<s:if test="#q.questionType == 'Additional Insured'">
		<s:select list="legalNames" value="%{#a.answer}" name="answer%{#divID}" 
			onchange="saveAnswer('%{#divID}', this);"></s:select>
	</s:if>
	<s:if test="#q.questionType == 'Date'">
		<s:textfield name="answer%{#divID}" value="%{#a.answer}" size="8" 
			onchange="saveAnswer('%{#divID}', this);"/>
			<span style="font-style: italic; font-size: 12px;">example: 12/31/1999</span>
	</s:if>
	<s:if test="#q.questionType == 'License'">
		<s:textfield name="answer%{#divID}" value="%{#a.answer}" size="30" 
			onchange="saveAnswer('%{#divID}', this);"/>
		<s:property value="@com.picsauditing.PICS.pqf.Constants@displayStateLink(#q.question, #a.answer)" escape="false" />			
	</s:if>
	<s:if test="#q.questionType == 'Check Box' || #q.questionType == 'Industry' || #q.questionType == 'Main Work'">
		<s:checkbox fieldValue="X" value="#a.id > 0 && #a.answer.length() == 1" name="answer%{#divID}"
			onclick="saveAnswer('%{#divID}', this);" />
	</s:if>
	<s:if test="#q.questionType == 'Yes/No'">
		<s:radio theme="pics" list="#{'Yes':'Yes','No':'No'}" name="answer%{#divID}" value="%{#a.answer}" 
			onclick="saveAnswer('%{#divID}', this);"></s:radio>
	</s:if>
	<s:if test="#q.questionType == 'Yes/No/NA'">
		<s:radio theme="pics" list="#{'Yes':'Yes','No':'No','NA':'NA'}" name="answer%{#divID}" value="%{#a.answer}" 
			onclick="saveAnswer('%{#divID}', this);"></s:radio>
	</s:if>
	<s:if test="#q.questionType == 'Office Location'">
		<s:radio theme="pics" list="#{'No':'No','Yes':'Yes','Yes with Office':'Yes with Office'}" 
			name="answer%{#divID}" value="%{#a.answer}"
			onclick="saveAnswer('%{#divID}', this);"></s:radio>
	</s:if>
	<s:if test="#q.questionType == 'State'">
		<s:select list="stateList" value="%{#a.answer}" name="answer%{#divID}" 
			onchange="saveAnswer('%{#divID}', this);"></s:select>
	</s:if>
	<s:if test="#q.questionType == 'Country'">
		<s:select list="countryList" value="%{#a.answer}" name="answer%{#divID}" 
			onchange="saveAnswer('%{#divID}', this);"></s:select>
	</s:if>
	<s:if test="#q.questionType == 'Money'">
		<s:textfield name="answer%{#divID}" value="%{#a.answer}" size="19" 
			onchange="saveAnswer('%{#divID}', this);" cssClass="number" />
	</s:if>
	<s:if test="#q.questionType == 'Decimal Number'">
		<s:textfield name="answer%{#divID}" value="%{#a.answer}" size="19" 
			onchange="saveAnswer('%{#divID}', this)" cssClass="number" />
	</s:if>
	<s:if test="#q.questionType == 'Service'">
		<nobr><s:checkbox fieldValue="C" value="%{#a.answer.indexOf('C') != -1}" name="answer%{#divID}_C" 
			onclick="saveAnswer('%{#divID}', this);" /> C</nobr>
		<nobr><s:checkbox fieldValue="S" value="%{#a.answer.indexOf('S') != -1}" name="answer%{#divID}_S" 
			onclick="saveAnswer('%{#divID}', this);" /> S</nobr>
	</s:if>
	<s:if test="#q.questionType == 'Radio'">
		<s:radio theme="pics" list="#q.optionsVisible" listKey="optionName" listValue="optionName" 
			value="#a.answer" name="answer%{#divID}" 
			onclick="saveAnswer('%{#divID}', this);"/>
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
				onclick="showFileUpload('<s:property value="#a.id"/>', <s:property value="#q.id"/>, '<s:property value="#a.parentAnswer.id"/>');"
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
		<s:textfield value="%{#a.comment}" size="30"
			onchange="saveComment('%{#divID}', this);"/>
	</s:if>
</div>

<s:if test="#a.hasRequirements && #a.requirementOpen">
	<br clear="all"/>
	<div id="error"><s:property value="#q.requirement" escape="false"/></div>
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
	
	<input id="verifyButton_<s:property value="#q.id"/>" type="submit" onclick="return verifyAnswer(<s:property value="#q.id"/>, <s:property value="#a.id"/>, '<s:property value="#parentAnswer.id"/>');"
	value="<s:property value="#attr.verifyText"/>" />

	<span id="verify_details_<s:property value="#q.id"/>"
	style='display: <s:property value ="#attr.verifyDetailDisplay"/>;'
	class="verified">Verified on <s:date name="#a.dateVerified"
	format="MMM d, yyyy" /> by <s:property value="#a.auditor.name" /></span>
</s:if>
<br clear="all"/>
