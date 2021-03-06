package lab.algorithms;

import lab.ds.*;
import lab.utils.ConsumerWithException;
import lab.utils.GraphUtils;

import java.util.*;
import java.util.function.Consumer;

public class MinimumSpanningTree {

	public static void boruvka(Graph graph, ConsumerWithException<Edge> onVisitedEdge,
			ConsumerWithException<Edge> onFoundEdge, Boolean isCanceled) throws Exception {

		DisjointSets sets = new DisjointSets(graph.getNodes());

		// sort all edges
		List<Edge> sortedEdges = new ArrayList<>(graph.getEdges());
		java.util.Collections.sort(sortedEdges, (e1, e2) -> Integer.compare(e1.getCost(), e2.getCost()));

		while (sets.size() > 1) {
			Iterator<DisjointSets.DisjointSet> setsIterator = sets.get().iterator();
			while (setsIterator.hasNext()) {

				TreeSet<Edge> edges = new TreeSet<>();
				DisjointSets.DisjointSet set = setsIterator.next();

				for (Node node : set.getNodes()) {
					Optional<Edge> closestEdgeForNode = node.getEdges().stream()
							.filter(edge -> !set.isNodeInSet(edge.getDestination()))
							.min((e1, e2) -> Integer.compare(e1.getCost(), e2.getCost()));
					if (closestEdgeForNode.isPresent()) {
						onVisitedEdge.accept(closestEdgeForNode.get());
						edges.add(closestEdgeForNode.get());
					}
				}

				Iterator<Edge> edgeIterator = edges.iterator();
				if (edgeIterator.hasNext()) {
					Edge closestEdge = edgeIterator.next();
					onFoundEdge.accept(closestEdge);
					sets.merge(closestEdge.getSource(), closestEdge.getDestination(), false);
					setsIterator.remove();
				}

				if (isCanceled) {
					return;
				}
			}
		}
	}

	public static void prim(Graph graph, Consumer<Node> onVisitedNode, Consumer<Node> onProcessedNode,
			ConsumerWithException<Edge> onVisitedEdge, ConsumerWithException<Edge> onFoundEdge, Boolean isCanceled)
			throws Exception {

		Set<Node> unvisitedNodes = new HashSet<>();
		graph.getNodes().stream().filter(node -> !node.isStartNode()).forEach(unvisitedNodes::add);
		Queue<Edge> availableEdges = new PriorityQueue<>((e1, e2) -> Integer.compare(e1.getCost(), e2.getCost()));

		Node currentNode = GraphUtils.getStartingNode(graph);
		while (!unvisitedNodes.isEmpty()) {

			onVisitedNode.accept(currentNode);
			unvisitedNodes.remove(currentNode);

			// adds to the PQ all the edges of the new node
			currentNode.getEdges().stream().filter(edge -> unvisitedNodes.contains(edge.getDestination()))
					.forEach(edge -> {
						availableEdges.add(edge);
						try {
							onVisitedEdge.accept(edge);
						} catch (Exception e) {
							e.printStackTrace();
						}
					});

			// currentNode is now processed
			currentNode.setStatus(NodeStatus.PROCESSED);
			onProcessedNode.accept(currentNode);

			Edge closestEdge = availableEdges.poll();
			if (closestEdge != null) {

				onFoundEdge.accept(closestEdge);

				// sets the destination as part of the MST
				closestEdge.getDestination().setPathParent(closestEdge.getSource());

				// removes other edges pointing to the destination of the closest edge
				availableEdges.removeIf(edge -> edge.getDestination().equals(closestEdge.getDestination()));
				currentNode = closestEdge.getDestination();
			}

			if (isCanceled) {
				return;
			}
		}
	}

	public static void kruskal(Graph graph, ConsumerWithException<Edge> onVisitedEdge,
			ConsumerWithException<Edge> onFoundEdge) throws Exception {

		DisjointSets sets = new DisjointSets(graph.getNodes());

		// sort all edges
		List<Edge> sortedEdges = new ArrayList<>(graph.getEdges());
		java.util.Collections.sort(sortedEdges, (e1, e2) -> Integer.compare(e1.getCost(), e2.getCost()));

		// greedy takes the minimum cost edge
		for (Edge edge : sortedEdges) {
			onVisitedEdge.accept(edge);
			if (sets.nodesAreInDifferentSets(edge.getSource(), edge.getDestination())) {
				onFoundEdge.accept(edge);
				sets.merge(sets.find(edge.getDestination()), sets.find(edge.getSource()), true);
				edge.getDestination().setPathParent(edge.getSource());
			}
		}
	}
}