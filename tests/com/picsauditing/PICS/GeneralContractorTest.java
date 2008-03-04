package com.picsauditing.PICS;

import junit.framework.TestCase;

import com.picsauditing.PICS.AccountBean;
import com.picsauditing.PICS.EmailBean;
import com.picsauditing.PICS.GeneralContractor;

public class GeneralContractorTest extends TestCase {
	public void testSave() {
		try {
			GeneralContractor gcBean = new GeneralContractor();
			gcBean.setConn(DefaultDatabase.getConnection());
			gcBean.setConID(123);
			gcBean.setOpID(123);
			gcBean.save();
			gcBean.setWorkStatus("Y");
			gcBean.setConn(DefaultDatabase.getConnection());
			gcBean.save();
			gcBean.setConn(DefaultDatabase.getConnection());
			gcBean.delete("123", "123");
		} catch (Exception e) {
			fail("Exception thrown: "+e.getMessage());
		}
    }
}
