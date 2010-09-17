<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<div id="ajaxDialog"  style="display:none; cursor: default; height: 250px;">
	<div id="messageDialog">
		<s:property value="saveMessage" />
		<s:property value="stepID" />
	</div>
	<div id="formDialog">
		<s:property value="noteMessage" />
		<textarea rows="3" cols="30" id="addToNotes"></textarea>
	</div>
	<input class="picsbutton positive" type="button" value="Save" id="yesButton" />
	<input class="picsbutton negative" type="button" value="Cancel" id="noButton" />
</div>