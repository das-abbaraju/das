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
<s:if test="data.size() > 0"><s:iterator value="data">
	$('#company_' + <s:property value="get('id')" />).autocomplete('ContractorSelectAjax.action', 
		{
			minChars: 3,
			extraParams: {'filter.accountName': function() {
				return $('#company_' + <s:property value="get('id')" />).val();} 
			},
			formatResult: function(data,i,count) { return data[0]; }
		}
	).result(function(event, data){
		$('input#accountID').val(data[1]);
		$('input#companyName').val('<s:property value="get('companyName')" />');
	});
</s:iterator></s:if>
});

function edit(row) {
	cancel();

	$('tr#' + row + ' .hidden').show();
	$('tr#' + row + ' .edit').hide();
	$('tr#' + row + ' #company_' + row).val($('tr#' + row + ' span.edit').text());
	$('#companyName').val($('tr#' + row + ' span.name').text());
}

function cancel() {
	$('.edit').show();
	$('.hidden').hide();
	$('#accountID').val(0);
	$('#companyName').val('');
}
</script>
</head>
<body>

<s:include value="assessmentHeader.jsp" />

<s:form>
	<s:hidden name="id" />
	<input type="hidden" name="accountID" value="0" id="accountID" />
	<input type="hidden" name="companyName" value="0" id="companyName" />
	<div><s:property value="report.pageLinksWithDynamicForm" escape="false" /></div>
	<table class="report">
		<thead>
			<tr>
				<th>Mapped Company</th>
				<th># of Records</th>
				<th>PICS Company</th>
				<th>Edit</th>
			</tr>
		</thead>
		<tbody>	
			<s:iterator value="data">
				<tr id="<s:property value="get('id')" />">
					<td><span class="name"><s:property value="get('companyName')" /></span></td>
					<td class="right"><s:property value="get('records')" /></td>
					<td>
						<span class="edit"><s:property value="get('name')" /></span>
						<input type="text" value="<s:property value="get('name')" />" onclick="this.value='';" 
							id="company_<s:property value="get('id')" />" class="hidden" />
					</td>
					<td class="center">
						<a href="#" onclick="edit(<s:property value="get('id')" />); return false;" class="edit"></a>
						<input type="submit" value="Save" name="button" class="picsbutton positive hidden" />
						<input type="submit" value="Remove" name="button" class="picsbutton negative hidden" />
						<input type="button" value="Cancel" class="picsbutton hidden" onclick="cancel();" />
					</td>
				</tr>
			</s:iterator>
			<s:if test="data.size() == 0">
				<tr><td colspan="4">No records found</td></tr>
			</s:if>
		</tbody>
	</table>
	<div><s:property value="report.pageLinksWithDynamicForm" escape="false" /></div>
</s:form>

</body>
</html>