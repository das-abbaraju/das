<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<s:if test="currentStep.showBottom">
	<div class="navigationButtonsBottom">
		<s:form>
			<span>
				<s:if test="previousRegistrationStep != null">
					<s:submit method="previousStep" cssClass="picsbutton previous" value="<< %{getText('button.Previous')}" />
				</s:if>
				<span id="next_button" <s:if test="nextRegistrationStep == null">style="display:none;"</s:if>>
					<s:submit method="nextStep" cssClass="picsbutton positive next right" value="%{getText('button.Next')} >>" />
				</span>
			</span>
		</s:form>
	</div>
</s:if>
