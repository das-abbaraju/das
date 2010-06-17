<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<h1 align="center">Confirm Before Removing Tag!</h1>
<script>
function removeTag(id){
	jQuery.get('ContractorTagsAjax.action',{tagID: id, button: 'Remove Tag'});
}
</script>

<div style="width: 475px; height: 10em;">
	This tag is associated with <s:property value="result" /> contractor(s), are you sure you want to remove this tag?
	<form method="post">
	    <s:hidden name="tagID" value="%{tagID}" />
	    <s:hidden name="result" value="%{result}" />
		<input type="submit" class="picsbutton negative" name="button" value="Remove Tag" />
		<input type="submit" class="picsbutton" value="Cancel" onclick="$.trigger('close.facebox')" />
	</form>
</div>