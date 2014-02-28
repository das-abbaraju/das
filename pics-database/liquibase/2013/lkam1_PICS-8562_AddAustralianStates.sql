--liquibase formatted sql

--changeset lkam:1
INSERT IGNORE INTO ref_country_subdivision (isoCode,countryCode,english)
VALUES 
('AU-ACT', 'AU', NULL),
('AU-NSW', 'AU', NULL),
('AU-NT', 'AU', NULL),
('AU-QLD', 'AU', NULL),
('AU-SA', 'AU', NULL),
('AU-TAS', 'AU', NULL),
('AU-VIC', 'AU', NULL),
('AU-WA', 'AU', NULL);

INSERT IGNORE INTO app_translation
            (msgKey,
             locale,
             msgValue,
             createdBy,
             updatedBy,
             creationDate,
             updateDate,
             qualityRating,
             applicable,
             sourceLanguage)
VALUES
('CountrySubdivision.AU-ACT', 'en', 'Australian Capital Territory', 83941, 83941, NOW(), NOW(), 2, 1, 'en'),
('CountrySubdivision.AU-NSW', 'en', 'New South Wales', 83941, 83941, NOW(), NOW(), 2, 1, 'en'),
('CountrySubdivision.AU-NT', 'en', ' Northern Territory', 83941, 83941, NOW(), NOW(), 2, 1, 'en'),
('CountrySubdivision.AU-QLD', 'en', 'Queensland', 83941, 83941, NOW(), NOW(), 2, 1, 'en'),
('CountrySubdivision.AU-SA', 'en', 'South Australia', 83941, 83941, NOW(), NOW(), 2, 1, 'en'),
('CountrySubdivision.AU-TAS', 'en', 'Tasmania', 83941, 83941, NOW(), NOW(), 2, 1, 'en'),
('CountrySubdivision.AU-VIC', 'en', 'Victoria', 83941, 83941, NOW(), NOW(), 2, 1, 'en'),
('CountrySubdivision.AU-WA', 'en', 'Western Australia', 83941, 83941, NOW(), NOW(), 2, 1, 'en'),
('Country.AU.SubdivisionLabel', 'en', 'State', 83941, 83941, NOW(), NOW(), 2, 1, 'en');