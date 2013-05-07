SET GLOBAL 	innodb_large_prefix	= 'ON'
;

SET GLOBAL 	expand_fast_index_creation	= 'ON'
;

SET GLOBAL 	innodb_file_format	= 'Barracuda'
;

SET SESSION 	identity	= 1
;


SHOW VARIABLES
;

SELECT
	@@global.innodb_large_prefix
;

