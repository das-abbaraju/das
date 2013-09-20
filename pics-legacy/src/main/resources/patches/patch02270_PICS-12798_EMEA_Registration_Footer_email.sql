-- Change the hard-coded company, address, phone, and email in the address block to use the country-aware velocity tags instead.
update token
set velocityCode = "<div style=\"width: 580px; margin-left: 75px; margin-top: 50px; color: #555555; font-size: 11px; background-color: #EEE; text-align: left; padding: 10px; line-height: 14px;\">
<div style=\"float: left;margin: 10px;\"><img src=\"http://www.picsauditing.com/app/images/logo.gif\" align=\"left\" width=\"146\" height=\"146\">
</div>
You are receiving this email because you are a registered PICS user and
have subscribed to this mailing list.<br />
This email was sent to ${user.email}<br>
If you no longer wish to receive this email, <a
href=\"${confirmLink}\">Click here to Unsubscribe</a> or <a
href=\"http://www.picsorganizer.com/ProfileEdit.action\">Click here to Change Your Preferences</a>.
<br>
<br>
$contractor.country.businessUnit.displayName <br />
$contractor.country.businessUnit.addressSingleLine <br />
Tel: $contractor.country.csrPhone <br />
Website: http://www.picsauditing.com <br />
Email: $contractor.country.csrEmail <br />
<b>Please add this email address to your address book to prevent it
from being labeled as spam.</b> <br clear=\"left\" />
</div>

</div>

<div style=\"width: 750px; background-color: #002240; padding: 15px; margin: 0; color: #6699CC; font-weight: normal; font-family: sans-serif; font-size: 11px; border-top: 3px solid #4686bf; border-left: 1px solid #002240; border-right: 1px solid #002240;\">
Copyright &copy; 2011 PICS
<a href=\"http://www.picsauditing.com/\" style=\"font-size: 11px; color: #6699CC; padding: 2px;\">http://www.picsauditing.com</a> |
<a href=\"http://www.picsauditing.com/app/PrivacyPolicy.action\" style=\"font-size: 11px; color: #6699CC; padding: 2px;\">Privacy Policy</a>
</div>
</body>
</html>
"
where tokenName = "SubscriptionFooter"