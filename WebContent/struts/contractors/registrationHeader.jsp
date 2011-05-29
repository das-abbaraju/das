<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<s:include value="../jquery.jsp" />
<script type="text/javascript">
$(function(){
	$('div.navigationButtons').clone().appendTo('div#content');
});
</script>
<h1>
<s:if test="permissions.loggedIn"><s:property value="contractor.name" /></s:if>
<s:else><s:text name="%{scope}.title" /></s:else>
<span class="sub"><s:property value="subHeading" /></span></h1>
<div id="internalnavcontainer">
<ul id="navlist">
<s:iterator value="menu">
	<s:if test="isStringEmpty(url)">
		<li><span class="inactive"><s:property value="name"/></span></li>
	</s:if>
	<s:else>
		<li><a <s:if test="current">class="current"</s:if> href="<s:property value="url"/>"><s:property value="name"/></a></li>
	</s:else>
</s:iterator>
</ul>
</div>

<div class="navigationButtons">
	<s:form>
		<s:if test="previousRegistrationStep != null">
			<s:submit action="%{scope}!previousStep" cssClass="picsbutton previous" value="<< %{getText('button.Previous')}" />
		</s:if>
		<div id="next_button" <s:if test="nextRegistrationStep == null">style="display:none;"</s:if>>
			<s:submit action="%{scope}!nextStep" cssClass="picsbutton positive next" value="%{getText('button.Next')} >>" />
		</div>
	</s:form>
</div>

<s:include value="../actionMessages.jsp" />
