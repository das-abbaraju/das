import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.ContractorInfoDAO;
import com.picsauditing.dao.ContractorInfoReportDAO;
import com.picsauditing.dao.DAOFactory;
import com.picsauditing.dao.OshaLogDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorInfo;
import com.picsauditing.jpa.entities.ContractorInfoReport;
import com.picsauditing.jpa.entities.OshaLog;
import com.picsauditing.jpa.entities.OshaLogReport;
import com.picsauditing.jpa.entities.PqfLog;
import com.picsauditing.jpa.entities.PqfLogReport;


public class AccountTests extends TestCase {

	protected void setUp() throws Exception {
	}

	protected void tearDown() throws Exception {
	}
	
	protected List<ContractorInfo> sortList(List<ContractorInfo> list) {
		  List<ContractorInfo> out = new ArrayList<ContractorInfo>();
		  out.addAll(list);
		  Collections.sort(out,  ASC_NAME_COMPARATOR);
		  return out;
	}	
	
	 
	 private static final Comparator<ContractorInfo> ASC_NAME_COMPARATOR = new Comparator<ContractorInfo>( ) {
	        public int compare(ContractorInfo a1, ContractorInfo a2) {
	            String s1 = a1.getAccount().getName();
	            String s2 = a2.getAccount().getName();
	            return s1.compareTo(s2);
	        }
	    };
	    
	   
	    public void testContractorInfoReports(){
			DAOFactory daof = DAOFactory.instance(DAOFactory.JPA, "PicsDBJPA");
			ContractorInfoReportDAO dao = daof.getContractorInfoReportDAO();
			dao.setMax(50);				
			
			List<ContractorInfoReport> reports = dao.findAll();
						
			int i = 0;
			
			
			System.out.println(reports.size());	
			
		}
	    
	    

	
}
