import java.util.Random;

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

    private int ballSpeedLim = 10;
    private int ballSpeedfloor = 5;
    private boolean dirIsPositive = true;

    public static void main(String[] args) {launch(args);}

    @Override
    public  void start(Stage stage) {
        rightrec = makeRectangle(250, 0, 6, 100);
        leftrec = makeRectangle(-250, 0, 6, 100);

        ball = new Circle(getPosx(0), getPosy(0), 6);

        top = new Rectangle(0, -3, swidth, 3);
        bottom = new Rectangle(0, sheight+2, swidth, 3);
        edgeL = new Rectangle(-3, 0, 3, sheight);
        edgeR = new Rectangle(swidth+3, 0, 3, sheight);

        root = new Pane();
        root.getChildren().addAll(leftrec, rightrec, ball, top, bottom, edgeL, edgeR);

        changeXDirection();
        
        Scene scene = new Scene(root, swidth, sheight);

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.UP) {
                leftrec.setTranslateY(leftrec.getTranslateY()-5);
            } else if (event.getCode() == KeyCode.DOWN) {
                leftrec.setTranslateY(leftrec.getTranslateY()+5);
            } else if (event.getCode() == KeyCode.LEFT) {
                rightrec.setTranslateY(rightrec.getTranslateY()-5);
            } else if (event.getCode() == KeyCode.RIGHT) {
                rightrec.setTranslateY(rightrec.getTranslateY()+5);
            }
            if (event.getCode() == KeyCode.X) {
                ball.setTranslateX(ball.getTranslateX() + ballxtrans);
                ball.setTranslateY(ball.getTranslateY() + ballytrans);
                checkForSideCollision(leftrec, ball);
                checkForSideCollision(rightrec, ball);
                checkForTBCollision(top, ball);
                checkForTBCollision(bottom, ball);
                checkForEdgeCollision(edgeL, ball);
                checkForEdgeCollision(edgeR, ball);
            }
        });

        stage.setScene(scene);
        stage.show();       
    }

    public Rectangle makeRectangle(double posx, double posy, int width, int height) {
        double x = getPosx(posx) - width/2;
        double y = getPosy(posy) - height/2;
        return new Rectangle(x, y, width, height);
    }

    public void changeXDirection() {
        ballxtrans = random.nextInt(ballSpeedLim - ballSpeedfloor + 1) + ballSpeedfloor;
        ballytrans = random.nextInt((int)ballxtrans);
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

    private void checkForSideCollision(Shape shape1, Shape shape2) {
        if (shape1.getBoundsInParent().intersects(shape2.getBoundsInParent())) {
            changeXDirection();
        }
    }

    private void checkForTBCollision(Shape shape1, Shape shape2) {
        if (shape1.getBoundsInParent().intersects(shape2.getBoundsInParent())) {
            changeYDirection();
        }
    }

    private void checkForEdgeCollision(Shape shape1, Shape shape2) {
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