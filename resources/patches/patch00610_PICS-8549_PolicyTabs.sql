-- update effective dates of policies to answer given
update contractor_audit ca
join pqfdata pd on pd.auditID = ca.id
set ca.effectiveDate = ca.creationDate
where STRCMP('', pd.answer) != 0
and ca.expiresDate >= NOW()
and (ca.effectiveDate is null or ca.effectiveDate < ca.creationDate)
and pd.questionID in(2081, 2104, 2110, 2116, 2122, 2128, 2134, 2140, 2146, 2286, 
2391, 2475, 3023, 3465, 5156, 6683, 6680, 12156, 10226, 10342, 
10495, 10496, 10494, 10952, 11919, 11929, 11937, 12272, 13018, 13061, 
14418, 14130, 14314, 14320, 14334, 14338, 14838, 14843, 15063, 15067);