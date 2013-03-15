<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<s:iterator value="contacts.keySet()" id="key">
	<p>
		<label>
			<s:text name="UserAccountRole.%{#key + (contacts.get(#key).size() > 1 ? 's' : '')}" />:
		</label>
	</p>
	<ul style="list-style-type: none">
		<s:iterator value="contacts.get(#key)" id="managers">
			<li>
				<s:property value="#managers.name" />
				<br />
				<s:text name="User.email" />:
				<a href="mailto:<s:property value="#managers.email" />"
					title="<s:text name="OperatorPicsContactsAjax.EmailManager"><s:param value="%{#managers.name}" /></s:text>">
					<s:property value="#managers.email" />
				</a>
				<br />
				<s:text name="User.phone" />:
				<s:property value="#managers.phone" />
			</li>
		</s:iterator>
	</ul>
</s:iterator>

<p>
    <label>
        <s:text name="ContactPage.GeneralInquiries"></s:text>:
    </label>
    ${picsPhoneNumber}
</p>
