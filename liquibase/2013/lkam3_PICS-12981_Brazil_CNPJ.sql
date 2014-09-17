--liquibase formatted sql

--changeset lkam:3
--preConditions onFail MARK_RAN
ALTER TABLE contractor_info
  CHANGE europeanUnionVATnumber europeanUnionVATnumber VARCHAR(30) CHARSET latin1 COLLATE latin1_swedish_ci NULL;
