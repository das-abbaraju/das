package com.picsauditing.actions.dev;

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import com.picsauditing.PICS.DBBean;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.util.FileUtils;

public class OshaFileConversionAjax extends PicsActionSupport {

	private static final String SCRIPT_FILE_NAME = "c:\\temp\\osha_crap.sh";

	private static final long serialVersionUID = 753348519512350112L;

	@Override
	public String execute() throws Exception {
		String sql = "select oa.id,pd.id from osha_audit oa\n" + " join contractor_audit ca on oa.auditID = ca.id\n"
				+ " join pqfData pd on ca.id = pd.auditID and pd.questionID = 8811" + " limit 2000,10";

		// SelectSQL selectSQL = new SelectSQL("app_translation");
		// selectSQL.addField("msgKey");
		// selectSQL.addField("msgValue");
		// selectSQL.addWhere(buildWhereClause());

		Connection conn = DBBean.getDBConnection();
		ResultSet results = conn.createStatement().executeQuery(sql);

		Map<Integer, Integer> pairings = populatePairings(results);
		String scriptTemplate = "\r\n# osha_${fromID} -> data_${toID}\r\n" +
				"files=`ls /var/pics/www_files/files/${sourceHashFolder}osha_${fromID}.*`\r\n" + 
				"for i in $files; do \r\n" + 
				"	ext=`echo $i|awk -F . '{print $NF}'`\r\n" + 
				"	echo 'update pqfdata set answer = \"${ext}\" where id = ${toID};' >> pics-3156.sql\r\n" +
				"	mkdir -p /var/pics/www_files/files/${destinationHashFolder}\r\n" + 
				"	mv $i /var/pics/www_files/files/${destinationHashFolder}data_${toID}.${ext}\r\n" + 
				"done\r\n";
		String script = FileUtils.massManipulateScript(pairings, scriptTemplate);
		FileWriter fw = new FileWriter(new File(SCRIPT_FILE_NAME));
		fw.write("echo '-- Update Script for PICS-3156 (OSHA Rewrite)' > pics-3156.sql");
		fw.write(script);
		fw.close();
		this.json.put("filename", SCRIPT_FILE_NAME);
		this.json.put("count", pairings.size());
		return JSON;

	}

	@SuppressWarnings("unchecked")
	private Map<Integer, Integer> populatePairings(ResultSet results) throws Exception {
		Map<Integer, Integer> pairings = new HashMap<Integer, Integer>();
		while (results.next()) {
			pairings.put(results.getInt(1), results.getInt(2));
		}
		return pairings;
	}
}
