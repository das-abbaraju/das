<%@ taglib prefix="s" uri="/struts-tags"%>
<tr id="status_<s:property value="questionID"/>" class="group<s:if test="#shaded">Shaded</s:if>">
	<td class="right"><s:property value="category.number"/>.<s:property value="subCategory.number"/>.<s:property value="number"/>&nbsp;&nbsp;
	<a name="q<s:property value="questionID"/>" /></td>
	<td class="question<s:if test="required">Required</s:if>"><s:property value="question" escape="false"/>
		<s:if test="linkUrl1.length() > 0"><a href="http://<s:property value="linkUrl1"/>" target="_BLANK" title="opens in new window"><s:property value="linkText1"/></a></s:if>
		<s:if test="linkUrl2.length() > 0"><a href="http://<s:property value="linkUrl2"/>" target="_BLANK" title="opens in new window"><s:property value="linkText2"/></a></s:if>
		<s:if test="linkUrl3.length() > 0"><a href="http://<s:property value="linkUrl3"/>" target="_BLANK" title="opens in new window"><s:property value="linkText3"/></a></s:if>
		<s:if test="linkUrl4.length() > 0"><a href="http://<s:property value="linkUrl4"/>" target="_BLANK" title="opens in new window"><s:property value="linkText4"/></a></s:if>
		<s:if test="linkUrl5.length() > 0"><a href="http://<s:property value="linkUrl5"/>" target="_BLANK" title="opens in new window"><s:property value="linkText5"/></a></s:if>
		<s:if test="linkUrl6.length() > 0"><a href="http://<s:property value="linkUrl6"/>" target="_BLANK" title="opens in new window"><s:property value="linkText6"/></a></s:if>
	</td>
	<td class="answer" id="td_answer_<s:property value="questionID"/>">
		<s:if test="questionType == 'Text'">
			<s:textfield id="answer_%{questionID}" name="answer.answer" size="30" onchange="javascript:saveAnswer(%{questionID}, this);"/>
		</s:if>
		<s:if test="questionType == 'License'">
			<s:textfield name="answer.answer" size="30" onchange="javascript:saveAnswer(%{questionID}, this);"/>
			<s:property value="@com.picsauditing.PICS.pqf.Constants@displayStateLink(question, answer.answer)" escape="false" />			
		</s:if>
		<s:if test="questionType == 'Date'">
			<nobr><s:textfield id="answer_%{questionID}" name="answer.answer" size="8" 
			onchange="javascript:saveAnswer(%{questionID}, this);"/> <span style="font-style: italic; font-size: 12px;">example: 12/31/1999</span>
		</s:if>
		<s:if test="questionType == 'Check Box' || questionType == 'Industry' || questionType == 'Main Work'">
			<s:checkbox fieldValue="X" value="answer.answer.length() == 1" name="answer.answer" onclick="javascript:saveAnswer(%{questionID}, this);" />
		</s:if>
		<s:if test="questionType == 'Yes/No'">
			<s:radio theme="pics" list="#{'Yes':'Yes','No':'No'}" value="answer.answer" name="question_%{questionID}" onclick="javascript:saveAnswer(%{questionID}, this);"></s:radio>
		</s:if>
		<s:if test="questionType == 'Yes/No/NA'">
			<s:radio theme="pics" list="#{'Yes':'Yes','No':'No','NA':'NA'}" value="answer.answer" name="question_%{questionID}" onclick="javascript:saveAnswer(%{questionID}, this);"></s:radio>
		</s:if>
		<s:if test="questionType == 'Office'">
			<s:radio theme="pics" list="#{'Yes':'Yes','No':'No','NA':'NA'}" value="answer.answer" name="question_%{questionID}" onclick="javascript:saveAnswer(%{questionID}, this);"></s:radio>
		</s:if>
		<s:if test="questionType == 'Manual'">
			<s:radio theme="pics" list="#{'Yes':'Yes','No':'No','NA':'NA'}" value="answer.answer" name="question_%{questionID}" onclick="javascript:saveAnswer(%{questionID}, this);"></s:radio>
			<br>Comments: <s:textfield id="comments_%{questionID}" name="answer.comment" size="30" onblur="javascript:saveComment(%{questionID}, this);"/>
		</s:if>
		<s:if test="questionType == 'Office Location'">
			<s:radio theme="pics" list="#{'No':'No','Yes':'Yes','Yes with Office':'Yes with Office'}" value="%{answer.answer}"  name="question_%{questionID}" onclick="javascript:saveAnswer(%{questionID}, this);"></s:radio>
		</s:if>
		<s:if test="questionType == 'State'">
			<s:select list="stateList" value="answer.answer" name="question_%{questionID}" onchange="javascript:saveAnswer(%{questionID}, this);"></s:select>
		</s:if>
		<s:if test="questionType == 'Country'">
			<s:select list="countryList" value="answer.answer" name="question_%{questionID}" onchange="javascript:saveAnswer(%{questionID}, this);"></s:select>
		</s:if>
		<s:if test="questionType == 'Money'">
			<s:textfield name="%{'answer_answer_'.concat(questionID)}" value="%{answer.answer}" size="19" onblur="validateNumber('answer_answer_%{questionID}','Question %{number}');  document.MM_returnValue && saveAnswer(%{questionID}, this)" />
		</s:if>
		<s:if test="questionType == 'Decimal Number'">
			<s:textfield name="%{'answer_answer_'.concat(questionID)}" value="%{answer.answer}" size="19" onblur="$('%{'answer_answer_'.concat(questionID)}').value = parseFloat($('%{'answer_answer_'.concat(questionID)}').value).toFixed(3); validateDecimal('answer_answer_%{questionID}','Question %{number}'); document.MM_returnValue && saveAnswer(%{questionID}, this)" />
		</s:if>
		<s:if test="questionType == 'Service'">
			<nobr><s:checkbox fieldValue="C" value="answer.answer.indexOf('C') != -1" name="question_%{questionID}_C" onclick="javascript:saveAnswer(%{questionID}, this);" /> C</nobr>
			<nobr><s:checkbox fieldValue="S" value="answer.answer.indexOf('S') != -1" name="question_%{questionID}_S" onclick="javascript:saveAnswer(%{questionID}, this);" /> S</nobr>
		</s:if>
		<s:if test="questionType == 'Radio'">
			<s:radio theme="pics" list="options" listKey="optionName" listValue="optionName" value="answer.answer" name="question_%{questionID}" onclick="javascript:saveAnswer(%{questionID}, this);"></s:radio>
		</s:if>
		<s:if test="questionType == 'Drop Down'">
			<% // TODO We never actually use Drop Down on any questions. We should evaluate if we want to ever support them. %>
			<s:select list="options" listKey="optionName" listValue="optionName" value="answer.answer" name="question_%{questionID}" onchange="javascript:saveAnswer(%{questionID}, this);"></s:select>
		</s:if>
		<s:if test="questionType == 'File'">
			<nobr>
				<s:if test="answer.answer.length() > 0">
					<a href="#" onClick="openQuestion('<s:property value="questionID"/>', '<s:property value="answer.answer"/>'); return false;">View File</a>
				</s:if>
				<s:else>File Not Uploaded</s:else>
				<s:if test="catDataID > 0">
				<input id="show_button_<s:property value="questionID"/>" type="button" 
					value="<s:if test="answer.answer.length() > 0">Edit</s:if><s:else>Add</s:else> File" 
					onclick="showFileUpload(<s:property value="questionID"/>);"
					title="Opens in new window (please disable your popup blocker)" />
				</s:if>	
			</nobr>
		</s:if>
	</td>
	<td align="center" id="required_td<s:property value="questionID"/>">
		<s:if test="required && (answer == null || answer.answer.length() < 1)">
			<span class="redMain">*</span>
		</s:if>
	</td>
</tr>
<s:if test="questionType == 'Text Area'">
	<tr class="group<s:if test="#shaded">Shaded</s:if>"><td>&nbsp;</td><td colspan="3" class="right">
	<s:textarea cols="70" rows="4" value="%{answer.answer}" onchange="javascript:saveAnswer(%{questionID}, this);">
	</s:textarea>
	</td>
	</tr>
</s:if>
<s:if test="answer.unverified && answer.verifiedAnswer.length() > 0">
	<tr class="group<s:if test="#shaded">Shaded</s:if>">
	<td colspan="4"><span class="verified">Answer changed to <s:property value="answer.verifiedAnswer"/> on <s:date name="answer.dateVerified" format="MMM d, yyyy" />
	</span></td></tr>
</s:if>
<s:if test="answer.hasRequirements && conAudit.auditType.hasRequirements">
	<tr class="group<s:if test="#shaded">Shaded</s:if>">
		<td class="center">Requirement</td>
		<td colspan="3">Status:
		<s:if test="answer.requirementOpen">
			<span class="unverified">Open</span>
		</s:if>
		<s:else>
			<span class="verified">Closed on <s:date name="answer.dateVerified" format="MMM d, yyyy" /></span>
		</s:else>
		
		<br>
		<s:if test="requirement.length() > 0">
			<s:if test="answer.requirementOpen">
				<div id="alert"><s:property value="requirement"/></div>
			</s:if>
			<s:else>
				<span class="answer"><s:property value="requirement"/></span>
			</s:else>
		</s:if>
		</td>
	</tr>
</s:if>
