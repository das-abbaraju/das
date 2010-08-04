<%@ taglib prefix="s" uri="/struts-tags"%>
<s:include value="../actionMessages.jsp"/>

<script type="text/javascript" src="js/jquery/autocomplete/jquery.autocomplete.min.js"></script>
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/autocomplete/jquery.autocomplete.css" />
<script>
$(function(){
	$('#audit_category_autocomplete').autocomplete('AuditCategorySuggestAjax.action',
	{
		minChars: 3,
		formatResult: function(data,i,count) {
			return data[1];
		}
	})
});
</script>
<s:form id="textForm">

	<s:hidden name="id" />
	<s:hidden name="parentID" value="%{subCategory.category.id}" />
	<s:hidden name="subCategory.category.id" />

	<fieldset class="form" style="border: none">
		<h2 class="formLegend">Copy To:</h2>
		<ol>
			<li><label>Category:</label>
				<s:textfield id="audit_category_autocomplete" name="targetID" value="%{#a.answer}" size="50" />
			</li>
		</ol>
	</fieldset>

	<fieldset class="form">
	<h2 class="formLegend">Sub Category</h2>
	<ol>
		<li><label>ID:</label>
			<s:if test="subCategory.id > 0">
				<s:property value="subCategory.id" />
			</s:if>
			<s:else>
				NEW
			</s:else>
		</li>
		<li><label>Sub Category Name:</label>
			<s:textfield disabled="true" name="subCategory.subCategory" size="50" />
		</li>
		<li><label>Countries:</label>
			<s:hidden name="countries" value="%{subCategory.countries}"/>
			<s:textfield disabled="true" size="50" cssClass="countries"/>
		</li>
		<li><label>Exclude Countries:</label>
			<s:checkbox disabled="true" name="exclude" label="Exclude Countries" value="subCategory.countries.startsWith('!')" />
		</li>
		<li><label>Help Text:</label>
			<s:textarea disabled="true" name="subCategory.helpText" rows="3" cols="50"/>
		</li>
	</ol>
	</fieldset>

</s:form>
