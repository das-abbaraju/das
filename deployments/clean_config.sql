-- Clean up the application data in config that was just copied from Yesterday (Live)
update users set password = SHA1(CONCAT('M0ckingj@y',id)) WHERE accountID != 1100;
update users set email = 'tester@picsauditing.com' WHERE accountID != 1100 AND email > '';
update employee set email = 'tester@picsauditing.com' where email > '';

update generalcontractors set baselineFlag = flag, baselineFlagDetail = flagDetail;
update flag_data set baselineFlag = flag;

update app_properties set value = '2J862r8678vrx3d3evk6m44B9RHXcWUv' where property = 'brainTree.key';
update app_properties set value = '1884502' where property = 'brainTree.key_id';
update app_properties set value = 'picstest' where property = 'brainTree.username';
update app_properties set value = 'password1' where property = 'brainTree.password';
update app_properties set value = 'ccprocessora' where property = 'brainTree.processor_id.us';
update app_properties set value = 'ccprocessorb' where property = 'brainTree.processor_id.canada';

/* Update the date loaded for this database */
UPDATE app_properties SET value = NOW() WHERE property = 'PICS.db.dateLoaded';
UPDATE app_properties SET value = '1' WHERE property = 'PICS.config';
