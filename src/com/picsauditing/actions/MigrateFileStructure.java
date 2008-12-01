package com.picsauditing.actions;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;

import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.OshaAuditDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.util.FileUtils;

public class MigrateFileStructure extends PicsActionSupport {

	protected boolean migrateOshas = false;
	protected boolean migratePQFs = false;
	protected boolean realRun = false;

	protected OshaAuditDAO dao = null;

	protected StringBuffer report = new StringBuffer();


	protected int moveCount = 0;
	protected int attemptedCount = 0;
	
	
	public MigrateFileStructure(OshaAuditDAO dao) {
		super();
		this.dao = dao;
	}

	@Override
	public String execute() throws Exception {

		reportLine("Starting load...");

		if (migrateOshas) {
			reportLine("migrating oshas...");
			for (File f : getDirectoryChildren(getFtpDir() + "/files/oshas/")) {
				processOshaFile(f);
			}
		}

		if (migratePQFs) {
			reportLine("migrating pqfs...");
			for (File f : getDirectoryChildren(getFtpDir() + "/files/pqf/")) {
				processPQFFile(f);
			}
		}

		reportLine("load complete: " + moveCount + " files out of " + attemptedCount + " moved.");
		
		
		return SUCCESS;
	}

	protected void processOshaFile(File file) {
		String fileName = file.getName();

		reportLine("processing osha file: " + file.getName());

		attemptedCount++;
		
		if (fileName.startsWith("osha") && fileName.indexOf("_") != -1
				&& fileName.indexOf("_") != 4) {

			int pos = fileName.indexOf("_");

			if (pos != -1) {
				String yearNum = fileName.substring(4, pos);
				String originalOshaId = fileName.substring(pos + 1, fileName
						.indexOf(".", pos + 1));

				String extension = fileName
						.substring(fileName.lastIndexOf(".") + 1);

				try {
					int yearDiff = Integer.parseInt(yearNum);
					int oshaYear = 2008 - yearDiff;
					int oshaId = Integer.parseInt(originalOshaId); // don't
																	// think we
																	// even need
																	// this

					// grab the osha for the oshaid, look up the audit with type
					// = osha and auditfor = same year, grab the osha audit with
					// that id
					Integer newAuditId = dao.findNewOshaAuditFromOld(oshaId,
							oshaYear).getId();

					if (realRun) {
						String path = "files/"
								+ FileUtils.thousandize(newAuditId);
						reportLine("moving to " + path + "/osha_" + newAuditId);
						FileUtils.moveFile(file, getFtpDir(), path, "osha_"
								+ newAuditId, extension, true);
						moveCount++;
					}

				} catch (Exception e) {
					
					if( ! ( e instanceof NoResultException ) )
					{
						e.printStackTrace();
						reportLine("\t" + e.getMessage());
					}
					else
					{
						reportLine("\tdata not found");
					}
					

				}
			} else {
				reportLine("\tno underscore found");
			}
		} else {
			reportLine("\tbad filename format");
		}
	}

	protected void processPQFFile(File file) {
		String fileName = file.getName();

		reportLine("processing pqf file: " + file.getName());

		if (file.isDirectory()) {
			for (File f123 : file.listFiles()) {
				processPQFFile(f123);
			}
		} else {
		
			attemptedCount++;
			
			int pos = fileName.indexOf("_");

			if (pos != -1) {
				String questionIdAsString = fileName.substring(0, pos);
				String contractorIdAsString = fileName.substring(pos + 1,
						fileName.indexOf(".", pos + 1));
				String extension = fileName
						.substring(fileName.lastIndexOf(".") + 1);

				try {
					int questionId = Integer.parseInt(questionIdAsString);
					int contractorId = Integer.parseInt(contractorIdAsString);
					int year = 0;

					// grab the osha for the oshaid, look up the audit with type
					// = osha and auditfor = same year, grab the osha audit with
					// that id
					
					switch( questionId )
					{
						case 872:
							year = 2005;
							break;
						case 1522:
							year = 2006;
							break;
						case 1618:
							year = 2007;
							break;
					}
					
					
					questionId = year > 0 ? 2037 : questionId;
					
					int dataId = 0;
					
					dataId = dao.findDataIdFromQuestionAndContractor(questionId, contractorId, year ).getDataID();
						
					if (realRun) {
						String path = "files/" + FileUtils.thousandize(dataId);
						reportLine("moving to " + path + "/data_" + dataId);
						FileUtils.moveFile(file, getFtpDir(), path, "data_"
								+ dataId, extension, true);
						moveCount++;
					}

				} catch (Exception e) {
					if( ! ( e instanceof NoResultException ) )
					{
						e.printStackTrace();
						reportLine("\t" + e.getMessage());
					}
					else
					{
						reportLine("\tdata not found");
					}

				}
			} else {
				reportLine("\tno underscore found");
			}
		}

	}

	protected File[] getDirectoryChildren(String parentPath) throws Exception {

		File parent = new File(parentPath);

		if (parent.isDirectory()) {
			return parent.listFiles();
		} else {
			throw new Exception("bad parent directory supplied: " + parentPath);
		}
	}

	public boolean isMigrateOshas() {
		return migrateOshas;
	}

	public void setMigrateOshas(boolean migrateOshas) {
		this.migrateOshas = migrateOshas;
	}

	public boolean isMigratePQFs() {
		return migratePQFs;
	}

	public void setMigratePQFs(boolean migratePQFs) {
		this.migratePQFs = migratePQFs;
	}

	public boolean isRealRun() {
		return realRun;
	}

	public void setRealRun(boolean realRun) {
		this.realRun = realRun;
	}

	public void reportLine(String line) {
		System.out.println(line);
		//report.append(line);
		//report.append('\n');
	}

}
