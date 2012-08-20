# SED script to convert NAICS TRIR data obtained from the U.S. Bureau of Labor Statistics 
# at http://www.bls.gov/iif/oshsum.htm into SQL statements
# 
# Craig Jones 2/28/2012
#
# To run this script, install SED.exe from http://gnuwin32.sourceforge.net/packages/sed.htm
# and invoke it like this:
#
#       sed -r -f NAICS_DATA.sed 2010NAICSStats.txt > 2010NAICSStats.sql
#
# IMPORTANT: The downloaded data file needs to be manually adjusted first.  There are a few rows that have a range of 
# 2-digit NAICS codes instead of just the one code.  (For example, "31-33" instead of just "31").  Those compound 
# records need to be converted into indivdual records, one for each code in the range, before this script is applied.



# Step 1. The s/.../.../ command below converts all data lines to SQL Insert statements
#
#  [^|]*                                                                                               Industry
#             ([0-9]+)                                                                              \1 NAICS Code
#                           [^ ]+                                                                      Annual avg empl
#                                      ([^ ]+)                                                      \2 TRIR (LWCR + restriction_transfer + other)
#                                                    ([^ ]+)                                        \3 DART Total (LWCR + restriction_transfer)
#                                                                ([^ ]+)                            \4 LWCR
#                                                                             ([^ ]+)               \5 restriction_transfer
#                                                                                          ([^ ]+)  \6 other
s/^[^|]* *\| +([0-9]+) +\| +[^ ]+ +\| +([^ ]+) +\| +([^ ]+) +\| +([^ ]+) +\| +([^ ]+) +\| +([^ ]+) +$/INSERT INTO `naics` (`code`,`trir`,`dart`,`lwcr`) VALUES (\1,\2,\3,\4);/gi

# Step 2.  This s command changes "-" values to 0.0
s/,-/,0.0/gi

# Step 2b.  This s command changes "(11)" values to 0.0 ("(11)" is a footnote that means "too small to display")
s/,\(11\)/,0.0/gi

# Step 3. These two d commands delete all other lines (lines that begin with something other than an I for INSERT, and lines that are completely empty)
/^[^I]/d
/./!d

# Step 4.  This i command inserts a header at the top.
1,1i\
-- TRIR data obtained from http://www.bls.gov/iif/oshsum.htm \
-- \
-- Notes: \
-- 1. Up until 3/2012, we had erroneously loaded "DART" data into the "LWCR" column.  This script properly assigns \
--    those values.  LWCR is supposed to correspond with just the "days away" value, but we previously loaded it with \
--    the sum of "days away" and "restriction or transfer" (i.e. DART).  \
-- 2. The original data contains some data values of "-".  They have been changed to 0.0. \
-- \
USE `database name here`; \
TRUNCATE `naics`; \
ALTER TABLE `naics` ADD COLUMN `dart` decimal(6,2) NULL AFTER `lwcr`;  \
INSERT INTO `naics` (`code`,`trir`,`dart`,`lwcr`) VALUES (0,2.0,1.5,1.5); \
\
