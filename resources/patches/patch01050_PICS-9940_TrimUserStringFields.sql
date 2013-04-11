UPDATE users u 
set u.firstName = trim(u.firstName),
u.lastName = trim(u.lastName),
u.phone = trim(u.phone),
u.fax = trim(u.fax),
u.department = trim(u.department),
u.email = trim(u.email),
u.username = trim(u.username);