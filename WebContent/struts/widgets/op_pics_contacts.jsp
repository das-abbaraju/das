<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>
<s:iterator value="contacts.keySet()" var="key">
	<p>
		<label>
			<s:text name="UserAccountRole.%{#key + (contacts.get(#key).size() > 1 ? 's' : '')}"/>:
		</label>
	</p>
	<ul style="list-style-type: none">
		<s:iterator value="contacts.get(#key)" var="manager">
			<li>
					${manager.name}
				<br/>
				<s:text name="User.email"/>:
				<a href="mailto:${manager.email}"
				   title="<s:text name="OperatorPicsContactsAjax.EmailManager"><s:param value="%{#managers.name}" /></s:text>">
						${manager.email}
				</a>
				<br/>
				<s:set var="manager_phone_number" value="getLocalizedPhoneNumberForUser(#manager)"/>
				<s:text name="User.phone"/>: ${manager_phone_number}
			</li>
		</s:iterator>
	</ul>
</s:iterator>

<p>
	<label>
		<s:text name="ContactPage.GeneralInquiries"/>:
	</label>
</p>
