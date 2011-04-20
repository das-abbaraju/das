package com.picsauditing.util;

import java.util.List;

import org.json.simple.JSONObject;

import com.picsauditing.jpa.entities.JSONable;

public class Tree<T extends JSONable> implements JSONable {

	private Node<T> root;

	public Tree() {

	}

	public Tree(Node<T> root) {
		this.root = root;
	}

	public Node<T> getRoot() {
		return root;
	}

	public void setRoot(Node<T> root) {
		this.root = root;
	}

	private void walk(Node<T> node, List<Node<T>> list) {
		list.add(node);
		for (Node<T> data : node.getChildren()) {
			walk(data, list);
		}
	}

	@Override
	public void fromJSON(JSONObject o) {

	}

	public JSONObject toJSON() {
		return toJSON(false);
	}

	@Override
	public JSONObject toJSON(boolean full) {
		return root.toJSON(full);
	}

}
