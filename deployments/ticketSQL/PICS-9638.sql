use pics_alpha;
update app_translation a set a.msgValue = replace(a.msgValue, "${contractor.auditor.name}", "<p>${contractor.auditor.name}") where a.msgKey = 'email.csrfooter';
update app_translation a set a.msgValue = replace(a.msgValue, "auditor.account.name}<br />", "auditor.account.name}</p><p style=\"font-weight:bold;color:#002441;\">") where a.msgKey = 'email.csrfooter';
update app_translation a set a.msgValue = replace(a.msgValue, "auditor.fax}<br />", "auditor.fax}<br /><a href=\"mailto:${contractor.auditor.email}\" style=\"color:#002441 !important;text-decoration:none;\">") where a.msgKey = 'email.csrfooter';
update app_translation a set a.msgValue = replace(a.msgValue, "auditor.email}", "auditor.email}</a></p>") where a.msgKey = 'email.csrfooter';
update app_translation a set a.msgValue = replace(a.msgValue, "auditor.email}</a></p>\"", "auditor.email}\"") where a.msgKey = 'email.csrfooter';
