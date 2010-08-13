<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<script type="text/javascript">
function changePage(form, start){
	var data = {
		button: 'search',
		startIndex: (start-1)*100,
		searchTerm: $('#hiddenSearchTerm').val()
	};
	startThinking( {div: 'pageResults', message: 'Getting Results', type: 'large' } );
	$('#pageResults').load('SearchAjax.action #pageResults', data);
}
</script>
<h2>Search Results</h2>
<s:hidden id="hiddenSearchTerm" value="%{searchTerm}" />
<div id="filterSuggest">
	<div id="info" style="">You searched for: <s:property value="searchTerm" /><br/>
		<s:if test="commonFilterSuggest.size() > 0">
			Try adding
			<s:iterator value="commonFilterSuggest" id="sug">
				<a href="Search.action?button=search&searchTerm=<s:property value="searchTerm.replace(' ','+')"/>+<s:property value="#sug.replace(' ','-').toLowerCase()"/>"><s:property value="#sug.toLowerCase()"/></a> 
			</s:iterator>
			to your search?
		</s:if>
	</div>
</div>
<div id="pageResults">
	<div id="pageLinks"><s:property value="pageLinks" escape="false"/></div>
		<table class="report">
			<thead>
				<tr>
					<td>Type</td>
					<td>Result</td>
				</tr>
			</thead>
			<s:iterator value="fullList" id="result" status="row">
				<tr>
					<s:if test="#result.returnType=='account'">
						<td><s:property value="#result.type"/></td>
						<td><a href="<s:property value="#result.getViewLink()"/>"><s:property value="#result.name"/></a></td>
					</s:if>
					<s:if test="#result.returnType=='user'">
						<td>User<s:if test="#result.isGroup()"> Group</s:if></td>
						<td><a href="<s:property value="#result.getViewLink()"/>"><s:property value="#result.name"/> at <s:property value="#result.account.name"/></a></td>
					</s:if>
					<s:if test="#result.returnType=='employee'">
						<td>Employee</td>
						<td><a href="<s:property value="#result.getViewLink()"/>"><s:property value="#result.displayName"/> at <s:property value="#result.account.name"/></a></td>
					</s:if>
				</tr>
			</s:iterator>
		</table>
	<div id="pageLinks"><s:property value="pageLinks" escape="false"/></div>
</div>