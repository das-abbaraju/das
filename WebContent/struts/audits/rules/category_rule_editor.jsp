<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<link rel="stylesheet" href="css/reports.css"/>
<style>
	.rule-header {
		font-weight: bold;
	}
</style>
<script type="text/javascript">
$(function() {
	$('.rule-header.less-granular').click(function() {
		$('tr.less-granular').not('.rule-header').toggle();
	});
	$('.rule-header.similar-rules').click(function() {
		$('tr.similar-rules').not('.rule-header').toggle();
	});
	$('.rule-header.more-granular').click(function() {
		$('tr.more-granular').not('.rule-header').toggle();
	});
});
</script>
</head>

<body>

<table class="report">
<thead>
	<tr>
		<td>Include</td>
		<td>Audit Type</td>
		<td>Category</td>
		<td>Account Type</td>
		<td>Operator</td>
		<td>Risk</td>
		<td>Tag</td>
		<td>Question</td>
		<td>Question Comparator</td>
		<td>Answer</td>
		<td></td>
	</tr>
</thead>

<s:iterator value="#{'Less Granular': lessGranular, 'Current Rule': rule, 'Similar Rules': similar, 'More Granular': moreGranular}">
	<tr class="rule-header <s:property value="key.toLowerCase().replaceAll(' ', '-')"/>">
		<td colspan="11" class="center"> <s:property value="key"/> </td>
	</tr>
	<s:iterator value="value">
		<tr class="<s:property value="key.toLowerCase().replaceAll(' ', '-')"/>">
			<td><s:property value="include"/></td>
			<td><s:property value="auditTypeLabel"/></td>
			<td><s:property value="auditCategoryLabel"/></td>
			<td><s:property value="contractorTypeLabel"/></td>
			<td><s:property value="operatorAccountLabel"/></td>
			<td><s:property value="riskLabel"/></td>
			<td><s:property value="tagLabel"/></td>
			<td><s:property value="questionLabel"/></td>
			<td><s:property value="questionComparatorLabel"/></td>
			<td><s:property value="questionAnswerLabel"/></td>
			<td>
				<s:if test="'Similar Rules' == key">
					<a href="?button=merge">Merge</a>
				</s:if>
				<s:if test="'More Granular' == key">
					<a href="?button=delete">Delete</a>
				</s:if>
			</td>
		</tr>
	</s:iterator>
</s:iterator>
</table>

</body>
</html>