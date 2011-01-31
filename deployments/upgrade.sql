-- PICS-1797 - Use a better label for annual stats
update flag_criteria set label = REPLACE(label, right(label, 3), '') where label like '% \'%';

update accounts set industry = 'WoodProducts'
where name like 'Roseburg Forest Products%'
and type in ('Operator','Corporate');