<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title><s:property value="reportName" /></title>
<s:include value="reportHeader.jsp" />
<script type="text/javascript" src="js/prototype.js"></script>
<script type="text/javascript"
	src="js/scriptaculous/scriptaculous.js?load=effects,controls"></script>
</head>
<body>
<h1><s:property value="reportName" /></h1>

<div id="search">
	<s:form id="form1" method="post" cssStyle="background-color: #F4F4F4;">
		<s:hidden name="filter.ajax" />
		<s:hidden name="filter.destinationAction" />
		<s:hidden name="filter.allowMailMerge" />
		<s:hidden name="showPage" value="1" />
		<s:hidden name="filter.startsWith" />
		<s:hidden name="orderBy" />
	
		<div class="filterOption">
			Select a Contractor :
			<s:textfield id="selected_contractor" cssClass="forms" name="filter.accountName" size="60" onfocus="clearText(this)"/> 
			<div id="selected_contractor_choices" class="autocomplete"></div>
			<script type="text/javascript">
				new Ajax.Autocompleter('selected_contractor', 'selected_contractor_choices', 'ContractorSelectAjax.action', {
					tokens: ',',
					paramName: "filter.accountName",
					minChars: 3
				});
			</script>
		</div>

		<br clear="all"/>
		<div class="filterOption">
			Invoice ID :
			<s:textfield cssClass="forms" name="invoiceID" size="15" /> 
		</div>
		<div class="buttons">
			<input type="submit" class="picsbutton positive" name="button" value="Search"/>
		</div>
</s:form>
</div>
<div id="caldiv2" style="position:absolute; visibility:hidden; background-color:white; layer-background-color:white;"></div>

<div><s:property value="report.pageLinksWithDynamicForm"
	escape="false" /></div>
	
<table class="report">
	<thead>
	<tr>
		<td></td>
	    <th><a href="javascript: changeOrderBy('form1','a.name');" >Contractor</a></th>
	    <th>Address</th>
		<th><a href="javascript: changeOrderBy('form1','i.id');">Invoice #</a></th>
		<th><a href="javascript: changeOrderBy('form1','totalAmount DESC');">Invoice Total</a></th>
		<th>Balance</th>
		<th><a href="javascript: changeOrderBy('form1','dueDate');">Due Date</a></th>
	</tr>
	</thead>
	<tbody>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td><a href="ContractorView.action?id=<s:property value="get('id')"/>"><s:property value="get('name')" /></a></td>
			<td><s:property value="get('address')"/><br/>
				<s:property value="get('city')"/>, <s:property value="get('state')"/> <s:property value="get('zip')"/></td>
			<td class="center"><a href="PaymentDetail.action?id=<s:property value="get('id')"/>"><s:property value="get('invoiceId')"/></a></td>
			<td class="right">$<s:property value="get('totalAmount')"/></td>
			<td class="right">$<s:property value="get('totalAmount')- get('amountApplied')"/></td>
			<td class="right"><s:date name="get('dueDate')" format="M/d/yy"/></td>
		</tr>
	</s:iterator>
	</tbody>
</table>

</body>
</html>
