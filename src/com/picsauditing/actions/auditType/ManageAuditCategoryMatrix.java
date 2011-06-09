package com.picsauditing.actions.auditType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.auditBuilder.AuditCategoryRuleCache;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.dao.AuditCategoryMatrixDAO;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.OperatorCompetencyDAO;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditCategoryMatrixCompetencies;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.OperatorCompetency;
import com.picsauditing.jpa.entities.QuestionComparator;
import com.picsauditing.util.DoubleMap;

@SuppressWarnings("serial")
public class ManageAuditCategoryMatrix extends PicsActionSupport {
	@Autowired
	protected AuditTypeDAO auditTypeDAO;
	@Autowired
	protected AuditCategoryDAO auditCategoryDAO;
	@Autowired
	protected AuditCategoryMatrixDAO auditCategoryMatrixDAO;
	@Autowired
	protected AuditQuestionDAO auditQuestionDAO;
	@Autowired
	protected OperatorCompetencyDAO operatorCompetencyDAO;
	@Autowired
	protected AuditDecisionTableDAO auditDecisionTableDAO;
	@Autowired
	protected AppPropertyDAO appPropertyDAO;
	@Autowired
	protected AuditCategoryRuleCache auditCategoryRuleCache;

	protected int auditTypeID;
	protected int categoryID;
	protected int itemID;
	protected boolean checked;
	protected boolean pivot;
	protected boolean editTable;
	protected AuditType auditType;

	protected int[] categoryIDs;
	protected int[] itemIDs;
	private List<AuditCategory> auditCategories;
	private List<ListItem> selectedCategories = new ArrayList<ListItem>();
	private List<ListItem> selectedItems = new ArrayList<ListItem>();
	private DoubleMap<Integer, Integer, Boolean> matrix = new DoubleMap<Integer, Integer, Boolean>();

	@RequiredPermission(value = OpPerms.ManageAudits, type = OpType.Edit)
	public String execute() throws Exception {
		return SUCCESS;
	}

	@SuppressWarnings("unchecked")
	@RequiredPermission(value = OpPerms.ManageAudits, type = OpType.Edit)
	public String toggleAjax() throws Exception {
		findAuditType();

		if (getActionErrors().size() > 0)
			return "toggle";

		if (itemID > 0 && categoryID > 0) {
			if (auditType.isDesktop()) {
				AuditCategory ac = auditCategoryDAO.find(categoryID);
				AuditQuestion aq = auditQuestionDAO.find(itemID);
				auditCategoryRuleCache.clear();
				AppProperty appProp = appPropertyDAO.find("clear_cache");
				if (appProp != null) {
					appProp.setValue("true");
					appPropertyDAO.save(appProp);
				}
				List<AuditCategoryRule> rules = auditDecisionTableDAO.findCategoryRulesByQuestion(itemID);
				AuditCategoryRule r = null;

				for (AuditCategoryRule rule : rules) {
					if (rule.getAuditCategory().equals(ac))
						r = rule;
				}

				if (r != null && !checked) {
					r.expire();
					auditDecisionTableDAO.save(r);
					json.put("msg", "Successfully expired " + ac.getName() + " from " + aq.getName());
				} else if (checked) {
					if (r == null) {
						r = new AuditCategoryRule();
						r.setAuditType(ac.getAuditType());
						r.setAuditCategory(ac);
						r.setQuestion(aq);

						if (aq.getQuestionType().equals("Check Box")) {
							r.setQuestionComparator(QuestionComparator.Equals);
							r.setQuestionAnswer("X");
						} else if (aq.getQuestionType().equals("Text"))
							r.setQuestionComparator(QuestionComparator.NotEmpty);

						r.defaultDates();
						r.calculatePriority();
					} else
						r.setInclude(true);

					r.setAuditColumns(permissions);
					auditDecisionTableDAO.save(r);
					json.put("msg", "Successfully added " + ac.getName() + " to " + aq.getName());
				}

				if (json.get("msg") == null) {
					json.put("reset", true);
					json.put("title", "Error");
					json.put("msg", "Could not " + (checked ? "add" : "remove") + ac.getName()
							+ (checked ? " to " : " from ") + aq.getName());
				} else
					json.put("title", "Success");
			} else {
				AuditCategoryMatrixCompetencies acmc = null;
				try {
					acmc = auditCategoryMatrixDAO.findByCategoryCompetency(categoryID, itemID);
				} catch (Exception e) {
					acmc = new AuditCategoryMatrixCompetencies();
					acmc.setCategory(auditCategoryDAO.find(categoryID));
					acmc.setOperatorCompetency(operatorCompetencyDAO.find(itemID));
				}

				if (checked) {
					acmc.setAuditColumns(permissions);
					auditCategoryMatrixDAO.save(acmc);
					json.put("msg", "Successfully added " + acmc.getOperatorCompetency().getLabel() + " to "
							+ acmc.getCategory().getName());
				} else {
					auditCategoryMatrixDAO.remove(acmc);
					json.put("msg", "Successfully removed " + acmc.getOperatorCompetency().getLabel() + " from "
							+ acmc.getCategory().getName());
				}

				json.put("title", "Success");
			}
		} else {
			json.put("reset", true);
			json.put("title", "Error");
			json.put("msg", "Missing category or item id");
		}

		return JSON;
	}

	@RequiredPermission(value = OpPerms.ManageAudits, type = OpType.Edit)
	public String tableAjax() throws Exception {
		findAuditType();

		if (getActionErrors().size() > 0)
			return "table";

		auditCategories = auditType.getTopCategories();

		if (categoryIDs != null || itemIDs != null)
			buildMatrix();

		if (categoryIDs != null) {
			for (Integer i : categoryIDs) {
				for (AuditCategory ac : auditCategories) {
					if (ac.getId() == i)
						selectedCategories.add(new ListItem(ac));
				}
			}

			Collections.sort(selectedCategories);
		}

		if (itemIDs != null) {
			for (Integer i : itemIDs) {
				for (OperatorCompetency oc : getOperatorCompetencies()) {
					if (oc.getId() == i)
						selectedItems.add(new ListItem(oc));
				}
			}

			Collections.sort(selectedItems);
		}

		return "table";
	}

	public int getAuditTypeID() {
		return auditTypeID;
	}

	public void setAuditTypeID(int auditTypeID) {
		this.auditTypeID = auditTypeID;
	}

	public int getCategoryID() {
		return categoryID;
	}

	public void setCategoryID(int categoryID) {
		this.categoryID = categoryID;
	}

	public int getItemID() {
		return itemID;
	}

	public void setItemID(int itemID) {
		this.itemID = itemID;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public boolean isPivot() {
		return pivot;
	}

	public void setPivot(boolean pivot) {
		this.pivot = pivot;
	}

	public boolean isEditTable() {
		return editTable;
	}

	public void setEditTable(boolean editTable) {
		this.editTable = editTable;
	}

	public AuditType getAuditType() {
		return auditType;
	}

	// Collections
	public List<AuditCategory> getAuditCategories() {
		return auditCategories;
	}

	public int[] getCategoryIDs() {
		return categoryIDs;
	}

	public void setCategoryIDs(int[] categoryIDs) {
		this.categoryIDs = categoryIDs;
	}

	public int[] getItemIDs() {
		return itemIDs;
	}

	public void setItemIDs(int[] itemIDs) {
		this.itemIDs = itemIDs;
	}

	public List<OperatorCompetency> getOperatorCompetencies() {
		return operatorCompetencyDAO.findAll();
	}

	public List<ListItem> getSelectedCategories() {
		return selectedCategories;
	}

	public void setSelectedCategories(List<ListItem> selectedCategories) {
		this.selectedCategories = selectedCategories;
	}

	public List<ListItem> getSelectedItems() {
		return selectedItems;
	}

	public void setSelectedItems(List<ListItem> selectedItems) {
		this.selectedItems = selectedItems;
	}

	public DoubleMap<Integer, Integer, Boolean> getMatrix() {
		return matrix;
	}

	// List Item
	public class ListItem implements Comparable<ListItem> {
		private int id;
		private String name;
		private String type;
		public static final String CATEGORY = "category";
		public static final String ITEM = "item";

		public ListItem(AuditCategory c) {
			this.id = c.getId();
			this.name = c.getName().toString();
			this.type = CATEGORY;
		}

		public ListItem(AuditQuestion q) {
			this.id = q.getId();
			this.name = q.getName().toString();
			this.type = ITEM;
		}

		public ListItem(OperatorCompetency o) {
			this.id = o.getId();
			this.name = o.getLabel();
			this.type = ITEM;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		@Override
		public int compareTo(ListItem o) {
			return this.name.compareTo(o.name);
		}
	}

	// Internal methods
	private void buildMatrix() {
		if (auditType.isDesktop()) {
			AuditCategory ac = auditCategoryDAO.find(categoryID);
			List<AuditCategoryRule> rules = auditDecisionTableDAO.findCategoryRulesByQuestionCategory(ac);

			for (AuditCategoryRule rule : rules) {
				if (rule.isInclude())
					matrix.put(rule.getAuditCategory().getId(), rule.getQuestion().getId(), true);
			}
		} else {
			Map<OperatorCompetency, List<AuditCategory>> acmcs = auditCategoryMatrixDAO.findCompetencyCategories();
			for (OperatorCompetency oc : acmcs.keySet()) {
				for (AuditCategory ac : acmcs.get(oc)) {
					matrix.put(ac.getId(), oc.getId(), true);
				}
			}
		}
	}

	private void findAuditType() {
		if (auditTypeID == 0) {
			addActionError("Please select a audit");
			return;
		}

		auditType = auditTypeDAO.find(auditTypeID);
	}
}