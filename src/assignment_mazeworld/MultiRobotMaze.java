package assignment_mazeworld;




import java.util.ArrayList;
import java.util.Arrays;




public class MultiRobotMaze extends InformedSearchProblem {
	
	public static int[] STAY = {0, 0};

	private static int actions[][] = {Maze.NORTH, Maze.EAST, Maze.SOUTH, Maze.WEST, STAY}; 
	
	private int a_xStart, a_yStart, a_xGoal, a_yGoal;
	private int b_xStart, b_yStart, b_xGoal, b_yGoal;
	private int c_xStart, c_yStart, c_xGoal, c_yGoal;

	private Maze maze;
	
	public MultiRobotMaze(Maze m, int a_sx, int a_sy, int a_gx, int a_gy, int b_sx, int b_sy, int b_gx, int b_gy, int c_sx, int c_sy, int c_gx, int c_gy) {
		startNode = new MultiRobotMazeNode(a_sx, a_sy, b_sx, b_sy, c_sx, c_sy, 0, 0);
		
		a_xStart = a_sx;
		a_yStart = a_sy;
		a_xGoal = a_gx;
		a_yGoal = a_gy;
		
		b_xStart = b_sx;
		b_yStart = b_sy;
		b_xGoal = b_gx;
		b_yGoal = b_gy;
		
		c_xStart = c_sx;
		c_yStart = c_sy;
		c_xGoal = c_gx;
		c_yGoal = c_gy;
		
		maze = m;		
	}
	
	// node class used by searches.  Searches themselves are implemented
	//  in SearchProblem.
	public class MultiRobotMazeNode implements SearchNode {

		// location of the agent in the maze
		protected int[] state; 
		
		// how far the current node is from the start.  Not strictly required
		//  for uninformed search, but useful information for debugging, 
		//  and for comparing paths
		private double cost; 
		
		private SearchNode parentNode = null;
		private boolean removed = false;

		public MultiRobotMazeNode(int a_x, int a_y, int b_x, int b_y, int c_x, int c_y, int currentRobot, double c) {
			state = new int[7];
			this.state[0] = a_x;
			this.state[1] = a_y;
			this.state[2] = b_x;
			this.state[3] = b_y;
			this.state[4] = c_x;
			this.state[5] = c_y;
			this.state[6] = currentRobot;
		
			cost = c;
		}
		
		public int[] getX() {
			int[] xValues = {state[0], state[2], state[4]};
			return xValues;
		}
		
		public int[] getY() {
			int[] yValues = {state[1], state[3], state[5]};
			return yValues;
		}

		public ArrayList<SearchNode> getSuccessors() {

			ArrayList<SearchNode> successors = new ArrayList<SearchNode>();

			for (int[] action: actions) {
				int xNew = state[(state[6] * 2)] + action[0];
				int yNew = state[((state[6] * 2) + 1)] + action[1]; 
				
				//System.out.println("testing successor " + xNew + " " + yNew);
				
				if (maze.isLegal(xNew, yNew)) {
					//System.out.println("legal successor found " + " " + xNew + " " + yNew);
					SearchNode succ = null;
					
					if (state[6] == 0) {
						succ = new MultiRobotMazeNode(xNew, yNew, state[2], state[3], state[4], state[5], 1, getCost() + 1.0);
					}
					
					if (state[6] == 1) {
						succ = new MultiRobotMazeNode(state[0], state[1], xNew, yNew, state[4], state[5], 2, getCost() + 1.0);
					}
					
					if (state[6] == 2) {
						succ = new MultiRobotMazeNode(state[0], state[1], state[2], state[3], xNew, yNew, 0, getCost() + 1.0);
					}
					
					if (! succ.robotOverlap()) {
						successors.add(succ);
					}
				}
				
			}
			return successors;

		}
		
		public boolean robotOverlap() {
			return (((state[0] == state[2]) && (state[1] == state[3])) || 
					((state[0] == state[4]) && (state[1] == state[5])) || 
					((state[2] == state[4]) && (state[3] == state[5])));
		}
		
		@Override
		public boolean goalTest() {
			return state[0] == a_xGoal && state[1] == a_yGoal && 
					state[2] == b_xGoal && state[3] == b_yGoal && 
					state[4] == c_xGoal && state[5] == c_yGoal;
		}


		// an equality test is required so that visited sets in searches
		// can check for containment of states
		@Override
		public boolean equals(Object other) {
			return Arrays.equals(state, ((MultiRobotMazeNode) other).state);
		}

		@Override
		public int hashCode() {
			return state[0] * 100000 + state[1] * 10000 + state[2] * 1000 + state[3] * 100 + state[4] * 10 + state[5]; 
		}

		@Override
		public String toString() {
			return new String("Maze state: " + state[0] + ", " + state[1] + ", " + state[2] + ", " + state[3] + ", " + state[4] + ", " + state[5] + " "
					+ " Depth: " + getCost());
		} 

		@Override
		public double getCost() {
			return cost;
		}
		

		@Override
		public double heuristic() {
			// manhattan distance metric for simple maze with one agent:
			double a_dx = a_xGoal - state[0];
			double a_dy = a_yGoal - state[1];
			
			double b_dx = b_xGoal - state[2];
			double b_dy = b_yGoal - state[3];
			
			double c_dx = c_xGoal - state[4];
			double c_dy = c_yGoal - state[5];
			
			return Math.abs(a_dx) + Math.abs(a_dy) + Math.abs(b_dx) + Math.abs(b_dy) + Math.abs(c_dx) + Math.abs(c_dy);
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
