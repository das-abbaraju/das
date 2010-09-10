<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>

<s:if test="#category == null"><s:set name="category" value="categoryData.category"/></s:if>

<div class="audit_category">
	<s:if test="#category.sha">
		<s:include value="audit_cat_sha.jsp"></s:include>
	</s:if>
	<s:else>
		<h2><s:property value="#category.fullNumber"/>. <s:property value="#category.name"/></h2>
		<s:set name="shaded" value="true" scope="action"/>
		<s:iterator value="#category.questions">
			<s:if test="title != null && title.length() > 0">
				<h4 class="groupTitle">
					<s:property value="title" escape="false"/>
				</h4>
			</s:if>
			<s:if test="mode == 'ViewQ'">
				<div class="question<s:if test="shaded"> shaded</s:if>">
					<s:include value="audit_cat_questions.jsp"></s:include>
				</div>
			</s:if>
			<s:set name="q" value="[0]" />
			<!-- Single Leaf Question -->
			<s:set name="a" value="answerMap.get(#q.id)" />
			<s:set name="visibleAnswer" value="answerMap.get(#q.requiredQuestion.id)" />
			<s:set name="visible" value="#q.visibleQuestion == null || #q.visibleAnswer == #visibleAnswer.answer" />
			<s:if test="onlyReq && !#a.hasRequirements">
				<s:set name="visible" value="false" />
			</s:if>
			<s:if test="!viewBlanks && (#a == null || #a.answer == null || #a.answer.length() == 0)">
				<s:set name="visible" value="false" />
			</s:if>
			<s:if test="#visible">
				<s:if test="!#q.groupedWithPrevious">
					<s:set name="shaded" value="!#shaded" scope="action"/>
				</s:if> 
				
				<div id="node_<s:property value="#q.id"/>" class="clearfix question<s:if test="#shaded"> shaded</s:if>">
					<s:include value="audit_cat_question.jsp"></s:include>
				</div>
			</s:if>
		</s:iterator>
		<s:iterator value="#category.subCategories" id="category">
			<s:include value="audit_cat_view.jsp"/>
			<s:if test="helpText != null && helpText.length() > 0">
				<div class="fieldhelp"><s:property value="helpText" escape="false"/></div>
			</s:if> 
		</s:iterator>
	</s:else>
</div>