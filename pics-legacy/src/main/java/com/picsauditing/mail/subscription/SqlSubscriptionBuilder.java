package com.picsauditing.mail.subscription;

import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.search.Report;

public abstract class SqlSubscriptionBuilder extends SubscriptionBuilder {
	protected Report report = new Report();
	protected List<BasicDynaBean> data;
}
