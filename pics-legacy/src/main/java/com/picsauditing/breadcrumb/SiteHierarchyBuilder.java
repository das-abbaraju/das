package com.picsauditing.breadcrumb;

import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SiteHierarchyBuilder {
	private static final Logger LOG = LoggerFactory.getLogger(SiteHierarchyBuilder.class);
	private static final StopWatch STOPWATCH = new Slf4JStopWatch(LoggerFactory.getLogger("org.perf4j.DebugTimingLogger"));

	private static final String BREADCRUMB_NODE = "breadcrumb";
	private static final String BREADCRUMB_CHILD_NODE = "children";
	private static final String HINT_NODE = "hint";
	private static final String LABEL_NODE = "label";
	private static final String URI_NODE = "uri";

	// For testing
	private Document document;

	public SiteHierarchyDirectedTree getRoot() throws Exception {
		STOPWATCH.start("SiteHierarchyBuilder.getRoot");

		VisitableNode root = new VisitableNode();
		Element documentElement = document().getDocumentElement();
		documentElement.normalize(); // <breadcrumbs>

		for (Node node : extractElementNodes(documentElement.getChildNodes())) {
			if (BREADCRUMB_NODE.equals(node.getNodeName())) {
				root = buildVisitableNode(node, null);
			}
		}

		STOPWATCH.stop("SiteHierarchyBuilder.getRoot");
		return new SiteHierarchyDirectedTree(root);
	}

	private List<Node> extractElementNodes(NodeList nodes) {
		List<Node> elementNodes = new ArrayList<>();

		if (nodes != null) {
			for (int index = 0; index < nodes.getLength(); index++) {
				Node node = nodes.item(index);

				if (node.getNodeType() == Node.ELEMENT_NODE) {
					elementNodes.add(node);
				}
			}
		}

		return elementNodes;
	}

	private VisitableNode buildVisitableNode(Node node, VisitableNode parent) {
		VisitableNode.Builder builder = new VisitableNode.Builder();
		builder.parent(parent);

		List<Node> childElements = extractElementNodes(node.getChildNodes());
		List<Node> visitableNodeChildren = new ArrayList<>();

		for (Node child : childElements) {
			String nodeValue = child.getFirstChild().getNodeValue();
			switch (child.getNodeName()) {
				case HINT_NODE:
					builder.hint(nodeValue);
					break;
				case LABEL_NODE:
					builder.label(nodeValue);
					break;
				case URI_NODE:
					builder.uri(nodeValue);
					break;
				case BREADCRUMB_CHILD_NODE:
					visitableNodeChildren.addAll(extractElementNodes(child.getChildNodes()));
					break;
				default:
					throw new RuntimeException("Could not parse XML configuration for hierarchy.");
			}
		}

		VisitableNode current = builder.build();
		for (Node child : visitableNodeChildren) {
			VisitableNode visitableChild = buildVisitableNode(child, current);
			current.getChildren().put(visitableChild.getHint(), visitableChild);
		}

		return current;
	}

	private Document document() throws Exception {
		InputStream resourceStream = null;

		try {
			if (document == null) {
				DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				resourceStream = SiteHierarchyBuilder.class.getResourceAsStream("breadcrumbs.xml");
				document = documentBuilder.parse(resourceStream);
			}
		} catch (Exception exception) {
			LOG.error("Could not open resource stream", exception);
		} finally {
			try {
				if (resourceStream != null) {
					resourceStream.close();
				}
			} catch (Exception exception) {
				LOG.error("Could not close resource stream", exception);
			}
		}

		return document;
	}
}
