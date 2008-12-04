<%@ taglib prefix="s" uri="/struts-tags"%>
<s:if test="auditData">
<ol>
<li><s:property value="auditData.question.subCategory.subCategory"/><br />
<s:property value="auditData.question.subCategory.category.number"/>.<s:property value="auditData.question.subCategory.number"/>.<s:property value="auditData.question.number"/>
<s:property value="auditData.question.question"/></li>

<li><label>Answer:</label> <s:textfield id="answer_%{auditData.question.id}" name="auditData.answer"></s:textfield>
<s:if test="auditData.verified == false">
<input id="verify_<s:property value="auditData.question.id"/>" type="submit" onclick="return toggleVerify(<s:property value="auditData.audit.id"/>, <s:property value="auditData.question.id"/>, <s:property value="auditData.question.subCategory.id"/>);" value="Verify"/>
<input id="unverify_<s:property value="auditData.question.id"/>" style="display: none;" type="submit" onclick="return toggleVerify(<s:property value="auditData.audit.id"/>, <s:property value="auditData.question.id"/>, <s:property value="auditData.question.subCategory.id"/>);" value="Unverify"/>
<s:div cssStyle="display : inline;" id="status_%{auditData.question.id}"></s:div>
</s:if>
<s:else>
<input id="verify_<s:property value="auditData.question.id"/>" type="submit" style="display: none;" onclick="return toggleVerify(<s:property value="auditData.audit.id"/>, <s:property value="auditData.question.id"/>, <s:property value="auditData.question.subCategory.id"/>);" value="Verify"/>
<input id="unverify_<s:property value="auditData.question.id"/>"type="submit" onclick="return toggleVerify(<s:property value="auditData.audit.id"/>, <s:property value="auditData.question.id"/>, <s:property value="auditData.question.subCategory.id"/>);" value="Unverify"/>
<s:div cssStyle="display : inline;" id="status_%{auditData.question.id}"></s:div>
</s:else></li>

<s:if test="auditData.verified">
<li><label>Verified:</label><s:date name="auditData.dateVerified"
	format="MM/dd/yyyy" /> by <s:property value="auditData.auditor.name"/></li>
</s:if>
<li><label>Comment:</label> <s:select id="comment_%{auditData.question.id}" list="emrProblems" name="auditData.comment" /></li>
<li>
<hr>
</li>
</ol>
</s:if>
<s:elseif test="osha">
			<ol>
				<s:if test="osha.verified">
					<input type="submit" onclick="return toggleOSHAVerify(<s:property value="osha.id"/>);" value="Unverify"/><s:div cssStyle="display : inline;" id="status_%{osha.id}"></s:div>
					<li><label>Verified:</label><s:date name="osha.verifiedDate"
						format="MM/dd/yyyy" /> by <s:property value="osha.conAudit.auditor.name"/></li>
				</s:if>
				<s:else>
					<input type="submit" 
						onclick="return toggleOSHAVerify(<s:property value="osha.id"/>);" 
						value="Verify"/><s:div cssStyle="display : inline;" id="status_%{osha.id}"></s:div>
				</s:else>


				<li><label>Comment:</label> <s:select id="comment_%{osha.id}" list="oshaProblems"
					name="osha.comment" /></li>

				<li>
				<hr>
				</li>
				<li><label>Applicable:</label> <s:checkbox id="applicable_%{osha.id}" name="osha.applicable" onclick="$('applicableFields').toggle()"
					value="osha.applicable" /></li>

				<s:if test="osha.applicable">
					<s:set name="showApplicableFieldsDisplay" value="'block'"/>
				</s:if>
				<s:else>
					<s:set name="showApplicableFieldsDisplay" value="'none'"/>
				</s:else>

				<s:div id="applicableFields" cssStyle="display : %{showApplicableFieldsDisplay};">
					<li><label>File:</label> <s:if test="osha.fileUploaded">
						<a
							href="#" onclick="openOsha(<s:property value="osha.id"/>); return false;"
							target="_BLANK">View File</a>
						<a
							href="AuditCat.action?auditID=<s:property value="osha.conAudit.id" />&catID=151&mode=Edit"
							target="_BLANK">Change File</a>
					</s:if>
					<s:else>
						None. <a
							href="AuditCat.action?auditID=<s:property value="osha.conAudit.id" />&catID=151&mode=Edit"
							target="_BLANK">Upload New Files</a>
					</s:else></li>					
					<li><label>Man Hours Worked:</label> <s:textfield id="manHours_%{osha.id}"
						name="osha.manHours" cssClass="oshanum" /></li>
					<li><label>Number of Fatalities:</label> <s:textfield id="fatalities_%{osha.id}"
						name="osha.fatalities" cssClass="oshanum" /></li>
					<li><label>Number of Lost Work Cases:</label> <s:textfield id="lwc_%{osha.id}"
						name="osha.lostWorkCases" cssClass="oshanum" /></li>
					<li><label>Number of Lost Workdays:</label> <s:textfield id="lwd_%{osha.id}"
						name="osha.lostWorkDays" cssClass="oshanum" /></li>
					<li><label>Injury &amp; Illnesses Medical Cases:</label> <s:textfield id="imc_%{osha.id}"
						name="osha.injuryIllnessCases" cssClass="oshanum" /></li>
					<li><label>Restricted Work Cases:</label> <s:textfield id="rwc_%{osha.id}"
						name="osha.restrictedWorkCases" cssClass="oshanum" /></li>
					<li><label>Total Injuries and Illnesses:</label> <s:textfield id="tii_%{osha.id}"
						name="osha.recordableTotal" cssClass="oshanum" /></li>
				</s:div>
			</ol>
</s:elseif>