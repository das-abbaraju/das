<%@ taglib prefix="s" uri="/struts-tags"%>
<script type="text/javascript">
$('.datepicker').datepicker();
</script>
<s:form id="editJobSite" method="POST" enctype="multipart/form-data" cssStyle="clear: both;">
	<s:hidden name="id" />
	<s:hidden name="siteID" />
	<div>
	<fieldset class="form">
		<h2 class="formLegend">Edit Project</h2>
		<ol>
			<li><label>Label<span class="redMain">*</span>:</label>
				<input type="text" name="siteLabel" value="<s:property value="newSite.label" />" size="20" maxlength="15" />
			</li>
			<li><label>Name<span class="redMain">*</span>:</label>
				<input type="text" name="siteName" value="<s:property value="newSite.name" />" size="20" />
			</li>
			<li><label>City:</label>
				<input type="text" name="siteCity" value="<s:property value="newSite.city" />" size="20" />
			</li>
			<li><label>Country:</label>
				<s:select list="countryList" name="siteCountry.isoCode" listKey="isoCode" headerKey=""
					headerValue="- Country -" listValue="name" value="newSite.country.isoCode"></s:select>
			</li>
			<li class="loadStates">
				<s:if test="newSite.country.isoCode != ''">
					<label>State:</label>
					<s:select list="getStateList(newSite.country.isoCode)" name="state.isoCode" listKey="isoCode"
						listValue="name" value="newSite.state.isoCode"></s:select>
				</s:if>
			</li>
			<li><label>Start Date:</label>
				<input type="text" name="siteStart" value="<s:property value="maskDateFormat(newSite.projectStart)" />"
					size="10" class="datepicker" />
			</li>
			<li><label>End Date:</label>
				<input type="text" name="siteEnd" value="<s:property value="maskDateFormat(newSite.projectStop)" />" size="10"
				class="datepicker" />
			</li>
		</ol>
	</fieldset>
	<fieldset class="form submit">
		<input type="submit" value="Update" class="picsbutton positive" name="button" />
		<button onclick=" $('#editJobSite').hide(); return false;"
			class="picsbutton">Cancel</button>
		<s:if test="siteID!=0">
			<input type="submit" value="Remove" class="picsbutton negative" name="button" />
		</s:if>
	</fieldset>
	</div>
</s:form>