<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<script type="text/javascript">
	$(function() {
		$('.datepicker').datepicker({
				changeMonth: true,
				changeYear:true,
				yearRange: '1940:2039',
				showOn: 'button',
				buttonImage: 'images/icon_calendar.gif',
				buttonImageOnly: true,
				buttonText: 'Choose a date...',
				constrainInput: true,
				showAnim: 'fadeIn'
			});
	});
</script>

<form id="siteForm_<s:property value="esSite.id" />">
	<fieldset class="form">
		<input type="hidden" value="<s:property value="esSite.id" />" name="childID" />
		<h2 class="formLegend noJump">Edit <s:property value="esSite.operator.name" /><s:if test="esSite.employee.account.requiresOQ">: <s:property value="esSite.jobSite.label" /></s:if></h2>
		<ol>
			<li><label>Start Date:</label>
				<s:textfield cssClass="datepicker" name="esSite.effectiveDate" value="%{maskDateFormat(esSite.effectiveDate)}" size="10" />
			</li>
			<li><label>End Date:</label>
				<s:textfield cssClass="datepicker" name="esSite.expirationDate" value="%{maskDateFormat(esSite.expirationDate)}" size="10" />
			</li>
			<s:if test="esSite.jobSite == null">
				<li><label>Site Orientation:</label>
					<s:textfield cssClass="datepicker" name="esSite.orientationDate" value="%{maskDateFormat(esSite.orientationDate)}" size="10" />
				</li>
				<li><label>Expires In:</label>
					<s:select label="Expires" list="#{0:36, 1:24, 2:12, 3:6, 4:' '}" name="esSite.monthsToExp" value="esSite.monthsToExp" /> months
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