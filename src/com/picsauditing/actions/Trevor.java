package com.picsauditing.actions;

import java.sql.SQLException;
import java.util.List;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Anonymous;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;

@SuppressWarnings("serial")
public class Trevor extends PicsActionSupport {
	@Autowired
	ContractorAccountDAO contractorAccountDAO;

	@Anonymous
	public String execute() throws SQLException {
		Ehcache ehCache = CacheManager.getInstance().getEhcache("temp");
		
		// Clearing Cache Initially
		for (Object key : ehCache.getKeys()) {
			ehCache.remove(key);
		}
		
		System.out.print("\n In memory size:" + ehCache.calculateInMemorySize());

		System.out.print("\n\n Load up Ancon Marine");
		ContractorAccount ancon = contractorAccountDAO.findConID("Ancon Marine");
		AccountStatus status = ancon.getStatus();
		status.toString();
		System.out.print("\n In memory size:" + ehCache.calculateInMemorySize());
		System.out.print("\n Memory Store Size:" + ehCache.getMemoryStoreSize()+"\n");
		for (Object o : ehCache.getKeys()) {
			System.out.println(o.toString());
		}

		System.out.print("\n\n Get Contractor Audits");
		for (ContractorAudit audit : ancon.getAudits()) {
			List<AuditCatData> data = audit.getCategories();
			//System.out.println("\n"+data.toString());
		}
		System.out.print("\n In memory size:" + ehCache.calculateInMemorySize());
		System.out.print("\n Memory Store Size:" + ehCache.getMemoryStoreSize()+"\n");
		for (Object o : ehCache.getKeys()) {
			System.out.println("\n Size:"+ehCache.get(o).getSerializedSize());
			System.out.println(o.toString());
		}

//		System.out.print("\n\n Get other contractors");
//		List<ContractorAccount> otherContractors = contractorAccountDAO.findWhere("name LIKE '1%'");
//		System.out.print("\n In memory size:" + ehCache.calculateInMemorySize());
//		System.out.print("\n Memory Store Size:" + ehCache.getMemoryStoreSize());
//		List<Object> finalMap = ehCache.getKeys();
//		for (Object o : finalMap) {
//			System.out.println(o.toString());
//		}

		return SUCCESS;
	}

}
