update users set password = SHA1(CONCAT('kkttl5',id));
update contractor_info set taxID = '123456789';
update users set email = 'tester@picsauditing.com' where email > '';
update employee set email = 'tester@picsauditing.com' where email > '';
update employee set ssn = '123456789' where ssn > '';
update email_queue set toAddresses = 'tester@picsauditing.com', ccAddresses = null, bccAddresses = null, fromPassword = null, fromAddress = null;

update generalcontractors set baselineFlag = flag, baselineFlagDetail = flagDetail;
update flag_data set baselineFlag = flag;

update app_properties set value = '2J862r8678vrx3d3evk6m44B9RHXcWUv' where property = 'brainTree.key';
update app_properties set value = '1884502' where property = 'brainTree.key_id';
update app_properties set value = 'picstest' where property = 'brainTree.username';
update app_properties set value = 'password1' where property = 'brainTree.password';
update app_properties set value = 'ccprocessora' where property = 'brainTree.processor_id.us';
update app_properties set value = 'ccprocessorb' where property = 'brainTree.processor_id.canada';

/* Update the date loaded for this database */
UPDATE app_properties SET VALUE = NOW() WHERE property = 'PICS.db.dateLoaded';
