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
				<td align="right">
					<s:if test="sentDate == null">
						<s:text name="ContractorNotes.Pending" />
					</s:if>
					<s:else>
						<s:date name="sentDate" format="M/d/yy h:mm a" />
					</s:else>
				</td>
				<td>
					<s:property value="subject" />
				</td>
				<td>
					<s:property value="toAddresses" />
				</td>
			</tr>
		</s:iterator>
	</tbody>
</table>