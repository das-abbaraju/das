--liquibase formatted sql

--changeset kchase:34

-- clean up bad email_subscriptions without user
CREATE temporary table myBadSubs as
select s.id from
email_subscription s
left join users u on u.id=s.userid
where u.id is null;

delete email_subscription from email_subscription
join myBadSubs on email_subscription.id=myBadSubs.id;

-- clean up duplicate email subscriptions
CREATE temporary table myDeletes as
select s.id from email_subscription s
join email_subscription s2 on s2.userId=s.userId and s2.subscription= s.subscription and s2.id < s.id
where s.subscription <> 'DynamicReports';

delete email_subscription from email_subscription
join myDeletes on email_subscription.id=myDeletes.id;

-- Update email subscription report id to dummy report
update 
email_subscription
set reportID=2209
where subscription<>'DynamicReports';