-- PICS-3414 removing spaces from the msgKey in these translations
update app_translation set msgKey = "ContractorEdit.error.BrochureFormat" where msgKey = "ContractorEdit.error.Brochure Format"

-- PICS-2600 Allow Audit Types to be hidden from Operators but not contractors
update audit_type set canOperatorView=0 where id=9 or id=232 or id=269 or id=270 or id =272 or id=281;
