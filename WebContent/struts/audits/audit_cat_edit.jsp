<%@ taglib prefix="s" uri="/struts-tags"%>
<s:if test="question.allowMultipleAnswers">
	<tr id="tuple_<s:property value="divId"/>"><td colspan="4" class="tupleHeading">
		<s:if test="id > 0">
			<a href="#" class="remove right" style="font-size: 14px; font-weight: normal; text-decoration: none;" onclick="return false;">Remove</a>
			<s:property value="answer"/>
		</s:if>
		<s:else>
			<a href="#" class="edit right" style="font-size: 14px; font-weight: normal; text-decoration: none;" onclick="return false;">Add</a>
			Add New
		</s:else>
		</td></tr>
</s:if>
<tr id="status_<s:property value="divId"/>" class="group<s:if test="#shaded">Shaded</s:if>">
	<td class="right"><s:property value="category.number"/>.<s:property value="question.subCategory.number"/>.<s:property value="question.number"/>&nbsp;&nbsp;
	<a name="q<s:property value="divId"/>" />&nbsp;&nbsp;
	<br/><s:div id="thinking_%{divId}"></s:div>
	</td>
	<td class="question<s:if test="required">Required</s:if>"><s:property value="question.question" escape="false"/>
		<s:if test="question.linkUrl1.length() > 0"><a href="http://<s:property value="question.linkUrl1"/>" target="_BLANK" title="opens in new window"><s:property value="linkText1"/></a></s:if>
		<s:if test="question.linkUrl2.length() > 0"><a href="http://<s:property value="question.linkUrl2"/>" target="_BLANK" title="opens in new window"><s:property value="linkText2"/></a></s:if>
		<s:if test="question.linkUrl3.length() > 0"><a href="http://<s:property value="question.linkUrl3"/>" target="_BLANK" title="opens in new window"><s:property value="linkText3"/></a></s:if>
		<s:if test="question.linkUrl4.length() > 0"><a href="http://<s:property value="question.linkUrl4"/>" target="_BLANK" title="opens in new window"><s:property value="linkText4"/></a></s:if>
		<s:if test="question.linkUrl5.length() > 0"><a href="http://<s:property value="question.linkUrl5"/>" target="_BLANK" title="opens in new window"><s:property value="linkText5"/></a></s:if>
		<s:if test="question.linkUrl6.length() > 0"><a href="http://<s:property value="question.linkUrl6"/>" target="_BLANK" title="opens in new window"><s:property value="linkText6"/></a></s:if>
	</td>
	<td class="answer" id="td_answer_<s:property value="divId"/>">
		<s:if test="question.questionType == 'Text'">
			<s:textfield id="answer_%{id}" name="answer" size="30" onchange="javascript:saveAnswer(%{question.id}, '%{parentAnswer.id}', this);"/>
		</s:if>
		<s:if test="question.questionType == 'License'">
			<s:textfield name="answer" size="30" onchange="javascript:saveAnswer(%{question.id}, '%{parentAnswer.id}', this);"/>
			<s:property value="@com.picsauditing.PICS.pqf.Constants@displayStateLink(question, answer)" escape="false" />			
		</s:if>
		<s:if test="question.questionType == 'Date'">
			<nobr><s:textfield id="answer_%{id}" name="answer" size="8" 
			onchange="javascript:saveAnswer(%{question.id}, '%{parentAnswer.id}', this);"/> <span style="font-style: italic; font-size: 12px;">example: 12/31/1999</span>
		</s:if>
		<s:if test="question.questionType == 'Check Box' || questionType == 'Industry' || questionType == 'Main Work'">
			<s:checkbox fieldValue="X" value="id > 0 && answer.length() == 1" name="answer" onclick="javascript:saveAnswer(%{question.id}, '%{parentAnswer.id}', this);" />
		</s:if>
		<s:if test="question.questionType == 'Yes/No'">
			<s:radio theme="pics" list="#{'Yes':'Yes','No':'No'}" value="answer" name="question_%{id}" onclick="javascript:saveAnswer(%{question.id}, '%{parentAnswer.id}', this);"></s:radio>
		</s:if>
		<s:if test="question.questionType == 'Yes/No/NA'">
			<s:radio theme="pics" list="#{'Yes':'Yes','No':'No','NA':'NA'}" value="answer" name="question_%{id}" onclick="javascript:saveAnswer(%{question.id}, '%{parentAnswer.id}', this);"></s:radio>
		</s:if>
		<s:if test="question.questionType == 'Manual'">
			<s:radio theme="pics" list="#{'Yes':'Yes','No':'No','NA':'NA'}" value="answer" name="question_%{id}" onclick="javascript:saveAnswer(%{question.id}, '%{parentAnswer.id}', this);"></s:radio>
			<br>Comments: <s:textfield id="comments_%{id}" name="comment" size="30" onblur="javascript:saveComment(%{id}, this);"/>
		</s:if>
		<s:if test="question.questionType == 'Office Location'">
			<s:radio theme="pics" list="#{'No':'No','Yes':'Yes','Yes with Office':'Yes with Office'}" value="%{answer}"  name="question_%{id}" onclick="javascript:saveAnswer(%{question.id}, '%{parentAnswer.id}', this);"></s:radio>
		</s:if>
		<s:if test="question.questionType == 'State'">
			<s:select list="stateList" value="answer" name="question_%{id}" onchange="javascript:saveAnswer(%{question.id}, '%{parentAnswer.id}', this);"></s:select>
		</s:if>
		<s:if test="question.questionType == 'Country'">
			<s:select list="countryList" value="answer" name="question_%{id}" onchange="javascript:saveAnswer(%{question.id}, '%{parentAnswer.id}', this);"></s:select>
		</s:if>
		<s:if test="question.questionType == 'Money'">
			<s:textfield name="%{'answer_answer_'.concat(id)}" value="%{answer}" size="19" onchange="validateNumber('answer_answer_%{id}','Question %{number}');  document.MM_returnValue && saveAnswer(%{question.id}, '%{parentAnswer.id}', this)" />
		</s:if>
		<s:if test="question.questionType == 'Decimal Number'">
			<s:textfield name="%{'answer_answer_'.concat(id)}" value="%{answer}" size="19" onchange="var temp = parseFloat($('%{'answer_answer_'.concat(id)}').value).toFixed(3); if( temp == 'NaN' ) temp = ''; $('%{'answer_answer_'.concat(id)}').value = temp; validateDecimal('answer_answer_%{id}','Question %{number}'); document.MM_returnValue && saveAnswer(%{question.id}, '%{parentAnswer.id}', this)" />
		</s:if>
		<s:if test="question.questionType == 'Service'">
			<nobr><s:checkbox fieldValue="C" value="answer.indexOf('C') != -1" name="question_%{id}_C" onclick="javascript:saveAnswer(%{question.id}, '%{parentAnswer.id}', this);" /> C</nobr>
			<nobr><s:checkbox fieldValue="S" value="answer.indexOf('S') != -1" name="question_%{id}_S" onclick="javascript:saveAnswer(%{question.id}, '%{parentAnswer.id}', this);" /> S</nobr>
		</s:if>
		<s:if test="question.questionType == 'Radio'">
			<s:radio theme="pics" list="question.options" listKey="optionName" listValue="optionName" 
				value="answer" name="question_%{id}" 
				onclick="javascript:saveAnswer(%{question.id}, '%{parentAnswer.id}', this);"/>
		</s:if>
		<s:if test="question.questionType.startsWith('File')">
			<nobr>
				<s:if test="id > 0 && answer.length() > 0">
					<a href="DownloadAuditData.action?auditID=<s:property value="auditID"/>&answer.id=<s:property value="id"/>" target="_BLANK">View File</a>
				</s:if>
				<s:else>File Not Uploaded</s:else>
				<s:if test="catDataID > 0">
				<input id="show_button_<s:property value="question.id"/>" type="button" 
					value="<s:if test="id > 0 && answer.length() > 0">Edit</s:if><s:else>Add</s:else> File" 
					onclick="showFileUpload(<s:property value="question.id"/>, '<s:property value="parentAnswer.id"/>');"
					title="Opens in new window (please disable your popup blocker)" />
				</s:if>
			</nobr>
		</s:if>
	</td>
	<td align="center" id="required_td<s:property value="divId"/>">
		<s:if test="required && (answer == null || answer.length() < 1)">
			<span class="redMain">*</span>
		</s:if>
	</td>
</tr>
<s:if test="question.questionType == 'Text Area'">
	<tr class="group<s:if test="#shaded">Shaded</s:if>"><td>&nbsp;</td><td colspan="3" class="right">
	<s:textarea cols="70" rows="4" value="%{answer}" onchange="javascript:saveAnswer(%{divId}, this);">
	</s:textarea>
	</td>
	</tr>
</s:if>

<s:if test="hasRequirements">
	<tr class="group<s:if test="#shaded">Shaded</s:if>">
		<td class="center">Requirement</td>
		<td colspan="3">Status:
		<s:if test="requirementOpen">
			<span class="unverified">Open</span>
		</s:if>
		<s:else>
			<span class="verified">Closed on <s:date name="dateVerified" format="MMM d, yyyy" /></span>
		</s:else>
		
		<br>
		<s:if test="requirement.length() > 0">
			<s:if test="requirementOpen">
				<div id="alert"><s:property value="requirement"/></div>
			</s:if>
			<s:else>
				<span class="answer"><s:property value="requirement"/></span>
			</s:else>
		</s:if>
		</td>
	</tr>
</s:if>
