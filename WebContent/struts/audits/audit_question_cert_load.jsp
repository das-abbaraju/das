<%@ taglib prefix="s" uri="/struts-tags"%>

<s:if test="question != null">
	<s:set name="q" value="%{question}" />
	<s:set name="a" value="answerMap.get(#q.id)" />
</s:if>
<s:form id="cert_form%{#q.id}">
	<s:hidden name="auditID" value="%{conAudit.id}" />
	<s:hidden name="question.id" value="%{#q.id}" />
	<s:hidden name="mode" />
	<s:set name="editable" value="mode == 'Edit' || mode == 'Verify'"/>
	<s:set name="showButtons" value="true"/>
	<div class="clearfix question" id="fileQuestion<s:property value="#q.id"/>">
		<div class="answer" style="width: 100%;">
			<s:set name="cert" value="%{getCertificate(#a.answer)}" />
			<s:hidden name="certID" value="%{#cert != null ? #cert.id : 0}"/>
			<s:if test="#cert != null">
				<s:date name="#cert.creationDate" format="M/d/yy" /> - <s:property value="#cert.description" /> <br/>
				<a href="CertificateUpload.action?id=<s:property value="contractor.id"/>&certID=<s:property value="#cert.id"/>&button=download"
					target="_BLANK" class="insurance">
					View
				</a>
				<s:if test="#editable">
					&nbsp;&nbsp;&nbsp;&nbsp;
					<a href="#" 
					onclick="if (confirm('Are you sure you want to detach this certificate?')) saveCertQ(<s:property value="#cert.id" />,<s:property value="#q.id"/>,'Detach',<s:property value="#a.id" />,<s:property value="catDataID" />); return false;" 
					class="remove">
						Detach
					</a>
				</s:if>
			</s:if> 
			<s:else>
				No File Attached
			</s:else> 
			<br />
			<s:if test="#editable">
				<a href="#" class="add" onclick="showCertUpload(<s:property value="contractor.id" />, 0, <s:property value="#q.id"/>); return false;" title="Opens in new window (please disable your popup blocker)">Upload New File</a>&nbsp;&nbsp;
				<a href="#" onclick="showCertificates(<s:property value="contractor.id"/>,<s:property value="#q.id"/>,'question'<s:if test="catDataID > 0">,<s:property value="catDataID" /></s:if>); return false;">Attach Existing File</a>
			</s:if>
		</div>
		<div id="certificates<s:property value="#q.id"/>" class="left"></div>
		<br clear="all"/>
		<div class="clear"></div>
	</div>
</s:form>