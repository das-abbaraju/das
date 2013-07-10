-- open LC-COR to just Alberta contractors
UPDATE app_properties ap
SET ap.value='contractor.countrySubdivision.toString().equals("CA-AB")'
WHERE ap.property='Toggle.LcCor_v2';