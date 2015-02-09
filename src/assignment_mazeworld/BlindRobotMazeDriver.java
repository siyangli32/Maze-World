package assignment_mazeworld;

import java.util.*;
import javafx.scene.shape.Circle;

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
import assignment_mazeworld.BlindRobotMaze.BlindRobotMazeNode;

public class BlindRobotMazeDriver extends Application {

	Maze maze;
	
	// instance variables used for graphical display
	private static final int PIXELS_PER_SQUARE = 32;
	MazeView mazeView;
	List<AnimationPath> animationPathList;
	
	// some basic initialization of the graphics; needs to be done before 
	//  runSearches, so that the mazeView is available
	private void initMazeView() {
		maze = Maze.readFromFile("simpler.maz");
		
		animationPathList = new ArrayList<AnimationPath>();
		// build the board
		mazeView = new MazeView(maze, PIXELS_PER_SQUARE);
		
	}
	
	// assumes maze and mazeView instance variables are already available
	private void runSearches() {
		int gx = 3;
		int gy = 3;

		BlindRobotMaze mazeProblem = new BlindRobotMaze(maze, gx, gy);

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
		private ArrayList<Node> pieceList;
	
		private List<SearchNode> searchPath;
		private int currentMove = 0;

		boolean animationDone = true;

		public AnimationPath(MazeView mazeView, List<SearchNode> path) {
			searchPath = path;
			BlindRobotMazeNode firstNode = (BlindRobotMazeNode) searchPath.get(0);
			
			pieceList = mazeView.changeBeliefState(firstNode.getCoordinates());
			}	

		// try to do the next step of the animation. Do nothing if
		// the mazeView is not ready for another step.
		public void doNextMove() {

			// animationDone is an instance variable that is updated
			//  using a callback triggered when the current animation
			//  is complete
			if (currentMove < searchPath.size() && animationDone) {
				BlindRobotMazeNode mazeNode = (BlindRobotMazeNode) searchPath
						.get(currentMove);
	
				mazeView.getChildren().removeAll(pieceList);
				pieceList = mazeView.changeBeliefState(mazeNode.getCoordinates());
				}
	}
}
}