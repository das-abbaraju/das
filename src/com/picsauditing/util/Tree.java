package com.picsauditing.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.google.common.base.Objects;
import com.picsauditing.jpa.entities.JSONable;

public class Tree<T extends Hierarchical<T>> implements JSONable {

	private Node<T> root;

	public Tree() {

	}

	public Tree(Node<T> root) {
		this.root = root;
	}

	public static <T extends Hierarchical<T>> Tree<T> createTreeFromOrderedList(Collection<T> treeList) {
		Map<T, Node<T>> nodes = new HashMap<T, Node<T>>();
		Tree<T> tree = new Tree<T>();
		Node<T> root = new Node<T>();
		tree.setRoot(root);
		nodes.put(null, root);

		for (T trade : treeList) {
			Node<T> node = new Node<T>(trade);

			if (nodes.get(trade.getParent()) != null && !nodes.get(trade.getParent()).getChildren().contains(node)) {
				nodes.get(trade.getParent()).addChild(node);
				nodes.put(trade, node);
			}
		}
		return tree;
	}

	public static <T extends Hierarchical<T>> Tree<T> createDecoratedTreeFromOrderedList(Collection<T> treeList,
			Collection<T> decoratedItems) {
		Map<T, Node<T>> nodes = new HashMap<T, Node<T>>();
		Tree<T> tree = new Tree<T>();
		Node<T> root = new Node<T>();
		tree.setRoot(root);
		nodes.put(null, root);

		for (T trade : treeList) {
			DecoratedNode<T> node = new DecoratedNode<T>(trade);
			if (decoratedItems.contains(trade))
				node.setDecorated(true);

			if (nodes.get(trade.getParent()) != null && !nodes.get(trade.getParent()).getChildren().contains(node)) {
				nodes.get(trade.getParent()).addChild(node);
				nodes.put(trade, node);
			}
		}
		return tree;
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

	public boolean contains(T data) {
		return contains(root, data);
	}

	public boolean contains(Node<T> node, T data) {
		if (Objects.equal(data, node.getData()))
			return true;
		for (Node<T> child : node.getChildren()) {
			if (contains(child, data))
				return true;
		}

		return false;
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
