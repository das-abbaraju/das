<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:hidden id="noteRequired" value="%{noteRequired}" />
<div id="messageDialog" style="text-transform: uppercase;">
	<s:property value="saveMessage" />
</div>
<div class="noteMessage"><s:property value="noteMessage"/></div>
<div id="formDialog">
	<s:textarea cssClass="clearOnce" rows="3" cols="30" id="addToNotes" name="note" />
</div>
<div id="buttonsDialog">
	<div class="button <s:property value="status.color" />" id="yesButton" ><s:text name="%{status.getI18nKey('button')}" /></div>
</div>
<input class="picsbutton negative closeButton" type="button" value="<s:text name="button.Close" />" id="noButton" />
