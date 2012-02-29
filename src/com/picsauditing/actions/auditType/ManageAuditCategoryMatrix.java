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
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditCategoryMatrixCompetencies;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.OperatorCompetency;
import com.picsauditing.util.DoubleMap;
import com.picsauditing.util.Strings;

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

	protected int itemID;
	protected boolean checked;
	protected boolean pivot;
	protected boolean editTable;

	protected AuditType auditType;
	protected AuditCategory category;

	protected int[] categoryIDs;
	protected int[] itemIDs;
	private List<ListItem> selectedCategories = new ArrayList<ListItem>();
	private List<ListItem> selectedItems = new ArrayList<ListItem>();
	private DoubleMap<Integer, Integer, Boolean> matrix = new DoubleMap<Integer, Integer, Boolean>();

	@RequiredPermission(value = OpPerms.ManageAudits, type = OpType.Edit)
	public String execute() {
		return SUCCESS;
	}

	@RequiredPermission(value = OpPerms.ManageAudits, type = OpType.Edit)
	public String filters() {
		return "filters";
	}

	@RequiredPermission(value = OpPerms.ManageAudits, type = OpType.Edit)
	public String table() {
		buildMatrix();

		if (auditType.getId() == AuditType.HSE_COMPETENCY_REVIEW) {
			fillSelectedCategories();
			fillSelectedItems();
		}

		if (hasActionErrors()) {
			return BLANK;
		}

		return "table";
	}

	@RequiredPermission(value = OpPerms.ManageAudits, type = OpType.Edit)
	public String manageAssociations() throws Exception {
		if (itemID > 0 && category != null) {
			AuditCategoryMatrixCompetencies acmc = findOrCreateCategoryCompetencyAssociation();

			if (checked) {
				acmc.setAuditColumns(permissions);
				auditCategoryMatrixDAO.save(acmc);
			} else {
				auditCategoryMatrixDAO.remove(acmc);
			}
		} else {
			addActionError(getText("AuditCategoryMatrix.MissingCategories"));
			addActionError(getText("AuditCategoryMatrix.MissingCompetencies"));
		}

		return BLANK;
	}

	public AuditType getAuditType() {
		return auditType;
	}

	public void setAuditType(AuditType auditType) {
		this.auditType = auditType;
	}

	public AuditCategory getCategory() {
		return category;
	}

	public void setCategory(AuditCategory category) {
		this.category = category;
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

	public List<OperatorCompetency> getOperatorCompetencies() {
		return operatorCompetencyDAO.findAll();
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

	private void buildMatrix() {
		if (auditType.isDesktop()) {
			List<AuditCategoryRule> rules = auditDecisionTableDAO.findCategoryRulesByQuestionCategory(category);

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

	private void fillSelectedCategories() {
		if (categoryIDs == null || categoryIDs.length == 0) {
			addActionError(getText("AuditCategoryMatrix.MissingCategories"));
		} else {
			List<AuditCategory> categories = auditCategoryDAO.findWhere("id IN (" + Strings.implode(categoryIDs) + ")");

			for (AuditCategory category : categories) {
				selectedCategories.add(new ListItem(category));
			}

			Collections.sort(selectedCategories);
		}
	}

	@SuppressWarnings("unchecked")
	private void fillSelectedItems() {
		if (itemIDs == null || itemIDs.length == 0) {
			if (auditType.getId() == AuditType.HSE_COMPETENCY_REVIEW) {
				addActionError(getText("AuditCategoryMatrix.MissingCompetencies"));
			}
		} else {
			List<OperatorCompetency> competencies = (List<OperatorCompetency>) operatorCompetencyDAO.findWhere(
					OperatorCompetency.class, "id IN (" + Strings.implode(itemIDs) + ")", 0);

			for (OperatorCompetency competency : competencies) {
				selectedItems.add(new ListItem(competency));
			}

			Collections.sort(selectedItems);
		}
	}

	private AuditCategoryMatrixCompetencies findOrCreateCategoryCompetencyAssociation() {
		AuditCategoryMatrixCompetencies acmc;

		try {
			acmc = auditCategoryMatrixDAO.findByCategoryCompetency(category.getId(), itemID);
		} catch (Exception e) {
			acmc = new AuditCategoryMatrixCompetencies();
			acmc.setCategory(category);
			acmc.setOperatorCompetency(operatorCompetencyDAO.find(itemID));
		}

		return acmc;
	}
}