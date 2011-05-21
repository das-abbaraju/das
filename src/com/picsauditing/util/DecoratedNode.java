package com.picsauditing.util;

import java.util.Collection;

import org.json.simple.JSONObject;

public class DecoratedNode<T extends Hierarchical<T>> extends Node<T> {
	private boolean decorated = false;
	
	public DecoratedNode() {
		super();
	}

	public DecoratedNode(T data) {
		super(data);
	}

	public DecoratedNode(T data, Node<T> child) {
		super(data, child);
	}

	public DecoratedNode(T data, Collection<Node<T>> children) {
		super(data, children);
	}

	public boolean isDecorated() {
		return decorated;
	}

	public void setDecorated(boolean decorated) {
		this.decorated = decorated;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject toJSON(boolean full) {
		JSONObject json = super.toJSON(full);

		if (decorated) {
			JSONObject attr = (JSONObject) json.get("attr");
			if (attr != null) {
				if (attr.get("class") != null) {
					attr.put("class", attr.get("class") + " matching");
				}
			} else {
				attr = new JSONObject();
				attr.put("class", "matching");
				json.put("attr", attr);
			}
		}
		return json;
	}
}
