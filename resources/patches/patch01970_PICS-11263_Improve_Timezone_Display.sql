CREATE TABLE IF NOT EXISTS ref_time_zone (
  id int(11) NOT NULL AUTO_INCREMENT,
  countryCode varchar(2) COLLATE utf8_bin NOT NULL,
  zoneName varchar(200) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (id)
);

ALTER TABLE ref_time_zone
  ADD  UNIQUE INDEX ak_ref_time_zone (countryCode, zoneName);