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
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
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

    protected String googleChartType;
    protected String googleStyleType;

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

	// /////////// Transient Properties/Methods ///////////////

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

		// TODO: Remove this once all charts have been changed to Google Visualization.
		if (widgetType.equals("Chart")) {
			if (chartType == null)
				return "chartType is null";
			// FusionChart.createChartHTML("charts/"+chartType.toString()+".swf",
			// dataURL, dataXML,
			// chartId, chartWidth, chartHeight, debug);
			return FusionChart.createChart("charts/" + chartType.toString() + ".swf", url, "", chartType.toString()
					+ "_" + widgetID, 400, 400, debug, false);
		}

        if (widgetType.equals("GoogleChart")) {
            if (googleChartType == null)
                return "chartType is null";
            return "";
        }

		if (widgetType.equals("Html") || widgetType.equals("Rss"))
			return "<div class=\"inprogress\"></div><script>$('#panel" + widgetID + "_content').load('" + url
					+ "');</script>";

		return "Unknown Widget Type " + widgetType;
	}

	@Transient
	public String getReload() {
		if (widgetType == null)
			return "";

		if (widgetType.equals("Chart")) {
			if (chartType == null)
				return "";
			String chartID = chartType.toString() + "_" + widgetID;
			return "chart_" + chartID + ".render('" + chartID + "Div')";
		}

		if (widgetType.equals("Html") || widgetType.equals("Rss"))
			return "$('#panel" + widgetID + "_content').html('<div class=inprogress></div>'); $('#panel" + widgetID
					+ "_content').load('" + url + "')";

		return "";
	}

    public String getGoogleChartType() {
        return googleChartType;
    }

    public void setGoogleChartType(String gChartType) {
        this.googleChartType = gChartType;
    }

    public String getGoogleStyleType() {
        return googleStyleType;
    }

    public void setGoogleStyleType(String googleStyleType) {
        this.googleStyleType = googleStyleType;
    }
}