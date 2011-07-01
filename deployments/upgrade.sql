-- PICS-2636
update accounts a
join contractor_info c on a.id = c.id
set c.renew = 0
where a.status = 'Deactivated' and c.renew = 1 and a.updateDate > '2011-06-01';
--
-- PICS-2645
update contractor_info set productRisk=0 where productRisk is NULL;
update contractor_info set safetyRisk=0 where safetyRisk is NULL;
--
-- PICS-2667
insert into invoice_fee (`id`, `fee`, `defaultAmount`, `visible`, `feeClass`, `minFacilities`, `maxFacilities`, `qbFullName`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`, `displayOrder`) 
values(342,'Suncor Early Registration Discount',0.00,1,'SuncorDiscount',1,10000,'SUNCORDISCOUNT',20952,20952,NOW(),NOW(),'43');
--
-- PICS-2480
update accounts a
set timezone = (
select distinct u.timezone 
from users u
where u.accountId = a.id
and a.timezone is null
and 1 = (select count(distinct u2.timezone) from users u2 where a.id = u2.accountId));

update accounts
set timezone = 'US/Pacific'
where timezone is null
and type = 'Assessment';

update accounts
set timezone = 'US/Pacific',
state = 'CA',
city = 'Irvine',
address = '17701 Cowan',
address2 = 'Suite 140',
zip = '92614'
where timezone is null
and name like 'PICS PSM';

update accounts
set timezone = 'US/Eastern',
state = 'PA',
city = 'Valley Forge',
address = '750 E. Swedesford Rd',
zip = '19482'
where timezone is null
and country in ('US','CA')
and (name like 'Saint-Gobain%'
or name like 'SGNA:%'
or name like 'Construction Materials'
or name like 'Verallia'
or name like 'Innovative Materials'
or name like 'Western Mining and Minerals%'
or name like 'CertainTeed%');

update accounts
set timezone = 'US/Eastern',
state = 'OH',
country = 'US'
where timezone is null
and name = 'Food Control Solutions, Inc';

update accounts
set timezone = 'US/Eastern',
state = 'KY',
city = 'Florence'
where timezone is null
and name = 'L''Oreal USA - Florence, KY';

update accounts
set timezone = 'US/Pacific',
country = 'US'
where timezone is null
and state = 'CA'
and city = 'Irvine';

update accounts
set timezone = 'Atlantic/Bermuda',
country = 'US',
state = 'PR'
where timezone is null
and country in ('US','AF')
and state in ('PR')
or (country in ('PR') and city = 'San Juan');

update accounts
set timezone = 'US/Eastern'
where timezone is null
and country = 'US'
and state in ('CT','DE','GA','ME','MD','MA','NH','NJ','NY','NC',
'OH','PA','RI','SC','VT','VA','WV','WI','FL','MI','IN','KY')
OR (state in ('TN') and city not in ('Memphis'));

update accounts
set timezone = 'US/Central'
where timezone is null
and country = 'US'
and state in ('AL','AR','IL','IA','LA','MN','MS','MO','OK','TX','KS')
or (state in ('TN') and city in ('Memphis'))
or (state in ('NE') and city in ('Sidney'));

update accounts
set timezone = 'US/Mountain'
where timezone is null
and country = 'US'
and (state in ('CO','MT','NM','UT','WY','ID','ND', 'SD'))
or (state in ('ID') and city not in ('Priest River','Coeur d''Alene'))
or (state in ('NE') and city in ('Omaha'));

update accounts
set timezone = 'US/Pacific'
where timezone is null
and country = 'US'
and state in ('CA','NV','WA','OR')
or (state in ('ID') and city in ('Priest River','Coeur d''Alene'));

update accounts
set timezone = 'US/Alaska'
where timezone is null
and country = 'US'
and state = 'AK';

update accounts
set timezone = 'Pacific/Honolulu'
where timezone is null
and country = 'US'
and state = 'HI';

update accounts
set timezone = 'US/Arizona'
where timezone is null
and country = 'US'
and state = 'AZ';

update accounts
set timezone = 'Atlantic/Bermuda'
where timezone is null
and country = 'CA'
and state in ('NB','PE');

update accounts
set timezone = 'US/Eastern'
where timezone is null
and country = 'CA'
and state in ('ON','QC');

update accounts
set timezone = 'US/Central'
where timezone is null
and country = 'CA'
and state = 'MB';

update accounts
set timezone = 'US/Mountain'
where timezone is null
and country = 'CA'
and state = 'AB';

update accounts
set timezone = 'US/Pacific'
where timezone is null
and country = 'CA'
and state = 'BC';

update accounts
set timezone = 'America/Sao_Paulo'
where timezone is null
and country = 'BR'
and city = 'Sao Paulo';

update accounts
set timezone = 'Europe/London'
where timezone is null
and country = 'GB'
and city in ('Cheshire','London');
--

-- We need to rerun the Indexer because of PICS-2723 Trade Search Filter Doesn't Like Symbols
-- PICS-2768
update email_template set allowsVelocity = '1' WHERE id = '155';
