update note n
join accounts a on a.id = n.accountID
set n.summary = 'This account has been set to declined because this account has been pending for over 90 days without completion of registration.'
where n.summary = 'Account has been deactivated, this account has been pending for 90 days without payment.'
and a.status = 'Deactivated'
and a.reason = 'Deactivated pending account';

update accounts
set reason = 'Did not Complete PICS process', status = 'Declined'
where reason = 'Deactivated pending account'
and status = 'Deactivated';