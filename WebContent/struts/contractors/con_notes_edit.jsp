<%@ taglib prefix="s" uri="/struts-tags"%>

<s:form id="editNotes" action="ContractorNotes">
<s:hidden name="id"/>
<s:hidden name="note.id"/>
	<fieldset class="form">
	<div id="thinking_noteEdit" style="float: right;"></div>
	<div class="buttons"><button class="positive" name="button" type="submit" value="saveNote">
	<s:if test="note.id > 0">Update</s:if><s:else>Add</s:else> Note
	</button></div>
	<ol>
		<li><label>Note:</label>
			<s:textfield name="note.summary" maxlength="150" size="75" onkeyup="if (this.value.length > 50) $('liAdditionalText').show(); else $('liAdditionalText').hide();" /> max 150 chars
		</li>
		<li><label>Category:</label>
			<s:radio theme="pics" list="noteCategoryList" name="note.noteCategory" />
		</li>
		<li><label>Priority:</label>
			<s:radio theme="pics" list="priorityList" name="note.priority" />
		</li>
		<li><label>Can Contractor View:</label>
			<s:checkbox name="note.canContractorView"></s:checkbox>
		</li>
		<li><label>Viewable By:</label>
			<s:select list="facilities" listKey="id" listValue="name" name="note.account.id"></s:select>
		</li>
		<li id="liAdditionalText" style="display: none;"><label>Additional Text (optional):</label>
			<s:textarea name="note.body" cols="45" rows="5"></s:textarea>
		</li>
	</ol>
	</fieldset>
</s:form>
