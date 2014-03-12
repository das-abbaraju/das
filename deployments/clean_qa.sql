UPDATE	app_user
,	users
SET app_user.password = SHA1(CONCAT('Wait7Lib', app_user.hashSalt))
WHERE	1
AND	users.accountID != 1100
AND	app_user.username	= users.username
;
update contractor_info set taxID = '123456789';
update users set email = 'tester@picsauditing.com' WHERE accountID != 1100 AND email > '';
update employee set email = 'tester@picsauditing.com' where email > '';
update accounts set email = 'tester@picsauditing.com' where email > '';
update employee set ssn = '123456789' where ssn > '';
update assessment_result_stage set email = 'tester@picsauditing.com' where email > '';
update contractor_registration_request set email = 'tester@picsauditing.com' where email > '';
update email_exclusion set email = 'tester@picsauditing.com' where email > '';
update ncms_contractors set email = 'tester@picsauditing.com' where email > '';
update operator_referral set email = 'tester@picsauditing.com' where email > '';
update profile set email = 'tester@picsauditing.com' where email > '';
update email_queue set toAddresses = 'tester@picsauditing.com', ccAddresses = null where status = 'Pending' and (toAddresses not like '%picsauditing.com' or ccAddresses not like '%picsauditing.com');
update contractor_operator set baselineFlag = flag, baselineFlagDetail = flagDetail;
update flag_data set baselineFlag = flag;

update app_properties set value = '2J862r8678vrx3d3evk6m44B9RHXcWUv' where property = 'brainTree.key';
update app_properties set value = '1884502' where property = 'brainTree.key_id';
update app_properties set value = 'picstest' where property = 'brainTree.username';
update app_properties set value = 'password1' where property = 'brainTree.password';
update app_properties set value = 'ccprocessora' where property like '%brainTree.processor_id.us%' or property like '%brainTree.processor_id.canada%';
update app_properties set value = 'ccprocessorb' where property like '%brainTree.processor_id.gbp%' or property like '%brainTree.processor_id.eur%';
insert into app_properties (property, value, description) values ('Toggle.UseMockPaymentService', 'true', 'Interacts with fack BrainTree but allows registration to go through') on duplicate key update value = 'true';

/* Update the date loaded for this database */
UPDATE app_properties SET value = NOW() WHERE property = 'PICS.db.dateLoaded';
