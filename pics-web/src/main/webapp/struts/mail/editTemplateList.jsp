<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<tr>
	<td style="width: 40%;">
		<table class="report" style="width: 100%;">
			<thead>
				<tr>
					<td></td>
					<td style="min-width: 200px;">
						<s:text name="global.Name" />
					</td>
					<td class="center">
						<s:text name="EditEmailTemplate.TemplateType" />
					</td>
					<pics:permission perm="EmailTemplates" type="Delete">
						<td>
							<s:text name="button.Remove" />
						</td>
					</pics:permission>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>1</td>
					<td>
						<a href="#template=blank">
							<s:text name="EditEmailTemplate.StartWithBlank" />
						</a>
					</td>
					<td>
						<s:text name="EditEmailTemplate.None" />
					</td>
					<td></td>
				</tr>
				<s:iterator value="emailTemplates" status="num" id="template">
					<tr>	
						<td>
							<s:property value="#num.count+1" />
						</td>
						<td>
							<a href="#template=<s:property value="id"/>">
								<s:property value="#template.templateName" />
							</a>
						</td>
						<td>
							<s:text name="%{#template.listType.i18nKey}" />
						</td>
						<pics:permission perm="EmailTemplates" type="Delete">
							<td class="center">
								<a href="javascript:;" class="remove" data-id="<s:property value="#template.id" />"></a>
							</td>
						</pics:permission>
					</tr>
				</s:iterator>
			</tbody>
		</table>
	</td>
	<td style="width: 60%; padding-left: 10px;">
		<div id="menu_selector" style="display: none;">
			<pics:permission perm="EmailTemplates" type="Edit">
				<button
					id="buttonSave"
					class="picsbutton"
					type="button"
					onclick="saveClick();"
					title="<s:text name="EditEmailTemplate.SaveAsTemplate" />">
					<s:text name="button.Save" />...
				</button>
			</pics:permission>
			<br clear="all">
		</div>
		<div id="draftEdit">
			<div id="draftEmail"></div>
			<div id="previewEmail" style="display: none;"></div>
		</div>
	</td>
</tr>