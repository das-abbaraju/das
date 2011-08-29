<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:if test="report.allRows == 0">
	<div class="alert"><s:text name="Report.message.NoRowsFound" /></div>
</s:if>
<s:else>

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
	<tr>
		<td></td>
		<td><a href="javascript: changeOrderBy('form1','a.name DESC');"><s:text name="global.ContractorName" /></a></td>
		<s:if test="permissions.operator">
			<td><a href="javascript: changeOrderBy('form1','dateAdded');"><s:text name="ContractorApproval.DateAdded" /></a></td>
			<td><s:text name="ContractorApproval.WorkStatus" /></td>
		</s:if>
	</tr>
	</thead>
	<tbody>
	<s:iterator value="data" status="stat">
		<tr>
			<td style="text-align: center;">
				<input id="conid_co<s:property value="get('id')"/>" type="checkbox" class="massCheckable" name="conids" value="<s:property value="get('id')"/>"/>
			</td>
			<td>
				<a href="ContractorView.action?id=<s:property value="get('id')"/>" rel="ContractorQuickAjax.action?id=<s:property value="get('id')"/>" 
					class="contractorQuick" title="<s:property value="get('name')" />">
					<s:property value="get('name')" />
				</a>
			</td>
			<s:if test="permissions.operator">
				<td><s:date name="get('dateAdded')" format="M/d/yy"/></td>
				<td><s:property value="getWorkStatusDesc(get('workStatus'))"/></td>
			</s:if>
		</tr>
	</s:iterator>
		<tr>
			<td class="center">
				<center><input title="Check all" type="checkbox" id="selectAll" /><br/><s:text name="ContractorApproval.SelectAll" /></center>
			</td>
			<td>
				<div style="height:28px;">
					<s:text name="ContractorApproval.ChangeStatusTo" />
					<s:radio cssClass="workStatus" list="#{'Y':getText('YesNo.Yes'),'N':getText('YesNo.No'),'P':getText('AccountStatus.Pending')}" name="workStatus"/>
				</div>
				<s:text name="global.Notes" />: <s:textarea name="operatorNotes" cols="20" rows="4"/>
				&nbsp;&nbsp;&nbsp;&nbsp;<br clear="all"/><br/>
				<div class="buttons">
					<a class="picsbutton positive" href="#" id="saveChanges"><s:text name="ContractorApproval.SaveChanges" /></a>
				</div>
				<br/>
			</td>
			<s:if test="permissions.operator">
				<td></td>
				<td></td>
			</s:if>			
		</tr>
	</tbody>
</table>

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</s:else>