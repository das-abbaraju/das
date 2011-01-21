package com.picsauditing.actions.auditType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.dao.AuditCategoryMatrixDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.OperatorCompetencyDAO;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditCategoryMatrixCompetencies;
import com.picsauditing.jpa.entities.AuditCategoryMatrixDesktop;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.OperatorCompetency;
import com.picsauditing.util.DoubleMap;

@SuppressWarnings("serial")
public class ManageAuditCategoryMatrix extends PicsActionSupport {
	protected AuditTypeDAO auditTypeDAO;
	protected AuditCategoryDAO acDAO;
	protected AuditCategoryMatrixDAO acmDAO;
	protected AuditQuestionDAO aqDAO;
	private OperatorCompetencyDAO ocDAO;

	protected int auditTypeID;
	protected int categoryID;
	protected int itemID;
	protected boolean checked;
	protected AuditType auditType;

	protected int[] categoryIDs;
	protected int[] itemIDs;
	private List<AuditCategory> auditCategories;
	private List<AuditCategory> desktopCategories;
	private List<AuditQuestion> desktopQuestions;
	private List<ListItem> selectedCategories = new ArrayList<ListItem>();
	private List<ListItem> selectedItems = new ArrayList<ListItem>();
	private DoubleMap<Integer, Integer, Boolean> matrix = new DoubleMap<Integer, Integer, Boolean>();

	public ManageAuditCategoryMatrix(AuditTypeDAO auditTypeDAO, AuditCategoryDAO acDAO, AuditCategoryMatrixDAO acmDAO,
			AuditQuestionDAO aqDAO, OperatorCompetencyDAO ocDAO) {
		this.auditTypeDAO = auditTypeDAO;
		this.acDAO = acDAO;
		this.acmDAO = acmDAO;
		this.aqDAO = aqDAO;
		this.ocDAO = ocDAO;
	}

	@SuppressWarnings("unchecked")
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		permissions.tryPermission(OpPerms.ManageAudits);

		if (button != null) {
			if (auditTypeID == 0) {
				addActionError("Please select a audit");
				return SUCCESS;
			}

			auditType = auditTypeDAO.find(auditTypeID);

			if ("Toggle".equals(button)) {
				if (itemID > 0 && categoryID > 0) {
					if (auditType.isDesktop()) {
						AuditCategoryMatrixDesktop acmd = null;
						try {
							acmd = acmDAO.findByCategoryQuestion(categoryID, itemID);
						} catch (Exception e) {
							acmd = new AuditCategoryMatrixDesktop();
							acmd.setCategory(acDAO.find(categoryID));
							acmd.setAuditQuestion(aqDAO.find(itemID));
						}

						if (checked) {
							acmd.setAuditColumns(permissions);
							acmDAO.save(acmd);
							json.put("msg", "Successfully added " + acmd.getAuditQuestion().getName() + " to "
									+ acmd.getCategory().getName());
						} else {
							acmDAO.remove(acmd);
							json.put("msg", "Successfully removed " + acmd.getAuditQuestion().getName() + " from "
									+ acmd.getCategory().getName());
						}

						json.put("title", "Success");
					} else {
						AuditCategoryMatrixCompetencies acmc = null;
						try {
							acmc = acmDAO.findByCategoryCompetency(categoryID, itemID);
						} catch (Exception e) {
							acmc = new AuditCategoryMatrixCompetencies();
							acmc.setCategory(acDAO.find(categoryID));
							acmc.setOperatorCompetency(ocDAO.find(itemID));
						}

						if (checked) {
							acmc.setAuditColumns(permissions);
							acmDAO.save(acmc);
							json.put("msg", "Successfully added " + acmc.getOperatorCompetency().getLabel() + " to "
									+ acmc.getCategory().getName());
						} else {
							acmDAO.remove(acmc);
							json.put("msg", "Successfully removed " + acmc.getOperatorCompetency().getLabel()
									+ " from " + acmc.getCategory().getName());
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

			auditCategories = auditType.getTopCategories();

			if ("DesktopCategories".equals(button)) {
				AuditCategory cat = acDAO.find(AuditCategory.SERVICES_PERFORMED);
				desktopCategories = new ArrayList<AuditCategory>(cat.getChildren());
				desktopCategories.remove(cat);
				Collections.sort(desktopCategories, new Comparator<AuditCategory>() {
					@Override
					public int compare(AuditCategory o1, AuditCategory o2) {
						if (o1.getAncestors().size() == o2.getAncestors().size())
							return o1.compareTo(o2);

						return o1.getAncestors().size() - o2.getAncestors().size();
					}
				});

				return SUCCESS;
			}

			if (auditType.isDesktop() && categoryID > 0) {
				AuditCategory cat = acDAO.find(categoryID);
				desktopQuestions = cat.getQuestions();
			}

			if ("Table".equals(button)) {
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
						if (auditType.isDesktop()) {
							for (AuditQuestion aq : desktopQuestions) {
								if (aq.getId() == i)
									selectedItems.add(new ListItem(aq));
							}
						} else {
							for (OperatorCompetency oc : getOperatorCompetencies()) {
								if (oc.getId() == i)
									selectedItems.add(new ListItem(oc));
							}
						}
					}

					Collections.sort(selectedItems);
				}

				return "table";
			}
		}

		// TODO possibly create a filter for selecting specific categories or
		// questions/competencies
		return SUCCESS;
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

	public List<AuditCategory> getDesktopCategories() {
		return desktopCategories;
	}

	public List<AuditQuestion> getDesktopQuestions() {
		return desktopQuestions;
	}

	public List<OperatorCompetency> getOperatorCompetencies() {
		return ocDAO.findAll();
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
			this.name = c.getName();
			this.type = CATEGORY;
		}

		public ListItem(AuditQuestion q) {
			this.id = q.getId();
			this.name = q.getName();
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
			Map<AuditQuestion, List<AuditCategory>> acmds = acmDAO.findQuestionCategories(categoryID);
			for (AuditQuestion aq : acmds.keySet()) {
				for (AuditCategory ac : acmds.get(aq)) {
					matrix.put(ac.getId(), aq.getId(), true);
				}
			}
		} else {
			Map<OperatorCompetency, List<AuditCategory>> acmcs = acmDAO.findCompetencyCategories();
			for (OperatorCompetency oc : acmcs.keySet()) {
				for (AuditCategory ac : acmcs.get(oc)) {
					matrix.put(ac.getId(), oc.getId(), true);
				}
			}
		}
	}
}