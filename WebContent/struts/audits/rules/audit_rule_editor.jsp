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
		font-size: 15px;
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
	tr.rule-percents {
		height: 0px;
		overflow-y: auto;
	}
	tr.rule-percents td {
		vertical-align:top;
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
		<s:if test="categoryRule"><td>Category</td></s:if>
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
				<td><s:property value="include ? 'Yes' : 'No'"/></td>
				<td><s:property value="auditTypeLabel"/></td>
				<s:if test="categoryRule"><td><s:property value="auditCategoryLabel"/></td></s:if>
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
					<s:if test="'More Granular' == key || 'Current Rule' == key">
						<a class="remove" href="?id=<s:property value="id"/>&button=delete">Delete</a>
					</s:if>
				</td>
			</tr>
		</s:iterator>
		
		<s:if test="'Current Rule' == key">
			<tr class="rule-percents">
				<td></td>
				<td></td>
				<s:if test="categoryRule">
					<td>
						<s:if test="rule.auditCategory == null">
							<table class="inner">
							<s:iterator value="getPercentOn('catID')">
								<s:if test="rule.include ? get('percentOn') < .3 : get('percentOn') > .3">
									<tr class="clickable" onclick="location.href='?id=<s:property value="id"/>&button=create&rule.include=<s:property value="!rule.include"/>&rule.auditCategory.id=<s:property value="get('catID')"/>'">
										<td class="right"><s:property value="get('catID')"/></td>
										<td class="right" title="<s:property value="get('includeTotal')"/> out of <s:property value="get('total')"/>"><s:property value="%{new java.text.DecimalFormat('#,##0.0').format(get('percentOn')*100)}"/>%</td>
									</tr>
								</s:if>
							</s:iterator>
							</table>
						</s:if>
					</td>
				</s:if>
				<td></td>
				<td>
					<s:if test="rule.operatorAccount == null">
						<table class="inner">
						<s:iterator value="getPercentOn('opID')">
							<s:if test="rule.include ? get('percentOn') < .3 : get('percentOn') > .3">
								<tr class="clickable" onclick="location.href='?id=<s:property value="id"/>&button=create&rule.include=<s:property value="!rule.include"/>&rule.operatorAccount.id=<s:property value="get('opID')"/>'">
									<td class="right"><s:property value="get('opID')"/></td>
									<td class="right" title="<s:property value="get('includeTotal')"/> out of <s:property value="get('total')"/>"><s:property value="%{new java.text.DecimalFormat('#,##0.0').format(get('percentOn')*100)}"/>%</td>
								</tr>
							</s:if>
						</s:iterator>
						</table>
					</s:if>
				</td>
				<td>
					<s:if test="rule.risk == null">
						<table class="inner">
						<s:iterator value="getPercentOn('risk')">
							<s:if test="rule.include ? get('percentOn') < .3 : get('percentOn') > .3">
								<tr class="clickable" onclick="location.href='?id=<s:property value="id"/>&button=create&rule.include=<s:property value="!rule.include"/>&rule.risk=<s:property value="@com.picsauditing.jpa.entities.LowMedHigh@getName(get('risk'))"/>'">
									<td class="right"><s:property value="get('risk')"/></td>
									<td class="right" title="<s:property value="get('includeTotal')"/> out of <s:property value="get('total')"/>"><s:property value="%{new java.text.DecimalFormat('#,##0.0').format(get('percentOn')*100)}"/>%</td>
								</tr>
							</s:if>
						</s:iterator>
						</table>
					</s:if>
				</td>
				<td>
					<s:if test="rule.tag == null">
						<table class="inner">
						<s:iterator value="getPercentOn('tagID')">
							<s:if test="rule.include ? get('percentOn') < .3 : get('percentOn') > .3">
								<tr class="clickable" onclick="location.href='?id=<s:property value="id"/>&button=create&rule.include=<s:property value="!rule.include"/>&rule.tag.id=<s:property value="get('tagID')"/>'">
									<td class="right"><s:property value="get('tagID')"/></td>
									<td class="right" title="<s:property value="get('includeTotal')"/> out of <s:property value="get('total')"/>"><s:property value="%{new java.text.DecimalFormat('#,##0.0').format(get('percentOn')*100)}"/>%</td>
								</tr>
							</s:if>
						</s:iterator>
						</table>
					</s:if>
				</td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
			</tr>
		</s:if>
	</s:if>
</s:iterator>
</table>

</body>
</html>