<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

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
		<h2 id="cathead_<s:property value="#category.id"/>">
			<s:property value="#category.name"/>
			<span class="debug">
			<pics:permission perm="ManageAudits" type="Edit">
				<a href="ManageCategory.action?id=<s:property value="#category.id"/>" class="edit"></a>
			</pics:permission>
			<pics:permission perm="ManageCategoryRules">
				<a class="filter" 
					href="CategoryRuleTableAjax.action?comparisonRule.auditCategory.id=<s:property value="#category.id" />" 
					rel="CategoryRuleTableAjax.action?comparisonRule.auditCategory.id=<s:property value="#category.id" />"></a>
			</pics:permission>
			</span>
			<span class="categoryNumber"><s:property value="#category.fullNumber"/></span>
		</h2>
		<pics:permission perm="DevelopmentEnvironment">
			<span class="debug">
				Required=<s:property value="categories.get(#category).requiredCompleted"/>/<s:property value="categories.get(#category).numRequired"/>
				<s:if test="conAudit.auditType.scoreable">
					Score=<s:property value="categories.get(#category).score"/>/<s:property value="categories.get(#category).scorePossible"/>,
					Weight=<s:property value="#category.scoreWeight"/>
				</s:if>
				<s:if test="categories.get(#category).override">
				Override
				</s:if>
			</span>
		</pics:permission>
		<s:if test="#category.sha">
			<s:include value="audit_cat_sha.jsp"></s:include>
		</s:if>
		<s:else>
			<s:if test="#category.helpText.length() > 0">
				<div class="helpbox"><s:property value="#category.helpText" escape="false"/></div>
			</s:if>
			<s:set name="shaded" value="true" scope="action"/>
			<s:set name="mode" value="mode"/>

			<s:iterator value="#category.questions" id="q">
				<s:set name="hidden" value="!#q.isVisible(answerMap)" />
				<s:if test="previewCat || #q.isValidQuestion(conAudit.validDate)">
					<s:if test="title != null && title.length() > 0">
						<h4 class="groupTitle<s:if test="#hidden"> hide</s:if>" id="title_<s:property value="#q.id"/>">
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
					<s:set name="visible" value="#q.isCurrent(conAudit.validDate)"/>
					<s:if test="onlyReq && !#a.hasRequirements">
						<s:set name="visible" value="false" />
					</s:if>
					<s:else>
						<s:set name="showCat" value="true" />
					</s:else>
					<s:if test="!viewBlanks && (#a == null || #a.answer == null || #a.answer.length() == 0)">
						<s:set name="visible" value="false" />
					</s:if>
					<s:if test="previewCat || #visible">
						<s:if test="!#q.groupedWithPrevious">
							<s:set name="shaded" value="!#shaded" scope="action"/>
						</s:if>

						<div id="node_<s:property value="#q.id"/>" class="clearfix question<s:if test="#shaded"> shaded</s:if><s:if test="#hidden"> hide</s:if><s:if test="#q.dependentRequired.size() > 0"> hasDependentRequired</s:if><s:if test="#q.dependentVisible.size() > 0"> hasDependentVisible</s:if><s:if test="#q.auditCategoryRules.size() > 0"> hasDependentRules</s:if><s:if test="affectsAudit"> affectsAudit</s:if><s:if test="#q.functionWatchers.size > 0"> hasFunctions</s:if>">
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