package assignment_mazeworld;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import assignment_mazeworld.SearchProblem.SearchNode;
import assignment_mazeworld.MultiRobotMaze.MultiRobotMazeNode;

public class MultiRobotMazeDriver extends Application {

	Maze maze;
	
	// instance variables used for graphical display
	private static final int PIXELS_PER_SQUARE = 32;
	MazeView mazeView;
	List<AnimationPath> animationPathList;
	
	// some basic initialization of the graphics; needs to be done before 
	//  runSearches, so that the mazeView is available
	private void initMazeView() {
		maze = Maze.readFromFile("simple.maz");
		
		animationPathList = new ArrayList<AnimationPath>();
		// build the board
		mazeView = new MazeView(maze, PIXELS_PER_SQUARE);
		
	}
	
	// assumes maze and mazeView instance variables are already available
	private void runSearches() {
		
		int aSx = 0;
		int aSy = 0;
		int bSx = 1;
		int bSy = 0;
		int cSx = 2;
		int cSy = 0;
		
		int aGx = 6;
		int aGy = 2;
		int bGx = 6;
		int bGy = 1;
		int cGx = 6;
		int cGy = 0;

		MultiRobotMaze mazeProblem = new MultiRobotMaze(maze, aSx, aSy, aGx, aGy, bSx, bSy, bGx, bGy, cSx, cSy, cGx, cGy);

/*
		List<SearchNode> bfsPath = mazeProblem.breadthFirstSearch();
		animationPathList.add(new AnimationPath(mazeView, bfsPath));
		System.out.println("DFS:  ");
		mazeProblem.printStats();

		List<SearchNode> dfsPath = mazeProblem
				.depthFirstPathCheckingSearch(5000);
		animationPathList.add(new AnimationPath(mazeView, dfsPath));
		System.out.println("BFS:  ");
		
		mazeProblem.printStats();
*/

		List<SearchNode> astarPath = mazeProblem.aStarSearch();
		animationPathList.add(new AnimationPath(mazeView, astarPath));
		System.out.println("A*:  ");
		mazeProblem.printStats();
		
	}


	public static void main(String[] args) {
		launch(args);
	}

	// javafx setup of main view window for mazeworld
	@Override
	public void start(Stage primaryStage) {
		
		initMazeView();
	
		primaryStage.setTitle("CS 76 Mazeworld");

		// add everything to a root stackpane, and then to the main window
		StackPane root = new StackPane();
		root.getChildren().add(mazeView);
		primaryStage.setScene(new Scene(root));

		primaryStage.show();

		// do the real work of the driver; run search tests
		runSearches();

		// sets mazeworld's game loop (a javafx Timeline)
		Timeline timeline = new Timeline(1.0);
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.getKeyFrames().add(
				new KeyFrame(Duration.seconds(.05), new GameHandler()));
		timeline.playFromStart();

	}

	// every frame, this method gets called and tries to do the next move
	//  for each animationPath.
	private class GameHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent e) {
			// System.out.println("timer fired");
			for (AnimationPath animationPath : animationPathList) {
				// note:  animationPath.doNextMove() does nothing if the
				//  previous animation is not complete.  If previous is complete,
				//  then a new animation of a piece is started.
				animationPath.doNextMove();
			}
		}
	}

	// each animation path needs to keep track of some information:
	// the underlying search path, the "piece" object used for animation,
	// etc.
	private class AnimationPath {
		private Node pieceA;
		private Node pieceB;
		private Node pieceC;
	
		private List<SearchNode> searchPath;
		private int currentMove = 0;

		private int lastAX;
		private int lastAY;
		private int lastBX;
		private int lastBY;
		private int lastCX;
		private int lastCY;

		boolean animationDone = true;

		public AnimationPath(MazeView mazeView, List<SearchNode> path) {
			searchPath = path;
			MultiRobotMazeNode firstNode = (MultiRobotMazeNode) searchPath.get(0);
			
			
			pieceA = mazeView.addPiece(firstNode.getX()[0], firstNode.getY()[0]);
			pieceB = mazeView.addPiece(firstNode.getX()[1], firstNode.getY()[1]);
			pieceC = mazeView.addPiece(firstNode.getX()[2], firstNode.getY()[2]);
			
			lastAX = firstNode.getX()[0];
			lastAY = firstNode.getY()[0];
			lastBX = firstNode.getX()[1];
			lastBY = firstNode.getY()[1];
			lastCX = firstNode.getX()[2];
			lastCY = firstNode.getY()[2];	
		}

		// try to do the next step of the animation. Do nothing if
		// the mazeView is not ready for another step.
		public void doNextMove() {

			// animationDone is an instance variable that is updated
			//  using a callback triggered when the current animation
			//  is complete
			if (currentMove < searchPath.size() && animationDone) {
				MultiRobotMazeNode mazeNode = (MultiRobotMazeNode) searchPath
						.get(currentMove);
				int aDx = mazeNode.getX()[0] - lastAX;
				int aDy = mazeNode.getY()[0] - lastAY;
				int bDx = mazeNode.getX()[1] - lastBX;
				int bDy = mazeNode.getY()[1] - lastBY;
				int cDx = mazeNode.getX()[2] - lastCX;
				int cDy = mazeNode.getY()[2] - lastCY;
				// System.out.println("animating " + dx + " " + dy);
				animateMove(pieceA, aDx, aDy);
				animateMove(pieceB, bDx, bDy);
				animateMove(pieceC, cDx, cDy);
				
				lastAX = mazeNode.getX()[0];
				lastAY = mazeNode.getY()[0];
				lastBX = mazeNode.getX()[1];
				lastBY = mazeNode.getY()[1];
				lastCX = mazeNode.getX()[2];
				lastCY = mazeNode.getY()[2];

				currentMove++;
			}

		}

		// move the piece n by dx, dy cells
		public void animateMove(Node n, int dx, int dy) {
			animationDone = false;
			TranslateTransition tt = new TranslateTransition(
					Duration.millis(300), n);
			tt.setByX(PIXELS_PER_SQUARE * dx);
			tt.setByY(-PIXELS_PER_SQUARE * dy);
			// set a callback to trigger when animation is finished
			tt.setOnFinished(new AnimationFinished());

			tt.play();

		}

		// when the animation is finished, set an instance variable flag
		//  that is used to see if the path is ready for the next step in the
		//  animation
		private class AnimationFinished implements EventHandler<ActionEvent> {
			@Override
			public void handle(ActionEvent event) {
				animationDone = true;
			}
		}
	}
}