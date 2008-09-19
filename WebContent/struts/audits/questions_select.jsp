<%@ taglib prefix="s" uri="/struts-tags"%>
<s:if test="questions.size > 0">
	<s:select cssClass="forms" name="questions[99].questionID" list="questions" listKey="questionID" listValue="shortQuestion" value="">
	</s:select>
	<br clear="all" />
	Criteria
		<s:select name="questions[99].criteria" value="" list="#{'':'No Criteria','=':'Equal To','!=':'Not Equal To','>':'Greater Than','>=':'Greater Or Equal Than','<':'Less Than','<=':'Less Or Equal Than','Contains':'Contains','Begins With':'Begins With','Ends With':'Ends With'}" cssClass="forms"/>
	Answer
		<s:textfield cssClass="forms" id="answer" name="questions[99].answer.answer" size="25" value=""  />
	<div class="buttons">
		<button class="positive" type="submit" name="button" value="Add">Add</button>
	</div>
</s:if>
<s:else>
No questions matching this text
</s:else>
