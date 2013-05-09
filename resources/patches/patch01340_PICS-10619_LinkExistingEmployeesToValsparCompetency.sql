INSERT INTO employee_competency (`employeeID`, `competencyID`, `createdBy`, `creationDate`)
SELECT es.`employeeID`, oc.`id`, 23157, NOW()
FROM employee_site es
JOIN operator_competency oc ON oc.`opID` = es.`opID`
JOIN operator_competency_course occ ON occ.competencyID = oc.id AND occ.courseType = 'REQUIRES_DOCUMENTATION'
WHERE NOT EXISTS (SELECT * FROM employee_competency ec WHERE ec.employeeID = es.`employeeID` AND ec.`competencyID` = oc.`id`);