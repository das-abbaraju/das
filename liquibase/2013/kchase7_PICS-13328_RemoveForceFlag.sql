--liquibase formatted sql

--changeset kchase:7
Update generalcontractors
set forceFlag=NULL, forceBegin=NULL, forceEnd=NULL, forcedBy=NULL
where subID=15175 and genID=14567;