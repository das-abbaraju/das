package com.picsauditing.struts.validator.constraints;

import com.picsauditing.dao.ContractorAccountDAO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ContractorNameNotTakenConstraintValidator implements ConstraintValidator<UniqueContractorName, String> {

    @Autowired
    private ContractorAccountDAO dao;

    @Override
    public void initialize(UniqueContractorName uniqueContractorName) {

    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return CollectionUtils.isEmpty(dao.findByCompanyName(s));
    }
}
