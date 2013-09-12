-- 34515 Valspar - Birmingham, AL
-- 23335 Valspar - Garland, TX
-- 33056 Valspar - Rochester, PA
UPDATE accounts
SET requiresCompetencyReview = 1
WHERE id IN (34515, 23335, 33056);

INSERT INTO operator_competency (`category`, `label`, `description`, `opID`, `createdBy`, `creationDate`)
SELECT 'Paperwork', 'Induction', 'Induction Training', 34515, 23157, NOW()
FROM operator_competency
WHERE NOT EXISTS
(
	SELECT * FROM operator_competency
	WHERE category = 'Paperwork'
	AND label = 'Induction'
	AND description = 'Induction Training'
	AND opID = 34515
)
LIMIT 1;

INSERT INTO operator_competency (`category`, `label`, `description`, `opID`, `createdBy`, `creationDate`)
SELECT 'Paperwork', 'Induction', 'Induction Training', 23335, 23157, NOW()
FROM operator_competency
WHERE NOT EXISTS
(
	SELECT * FROM operator_competency
	WHERE category = 'Paperwork'
	AND label = 'Induction'
	AND description = 'Induction Training'
	AND opID = 23335
)
LIMIT 1;

INSERT INTO operator_competency (`category`, `label`, `description`, `opID`, `createdBy`, `creationDate`)
SELECT 'Paperwork', 'Induction', 'Induction Training', 33056, 23157, NOW()
FROM operator_competency
WHERE NOT EXISTS
(
	SELECT * FROM operator_competency
	WHERE category = 'Paperwork'
	AND label = 'Induction'
	AND description = 'Induction Training'
	AND opID = 33056
)
LIMIT 1;

INSERT INTO operator_competency_course (`competencyID`, `courseType`, `createdBy`, `creationDate`)
SELECT oc.id, 'REQUIRES_DOCUMENTATION', 23157, NOW()
FROM operator_competency oc
LEFT JOIN operator_competency_course occ ON occ.`competencyID` = oc.id AND occ.`courseType` = 'REQUIRES_DOCUMENTATION'
WHERE oc.`opID` IN (34515, 23335, 33056)
AND oc.`label` = 'Induction'
AND occ.id IS NULL;