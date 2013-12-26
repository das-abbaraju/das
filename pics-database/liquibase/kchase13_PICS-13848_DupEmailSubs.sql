--liquibase formatted sql

--changeset kchase:13
-- delete duplicate subscriptions
delete from email_subscription
where id in (242, 352, 247, 381, 375, 427, 425, 429, 441, 407,
408, 36334, 279, 154498, 411, 412, 417, 229, 231, 1264,
62708, 70859,  139349, 36484, 154636, 2972, 103670, 147447, 147444, 147446, 147443, 147445, 145758);

-- update specified subscriptions
Update email_subscription
set timePeriod='None'
where id in (146013, 146014);
