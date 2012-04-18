<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set name="cao_id">${id}</s:set>
<s:set name="operator_visible">${operator_visible}</s:set>

<form class="insurance-rejection-status-form" action="CaoSaveAjax!save.action" method="POST">
    <s:hidden name="cao_id" value="%{#cao_id}" />
    
    <s:if test="#operator_visible == 'true'">
        <!-- <input class="insurance-rejection-tagit" name="jsonArray" />-->
    </s:if>
    
    <s:textarea name="note" cssClass="notes" />
</form>