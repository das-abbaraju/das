ALTER TABLE `accounts` CHANGE `name` `name` varchar(50) COLLATE utf8_general_ci,
CHANGE `address` `address` varchar(50) COLLATE utf8_general_ci,
CHANGE `address2` `address2` varchar(50) COLLATE utf8_general_ci,
CHANGE `address3` `address3` varchar(50) COLLATE utf8_general_ci,
CHANGE `city` `city` varchar(35) COLLATE utf8_general_ci,
CHANGE `email` `email` varchar(50) COLLATE utf8_general_ci,
CHANGE `web_URL` `web_URL` varchar(50) COLLATE utf8_general_ci,
CHANGE `dbaName` `dbaName` varchar(400) COLLATE utf8_general_ci,
CHANGE `nameIndex` `nameIndex` varchar(50) COLLATE utf8_general_ci,
CHANGE `reason` `reason` varchar(100) COLLATE utf8_general_ci,
CHANGE `description` `description` text COLLATE utf8_general_ci,DEFAULT CHARSET='utf8';

ALTER TABLE `ambest` DEFAULT CHARSET='utf8';

ALTER TABLE `app_error_log` CHANGE `message` `message` text COLLATE utf8_general_ci,DEFAULT CHARSET='utf8';
  
ALTER TABLE `app_filter_stats` CHANGE `filterName` `filterName` varchar(100) COLLATE utf8_general_ci,DEFAULT CHARSET='utf8';
  
ALTER TABLE `app_properties` CHANGE `value` `value` varchar(4000) COLLATE utf8_general_ci,DEFAULT CHARSET='utf8';
  
ALTER TABLE `assessment_result_stage` CHANGE `description` `description` varchar(255) COLLATE utf8_general_ci,
CHANGE `firstName` `firstName` varchar(50) COLLATE utf8_general_ci,
CHANGE `lastName` `lastName` varchar(50) COLLATE utf8_general_ci,
CHANGE `email` `email` varchar(255) COLLATE utf8_general_ci,
CHANGE `companyName` `companyName` varchar(50) COLLATE utf8_general_ci;
  
ALTER TABLE `assessment_test` CHANGE `description` `description` varchar(255) COLLATE utf8_general_ci,DEFAULT CHARSET='utf8';
  
ALTER TABLE `audit_category_rule` CHANGE `questionAnswer` `questionAnswer` varchar(100) COLLATE utf8_general_ci;
  
ALTER TABLE `audit_question` CHANGE `requiredAnswer` `requiredAnswer` varchar(100) COLLATE utf8_general_ci,
CHANGE `visibleAnswer` `visibleAnswer` varchar(100) COLLATE utf8_general_ci,
CHANGE `columnHeader` `columnHeader` varchar(30) COLLATE utf8_general_ci,
CHANGE `title` `title` varchar(250) COLLATE utf8_general_ci,
CHANGE `helpPage` `helpPage` varchar(100) COLLATE utf8_general_ci,
CHANGE `requirement` `requirement` varchar(1000) COLLATE utf8_general_ci,
CHANGE `helpText` `helpText` varchar(1000) COLLATE utf8_general_ci,DEFAULT CHARSET='utf8';
  
ALTER TABLE `audit_type` CHANGE `description` `description` varchar(255) COLLATE utf8_general_ci,DEFAULT CHARSET='utf8';
  
ALTER TABLE `audit_type_rule` CHANGE `questionAnswer` `questionAnswer` varchar(100) COLLATE utf8_general_ci;
  
ALTER TABLE `auditor_vacation` CHANGE `description` `description` varchar(100) COLLATE utf8_general_ci,DEFAULT CHARSET='utf8';
  
ALTER TABLE `certificate` CHANGE `description` `description` varchar(100) COLLATE utf8_general_ci,DEFAULT CHARSET='utf8';
  
ALTER TABLE `contractor_audit` CHANGE `auditLocation` `auditLocation` varchar(45) COLLATE utf8_general_ci,
CHANGE `auditFor` `auditFor` varchar(50) COLLATE utf8_general_ci,
CHANGE `contractorContact` `contractorContact` varchar(50) COLLATE utf8_general_ci,
CHANGE `address` `address` varchar(50) COLLATE utf8_general_ci,
CHANGE `address2` `address2` varchar(50) COLLATE utf8_general_ci,
CHANGE `city` `city` varchar(35) COLLATE utf8_general_ci,DEFAULT CHARSET='utf8';
  
ALTER TABLE `contractor_audit_file` DEFAULT CHARSET='utf8';

ALTER TABLE `contractor_audit_operator` DEFAULT CHARSET='utf8';

ALTER TABLE `contractor_info` CHANGE `description` `description` text COLLATE utf8_general_ci,
CHANGE `secondContact` `secondContact` varchar(50) COLLATE utf8_general_ci,
CHANGE `secondEmail` `secondEmail` varchar(50) COLLATE utf8_general_ci,
CHANGE `billingContact` `billingContact` varchar(50) COLLATE utf8_general_ci,
CHANGE `billingEmail` `billingEmail` varchar(50) COLLATE utf8_general_ci,
CHANGE `billingAddress` `billingAddress` varchar(50) COLLATE utf8_general_ci,
CHANGE `billingCity` `billingCity` varchar(35) COLLATE utf8_general_ci,
CHANGE `ccEmail` `ccEmail` varchar(50) COLLATE utf8_general_ci,
CHANGE `tradesSelf` `tradesSelf` varchar(4000) COLLATE utf8_general_ci,
CHANGE `tradesSub` `tradesSub` varchar(4000) COLLATE utf8_general_ci,DEFAULT CHARSET='utf8';
  
ALTER TABLE `contractor_registration_request` CHANGE `name` `name` varchar(100) COLLATE utf8_general_ci,
CHANGE `requestedByUser` `requestedByUser` varchar(20) COLLATE utf8_general_ci,
CHANGE `contact` `contact` varchar(30) COLLATE utf8_general_ci,
CHANGE `email` `email` varchar(50) COLLATE utf8_general_ci,
CHANGE `address` `address` varchar(100) COLLATE utf8_general_ci,
CHANGE `city` `city` varchar(50) COLLATE utf8_general_ci,
CHANGE `notes` `notes` text COLLATE utf8_general_ci,
CHANGE `reasonForRegistration` `reasonForRegistration` varchar(500) COLLATE utf8_general_ci,
CHANGE `reasonForDecline` `reasonForDecline` varchar(500) COLLATE utf8_general_ci,DEFAULT CHARSET='utf8';
  
ALTER TABLE `email_queue` CHANGE `fromAddress` `fromAddress` varchar(150) COLLATE utf8_general_ci,
CHANGE `toAddresses` `toAddresses` varchar(1000) COLLATE utf8_general_ci,
CHANGE `ccAddresses` `ccAddresses` varchar(2000) COLLATE utf8_general_ci,
CHANGE `bccAddresses` `bccAddresses` varchar(2000) COLLATE utf8_general_ci,
CHANGE `subject` `subject` varchar(150) COLLATE utf8_general_ci,
CHANGE `body` `body` mediumtext COLLATE utf8_general_ci,DEFAULT CHARSET='utf8';
  
ALTER TABLE `email_template` CHANGE `templateName` `templateName` varchar(50) COLLATE utf8_general_ci,
CHANGE `subject` `subject` varchar(150) COLLATE utf8_general_ci,
CHANGE `body` `body` text COLLATE utf8_general_ci,DEFAULT CHARSET='utf8';
  
ALTER TABLE `employee` CHANGE `firstName` `firstName` varchar(50) COLLATE utf8_general_ci,
CHANGE `lastName` `lastName` varchar(50) COLLATE utf8_general_ci,
CHANGE `title` `title` varchar(100) COLLATE utf8_general_ci,
CHANGE `location` `location` varchar(100) COLLATE utf8_general_ci,
CHANGE `email` `email` varchar(100) COLLATE utf8_general_ci,DEFAULT CHARSET='utf8';
  
ALTER TABLE `file_attachment` CHANGE `fileName` `fileName` varchar(100) COLLATE utf8_general_ci,
CHANGE `directory` `directory` varchar(255) COLLATE utf8_general_ci,
CHANGE `extension` `extension` char(5) COLLATE utf8_general_ci;
  
ALTER TABLE `flag_criteria` CHANGE `label` `label` varchar(30) COLLATE utf8_general_ci,
CHANGE `description` `description` varchar(255) COLLATE utf8_general_ci,
CHANGE `defaultValue` `defaultValue` varchar(100) COLLATE utf8_general_ci,DEFAULT CHARSET='utf8';
  
ALTER TABLE `flag_override_history` CHANGE `forceReason` `forceReason` varchar(255) COLLATE utf8_general_ci,
CHANGE `deleteReason` `deleteReason` varchar(255) COLLATE utf8_general_ci;
  
ALTER TABLE `generalcontractors` CHANGE `forceReason` `forceReason` varchar(255) COLLATE utf8_general_ci,
CHANGE `flagDetail` `flagDetail` text COLLATE utf8_general_ci,
CHANGE `baselineFlagDetail` `baselineFlagDetail` text COLLATE utf8_general_ci,
CHANGE `requestedByUser` `requestedByUser` varchar(20) COLLATE utf8_general_ci,
CHANGE `reasonForRegistration` `reasonForRegistration` varchar(500) COLLATE utf8_general_ci,DEFAULT CHARSET='utf8';
  
ALTER TABLE `invoice` CHANGE `notes` `notes` text COLLATE utf8_general_ci;
  
ALTER TABLE `invoice_fee` CHANGE `fee` `fee` varchar(100) COLLATE utf8_general_ci,
CHANGE `qbFullName` `qbFullName` varchar(75) COLLATE utf8_general_ci,DEFAULT CHARSET='utf8';
  
ALTER TABLE `invoice_item` CHANGE `description` `description` varchar(100) COLLATE utf8_general_ci,DEFAULT CHARSET='utf8';
  
ALTER TABLE `iplookup` CHANGE `location` `location` varchar(255) COLLATE utf8_general_ci,
CHANGE `hostname` `hostname` varchar(255) COLLATE utf8_general_ci;
  
ALTER TABLE `job_role` CHANGE `name` `name` varchar(100) COLLATE utf8_general_ci,DEFAULT CHARSET='utf8';
  
ALTER TABLE `job_site` CHANGE `label` `label` varchar(15) COLLATE utf8_general_ci,
CHANGE `name` `name` varchar(255) COLLATE utf8_general_ci,
CHANGE `city` `city` varchar(30) COLLATE utf8_general_ci,DEFAULT CHARSET='utf8';
  
ALTER TABLE `job_task` CHANGE `label` `label` varchar(15) COLLATE utf8_general_ci,
CHANGE `name` `name` varchar(255) COLLATE utf8_general_ci;
  
ALTER TABLE `loginlog` CHANGE `browser` `browser` varchar(50) COLLATE utf8_general_ci,
CHANGE `fullUserAgent` `fullUserAgent` text COLLATE utf8_general_ci;
  
ALTER TABLE `naics_lookup` CHANGE `description` `description` varchar(200) COLLATE utf8_general_ci,DEFAULT CHARSET='utf8';
  
ALTER TABLE `ncms_contractors` CHANGE `ContractorsName` `ContractorsName` varchar(250) COLLATE utf8_general_ci,
CHANGE `Address` `Address` varchar(100) COLLATE utf8_general_ci,
CHANGE `City` `City` varchar(50) COLLATE utf8_general_ci,
CHANGE `Contact` `Contact` varchar(50) COLLATE utf8_general_ci,
CHANGE `EMAIL` `EMAIL` varchar(100) COLLATE utf8_general_ci,
CHANGE `Multi_Location_Com` `Multi_Location_Com` mediumtext COLLATE utf8_general_ci,DEFAULT CHARSET='utf8';
  
ALTER TABLE `ncms_desktop` CHANGE `ContractorsName` `ContractorsName` varchar(250) COLLATE utf8_general_ci,DEFAULT CHARSET='utf8';
  
ALTER TABLE `note` DEFAULT CHARSET='utf8';

ALTER TABLE `operator_competency` CHANGE `category` `category` varchar(50) COLLATE utf8_general_ci,
CHANGE `label` `label` varchar(25) COLLATE utf8_general_ci,
CHANGE `description` `description` varchar(255) COLLATE utf8_general_ci,
CHANGE `helpPage` `helpPage` varchar(100) COLLATE utf8_general_ci,DEFAULT CHARSET='utf8';
  
ALTER TABLE `operator_referral` CHANGE `sourceContact` `sourceContact` varchar(30) COLLATE utf8_general_ci,
CHANGE `sourceEmail` `sourceEmail` varchar(50) COLLATE utf8_general_ci,
CHANGE `name` `name` varchar(100) COLLATE utf8_general_ci,
CHANGE `contact` `contact` varchar(30) COLLATE utf8_general_ci,
CHANGE `email` `email` varchar(50) COLLATE utf8_general_ci,
CHANGE `notes` `notes` text COLLATE utf8_general_ci,
CHANGE `reasonForDecline` `reasonForDecline` varchar(500) COLLATE utf8_general_ci,DEFAULT CHARSET='utf8';
  
ALTER TABLE `operator_tag` CHANGE `tag` `tag` varchar(50) COLLATE utf8_general_ci,DEFAULT CHARSET='utf8';
  
ALTER TABLE `operatorforms` CHANGE `formName` `formName` varchar(100) COLLATE utf8_general_ci,
CHANGE `file` `file` varchar(100) COLLATE utf8_general_ci,DEFAULT CHARSET='utf8';
  
ALTER TABLE `operators` DEFAULT CHARSET='utf8';

ALTER TABLE `pqfdata` CHANGE `answer` `answer` text COLLATE utf8_general_ci,
CHANGE `comment` `comment` text COLLATE utf8_general_ci,DEFAULT CHARSET='utf8';
  
ALTER TABLE `pqfdata_employees` CHANGE `answer` `answer` varchar(100) COLLATE utf8_general_ci,DEFAULT CHARSET='utf8';
  
ALTER TABLE `pqfdata_hist` CHANGE `answer` `answer` text COLLATE utf8_general_ci,
CHANGE `comment` `comment` text COLLATE utf8_general_ci,DEFAULT CHARSET='utf8';
  
ALTER TABLE `pqfoptions` CHANGE `optionName` `optionName` varchar(200) COLLATE utf8_general_ci,DEFAULT CHARSET='utf8';
  
ALTER TABLE `ref_country` DEFAULT CHARSET='utf8';

ALTER TABLE `token` DEFAULT CHARSET='utf8';

ALTER TABLE `rpt_dashboard_widget` CHANGE `url` `url` varchar(255) COLLATE utf8_general_ci,
CHANGE `parameters` `parameters` text COLLATE utf8_general_ci;
  
ALTER TABLE `users` CHANGE `username` `username` varchar(100) COLLATE utf8_general_ci,
CHANGE `password` `password` varchar(100) COLLATE utf8_general_ci,
CHANGE `email` `email` varchar(100) COLLATE utf8_general_ci,
CHANGE `name` `name` varchar(100) COLLATE utf8_general_ci,
CHANGE `department` `department` varchar(100) COLLATE utf8_general_ci,DEFAULT CHARSET='utf8';
  
ALTER TABLE `widget` CHANGE `caption` `caption` varchar(50) COLLATE utf8_general_ci,DEFAULT CHARSET='utf8';