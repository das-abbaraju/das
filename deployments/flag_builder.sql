-- This is an experimental script to quickly generate pre-flag colors for contractors used on contractor search

TRUNCATE TABLE flags;

-- 30 seconds without indexes
INSERT INTO flags (conID, opID, flag)
SELECT c.id, o.id, 'Green'
FROM accounts c, accounts o
WHERE c.type = 'Contractor' and o.type IN ('Operator','Corporate')
AND c.status IN ('Active','Demo')
AND o.status IN ('Active','Demo');

-- 15 minutes run time
UPDATE flags, (
	SELECT fcc.conID, fco.opID, fco.flag FROM flag_criteria fc
	JOIN flag_criteria_operator fco ON fc.id = fco.criteriaID
	JOIN flag_criteria_contractor fcc ON fc.id = fcc.criteriaID
	WHERE fc.comparison = '>' AND fc.dataType = 'number' AND fc.allowCustomValue = 1
	AND fco.hurdle < fcc.answer
) t
SET flags.flag = t.flag
WHERE flags.conID = t.conID AND flags.opID = t.opID;

-- 15 minutes run time (320225 rows)
UPDATE flags, (
SELECT fcc.conID, fco.opID, fco.flag from flag_criteria fc
JOIN flag_criteria_operator fco on fc.id = fco.criteriaID
JOIN flag_criteria_contractor fcc ON fc.id = fcc.criteriaID
WHERE fc.dataType = 'boolean'
AND fc.defaultValue = fcc.answer
) t
SET flags.flag = t.flag
WHERE flags.conID = t.conID AND flags.opID = t.opID
and (flags.flag = 'Green' OR (flags.flag = 'Amber' AND t.flag = 'Red'));

UPDATE flags, (
	SELECT fcc.conID, fco.opID, fco.flag FROM flag_criteria fc
	JOIN flag_criteria_operator fco ON fc.id = fco.criteriaID
	JOIN flag_criteria_contractor fcc ON fc.id = fcc.criteriaID
	WHERE fc.comparison = '>' AND fc.dataType = 'number' AND fc.allowCustomValue = 1
	AND fco.hurdle < fcc.answer
) t
SET flags.flag = t.flag
WHERE flags.conID = t.conID AND flags.opID = t.opID;
