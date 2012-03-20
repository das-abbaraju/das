<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:text name="ReportActivityWatch.title" /></title>
<s:include value="reportHeader.jsp" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<style type="text/css">
#search {
	margin-bottom: 10px;
}

#addWatch {
	display: none;
	width: 350px;
}

fieldset.form ol li label {
	width: auto;
}

.leftSide {
	min-width: 400px;
	padding-right: 10px;
	vertical-align: top;
}

* html .leftSide {
	width: 400px;
}
</style>
<script type="text/javascript">
function clearFilterCheckboxes() {
	$("#audits").attr('checked', false);
	$("#flagColorChange").attr('checked', false);
	$("#login").attr('checked', false);
	$("#notesAndEmail").attr('checked', false);
	$("#flagCriteria").attr('checked', false);
}
$(function() {
	$('#newContractor').autocomplete('ContractorSelectAjax.action', 
		{
			minChars: 3,
			extraParams: {'filter.accountName': function() {return $('#newContractor').val();} },
			formatResult: function(data,i,count) { return data[0]; }
		}
	).result(function(event, data){
		$('input#findConID').val(data[1]);
	});
	
	$('#addLink').live('click', function(e) {
		e.preventDefault();
		$(this).hide();
		$('#addWatch').show();
	});
	
	$('.picsbutton .cancelButton').live('click', function(e) {
		e.preventDefault();
		$('#addWatch').hide();
		$('#addLink').show();
	});
	
	$('a.remove').live('click', function() {
		return confirm(translate('JS.ReportActivityWatch.confirm.RemoveContractor'));
	});
});
</script>
</head>
<body>
<h1><s:text name="ReportActivityWatch.title" /></h1>
<s:include value="../actionMessages.jsp"></s:include>
<div id="search"><s:form>
	<s:hidden name="conID" />
	<div>
		<input id="searchfilter" type="submit" value="<s:text name="button.Search" />" class="picsbutton positive" />
	</div>
	<div class="filterOption">
		<s:textfield name="filter.accountName" cssClass="forms" size="17" onfocus="clearText(this)" />
	</div>
	<div class="filterOption">
		<a href="#" onclick="toggleBox('form1_auditStatus'); return false;"><s:text name="AuditStatus" /></a> =
		<span id="form1_auditStatus_query"><s:text name="JS.Filters.status.All" /></span><br />
		<span id="form1_auditStatus_select" style="display: none" class="clearLink">
			<s:select id="form1_auditStatus" list="filter.auditStatusList" cssClass="forms"
				name="filter.auditStatus" multiple="true" size="5" />
			<br />
			<a class="clearLink" href="#" onclick="clearSelected('form1_auditStatus'); return false;">
				<s:text name="Filters.status.Clear" />
			</a>
		</span>
	</div>
	<br clear="all" />
	<div class="filterOption">
		<s:checkbox value="audits" name="audits" id="audits"></s:checkbox>
		<label for="audits"><s:text name="ReportActivityWatch.label.PQFAudits" /></label>
	</div>
	<div class="filterOption">
		<s:checkbox value="flagColorChange" name="flagColorChange" id="flagColorChange"></s:checkbox>
		<label for="flagColorChange"><s:text name="ReportActivityWatch.label.FlagChanges" /></label>
	</div>
	<div class="filterOption">
		<s:checkbox value="login" name="login" id="login"></s:checkbox>
		<label for="login"><s:text name="ReportActivityWatch.label.UserLogins" /></label>
	</div>
	<div class="filterOption">
		<s:checkbox value="notesAndEmail" name="notesAndEmail" id="notesAndEmail"></s:checkbox>
		<label for="notesAndEmail"><s:text name="ReportActivityWatch.label.NotesAndEmails" /></label>
	</div>
	<div class="filterOption">
		<s:checkbox value="flagCriteria" name="flagCriteria" id="flagCriteria"></s:checkbox>
		<label for="flagCriteria"><s:text name="ReportActivityWatch.label.FlagCriteria" /></label>
	</div>
	<div>&nbsp;&nbsp;<a class="clearLink" href="#" onclick="clearFilterCheckboxes(); return false;"><s:text name="button.Clear" /></a></div>
	<br clear="all" />
</s:form></div>

<table>
	<tr>
		
			<s:if test="contractor == null">
				<td class="leftSide">
					<s:if test="watched.size() > 0">
						<table class="report">
							<thead>
								<tr>
									<th><s:text name="global.Contractor" /></th>
									<th><s:text name="button.Remove" /></th>
								</tr>
							</thead>
							<tbody>
								<s:iterator value="watched" id="watch">
									<tr>
										<td><a href="ContractorView.action?id=<s:property value="contractor.id" />"><s:property value="contractor.name" /></a></td>
										<td class="center"><a href="ReportActivityWatch!remove.action?contractorWatch=<s:property value="#watch.id" />" class="remove"></a></td>
									</tr>
								</s:iterator>
							</tbody>
						</table>
					</s:if>
					<s:else>
						<div class="info"><s:text name="ReportActivityWatch.help.AddNewContractor" /></div>
					</s:else>
					<a href="#" id="addLink" class="add"><s:text name="ReportActivityWatch.link.AddNewContractor" /></a>
					<s:form id="addWatch">
						<input type="hidden" id="findConID" name="contractor" value="0" />
						<fieldset class="form">
							<h2 class="formLegend"><s:text name="ReportActivityWatch.link.AddNewContractor" /></h2>
							<ol>
								<li>
									<label><s:text name="global.ContractorName" />:</label>
									<s:textfield id="newContractor" />
								</li>
							</ol>
						</fieldset>
						<fieldset class="form submit">
							<s:submit method="add" value="%{getText('button.Add')}" cssClass="picsbutton positive" />
							<input type="button" value="<s:text name="button.Cancel" />" class="picsbutton negative cancelButton" />
						</fieldset>
					</s:form>
				</td>
			</s:if>
	

		<td>
			<s:if test="data.size() > 0">
				<table class="report">
					<thead>
						<tr>
							<th><s:text name="global.Contractor" /></th>
							<th><s:text name="ReportActivityWatch.label.ActivityDetails" /></th>
							<th><s:text name="ReportActivityWatch.label.ActivityDate" /></th>
						</tr>
					</thead>
					<tbody>
						<s:iterator value="data" status="stat">
						<tr>
							<td><s:property value="get('name')" /></td>
							<td>
								<s:if test="get('activityType').toString().endsWith('Audits')">
									<s:set name="activityDetail">
										<s:text name="%{get('activityType')}">
											<s:param><s:text name="AuditType.%{get('v1')}.name"/></s:param>
											<s:param>
												<s:if test="get('v2') != null && get('v2').toString().startsWith('FlagCriteria')"><s:text name="%{get('v2')}" /></s:if>
												<s:else><s:property value="%{get('v2')}" /></s:else>
											</s:param>
											<s:param value="%{get('v3')}" />
											<s:param value="%{@java.lang.Integer@parseInt(get('v4'))}" />
										</s:text>
									</s:set>
								</s:if>
								<s:else>										
									<s:set name="activityDetail">
										<s:text name="%{get('activityType')}" >
											<s:param value="%{get('v1')}" />
											<s:param>
												<s:if test="get('v2') != null && get('v2').toString().startsWith('FlagCriteria')"><s:text name="%{get('v2')}" /></s:if>
												<s:else><s:property value="%{get('v2')}" escapeHtml="false"/></s:else>
											</s:param>
											<s:param value="%{get('v3')}" />
											<s:param value="%{@java.lang.Integer@parseInt(get('v4'))}" />
										</s:text>
									</s:set>
								</s:else>
								<s:if test="get('url').length() > 0">
									<a href="<s:property value="get('url')" />">
										<s:property value="#activityDetail"/>
									</a>
								</s:if>
								<s:else>
									<s:property value="#activityDetail"/>
								</s:else>
							</td>
							<td><span title="<s:date name="get('activityDate')" nice="true" />"><s:date name="get('activityDate')" /></span></td>
						</tr>
					</s:iterator>
					</tbody>
				</table>
			</s:if>
		</td>
	</tr>
</table>

</body>
</html>
