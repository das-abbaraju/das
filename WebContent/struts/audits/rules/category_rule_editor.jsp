<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<link rel="stylesheet" href="css/reports.css"/>
<style>
	tr.less-granular td {
		font-style: italic;
	}
	tr.less-granular.on {
		background-color: #9DEB95;	
	}
	tr.less-granular.off {
		background-color: #FFABA6;	
	}
	tr.current-rule td {
		font-weight: bold;
	}
	tr.current-rule.on {
		background-color: #9DEB95;
	}
	tr.current-rule.off {
		background-color: #FFABA6;
	}
	tr.similar-rules.on {
		background-color: maroon;	
	}
	tr.similar-rules.off {
		background-color: navy;	
	}
	tr.more-granular.on {
		background-color: #9DEB95;	
	}
	tr.more-granular.off {
		background-color: #FFABA6;	
	}
	tr.rule-header td {
		font-weight: bold;
		font-size: 120%;
		background-color: #f9f9f9;	
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
		<td>Account</td>
		<td>Operator</td>
		<td>Risk</td>
		<td>Tag</td>
		<td>Question</td>
		<td></td>
		<td>Answer</td>
		<td></td>
	</tr>
</thead>

<s:iterator value="#{'Less Granular': lessGranular, 'Current Rule': rule, 'Similar Rules': similar, 'More Granular': moreGranular}">
	<s:if test="'Current Rule' == key || value.size() > 0">
		<tr class="rule-header clickable <s:property value="key.toLowerCase().replaceAll(' ', '-')"/>">
			<td colspan="11" class="center"> <s:property value="key"/> </td>
		</tr>
		<s:iterator value="value" id="r">
			<tr class="<s:property value="key.toLowerCase().replaceAll(' ', '-')"/> clickable<s:if test="include"> on</s:if><s:else> off</s:else>" onclick="location.href='?id=<s:property value="#r.id"/>'">
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
						<a class="remove" href="?button=delete">Delete</a>
					</s:if>
				</td>
			</tr>
		</s:iterator>
	</s:if>
</s:iterator>
</table>

</body>
</html>