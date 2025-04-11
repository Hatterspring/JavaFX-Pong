/* Pong.java
 * Author: Aeden Hastie
 * 
 * Description: 
 * This is a simple pong game made by myself as a testing space for javaFX. Built in Java 8, 
 * it is designed to support future versions of java as well.
 *
 * Simply run the batch file corresponding to your OS, and the batch file will add the javaFX SDK to the jar file, 
 * meaning it can run on a more recent version of java.
 *
 * This project also includes the javaFX SDK. I do not own the javaFX SDK in any way, and it is included purely 
 * for the purposes of compiling the .jar file on later versions. More information in SDKs/LICENSE and SDKs/README
 */
package code;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Pong extends Application {

    //miscellanous fields
    private Random random = new Random();
    private AnimationTimer timer;

    //branch nodes
    private Pane root;
    private Scene menuScene;
    private VBox menuBox;
    private Scene gameScene;
    private Scene resetScene;
    private VBox resetv;
    private HBox reseth;

    //scene specifications
    private final int swidth = 600;
    private final int xorigin = swidth/2;
    private final int sheight = 500;
    private final int yorigin = sheight/2;

    //menu leaf nodes
    private Text menuText;
    private Button menuButton;

    //game leaf nodes
    private Rectangle leftrec;
    private Rectangle rightrec;
    private Rectangle top;
    private Rectangle bottom;
    private Rectangle edgeL;
    private Rectangle edgeR;
    private Circle ball;
    private Text scoreboard;

    //ball speed fields
    private double ballxtrans;
    private double ballytrans;
    private int ballSpeedLim = 3;
    private int ballSpeedfloor = 2;
    private boolean dirIsPositive = false;

    //score fields
    private int score = 0;
    private int highScore = 0;

    //reset menu leaf nodes
    private Text resetText;
    private Button resetButton;
    private Button quitButton;

    //main function
    public static void main(String[] args) {launch(args);}

    //run the application
    @Override
    public  void start(Stage stage) {
        root = new Pane();
        menu(stage);
        stage.setScene(menuScene);
        stage.show();
        game(stage);  
        resetMenu(stage);       
    }

    //start menu
    private void menu(Stage stage) {
        initMenuLeaves();
        initMenuBranch();
        handleMenuButton(stage);
    }

    //initialise the menu leaf nodes
    private void initMenuLeaves() {
        menuText = new Text("Pong. It's literally just pong.");
        menuButton = new Button("Start");
    }

    //initialise the menu branch nodes
    private void initMenuBranch() {
        menuBox = new VBox(10, menuText, menuButton);
        menuScene = new Scene(menuBox);
    }

    //if the menu start button is clicked, start the game and display it. Get the ball moving.
    private void handleMenuButton(Stage stage) {
        menuButton.setOnAction(event -> {stage.setScene(gameScene); timer.start(); changeXDirection();});
    }

    //game
    private void game(Stage stage) {
        initGameLeaves();

        initGameBranches();

        setupBall(stage);

        handleInputs();
    }

    //initialise the game leaf nodes
    private void initGameLeaves() {
        rightrec = makeRectangle(250, 0, 6, 100);
        leftrec = makeRectangle(-250, 0, 6, 100);

        ball = new Circle(xorigin, yorigin, 6);

        top = new Rectangle(0, -3, swidth, 3);
        bottom = new Rectangle(0, sheight+3, swidth, 3);
        edgeL = new Rectangle(-3, 0, 3, sheight);
        edgeR = new Rectangle(swidth+3, 0, 3, sheight);

        scoreboard = new Text("SCORE: " + Integer.toString(score));
        scoreboard.setScaleX(3);
        scoreboard.setScaleY(3);
        scoreboard.setLayoutX(xorigin - scoreboard.getBoundsInLocal().getWidth()/2);
        scoreboard.setLayoutY(20);
    }

    //initialise the game branch nodes
    private void initGameBranches() {
        root.getChildren().addAll(leftrec, rightrec, ball, top, bottom, edgeL, edgeR, scoreboard);
        gameScene = new Scene(root, swidth, sheight);
    }

    //the ball uses an animation timer to move independently of user input.
    private void setupBall(Stage stage) {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {

                //ball movement
                ball.setTranslateX(ball.getTranslateX() + ballxtrans);
                ball.setTranslateY(ball.getTranslateY() + ballytrans);

                //collision detected between the ball and a paddle. Increase the score and bounce the ball off the paddle.
                if (isCollision(ball, leftrec, rightrec)) {
                    incrementScore();
                    changeXDirection();
                }

                //collision detected between the ball and the roof or floor. Bounce the ball off it.
                if (isCollision(ball, top, bottom)) changeYDirection();

                //collision detected between the ball and the left or right edge. End the game and display the reset menu.
                if (isCollision(ball, edgeL, edgeR)) {
                    updateResetScore();
                    stage.setScene(resetScene);
                }
            }
        };
    }

    //left paddle controlled by W/S keys, right paddle controlled by UP/DOWN keys.
    private void handleInputs() {
        gameScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.UP && !isCollision(leftrec, top)) {
                leftrec.setTranslateY(leftrec.getTranslateY()-10);
            } else if (event.getCode() == KeyCode.DOWN && !isCollision(leftrec, bottom)) {
                leftrec.setTranslateY(leftrec.getTranslateY()+10);
            } else if (event.getCode() == KeyCode.W && !isCollision(rightrec, top)) {
                rightrec.setTranslateY(rightrec.getTranslateY()-10);
            } else if (event.getCode() == KeyCode.S && !isCollision(rightrec, bottom)) {
                rightrec.setTranslateY(rightrec.getTranslateY()+10);
            }
        });
    }

    //initialise a rectangle using the scene's values
    public Rectangle makeRectangle(double posx, double posy, int width, int height) {
        double x = getPosx(posx) - width/2;
        double y = getPosy(posy) - height/2;
        return new Rectangle(x, y, width, height);
    }

    /*
     * if the ball's direction is positive:
     * - move the ball towards the right with a random x and y speed value, within the given speed limits.
     * - the x value can be slightly higher than the Y value can, in the hopes that the ball will tend towards moving horizontally.
     * if the ball's direction is negative:
     * - ditto, except that the ball moves towards the left.
     */
    public void changeXDirection() {
        ballxtrans = random.nextInt(ballSpeedLim - ballSpeedfloor + 1) + ballSpeedfloor;
        ballytrans = random.nextInt(ballSpeedLim - ballSpeedfloor) + ballSpeedfloor;
        dirIsPositive = !dirIsPositive;
        if (!dirIsPositive) {
            ball.setLayoutX(ball.getLayoutX() - rightrec.getWidth()/2);
            ballxtrans *= -1;
            ballytrans *= -1;
        } else {
            ball.setLayoutX(ball.getLayoutX() + leftrec.getWidth()/2);
        }
    }

    //used for bouncing the ball off the roof or floor.
    public void changeYDirection() {
        ballytrans = -ballytrans;
    }

    //increase the score by one, update the high score if necessary and update the scoreboard on screen.
    private void incrementScore() {
        score++;
        if (highScore < score) {
            highScore = score;
        }
        scoreboard.setText("SCORE: " + Integer.toString(score));
    }

    //detect a collision between a prime object and another object
    private boolean isCollision(Shape prime, Shape shape1) {
        if (prime.getBoundsInParent().intersects(shape1.getBoundsInParent())) {
            return true;
        }
        return false;
    }

    //detect a collision between a prime object and another object, or a prime object and a different object.
    private boolean isCollision(Shape prime, Shape shape1, Shape shape2) {
        if (prime.getBoundsInParent().intersects(shape1.getBoundsInParent()) || prime.getBoundsInParent().intersects(shape2.getBoundsInParent())) {
            return true;
        }
        return false;
    }

    //reset all necessary values, including the position of the paddles, position of the ball, and the current score.
    private void resetGame() {     
        rightrec.setTranslateY(0);
        rightrec.setLayoutY(0);
        leftrec.setTranslateY(0);
        leftrec.setLayoutY(0);

        ball.setTranslateX(0);
        ball.setTranslateY(0);
        ball.setLayoutX(0);
        ball.setLayoutY(0);

        score = 0;
        scoreboard.setText("SCORE: " + Integer.toString(score));
    }

    //get an x position relative to the middle of the scene.
    public double getPosx(double val) {
        return xorigin - val;
    }

    //get a y position relative to the middle of the scene.
    public double getPosy(double val) {
        return yorigin - val;
    }

    //initialise the reset menu leaf nodes
    private void initResetLeaves() {
        resetText = new Text("You failed!\nScore: " + score + "\nHigh score: "+ highScore + "\nTry again?");
        resetButton = new Button("Play again?");
        quitButton = new Button("Quit");
    }

    //initialise the reset menu branch nodes
    private void initResetBranches() {
        reseth = new HBox(20,resetButton, quitButton);
        resetv = new VBox(20,resetText, reseth);
        resetScene = new Scene(resetv);
    }

    //setup the reset menu
    private void resetMenu(Stage stage) {
        initResetLeaves();
        initResetBranches();
        handleResetButtons(stage);
    }

    //if the "Play again?" button is pressed, play the game again. If the "Quit" button is pressed, exit with code 0.
    private void handleResetButtons(Stage stage) {
        resetButton.setOnAction(event -> {timer.stop(); resetGame(); stage.setScene(gameScene); timer.start();});
        quitButton.setOnAction(event -> System.exit(0));
    }

    //update the reset menu's displayed scores.
    private void updateResetScore() {
        resetText.setText("You failed!\nScore: " + score + "\nHigh score: "+ highScore + "\nTry again?");
    }
}