package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.json.simple.JSONObject;

@SuppressWarnings("serial")
@Entity
@Table(name = "rpt_dashboard")
public class Dashboard extends BaseTable {
	public static final int DEFAULT = 1;
	private User user;

	private List<DashboardWidget> widgets = new ArrayList<DashboardWidget>();

	@ManyToOne
	@JoinColumn(name = "userID", nullable = false)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@OneToMany(mappedBy = "dashboard", cascade = { CascadeType.ALL })
	public List<DashboardWidget> getWidgets() {
		return widgets;
	}

	public void setWidgets(List<DashboardWidget> widgets) {
		this.widgets = widgets;
	}

	@Transient
	public DashboardWidget addWidget(WidgetType type, int column) {
		DashboardWidget widget = new DashboardWidget();
		widget.setColumn(column);
		widget.setWidgetType(type);
		widget.setSequence(getMaxSequenceForColumn(column) + 10);
		this.widgets.add(widget);
		return widget;
	}

	private int getMaxSequenceForColumn(int column) {
		int lastSequence = 0;
		for (DashboardWidget siblingWidget : this.widgets) {
			if (column == siblingWidget.getColumn() && lastSequence < siblingWidget.getSequence()) {
				lastSequence = siblingWidget.getSequence();
			}
		}
		return lastSequence;
	}
}
