package com.picsauditing.coverage;

import com.picsauditing.dao.ContractorTagDAO;
import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.ContractorTag;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ContractorTagger {
    @Autowired
    private ContractorTagDAO contractorTagDAO;

    public List<ContractorTag> getTagsByTagID(int tagID) {
        return contractorTagDAO.getTagsByTagID(tagID);
    }

    public void remove(int id) {
        contractorTagDAO.remove(id);
    }

    public BaseTable save(BaseTable o) {
        return contractorTagDAO.save(o);
    }
}
