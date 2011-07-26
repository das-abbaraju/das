<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<form id="siteForm_<s:property value="esSite.id" />">
	<fieldset class="form">
		<input type="hidden" value="<s:property value="esSite.id" />" name="childID" />
		<h2 class="formLegend noJump">
			<s:text name="ManageEmployees.header.EditSiteProject">
				<s:param value="%{esSite.operator.name}" />
				<s:param value="%{esSite.jobSite != null ? 1 : 0}" />
				<s:param value="%{esSite.jobSite.label}" />
			</s:text>
		</h2>
		<ol>
			<li>
				<s:textfield cssClass="datepicker" name="esSite.effectiveDate" value="%{esSite.effectiveDate}" 
					size="10" theme="formhelp" />
			</li>
			<li>
				<s:textfield cssClass="datepicker" name="esSite.expirationDate" value="%{esSite.expirationDate}"
					size="10" theme="formhelp" />
			</li>
			<s:if test="esSite.jobSite == null">
				<li>
					<s:textfield cssClass="datepicker" name="esSite.orientationDate" value="%{esSite.orientationDate}"
						size="10" theme="formhelp" />
				</li>
				<li>
					<s:select list="#{0:36, 1:24, 2:12, 3:6, 4:' '}" name="esSite.monthsToExp" value="esSite.monthsToExp"
						theme="formhelp" />
					<s:text name="ManageEmployees.label.Months" />
				</li>
			</s:if>
			<div style="clear: both; font-size: smaller; text-align: center">
				<input type="submit" value="Save Site" id="saveSite" onclick="editAssignedSites(<s:property value="esSite.id" />); return false;" class="picsbutton positive"/>
				<input type="submit" value="Remove Site" id="removeSite" onclick="return removeJobSite(<s:property value="esSite.id" />);" class="picsbutton negative" />
				<input type="button" value="Close" id="closeEdit" onclick="$.unblockUI(); return false;" class="picsbutton" />
			</div>
		</ol>
	</fieldset>
</form>