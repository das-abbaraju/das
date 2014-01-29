--liquibase formatted sql

--changeset pschlesinger:14
update accounts set countrySubdivision = null where country not in ('US','CA','AU');
