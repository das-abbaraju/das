package com.picsauditing.rules;

import java.util.List;
import javax.servlet.http.HttpSession;

public class RulesFactory {
	/*
	public static RulesEngine getRulesEngine(String engineName) throws Exception {
		if (engineName == null) return new RulesEngine();
		
		RulesRowDAO dao = new RulesRowDAO();
		List<RulesRowBean> rowList = dao.getRowsByTable(engineName);
		if (engineName.equals("pricing")) {
			BillingEngine engine = new BillingEngine();
			engine.setUp(rowList);
			return engine;
		}
		return new RulesEngine();
	}

	public static RulesEngine getRulesEngine(HttpSession session, String engineName) throws Exception {
		RulesEngine engine = (RulesEngine)session.getAttribute("ENGINE_"+engineName);
		if (engine == null) engine = RulesFactory.getRulesEngine(engineName);
		return engine;
	}

	public static void setRulesEngine(HttpSession session, String engineName, RulesEngine engine) {
		session.setAttribute("ENGINE_"+engineName, engine);
	}
	*/
}
