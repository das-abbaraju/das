<%@ taglib prefix="s" uri="/struts-tags"%>
<tr id="status_<s:property value="questionID"/>" class="group<s:if test="#shaded">Shaded</s:if>">
	<td class="right"><s:property value="category.number"/>.<s:property value="subCategory.number"/>.<s:property value="number"/>&nbsp;&nbsp;</td>
	<td class="question<s:if test="required">Required</s:if>"><s:property value="question" escape="false"/>
		<s:if test="linkUrl1 > ''"><a href="http://<s:property value="linkUrl1"/>" target="_BLANK" title="opens in new window"><s:property value="linkText1"/></a></s:if>
		<s:if test="linkUrl2 > ''"><a href="http://<s:property value="linkUrl2"/>" target="_BLANK" title="opens in new window"><s:property value="linkText2"/></a></s:if>
		<s:if test="linkUrl3 > ''"><a href="http://<s:property value="linkUrl3"/>" target="_BLANK" title="opens in new window"><s:property value="linkText3"/></a></s:if>
		<s:if test="linkUrl4 > ''"><a href="http://<s:property value="linkUrl4"/>" target="_BLANK" title="opens in new window"><s:property value="linkText4"/></a></s:if>
		<s:if test="linkUrl5 > ''"><a href="http://<s:property value="linkUrl5"/>" target="_BLANK" title="opens in new window"><s:property value="linkText5"/></a></s:if>
		<s:if test="linkUrl6 > ''"><a href="http://<s:property value="linkUrl6"/>" target="_BLANK" title="opens in new window"><s:property value="linkText6"/></a></s:if>
	</td>
	<td class="answer">
		<s:if test="questionType == 'Text'">
			<s:textfield id="answer_%{questionID}" name="answer.answer" size="30" onchange="javascript:saveAnswer(%{questionID}, this);"/>
		</s:if>
		<s:if test="questionType == 'License'">
			<s:textfield name="answer.answer" size="30" onchange="javascript:saveAnswer(%{questionID}, this);"/>
			<s:property value="@com.picsauditing.PICS.pqf.Constants@displayStateLink(question, answer.answer)" escape="false" />			
		</s:if>
		<s:if test="questionType == 'Date'">
			<nobr><s:textfield id="answer_%{questionID}" name="answer.answer" size="8" 
			onclick="cal1.select($('answer_%{questionID}'),'answer_%{questionID}','yyyy-MM-dd','%{answer.answer}'); return false;"
			onblur="javascript:saveAnswer(%{questionID}, this);"/>
			<input type="image" src="images/icon_calendar.gif" width="18" height="15" onclick="cal1.select($('answer_<s:property value="questionID"/>'),'answer_<s:property value="questionID"/>','yyyy-MM-dd','<s:property value="answer.answer"/>'); return false;"/>
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
			<br>Comments: <s:textfield id="comments_%{questionID}" name="answer.comment" size="30" onblur="javascript:saveAnswer(%{questionID}, document.getElementById('question_%{questionID}Yes') );"/>
		</s:if>
		<s:if test="questionType == 'Office Location'">
			<s:radio theme="pics" list="#{'No':'No','Yes':'Yes','Yes with Office':'Yes with Office'}" value="answer.answer" name="question_%{questionID}" onclick="javascript:saveAnswer(%{questionID}, this);"></s:radio>
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
			<s:textfield name="%{'answer_answer_'.concat(questionID)}" value="%{answer.answer}" size="19" onblur="validateDecimal('answer_answer_%{questionID}','Question %{number}');  document.MM_returnValue && saveAnswer(%{questionID}, this)" />
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
			<span id="status_upload_<s:property value="questionID"/>">&nbsp;</span>
			<div id="show_upload_<s:property value="questionID"/>">
				<span id="meta_upload_<s:property value="questionID"/>">
				<nobr>
					<s:if test="answer.answer.length() > 0">
						<a id="link_<s:property value="questionID"/>" href="#" onClick="openQuestion('<s:property value="questionID"/>', '<s:property value="answer.answer"/>'); return false;">View File</a>
					</s:if>
					<s:else>File Not Uploaded</s:else>
				</nobr>
				</span>
				<input id="show_button_<s:property value="questionID"/>" type="button" value="<s:if test="answer.answer.length() > 0">Edit</s:if><s:else>Add</s:else> File" onclick="$('file_upload_<s:property value="questionID"/>').show(); $('show_upload_<s:property value="questionID"/>').hide();" />
			</div>
			<form id="file_upload_<s:property value="questionID"/>" target="upload_iframe_<s:property value="questionID"/>" action="AuditDataFileUploadAjax.action" method="post" enctype="multipart/form-data" style="display: none;">
				<s:hidden name="contractorID" value="%{contractor.id}"/>
				<s:hidden name="auditData.audit.id" value="%{conAudit.id}"/>
				<s:hidden name="auditData.question.questionID" value="%{questionID}"/>
				<input type="file" name="file" value="Upload file (Max 150 MB)" accept=".pdf,.doc,.txt,.xls,.jpg" />
				<input type="submit" value="Upload File"  onclick="AIM.submit('<s:property value="questionID"/>', {'onStart' : startCallback, 'onComplete' : completeCallback});" />
				<iframe style="display:none" src="about:blank" id="upload_iframe_<s:property value="questionID"/>" 
						name="upload_iframe_<s:property value="questionID"/>" id="upload_iframe_<s:property value="questionID"/>" onload="AIM.loaded('<s:property value="questionID"/>')" ></iframe>
			</form>

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
