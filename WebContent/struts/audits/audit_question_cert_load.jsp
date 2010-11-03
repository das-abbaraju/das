<%@ taglib prefix="s" uri="/struts-tags"%>

<s:set name="editable" value="mode == 'Edit' || mode == 'Verify'"/>
<s:set name="cert" value="%{getCertificate(#a)}" />
<s:if test="#cert != null">
	<s:date name="#cert.creationDate" format="M/d/yy" /> - <s:property value="#cert.description" /> <br/>
	<a href="CertificateUpload.action?id=<s:property value="contractor.id"/>&certID=<s:property value="#cert.id"/>&button=download"
		target="_BLANK" class="insurance">
		View
	</a>
	<s:if test="#editable">
		&nbsp;&nbsp;&nbsp;&nbsp;
		<a href="#" class="detachCertificate remove">Detach</a>
	</s:if>
</s:if> 
<s:else>
	No File Attached
</s:else> 
<br />
<s:if test="#editable">
	<s:hidden name="certID" value="%{#cert != null ? #cert.id : 0}"/>
	<s:hidden name="auditData.answer" value="%{#a.answer}"/>
	<a href="#" class="add uploadNewCertificate" title="Opens in new window (please disable your popup blocker)">Upload New File</a>
	<a href="#" class="showExistingCertificates">Attach Existing File</a>
	<br clear="all"/>
	<div id="certificates<s:property value="#q.id"/>" class="certificateContainer left"></div>
</s:if>
