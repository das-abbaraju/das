package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "report_chart")
public class ReportChart extends BaseTable {

	private Widget widget;
	private Report report;
    private ChartType chartType;
	private ChartOption chartOption;
    private ChartSeries series;

	public ReportChart() {
	}

	public ReportChart(Report report, Widget widget) {
		this.report = report;
		this.widget = widget;
	}

	@ManyToOne
	@JoinColumn(name = "widgetID", nullable = false, updatable = false)
	public Widget getWidget() {
		return widget;
	}

	public void setWidget(Widget widget) {
		this.widget = widget;
	}

	@ManyToOne
	@JoinColumn(name = "reportID", nullable = false, updatable = false)
	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

    public ChartType getChartType() {
        return chartType;
    }

    public void setChartType(ChartType chartType) {
        this.chartType = chartType;
    }

    public ChartOption getChartOption() {
        return chartOption;
    }

    public void setChartOption(ChartOption chartOption) {
        this.chartOption = chartOption;
    }

    public ChartSeries getSeries() {
        return series;
    }

    public void setSeries(ChartSeries series) {
        this.series = series;
    }

    @Override
	public String toString() {
		return "{" + report.getName() + "}(" + widget.getWidgetID() + ")";
	}
}