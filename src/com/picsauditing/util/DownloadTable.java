package com.picsauditing.util;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.beanutils.BasicDynaBean;

public class DownloadTable {
	String filename = "filename";
	String extension = "csv";
	Set<DownloadColumn> columns = new TreeSet<DownloadColumn>();
	List<BasicDynaBean> data;

	public byte[] output() {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		byteStream.write(1);
		byteStream.write(2);
		for (DownloadColumn column : columns) {
			// Print column header
		}
		for (BasicDynaBean row : data) {
			for (DownloadColumn column : columns) {
				// Print cell
			}
		}
		return byteStream.toByteArray();
	}

	public void setData(List<BasicDynaBean> data) {
		this.data = data;
	}

	public void addColumn(String column) {
		addColumn(column, column);
	}

	public void addColumn(String column, String label) {
		DownloadColumn c = new DownloadColumn();
		c.column = column;
		c.label = label;
		columns.add(c);
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	private class DownloadColumn implements Comparable<DownloadColumn> {
		public String label;
		public String column;
		public int order;
		
		@Override
		public int compareTo(DownloadColumn o) {
			return order - o.order;
		}
	}

}
