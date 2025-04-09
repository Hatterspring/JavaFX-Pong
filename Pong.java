import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

public class Pong extends Application {

    private Random random = new Random();

    private Pane root;
    private Scene gameScene;

    private final int swidth = 600;
    private final int xorigin = swidth/2;
    private final int sheight = 500;
    private final int yorigin = sheight/2;

    private double ballxtrans;
    private double ballytrans;
    
    private Rectangle leftrec;
    private Rectangle rightrec;
    private Rectangle top;
    private Rectangle bottom;
    private Rectangle edgeL;
    private Rectangle edgeR;
    private Shape ball;

    private int ballSpeedLim = 3;
    private int ballSpeedfloor = 1;
    private boolean dirIsPositive = true;

    public static void main(String[] args) {launch(args);}

    @Override
    public  void start(Stage stage) {
        initGameLeaves();

        initGameBranches();

        setupBall();

        handleInputs();

        stage.setScene(gameScene);
        stage.show();       
    }

    private void initGameLeaves() {
        rightrec = makeRectangle(250, 0, 6, 100);
        leftrec = makeRectangle(-250, 0, 6, 100);

        ball = new Circle(getPosx(0), getPosy(0), 6);
        changeXDirection();

        top = new Rectangle(0, -3, swidth, 3);
        bottom = new Rectangle(0, sheight+3, swidth, 3);
        edgeL = new Rectangle(-3, 0, 3, sheight);
        edgeR = new Rectangle(swidth+3, 0, 3, sheight);
    }

    private void initGameBranches() {
        root = new Pane();
        root.getChildren().addAll(leftrec, rightrec, ball, top, bottom, edgeL, edgeR);
        gameScene = new Scene(root, swidth, sheight);
    }

    private void setupBall() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                ball.setTranslateX(ball.getTranslateX() + ballxtrans);
                ball.setTranslateY(ball.getTranslateY() + ballytrans);
                if (isCollision(ball, leftrec, rightrec)) changeXDirection();
                if (isCollision(ball, top, bottom)) changeYDirection();
                if (isCollision(ball, edgeL, edgeR)) System.exit(0);
            }
        };

        timer.start();
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
            ballxtrans = -ballxtrans;
            ballytrans = -ballytrans;
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

    private void handleSideCollision(Shape shape1, Shape shape2) {
        if (shape1.getBoundsInParent().intersects(shape2.getBoundsInParent())) {
            changeXDirection();
        }
    }

    private void handleTBCollision(Shape shape1, Shape shape2) {
        if (shape1.getBoundsInParent().intersects(shape2.getBoundsInParent())) {
            changeYDirection();
        }
    }

    private void handleEdgeCollision(Shape shape1, Shape shape2) {
        if (shape1.getBoundsInParent().intersects(shape2.getBoundsInParent())) {
            System.exit(0);
        }
    }

    public double getPosx(double val) {
        return xorigin + val;
    }

    public double getPosy(double val) {
        return yorigin + val;
    }
}