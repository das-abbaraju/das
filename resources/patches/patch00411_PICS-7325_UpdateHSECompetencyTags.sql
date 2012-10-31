update operator_tag
set category = 'CompetencyReview'
where tag = 'HSE Competency'
or id = 142;

update operator_tag
set category = 'OperatorQualification'
where tag = 'Operator Qualification (OQ)';

update operator_tag
set category = 'OtherEmployeeGUARD'
where tag IN ('Implementation Audit Plus', 'Integrity Management');