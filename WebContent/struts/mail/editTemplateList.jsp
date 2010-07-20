<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<tr>
	<td style="width: 40%;">
		<table class="report" style="width: 100%;">
			<thead>
				<tr>
					<td></td>
					<td style="min-width: 200px;">Name</td>
					<td class="center">Template Type</td>
					<td>Remove</td>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>1</td>
					<td><a href="javascript: changeTemplate(-1, $('#changeType').val());">~ Start with a Blank Email ~</a></td>
					<td>None</td>
					<td></td>
				</tr>
				<s:iterator value="emailTemplates" status="num" id="template">
					<tr>	
						<td><s:property value="#num.count+1" /></td>
						<td><a href="javascript: changeTemplate(<s:property value="id"/>, '<s:property value="#template.listType"/>');"><s:property	value="#template.templateName" /></a></td>
						<td><s:property value="#template.listType" /></td>
						<td><img src="images/cross.png" /></td>
					</tr>
				</s:iterator>
			</tbody>
		</table>
	</td>
	<td style="width: 60%; padding-left: 10px;">
		<div id="menu_selector" style="display: none;">
			<pics:permission perm="EmailTemplates" type="Edit">
				<button id="buttonSave" class="picsbutton" type="button" onclick="saveClick();" title="Save this email as a template for future use">Save...</button>
			</pics:permission>
			<br clear="all">
		</div>
		<div id="draftEdit">
			<div id="draftEmail"></div>
			<div id="previewEmail" style="display: none;"></div>
		</div>
	</td>
</tr>