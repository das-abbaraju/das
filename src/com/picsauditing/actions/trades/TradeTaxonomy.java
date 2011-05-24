package com.picsauditing.actions.trades;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.apache.struts2.dispatcher.StreamResult;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Result;
import com.picsauditing.access.Anonymous;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.TradeAlternateDAO;
import com.picsauditing.dao.TradeDAO;
import com.picsauditing.jpa.entities.Trade;
import com.picsauditing.jpa.entities.TradeAlternate;
import com.picsauditing.search.Database;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.Tree;

@SuppressWarnings("serial")
public class TradeTaxonomy extends PicsActionSupport {

	@Autowired
	protected TradeDAO tradeDAO;
	@Autowired
	protected TradeAlternateDAO tradeAlternateDAO;
	private Trade trade;

	private List<Trade> trades;
	private String alternateName;
	private TradeAlternate alternate;

	private String q;

	/*
	 * Values used for the trade logo.
	 */
	private File tradeLogo;
	private String tradeLogoContentType = null;
	private String tradeLogoFileName = null;
	private String tradeLogoName = null;

	@SuppressWarnings("unchecked")
	public String json() {

		List<Trade> nodes = new ArrayList<Trade>();

		if (trade == null) {
			nodes = tradeDAO.findWhere("p.parent IS NULL");
		} else {
			nodes = tradeDAO.findByParent(trade.getId());
		}

		JSONArray result = new JSONArray();
		for (Trade trade : nodes) {
			result.add(trade.toJSON());
		}
		json.put("result", result);

		return JSON;
	}

	@SuppressWarnings("unchecked")
	public String searchJson() {
		Tree<Trade> tradeTree = tradeDAO.findHierarchyByIndexValue(q);
		json.put("result", tradeTree.toJSON(true).get("children"));

		return JSON;
	}

	@SuppressWarnings("unchecked")
	public String hierarchyJson() {
		if (trade != null) {
			Tree<Trade> tradeTree = tradeDAO.findHierarchyByTrade(trade.getId(), 1);
			JSONArray result = (JSONArray) tradeTree.toJSON(true).get("children");
			json.put("result", result);
		}

		return JSON;
	}

	@Anonymous
	public String index() throws Exception {
		Database db = new Database();
		db.execute("CALL reindexTrades(" + Trade.TOP + ",1,0,@counter)");
		return SUCCESS;
	}

	@RequiredPermission(value = OpPerms.ManageTrades, type = OpType.Edit)
	public String saveTradeAjax() throws Exception {
		if (trade != null) {
			trade.setAuditColumns(permissions);
			tradeDAO.save(trade);

			if (tradeLogo != null) {
				trade.setImageExtension(FileUtils.getExtension(tradeLogoFileName));
				FileUtils.moveFile(tradeLogo, getFtpDir(), "files/" + FileUtils.thousandize(trade.getId()), "trade_"
						+ trade.getId(), trade.getImageExtension(), true);
				tradeDAO.save(trade);
			}
		}

		return "trade";
	}

	@RequiredPermission(value = OpPerms.ManageTrades, type = OpType.Delete)
	public String deleteTradeAjax() throws Exception {
		if (trade != null) {
			if (deleteTrade(trade))
				trade = null;
		}

		return "trade";
	}

	@SuppressWarnings("unchecked")
	@RequiredPermission(value = OpPerms.ManageTrades, type = OpType.Delete)
	public String deleteMultipleJson() {
		boolean success = true;
		for (Trade t : trades) {
			if (!deleteTrade(t))
				success = false;
		}

		json.put("success", success);
		return JSON;
	}

	@RequiredPermission(value = OpPerms.ManageTrades, type = OpType.Edit)
	public String tradeAjax() {
		if (trade == null)
			trade = new Trade();
		return "trade";
	}

	@SuppressWarnings("unchecked")
	@RequiredPermission(value = OpPerms.ManageTrades, type = OpType.Edit)
	public String moveTradeJson() {
		json = new JSONObject();
		try {
			for (Trade t : trades) {
				t.setParent(trade);
				tradeDAO.save(t);
			}
			json.put("success", true);
		} catch (Exception e) {
			json.put("success", false);
		}
		return JSON;
	}

	@RequiredPermission(value = OpPerms.ManageTrades, type = OpType.Edit)
	public String addAlternateAjax() {
		if (alternateName == null || alternateName.equals("")) {
			addActionError("Alternate Name cannot be blank.");
			return "alternate";
		}

		TradeAlternate tradeAlternate = new TradeAlternate(trade, alternateName);
		if (trade.getAlternates().contains(tradeAlternate))
			addActionError("Alternate Already Exists.");
		else {
			trade.getAlternates().add(tradeAlternate);
			tradeDAO.save(trade);
		}

		return "alternate";
	}

	@RequiredPermission(value = OpPerms.ManageTrades, type = OpType.Edit)
	public String removeAlternateAjax() {
		tradeAlternateDAO.remove(alternate);
		return "alternate";
	}

	@RequiredPermission(value = OpPerms.ManageTrades, type = OpType.Edit)
	public String removeFileAjax() {
		if (trade != null && trade.getId() > 0) {
			FileUtils.deleteFile(getFtpDir() + trade.getImageLocation());
			trade.setImageExtension(null);
			trade.setAuditColumns(permissions);
			tradeDAO.save(trade);
		}

		return "trade";
	}

	public Result tradeLogo() {
		try {
			File logo = new File(getFtpDir() + trade.getImageLocationI());
			return new StreamResult(new FileInputStream(logo));
		} catch (FileNotFoundException e) {
			return null;
		}
	}

	private boolean deleteTrade(Trade trade) {
		boolean success = true;

		Trade parent = trade.getParent();

		if (parent != null) {
			for (Trade child : trade.getChildren()) {
				child.setParent(parent);
				tradeDAO.save(child);
			}

			tradeDAO.updateContractorTrades(trade.getId(), parent.getId());

			List<TradeAlternate> alternates = tradeAlternateDAO.findByTrade(trade.getId());
			for (TradeAlternate ta : alternates) {
				if (hasAlternate(ta, parent))
					trade.getAlternates().remove(ta);
				else
					ta.setTrade(parent);
			}

			tradeDAO.refresh(parent);
			tradeDAO.refresh(trade);
			tradeDAO.remove(trade);
		} else if (trade.getContractorCount() == 0 && trade.getChildren().size() == 0) {
			tradeDAO.remove(trade);
		} else {
			addActionError("You cannot delete a root level trade that is associated with any contractors or trades.");
			success = false;
		}

		return success;
	}

	public Trade getTrade() {
		return trade;
	}

	public void setTrade(Trade trade) {
		this.trade = trade;
	}

	public List<Trade> getTrades() {
		return trades;
	}

	public void setTrades(List<Trade> trades) {
		this.trades = trades;
	}

	public String getAlternateName() {
		return alternateName;
	}

	public void setAlternateName(String alternateName) {
		this.alternateName = alternateName;
	}

	public TradeAlternate getAlternate() {
		return alternate;
	}

	public void setAlternate(TradeAlternate alternate) {
		this.alternate = alternate;
	}

	public String getQ() {
		return q;
	}

	public void setQ(String q) {
		this.q = q;
	}

	public File getTradeLogo() {
		return tradeLogo;
	}

	public void setTradeLogo(File tradeLogo) {
		this.tradeLogo = tradeLogo;
	}

	public String getTradeLogoContentType() {
		return tradeLogoContentType;
	}

	public void setTradeLogoContentType(String tradeLogoContentType) {
		this.tradeLogoContentType = tradeLogoContentType;
	}

	public String getTradeLogoFileName() {
		return tradeLogoFileName;
	}

	public void setTradeLogoFileName(String tradeLogoFileName) {
		this.tradeLogoFileName = tradeLogoFileName;
	}

	public String getTradeLogoName() {
		return tradeLogoName;
	}

	public void setTradeLogoName(String tradeLogoName) {
		this.tradeLogoName = tradeLogoName;
	}
	
	public boolean hasAlternate(TradeAlternate tradeAlternate, Trade parent) {
		boolean result = false;
		for (TradeAlternate ta : parent.getAlternates()) {
			if (ta.getName().equals(tradeAlternate.getName()))
				result = true;
		}
		return result;
	}

}
