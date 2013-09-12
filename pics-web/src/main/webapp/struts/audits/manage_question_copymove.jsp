<%@ taglib prefix="s" uri="/struts-tags"%>
<s:include value="../actionMessages.jsp"/>

<script type="text/javascript"> 
$(document).ready(function (){ 
    $("#audit_subcategory_autocomplete").mcDropdown("#allCategories"); 
}); 
</script>

<s:form id="textForm">
	<s:hidden name="id" />
	<s:hidden name="parentID" value="%{category.id}" />
	<s:hidden name="category.id" />

	<fieldset class="form" style="border: none">
		<ol>
			<li><label>Category:</label>
				<s:textfield id="audit_subcategory_autocomplete" name="targetID" size="50" />
			</li>
		</ol>
	</fieldset>
</s:form>