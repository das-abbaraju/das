<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>

<s:if test="#req==null">
	<s:set value="%{onlyReq}" name="req" />
</s:if>
<s:if test="#req">
	<s:iterator value="categories">
		<s:if test="value.applies">
			<s:set value="false" name="req" />
			<s:set name="category" value="key" />
			<s:include value="audit_cat_view.jsp" />
		</s:if> 
	</s:iterator>
</s:if>
<s:else>
	<s:set name="showCat" value="false" />
	<div class="audit_category" id="audit_cat_<s:property value="#category.id"/>">
		<s:if test="#category.sha">
			<s:include value="audit_cat_sha.jsp"></s:include>
		</s:if>
		<s:else>
			<div class="categoryNumber"><s:property value="#category.fullNumber"/></div>
			<h2 id="cathead_<s:property value="#category.id"/>">
				<s:property value="#category.name"/>
				<pics:permission perm="ManageAudits" type="Edit"><a href="ManageCategory.action?id=<s:property value="#category.id"/>"><img src="images/edit_pencil.png" title="Manage Category"></a></pics:permission>
			</h2>
			<s:if test="#category.helpText != null && #category.helpText.length() > 0">
				<div class="info"><s:property value="#category.helpText" escape="false"/></div>
			</s:if> 
			<s:set name="shaded" value="true" scope="action"/>
			<s:iterator value="#category.questions" id="q">
				<s:if test="previewCat || #q.isValidQuestion(conAudit.validDate)">
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
					<!-- Single Leaf Question -->
					<s:set name="a" value="answerMap.get(#q.id)" />
					<s:set name="visibleAnswer" value="answerMap.get(#q.visibleQuestion.id)" />
					<s:set name="hidden" value="#q.visibleQuestion != null && #q.visibleAnswer != #visibleAnswer.answer" />
					<s:set name="visible" value="#q.current"/>
					<s:if test="onlyReq && !#a.hasRequirements">
						<s:set name="visible" value="false" />
					</s:if>
					<s:else>
						<s:set name="showCat" value="true" />				
					</s:else>
					<s:if test="!viewBlanks && (#a == null || #a.answer == null || #a.answer.length() == 0)">
						<s:set name="visible" value="false" />
					</s:if>
					<s:if test="#visible">
						<s:if test="!#q.groupedWithPrevious">
							<s:set name="shaded" value="!#shaded" scope="action"/>
						</s:if> 
						
						<div id="node_<s:property value="#q.id"/>" class="clearfix question<s:if test="#shaded"> shaded</s:if><s:if test="#hidden"> hide</s:if><s:if test="#q.dependentRequired.size() > 0"> hasDependentRequired</s:if><s:if test="#q.dependentVisible.size() > 0"> hasDependentVisible</s:if><s:if test="#q.auditCategoryRules.size() > 0"> hasDependentRules</s:if>">
							<s:include value="audit_cat_question.jsp"></s:include>
						</div>
					</s:if>
				</s:if>
			</s:iterator>
			<s:iterator value="#category.subCategories" id="category">
				<s:if test="previewCat || isAppliesSubCategory(#category)">
					<s:include value="audit_cat_view.jsp"/>
				</s:if>
			</s:iterator>	
		</s:else>
			<s:if test="!#showCat && onlyReq">	
				<script>
					$(function() {
						$('#audit_cat_'+<s:property value="#category.id"/>).hide();
					});
				</script>
			</s:if>
	</div>
</s:else>