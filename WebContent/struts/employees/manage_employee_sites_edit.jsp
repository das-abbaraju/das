<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<form id="edit_site_form">
	<s:hidden name="employeeSite" />
	<fieldset class="form">
		<ol>
			<li>
				<s:textfield
					cssClass="datepicker"
					name="employeeSite.effectiveDate"
					value="%{employeeSite.effectiveDate}"
					size="10"
					theme="formhelp" />
			</li>
			<li>
				<s:textfield
					cssClass="datepicker"
					name="employeeSite.expirationDate"
					value="%{employeeSite.expirationDate}"
					size="10"
					theme="formhelp" />
			</li>
			<s:if test="employeeSite.jobSite == null">
				<li>
					<s:textfield
						cssClass="datepicker"
						name="employeeSite.orientationDate"
						value="%{employeeSite.orientationDate}"
						size="10"
						theme="formhelp" />
				</li>
			</s:if>
		</ol>
	</fieldset>
</form>