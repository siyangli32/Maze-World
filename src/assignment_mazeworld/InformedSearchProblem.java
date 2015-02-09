package assignment_mazeworld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.*;

import assignment_mazeworld.SearchProblem.SearchNode;

public class InformedSearchProblem extends SearchProblem {
	
	public List<SearchNode> aStarSearch() {
		resetStats();
		
		SearchNode currentNode = startNode;
		ArrayList<SearchNode> successorsArray;
		PriorityQueue<SearchNode> fringe = new PriorityQueue<SearchNode>();
		HashMap<SearchNode, SearchNode> visitedMap = new HashMap<SearchNode, SearchNode>();
		
		visitedMap.put(startNode, startNode);
		incrementNodeCount();
		
		while (! currentNode.goalTest()) {
			successorsArray = currentNode.getSuccessors();
			
			for (SearchNode successor: successorsArray) {
				if (! visitedMap.containsKey(successor)) {
					successor.addParent(currentNode);
					fringe.add(successor);
					visitedMap.put(successor, successor);
					updateMemory(visitedMap.size());
				}
				
				if ((visitedMap.containsKey(successor)) && ((successor.compareTo(visitedMap.get(successor))) == -1)) {
					visitedMap.get(successor).markRemoved();
					fringe.add(successor);
					visitedMap.put(successor, successor);
					}
				}
			
			currentNode = fringe.poll();
			
			while (currentNode.isRemoved()) {
				currentNode = fringe.poll();
			}
			
			incrementNodeCount();
		}
	
			return aStarBackchain(currentNode);
}

	protected List<SearchNode> aStarBackchain(SearchNode currentNode) {

		LinkedList<SearchNode> backchainList = new LinkedList<SearchNode>();

		while (currentNode.returnParent() != null) {
			backchainList.addFirst(currentNode);
			currentNode = currentNode.returnParent();
		}
		
		return backchainList;
	}
	
}
