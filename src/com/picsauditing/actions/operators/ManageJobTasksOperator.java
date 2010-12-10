package com.picsauditing.actions.operators;

import java.util.Collections;
import java.util.List;

import javax.servlet.ServletOutputStream;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.JobTaskDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.JobTask;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageJobTasksOperator extends OperatorActionSupport {
	protected JobTaskDAO jobTaskDAO;

	protected int jobTaskID;
	protected boolean taskActive;
	protected String jobTaskLabel;
	protected String jobTaskName;
	protected String taskType;
	protected int displayOrder;

	protected JobTask newTask = new JobTask();

	public ManageJobTasksOperator(OperatorAccountDAO operatorDao, JobTaskDAO jobTaskDAO) {
		super(operatorDao);
		this.jobTaskDAO = jobTaskDAO;

		subHeading = "Manage Job Tasks";
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		findOperator();
		tryPermissions(OpPerms.ManageJobTasks);

		if (button != null) {
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

			if (permissions.isOperator())
				return redirect("ManageJobTasksOperator.action");
			else
				return redirect("ManageJobTasksOperator.action?id=" + operator.getId());
		}

		return SUCCESS;
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

	public List<JobTask> getTasks() {
		return jobTaskDAO.findOperatorTasks(operator.getId());
	}

	public void getExcelDownload() throws Exception {
		loadPermissions();
		findOperator();

		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		wb.setSheetName(0, "Job Tasks for " + operator.getName());

		// Header
		HSSFFont headerFont = wb.createFont();
		headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

		HSSFCellStyle header = wb.createCellStyle();
		header.setFont(headerFont);
		header.setAlignment(HSSFCellStyle.ALIGN_CENTER);

		HSSFRow row = sheet.createRow(0);
		HSSFCell cell = row.createCell(0);
		cell.setCellStyle(header);
		cell.setCellValue(new HSSFRichTextString("Label"));

		cell = row.createCell(1);
		cell.setCellStyle(header);
		cell.setCellValue(new HSSFRichTextString("Task Name"));

		cell = row.createCell(2);
		cell.setCellStyle(header);
		cell.setCellValue(new HSSFRichTextString("Active"));

		cell = row.createCell(3);
		cell.setCellStyle(header);
		cell.setCellValue(new HSSFRichTextString("Task Type"));

		List<JobTask> tasks = getTasks();
		Collections.sort(tasks);

		int rownum = 1;
		for (JobTask task : tasks) {
			row = sheet.createRow(rownum);
			rownum++;

			cell = row.createCell(0);
			cell.setCellValue(new HSSFRichTextString(task.getLabel()));

			cell = row.createCell(1);
			cell.setCellValue(new HSSFRichTextString(task.getName()));

			cell = row.createCell(2);
			cell.setCellValue(new HSSFRichTextString(task.isActive() ? "Active" : "Inactive"));

			cell = row.createCell(3);
			cell.setCellValue(new HSSFRichTextString(task.getTaskType()));
		}

		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2);
		sheet.autoSizeColumn(3);

		String filename = "JobTasks.xls";

		ServletActionContext.getResponse().setContentType("application/vnd.ms-excel");
		ServletActionContext.getResponse().setHeader("Content-Disposition", "attachment; filename=" + filename);
		ServletOutputStream outstream = ServletActionContext.getResponse().getOutputStream();
		wb.write(outstream);
		outstream.flush();
		ServletActionContext.getResponse().flushBuffer();
	}
}
