<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>

<s:if test="conOpList.size>0">
	<table class="report">
		<thead>
			<tr>
				<th>Facility</th>
				<th>Baseline</th>
				<th>Flag</th>
				<th>Approve</th>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="conOpList" var="co">
				<tr>
					<td><s:property value="#co.operatorAccount.name"/></td>
					<td><s:property value="#co.baselineFlag.smallIcon" escape="false"/></td>
					<td><s:property value="#co.flagColor.smallIcon" escape="false"/></td>
					<td><input type="checkbox" name="conOpSave" class="coFlag" value="<s:property value="#co.id"/>" /></td>
				</tr>
			</s:iterator>
			<tr>
				<td colspan="4"><a style="float: right;" class="saveFlag save showPointer">Approve Checked Flags</a></td>
			</tr>
		</tbody>
	</table>
	<button class="cancelFlags picsbutton negative">Cancel</button>
</s:if>
<s:else>
	<div class="info">No Flag Changes to Approve</div>
</s:else>