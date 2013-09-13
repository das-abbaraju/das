<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<h4><s:text name="%{subscription.getI18nKey('description')}"/></h4>

<div>
	<s:text name="%{subscription.getI18nKey('longDescription')}"/>
</div>

<s:radio
	list="subscription.supportedTimePeriods"
	listValue="getText(i18nKey)"
	id="timePeriod_%{subscription}"
	value="%{timePeriod}"
	onclick="save('%{subscription}', %{id}, this)"
	theme="pics"
	cssClass="inline"
/>