--liquibase formatted sql

--changeset pschlesinger:14
update accounts set countrySubdivsion = null where country not in ('US','CA','AU');
