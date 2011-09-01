<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<section>
	<h1 align="center"><s:text name="ContractorPaymentOptions.ContractorAgreement" /></h1>
	
	<s:text name="ContractorAgreement.content">
		<s:param value="%{permissions.picsPhone}" />
	</s:text>
</section>