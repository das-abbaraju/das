package com.picsauditing.actions.auditType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.picsauditing.PICS.AuditCategoryRuleCache;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.PicsActionSupport;
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
	protected AuditTypeDAO auditTypeDAO;
	protected AuditCategoryDAO acDAO;
	protected AuditCategoryMatrixDAO acmDAO;
	protected AuditQuestionDAO aqDAO;
	protected OperatorCompetencyDAO ocDAO;
	protected AuditDecisionTableDAO adtDAO;
	protected AppPropertyDAO appPropertyDAO;
	protected AuditCategoryRuleCache acrCache;

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
	private List<AuditCategory> desktopCategories;
	private List<AuditQuestion> desktopQuestions;
	private List<ListItem> selectedCategories = new ArrayList<ListItem>();
	private List<ListItem> selectedItems = new ArrayList<ListItem>();
	private DoubleMap<Integer, Integer, Boolean> matrix = new DoubleMap<Integer, Integer, Boolean>();

	public ManageAuditCategoryMatrix(AuditTypeDAO auditTypeDAO, AuditCategoryDAO acDAO, AuditCategoryMatrixDAO acmDAO,
			AuditQuestionDAO aqDAO, OperatorCompetencyDAO ocDAO, AuditDecisionTableDAO adtDAO,
			AuditCategoryRuleCache acrCache, AppPropertyDAO appPropertyDAO) {
		this.auditTypeDAO = auditTypeDAO;
		this.acDAO = acDAO;
		this.acmDAO = acmDAO;
		this.aqDAO = aqDAO;
		this.ocDAO = ocDAO;
		this.adtDAO = adtDAO;
		this.acrCache = acrCache;
		this.appPropertyDAO = appPropertyDAO;
	}

	@SuppressWarnings("unchecked")
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		permissions.tryPermission(OpPerms.ManageAudits, OpType.Edit);

		if (button != null) {
			if (auditTypeID == 0) {
				addActionError("Please select a audit");
				return SUCCESS;
			}

			auditType = auditTypeDAO.find(auditTypeID);

			if ("Toggle".equals(button)) {
				if (itemID > 0 && categoryID > 0) {
					if (auditType.isDesktop()) {
						AuditCategory ac = acDAO.find(categoryID);
						AuditQuestion aq = aqDAO.find(itemID);
						acrCache.clear();
						AppProperty appProp = appPropertyDAO.find("clear_cache");
						if (appProp != null) {
							appProp.setValue("true");
							appPropertyDAO.save(appProp);
						}
						List<AuditCategoryRule> rules = adtDAO.findCategoryRulesByQuestion(itemID);
						AuditCategoryRule r = null;

						for (AuditCategoryRule rule : rules) {
							if (rule.getAuditCategory().equals(ac))
								r = rule;
						}

						if (r != null && !checked) {
							// TODO Delete or expire this category rule?
							json.put("msg", "Successfully removed " + ac.getName() + " from " + aq.getName());
							rules.remove(r);
							adtDAO.remove(r);
						} else if (checked) {
							if (r == null) {
								r = new AuditCategoryRule();
								r.setAuditType(ac.getAuditType());
								r.setAuditCategory(ac);
								r.setAcceptsBids(false);
								r.setQuestion(aq);

								if (aq.getQuestionType().equals("Service")) {
									r.setQuestionComparator(QuestionComparator.StartsWith);
									r.setQuestionAnswer("C");
								} else if (aq.getQuestionType().equals("Main Work")
										|| aq.getQuestionType().equals("Check Box")) {
									r.setQuestionComparator(QuestionComparator.Equals);
									r.setQuestionAnswer("X");
								} else if (aq.getQuestionType().equals("Text"))
									r.setQuestionComparator(QuestionComparator.NotEmpty);

								r.defaultDates();
								r.calculatePriority();
							} else
								r.setInclude(true);

							r.setAuditColumns(permissions);

							adtDAO.save(r);

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
			AuditCategory ac = acDAO.find(categoryID);
			List<AuditCategoryRule> rules = adtDAO.findCategoryRulesByQuestionCategory(ac);

			for (AuditCategoryRule rule : rules) {
				if (rule.isInclude())
					matrix.put(rule.getAuditCategory().getId(), rule.getQuestion().getId(), true);
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