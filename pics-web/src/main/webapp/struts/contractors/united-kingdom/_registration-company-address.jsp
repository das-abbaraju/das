<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set name="theme" value="'pics'" scope="page" />

<li class="address">
	<s:textfield name="registrationForm.address" label="ContractorAccount.address"/>
</li>

<li class="address">
	<%-- unspecified label overrides strut's assignment of the element's name value as the label--%>
    <s:textfield name="registrationForm.address" label="ContractorAccount.address"/>
</li>

<li class="city">
	<s:textfield name="registrationForm.city" label="global.city"/>
</li>

<li class="zipcode" style="${zip_display}">
	<s:textfield name="registrationForm.zip" label="ContractorAccount.zip" />
</li>