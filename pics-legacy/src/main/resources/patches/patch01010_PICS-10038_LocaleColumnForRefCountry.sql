ALTER TABLE ref_country
ADD COLUMN locale CHAR(5);

UPDATE ref_country
SET locale = "fr_FR"
WHERE isoCode = "FR";
UPDATE ref_country
SET locale = "de_DE"
WHERE isoCode = "DE";
UPDATE ref_country
SET locale = "en_US"
WHERE isoCode = "US";
UPDATE ref_country
SET locale = "en_UK"
WHERE isoCode = "GB";
UPDATE ref_country
SET locale = "en_CA"
WHERE isoCode = "CA";
UPDATE ref_country
SET locale = "es_ES"
WHERE isoCode = "ES";




