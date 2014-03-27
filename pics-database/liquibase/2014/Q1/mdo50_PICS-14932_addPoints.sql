--liquibase formatted sql

--changeset mdo:50
ALTER TABLE payment_operator_commission
  ADD COLUMN points DECIMAL(11,7) NOT NULL AFTER paymentAmount;
