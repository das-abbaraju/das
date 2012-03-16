<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<form class="insurance-rejection-status-form" action="CaoSaveAjax!save.action" method="POST">
    <s:hidden name="cao_id" value="%{#id}" />
    
    <s:textarea name="note" cssClass="notes" />
</form>