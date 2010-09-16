<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>

	<s:set name="showApproveButton" value="true" />
	<s:set name="fullyVerified" value="true" />
	<s:iterator value="conAudit.operators" id="cao">
		<s:if test="!#cao.status.submittedResubmitted">
			<s:set name="showApproveButton" value="false" />
		</s:if>
		<s:if test="#cao.percentVerified < 100">
			<s:set name="fullyVerified" value="false" />
		</s:if>
	</s:iterator>

	<s:if test="(#showApproveButton && pqfQuestions.size == 0) || #fullyVerified">
		<s:set name="showApproveButton" value="'inline'"/>
	</s:if>
	<s:else>
		<s:set name="showApproveButton" value="'none'"/>
	</s:else>
<h2><s:property value="conAudit.auditFor" /> <s:property value="conAudit.auditType.auditName" /></h2>
<br clear="all"/>
<table class="report">
	<thead>	
		<tr>
			<th colspan="2">Operators</th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="caos">
			<tr>
				<td><s:property value="key.name" /></td>
				<td>
					<s:set name="allVerified" value="true" />
					<s:iterator value="value">
						<s:if test="percentVerified < 100"><s:set name="allVerified" value="false" /></s:if>
					</s:iterator>
					<s:if test="allVerified">
						<button class="picsbutton positive" name="button">Approve</button>
					</s:if>
					<button class="picsbutton negative" name="button">Reject</button>
				</td>
			</tr>
		</s:iterator>
	</tbody>
</table>

<div>
	<button class="picsbutton negative" name="button" onclick="return allApproveReject(<s:property value="conAudit.id"/>,'Incomplete',<s:property value="allCaoIDs" />);">Reject All</button>
	<button style="display: <s:property value="#showApproveButton" />" class="picsbutton positive approveButton" name="button" onclick="return allApproveReject(<s:property value="conAudit.id"/>,'Approved',<s:property value="allCaoIDs" />);">Approve All</button>
</div>
<br/>
<s:if test="conAudit.auditType.pqf">
	<fieldset class="form" style="clear: none; float: left; width: 50%; margin: 0.5em;">
	<h2 class="formLegend">PQF Questions</h2>
	<s:iterator value="pqfQuestions">
		<ol>
			<li><s:property value="question.category.name"/><br />
				<s:property value="question.expandedNumber"/>
				<s:property value="question.name"/></li>
		 
			<s:if test="question.questionType != 'File'">
				<li><label>Answer:</label>			
				<s:textfield id="answer_%{question.id}" name="answer"/></li>
			</s:if>
			<s:else>
				<li><label>File:</label> 
				<s:if test="answer.length() > 0">
						<a href="DownloadAuditData.action?auditID=<s:property value="conAudit.id"/>&answer.id=<s:property value="id"/>" target="_BLANK">View File</a>
						<a href="Audit.action?auditID=<s:property value="conAudit.id" />&catID=33&mode=Edit"	target="_BLANK">Change File</a>
					</s:if>
					<s:else>
						None. <a
							href="Audit.action?auditID=<s:property value="conAudit.id" />&catID=33&mode=Edit"
							target="_BLANK">Upload New Files</a>
					</s:else>
				</li>
			</s:else>
			<s:if test="verified == false">
				<s:set name="verifyText" value="'Verify'"/>
			</s:if>
			<s:else>
				<s:set name="verifyText" value="'Unverify'"/>
			</s:else>

			<li>
				<input id="verify_<s:property value="question.id"/>" type="submit" onclick="return toggleVerify(<s:property value="conAudit.id"/>, <s:property value="question.id"/>, <s:property value="id"/>);"	value="<s:property value="#attr.verifyText"/>"/>
				<s:div cssStyle="display : inline;" id="status_%{question.id}"></s:div>
			</li>
			<s:if test="verified">
				<s:set name="displayVerified" value="'block'"/>
			</s:if>
			<s:else>
				<s:set name="displayVerified" value="'none'"/>
			</s:else>
			<li id="verified_<s:property value="question.id"/>" style="display: <s:property value="#attr.displayVerified"/>;"><label>Verified:</label><s:div cssStyle="display:inline;" id="verify_details_%{question.id}"><s:date name="dateVerified"
				format="MM/dd/yyyy" /> by <s:property value="auditor.name"/></s:div>
			</li>
			<li>
				<label>Comment:</label> <s:textfield onblur="return setComment( %{conAudit.id}, %{question.id}, %{id});" id="comment_%{question.id}" name="comment" />
			</li>
			<s:if test="question.questionType == 'License'">
				<li>
					<s:property value="@com.picsauditing.util.Constants@displayStateLink(question.question, answer)" escape="false" />
				</li>
			</s:if>
			<li>
				<hr>
			</li>
		</ol>
		<s:div id="qid_%{question.id}">
		</s:div>
	</s:iterator>
	</fieldset>
</s:if>
<s:else>
	<fieldset class="form" style="clear: none; float: left; width: 50%; margin: 0.5em;">
	<h2 class="formLegend">Audit Questions</h2>
		<s:sort comparator="dataComparator" source="conAudit.data">
	 <s:iterator>
		<s:if test="isShowQuestionToVerify(question, answered)">
			<s:div id="qid_%{question.id}">
			<ol>
				<li><strong><s:property value="question.category.name"/></strong><br />
					<s:property value="question.expandedNumber"/>
					<s:property value="question.name"/>
					<br/>
					<s:if test="question.id == 3563 || question.id == 3565 || question.id == 3566"><a href="http://www.osha.gov/pls/imis/establishment.inspection_detail?id=<s:property value="answer"/>" target="_BLANK" title="opens in new window">OSHA Citations</a></s:if>
				</li>
	
				<s:if test="question.questionType != 'File'">
					<li><label>Answer:</label>			
					<s:textfield id="answer_%{question.id}" name="answer"/>
				</s:if>
				<s:else>
					<li><label>File:</label> 
					<s:if test="answer.length() > 0">
							<a href="DownloadAuditData.action?auditID=<s:property value="conAudit.id"/>&answer.id=<s:property value="id"/>" target="_BLANK">View File</a>
							<a href="Audit.action?auditID=<s:property value="conAudit.id" />&catID=152&mode=Edit" target="_BLANK">Change File</a>
						</s:if>
						<s:else>
							None. <a
								href="Audit.action?auditID=<s:property value="conAudit.id" />&catID=152&mode=Edit"
								target="_BLANK">Upload New Files</a>
						</s:else>
				</s:else>
				<s:if test="verified == false">
					<s:set name="verifyText" value="'Verify'"/>
				</s:if>
				<s:else>
					<s:set name="verifyText" value="'Unverify'"/>
				</s:else>
				<li><input id="verify_<s:property value="question.id"/>" type="submit" onclick="return toggleVerify(<s:property value="conAudit.id"/>, <s:property value="question.id"/>, <s:property value="id"/>);"	value="<s:property value="#attr.verifyText"/>"/>
				<s:div cssStyle="display : inline;" id="status_%{question.id}"></s:div></li>
				<s:if test="verified">
					<s:set name="displayVerified" value="'block'"/>
				</s:if>
				<s:else>
					<s:set name="displayVerified" value="'none'"/>
				</s:else>
				<li id="verified_<s:property value="question.id"/>" style="display: <s:property value="#attr.displayVerified"/>;"><label>Verified:</label><s:div cssStyle="display:inline;" id="verify_details_%{question.id}"><s:date name="dateVerified"
					format="MM/dd/yyyy" /> by <s:property value="auditor.name"/></s:div></li>
				<li><label>Comment:</label> 
					<s:if test="question.subCategory.category.id == 152">
						<s:select onchange="return setComment(%{conAudit.id}, %{question.id}, %{id});" id="comment_%{question.id}" list="emrProblems" name="comment" />
					</s:if>
					<s:else>
						<nobr><s:textfield onblur="return setComment( %{conAudit.id}, %{question.id}, %{id});" id="comment_%{question.id}" name="comment" />
						<s:if test="question.id == 2033">
							<s:select id="emrExempt" list="emrExemptReason" headerKey="" headerValue="- Exempt Reasons -" onchange="copyComment('emrExempt','comment_%{question.id}');"/>
						</s:if>
						<s:if test="question.id == 2064">
							<s:select id="oshaExempt" list="oshaExemptReason" headerKey="" headerValue="- Exempt Reasons -" onchange="copyComment('oshaExempt','comment_%{question.id}');"/>
						</s:if></nobr>
					</s:else>
				</li>
				<li>
				<hr>
				</li>
			</ol>
			</s:div>
			</s:if>
	</s:iterator>
	</s:sort>
	</fieldset>
	<s:if test="osha != null">
		<fieldset class="form" style="clear: none; float: left; width: 40%; margin: 0.5em;">
			<h2 class="formLegend">OSHA</h2>
			<s:div id="oid_%{osha.id}">
				<ol>
				<s:if test="!osha.verified">
					<s:set name="verifyText" value="'Verify'"/>
				</s:if>
				<s:else>
					<s:set name="verifyText" value="'Unverify'"/>
				</s:else>
				<input id="verify_<s:property value="osha.id"/>" name="verify" type="submit" onclick="return toggleOSHAVerify(<s:property value="osha.id"/>);" value="<s:property value="#attr.verifyText"/>"/>
				<s:div id="status_%{osha.id}"></s:div>
				<s:if test="osha.verified">
					<s:set name="displayVerified" value="'block'"/>
				</s:if>
				<s:else>
					<s:set name="displayVerified" value="'none'"/>
				</s:else>
				<li id="verified_<s:property value="osha.id"/>" style="display: <s:property value="#attr.displayVerified"/>;"><label>Verified:</label><s:div cssStyle="display:inline;" id="verify_details_%{osha.id}"><s:date name="osha.verifiedDate"
						format="MM/dd/yyyy" /> by <s:property value="osha.auditor.name"/></s:div></li>
				<li><label>Comment:</label> <s:select onchange="return setOSHAComment(%{osha.id});" id="comment_%{osha.id}" list="oshaProblems"
					name="osha.comment" /></li>
				<li><label>Links:</label><a href="http://www.osha.gov/dep/fatcat/dep_fatcat.html" target="_BLANK">OSHA Fatalities</a></li>	
				<li>
				<hr>
				</li>
				<li><label>File:</label> <s:if test="osha.fileUploaded">
					<a href="#" onclick="openOsha(<s:property value="osha.id"/>); return false;"	target="_BLANK">View File</a>
					<a href="Audit.action?auditID=<s:property value="conAudit.id" />&catID=151&mode=Edit" target="_BLANK">Change File</a>
				</s:if>
				<s:else>
					None. <a
						href="Audit.action?auditID=<s:property value="conAudit.id" />&catID=151&mode=Edit"
						target="_BLANK">Upload New Files</a>
				</s:else></li>
				<li><label>Total Man Hours:</label> <s:textfield id="manHours_%{osha.id}"
					name="osha.manHours" cssClass="oshanum" /></li>
				<li><label>Fatalities:</label> <s:textfield id="fatalities_%{osha.id}"
					name="osha.fatalities" cssClass="oshanum" /></li>
				<li><label>LWD Cases:</label> <s:textfield id="lwc_%{osha.id}"
					name="osha.lostWorkCases" cssClass="oshanum" /></li>
				<li><label>Lost Days:</label> <s:textfield id="lwd_%{osha.id}"
					name="osha.lostWorkDays" cssClass="oshanum" /></li>
				<li><label>Restricted Cases:</label> <s:textfield id="rwc_%{osha.id}"
					name="osha.restrictedWorkCases" cssClass="oshanum" /></li>
				<li><label>Restricted Days:</label> <s:textfield id="rwd_%{osha.id}"
					name="osha.modifiedWorkDay" cssClass="oshanum" /></li>
				<li><label>Other Injuries:</label> <s:textfield id="imc_%{osha.id}"
					name="osha.injuryIllnessCases" cssClass="oshanum" /></li>
				<li><label>Total Injuries:</label><s:property	value="osha.recordableTotal"/></li>
			</ol>
		</s:div>
	</fieldset>
	</s:if>
</s:else>
<br clear="all"/>
<table class="report">
	<thead>	
		<tr>
			<th colspan="2">Operators</th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="caos">
			<tr>
				<td><s:property value="key.name" /></td>
				<td>
					<s:set name="allVerified" value="true" />
					<s:iterator value="value">
						<s:if test="percentVerified < 100"><s:set name="allVerified" value="false" /></s:if>
					</s:iterator>
					<s:if test="allVerified">
						<button class="picsbutton positive" name="button">Approve</button>
					</s:if>
					<button class="picsbutton negative" name="button">Reject</button>
				</td>
			</tr>
		</s:iterator>
	</tbody>
</table>
<div>
	<button class="picsbutton negative" name="button" onclick="return allApproveReject(<s:property value="conAudit.id"/>,'Incomplete',<s:property value="allCaoIDs" />);">Reject All</button>
	<button style="display: <s:property value="#showApproveButton" />" class="picsbutton positive approveButton" name="button" onclick="return allApproveReject(<s:property value="conAudit.id"/>,'Approved',<s:property value="allCaoIDs" />);">Approve All</button>
</div>
