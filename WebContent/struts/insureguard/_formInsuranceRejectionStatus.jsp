<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<s:set name="cao_id">${id}</s:set>
<%-- <s:set name="operator_visible">${operator_visible}</s:set> --%>

<form class="insurance-rejection-status-form" action="CaoSaveAjax!save.action" method="POST">
    <s:hidden name="cao_id" value="%{#cao_id}" />
    
    <pics:toggle name="AuditRejection">
    	<input class="insurance-rejection-tagit" name="jsonArray" />
    </pics:toggle>
    
    <ul id='rejection_tag_list'></ul>

    <s:textarea name="note" cssClass="notes" />
</form>