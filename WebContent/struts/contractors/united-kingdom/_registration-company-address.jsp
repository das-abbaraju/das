<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set name="theme" value="'pics'" scope="page" />

<li class="address">
	<s:textfield name="contractor.address" />
</li>

<li class="address">
    <s:textfield name="contractor.address2" label="&nbsp;" />
</li>

<li class="city">
	<s:textfield name="contractor.city" label="Post Town"/>
</li>

<li class="zipcode" style="${zip_display}">
	<s:textfield name="contractor.zip" label="Postcode" />
</li>