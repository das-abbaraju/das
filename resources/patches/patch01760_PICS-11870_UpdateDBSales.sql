UPDATE ref_country
SET salesPhone = '+1-877-725-3022';

UPDATE ref_country
SET salesPhone = '1-877-725-3022'
where isoCode='US' or isoCode='CA';