-- PICS-1797 - Use a better label for annual stats
update flag_criteria set label = REPLACE(label, right(label, 3), '') where label like '% \'%';