<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report">
	<thead>
		<tr>
			<td>Contractor</td>
			<td>Activity</td>
			<td>Date</td>
		</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td><s:property value="get('name')" /></td>
			<td>
				<s:if test="get('url').length() > 0">
					<a href="<s:property value="get('url')" />">
						<s:text name="%{get('activityType')}" >
							<s:param value="%{get('v1')}" />
							<s:param value="%{get('v2')}" />
							<s:param value="%{get('v3')}" />
							<s:param value="%{@java.lang.Integer@parseInt(get('v4'))}" />
						</s:text>
					</a>
				</s:if>
				<s:else>
					<s:text name="%{get('activityType')}" >
						<s:param value="%{get('v1')}" />
						<s:param value="%{get('v2')}" />
						<s:param value="%{get('v3')}" />
						<s:param value="%{@java.lang.Integer@parseInt(get('v4'))}" />
					</s:text>
				</s:else>
			</td>
			<td><span title="<s:date name="get('activityDate')" nice="true" />"><s:date name="get('activityDate')" /></span></td>
		</tr>
	</s:iterator>
</table>
