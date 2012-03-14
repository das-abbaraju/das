<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set name="cao_id"><s:property value="#parameters['id']" /></s:set>

<form class="insurance-rejection-status-form" action="CaoSaveAjax!save.action" method="POST">
    <s:hidden name="cao_id" value="%{#cao_id}" />
    
    <s:textarea name="note" cssClass="notes" />
</form>