<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<h3>
	<s:text name="ProfileEdit.RecentLogins" />
</h3>

<table class="report" style="position: static;">

	<thead>
		<tr>
			<th><s:text name="Login.LoginDate" /></th>
			<th><s:text name="Login.IPAddress" /></th>
			<s:if test="permissions.isDeveloperEnvironment()">
				<th><s:text name="Login.Server" /></th>
			</s:if>
			<th><s:text name="global.Notes" /></th>
            <th><s:text name="Login.Method" /></th>
		</tr>
	</thead>

	<tbody>
		<s:iterator value="recentLogins">
			<tr>
				<td>
                    <s:date name="loginDate" format="%{@com.picsauditing.util.PicsDateFormat@Iso}" />
                </td>

				<td>
                    <s:property value="remoteAddress" />
                </td>

				<s:if test="permissions.isDeveloperEnvironment()">
					<td>
						<s:property value="serverAddress" />
					</td>
				</s:if>

				<td>
                    <s:if test="admin.id > 0">
						<s:text name="Login.LoginBy">
							<s:param value="admin.name" />
							<s:param value="admin.account.name" />
						</s:text>
					</s:if>

                    <s:if test="successful == 'N'">
						<s:text name="ProfileEdit.message.IncorrectPasswordAttempt" />
					</s:if>
                </td>

                <td>
                    <s:if test="loginMethod != null">
                        <s:text name="loginMethod.i18nKey"/>
                    </s:if>
                </td>
            </tr>
		</s:iterator>
	</tbody>

</table>
