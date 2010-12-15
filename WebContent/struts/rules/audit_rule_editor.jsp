<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title><s:property value="title"/></title>
<link rel="stylesheet" href="css/reports.css"/>
<link rel="stylesheet" href="css/forms.css"/>
<s:include value="../jquery.jsp"/>
<link rel="stylesheet" type="text/css" media="screen" href="css/rules.css?v=<s:property value="version"/>" />
</head>
<body>
<h1><s:property value="title"/></h1>
<s:include value="../actionMessages.jsp"/>

<div id="summary">
	<s:property value="rule.toString()"/>
	<ol>
		<li><label>Created By</label>
			<s:property value="rule.createdBy"/>
		</li>
		<li><label>Creation Date</label>
			<s:property value="rule.creationDate"/>
		</li>
		<li><label>Updated By</label>
			<s:property value="rule.updatedBy"/>
		</li>
		<li><label>Updated Date</label>
			<s:property value="rule.updateDate"/>
		</li>
	</ol>
</div>

<div id="detail">
	<s:if test="canEditRule">
		<s:form method="post" id="rule_form">
			<s:hidden name="rule.id"/>
			<fieldset class="form">
				<h2 class="formLegend">Rule</h2>
				<ol>
					<li><label>Include</label>
						<s:checkbox name="rule.include"/>
					</li>
					<li><label>Level</label>
						<s:property value="rule.level"/> + <s:textfield name="rule.levelAdjustment" />
					</li>
					<li><label>Priority</label>
						<s:property value="rule.priority"/>
					</li>
				</ol>
			</fieldset>
			<fieldset class="form">
				<h2 class="formLegend">Options</h2>
				<ol>
					<li><label>Audit Type</label>
						<s:select name="rule.auditType.id" list="{}" headerKey="" headerValue=" - Audit Type - ">
							<s:iterator value="auditTypeMap" var="aType">
								<s:optgroup label="%{#aType.key}" list="#aType.value" listKey="id" listValue="auditName"/>
							</s:iterator>
						</s:select>
					</li>
					<s:if test="!auditTypeRule">
						<li><label>Category</label>
							<input type="text" class="searchAuto" id="category" value="<s:property value="rule.auditCategory.name"/>"/>
							<s:hidden name="rule.auditCategory.id" id="cat_hidden"/>
							<s:if test="rule.auditCategory.id != null">
								<a href="#" class="clearfield">Clear Field</a>
								<div><a href="ManageCategory.action?id=<s:property value="rule.auditCategory.id"/>">Go To Category</a></div>
							</s:if>
						</li>
						<li><label>Top or Sub Category</label>
							<s:select list="#{-1:'Any',0:'Sub Categories',1:'Top Categories'}" name="rootCat"/> 
						</li>
					</s:if>
					<li><label>Bid-Only</label>
						<s:select name="bidOnly" list="#{-1:'Any',0:'No',1:'Yes'}" value="bidOnly"/>
					</li>
					<li><label>Account Type</label>
						<s:select name="rule.contractorType" list="@com.picsauditing.jpa.entities.ContractorType@values()" listValue="type" headerKey="" headerValue="Any"/>
					</li>
					<li><label>Risk</label>
						<s:select name="rule.risk" list="#{'':'Any','Low':'Low','Med':'Medium','High':'High'}"/>
					</li>
					<li <s:if test="operatorRequired">class="required"</s:if>>
						<label>Operator</label>
						<input type="text" class="searchAuto" id="operator" value="<s:property value="rule.operatorAccount.name"/>"/>
						<s:hidden name="rule.operatorAccount.id" id="op_hidden"/>
						<s:if test="rule.operatorAccount.id != null">
							<a href="#" class="clearfield">Clear Field</a>
							<div><a href="FacilitiesEdit.action?id=<s:property value="rule.operatorAccount.id"/>">Go To Operator</a></div>
						</s:if>
						<s:if test="operatorRequired"> 
							<div class="fieldhelp">
							<h3>Operator</h3>
							<p>You must specify the Operator that this rule will apply to</p>
							</div>
						</s:if>
					</li>
					<li id="opTagli" <s:if test="rule.operatorAccount==null">style="display: none;"</s:if>><label>Tag</label>
						<s:select list="operatorTagList" name="tagID" listKey="id" listValue="tag" id="tag" headerKey="0" headerValue="- Any -"
						value="rule.tag.id" />
					</li>
					<s:if test="auditTypeRule">
						<li><label>Dependent Audit</label>
							<s:select name="rule.dependentAuditType.id" list="{}" headerKey="" headerValue=" - Audit Type - ">
								<s:iterator value="auditTypeMap" var="aType">
									<s:optgroup label="%{#aType.key}" list="#aType.value" listKey="id" listValue="auditName"/>
								</s:iterator>
							</s:select>
						</li>					
						<li id="dAuditSelectli" <s:if test="rule.dependentAuditStatus==null">style="display: none;"</s:if>><label>Dependent Status</label>
							<s:select list="dependentAuditStatus" name="rule.dependentAuditStatus" id="dAuditSelect" headerKey="" headerValue="- Any -" />
							<a href="#" class="clearfield">Clear Field</a>
						</li>					
					</s:if>
					<li><label>Question</label>
						<input type="text" class="searchAuto" id="question" value="<s:property value="rule.question.name"/>"/>
						<s:hidden name="rule.question.id" id="question_hidden"/>
						<s:if test="rule.question.id != null">
							<a href="#" class="clearfield">Clear Field</a>
							<div><a href="ManageQuestion.action?id=<s:property value="rule.question.id"/>">Go To Question</a></div>
						</s:if>
					</li>
					<li><label>Question Comparator</label>
						<s:select name="rule.questionComparator" list="@com.picsauditing.jpa.entities.QuestionComparator@values()" headerKey="" headerValue=""/>
					</li>
					<li><label>Answer</label>
						<s:textfield name="rule.questionAnswer" />
					</li>
				</ol>
			</fieldset>
			<fieldset class="form submit">
				<input type="submit" class="picsbutton positive" name="button" value="Save"/>
			</fieldset>
		</s:form>
	</s:if>
	<s:else>
	
	</s:else>
</div>

</body>
</html>