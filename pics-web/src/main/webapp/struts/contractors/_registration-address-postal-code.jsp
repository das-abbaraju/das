<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set name="theme" value="'pics'" scope="page" />

<li class="address">
    <label><s:text name="ContractorAccount.address" /></label>
    <s:textarea name="registrationForm.address" rows="3" />
</li>

<s:if test="#country_iso_code != 'AE'">
    <li class="zipcode">
        <s:textfield name="registrationForm.zip" label="ContractorAccount.zip" />
    </li>
</s:if>