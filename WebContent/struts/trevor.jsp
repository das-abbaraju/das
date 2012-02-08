<%@ taglib prefix="s" uri="/struts-tags" %>

<h1>Hello World</h1>
        
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/tagit/jquery.tagit.css" />
<script type="text/javascript" src="js/jquery/tagit/jquery.tagit.js"></script>

<s:form>
    <label for="tag" style="float: left;">Tag:</label>
    <input type="hidden" name="id" value="1" />
    <input id="tag" type="text" name="auditName" style="width: 400px" />
    <input type="submit" value="Submit" />
</s:form>

<script>
$(document).ready(function () {
    $('#tag').tagit({
        postType: 'string',
        source: 'Trevor!getItemsInJson.action?optionGroupId=27',
        source_selected: 'Trevor!getItemsInJson.action?optionGroupId=27'
    });
});
</script>