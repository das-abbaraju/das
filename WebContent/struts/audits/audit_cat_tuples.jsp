<%@ taglib prefix="s" uri="/struts-tags"%>
<s:set name="anchorQuestion" value="#q" />
<s:iterator value="answerMap.getAnswerList(#anchorQuestion.id)">
	<s:set name="tupleShaded" value="false" scope="action"/>
	<s:set name="q" value="#anchorQuestion" />
	<s:set name="a" value="[0]" />
	<div id="node_tuple_<s:property value="#a.id"/>" class="tuple">
		<div id="node_<s:property value="#a.id"/>_<s:property value="#q.id"/>" class="question <s:if test="#tupleShaded">shaded</s:if>">
			<s:include value="audit_cat_question.jsp"></s:include>
		</div>
		<s:set name="parentAnswer" value="[0]" />
		<s:iterator value="#anchorQuestion.childQuestions">
			<s:set name="q" value="[0]" />
			<s:set name="a" value="answerMap.get(#q.id, #parentAnswer.id)" />
			<s:if test="#q.isGroupedWithPrevious.toString() == 'No'">
				<s:set name="tupleShaded" value="!#tupleShaded" scope="action"/>
			</s:if>
			<div id="node_<s:property value="#parentAnswer.id"/>_<s:property value="#q.id"/>" class="question <s:if test="#tupleShaded">shaded</s:if>">
				<s:include value="audit_cat_question.jsp"></s:include>
			</div>
		</s:iterator>
		<s:set name="parentAnswer" value="" />
	</div>
</s:iterator>
<s:if test="mode == 'Edit'">
	<div class="tuple">
		<h4 class="groupTitle">Add New</h4>
		<s:set name="q" value="#anchorQuestion" />
		<s:set name="requiredLeft" value="#anchorQuestion.minimumTuples - answerMap.getAnswerList(#anchorQuestion.id).size" />
		<s:if test="#requiredLeft > 0"><span class="required" style="padding-left: 20px;"><s:property value="#requiredLeft"/> more required</span></s:if>
		<s:set name="a" value="" />
		<div class="question <s:if test="#shaded">shaded</s:if>">
			<s:include value="audit_cat_question.jsp"></s:include>
		</div>
	</div>
</s:if>
