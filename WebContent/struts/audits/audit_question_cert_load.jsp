<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"  errorPage="/exception_handler.jsp" %>

<s:set name="editable" value="mode == 'Edit' || mode == 'Verify'"/>
<s:set name="cert" value="%{getCertificate(#a)}" />
<s:if test="#cert != null">
	<s:date name="#cert.creationDate" format="%{@com.picsauditing.util.PicsDateFormat@Iso}" /> - <s:property value="#cert.description" /> <br/>
	<a href="CertificateUpload.action?id=<s:property value="contractor.id"/>&certID=<s:property value="#cert.id"/>&button=download"
		target="_BLANK" class="insurance"><s:text name="button.View" />
	</a>
	<s:if test="#editable">
		&nbsp;&nbsp;&nbsp;&nbsp;
		<a href="#" class="detachCertificate remove"><s:text name="Audit.message.Detach" /></a>
	</s:if>
</s:if> 
<s:else>
	<s:text name="Audit.message.NoFileAttached" />
</s:else> 
<br />
<s:if test="#editable">
	<s:hidden name="certID" value="%{#cert != null ? #cert.id : 0}"/>
	<s:hidden name="auditData.answer" value="%{#a.answer}"/>
	<a href="#" class="add uploadNewCertificate" title="<s:text name="Audit.help.OpensNewWindow" />"><s:text name="Audit.link.UploadNewFile" /></a>
	<a href="#" class="showExistingCertificates"><s:text name="Audit.link.AttachExistingFile" /></a>
	<br clear="all"/>
	<div id="certificates<s:property value="#q.id"/>" class="certificateContainer left"></div>
</s:if>
