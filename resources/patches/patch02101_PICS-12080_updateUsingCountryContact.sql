UPDATE IGNORE ref_country rc
JOIN country_contact cc ON rc.isoCode = cc.country
SET rc.id = cc.id, 
    rc.createdBy = cc.createdBy, 
    rc.creationDate = cc.creationDate,
    rc.updatedBy = cc.updatedBy,
    rc.updateDate = cc.updateDate,
    rc.csrPhone = cc.csrPhone,
    rc.csrFax = cc.csrFax,
    rc.csrEmail = cc.csrEmail,
    rc.csrAddress = cc.csrAddress,
    rc.csrCity = cc.csrCity,
    rc.csrCountrySubdivision = cc.csrCountrySubdivision,
    rc.csrZip = cc.csrZip,
    rc.isrPhone = cc.isrPhone,
    rc.isrFax = cc.isrFax,
    rc.isrEmail = cc.isrEmail,
    rc.isrAddress = cc.isrAddress,
    rc.isrCity = cc.isrCity,
    rc.isrCountrySubdivision = cc.isrCountrySubdivision,
    rc.isrZip = cc.isrZip,
    rc.businessUnitID = cc.businessUnitID;
