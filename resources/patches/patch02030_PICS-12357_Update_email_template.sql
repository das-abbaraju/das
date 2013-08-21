UPDATE email_template
SET body = '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<body style="width: 100%; margin: 0; padding: 0;">
<TABLE style="border-collapse: collapse; line-height: 16px; font-family: ''Helvetica Neue'', Helvetica, Arial, sans-serif;">
<tr>
<td style="padding: 18px 0; line-height: normal; vertical-align: bottom; margin: 0; padding: 0;">
<h1 style="color: #73bbe8; font-size: 30px; font-weight: normal; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; margin: 0; padding: 0;"><ReportName></h1>
<h2 style="color: #808285; font-size: 14px; font-weight: normal; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; margin: 0; padding-bottom: 18px;"><ReportDescription></h2>
</td>
<td style="text-align: right; vertical-align: bottom; padding: 0 0 18px 20px;">
<a href="<ReportLink>" style="padding: 6px 0; text-decoration: none;">
<input TYPE="button" VALUE="View Full Report" style="margin: 0; font-size: 12px; padding: 6px 15px; color: #454545; border: 1px solid #bcbcbc; background: #dfdfdf;" />
</a>
</td>
</tr>
<tr>
<td colspan="2">
<ReportData></td>
</tr>
<tr>
<td colspan="2">
<TABLE style="border-collapse: collapse; line-height: 16px;">
#if($notFullReport)
<tr>
<td style="padding: 18px 10px 0 10px; 10px;">
<img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABQAAAAUCAYAAACNiR0NAAAACXBIWXMAAA7DAAAOwwHHb6hkAAAA
s0lEQVQ4T2MYBSMAME6bNk2QmZnZBcpn+Pv379msrKx7UC7DzJkzQ6FMEHifnp6+B8rGKsc4Y8aM
M4yMjMZQQTBgYmIySU1NPQuU2w2Ug1sGAv///0/PyMiYBTSsA8gth4hCAFCuggndMBD49++fEogG
yoFpZIAkhuw6MADKpTFB2VQDVDUQ6OV7IANnQbhwcA8YMbCAXw2lYeA9MHzBYkDNnWARBHgPxOjq
R8EoGADAwAAAzJs6FbIF1Y4AAAAASUVORK5CYII=" style="height: 20px; width: 20px; border: 0;" />
</td>
<td style="color: #454545; padding: 18px 0 0 0; font-size: 12px;">
This is <strong style="font-weight: bold;">not</strong> a full report.<br />
<a href="<ReportLink>" style="text-decoration: none; color: #226396;">Log in and view the full report in PICS Organizer.</a>
</td>
</tr>
#end
<tr>
<td style="padding: 18px 10px 0 10px;">
<img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABQAAAARCAYAAADdRIy+AAAACXBIWXMAAA7DAAAOwwHHb6hkAAAB
hElEQVQ4T7WUu0oDYRCFs8vaRREL8SEEu6QS4guYFKKC2uVWhXQ+gzaiCeQGXgqj4CVgZ+MbaCUo
mCewtVpwd/3OZlcSTTQkemCY+edyODNsYtRqteNIJLKFmdg4cLGmCB2CV8/zHv30iDAMYx43K0K4
vJt8Pp/slEZDtVptQZry1yRINBqNGb8yAoLZhOLwblOO4xyhdiJ4Dw3NuK57iKhpvX1CVnZILONv
y+XynHLDQL2aIUyKQ7lw5UtcCb9kWdZ9vV5fVP4nqIfeB83wLGFXyn8qzOVyBcJVbJL3HUcuqtYP
qqkHMvVuBLPvqoU39EHhgqYYTS/4Pe5zzlrRoKwVo5CdqUZP2zTNGF9HMyj76CEUIH1mlTjhNbam
E0CcqlQqScWQratm23Y8k8k8aaYbVuB7kE6n31Cwwp0KEOyQaqFGJRsrZrPZA/KeEl/xTWEIDaB2
HyL9ArZlfFoLyg0iE/oq7AZrtXG7ndfvGKhwVPw54b/825wQbGJjqUWQZxjG6QcOaKhgVmwvxAAA
AABJRU5ErkJggg=="style="height: 17px; width: 20px; border: 0;">
</td>
<td style="color: #454545; padding: 18px 0 0 0; font-size: 12px;">
You are subscribed to receive this report <SubscriptionFrequency>.<br />
<a href="<ReportLink>" style="text-decoration: none; color: #226396;">Edit or cancel your subscription.</a>
</td>
</tr>
</TABLE>
</td>
</tr>
</TABLE>
</body>
</html>', subject = '<ReportName>'
WHERE id = 350;