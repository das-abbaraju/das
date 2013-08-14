<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<s:hidden id="noteRequired" value="%{noteRequired}" />

<div id="messageDialog" style="text-transform: uppercase;">
	<s:property value="saveMessage" />
</div>

<div class="noteMessage">
	<s:property value="noteMessage"/>
</div>

<div id="formDialog">
	<s:textarea cssClass="clearOnce" rows="5" cols="40" id="addToNotes" name="note" />
</div>

<div id="buttonsDialog">
	<input class="btn approve" disabled="disabled" type="button" value="<s:text name="%{status.getI18nKey('button')}" />" id="yesButton" />

	<input class="btn error" type="button" value="<s:text name="button.Close" />" id="noButton" />
</div>