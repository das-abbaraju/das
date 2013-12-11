--liquibase formatted sql

--changeset mdo:3
--preConditions onFail MARK_RAN
ALTER TABLE `pics_alpha1`.`contractor_info`
  CHANGE `europeanUnionVATnumber` `europeanUnionVATnumber` VARCHAR(30) CHARSET latin1 COLLATE latin1_swedish_ci NULL;
