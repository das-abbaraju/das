<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<section>
	<s:text name="ContractorAgreement.content">
		<s:param value="%{@com.picsauditing.util.Strings@getPicsPhone(permissions.country)}" />
	</s:text>
</section>