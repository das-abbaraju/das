/**
update pqfquestions set isVisible = CASE isVisible WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set hasRequirement = CASE hasRequirement WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isGroupedWithPrevious = CASE isGroupedWithPrevious WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isRedFlagQuestion = CASE isRedFlagQuestion WHEN 2 THEN 1 ELSE 0 END;
*/


/**
* adding the contractor count flag for corporate users
*/
insert into widget_user
values (null,1,646,1,1,10,null)

/**
 * add a new widget for Contractor Cron Statistics for admins  
 */
insert into widget values 
(null,"Contractor Cron Statistics","Html",0,"ContractorCronStatisticsAjax.action","DevelopmentEnvironment",null);

insert into widget_user
values (null,16,941,1,2,40,null);

/**
update Delinquent Contractor Accounts permisission on widget 
**/
update widget set requiredPermission = "DelinquentAccounts"
where widgetID = 15;

