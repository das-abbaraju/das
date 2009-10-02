<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Manage Question</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<script type="text/javascript" src="js/prototype.js"></script>
</head>
<body>
<s:include value="manage_audit_type_breadcrumbs.jsp" />

<s:form id="save">
	<s:hidden name="id" />
	<s:hidden name="parentID" value="%{question.subCategory.id}" />
	<s:hidden name="question.subCategory.id" />
	<fieldset class="form submit">
		<div>
			<button class="picsbutton positive" name="button" type="submit" value="save">Save</button>
		</div>
	</fieldset>
	<fieldset class="form">
	<legend><span>Question</span></legend>
	<ol>
		<li><label>ID:</label>
			<s:if test="question.id > 0">
				<s:property value="question.id" />
			</s:if>
			<s:else>
				NEW
			</s:else>
		</li>
		<li><label>Question:</label>
			<s:textarea name="question.question" rows="10" cols="100" />
		</li>		
		<li><label>Effective Date:</label>
			<s:textfield name="question.effectiveDate" value="%{ question.effectiveDate && getText('short_dates', {question.effectiveDate})}"/>
		</li>
		<li><label>Expiration Date:</label>
			<s:textfield name="question.expirationDate" value="%{ question.expirationDate && getText('short_dates', {question.expirationDate})}"/>
		</li>
		<li><label>Added:</label>
			<s:date name="question.creationDate" />
		</li>
		<li><label>Updated:</label>
			<s:date name="question.updateDate" />
		</li>
		<li><label>Column Header:</label>
			<s:textfield name="question.columnHeader" size="20" maxlength="30"/>
		</li>	
		<li><label>Field Identifier:</label>
			<s:textfield name="question.uniqueCode" size="20" maxlength="50"/>
		</li>
		<li><label>Has Requirement:</label>
			<s:checkbox name="question.hasRequirement" value="question.hasRequirement.name() == 'Yes' ? true : false"/>
		</li>
		<li><label>OK Answer:</label>
			<s:textfield name="question.okAnswer" />
		</li>
		<li><label>Requirement:</label>
			<s:textarea name="question.requirement" rows="2" cols="50" />
		</li>
		<li><label>Flaggable:</label>
			<s:checkbox name="question.isRedFlagQuestion" value="question.isRedFlagQuestion.name() == 'Yes' ? true : false"/>
		</li>
		<li><label>Required:</label>
			<s:select list="#{'No':'No','Yes':'Yes','Depends':'Depends'}" name="question.isRequired" />
		</li>
		<li><label>Depends on Question:</label>
			<s:textfield name="dependsOnQuestionID" />
			<s:if test="dependsOnQuestionID > 0"><a href="?id=<s:property value="dependsOnQuestionID" />">Show</a></s:if>
		</li>
		<li><label>Depends on Answer:</label>
			<s:textfield name="question.dependsOnAnswer" />
		</li>
		<li><label>Question Type:</label>
			<s:select list="questionTypes" name="question.questionType" />
		</li>
		
		<s:if test="subCategory.id == 40">
			<li><label>Risk Level:</label>
				<s:select list="@com.picsauditing.jpa.entities.LowMedHigh@values()" name="question.riskLevel" />
			</li>
		</s:if>
		
		<li><label>Title:</label>
			<s:textfield name="question.title" size="65"/>
		</li>
		<li><label>Visible:</label>
			<s:checkbox name="question.isVisible" value="question.isVisible.name() == 'Yes' ? true : false"/>
		</li>
		<li><label>Grouped with Previous:</label>
			<s:checkbox name="question.isGroupedWithPrevious" value="question.isGroupedWithPrevious.name() == 'Yes' ? true : false"/>
		</li>
		<li><label>Show Comments:</label>
			<s:checkbox name="question.showComment" value="question.showComment"/>
		</li>
		<li><label>Help Page:</label>
			<div>
				<s:textfield name="question.helpPage" size="30" maxlength="100" />
				<s:if test="question.helpPage.length() > 0"><a href="http://help.picsauditing.com/wiki/<s:property value="question.helpPage"/>">Help Center</a></s:if>
				<s:else>help.picsauditing.com/wiki/???</s:else>
			</div>
		</li>
	</ol>
	</fieldset>
	<fieldset class="form">
	<legend><span>Useful Links</span></legend>
	<ol>
		<li><label>URL 1:</label>
			<s:textfield name="question.linkUrl1" size="65"/>
		</li>
		<li><label>Label 1:</label>
			<s:textfield name="question.linkText1" size="25"/>
		</li>
		<li><label>URL 2:</label>
			<s:textfield name="question.linkUrl2" size="65"/>
		</li>	
		<li><label>Label 2:</label>
			<s:textfield name="question.linkText2" size="25"/>
		</li>		
		<li><label>URL 3:</label>
			<s:textfield name="question.linkUrl3" size="65"/>
		</li>	
		<li><label>Label 3:</label>
			<s:textfield name="question.linkText3" size="25"/>
		</li>
		<li><label>URL 4:</label>
			<s:textfield name="question.linkUrl4" size="65"/>
		</li>	
		<li><label>Label 4:</label>
			<s:textfield name="question.linkText4" size="25"/>
		</li>
		<li><label>URL 5:</label>
			<s:textfield name="question.linkUrl5" size="65"/>
		</li>	
		<li><label>Label 5:</label>
			<s:textfield name="question.linkText5" size="25"/>
		</li>
		<li><label>URL 6:</label>
			<s:textfield name="question.linkUrl6" size="65"/>
		</li>	
		<li><label>Label 6:</label>
			<s:textfield name="question.linkText6" size="25"/>
		</li>									
	</ol>
	</fieldset>
	<fieldset class="form submit">
		<div>
			<button class="picsbutton positive" name="button" type="submit" value="save">Save</button>
		<s:if test="question.id > 0">
			<button name="button" class="picsbutton negative" type="submit" value="delete">Delete</button>
		</s:if>
		</div>
	</fieldset>
</s:form>

</body>
</html>