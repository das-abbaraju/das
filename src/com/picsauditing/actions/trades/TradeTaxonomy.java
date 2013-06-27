package com.picsauditing.actions.trades;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.struts2.dispatcher.StreamResult;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Objects;
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
import com.picsauditing.model.i18n.EntityTranslationHelper;
import com.picsauditing.search.Database;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.Node;
import com.picsauditing.util.Strings;
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
	private String alternateCategory;
	private TradeAlternate alternate;

	private String q;

	private static final List<String> ALTERNATE_CATEGORIES = Arrays.asList("Alias", "ISIC", "NACE", "NAICS", "SIC");

	/*
	 * Values used for the trade logo.
	 */
	private File tradeLogo;
	private String tradeLogoContentType = null;
	private String tradeLogoFileName = null;
	private String tradeLogoName = null;

	@SuppressWarnings("unchecked")
	public String json() {

		List<Trade> trades = Collections.emptyList();

		/*
		 * By default the tree will pass in 5, which is the Trade.TOP node.
		 */
		if (trade == null) {
			// TODO: Consider throwing an exception here instead
			trades = tradeDAO.findWhere("p.parent IS NULL");
		} else {
			trades = tradeDAO.findByParent(trade.getId());
		}

		JSONArray result = new JSONArray();
		for (Trade trade : trades) {
			result.add(Node.newNode(trade).toJSON());
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
		db.execute("CALL reindexTrades(" + Trade.TOP_ID + ",1,0,@counter)");
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

			EntityTranslationHelper.saveRequiredTranslationsForTrade(trade, permissions);
		}

		return "edit";
	}

	@RequiredPermission(value = OpPerms.ManageTrades, type = OpType.Delete)
	public String deleteTradeAjax() throws Exception {
		if (trade != null) {
			if (deleteTrade(trade)) {
				trade = null;
			}
		}

		return "edit";
	}

	@SuppressWarnings("unchecked")
	@RequiredPermission(value = OpPerms.ManageTrades, type = OpType.Delete)
	public String deleteMultipleJson() {
		boolean success = true;
		for (Trade t : trades) {
			if (!deleteTrade(t)) {
				success = false;
			}
		}

		json.put("success", success);
		return JSON;
	}

	@RequiredPermission(value = OpPerms.ManageTrades)
	public String tradeAjax() {
		if (trade == null) {
			trade = new Trade();
		}

		if (permissions.hasPermission(OpPerms.ManageTrades, OpType.Edit)) {
			return "edit";
		} else {
			return "view";
		}
	}

	@SuppressWarnings("unchecked")
	@RequiredPermission(value = OpPerms.ManageTrades, type = OpType.Edit)
	public String moveTradeJson() {
		json = new JSONObject();
		try {
			for (Trade t : trades) {
				t.setParent(trade);
				t.setAuditColumns(permissions);
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
		if (Strings.isEmpty(alternateName)) {
			addActionError(getText("TradeTaxonomy.error.NeedAlternateName"));
			return "alternate";
		}
		if (Strings.isEmpty(alternateCategory)) {
			addActionError("Alternate Category cannot be blank.");
			return "alternate";
		}

		TradeAlternate tradeAlternate = new TradeAlternate(trade, alternateName, alternateCategory);
		if (trade.getAlternates().contains(tradeAlternate)) {
			addActionError(getText("TradeTaxonomy.error.NeedAlternateCategory"));
		} else {
			trade.getAlternates().add(tradeAlternate);
			tradeDAO.save(trade);
			EntityTranslationHelper.saveRequiredTranslationsForTrade(trade, permissions);
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
			EntityTranslationHelper.saveRequiredTranslationsForTrade(trade, permissions);
		}

		return "edit";
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
		if (Objects.equal(trade, Trade.TOP)) {
			/*
			 * This is a sanity check. It should never happen as there is
			 * nothing in the UI to allow it.
			 */
			addActionError(getText("TradeTaxonomy.error.CannotDeleteTopNode"));
			return false;
		}

		boolean success = true;

		Trade parent = trade.getParent();

		if (!Objects.equal(parent, Trade.TOP)) {
			for (Trade child : trade.getChildren()) {
				child.setParent(parent);
				tradeDAO.save(child);
			}

			tradeDAO.updateContractorTrades(trade.getId(), parent.getId());

			List<TradeAlternate> alternates = tradeAlternateDAO.findByTrade(trade.getId());
			for (TradeAlternate ta : alternates) {
				if (hasAlternate(ta, parent)) {
					trade.getAlternates().remove(ta);
				} else {
					ta.setTrade(parent);
				}
			}

			tradeDAO.refresh(parent);
			tradeDAO.refresh(trade);
			tradeDAO.remove(trade);
		} else if (trade.getContractorCount() == 0 && trade.getChildren().size() == 0) {
			tradeDAO.remove(trade);
		} else {
			addActionError(getText("TradeTaxonomy.error.CannotDeleteTradeRootNode"));
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

	public String getAlternateCategory() {
		return alternateCategory;
	}

	public void setAlternateCategory(String alternateCategory) {
		this.alternateCategory = alternateCategory;
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

	public List<String> getAlternateCategories() {
		return Collections.unmodifiableList(ALTERNATE_CATEGORIES);
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
			if (ta.getName().equals(tradeAlternate.getName())) {
				result = true;
			}
		}
		return result;
	}

	public List<Trade> getTradeClassification() {
		List<Trade> list = tradeDAO.findListByTrade(trade.getId(), -1);
		/*
		 * TODO: This is the only instance I have found to exclude TOP. Find out
		 * if the other usages need changing.
		 */
		list.remove(Trade.TOP);

		return list;
	}

}
