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

    private Random random = new Random();
    private AnimationTimer timer;

    private Pane root;
    private Scene menuScene;
    private VBox menuBox;
    private Scene gameScene;
    private Scene resetScene;
    private VBox resetv;
    private HBox reseth;

    private final int swidth = 600;
    private final int xorigin = swidth/2;
    private final int sheight = 500;
    private final int yorigin = sheight/2;

    private Text menuText;
    private Button menuButton;

    private double ballxtrans;
    private double ballytrans;
    
    private Rectangle leftrec;
    private Rectangle rightrec;
    private Rectangle top;
    private Rectangle bottom;
    private Rectangle edgeL;
    private Rectangle edgeR;
    private Circle ball;

    private int ballSpeedLim = 3;
    private int ballSpeedfloor = 1;
    private boolean dirIsPositive = true;

    private Text resetText;
    private Button resetButton;
    private Button quitButton;

    public static void main(String[] args) {launch(args);}

    @Override
    public  void start(Stage stage) {
        root = new Pane();
        menu(stage);
        stage.setScene(menuScene);
        stage.show();
        game(stage);  
        resetMenu(stage);       
    }

    private void menu(Stage stage) {
        initMenuLeaves();
        initMenuBranch();
        handleMenuButton(stage);
    }

    private void initMenuLeaves() {
        menuText = new Text("Pong. It's literally just pong.");
        menuButton = new Button("Start");
    }

    private void initMenuBranch() {
        menuBox = new VBox(10, menuText, menuButton);
        menuScene = new Scene(menuBox);
    }

    private void handleMenuButton(Stage stage) {
        menuButton.setOnAction(event -> {stage.setScene(gameScene); timer.start(); changeXDirection();});
    }

    private void game(Stage stage) {
        initGameLeaves();

        initGameBranches();

        setupBall(stage);

        handleInputs();
    }

    private void initGameLeaves() {
        rightrec = makeRectangle(250, 0, 6, 100);
        leftrec = makeRectangle(-250, 0, 6, 100);

        ball = new Circle(xorigin, yorigin, 6);

        top = new Rectangle(0, -3, swidth, 3);
        bottom = new Rectangle(0, sheight+3, swidth, 3);
        edgeL = new Rectangle(-3, 0, 3, sheight);
        edgeR = new Rectangle(swidth+3, 0, 3, sheight);
    }

    private void initGameBranches() {
        root.getChildren().addAll(leftrec, rightrec, ball, top, bottom, edgeL, edgeR);
        gameScene = new Scene(root, swidth, sheight);
    }

    private void setupBall(Stage stage) {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                ball.setTranslateX(ball.getTranslateX() + ballxtrans);
                ball.setTranslateY(ball.getTranslateY() + ballytrans);
                if (isCollision(ball, leftrec, rightrec)) changeXDirection();
                if (isCollision(ball, top, bottom)) changeYDirection();
                if (isCollision(ball, edgeL, edgeR)) {
                    stage.setScene(resetScene);
                }
            }
        };
    }

    private void handleInputs() {
        gameScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.UP && !isCollision(leftrec, top)) {
                leftrec.setTranslateY(leftrec.getTranslateY()-10);
            } else if (event.getCode() == KeyCode.DOWN && !isCollision(leftrec, bottom)) {
                leftrec.setTranslateY(leftrec.getTranslateY()+10);
            } else if (event.getCode() == KeyCode.LEFT && !isCollision(rightrec, top)) {
                rightrec.setTranslateY(rightrec.getTranslateY()-10);
            } else if (event.getCode() == KeyCode.RIGHT && !isCollision(rightrec, bottom)) {
                rightrec.setTranslateY(rightrec.getTranslateY()+10);
            }
        });
    }

    public Rectangle makeRectangle(double posx, double posy, int width, int height) {
        double x = getPosx(posx) - width/2;
        double y = getPosy(posy) - height/2;
        return new Rectangle(x, y, width, height);
    }

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

    public void changeYDirection() {
        ballytrans = -ballytrans;
    }

    private boolean isCollision(Shape prime, Shape shape1) {
        if (prime.getBoundsInParent().intersects(shape1.getBoundsInParent())) {
            return true;
        }
        return false;
    }

    private boolean isCollision(Shape prime, Shape shape1, Shape shape2) {
        if (prime.getBoundsInParent().intersects(shape1.getBoundsInParent()) || prime.getBoundsInParent().intersects(shape2.getBoundsInParent())) {
            return true;
        }
        return false;
    }

    private void resetGame() {     
        rightrec.setTranslateY(0);
        rightrec.setLayoutY(0);
        leftrec.setTranslateY(0);
        leftrec.setLayoutY(0);

        ball.setTranslateX(0);
        ball.setTranslateY(0);
        ball.setLayoutX(0);
        ball.setLayoutY(0);
    }

    public double getPosx(double val) {
        return xorigin - val;
    }

    public double getPosy(double val) {
        return yorigin - val;
    }

    private void initResetLeaves() {
        resetText = new Text("You failed! Try again?");
        resetButton = new Button("Play again?");
        quitButton = new Button("Quit");
    }

    private void initResetBranches() {
        reseth = new HBox(resetButton, quitButton);
        resetv = new VBox(resetText, reseth);
        resetScene = new Scene(resetv);
    }

    private void resetMenu(Stage stage) {
        initResetLeaves();
        initResetBranches();
        handleResetButtons(stage);
    }

    private void handleResetButtons(Stage stage) {
        resetButton.setOnAction(event -> {timer.stop(); resetGame(); stage.setScene(gameScene); timer.start();});
        quitButton.setOnAction(event -> System.exit(0));
    }
}