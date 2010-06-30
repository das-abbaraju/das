<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title><s:property value="subHeading" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/menu1.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/autocomplete/jquery.autocomplete.css" />
<style type="text/css">
.hidden {
	display: none;
}
</style>
<s:include value="../reportHeader.jsp" />
<script type="text/javascript" src="js/jquery/autocomplete/jquery.autocomplete.min.js"></script>
<script type="text/javascript">
$(function () {
<s:if test="unmapped.keySet().size() > 0"><s:iterator value="unmapped.keySet()" id="companies">
	$('#company_<s:property value="unmapped.get(#companies).get(0).id" />').autocomplete('ContractorSelectAjax.action', 
		{
			minChars: 3,
			extraParams: {'filter.accountName': function() {
				return $('#company_<s:property value="unmapped.get(#companies).get(0).id" />').val();} 
			},
			formatResult: function(data,i,count) { return data[0]; }
		}
	).result(function(event, data){
		$('input#accountID').val(data[1]);
		$('input#companyName').val('<s:property value="unmapped.get(#companies).get(0).companyName" />');
	});
</s:iterator></s:if>
});
function add(row) {
	cancel();

	$('tr#' + row + ' input.add').val('');
	$('tr#' + row + ' .hidden').show();
	$('tr#' + row + ' .add').hide();
}

function cancel() {
	$('.hidden').hide();
	$('.add').show();
	$('input#accountID').val(0);
}
</script>
</head>
<body>

<s:include value="assessmentHeader.jsp" />

<s:form>
<s:hidden name="id" />
<input type="hidden" name="accountID" value="0" id="accountID" />
<input type="hidden" name="companyName" value="0" id="companyName" />
<table class="report">
	<thead>
		<tr>
			<th>Imported Company</th>
			<th># of Records</th>
			<th>Match PICS Company</th>
		</tr>
	</thead>
	<tbody>	
		<s:iterator value="unmapped.keySet()" status="stat" id="companies">
			<tr id="<s:property value="unmapped.get(#companies).get(0).id" />">
				<td><s:property value="#companies" /></td>
				<td class="right"><s:property value="unmapped.get(#companies).size()" /></td>
				<td class="center">
					<a href="#" onclick="add(<s:property value="unmapped.get(#companies).get(0).id" />); return false;" class="add"></a>
					<s:textfield cssClass="hidden" id="company_%{unmapped.get(#companies).get(0).id}" />
					<input type="submit" name="button" value="Save" class="hidden picsbutton positive" />
					<input type="button" value="Cancel" class="hidden picsbutton" onclick="cancel();" />
				</td>
			</tr>
		</s:iterator>
		<s:if test="unmapped.size() == 0">
			<tr>
				<td colspan="3">No unmapped companies currently</td>
			</tr>
		</s:if>
	</tbody>
</table>
</s:form>

</body>
</html>