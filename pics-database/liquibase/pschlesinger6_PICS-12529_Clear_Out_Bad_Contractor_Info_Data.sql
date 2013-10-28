--liquibase formatted sql

--changeset pschlesinger:6
update
    contractor_info
join
  accounts
on
  contractor_info.id = accounts.id
set
  billingAddress = null,
  billingCity = null,
  billingCountrySubdivision = null,
  billingCountry = null,
  billingZip = null
where
  accounts.status != 'Demo'
and
      country != billingCountry
and
      billingCountry is not null
and
(billingAddress is null or billingAddress = '');

