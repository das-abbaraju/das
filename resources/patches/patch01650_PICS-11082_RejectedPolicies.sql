-- update expiration answers
UPDATE
    pqfdata pd
    JOIN contractor_audit ca ON pd.auditID = ca.id
    JOIN contractor_audit_operator cao ON ca.id = cao.auditID
    JOIN audit_type typ ON typ.id = ca.auditTypeID
SET pd.answer = DATE_FORMAT(STR_TO_DATE(pd.answer, '%m/%d/%Y'), '%Y-%m-%d')
WHERE 1
      AND pd.answer LIKE '%/%'
      AND cao.status IN ('Incomplete', 'Submitted', 'Complete', 'Approved')
      AND cao.visible=1
      AND typ.id=13
      AND pd.questionID=2082
      AND ca.expiresDate IS NULL;

UPDATE
    pqfdata pd
    JOIN contractor_audit ca ON pd.auditID = ca.id
    JOIN contractor_audit_operator cao ON ca.id = cao.auditID
    JOIN audit_type typ ON typ.id = ca.auditTypeID
SET pd.answer = DATE_FORMAT(STR_TO_DATE(pd.answer, '%m/%d/%Y'), '%Y-%m-%d')
WHERE 1
      AND pd.answer LIKE '%/%'
      AND cao.status IN ('Incomplete', 'Submitted', 'Complete', 'Approved')
      AND cao.visible=1
      AND typ.id=14
      AND pd.questionID=2105
      AND ca.expiresDate IS NULL;

UPDATE
    pqfdata pd
    JOIN contractor_audit ca ON pd.auditID = ca.id
    JOIN contractor_audit_operator cao ON ca.id = cao.auditID
    JOIN audit_type typ ON typ.id = ca.auditTypeID
SET pd.answer = DATE_FORMAT(STR_TO_DATE(pd.answer, '%m/%d/%Y'), '%Y-%m-%d')
WHERE 1
      AND pd.answer LIKE '%/%'
      AND cao.status IN ('Incomplete', 'Submitted', 'Complete', 'Approved')
      AND cao.visible=1
      AND typ.id=15
      AND pd.questionID=2111
      AND ca.expiresDate IS NULL;

UPDATE
    pqfdata pd
    JOIN contractor_audit ca ON pd.auditID = ca.id
    JOIN contractor_audit_operator cao ON ca.id = cao.auditID
    JOIN audit_type typ ON typ.id = ca.auditTypeID
SET pd.answer = DATE_FORMAT(STR_TO_DATE(pd.answer, '%m/%d/%Y'), '%Y-%m-%d')
WHERE 1
      AND pd.answer LIKE '%/%'
      AND cao.status IN ('Incomplete', 'Submitted', 'Complete', 'Approved')
      AND cao.visible=1
      AND typ.id=16
      AND pd.questionID=2117
      AND ca.expiresDate IS NULL;

UPDATE
    pqfdata pd
    JOIN contractor_audit ca ON pd.auditID = ca.id
    JOIN contractor_audit_operator cao ON ca.id = cao.auditID
    JOIN audit_type typ ON typ.id = ca.auditTypeID
SET pd.answer = DATE_FORMAT(STR_TO_DATE(pd.answer, '%m/%d/%Y'), '%Y-%m-%d')
WHERE 1
      AND pd.answer LIKE '%/%'
      AND cao.status IN ('Incomplete', 'Submitted', 'Complete', 'Approved')
      AND cao.visible=1
      AND typ.id=20
      AND pd.questionID=2123
      AND ca.expiresDate IS NULL;

UPDATE
    pqfdata pd
    JOIN contractor_audit ca ON pd.auditID = ca.id
    JOIN contractor_audit_operator cao ON ca.id = cao.auditID
    JOIN audit_type typ ON typ.id = ca.auditTypeID
SET pd.answer = DATE_FORMAT(STR_TO_DATE(pd.answer, '%m/%d/%Y'), '%Y-%m-%d')
WHERE 1
      AND pd.answer LIKE '%/%'
      AND cao.status IN ('Incomplete', 'Submitted', 'Complete', 'Approved')
      AND cao.visible=1
      AND typ.id=22
      AND pd.questionID=2135
      AND ca.expiresDate IS NULL;

UPDATE
    pqfdata pd
    JOIN contractor_audit ca ON pd.auditID = ca.id
    JOIN contractor_audit_operator cao ON ca.id = cao.auditID
    JOIN audit_type typ ON typ.id = ca.auditTypeID
SET pd.answer = DATE_FORMAT(STR_TO_DATE(pd.answer, '%m/%d/%Y'), '%Y-%m-%d')
WHERE 1
      AND pd.answer LIKE '%/%'
      AND cao.status IN ('Incomplete', 'Submitted', 'Complete', 'Approved')
      AND cao.visible=1
      AND typ.id=23
      AND pd.questionID=2141
      AND ca.expiresDate IS NULL;

UPDATE
    pqfdata pd
    JOIN contractor_audit ca ON pd.auditID = ca.id
    JOIN contractor_audit_operator cao ON ca.id = cao.auditID
    JOIN audit_type typ ON typ.id = ca.auditTypeID
SET pd.answer = DATE_FORMAT(STR_TO_DATE(pd.answer, '%m/%d/%Y'), '%Y-%m-%d')
WHERE 1
      AND pd.answer LIKE '%/%'
      AND cao.status IN ('Incomplete', 'Submitted', 'Complete', 'Approved')
      AND cao.visible=1
      AND typ.id=74
      AND pd.questionID=3024
      AND ca.expiresDate IS NULL;

UPDATE
    pqfdata pd
    JOIN contractor_audit ca ON pd.auditID = ca.id
    JOIN contractor_audit_operator cao ON ca.id = cao.auditID
    JOIN audit_type typ ON typ.id = ca.auditTypeID
SET pd.answer = DATE_FORMAT(STR_TO_DATE(pd.answer, '%m/%d/%Y'), '%Y-%m-%d')
WHERE 1
      AND pd.answer LIKE '%/%'
      AND cao.status IN ('Incomplete', 'Submitted', 'Complete', 'Approved')
      AND cao.visible=1
      AND typ.id=88
      AND pd.questionID=3466
      AND ca.expiresDate IS NULL;

-- update expiration dates
UPDATE
    contractor_audit ca
    JOIN contractor_audit_operator cao ON ca.id = cao.auditID
    JOIN audit_type typ ON typ.id = ca.auditTypeID
    JOIN pqfdata pd ON pd.auditID = ca.id
SET ca.expiresDate = DATE_ADD(DATE(pd.answer), INTERVAL 1 DAY)
WHERE 1
      AND cao.status IN ('Incomplete', 'Submitted', 'Complete', 'Approved')
      AND cao.visible=1
      AND typ.id=13
      AND pd.questionID=2082
      AND pd.answer LIKE '____-__-__'
      AND CAST(MID(pd.answer, 6, 2) AS UNSIGNED INTEGER) <= 12
      AND pd.answer NOT LIKE '%13-02-29'
      AND DATE(pd.answer) IS NOT NULL
      AND ca.expiresDate IS NULL;

UPDATE
    contractor_audit ca
    JOIN contractor_audit_operator cao ON ca.id = cao.auditID
    JOIN audit_type typ ON typ.id = ca.auditTypeID
    JOIN pqfdata pd ON pd.auditID = ca.id
SET ca.expiresDate = DATE_ADD(DATE(pd.answer), INTERVAL 1 DAY)
WHERE 1
      AND cao.status IN ('Incomplete', 'Submitted', 'Complete', 'Approved')
      AND cao.visible=1
      AND typ.id=14
      AND pd.questionID=2105
      AND pd.answer LIKE '____-__-__'
      AND CAST(MID(pd.answer, 6, 2) AS UNSIGNED INTEGER) <= 12
      AND pd.answer NOT LIKE '%13-02-29'
      AND DATE(pd.answer) IS NOT NULL
      AND ca.expiresDate IS NULL;

UPDATE
    contractor_audit ca
    JOIN contractor_audit_operator cao ON ca.id = cao.auditID
    JOIN audit_type typ ON typ.id = ca.auditTypeID
    JOIN pqfdata pd ON pd.auditID = ca.id
SET ca.expiresDate = DATE_ADD(DATE(pd.answer), INTERVAL 1 DAY)
WHERE 1
      AND cao.status IN ('Incomplete', 'Submitted', 'Complete', 'Approved')
      AND cao.visible=1
      AND typ.id=15
      AND pd.questionID=2111
      AND pd.answer LIKE '____-__-__'
      AND CAST(MID(pd.answer, 6, 2) AS UNSIGNED INTEGER) <= 12
      AND pd.answer NOT LIKE '%13-02-29'
      AND DATE(pd.answer) IS NOT NULL
      AND ca.expiresDate IS NULL;

UPDATE
    contractor_audit ca
    JOIN contractor_audit_operator cao ON ca.id = cao.auditID
    JOIN audit_type typ ON typ.id = ca.auditTypeID
    JOIN pqfdata pd ON pd.auditID = ca.id
SET ca.expiresDate = DATE_ADD(DATE(pd.answer), INTERVAL 1 DAY)
WHERE 1
      AND cao.status IN ('Incomplete', 'Submitted', 'Complete', 'Approved')
      AND cao.visible=1
      AND typ.id=16
      AND pd.questionID=2117
      AND pd.answer LIKE '____-__-__'
      AND CAST(MID(pd.answer, 6, 2) AS UNSIGNED INTEGER) <= 12
      AND pd.answer NOT LIKE '%13-02-29'
      AND DATE(pd.answer) IS NOT NULL
      AND ca.expiresDate IS NULL;

UPDATE
    contractor_audit ca
    JOIN contractor_audit_operator cao ON ca.id = cao.auditID
    JOIN audit_type typ ON typ.id = ca.auditTypeID
    JOIN pqfdata pd ON pd.auditID = ca.id
SET ca.expiresDate = DATE_ADD(DATE(pd.answer), INTERVAL 1 DAY)
WHERE 1
      AND cao.status IN ('Incomplete', 'Submitted', 'Complete', 'Approved')
      AND cao.visible=1
      AND typ.id=20
      AND pd.questionID=2123
      AND pd.answer LIKE '____-__-__'
      AND CAST(MID(pd.answer, 6, 2) AS UNSIGNED INTEGER) <= 12
      AND pd.answer NOT LIKE '%13-02-29'
      AND DATE(pd.answer) IS NOT NULL
      AND ca.expiresDate IS NULL;

UPDATE
    contractor_audit ca
    JOIN contractor_audit_operator cao ON ca.id = cao.auditID
    JOIN audit_type typ ON typ.id = ca.auditTypeID
    JOIN pqfdata pd ON pd.auditID = ca.id
SET ca.expiresDate = DATE_ADD(DATE(pd.answer), INTERVAL 1 DAY)
WHERE 1
      AND cao.status IN ('Incomplete', 'Submitted', 'Complete', 'Approved')
      AND cao.visible=1
      AND typ.id=22
      AND pd.questionID=2135
      AND pd.answer LIKE '____-__-__'
      AND CAST(MID(pd.answer, 6, 2) AS UNSIGNED INTEGER) <= 12
      AND pd.answer NOT LIKE '%13-02-29'
      AND DATE(pd.answer) IS NOT NULL
      AND ca.expiresDate IS NULL;

UPDATE
    contractor_audit ca
    JOIN contractor_audit_operator cao ON ca.id = cao.auditID
    JOIN audit_type typ ON typ.id = ca.auditTypeID
    JOIN pqfdata pd ON pd.auditID = ca.id
SET ca.expiresDate = DATE_ADD(DATE(pd.answer), INTERVAL 1 DAY)
WHERE 1
      AND cao.status IN ('Incomplete', 'Submitted', 'Complete', 'Approved')
      AND cao.visible=1
      AND typ.id=23
      AND pd.questionID=2141
      AND pd.answer LIKE '____-__-__'
      AND CAST(MID(pd.answer, 6, 2) AS UNSIGNED INTEGER) <= 12
      AND pd.answer NOT LIKE '%13-02-29'
      AND DATE(pd.answer) IS NOT NULL
      AND ca.expiresDate IS NULL;

UPDATE
    contractor_audit ca
    JOIN contractor_audit_operator cao ON ca.id = cao.auditID
    JOIN audit_type typ ON typ.id = ca.auditTypeID
    JOIN pqfdata pd ON pd.auditID = ca.id
SET ca.expiresDate = DATE_ADD(DATE(pd.answer), INTERVAL 1 DAY)
WHERE 1
      AND cao.status IN ('Incomplete', 'Submitted', 'Complete', 'Approved')
      AND cao.visible=1
      AND typ.id=74
      AND pd.questionID=3024
      AND pd.answer LIKE '____-__-__'
      AND CAST(MID(pd.answer, 6, 2) AS UNSIGNED INTEGER) <= 12
      AND pd.answer NOT LIKE '%13-02-29'
      AND DATE(pd.answer) IS NOT NULL
      AND ca.expiresDate IS NULL;

UPDATE
    contractor_audit ca
    JOIN contractor_audit_operator cao ON ca.id = cao.auditID
    JOIN audit_type typ ON typ.id = ca.auditTypeID
    JOIN pqfdata pd ON pd.auditID = ca.id
SET ca.expiresDate = DATE_ADD(DATE(pd.answer), INTERVAL 1 DAY)
WHERE 1
      AND cao.status IN ('Incomplete', 'Submitted', 'Complete', 'Approved')
      AND cao.visible=1
      AND typ.id=88
      AND pd.questionID=3466
      AND pd.answer LIKE '____-__-__'
      AND CAST(MID(pd.answer, 6, 2) AS UNSIGNED INTEGER) <= 12
      AND pd.answer NOT LIKE '%13-02-29'
      AND DATE(pd.answer) IS NOT NULL
      AND ca.expiresDate IS NULL;


