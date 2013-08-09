-- update expiration answers
UPDATE
    pqfdata pd
    JOIN contractor_audit ca ON pd.auditID = ca.id
    JOIN contractor_audit_operator cao ON ca.id = cao.auditID
    JOIN audit_type typ ON typ.id = ca.auditTypeID
SET pd.answer = DATE_FORMAT(STR_TO_DATE(pd.answer, '%d/%m/%Y'), '%Y-%m-%d')
WHERE 1
      AND (pd.answer = '29/06/2013' OR pd.answer = '31/07/2013')
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
      AND pd.answer NOT LIKE '20%'
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
      AND pd.answer NOT LIKE '20%'
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
      AND pd.answer NOT LIKE '20%'
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
      AND pd.answer NOT LIKE '20%'
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
      AND pd.answer NOT LIKE '20%'
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
      AND pd.answer NOT LIKE '20%'
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
      AND pd.answer NOT LIKE '20%'
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
      AND pd.answer NOT LIKE '20%'
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
      AND pd.answer NOT LIKE '20%'
      AND cao.status IN ('Incomplete', 'Submitted', 'Complete', 'Approved')
      AND cao.visible=1
      AND typ.id=88
      AND pd.questionID=3466
      AND ca.expiresDate IS NULL;

