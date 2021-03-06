package assignment_mazeworld;


import java.util.ArrayList;
import java.util.Arrays;


// Find a path for a single agent to get from a start location (xStart, yStart)
//  to a goal location (xGoal, yGoal)

public class SimpleMazeProblem extends InformedSearchProblem {

	private static int actions[][] = {Maze.NORTH, Maze.EAST, Maze.SOUTH, Maze.WEST}; 
	
	private int xStart, yStart, xGoal, yGoal;

	private Maze maze;
	
	public SimpleMazeProblem(Maze m, int sx, int sy, int gx, int gy) {
		startNode = new SimpleMazeNode(sx, sy, 0);
		xStart = sx;
		yStart = sy;
		xGoal = gx;
		yGoal = gy;
		
		maze = m;		
	}
	
	// node class used by searches.  Searches themselves are implemented
	//  in SearchProblem.
	public class SimpleMazeNode implements SearchNode {

		// location of the agent in the maze
		protected int[] state; 
		
		// how far the current node is from the start.  Not strictly required
		//  for uninformed search, but useful information for debugging, 
		//  and for comparing paths
		private double cost; 
		
		private SearchNode parentNode = null;
		private boolean removed = false;

		public SimpleMazeNode(int x, int y, double c) {
			state = new int[2];
			this.state[0] = x;
			this.state[1] = y;
		
			cost = c;

		}
		
		public int getX() {
			return state[0];
		}
		
		public int getY() {
			return state[1];
		}

		public ArrayList<SearchNode> getSuccessors() {

			ArrayList<SearchNode> successors = new ArrayList<SearchNode>();

			for (int[] action: actions) {
				int xNew = state[0] + action[0];
				int yNew = state[1] + action[1]; 
				
				//System.out.println("testing successor " + xNew + " " + yNew);
				
				if(maze.isLegal(xNew, yNew)) {
					//System.out.println("legal successor found " + " " + xNew + " " + yNew);
					SearchNode succ = new SimpleMazeNode(xNew, yNew, getCost() + 1.0);
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
			return state[0] == xGoal && state[1] == yGoal;
		}


		// an equality test is required so that visited sets in searches
		// can check for containment of states
		@Override
		public boolean equals(Object other) {
			return Arrays.equals(state, ((SimpleMazeNode) other).state);
		}

		@Override
		public int hashCode() {
			return state[0] * 100 + state[1]; 
		}

		@Override
		public String toString() {
			return new String("Maze state " + state[0] + ", " + state[1] + " "
					+ " depth " + getCost());
		}

		@Override
		public double getCost() {
			return cost;
		}
		

		@Override
		public double heuristic() {
			// manhattan distance metric for simple maze with one agent:
			double dx = xGoal - state[0];
			double dy = yGoal - state[1];
			return Math.abs(dx) + Math.abs(dy);
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
