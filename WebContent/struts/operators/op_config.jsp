<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:property value="operator.name" /> Configuration</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp" />
<script type="text/javascript">
function toggleType(ruleID) {
	var div = "#type_" + ruleID;
	var link = "#type_link_" + ruleID;
	
	$(div).toggle();
	$(link).find('span.toggle').toggle();
	
	return false;
}

function toggleCategory(catID) {
	var ol = "#subcat_" + catID;
	var arrow = ".arrow_" + catID;
	$(ol).slideToggle();
	$(arrow).toggle();

	return false;
}
</script>
<style type="text/css">
.auditTypeRule, .subcat-list {
	display: none;
}
</style>
</head>
<body>
<s:include value="opHeader.jsp" />
<fieldset class="form">
	<h2 class="formLegend">General</h2>
	<ol>
		<li><label>Parent Accounts</label>
			<table class="report">
				<tbody>
					<s:set name="opID" value="%{operator.id}" />
					<s:iterator value="allParents" id="corp">
						<tr>
							<td>
								<a href="FacilitiesEdit.action?id=<s:property value="#corp.id" />">
								<s:property value="#corp.name" /></a>
							</td>
							<td><a href="OperatorConfiguration.action?id=<s:property value="#opID" />&button=Remove&corpID=<s:property value="#corp.id" />" class="remove">Remove</a></td>
						</tr>
					</s:iterator>
				</tbody>
			</table>
			<s:form>
				<s:hidden value="%{operator.id}" name="id" />
				<s:select list="otherCorporates" listValue="name" listKey="id" name="corpID" 
					headerValue="- Add Parent Account -" headerKey="0" />
				<input type="submit" name="button" value="Add" class="picsbutton positive" />
			</s:form>
		</li>
	</ol>
</fieldset>
<fieldset class="form">
	<h2 class="formLegend">Likely Included Audits</h2>
	<ol>
		<li>
			<s:if test="typeList.size > 0">
				<table class="report">
					<thead>
						<tr>
							<th>Category</th>
							<th>Audit</th>
							<th></th>
						</tr>
					</thead>
					<tbody>
						<s:iterator value="typeList" id="type">
							<tr>
								<td><s:property value="#type.classType" /></td>
								<td><s:property value="#type.auditName" /></td>
								<td>
									<a href="#" onclick="return toggleType(<s:property value="#type.id" />);" id="type_link_<s:property value="#type.id" />">
										<span class="toggle">Show Rules</span>
										<span class="toggle" style="display: none;">Hide Rules</span>
									</a>
									<table id="type_<s:property value="#type.id" />" class="auditTypeRule">
										<s:include value="../audits/rules/audit_rule_header.jsp"/>
										<s:set name="ruleURL" value="'AuditTypeRuleEditor.action'"/>
										<s:set name="newWindow" value="true"/>
										<s:set name="categoryRule" value="false"/>
										<s:iterator value="typeMap.get(#type)" id="r">
											<s:include value="../audits/rules/audit_rule_view.jsp"/>
										</s:iterator>
									</table>
								</td>
							</tr>
						</s:iterator>
					</tbody>
				</table>
			</s:if>
		</li>
		<li>
			<s:form id="includeNewAudit">
				<s:hidden value="%{operator.id}" name="id" />
				<s:hidden value="Include" name="button" />
				<s:select list="otherAudits" headerKey="0" headerValue="- Include Another Audit -" 
					listKey="id" listValue="auditName" name="auditTypeID" onchange="$('#includeNewAudit').submit();" />
			</s:form>
		</li>
	</ol>
</fieldset>
<fieldset class="form">
	<h2 class="formLegend">Rules to Explicitly Remove Audits</h2>
	<ol>
		<li>
			<s:if test="excludeTypes.size > 0">
				<table class="report">
					<thead>
						<tr>
							<th>Include</th>
							<th>Audit Type</th>
							<th>Operator</th>
						</tr>
					</thead>
					<tbody>
						<s:iterator value="excludeTypes" id="type">
							<tr class="clickable">
								<td><s:property value="#type.include ? 'Yes' : 'No'" /></td>
								<td><s:property value="#type.auditType == null ? '*' : #type.auditType.auditName" /></td>
								<td><s:property value="#type.operatorAccount == null ? '*' : #type.operatorAccount.name" /></td>
							</tr>
						</s:iterator>
					</tbody>
				</table>
			</s:if>
		</li>
	</ol>
</fieldset>
<fieldset class="form">
	<h2 class="formLegend">Likely Included PQF Categories</h2>
	<s:if test="categoryList.size > 0">
		<ol class="categoryList">
			<s:iterator value="categoryList" id="cat">
				<li>
					<s:property value="#cat.name" />
					<s:if test="#cat.subCategories.size > 0">
						<a href="#" onclick="return toggleCategory(<s:property value="#cat.id" />);">
							<img src="images/arrow-blue-down.png" class="arrow_<s:property value="#cat.id" />" alt="Expand" />
							<img src="images/arrow-blue-right.png" class="arrow_<s:property value="#cat.id" />" alt="Collapse" style="display: none;" />
						</a>
					</s:if>
					<s:if test="#cat.subCategories.size > 0">
						<s:set name="subcat" value="%{#cat}" />
						<div class="subcat"><s:include value="op_config_subcat.jsp" /></div>
					</s:if>
				</li>
			</s:iterator>
		</ol>
	</s:if>
</fieldset>
<fieldset class="form">
	<h2 class="formLegend">Rules to Explicitly Remove PQF Categories</h2>
	<ol>
		<li>
			<s:if test="excludeCategories.size > 0">
				<table class="report">
					<thead>
						<tr>
							<th>Include</th>
							<th>Category</th>
							<th>Operator</th>
						</tr>
					</thead>
					<tbody>
						<s:iterator value="excludeCategories" id="cat">
							<tr class="clickable">
								<td><s:property value="#cat.include ? 'Yes' : 'No'" /></td>
								<td><s:property value="#cat.auditCategory == null ? '*' : #cat.auditCategory.name" /></td>
								<td><s:property value="#cat.operatorAccount == null ? '*' : #cat.operatorAccount.name" /></td>
							</tr>
						</s:iterator>
					</tbody>
				</table>
			</s:if>
		</li>
	</ol>
</fieldset>
</body>
</html>