<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<div id="thinking_emailList"></div>

<table class="notes">
	<thead>
		<tr>
			<th>
				<s:text name="EmailQueueList.header.From" />
			</th>
			<th>
				<s:text name="global.Sent" />
			</th>
			<th>
				<s:text name="EmailQueueList.header.Subject" />
			</th>
			<th>
				<s:text name="EmailQueueList.header.To" />
			</th>
			<th>
                <s:text name="EmailQueueList.header.Preview" />
            </th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="emailList">
			<tr>
				<td>
					<s:if test="fromAddress == null || fromAddress == ''">
						info@picsauditing.com
					</s:if>
					<s:else>
						<s:property value="fromAddress" />
					</s:else>
				</td>
				<td class="right <s:property value='status'/>">
					<s:if test="sentDate == null">
						<s:text name="ContractorNotes.Pending" />
					</s:if>
					<s:else>
						<s:date name="sentDate" format="%{@com.picsauditing.util.PicsDateFormat@Datetime}" />
					</s:else>
				</td>
				<td>
					<s:property value="subject" />
				</td>
				<td>
					<s:property value="toAddresses" />
				</td>
                <td class="center">
                    <s:if test="canPreviewEmail(id)" >
                        <a href="EmailQueueList!previewAjax.action?id=<s:property value="id"/>"
                           class="fancybox iframe preview" title="<s:property value="subject"/>"></a>
                    </s:if>
                </td>
			</tr>
		</s:iterator>
	</tbody>
</table>