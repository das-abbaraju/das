<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<section>
    <s:text name="ContractorAgreement.content">
		<s:param><span class="pics_phone_number">${picsPhoneNumber}</span></s:param>
        <s:param><span class="pics_display_name">${picsDisplayName}</span></s:param>
        <s:param><span class="pics_address">${picsAddress}</span></s:param>
    </s:text>
</section>
