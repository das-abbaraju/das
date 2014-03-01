<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:url action="" method="" var="manual_audit_url" />

<div class="alert alert-info">
	<s:text name="ContractorCertificate.SSIP.SavingChangesMessage" />
</div>

<button type="submit" class="btn-success"><s:text name="ContractorCertificate.SSIP.Save" /></button>
<a href="${manual_audit_url}" class="btn-default"><s:text name="ContractorCertificate.SSIP.Cancel" /></a>