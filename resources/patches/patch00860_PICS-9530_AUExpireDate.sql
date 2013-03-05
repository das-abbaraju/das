-- update 2009 AU
Update contractor_audit ca
set ca.expiresDate = '2013-03-15 23:59:59'
where
  ca.auditTypeID=11
  and ca.auditFor='2009'
  and ca.expiresDate > '2013-02-27 23:59:59'
  and ca.expiresDate < '2013-03-04 00:00:00';

-- update 2010 AU
Update contractor_audit ca
set ca.expiresDate = '2014-03-15 23:59:59'
where
  ca.auditTypeID=11
  and ca.auditFor='2010';

-- update 2011 AU
Update contractor_audit ca
set ca.expiresDate = '2015-03-15 23:59:59'
where
  ca.auditTypeID=11
  and ca.auditFor='2011';

-- update 2012 AU
Update contractor_audit ca
set ca.expiresDate = '2016-03-15 23:59:59'
where
  ca.auditTypeID=11
  and ca.auditFor='2012';


