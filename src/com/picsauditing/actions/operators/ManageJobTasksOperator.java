package com.picsauditing.actions.operators;

import javax.servlet.ServletOutputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.interceptor.annotations.Before;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.RequiredPermission;
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
public class ManageJobTasksOperator extends ReportActionSupport {
	@Autowired
	protected JobTaskDAO jobTaskDAO;
	@Autowired
	protected OperatorAccountDAO operatorAccountDAO;

	protected OperatorAccount operator;
	protected JobTask jobTask;
	protected String label;
	protected String name;
	protected String taskType;
	protected boolean active;
	// Filters
	protected SelectSQL sql;
	protected ReportFilterJobTask filter = new ReportFilterJobTask();

	@Before
	public void startup() throws Exception {
		if (operator == null && permissions.isOperatorCorporate())
			operator = operatorAccountDAO.find(permissions.getAccountId());

		if (operator == null)
			throw new NoRightsException("Operator or PICS Administrator");
	}

	@Override
	@RequiredPermission(value = OpPerms.ManageJobTasks)
	public String execute() throws Exception {
		orderByDefault = "displayOrder";
		sql = new SelectSQL("job_task");
		buildQuery();
		run(sql);

		if (download) {
			addExcelColumns();
			HSSFWorkbook wb = excelSheet.buildWorkbook(permissions.hasPermission(OpPerms.DevelopmentEnvironment));

			String filename = this.getClass().getSimpleName();

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

	@RequiredPermission(value = OpPerms.ManageJobTasks, type = OpType.Edit)
	public String save() throws Exception {
		if (jobTask == null)
			jobTask = new JobTask();
		// Labels are required
		if (Strings.isEmpty(label))
			addActionError(getText(String.format("%s.message.AddLabel", getScope())));

		return saveJobTask();
	}

	@RequiredPermission(value = OpPerms.ManageJobTasks, type = OpType.Edit)
	public String edit() throws Exception {
		if (jobTask == null || Strings.isEmpty(label))
			addActionError(getText(String.format("%s.message.MissingJobTaskLabel", getScope())));

		return saveJobTask();
	}

	@RequiredPermission(value = OpPerms.ManageJobTasks, type = OpType.Edit)
	public String remove() throws Exception {
		jobTaskDAO.remove(jobTask);

		return redirect("ManageJobTasksOperator.action?operator=" + operator.getId());
	}

	protected void buildQuery() {
		sql.addField("id");
		sql.addField("label");
		sql.addField("name");
		sql.addField("CASE WHEN active = 1 THEN 'Active' ELSE 'Inactive' END activeLabel");
		sql.addField("taskType");
		sql.addField("displayOrder");

		sql.addWhere("opID = " + operator.getId());

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

		excelSheet.addColumn(new ExcelColumn("label", getText("JobTask.label")));
		excelSheet.addColumn(new ExcelColumn("name", getText("JobTask.name")));
		excelSheet.addColumn(new ExcelColumn("activeLabel", getText("JobTask.active")));
		excelSheet.addColumn(new ExcelColumn("taskType", getText("JobTask.taskType")));
	}

	private String saveJobTask() throws Exception {
		// Operators are required, but if one isn't set,
		// this operator should be added by default
		if (jobTask.getOperator() == null && operator != null)
			jobTask.setOperator(operator);

		if (getActionErrors().size() > 0)
			return redirect("ManageJobTasksOperator.action?operator=" + operator.getId());
		
		jobTask.setLabel(label);
		jobTask.setName(name);
		jobTask.setTaskType(taskType);
		jobTask.setActive(active);

		jobTask.setAuditColumns(permissions);
		jobTaskDAO.save(jobTask);

		return redirect("ManageJobTasksOperator.action?operator=" + operator.getId());
	}

	public ReportFilterJobTask getFilter() {
		return filter;
	}

	public OperatorAccount getOperator() {
		return operator;
	}

	public void setOperator(OperatorAccount operator) {
		this.operator = operator;
	}

	public JobTask getJobTask() {
		return jobTask;
	}

	public void setJobTask(JobTask jobTask) {
		this.jobTask = jobTask;
	}

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

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
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
