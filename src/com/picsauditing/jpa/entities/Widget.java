package com.picsauditing.jpa.entities;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.picsauditing.access.OpPerms;
import com.picsauditing.util.chart.ChartType;
import com.picsauditing.util.chart.FusionChart;


@Entity
@Table(name = "widget")
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE, region="daily")
public class Widget {
	protected int widgetID;
	protected String caption;
	protected String widgetType;
	protected boolean synchronous = false;
	protected String url;

	protected boolean expanded = true;
	protected String customConfig;

	protected boolean debug = false;

	protected OpPerms requiredPermission;
	
	protected ChartType chartType = ChartType.Column2D;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "widgetID", nullable = false, updatable = false)
	public int getWidgetID() {
		return widgetID;
	}

	public void setWidgetID(int widgetID) {
		this.widgetID = widgetID;
	}

	@Column(nullable = false)
	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getWidgetType() {
		return widgetType;
	}

	public void setWidgetType(String widgetType) {
		this.widgetType = widgetType;
	}

	public boolean isSynchronous() {
		return synchronous;
	}

	public void setSynchronous(boolean synchronous) {
		this.synchronous = synchronous;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Enumerated(EnumType.STRING)
	public OpPerms getRequiredPermission() {
		return requiredPermission;
	}

	public void setRequiredPermission(OpPerms requiredPermission) {
		this.requiredPermission = requiredPermission;
	}

	@Enumerated(EnumType.STRING)
	public ChartType getChartType() {
		return chartType;
	}

	public void setChartType(ChartType chartType) {
		this.chartType = chartType;
	}
	
	///////////// Transient Properties/Methods ///////////////

	@Transient
	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	@Transient
	public String getCustomConfig() {
		return customConfig;
	}

	public void setCustomConfig(String customConfig) {
		this.customConfig = customConfig;
	}

	@Transient
	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	@Transient
	public String getContent() {
		if (widgetType == null)
			return "widgetType is null";
		
		if (widgetType.equals("Chart")) {
			if (chartType == null)
				return "chartType is null";
			// FusionChart.createChartHTML("charts/"+chartType.toString()+".swf",
			// dataURL, dataXML,
			// chartId, chartWidth, chartHeight, debug);
			return FusionChart.createChart("charts/" + chartType.toString() + ".swf", url, "", chartType.toString() + "_"
					+ widgetID, 300, 300, debug, false);
		}
		
		if (widgetType.equals("Html"))
			return "<div class=\"inprogress\"></div><script>new Ajax.Updater('panel" + widgetID + "_content', '" + url
			+ "');</script>";

		if (widgetType.equals("Rss"))
			return "<div class=\"inprogress\"></div><script>new Ajax.Updater('panel" + widgetID + "_content', '" + url
			+ "');</script>";

		return "Unknown Widget Type "+widgetType;
	}

	
}
