import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
public class Main extends Application {
    private static double angle = Math.random() * Math.PI / 2 - Math.PI / 4;   //Ball angle
    private static double vi = 7; //Ball incipient velocity
    private static double v = vi; //Ball velocity
    private static double vX;   //Ball X velocity
    private static double vY;   //Ball Y velocity
    private static double DELTA_PLAYER = 5;   //player velocity
    private boolean keyW = false;     //W pressed -> true
    private boolean keyS = false;     //S pressed -> true
    private boolean keyUP = false;    //UP pressed -> true
    private boolean keyDOWN = false;  //DOWN pressed -> true
    private int counterStroke = 0;    //Counter stroke
    private int scorePlayer1 = 0;         //Player1 score
    private int scorePlayer2 = 0;         //Player2 score
    private boolean stopBoolean = true;   //Game is stop -> true

    @Override
    public void start(Stage stage) throws Exception {
        updateV(v, angle);          //updateV
        double width = 1200;        //Window width
        double height = 800;          //Window height
        Group root = new Group();
        Scene scene = new Scene(root, width, height, Color.rgb(0, 0, 10));
        stage.setScene(scene);
        stage.setTitle("Pong");     //Window title
        stage.setResizable(false);
        stage.show();

        Rectangle player1 = new Rectangle(10, 160);   //Player1 (left)
        player1.setFill(Color.RED);
        player1.setX(20);
        player1.setY(height / 2 - player1.getHeight() / 2);

        Rectangle player2 = new Rectangle(10, 160);   //Player2 (right)
        player2.setFill(Color.RED);
        player2.setX(width - 30);
        player2.setY(height / 2 - player2.getHeight() / 2);

        Line linePlayer1 = new Line(30, 0, 30, height);               //border
        linePlayer1.setStroke(Color.rgb(190, 190, 190));
        Line linePlayer2 = new Line(width - 30, 0, width - 30, height);   //border
        linePlayer2.setStroke(Color.rgb(190, 190, 190));

        Circle ball = new Circle(15);                      //Ball
        ball.setFill(Color.GREENYELLOW);
        ball.setCenterX(width / 4);
        ball.setCenterY(height / 2);

        Text scores = new Text();                                  //Scores
        scores.setText(updateScoresText());                       //Update scores
        scores.setFill(Color.RED);
        scores.setX(width / 2 - 120);
        scores.setY(20);
        scores.setFont(Font.font("bahnschrift", FontWeight.LIGHT, 20));

        Text startText = new Text();                               //Start text
        startText.setText("Player1 : up=W & down=S\nPlayer2 : up=UP & down=DOWN\nAfter every 10 shots, the ball speed increases\n\n\nPress Enter Key To Start Game");
        startText.setFill(Color.RED);
        startText.setX(width / 2 - 120);
        startText.setY(60);
        startText.setFont(Font.font("bahnschrift", FontWeight.LIGHT, 20));

        Text stopText = new Text();                                //Stop text
        stopText.setText("Stop=esc");
        stopText.setFill(Color.RED);
        stopText.setX(width / 2 - 20);
        stopText.setY(height - 10);
        stopText.setFont(Font.font("bahnschrift", FontWeight.LIGHT, 14));

        root.getChildren().addAll(player1, player2, ball, scores, linePlayer1, linePlayer2, startText); //Add all elements
        AnimationTimer game = new AnimationTimer() {
            public void handle(long currentNanoTime) {

                //Ball velocity increase after every 10 stroke
                if (counterStroke >= 10) {
                    v += 2;
                    counterStroke = 0;
                }

                ball.setCenterX(ball.getCenterX() + vX);
                ball.setCenterY(ball.getCenterY() + vY);

                //If ball hits the wall up or down -> ball Y velocity is mirrored
                if (ball.getCenterY() - ball.getRadius() < 0 || ball.getCenterY() + ball.getRadius() > height)
                    symmetryY();

                //If ball hits the right border
                if (ball.getCenterX() + ball.getRadius() > width - 30) {

                    //If ball hits the Player2
                    if (ball.getCenterY() > player2.getY() && ball.getCenterY() < player2.getY() + player2.getHeight()) {
                        counterStroke += 1;

                        //Make new angle
                        double p = ball.getCenterY() - player2.getY() - player2.getHeight() / 2;
                        angle = Math.PI * (1 - p / (2 * player2.getHeight())) - Math.abs(angle);
                        normalizeAngle();

                        //the new angle should be between 3pi/4 and 5pi/4
                        if (angle < Math.PI * 3 / 4) angle = Math.PI * 3 / 4;
                        else if (angle > Math.PI * 5 / 4) angle = Math.PI * 5 / 4;

                        updateV(v, angle);  //update velocity

                    } else { //Player2 lost
                        scorePlayer1 += 1;
                        scores.setText(updateScoresText()); //Update scores
                        root.getChildren().add(startText);
                        root.getChildren().remove(stopText);
                        stopBoolean = true;                 //Game is stop
                        this.stop();
                    }
                }
                //If ball hits the left border
                if (ball.getCenterX() - ball.getRadius() < 30) {

                    //If ball hits the Player2
                    if (ball.getCenterY() > player1.getY() && ball.getCenterY() < player1.getY() + player1.getHeight()) {
                        counterStroke += 1;

                        //Make new angle
                        double p = ball.getCenterY() - player1.getY() - player1.getHeight() / 2;
                        angle = Math.PI * (1 + p / (2 * player1.getHeight())) - Math.abs(angle);
                        normalizeAngle();

                        //the new angle should be between (0 and pi/4) or (7pi/4 and 2pi)
                        if (angle > Math.PI / 4 && angle < Math.PI) angle = Math.PI / 4;
                        else if (angle < Math.PI * 7 / 4 && angle > Math.PI) angle = Math.PI * 7 / 4;

                        updateV(v, angle);  //update velocity
                    } else { //Player1 lost
                        scorePlayer2 += 1;
                        scores.setText(updateScoresText()); //Update scores
                        root.getChildren().add(startText);
                        root.getChildren().remove(stopText);
                        stopBoolean = true;                 //Game is stop
                        this.stop();
                    }
                }

                //Move player1
                if (keyW && !keyS && player1.getY() > 10) player1.setY(player1.getY() - DELTA_PLAYER);
                else if (!keyW && keyS && player1.getY() + player1.getHeight() < height - 10) player1.setY(player1.getY() + DELTA_PLAYER);

                //Move player2
                if (keyUP && !keyDOWN && player2.getY() > 10) player2.setY(player2.getY() - DELTA_PLAYER);
                else if (!keyUP && keyDOWN && player2.getY() + player2.getHeight() < height - 10) player2.setY(player2.getY() + DELTA_PLAYER);
            }
        };

        //When a key pressed
        EventHandler<KeyEvent> eventHandlerPressed = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.W) keyW = true;
                if (event.getCode() == KeyCode.S) keyS = true;
                if (event.getCode() == KeyCode.UP) keyUP = true;
                if (event.getCode() == KeyCode.DOWN) keyDOWN = true;
            }
        };

        //When a key released
        EventHandler<KeyEvent> eventHandlerReleased = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.W) keyW = false;
                if (event.getCode() == KeyCode.S) keyS = false;
                if (event.getCode() == KeyCode.UP) keyUP = false;
                if (event.getCode() == KeyCode.DOWN) keyDOWN = false;
            }
        };

        //handle stop and start game
        EventHandler<KeyEvent> startGame = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER && stopBoolean) {
                    angle = Math.random() * Math.PI / 2 - Math.PI / 4;
                    counterStroke = 0;
                    v = vi;
                    updateV(v, angle);
                    player1.setY(height / 2 - player1.getHeight() / 2);
                    player2.setY(height / 2 - player2.getHeight() / 2);
                    ball.setCenterX(width / 4);
                    ball.setCenterY(height / 2);
                    root.getChildren().remove(startText);
                    root.getChildren().add(stopText);
                    game.start();
                    stopBoolean = false;
                } else if (event.getCode() == KeyCode.ESCAPE && !stopBoolean) {
                    game.stop();
                    root.getChildren().add(startText);
                    root.getChildren().remove(stopText);
                    stopBoolean = true;
                }
            }
        };

        scene.setOnKeyPressed(eventHandlerPressed);
        scene.setOnKeyReleased(eventHandlerReleased);
        scene.addEventFilter(KeyEvent.ANY, startGame);
    }

    //Update velocity
    public void updateV(double v, double teta) {
        vX = v * Math.cos(teta);
        vY = v * Math.sin(teta);
        Main.v = v;
        Main.angle = teta;
    }

    public void symmetryY() {
        angle *= -1;
        normalizeAngle();
        updateV(v, angle);
    }

    public String updateScoresText() {
        return "Player1    " + scorePlayer1 + " | " + scorePlayer2 + "    Player2";
    }

    public void normalizeAngle() {
        while (angle >= Math.PI * 2) {
            angle -= Math.PI * 2;
        }
        while (angle < 0) {
            angle += Math.PI * 2;
        }
    }

    public static void main(String[] args) { launch(args);}
}