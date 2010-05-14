<%@ taglib prefix="s" uri="/struts-tags"%>
<script type="text/javascript">
$('.datepicker').datepicker();
</script>
<s:form id="editJobSite" method="POST" enctype="multipart/form-data" cssStyle="clear: both;">
	<s:hidden name="id" />
	<s:hidden name="siteID" />
	<fieldset class="form bottom">
		<legend><span>Edit Project</span></legend>
		<ol>
			<li><label>Label:</label>
				<input type="text" name="siteLabel" value="<s:property value="newSite.label" />" size="20" />
			</li>
			<li><label>Name:</label>
				<input type="text" name="siteName" value="<s:property value="newSite.name" />" size="20" />
			</li>
			<li><label>City:</label>
				<input type="text" name="siteCity" value="<s:property value="newSite.city" />" size="20" />
			</li>
			<li><label>Country:</label>
				<s:select list="countryList" name="siteCountry.isoCode" listKey="isoCode"
					listValue="name" value="newSite.country.isoCode"></s:select>
			</li>
			<li><label>State:</label>
				<s:select list="getStateList(newSite.country.isoCode)" name="state.isoCode" listKey="isoCode"
					listValue="name" value="newSite.state.isoCode"></s:select>
			</li>
			<li><label>Start Date:</label>
				<input type="text" name="siteStart" value="<s:property value="newSite.projectStart" />" size="10"
					class="datepicker" />
			</li>
			<li><label>End Date:</label>
				<input type="text" name="siteEnd" value="<s:property value="newSite.projectStop" />" size="10"
				class="datepicker" />
			</li>
		</ol>
		<div style="text-align: center; margin: 0px auto;">
			<input type="submit" value="Update" class="picsbutton positive" name="button" />
			<button onclick=" $('#editJobSite').hide(); return false;"
				class="picsbutton negative">Cancel</button>
		</div>
	</fieldset>
</s:form>