<%@ taglib prefix="s" uri="/struts-tags"%>
<ol>
<li><s:property value="auditData.question.subCategory.subCategory"/><br />
<s:property value="auditData.question.subCategory.category.number"/>.<s:property value="auditData.question.subCategory.number"/>.<s:property value="auditData.question.number"/>
<s:property value="auditData.question.question"/></li>

<li><label>Answer:</label> <s:textfield id="answer_%{auditData.question.id}" name="auditData.answer"></s:textfield>
<s:if test="auditData.verified == false">
<input id="verify_<s:property value="auditData.question.id"/>" type="submit" onclick="return toggleVerify(<s:property value="auditData.audit.id"/>, <s:property value="auditData.question.id"/>, <s:property value="auditData.question.subCategory.id"/>);" value="Verify"/>
<input id="unverify_<s:property value="auditData.question.id"/>" style="display: none;" type="submit" onclick="return toggleVerify(<s:property value="auditData.audit.id"/>, <s:property value="auditData.question.id"/>, <s:property value="auditData.question.subCategory.id"/>);" value="Unverify"/>
</s:if>
<s:else>
<input id="verify_<s:property value="auditData.question.id"/>" type="submit" style="display: none;" onclick="return toggleVerify(<s:property value="auditData.audit.id"/>, <s:property value="auditData.question.id"/>, <s:property value="auditData.question.subCategory.id"/>);" value="Verify"/>
<input id="unverify_<s:property value="auditData.question.id"/>"type="submit" onclick="return toggleVerify(<s:property value="auditData.audit.id"/>, <s:property value="auditData.question.id"/>, <s:property value="auditData.question.subCategory.id"/>);" value="Unverify"/>
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