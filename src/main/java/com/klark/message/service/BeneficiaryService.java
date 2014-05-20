package com.klark.message.service;

import java.util.List;

import com.klark.user.model.Beneficiary;
import com.klark.user.model.BeneficiaryDTO;
import com.klark.user.model.User;

public interface BeneficiaryService {

    Beneficiary addBeneficiary(Beneficiary Beneficiary);

    Beneficiary getBeneficiaryById(Long beneficiaryId);

    List<BeneficiaryDTO> getBeneficiariesByUserId(User user);

    List<Beneficiary> getBeneficiariesByMessageId(Long messageId);

    int countBeneficiaries(Long userId);
}
