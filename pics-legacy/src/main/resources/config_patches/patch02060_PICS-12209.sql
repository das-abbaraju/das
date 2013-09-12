-- insert keys if needed
insert IGNORE into app_translation
	(msgKey, locale,
	msgValue,
	createdBy, updatedBy, creationDate, updateDate, qualityRating, applicable, sourceLanguage, contentDriven, js)
	values
	('EmailTemplate.356.translatedSubject', 'en',
	'Notification of Interest',
	37745, 37745, NOW(), NOW(), 2, 1, 'en', 0, 0 );

insert IGNORE into app_translation
	(msgKey, locale,
	msgValue,
	createdBy, updatedBy, creationDate, updateDate, qualityRating, applicable, sourceLanguage, contentDriven, js)
	values
	('EmailTemplate.356.translatedBody', 'en',
	'notification of interest body',
	37745, 37745, NOW(), NOW(), 2, 1, 'en', 0, 0 );

insert IGNORE into app_translation
	(msgKey, locale,
	msgValue,
	createdBy, updatedBy, creationDate, updateDate, qualityRating, applicable, sourceLanguage, contentDriven, js)
	values
	('EmailTemplate.354.translatedSubject', 'en',
	'Notification of Interest Sent',
	37745, 37745, NOW(), NOW(), 2, 1, 'en', 0, 0 );

insert IGNORE into app_translation
	(msgKey, locale,
	msgValue,
	createdBy, updatedBy, creationDate, updateDate, qualityRating, applicable, sourceLanguage, contentDriven, js)
	values
	('EmailTemplate.354.translatedBody', 'en',
	'Notification of interest sent body',
	37745, 37745, NOW(), NOW(), 2, 1, 'en', 0, 0 );

-- update keys
update app_translation
set msgValue = 'Notification of Interest'
where msgKey = 'EmailTemplate.356.translatedSubject' ;

update app_translation
set msgValue = 'Notification of Interest Sent'
where msgKey = 'EmailTemplate.354.translatedSubject' ;

update app_translation
set msgValue = '<!DOCTYPE html>
<html>
	<head>
		<meta content="text/html; charset=utf-8" http-equiv="Content-Type" />
		<meta content="width=device-width, initial-scale=1.0" name="viewport" />
		<title>PICS Organizer</title>
		<style media="screen" type="text/css">
.ReadMsgBody {width:100%;}
	.ExternalClass {width:100%;}
	.ExternalClass, .ExternalClass p, .ExternalClass span, .ExternalClass font, .ExternalClass td, .ExternalClass div {line-height:1.5;}
	img {outline:none; text-decoration:none; -ms-interpolation-mode: bicubic;}
	table td {border-collapse: collapse;}
	h1 {font-size:24px;color:#a84d0f !important;}
	a {color:#002441 !important;text-decoration:none;}
	.content p {font-size:15px;color:#333333;}
	.signature p {font-size:13px;color:#333333;}
	@media only screen and (orientation:portrait) and (max-width:480px) {
		table[class=container] {width:auto !important;}
	}		</style>
	</head>
	<body style="background:#002441;width:100% !important;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;margin:0;padding:0;">
		<table bgcolor="#002441" border="0" cellpadding="0" cellspacing="0" style="margin:0;padding:0;width:100% !important;line-height:1.5 !important;font-family:Helvetica,Arial,sans-serif;background:#002441;border-collapse:collapse;mso-table-lspace:0pt;mso-table-rspace:0pt;" width="100%">
			<tbody>
				<tr>
					<td style="padding:40px 10px;">
						<table align="center" bgcolor="#ffffff" border="0" cellpadding="0" cellspacing="0" class="container" style="background:#ffffff" width="600">
							<tbody>
								<tr>
									<td bgcolor="#f1f2f2" style="padding:24px 30px 16px;background:#f1f2f2">
										<a href="http://www.picsauditing.com"><img align="none" alt="" border="0" height="56" hspace="0" src="https://df6357ff93-custmedia.vresp.com/8c91bda7a3/PICS-FINAL-LOGO.gif" style="width: 160px; height: 56px;" title="" vspace="0" width="160" /></a></td>
								</tr>
								<tr>
									<td bgcolor="#ffffff" class="content" style="padding:30px 30px 0;background:#ffffff;font-size:15px;color:#333333;">
										<p>
											Dear $contractor.name,<br />
											<br />
											PICS is excited that your company has begun the registration process for $contractor.requestedBy.name. We would like to inform you that $permissions.accountName has also indicated that they would like to have you on their approved vendor list.<br />
											<br />
											To complete your registration, please log in at the following link <a href="http://www.picsorganizer.com/Login.action">PICS Organizer</a>. Once you have completed the registration process, you will be assigned a customer service representative that will assist you in completing requirements for both of the client sites. <br />
											<br />
											If you require any assistance with completing your registration, feel free to call the Registration hotline at 877-725-3022.  They can also be reached at <a href="mailto:registrations@picsauditing.com" style="color:#002441 !important;text-decoration:none;">Registrations@PICSauditing.com.</a><br />
											<br />
											<span style="line-height: 1.5;">Thank you,</span></p>
									</td>
								</tr>
								<tr>
									<td bgcolor="#ffffff" class="signature" style="padding:0 30px 30px;background:#ffffff;font-size:13px;color:#333333;">
										<p>
											<strong><span style="line-height: 1.5;">The PICS Registration Team</span></strong></p>
										<p>
											<countryspecificpicsname></countryspecificpicsname></p>
										<p style="font-weight:bold;color:#002441;">
											<countryspecificsalesphone><br />
											<a href="mailto:registrations@picsauditing.com" style="color:#002441 !important;text-decoration:none;">Registrations@PICSauditing.com</a></countryspecificsalesphone></p>
									</td>
								</tr>
							</tbody>
						</table>
					</td>
				</tr>
			</tbody>
		</table>
		<br />
	</body>
</html>
'
where msgKey = 'EmailTemplate.356.translatedBody' ;

update app_translation
set msgValue = '<!DOCTYPE html>
<html>
	<head>
		<meta content="text/html; charset=utf-8" http-equiv="Content-Type" />
		<meta content="width=device-width, initial-scale=1.0" name="viewport" />
		<title>PICS Organizer</title>
		<style media="screen" type="text/css">
.ReadMsgBody {width:100%;}
	.ExternalClass {width:100%;}
	.ExternalClass, .ExternalClass p, .ExternalClass span, .ExternalClass font, .ExternalClass td, .ExternalClass div {line-height:1.5;}
	img {outline:none; text-decoration:none; -ms-interpolation-mode: bicubic;}
	table td {border-collapse: collapse;}
	h1 {font-size:24px;color:#a84d0f !important;}
	a {color:#002441 !important;text-decoration:none;}
	.content p {font-size:15px;color:#333333;}
	.signature p {font-size:13px;color:#333333;}
	@media only screen and (orientation:portrait) and (max-width:480px) {
		table[class=container] {width:auto !important;}
	}		</style>
	</head>
	<body style="background:#002441;width:100% !important;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;margin:0;padding:0;">
		<table bgcolor="#002441" border="0" cellpadding="0" cellspacing="0" style="margin:0;padding:0;width:100% !important;line-height:1.5 !important;font-family:Helvetica,Arial,sans-serif;background:#002441;border-collapse:collapse;mso-table-lspace:0pt;mso-table-rspace:0pt;" width="100%">
			<tbody>
				<tr>
					<td style="padding:40px 10px;">
						<table align="center" bgcolor="#ffffff" border="0" cellpadding="0" cellspacing="0" class="container" style="background:#ffffff" width="600">
							<tbody>
								<tr>
									<td bgcolor="#f1f2f2" style="padding:24px 30px 16px;background:#f1f2f2">
										<a href="http://www.picsauditing.com"><img align="none" alt="" border="0" height="56" hspace="0" src="https://df6357ff93-custmedia.vresp.com/8c91bda7a3/PICS-FINAL-LOGO.gif" style="width: 160px; height: 56px;" title="" vspace="0" width="160" /></a></td>
								</tr>
								<tr>
									<td bgcolor="#ffffff" class="content" style="padding:30px 30px 0;background:#ffffff;font-size:15px;color:#333333;">
										<p>
											Dear $permissions.name,<br />
											<br />
											The company you have requested is currently in the process of registering with PICS.<br />
											<br />
											$contractor.name has been notified via email that you have an interest in selecting them for future business.<br />
											<br />
											<span style="line-height: 1.5;">Thank you,</span></p>
									</td>
								</tr>
								<tr>
									<td bgcolor="#ffffff" class="signature" style="padding:0 30px 30px;background:#ffffff;font-size:13px;color:#333333;">
										<p>
											<strong><span style="line-height: 1.5;">The PICS Registration Team</span></strong></p>
										<p>
											<countryspecificpicsname></countryspecificpicsname></p>
										<p style="font-weight:bold;color:#002441;">
											<countryspecificsalesphone><br />
											<a href="mailto:registrations@picsauditing.com" style="color:#002441 !important;text-decoration:none;">Registrations@PICSauditing.com</a></countryspecificsalesphone></p>
									</td>
								</tr>
							</tbody>
						</table>
					</td>
				</tr>
			</tbody>
		</table>
		<br />
	</body>
</html>
'
where msgKey = 'EmailTemplate.354.translatedBody' ;

