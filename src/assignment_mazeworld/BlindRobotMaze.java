package assignment_mazeworld;

import java.util.*;


// Find a path for a single agent to get from a start location (xStart, yStart)
//  to a goal location (xGoal, yGoal)

public class BlindRobotMaze extends InformedSearchProblem {

	private static int actions[][] = {Maze.NORTH, Maze.EAST, Maze.SOUTH, Maze.WEST}; 
	
	private int[] goalPosition = new int[2];

	private Maze maze;
	
	public BlindRobotMaze(Maze m, int goalX, int goalY) {
		HashSet<int[]> startState = new HashSet<int[]>();
		maze = m;
		goalPosition[0] = goalX;
		goalPosition[1] = goalY;
		
		for (int i = 0; i < maze.height; i++) {
			for (int j = 0; j < maze.width; j++) {
				if (maze.isLegal(j, i)) {
					int[] possiblePosition = {j, i};
					startState.add(possiblePosition);
				}
			}
		}

		startNode = new BlindRobotMazeNode(startState, 0);
		
		maze = m;		
	}
	
	// node class used by searches.  Searches themselves are implemented
	//  in SearchProblem.
	public class BlindRobotMazeNode implements SearchNode {

		// location of the agent in the maze
		protected HashSet<int[]> state; 
		
		// how far the current node is from the start.  Not strictly required
		//  for uninformed search, but useful information for debugging, 
		//  and for comparing paths
		private double cost; 
		
		private SearchNode parentNode = null;
		private boolean removed = false;

		public BlindRobotMazeNode(HashSet<int[]> possibleStates, double c) {
			state = possibleStates;
			cost = c;
		}
		
		public HashSet<int[]> getCoordinates() {
			return state;
		}

		public ArrayList<SearchNode> getSuccessors() {
			
			ArrayList<SearchNode> successors = new ArrayList<SearchNode>();

			for (int[] action: actions) {
				Iterator<int[]> stateIterator = state.iterator();
				HashSet<int[]> newState = new HashSet<int[]>();
				
				while (stateIterator.hasNext()) {
					int[] possibleState = stateIterator.next();
					
					int newX = possibleState[0] + action[0];
					int newY = possibleState[1] + action[1];
					
					int[] newPosition = {newX, newY};
					
					if (maze.isLegal(newX, newY)) {
						Iterator<int[]> newStateIterator = newState.iterator();
						int[] newPotentialPosition;
						boolean inNewState = false;
						
						while (newStateIterator.hasNext()) {
							newPotentialPosition = newStateIterator.next();
							if ((newPotentialPosition[0] == newPosition[0]) && (newPotentialPosition[1] == newPosition[1])) {
								inNewState = true;
							}
						}
						
						if (! inNewState) {
							newState.add(newPosition);
						}

					}
					
					if (! maze.isLegal(newX, newY)) {
						Iterator<int[]> newStateIterator = newState.iterator();
						int[] newPotentialPosition;
						boolean inNewState = false;
						
						while (newStateIterator.hasNext()) {
							newPotentialPosition = newStateIterator.next();
							if ((newPotentialPosition[0] == possibleState[0]) && (newPotentialPosition[1] == possibleState[1])) {
								inNewState = true;
							}
						}
						
						if (! inNewState) {	
							newState.add(possibleState);
						}
					}
				}
			
					
				if (! newState.isEmpty()) {
					SearchNode succ = new BlindRobotMazeNode(newState, getCost() + 1.0);
					successors.add(succ);
				}

			}
		
			return successors;
			
		}
		
		public boolean robotOverlap() {
			return false;
		}
		
		@Override
		public boolean goalTest() {
			if (state.size() == 1) {
				Iterator<int[]> stateIterator = state.iterator();
				int[] position = stateIterator.next();
				
				return ((position[0] == goalPosition[0]) && (position[1] == goalPosition[1]));	
			}
			
			else {
				return false;
			}
		}


		// an equality test is required so that visited sets in searches
		// can check for containment of states
		@Override
		public boolean equals(Object other) {
			return (state == ((BlindRobotMazeNode) other).state);
		}

		@Override
		public int hashCode() {
			int hashcode = 0;
			int multiplier = 2;
			
			for (int i = 0; i < maze.height; i++) {
				for (int j = 0; j < maze.width; j++) {
					int[] possibleLocation = {j, i};
					if (state.contains(possibleLocation)) {
						hashcode = hashcode + (multiplier);
					}
					multiplier = multiplier * 2;
				}
			}
			return hashcode;
		}

		@Override
		public String toString() {
			Iterator<int[]> stateIterator = state.iterator();
			int[] possibleState;
			
			String toString = new String("State: ");
			
			while (stateIterator.hasNext()) {
				possibleState = stateIterator.next();
				toString += "(" + possibleState[0] + ", " + possibleState[1] + ") ";
			}
			
			toString += "  Depth: " + getCost();
			
			return toString;
		}

		@Override
		public double getCost() {
			return cost;
		}
		

		@Override
		public double heuristic() {
			double heuristicValue = 999999;
			
			if (state.size() > 1) {
				Iterator<int[]> stateIterator = state.iterator();
				int minX = 999999, minY = 999999;
				int maxX = 0, maxY = 0;
				int[] possibleState;
				
				while (stateIterator.hasNext()) {
					possibleState = stateIterator.next();
					
					if (possibleState[0] < minX) {
						minX = possibleState[0];
					}
					if (possibleState[0] > maxX) {
						maxX = possibleState[0];
					}
					if (possibleState[1] < minY) {
						minY = possibleState[1];
					}
					if (possibleState[1] > maxY) {
						maxY = possibleState[1];
					}
				}
				
				heuristicValue = ((double) (maxX - minX) + (maxY + minY));
				
				}
			
			else {
				Iterator<int[]> stateIterator = state.iterator();
				int[] position;
				
				while (stateIterator.hasNext()) {
					position = stateIterator.next();
					
					double dx = goalPosition[0] - position[0];
					double dy = goalPosition[1] - position[1];
					heuristicValue = ((double) (Math.abs(dx) + Math.abs(dy)));
				}
			}
			return heuristicValue;
			
		}

		@Override
		public int compareTo(SearchNode o) {
			return (int) Math.signum(priority() - o.priority());
		}
		
		@Override
		public double priority() {
			return heuristic() + getCost();
		}
		
		public void addParent(SearchNode newParent) {
			parentNode = newParent;
		}
		
		
		public SearchNode returnParent() {
			return parentNode;
		}
		
		public void markRemoved() {
			removed = true;
		}
		
		public boolean isRemoved() {
			return removed;
		}

	}
}