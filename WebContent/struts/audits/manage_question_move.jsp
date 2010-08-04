<%@ taglib prefix="s" uri="/struts-tags"%>
<s:include value="../actionMessages.jsp"/>

<script type="text/javascript" src="js/jquery/jquery.fieldfocus.js"></script>
<script>
$(function(){
	$('#audit_subcategory_autocomplete').autocomplete('AuditSubCategorySuggestAjax.action',
	{
		minChars: 3,
		formatResult: function(data,i,count) {
			return data[1];
		}
	})
});
</script>
<s:form id="textForm">

	<s:hidden name="id" />
	<s:hidden name="parentID" value="%{subCategory.id}" />
	<s:hidden name="subCategory.id" />

	<fieldset class="form" style="border: none">
		<h2 class="formLegend">Copy To:</h2>
		<ol>
			<li><label>Category:</label>
				<s:textfield id="audit_subcategory_autocomplete" name="targetID" value="%{#a.answer}" size="50" />
			</li>
		</ol>
	</fieldset>

	<fieldset class="form">
	<h2 class="formLegend">Question</h2>
	<ol>
		<li><label>ID:</label>
			<s:if test="question.id > 0">
				<s:property value="question.id" />
			</s:if>
			<s:else>
				NEW
			</s:else>
		</li>
		<li><label>Text:</label>
			<s:if test="question.id > 0">
				<table class="report" style="clear: none">
				<thead>
					<tr>
						<th>Locale</th>
						<th>Question</th>
					</tr>
				</thead>
					<s:iterator value="question.questionTexts">
					<tr>
						<td><s:property value="locale"/></td>
						<td><s:property value="question"/></td>
					</tr>
					</s:iterator>
				</table>
			</s:if>
			<s:else>
				<s:textarea disabled="true" name="defaultQuestion" rows="3" cols="65"/>
			</s:else>
		</li>
		<li><label>Effective Date:</label>
			<s:textfield disabled="true" name="question.effectiveDate" value="%{ question.effectiveDate && getText('short_dates', {question.effectiveDate})}"/>
		</li>
		<li><label>Expiration Date:</label>
			<s:textfield disabled="true" name="question.expirationDate" value="%{ question.expirationDate && getText('short_dates', {question.expirationDate})}"/>
		</li>
		<s:if test="question.id > 0">
			<li><label>Added:</label>
				<s:date name="question.creationDate" />
			</li>
			<li><label>Updated:</label>
				<s:date name="question.updateDate" />
			</li>
		</s:if>
		<li><label>Column Header:</label>
			<s:textfield disabled="true" name="question.columnHeader" size="20" maxlength="30"/>
		</li>	
		<li><label>Field Identifier:</label>
			<s:textfield disabled="true" name="question.uniqueCode" size="20" maxlength="50"/>
		</li>
		<li><label>Has Requirement:</label>
			<s:checkbox disabled="true" name="question.hasRequirement" value="question.hasRequirement.name() == 'Yes' ? true : false"/>
		</li>
		<li><label>OK Answer:</label>
			<s:textfield disabled="true" name="question.okAnswer" />
		</li>
		<li><label>Requirement:</label>
			<s:if test="question.id > 0">
				<table class="report" style="clear: none">
				<thead>
					<tr>
						<th>Locale</th>
						<th>Requirement</th>
					</tr>
				</thead>
					<s:iterator value="question.questionTexts">
					<tr>
						<td><s:property value="locale"/></td>
						<td><s:property value="requirement"/></td>
					</tr>
					</s:iterator>
				</table>
			</s:if>
			<s:else>
				<s:textarea disabled="true" name="defaultRequirement" rows="3" cols="65"/>
			</s:else>
		</li>
		<li><label>Flaggable:</label>
			<s:checkbox disabled="true" name="question.isRedFlagQuestion" value="question.isRedFlagQuestion.name() == 'Yes' ? true : false"/>
		</li>
		<li><label>Required:</label>
			<s:select disabled="true" list="#{'No':'No','Yes':'Yes','Depends':'Depends'}" name="question.isRequired" />
		</li>
		<li><label>Depends on Question:</label>
			<s:textfield disabled="true" name="dependsOnQuestionID" />
			<s:if test="dependsOnQuestionID > 0"><a href="?id=<s:property value="dependsOnQuestionID" />">Show</a></s:if>
		</li>
		<li><label>Depends on Answer:</label>
			<s:textfield disabled="true" name="question.dependsOnAnswer" />
		</li>
		<li><label>Question Type:</label>
			<s:select disabled="true" list="questionTypes" name="question.questionType" />
		</li>
		
		<s:if test="subCategory.id == 40">
			<li><label>Risk Level:</label>
				<s:select disabled="true" list="@com.picsauditing.jpa.entities.LowMedHigh@values()" name="question.riskLevel" />
			</li>
		</s:if>
		
		<li><label>Title:</label>
			<s:textfield disabled="true" name="question.title" size="65"/>
		</li>
		<li><label>Visible:</label>
			<s:checkbox disabled="true" name="question.isVisible" value="question.isVisible.name() == 'Yes' ? true : false"/>
		</li>
		<li><label>Grouped with Previous:</label>
			<s:checkbox disabled="true" name="question.isGroupedWithPrevious" value="question.isGroupedWithPrevious.name() == 'Yes' ? true : false"/>
		</li>
		<li><label>Show Comments:</label>
			<s:checkbox disabled="true" name="question.showComment" value="question.showComment"/>
		</li>
		<li><label>Help Page:</label>
			<div>
				<s:textfield disabled="true" name="question.helpPage" size="30" maxlength="100" />
				<s:if test="question.helpPage.length() > 0"><a href="http://help.picsauditing.com/wiki/<s:property value="question.helpPage"/>">Help Center</a></s:if>
				<s:else>help.picsauditing.com/wiki/???</s:else>
			</div>
		</li>
		<li><label>Countries:</label>
			<s:hidden name="countries" value="%{question.countries}"/>
			<s:textfield disabled="true" size="50" cssClass="countries"/>
		</li>
		<li><label>Exclude Countries:</label>
			<s:checkbox disabled="true" name="exclude" label="Exclude Countries" value="question.countries.startsWith('!')" />
		</li>			
	</ol>
	</fieldset>
 
	<fieldset class="form">
	<h2 class="formLegend">Useful Links</h2>
	<ol>
		<li><label>URL 1:</label>
			<s:textfield disabled="true" name="question.linkUrl1" size="65"/>
		</li>
		<li><label>Label 1:</label>
			<s:textfield disabled="true" name="question.linkText1" size="25"/>
		</li>
		<li><label>URL 2:</label>
			<s:textfield disabled="true" name="question.linkUrl2" size="65"/>
		</li>	
		<li><label>Label 2:</label>
			<s:textfield disabled="true" name="question.linkText2" size="25"/>
		</li>		
		<li><label>URL 3:</label>
			<s:textfield disabled="true" name="question.linkUrl3" size="65"/>
		</li>	
		<li><label>Label 3:</label>
			<s:textfield disabled="true" name="question.linkText3" size="25"/>
		</li>
		<li><label>URL 4:</label>
			<s:textfield disabled="true" name="question.linkUrl4" size="65"/>
		</li>	
		<li><label>Label 4:</label>
			<s:textfield disabled="true" name="question.linkText4" size="25"/>
		</li>
		<li><label>URL 5:</label>
			<s:textfield disabled="true" name="question.linkUrl5" size="65"/>
		</li>	
		<li><label>Label 5:</label>
			<s:textfield disabled="true" name="question.linkText5" size="25"/>
		</li>
		<li><label>URL 6:</label>
			<s:textfield disabled="true" name="question.linkUrl6" size="65"/>
		</li>	
		<li><label>Label 6:</label>
			<s:textfield disabled="true" name="question.linkText6" size="25"/>
		</li>									
	</ol>
	</fieldset>

</s:form>
