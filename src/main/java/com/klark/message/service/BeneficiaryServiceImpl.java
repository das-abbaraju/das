package com.klark.message.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.klark.exception.NotFoundException;
import com.klark.message.dao.BeneficiaryDAO;
import com.klark.user.model.Beneficiary;
import com.klark.user.model.BeneficiaryDTO;
import com.klark.user.model.User;

@Service
public class BeneficiaryServiceImpl extends BaseEntityServiceImpl<Beneficiary, BeneficiaryDTO> implements BeneficiaryService {

    @Autowired
    private BeneficiaryDAO beneficiaryDAO;

    @Transactional
    public Beneficiary addBeneficiary(Beneficiary beneficiary) {
        return beneficiaryDAO.addBeneficiary(beneficiary);
    }

    @Transactional
    public Beneficiary getBeneficiaryById(Long beneficiaryId) {
        return beneficiaryDAO.getBeneficiaryById(beneficiaryId);
    }

    @Transactional
    public List<BeneficiaryDTO> getBeneficiariesByUserId(User user) {
        List<Beneficiary> beneficiaries = beneficiaryDAO.getBeneficiariesByUserId(user);
        List<BeneficiaryDTO> dtos = new ArrayList<BeneficiaryDTO>();
        for (Beneficiary beneficiary : beneficiaries) {
            dtos.add(toDTO(beneficiary, true));
        }
        return dtos;
    }

    @Transactional
    public Beneficiary getBeneficiariesById(Long beneficiaryId) {
        return beneficiaryDAO.getBeneficiaryById(beneficiaryId);
    }

    @Transactional
    public List<Beneficiary> getBeneficiariesByMessageId(Long messageId) {
        return beneficiaryDAO.getBeneficiariesByMessageId(messageId);
    }

    @Transactional
    public int countBeneficiaries(Long userId) {
        return 0;// TODO
    }

    @Transactional
    public void deleteById(Long id) throws NotFoundException {
        // TODO Auto-generated method stub

    }

    public void clearDtoCache() {
        // TODO Auto-generated method stub

    }

}
