<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set name="theme" value="'pics'" scope="page" />

<li class="address">
	<s:textfield name="contractor.address" />
</li>

<li class="address">
    <s:textfield name="contractor.address2" label="" cssClass="no-label"/>
</li>

<li class="city">
	<s:textfield name="contractor.city" label="global.city"/>
</li>

<li class="zipcode" style="${zip_display}">
	<s:textfield name="contractor.zip" label="global.ZipPostalCode" />
</li>