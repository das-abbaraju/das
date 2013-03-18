insert into report_permission_group (id, createdBy, updatedBy, creationDate, updateDate, reportID, userID, editable)
select rpu.id, rpu.createdBy, rpu.updatedBy, rpu.creationDate, rpu.updateDate, rpu.reportID, rpu.userID, rpu.editable
from report_permission_user rpu
join users u on rpu.userID = u.id and u.isGroup = 'Yes';

delete rpu from report_permission_user rpu
join users u on rpu.userID = u.id and u.isGroup = 'Yes';