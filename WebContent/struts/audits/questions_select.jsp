<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>

<s:if test="questions.size > 0">
	<s:select name="questions[99].id" list="{}" cssClass="forms">
		<s:iterator value="questionMap" var="q">
			<s:optgroup label="%{#q.key.name}" list="#q.value" listKey="id" listValue="%{@com.picsauditing.PICS.Utilities@stripTags(name)}" />
		</s:iterator>
	</s:select>
	<br clear="all" />
	<s:text name="QuestionAnswerSearch.label.Criteria" />
	<s:select name="questions[99].criteria" value="" 
		list="#{'':getTextNullSafe('QuestionAnswerSearch.status.NoCriteria'),
			'=':getTextNullSafe('QuestionAnswerSearch.status.EqualTo'),
			'!=':getTextNullSafe('QuestionAnswerSearch.status.NotEqualTo'),
			'>':getTextNullSafe('QuestionAnswerSearch.status.GreaterThan'),
			'>=':getTextNullSafe('QuestionAnswerSearch.status.GreaterOrEqualThan'),
			'<':getTextNullSafe('QuestionAnswerSearch.status.LessThan'),
			'<=':getTextNullSafe('QuestionAnswerSearch.status.LessOrEqualThan'),
			'Contains':getTextNullSafe('QuestionAnswerSearch.status.Contains'),
			'Begins With':getTextNullSafe('QuestionAnswerSearch.status.BeginsWith'),
			'Ends With':getTextNullSafe('QuestionAnswerSearch.status.EndsWith')}"
		cssClass="forms" />
	<s:text name="Filters.label.Answer" />
	<s:textfield cssClass="forms" id="answer" name="questions[99].criteriaAnswer" size="25" value=""  />
	<div>
		<input class="picsbutton positive" type="submit" value="<s:text name="button.Add" />" />
	</div>
</s:if>
<s:else>
	<s:text name="QuestionAnswerSearch.message.NoQuestionsMatching" />
</s:else>
