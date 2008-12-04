<%@ taglib prefix="s" uri="/struts-tags"%>

<h2><s:property value="conAudit.auditFor" /> <s:property
	value="conAudit.auditType.auditName" /></h2>


<s:form id="verify2">
<s:if test="conAudit.auditType.pqf">
	<fieldset style="clear: none; float: left; width: 50%; margin: 0.5em;"><legend><span>PQF
	Questions</span></legend> <s:iterator value="pqfQuestions">
		<s:div id="qid_%{question.id}">
		<ol>
			<li><s:property value="question.subCategory.subCategory"/><br />
				<s:property value="question.subCategory.category.number"/>.<s:property value="question.subCategory.number"/>.<s:property value="question.number"/>
				<s:property value="question.question"/></li>

			<li><label>Answer:</label> <s:textfield id="answer_%{question.id}" name="answer"></s:textfield>
			<s:if test="verified == false">
				<input id="verify_<s:property value="question.id"/>" type="submit" 
					onclick="return toggleVerify(<s:property value="conAudit.id"/>, <s:property value="question.id"/>, <s:property value="question.subCategory.id"/>);" 
					value="Verify"/>
				<input id="unverify_<s:property value="question.id"/>" style="display: none;" type="submit" 
					onclick="return toggleVerify(<s:property value="conAudit.id"/>, <s:property value="question.id"/>, <s:property value="question.subCategory.id"/>);" value="Unverify"/>
					<s:div cssStyle="display : inline;" id="status_%{question.id}"></s:div>
			</s:if>
			<s:else>
				<input id="verify_<s:property value="question.id"/>" type="submit" style="display: none;" onclick="return toggleVerify(<s:property value="conAudit.id"/>, <s:property value="question.id"/>, <s:property value="question.subCategory.id"/>);" value="Verify"/>
				<input id="unverify_<s:property value="question.id"/>" type="submit" onclick="return toggleVerify(<s:property value="conAudit.id"/>, <s:property value="question.id"/>, <s:property value="question.subCategory.id"/>);" value="Unverify"/>
				<s:div cssStyle="display : inline;" id="status_%{question.id}"></s:div>
			</s:else></li>
			
			<s:if test="verified">
				<li><label>Verified:</label><s:date name="dateVerified"
					format="MM/dd/yyyy" /> by <s:property value="auditor.name"/></li>
			</s:if>
			<li><label>Comment:</label> <s:select id="comment_%{question.id}" list="emrProblems" name="comment" /></li>
			<li>
			<hr>
			</li>
		</ol>
		</s:div>
	</s:iterator></fieldset>
</s:if>
<s:else>
	<fieldset style="clear: none; float: left; width: 50%; margin: 0.5em;"><legend><span>EMR
	Questions</span></legend> <s:iterator value="conAudit.data">
		<s:div id="qid_%{question.id}">
		<ol>
			<li><s:property value="question.subCategory.subCategory"/><br />
				<s:property value="question.subCategory.category.number"/>.<s:property value="question.subCategory.number"/>.<s:property value="question.number"/>
				<s:property value="question.question"/></li>

			<li><label>Answer:</label> <s:textfield id="answer_%{question.id}" name="answer"></s:textfield>
			<s:if test="verified == false">
				<input id="verify_<s:property value="question.id"/>" type="submit" 
					onclick="return toggleVerify(<s:property value="conAudit.id"/>, <s:property value="question.id"/>, <s:property value="question.subCategory.id"/>);" 
					value="Verify"/>
				<input id="unverify_<s:property value="question.id"/>" style="display: none;" type="submit" 
					onclick="return toggleVerify(<s:property value="conAudit.id"/>, <s:property value="question.id"/>, <s:property value="question.subCategory.id"/>);" value="Unverify"/>
					<s:div id="status_%{question.id}" cssStyle="display : inline;"></s:div>
			</s:if>
			<s:else>
				<input id="verify_<s:property value="question.id"/>" type="submit" style="display: none;" onclick="return toggleVerify(<s:property value="conAudit.id"/>, <s:property value="question.id"/>, <s:property value="question.subCategory.id"/>);" value="Verify"/>
				<input id="unverify_<s:property value="question.id"/>" type="submit" onclick="return toggleVerify(<s:property value="conAudit.id"/>, <s:property value="question.id"/>, <s:property value="question.subCategory.id"/>);" value="Unverify"/>
				<s:div id="status_%{question.id}" cssStyle="display : inline;"></s:div>
			</s:else></li>
			
			<s:if test="verified">
				<li><label>Verified:</label><s:date name="dateVerified"
					format="MM/dd/yyyy" /> by <s:property value="auditor.name"/></li>
			</s:if>
			<li><label>Comment:</label> <s:select id="comment_%{question.id}" list="emrProblems" name="comment" /></li>
			<li>
			<hr>
			</li>
		</ol>
		</s:div>
	</s:iterator></fieldset>

	<fieldset style="clear: none; float: left; width: 40%; margin: 0.5em;"><legend><span>OSHA</span></legend>
	<s:iterator value="conAudit.oshas">
		<s:if test="corporate">
		<s:div id="oid_%{id}">
			<ol>
				<s:if test="verified">
					<input type="submit" style="display: none;" onclick="return toggleOSHAVerify(<s:property value="id"/>);" value="Verify"/>
					<input type="submit" onclick="return toggleOSHAVerify(<s:property value="id"/>);" value="Unverify"/><s:div cssStyle="display : inline;" id="status_%{id}"></s:div>
					<li><label>Verified:</label><s:date name="verifiedDate"
						format="MM/dd/yyyy" /> by <s:property value="conAudit.auditor.name"/></li>

				</s:if>
				<s:else>
					<input type="submit" 
						onclick="return toggleOSHAVerify(<s:property value="id"/>);" 
						value="Verify"/>
					<input style="display: none;" type="submit" 
						onclick="return toggleOSHAVerify(<s:property value="id"/>);" value="Unverify"/><s:div id="status_%{id}"></s:div>
				</s:else>


				<li><label>Comment:</label> <s:select id="comment_%{id}" list="oshaProblems"
					name="comment" /></li>

				<li>
				<hr>
				</li>
				<li><label>Applicable:</label> <s:checkbox id="applicable_%{id}" name="applicable"
					value="applicable" onclick="$('applicableFields').toggle()"/></li>
				<s:if test="applicable">
					<s:set name="showApplicableFieldsDisplay" value="'block'"/>
				</s:if>
				<s:else>
					<s:set name="showApplicableFieldsDisplay" value="'none'"/>
				</s:else>
				<s:div id="applicableFields" cssStyle="display : %{showApplicableFieldsDisplay};">
					<li><label>File:</label> <s:if test="fileUploaded">
						<a
							href="#" onclick="openOsha(<s:property value="id"/>); return false;"
							target="_BLANK">View File</a>
						<a
							href="AuditCat.action?auditID=<s:property value="conAudit.id" />&catID=151&mode=Edit"
							target="_BLANK">Change File</a>
					</s:if>
					<s:else>
						None. <a
							href="AuditCat.action?auditID=<s:property value="conAudit.id" />&catID=151&mode=Edit"
							target="_BLANK">Upload New Files</a>
					</s:else></li>
					<li><label>Man Hours Worked:</label> <s:textfield id="manHours_%{id}"
						name="manHours" cssClass="oshanum" /></li>
					<li><label>Number of Fatalities:</label> <s:textfield id="fatalities_%{id}"
						name="fatalities" cssClass="oshanum" /></li>
					<li><label>Number of Lost Work Cases:</label> <s:textfield id="lwc_%{id}"
						name="lostWorkCases" cssClass="oshanum" /></li>
					<li><label>Number of Lost Workdays:</label> <s:textfield id="lwd_%{id}"
						name="lostWorkDays" cssClass="oshanum" /></li>
					<li><label>Injury &amp; Illnesses Medical Cases:</label> <s:textfield id="imc_%{id}"
						name="injuryIllnessCases" cssClass="oshanum" /></li>
					<li><label>Restricted Work Cases:</label> <s:textfield id="rwc_%{id}"
						name="restrictedWorkCases" cssClass="oshanum" /></li>
					<li><label>Total Injuries and Illnesses:</label> <s:textfield id="tii_%{id}"
						name="recordableTotal" cssClass="oshanum" /></li>
				 </s:div>
			</ol>
		</s:div>
		</s:if>
	</s:iterator></fieldset>
</s:else>
</s:form>
