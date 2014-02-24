--liquibase formatted sql

--changeset mdo:27
--preConditions onFail MARK_RAN
--onUpdateSQL IGNORE
ALTER TABLE `contractor_operator_number`
  ADD  INDEX `conOp` (`conID`, `opID`);
