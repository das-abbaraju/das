<%@ taglib prefix="s" uri="/struts-tags"%>

<h2><s:property value="conAudit.auditFor" /> <s:property
	value="conAudit.auditType.auditName" /></h2>

<s:form id="verify2">
<s:if test="conAudit.auditType.pqf">
	<fieldset style="clear: none; float: left; width: 50%; margin: 0.5em;"><legend><span>PQF
	Questions</span></legend> <s:iterator value="pqfQuestions">
		<ol>
			<li><s:property value="question.subCategory.subCategory"/><br />
				<s:property value="question.subCategory.category.number"/>.<s:property value="question.subCategory.number"/>.<s:property value="question.number"/>
				<s:property value="question.question"/></li>
			<li><label>Is
			Correct:</label> <s:checkbox name="verified" value="verified" /></li>
			<s:if test="verified">
				<li><label>Verified:</label><s:date name="dateVerified"
					format="MM/dd/yyyy" /> by <s:property value="auditor.name"/></li>
			</s:if>
			<s:else>
				<li><label>Issue:</label> <s:select list="emrProblems"
					name="comment" /></li>
			</s:else>
			<li><label>Contractor Answer:</label> <s:textfield name="answer"></s:textfield> </li>
			<li><label>Verified Answer:</label> <s:textfield name="verifiedAnswer"></s:textfield> </li>
			<li>
			<hr>
			</li>
		</ol>
	</s:iterator></fieldset>
</s:if>
<s:else>
	<fieldset style="clear: none; float: left; width: 50%; margin: 0.5em;"><legend><span>EMR
	Questions</span></legend> <s:iterator value="conAudit.data">
		<ol>
			<li><label><s:property value="question.question"/></label> <s:property value="answer"/></li>
			<li><label>Is
			Correct:</label> <s:checkbox name="verified" value="verified" /></li>
			<s:if test="verified">
				<li><label>Verified:</label><s:date name="dateVerified"
					format="MM/dd/yyyy" /> by <s:property value="auditor.name"/></li>
			</s:if>
			<s:else>
				<li><label>Issue:</label> <s:select list="emrProblems"
					name="comment" /></li>
			</s:else>
			<li><label>Verified Answer:</label> <s:textfield name="verifiedAnswer"></s:textfield> </li>
			<li>
			<hr>
			</li>
		</ol>
	</s:iterator></fieldset>

	<fieldset style="clear: none; float: left; width: 40%; margin: 0.5em;"><legend><span>OSHA</span></legend>
	<s:iterator value="conAudit.oshas">
		<s:if test="corporate">
			<ol>
				<li style="font-size: 14px; font-weight: bolder;"><label>Is
				Correct:</label> <s:checkbox name="verified" value="verified" /></li>
				<s:if test="verified">
					<li><label>Verified:</label><s:date name="verifiedDate"
						format="MM/dd/yyyy" /> by <s:property value="auditor.name"/></li>
				</s:if>
				<s:else>
					<li><label>Issue:</label> <s:select list="oshaProblems"
						name="comment" /></li>
				</s:else>
				<li>
				<hr>
				</li>
				<li><label>Applicable:</label> <s:checkbox name="applicable"
					value="applicable" /></li>
				<s:if test="applicable">
					<li><label>File:</label> <s:if test="fileUploaded">
						<a
							href="AuditCat.action?auditID=<s:property value="#oshaemr.id" />&catDataID=151&mode=Edit"
							target="_BLANK">Upload New Files</a>
					</s:if></li>
					<li><label>Man Hours Worked:</label> <s:textfield
						name="manHours" cssClass="oshanum" /></li>
					<li><label>Number of Fatalities:</label> <s:textfield
						name="fatalities" cssClass="oshanum" /></li>
					<li><label>Number of Lost Work Cases:</label> <s:textfield
						name="lostWorkCases" cssClass="oshanum" /></li>
					<li><label>Number of Lost Workdays:</label> <s:textfield
						name="lostWorkDays" cssClass="oshanum" /></li>
					<li><label>Injury &amp; Illnesses Medical Cases:</label> <s:textfield
						name="injuryIllnessCases" cssClass="oshanum" /></li>
					<li><label>Restricted Work Cases:</label> <s:textfield
						name="restrictedWorkCases" cssClass="oshanum" /></li>
					<li><label>Total Injuries and Illnesses:</label> <s:textfield
						name="recordableTotal" cssClass="oshanum" /></li>
				</s:if>
			</ol>
		</s:if>
	</s:iterator></fieldset>
</s:else>
</s:form>
