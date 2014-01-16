package com.picsauditing.service;

import com.picsauditing.access.Permissions;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.ContractorTagDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.model.user.UserManagementService;
import com.picsauditing.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class RequestNewContractorService {
    @Autowired
    private ContractorAccountDAO contractorAccountDAO;
    @Autowired
    private ContractorOperatorDAO contractorOperatorDAO;
    @Autowired
    protected UserDAO userDAO;
    @Autowired
    private UserManagementService userManagementService;
    @Autowired
    private ContractorTagDAO contractorTagDAO;

    Permissions permissions = null;

    public ContractorOperator saveRelationship(ContractorAccount contractor, ContractorOperator requestRelationship) throws Exception {
        if (requestRelationship.getId() == 0) {
            requestRelationship.setFlagColor(FlagColor.Clear);
        }

        requestRelationship.setContractorAccount(contractor);
        requestRelationship.setAuditColumns(permissions);
        requestRelationship= (ContractorOperator) contractorOperatorDAO.save(requestRelationship);

        return requestRelationship;
    }

    public User savePrimaryContact(ContractorAccount contractor, User primaryContact) throws Exception {
        // Username and isGroup is required
        if (primaryContact.getId() == 0) {
            primaryContact.setAccount(contractor);
            primaryContact.setIsGroup(YesNo.No);

            boolean usernameIsAlreadyTaken = userDAO.duplicateUsername(primaryContact.getEmail(), 0);
            if (usernameIsAlreadyTaken) {
                primaryContact.setUsername(String.format("%s-%d", primaryContact.getEmail(), contractor.getId()));
            } else {
                primaryContact.setUsername(primaryContact.getEmail());
            }
            primaryContact.setName(primaryContact.getFirstName() + " " + primaryContact.getLastName());

            primaryContact.setAuditColumns(permissions);

            contractor.setPrimaryContact(primaryContact);
            contractor.getUsers().add(primaryContact);
        }

        primaryContact.setPhoneIndex(Strings.stripPhoneNumber(primaryContact.getPhone()));
        primaryContact = userManagementService.saveWithAuditColumnsAndRefresh(primaryContact, permissions);
        return primaryContact;
    }

    public ContractorAccount saveRequestingContractor(ContractorAccount contractor, OperatorAccount requestingOperator) throws Exception {
        if (contractor.getId() == 0) {
            contractor.setNaics(new Naics());
            contractor.getNaics().setCode("0");
            contractor.setRequestedBy(requestingOperator);
            contractor.generateRegistrationHash();
            if (requestingOperator != null) {
                if (requestingOperator.getStatus().isActive()
                        && !"No".equals(requestingOperator.getDoContractorsPay())) {
                    contractor.setPayingFacilities(1);
                }
            }
        }

        contractor.setAuditColumns(permissions);
        contractor = (ContractorAccount) contractorAccountDAO.save(contractor);
        return contractor;
    }

    public void addTagsToContractor(ContractorAccount contractor, List<OperatorTag> tags) throws Exception {
        if (tags == null)
            return;
        for (OperatorTag tag:tags) {
            ContractorTag conTag = new ContractorTag();
            conTag.setContractor(contractor);
            conTag.setTag(tag);
            conTag.setAuditColumns(permissions);

            contractorTagDAO.save(conTag);

            contractor.getOperatorTags().add(conTag);
        }
        contractorAccountDAO.save(contractor);
    }

    public void setPermissions(Permissions permissions) {
        this.permissions = permissions;
    }
}
