<%@ taglib prefix="s" uri="/struts-tags"%>

<div id="thinking_emailList"></div>
<table class="notes">
	<thead>
		<tr>
			<th>From</th>
			<th>Sent</th>
			<th>Subject</th>
			<th>To</th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="emailList">
		<tr>
			<td>
				<s:if test="fromAddress == null || fromAddress == ''">info@picsauditing.com</s:if>
				<s:else><s:property value="fromAddress" /></s:else>
			</td>
			<td align="right"><s:date name="sentDate" format="M/d/yy h:mm a" /></td>
			<td><s:property value="subject" /></td>
			<td><s:property value="toAddresses" /></td>
		</tr>
		</s:iterator>
	</tbody>
</table>
