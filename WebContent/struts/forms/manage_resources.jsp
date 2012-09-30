<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title><s:text name="global.Resources" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/pics.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />

<s:include value="../jquery.jsp"/>

<script type="text/javascript">
$(function() {
	$('#operator').autocomplete('OperatorAutocomplete.action', {
		formatItem  : function(data,i,count) {
			return data[1];
		},
		formatResult: function(data,i,count) {
			return data[2];
		},
		max	: 50
	});
});
</script>
</head>
<body>
<h1><s:text name="global.Resources" /></h1>

<s:include value="../actionMessages.jsp" />


<s:if test="parentId!=0">
	<a href="ManageResources.action?id=<s:property value="parentId"/>" ><s:text name="ManageResources.ToResource" /></a>
</s:if>
<s:else>
	<a href="Resources.action" ><s:text name="ManageResources.ToList" /></a> 
</s:else>


<s:set name="canManageForm" value="permissions.admin || permissions.hasPermission(@com.picsauditing.access.OpPerms@FormsAndDocs, 'Edit')" />
<s:form id="save" method="POST" enctype="multipart/form-data" cssClass="form" >
	<s:hidden name="id" />
	<s:hidden name="parentId" />
	<fieldset class="form">
	<s:if test="id==0 && parentId==0">
		<h2 class="formLegend"><s:text name="ManageResources.AddResource" /></h2>
	</s:if>
	<s:elseif test="id==0 && parentId!=0">
		<h2 class="formLegend"><s:text name="ManageResources.AddFile" ><s:param><s:property value="parentResource.formName" /></s:param></s:text></h2>
	</s:elseif>
	<s:elseif test="id!=0 && parentId!=0">
		<h2 class="formLegend"><s:text name="ManageResources.EditFile" /></h2>
	</s:elseif>
	<s:else>
		<h2 class="formLegend"><s:text name="ManageResources.UpdateResource" /></h2>
	</s:else>
	<ol>
		<li>
			<label><s:text name="global.Operator" />: </label>
			<s:if test="id==0 && parentId==0">
				<s:if test="permissions.admin" >
					<s:textfield name="accountName" id="operator" cssClass="autocomplete" value="%{account.name}" />
					<div id="operator_display"></div>
				</s:if>
				<s:else>
					<s:select name="account" list="facilities" listValue="name" />
				</s:else>
			</s:if>
			<s:else>
				<s:property value="account.name" />
			</s:else>
		</li>
		<s:if test="parentId!=0 && (id!=parentId)">
			<li>
				<label><s:text name="ManageResources.Primary" />: </label>
				<s:set name="displayName" value="parentResource.locale.getDisplayName(parentResource.locale)" />
				<s:set name="localeName" value="#displayName.substring(0,1).toUpperCase() + #displayName.substring(1)" />
				<s:property value="parentResource.formName" /> - <a href="Resources!download.action?id=<s:property value="parentResource.id" />&amp;loc=<s:property value="parentResource.locale.toString()" />" target="_blank" title="<s:text name="global.ClickToView"/>" ><s:property value="#localeName" /></a>
			</li>
		</s:if>
		<li>
			<label><s:text name="ManageResources.Name" />: </label>
			<s:if test="id!=0 && parentId==0">
				<s:property value="formName" />
			</s:if>
			<s:else>
				<s:textfield name="formName"/>
				<div class="fieldhelp">
				<h3><s:text name="ManageResources.Name" /></h3>
				<s:text name="ManageResources.name.fieldhelp" />
				</div>
			</s:else>
		</li>
			<s:if test="id!=0 && parentId==0">
			<li>
				<label><s:text name="ManageResources.ExistingFiles" />:</label>
				<table class="report">
					<thead>
						<tr>
							<th><s:text name="ManageResources.Language" /></th>
							<th><s:text name="global.Action" /></th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<s:set name="displayName" value="resource.locale.getDisplayName(resource.locale)" />
							<s:set name="localeName" value="#displayName.substring(0,1).toUpperCase() + #displayName.substring(1)" />
							<td><a href="Resources!download.action?id=<s:property value="resource.id" />&amp;loc=<s:property value="resource.locale.toString()" />" target="_blank" title="<s:text name="global.ClickToView"/>" ><s:property value="#localeName" /></a></td>
							<td>
								<a href="ManageResources.action?id=<s:property value="resource.id" />&amp;parentId=<s:property value="resource.id" />" class="edit"><s:text name="global.Edit" /></a> | 
								<s:text name="ManageResources.Primary" /> 
							</td>
						</tr>
						<s:iterator value="resource.children" var="child">
							<tr>
								<s:set name="displayName" value="#child.locale.getDisplayName(#child.locale)" />
								<s:set name="localeName" value="#displayName.substring(0,1).toUpperCase() + #displayName.substring(1)" />
								<td><a href="Resources!download.action?id=<s:property value="resource.id" />&amp;loc=<s:property value="#child.locale.toString()" />" target="_blank" title="<s:text name="global.ClickToView"/>" ><s:property value="#localeName" /></a></td>
								<td>
									<a href="ManageResources.action?id=<s:property value="#child.id" />&amp;parentId=<s:property value="resource.id" />" class="edit"><s:text name="global.Edit" /></a> | 
									<a href="ManageResources!remove.action?id=<s:property value="resource.id"  />&amp;selectedId=<s:property value="#child.id" />" class="remove" onclick="return confirm(translate('JS.ConfirmDeletion'));"><s:text name="ManageResources.Remove" /></a> | 
									<a href="ManageResources!makeDefault.action?id=<s:property value="resource.id" />&amp;selectedId=<s:property value="#child.id"  />" class="top" ><s:text name="ManageResources.MakePrimary" /></a>
								</td>
							</tr>
						</s:iterator>
					</tbody>
				</table>
				<a href="ManageResources.action?id=0&amp;parentId=<s:property value="resource.id" />" class="add"><s:text name="ManageResources.AddFile" ><s:param><s:property value="formName" /></s:param></s:text></a>
			</li>
			</s:if>
			<s:else>
			<li>
				<label><s:text name="ManageResources.Language" />: </label>
				<s:if test="id!=0">
					<s:set name="displayName" value="resource.locale.getDisplayName(resource.locale)" />
					<s:set name="localeName" value="#displayName.substring(0,1).toUpperCase() + #displayName.substring(1)" />
					<a href="Resources!download.action?id=<s:property value="parentResource.id" />&amp;loc=<s:property value="resource.locale.toString()" />" target="_blank" title="<s:text name="global.ClickToView"/>" ><s:property value="#localeName" /></a>
				</s:if>
				<s:else>
					<s:select name="locale" list="@com.picsauditing.jpa.entities.AppTranslation@getLocales()" listValue="@org.apache.commons.lang3.StringUtils@capitalize(getDisplayName(language))" theme="" />
					<div class="fieldhelp">
					<h3><s:text name="ManageResources.Language" /></h3>
					<s:text name="ManageResources.locale.fieldhelp" />
					</div>
				</s:else>
			</li>
			<li>
				<label><s:text name="global.File" />: </label>
				<s:file name="file" />
			</li>
			</s:else>
		</ol>
	</fieldset>
 	<fieldset class="form submit">
		<s:if test="id!=0 && parentId==0">
			<s:submit value="%{getText('ManageResources.DeleteAll')}" cssClass="picsbutton negative" method="delete" onclick="return confirm(translate('JS.ConfrimDeleteResource'));" />
		</s:if>
		<s:else>
			<s:submit value="%{getText('button.Save')}" cssClass="picsbutton positive" method="save" />
		</s:else>
	</fieldset>
</s:form>
</body>
</html>
