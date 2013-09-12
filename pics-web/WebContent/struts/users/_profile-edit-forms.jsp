<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<%@ page import="com.picsauditing.toggle.FeatureToggle" %>

<s:set var="user_id">
	<s:property value="u.id"/>
</s:set>

<s:form id="saveProfileForm" cssClass="form">
	<s:hidden name="url"/>
	<s:hidden name="u" value="%{user_id}"/>

	<fieldset>
		<h2>
			<s:text name="ProfileEdit.Profile.heading"/>
		</h2>

		<ul>
			<li>
				<label><s:text name="ProfileEdit.AssignedToAccount"></s:text>:</label>
				<s:property value="u.account.name"/>
			</li>

			<li class="country">
				<label><s:text name="global.Country"></s:text>:</label>
				<s:property value="u.account.country.name"/>
			</li>

			<li>
				<s:textfield name="u.firstName" theme="form"/>
			</li>

			<li>
				<s:textfield name="u.lastName" theme="form"/>
			</li>

			<li>
				<s:textfield id="departmentSuggest" name="u.department" size="15" theme="formhelp"/>
			</li>

			<li>
				<s:textfield name="u.email" theme="form"/>
			</li>

			<li>
				<s:textfield name="u.username" size="30" onchange="checkUsername(this.value);" theme="form"/>
				<span id="username_status"></span>
			</li>

			<li>
				<s:textfield name="u.phone" theme="form"/>
			</li>

			<li>
				<s:textfield name="u.fax" theme="form"/>
			</li>

            <li>
                <label for="profile_language"><s:text name="User.locale"/>:</label>
                <s:select list="supportedLanguages.visibleLanguagesSansDialect"
                          listKey="key"
                          listValue="value"
                          name="language"
                          value="language"
                          id="profile_language"
                          cssClass="select2"
                        />
            </li>
            <li id="profile_dialect">
                <s:include value="_profile-edit-dialects.jsp"/>
            </li>

            <li class="timezone">
                <label for="timezone_input"><s:text name="global.timezone" /></label>
                <input id="timezone_input" class="timezone_input" name="u.timezone" data-country="${u.account.country.isoCode}" data-placeholder="<s:text name='Timezone.list.select.header' />" value="${u.timezone.ID}" />
            </li>

			<li>
				<label><s:text name="global.CreationDate"/>:</label>
				<s:date name="u.creationDate" format="%{@com.picsauditing.util.PicsDateFormat@Iso}"/>
			</li>

			<pics:toggle name="<%= FeatureToggle.TOGGLE_V7MENUS %>">
				<li>
					<label> <s:text name="User.useDynamicReport"/></label>
					<s:checkbox id="usingVersion7Menus" name="usingVersion7Menus" value="u.usingVersion7Menus"/>
				</li>
			</pics:toggle>
		</ul>
	</fieldset>

	<fieldset class="form submit">
		<s:submit value="%{getText('button.Save')}" cssClass="picsbutton positive" method="save"/>

		<a class="change-password btn" href="ChangePassword.action?source=profile&user=${user_id}"
		   id="profile_edit_changePassword2">
			<s:text name="button.password"/>
		</a>
	</fieldset>

</s:form>

<!-- See if this user has the RestApi permission, which means it is a special (non-human) API user. If so, allow it to (re)generate the API key. -->
<s:if test="permissions.hasPermission(@com.picsauditing.access.OpPerms@RestApi)">
	<s:include value="user_api_key.jsp"/>
</s:if>
