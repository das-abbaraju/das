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
	private ChartFormat chartFormat;
    private ChartSeries chartSeries;

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

    public ChartFormat getChartFormat() {
        return chartFormat;
    }

    public void setChartFormat(ChartFormat chartFormat) {
        this.chartFormat = chartFormat;
    }

    public ChartSeries getChartSeries() {
        return chartSeries;
    }

    public void setChartSeries(ChartSeries chartSeries) {
        this.chartSeries = chartSeries;
    }

    @Override
	public String toString() {
		return "{" + report.getName() + "}(" + widget.getWidgetID() + ")";
	}
}