package com.picsauditing.actions.operators;

import javax.servlet.ServletOutputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.report.ReportActionSupport;
import com.picsauditing.dao.JobTaskDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.JobTask;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.ReportFilter;
import com.picsauditing.util.Strings;
import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class ManageJobTasksOperator extends ReportActionSupport implements Preparable {
	protected JobTaskDAO jobTaskDAO;
	protected OperatorAccountDAO opDAO;

	protected int id;
	protected int jobTaskID;
	protected boolean taskActive;
	protected String jobTaskLabel;
	protected String jobTaskName;
	protected String taskType;
	protected int displayOrder;

	protected OperatorAccount operator;
	protected JobTask newTask = new JobTask();
	protected ReportFilterJobTask filter = new ReportFilterJobTask();
	protected SelectSQL sql = new SelectSQL("job_task");
	
	public ManageJobTasksOperator(JobTaskDAO jobTaskDAO, OperatorAccountDAO opDAO) {
		this.jobTaskDAO = jobTaskDAO;
		this.opDAO = opDAO;
		
		orderByDefault = "displayOrder";
	}
	
	@Override
	public void prepare() throws Exception {
		loadPermissions();
		id = getParameter("id");
		
		if (id == 0 && permissions.isOperatorCorporate())
			id = permissions.getAccountId();
		if (id > 0)
			operator = opDAO.find(id);
		else
			throw new NoRightsException("Operator or PICS Administrator");
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		tryPermissions(OpPerms.ManageJobTasks);

		if (button != null && !"download".equals(button) && !"Search".equals(button)) {
			if ("Tasks".equalsIgnoreCase(button)) {
				newTask = jobTaskDAO.find(jobTaskID);
				return SUCCESS;
			}
			
			// Check if they can edit here
			tryPermissions(OpPerms.ManageJobTasks, OpType.Edit);

			if ("Save".equalsIgnoreCase(button)) {
				// Labels are required
				if (Strings.isEmpty(newTask.getLabel()))
					addActionError("Please add a label to this job site.");
				// Operators are required, but if one isn't set,
				// this operator should be added by default
				if (newTask.getOperator() == null && operator != null)
					newTask.setOperator(operator);
			}

			if ("Edit".equalsIgnoreCase(button)) {
				if (jobTaskID > 0 && !Strings.isEmpty(jobTaskLabel)) {
					newTask = jobTaskDAO.find(jobTaskID);
					newTask.setLabel(jobTaskLabel);

					if (!Strings.isEmpty(jobTaskName))
						newTask.setName(jobTaskName);

					newTask.setActive(taskActive);
					newTask.setTaskType(taskType);
					newTask.setDisplayOrder(displayOrder);
				} else
					addActionError("Missing job task ID or label");
			}

			if ("Remove".equalsIgnoreCase(button)) {
				newTask = jobTaskDAO.find(jobTaskID);
				jobTaskDAO.remove(newTask);
				newTask = null;
			}

			if (getActionErrors().size() > 0)
				return SUCCESS;

			if (newTask != null) {
				newTask.setAuditColumns(permissions);
				jobTaskDAO.save(newTask);
			}
			
			return redirect("ManageJobTasksOperator.action?id=" + id);
		}
		
		buildQuery();
		run(sql);
		
		if (download) {
			addExcelColumns();
			HSSFWorkbook wb = excelSheet.buildWorkbook(permissions.hasPermission(OpPerms.DevelopmentEnvironment));

			String filename = this.getClass().getName().substring(this.getClass().getName().lastIndexOf("."));
			
			excelSheet.setName(filename);
			filename += ".xls";

			ServletActionContext.getResponse().setContentType("application/vnd.ms-excel");
			ServletActionContext.getResponse().setHeader("Content-Disposition", "attachment; filename=" + filename);
			ServletOutputStream outstream = ServletActionContext.getResponse().getOutputStream();
			wb.write(outstream);
			outstream.flush();
			ServletActionContext.getResponse().flushBuffer();
			return null;
		}

		return SUCCESS;
	}
	
	protected void buildQuery() {
		sql.addField("id");
		sql.addField("label");
		sql.addField("name");
		sql.addField("CASE WHEN active = 1 THEN 'Active' ELSE 'Inactive' END activeLabel");
		sql.addField("taskType");
		sql.addField("displayOrder");
		
		sql.addWhere("opID = " + id);
		
		if (filterOn(filter.getLabel()))
			sql.addWhere("label LIKE '%" + filter.getLabel() + "%'");
		if (filterOn(filter.getName()))
			sql.addWhere("name LIKE '%" + filter.getName() + "%'");
		if (filterOn(filter.getTaskType()))
			sql.addWhere("taskType IN (" + Strings.implodeForDB(filter.getTaskType(), ",") + ")");
		if (filter.isActive())
			sql.addWhere("active = 1");
		else
			sql.addWhere("active = 0");
	}
	
	protected void addExcelColumns() {
		excelSheet.setData(data);
		
		excelSheet.addColumn(new ExcelColumn("label", "Label"));
		excelSheet.addColumn(new ExcelColumn("name", "Task Name"));
		excelSheet.addColumn(new ExcelColumn("activeLabel", "Active"));
		excelSheet.addColumn(new ExcelColumn("taskType", "Task Type"));
	}
	
	public String getSubHeading() {
		return "Manage Job Tasks";
	}
	
	public ReportFilterJobTask getFilter() {
		return filter;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public OperatorAccount getOperator() {
		return operator;
	}
	
	public void setOperator(OperatorAccount operator) {
		this.operator = operator;
	}

	public int getJobTaskID() {
		return jobTaskID;
	}

	public void setJobTaskID(int jobTaskID) {
		this.jobTaskID = jobTaskID;
	}

	public boolean isTaskActive() {
		return taskActive;
	}

	public void setTaskActive(boolean taskActive) {
		this.taskActive = taskActive;
	}

	public String getJobTaskLabel() {
		return jobTaskLabel;
	}

	public void setJobTaskLabel(String jobTaskLabel) {
		this.jobTaskLabel = jobTaskLabel;
	}

	public String getJobTaskName() {
		return jobTaskName;
	}

	public void setJobTaskName(String jobTaskName) {
		this.jobTaskName = jobTaskName;
	}

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public int getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}

	public JobTask getNewTask() {
		return newTask;
	}

	public void setNewTask(JobTask newTask) {
		this.newTask = newTask;
	}
	
	public class ReportFilterJobTask extends ReportFilter {
		private String label;
		private String name;
		private boolean active = true;
		private String[] taskType;
		private String[] taskTypeList = new String[] { "L", "G", "L/G" };

		public String getLabel() {
			return label;
		}
		
		public void setLabel(String label) {
			this.label = label;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public boolean isActive() {
			return active;
		}
		
		public void setActive(boolean active) {
			this.active = active;
		}
		
		public String[] getTaskType() {
			return taskType;
		}
		
		public void setTaskType(String[] taskType) {
			this.taskType = taskType;
		}
		
		public String[] getTaskTypeList() {
			return taskTypeList;
		}
	}
}
