<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<table id="email_preview_modal">
	<tr>
		<th>
			<s:text name="EmailQueue.subject" />
		</th>
		<td>
			${email.subject}
		</td>
		<th>
			<s:text name="EmailQueue.toAddresses" />
		</th>
		<td>
			${email.toAddresses}
		</td>
	</tr>
	<tr>
		<td colspan="4" id="email_body">
			${email.body}
		</td>
	</tr>
</table>