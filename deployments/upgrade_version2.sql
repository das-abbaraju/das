/*
 * Added a new column to widget table for Delinquent Accounts
 */

insert into widget values 
(15,'Delinquent Contractor Accounts','Html',0,'DelinquentAccountsAjax.action',null,null);

insert into widget_user values
(31,15,941,1,1,40,null);

/*
 * Added the widget for Delinquent Accounts for operators
 */
insert into widget_user values
(32,15,616,1,1,40,null);