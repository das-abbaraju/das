<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="../exception_handler.jsp"%>
<html>
<head>
<title>Notes</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
</head>
<body>
<s:include value="../actionMessages.jsp"></s:include>

<s:form id="editNotes">
<s:hidden name="id"/>
<s:hidden name="note.id"/>
<fieldset class="form">
	<div class="buttons">
		<button class="positive" name="button" type="submit" value="saveNote">
			<s:if test="note.id > 0">Update</s:if><s:else>Add</s:else> Note
		</button>
		<button class="negative" name="button" type="button" value="cancel" onclick="window.close();">Close</button>
	</div>
<ol>
	<li><label>Note:</label>
		<s:textfield name="note.summary" maxlength="150" size="60" onkeyup="if (this.value.length > 50) $('liAdditionalText').show(); else $('liAdditionalText').hide();" />
	</li>
	<li><label>Category:</label>
		<span style=""><s:radio theme="pics" list="filter.noteCategoryList" name="note.noteCategory" /></span>
	</li>
	<li><label>Priority:</label>
		<s:radio theme="pics" list="filter.priorityList" name="note.priority" />
	</li>
	<li><label>Can Contractor View:</label>
		<s:checkbox name="note.canContractorView"></s:checkbox>
	</li>
	<li><label>Viewable By:</label>
		<s:radio theme="pics" list="viewableByList" name="viewableBy"></s:radio>
		<s:select list="facilities" listKey="id" listValue="name" name="viewableByOther"></s:select>
	</li>
	<li id="liAdditionalText"><label>Additional Text (optional):</label>
		<s:textarea name="note.body" cols="50" rows="6"></s:textarea>
	</li>
</ol>
</fieldset>
</s:form>
<br clear="all" /><br />

</body>
</html>
