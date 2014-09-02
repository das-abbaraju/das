<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set var="section_title">Forms</s:set>
<s:set var="section_id_prefix">forms</s:set>

<s:include value="complex-form/_complex-form.jsp" />
<s:include value="single-line-text-field/_single-line-text-field.jsp" />
<s:include value="single-line-text-field-with-error/_single-line-text-field-with-error.jsp" />
<s:include value="single-line-text-input-with-tooltip/_single-line-text-input-with-tooltip.jsp" />
<s:include value="multi-line-text-field/_multi-line-text-field.jsp" />
<s:include value="single-select-dropdown/_single-select-dropdown.jsp" />
<s:include value="date-field/_date-field.jsp" />
<s:include value="multi-select-dropdown/_multi-select-dropdown.jsp" />
<s:include value="radio-button/_radio-button.jsp" />
<s:include value="checkbox-field/_checkbox-field.jsp" />
<s:include value="toggle-switch/_toggle-switch.jsp" />
<s:include value="action-button/_action-button.jsp" />