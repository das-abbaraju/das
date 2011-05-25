package com.picsauditing.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
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

	public static <T extends Hierarchical<T>> Tree<T> createTreeFromOrderedList(Collection<T> materializedPath) {
		Map<T, Node<T>> nodes = new HashMap<T, Node<T>>();
		Tree<T> tree = new Tree<T>();
		Node<T> root = new Node<T>();
		tree.setRoot(root);

		if (materializedPath.size() > 0) {
			Iterator<T> iter = materializedPath.iterator();

			nodes.put(iter.next(), root);

			while (iter.hasNext()) {
				T item = iter.next();
				Node<T> node = new Node<T>(item);

				if (nodes.get(item.getParent()) != null && !nodes.get(item.getParent()).getChildren().contains(node)) {
					nodes.get(item.getParent()).addChild(node);
					nodes.put(item, node);
				}
			}
		}

		return tree;
	}

	public static <T extends Hierarchical<T>> Tree<T> createDecoratedTreeFromOrderedList(Collection<T> materializedPath,
			Collection<T> decoratedItems) {
		Map<T, Node<T>> nodes = new HashMap<T, Node<T>>();
		Tree<T> tree = new Tree<T>();
		Node<T> root = new Node<T>();
		tree.setRoot(root);

		if (materializedPath.size() > 0) {
			Iterator<T> iter = materializedPath.iterator();

			nodes.put(iter.next(), root);

			while (iter.hasNext()) {
				T item = iter.next();
				DecoratedNode<T> node = new DecoratedNode<T>(item);
				if (decoratedItems.contains(item))
					node.setDecorated(true);

				if (nodes.get(item.getParent()) != null && !nodes.get(item.getParent()).getChildren().contains(node)) {
					nodes.get(item.getParent()).addChild(node);
					nodes.put(item, node);
				}
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
