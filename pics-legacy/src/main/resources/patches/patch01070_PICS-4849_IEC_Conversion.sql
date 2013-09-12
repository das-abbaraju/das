-- update question type
UPDATE
  audit_question aq
SET aq.questionType='Percent'
WHERE aq.id IN (10257, 10317);

-- update question text
UPDATE
  app_translation trans
SET trans.msgValue = 'What percent was achieved on Health and Safety Training section(Section 5). Value must be in decimal format, for example 85'
WHERE trans.msgKey LIKE 'AuditQuestion.10257.name' AND trans.locale='en';

UPDATE
  app_translation trans
SET trans.msgValue = 'What percent was achieved on overall Actual Compliance? Value must be in decimal format, for example 85'
WHERE trans.msgKey LIKE 'AuditQuestion.10317.name' AND trans.locale='en';

-- update flag criteria
UPDATE
  flag_criteria fc
SET fc.defaultValue = 85, fc.requiredStatus = NULL
WHERE fc.id=765;

UPDATE
  flag_criteria fc
SET fc.defaultValue = 80, fc.requiredStatus = NULL
WHERE fc.id=766;

-- update answers
UPDATE
  pqfdata pd
SET pd.answer = FORMAT(CAST(pd.answer AS DECIMAL (3, 3)) * 100, 2)
WHERE pd.questionID IN (10257, 10317)
      AND pd.answer IS NOT NULL AND pd.answer != ''
      AND pd.answer < 1;

UPDATE
  pqfdata pd
SET pd.answer = '100.00'
WHERE pd.questionID IN (10257, 10317)
      AND pd.answer IS NOT NULL AND pd.answer != ''
      AND pd.answer = 1;