/*
 * Inserted a new row for Corporate Statistics widget, RecentlyLogged In Contractors and Operators for Admin.
 */
insert into widget values
(12,'Corporate Statistics','Html',0,'CorporateStatisticsAjax.action',null,null)
(13,'Recently Logged In Contractors','Html',0,'ContractorsLoggedAjax.action',null,null)
(14,'Recently Logged In Operators','Html',0,'OperatorsLoggedAjax.action',null,null);

insert into widget_user values 
(28,12,941,1,2,10,null)
(29,13,941,1,2,10,null)
(30,14,941,1,2,10,null);
