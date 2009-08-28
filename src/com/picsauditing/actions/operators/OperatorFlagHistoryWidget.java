package com.picsauditing.actions.operators;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;

@SuppressWarnings("serial")
public class OperatorFlagHistoryWidget extends PicsActionSupport {

	public List<BasicDynaBean> getFlagSummary() {
		SelectSQL sql = new SelectSQL("flags f");
		sql.addJoin("JOIN flag_archive fa ON fa.opID = f.opID AND fa.conID = f.conID");
		sql.addJoin("JOIN accounts a ON f.opID = a.id");
		sql.addWhere("f.flag <> fa.flag");
		
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd");
		String today = dbFormat.format(cal.getTime());
		sql.addWhere("fa.creationDate = '" + today + "'");
		
		sql.addField("a.id");
		sql.addGroupBy("a.id");
		sql.addField("a.name");
		sql.addField("count(*) flagChanges");
		sql.addOrderBy("flagChanges DESC");

		sql.setLimit(10);

		try {
			Database db = new Database();
			List<BasicDynaBean> pageData = db.select(sql.toString(), false);
			return pageData;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
