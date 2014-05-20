package com.klark.message.dao;

import java.util.List;

import com.klark.user.model.Beneficiary;
import com.klark.user.model.User;

public interface BeneficiaryDAO {

    Beneficiary addBeneficiary(Beneficiary Beneficiary);

    Beneficiary getBeneficiaryById(Long beneficiaryId);

    List<Beneficiary> getBeneficiariesByUserId(User user);

    List<Beneficiary> getBeneficiariesByBenificiaryId(Long beneficiaryId);

    List<Beneficiary> getBeneficiariesByMessageId(Long messageId);

    int countBeneficiaries(Long userId);
}
