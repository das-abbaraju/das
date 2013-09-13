<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<s:if test="report.allRows == 0">
	<div class="alert">
		<s:text name="Report.message.NoRowsFound" />
	</div>
</s:if>
<s:else>
	<div class="right">
		<a 
			class="excel" <s:if test="report.allRows > 500">onclick="return confirm('<s:text name="JS.ConfirmDownloadAllRows"><s:param value="%{report.allRows}" /></s:text>');"</s:if> 
			href="javascript: download('ContractorApproval');" 
			title="<s:text name="javascript.DownloadAllRows"><s:param value="%{report.allRows}" /></s:text>"
		><s:text name="global.Download" /></a>
	</div>

	<div>
		<s:property value="report.pageLinksWithDynamicForm" escape="false" />
	</div>
	
	<table class="report">
		<thead>
			<tr>
				<td></td>
				<td>
					<a href="javascript: changeOrderBy('form1','a.name');"><s:text name="global.ContractorName" /></a>
				</td>
				
				<s:if test="permissions.operator">
					<td>
						<a href="javascript: changeOrderBy('form1','dateAdded');"><s:text name="ContractorApproval.DateAdded" /></a>
					</td>
					<td>
						<s:text name="ContractorApproval.WorkStatus" />
					</td>
				</s:if>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="data" status="stat">
				<tr>
					<td class="center">
                        <s:if test="isOperatorCanChangeWorkStatus(get('workStatus'))">
                            <input id="conid_co<s:property value="get('id')"/>" type="checkbox" class="massCheckable" name="conids" value="<s:property value="get('id')"/>"/>
                        </s:if>
					</td>
					<td>
						<a href="ContractorView.action?id=<s:property value="get('id')"/>" rel="ContractorQuick.action?id=<s:property value="get('id')"/>" class="contractorQuick" title="<s:property value="get('name')" />">
							<s:property value="get('name')" />
						</a>
					</td>
					
					<s:if test="permissions.operator">
						<td>
							<s:date name="get('dateAdded')" format="%{@com.picsauditing.util.PicsDateFormat@Iso}"/>
						</td>
						<td class="center">
							<s:property value="get('workStatus')"/>
						</td>
					</s:if>
				</tr>
			</s:iterator>
			
			<tr>
				<td class="center">
					<input title="ContractorApproval.CheckAll" type="checkbox" id="selectAll" />
					<br />
					<s:text name="ContractorApproval.SelectAll" />
				</td>
				<td>
                    <s:text name="ContractorApproval.ChangeStatusTo" />

					<s:radio
						list="#{'Y':getTextNullSafe('YesNo.Yes'),'N':getTextNullSafe('YesNo.No'),'P':getTextNullSafe('AccountStatus.Pending')}" 
						name="workStatus"
						theme="pics"
						cssClass="workStatus inline"
					/>

                    <label for="operatorNotes"><s:text name="global.Notes" />:
                        <s:textarea id="operatorNotes" name="operatorNotes" cols="20" rows="4"/>
                    </label>

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
