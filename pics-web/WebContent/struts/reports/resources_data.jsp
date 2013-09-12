<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:if test="report.allRows == 0">
	<div class="alert"><s:text name="Report.message.NoRowsFound" /></div>
</s:if>
<s:else>

<s:set name="canManageForm" value="permissions.isAdmin() || permissions.hasPermission(@com.picsauditing.access.OpPerms@FormsAndDocs, 'Edit')" />

<div><s:property value="report.pageLinksWithDynamicForm" escape="false" /></div>
<table class="report">
	<thead>
	<tr>
		<td></td>
	    <th><s:text name="global.Resource" /></th>
	    <td><s:text name="global.Languages" /></td>
	    <td><s:text name="global.Operator" /></td>
        <s:if test="!permissions.contractor" >
            <th><s:text name="ReportResources.Visibility" /></th>
        </s:if>
	    <s:if test="canManageForm">
	    	<td><s:text name="global.Edit" /></td>
	    </s:if>
	</tr>
	</thead>
	<s:iterator value="resources" status="stat" var="resource">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td><a href="Resources!download.action?id=<s:property value="#resource.id" />"  target="_blank" title="<s:text name="global.ClickToView"/>"><s:property value="formName"/></a></td>
			<td>
				<s:iterator value="locales" status="track" var="locale">
					<s:if test="#track.index != 0" > | </s:if>
					<s:set name="displayName" value="#locale.getDisplayName(#locale)" />
					<s:set name="localeName" value="#displayName.substring(0,1).toUpperCase() + #displayName.substring(1)" />
					<a href="Resources!download.action?id=<s:property value="#resource.id" />&amp;loc=<s:property value="#locale.toString()" />"  target="_blank" title="<s:text name="global.ClickToView"/>"><s:property value="#localeName" /></a>
				</s:iterator>
			</td>
            <td><s:property value="operatorName"/></td>
            <s:if test="!permissions.contractor">
                <td>
                    <s:if test="#resource.clientSiteOnly">
                        <s:text name="global.Operators"/>
                    </s:if>
                    <s:else>
                        <s:text name="global.Contractors"/>
                    </s:else>
                </td>
            </s:if>
			<s:if test="canManageForm">
				<td><s:if test="isEditableByUser(#resource)" ><a href="ManageResources.action?id=<s:property value="#resource.id" />" class="edit"><s:text name="button.Edit" /></a></s:if></td>
			</s:if>
		</tr>
	</s:iterator>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</s:else>
