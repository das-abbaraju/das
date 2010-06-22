<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title><s:property value="reportName" /></title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1><s:property value="reportName" /></h1>
<div>
<table>
	<tr><td>
		<s:include value="filters.jsp" />
	</td></tr>
	<tr><td>
		<table class="report">
			<thead>
			<tr>
			    <th><a href="javascript: changeOrderBy('form1','category');" >Category</a></th>
			    <th><a href="javascript: changeOrderBy('form1','priority');" >Priority</a></th>
				<th><a href="javascript: changeOrderBy('form1','status');">Status</a></th>
				<th><a href="javascript: changeOrderBy('form1','createdBy');">Created By</a></th>
				<th><a href="javascript: changeOrderBy('form1','updatedBy');">Updated By</a></th>
				<th><a href="javascript: changeOrderBy('form1','creationDate DESC');">Creation Date</a></th>
				<th><a href="javascript: changeOrderBy('form1','updateDate DESC');">Updated Date</a></th>
				<th><a href="javascript: changeOrderBy('form1','message');">Message</a></th>
			</tr>
			</thead>
			<tbody>
			<s:iterator value="data" status="stat">
				<tr>
					<td><s:property value="get('category')" /></td>
					<td class="center"><s:property value="get('priority')" /></td>
					<td class="center"><s:property value="get('status')" /></td>
					<td class="right"><s:property value="get('createdBy')" /></td>
					<td class="right"><s:property value="get('updatedBy')" /></td>
					<td class="right"><s:date name="get('creationDate')" format="M/d/yy"/></td>
					<td class="right"><s:if test="get('updateDate') != null" ><s:date name="get('updateDate')" format="M/d/yy"/></s:if><s:else>&nbsp;</s:else></td>
					<td class="center">
						<s:if test="get('message') != null">
							<input id="showMessageButton_<s:property value="get('id')" />" class="picsbutton positive" type="button" value="Show Message" onclick="$('#message_<s:property value="get('id')" />').fadeIn(1000); $('#hideMessageButton_<s:property value="get('id')" />').show(); $('#showMessageButton_<s:property value="get('id')" />').hide();"/>
							<input id="hideMessageButton_<s:property value="get('id')" />" style="display:none;" class="picsbutton negative" type="button" value="Hide Message" onclick="$('#message_<s:property value="get('id')" />').hide(); $('#hideMessageButton_<s:property value="get('id')" />').hide(); $('#showMessageButton_<s:property value="get('id')" />').show();"/>
						</s:if>
						<s:else>&nbsp;</s:else>
					</td>
				</tr>
				<tr style="display:none;" id="message_<s:property value="get('id')" />">
					<td colspan="8">
						<s:property value="get('message')" />
					</td>
				</tr>
			</s:iterator>
			</tbody>
		</table>
	</td></tr>
</table>
</div>
</body>
</html>
