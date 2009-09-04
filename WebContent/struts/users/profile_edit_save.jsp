<%@ taglib prefix="s" uri="/struts-tags"%>

<s:form id="saveProfileForm">
	<s:hidden name="u.id" />
	<fieldset class="form"><legend><span>Profile</span></legend>
	<ol>
		<li><label>Assigned to account:</label> <s:property
			value="u.account.name" /></li>
		<li><label for="u.name">Display name:</label> <s:textfield
			name="u.name" /></li>
		<li><label for="u.email">Email address:</label> <s:textfield
			name="u.email" size="30" /></li>
		<li><label for="u.phone">Phone:</label> <s:textfield
			name="u.phone" size="20" /></li>
		<li><label for="u.fax">Fax:</label> <s:textfield name="u.fax"
			size="20" /></li>
		<li><label for="u.timezone">Timezone:</label>
				<s:select name="u.timezone" 
				list="@com.picsauditing.util.TimeZoneUtil@getTimeZoneSelector()"></s:select>
				<br />* Automatically adjusts for Daylight Savings
		</li>
		<li><label>Created:</label> <s:date name="u.creationDate" /></li>
	</ol>
	</fieldset>
	<fieldset class="form"><legend><span>Username
	&amp; Password</span></legend>
	<ol>
		<li><label for="u.username">Username:</label> <s:textfield
			name="u.username" onchange="checkUsername(this.value);" />
		<div id="username_status">&nbsp;</div>
		</li>
		<li><label for="password1">Password:</label> <s:password
			name="password1" value="" /></li>
		<li><label for="password2">Confirm Password:</label> <s:password
			name="password2" value="" /></li>
	</ol>
	</fieldset>
	<fieldset class="form submit">
	<div>
	<button id="saveButton" class="picsbutton positive"
		value="Save Profile" name="button" type="submit">Save Profile</button>
	</div>
	</fieldset>
</s:form>
