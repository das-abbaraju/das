<%@ taglib prefix="s" uri="/struts-tags"%>
<s:include value="../actionMessages.jsp"/>

<s:form id="textForm">
<fieldset class="form" style="border: none">
	<h2 class="formLegend">Copy To:</h2>
	<ol>
		<li><label>Audit Type:</label>
			<s:select 
				list="auditTypes"
				name="targetID"
				headerKey="0"
				headerValue="- Select an Audit Type -" 
				listKey="id" 
				listValue="auditName"
			/>
		</li>
	</ol>
</fieldset>
<fieldset class="form" style="border: none">
	<h2 class="formLegend">New Category:</h2>
	<ol>
		<li><label>ID:</label>
			<s:if test="category.id > 0">
				<s:property value="category.id" />
			</s:if>
				<s:else>NEW</s:else>
		</li>
		<li><label>Category Name:</label>
			<s:textfield id="catName" name="category.name" size="30" />
		</li>
		<li><label># of Questions:</label>
			<s:property value="category.numQuestions"/>
		</li>
		<li><label># Required:</label>
			<s:property value="category.numRequired"/>
		</li>
		<s:if test="category.auditType.dynamicCategories">
			<li><label>Apply on Question:</label>
				<s:textfield name="applyOnQuestionID" />
				<s:if test="applyOnQuestionID > 0"><a href="ManageQuestion.action?id=<s:property value="applyOnQuestionID" />">Show</a></s:if>
				<div class="fieldhelp">
				<h3>Apply on Question</h3>
				<p>This field is only available on audits with dynamic categories</p>
				</div>
			</li>
			<li><label>When Answer is:</label>
				<s:textfield name="category.applyOnAnswer" />
			</li>
		</s:if>
	</ol>
</fieldset>
</s:form>
