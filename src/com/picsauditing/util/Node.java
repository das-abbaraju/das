package com.picsauditing.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.common.base.Objects;
import com.picsauditing.jpa.entities.JSONable;

public class Node<T extends JSONable> implements JSONable {

	private T data;
	private List<Node<T>> children = new ArrayList<Node<T>>();

	public Node() {
	}

	public Node(T data) {
		this.data = data;
	}

	public Node(T data, Node<T> child) {
		this.data = data;
		this.addChild(child);
	}

	public Node(T data, Collection<Node<T>> children) {
		this.data = data;
		this.children = new ArrayList<Node<T>>(children);
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public List<Node<T>> getChildren() {
		return children;
	}

	public void setChildren(List<Node<T>> children) {
		this.children = children;
	}

	public void addChild(Node<T> node) {
		this.children.add(node);
	}

	@Override
	public void fromJSON(JSONObject o) {

	}

	public JSONObject toJSON() {
		return toJSON(false);
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject toJSON(boolean full) {
		JSONObject json = new JSONObject();

		if (data != null) {
			json = data.toJSON(full);
		}

		JSONArray children = new JSONArray();
		for (Node<T> child : this.children) {
			children.add(child.toJSON(full));
		}
		json.put("children", children);

		return json;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Node) {
			Node<T> other = (Node<T>) obj;
			return Objects.equal(this, other.getData());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(data);
	}

	@Override
	public String toString() {
		return String.valueOf(data);
	}
}
