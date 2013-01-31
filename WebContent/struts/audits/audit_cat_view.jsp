<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<s:if test="#req == null">
	<s:set value="%{onlyReq}" name="req" />
</s:if>

<s:if test="#req">
	<s:iterator value="categories">
		<s:set value="false" name="req" />
		<s:set name="category" value="key" />
		
		<s:include value="audit_cat_view.jsp" />
	</s:iterator>
</s:if>
<s:else>
	<s:set name="showCat" value="false" />
	
	<div class="audit_category" id="audit_cat_<s:property value="#category.id"/>">
		<h2 id="cathead_<s:property value="#category.id"/>">
			<s:property value="#category.name"/>
			
			<span class="categoryNumber">
				<s:property value="#category.fullNumber"/>
			</span>
		</h2>
		
		<pics:permission perm="ManageAudits">
			<div class="debug debug-summary">
				<h1>Debug Mode</h1>
				
				<s:set name="scoreable" value="conAudit.auditType.scoreable" />
				
				<table cellspacing="0" cellpadding="0" border="0">
					<tr>
						<th>Required</th>
						
						<s:if test="scoreable">
							<th>Score</th>
							<th>Weight</th>
						</s:if>
						
						<th>Override</th>
						<th>Actions</th>
					</tr>
					<tr>
						<td>
							<s:property value="categories.get(#category).requiredCompleted"/>/<s:property value="categories.get(#category).numRequired"/>
						</td>
						
						<s:if test="scoreable">
							<td>
								<s:property value="categories.get(#category).score"/>/<s:property value="categories.get(#category).scorePossible"/>
							</td>
							<td>
								<s:property value="#category.scoreWeight"/>
							</td>
						</s:if>
						
						<td>
							<s:if test="categories.get(#category).override">
								true
							</s:if>
							<s:else>
								false
							</s:else>
						</td>
						<td>
							<pics:permission perm="ManageAudits" type="Edit">
								<a href="ManageCategory.action?id=${category.id}" class="edit"></a>
							</pics:permission>
							
							<pics:permission perm="ManageCategoryRules">
								<a class="filter" href="CategoryRuleTableAjax.action?comparisonRule.auditCategory.id=<s:property value="#category.id" />" rel="CategoryRuleTableAjax.action?comparisonRule.auditCategory.id=<s:property value="#category.id" />"></a>
							</pics:permission>
						</td>
					</tr>
				</table>
			</div>
		</pics:permission>
		
		<s:if test="#category.helpText.exists">
			<div class="helpbox"><s:property value="#category.helpText" escape="false"/></div>
		</s:if>
		
		<s:set name="shaded" value="true" scope="action"/>
		<s:set name="mode" value="mode"/>
		<s:set name="questions" value="#category.getEffectiveQuestions(conAudit.effectiveDate)"/>
		
		<s:if test="#questions.size() > 0">
			<div class="columns-<s:property value="#category.columns" />">
				<s:set name = "questionsPerColumn" value="#questions.size() / (#category.columns)" />
				<s:iterator status="status" begin="1" end="#category.columns">
					<s:set name="begin" value="#status.index * #questionsPerColumn" />
					<s:set name="end" value="(#status.index + 1) * #questionsPerColumn - 1" />
					<s:if test="#status.index + 1 == #category.columns">
						<s:set name="end" value="#questions.size() - 1" />
					</s:if>

					<ul class="column column<s:property value="#status.count" />">
						<s:iterator value="#questions" var="q" begin="#begin" end="#end">
						<li>
							<s:set name="hidden" value="!#q.isVisible(answerMap)" />
							
							<s:if test="previewCat || #q.isValidQuestion(conAudit.validDate)">
								<s:if test="#q.title != null && #q.title.exists && !#hidden">
									<h4 class="groupTitle<s:if test="#hidden"> hide</s:if>" id="title_<s:property value="#q.id"/>">
										<s:property value="#q.title" escape="false"/>
									</h4>
								</s:if>
								
								<s:if test="mode == 'ViewQ'">
									<div class="question<s:if test="shaded"> shaded</s:if>">
										<s:include value="audit_cat_questions.jsp"></s:include>
									</div>
								</s:if>
								
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
								
								<s:if test="#hidden && mode=='ViewAll'" >
									<s:set name="visible" value="false" />
								</s:if>
								
								<s:if test="previewCat || #visible">
									<s:if test="!#q.groupedWithPrevious">
										<s:set name="shaded" value="!#shaded" scope="action"/>
									</s:if>

									<%-- Audit Category Question --%>
									<s:include value="audit_cat_question.jsp"></s:include>
								</s:if>
							</s:if>		
						</li>
						</s:iterator>
					</ul>
				</s:iterator>
			</div>
		</s:if>
		
		<s:iterator value="#category.subCategories" id="category">
			<s:if test="previewCat || isAppliesSubCategory(#category)">
				<s:include value="audit_cat_view.jsp"/>
			</s:if>
		</s:iterator>
			
		<s:if test="!#showCat && onlyReq">
			<script>
				$(function() {
					$('#audit_cat_' + <s:property value="#category.id"/>).hide();
				});
			</script>
		</s:if>
	</div>
</s:else>