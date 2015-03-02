package ClockFx;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

import java.util.Calendar;
import java.util.Date;


public class Clock extends Application {

    public void start(Stage stage) {
        // Create group
        Group group = new Group();

        // Create clock face circle
        final Circle clockFace = new Circle();

        // Create minute circle (for arrow)
        Circle minuteCircle = new Circle();
        // Create second circle (for arrow)
        Circle secondCircle = new Circle();

        // Create second arrow
        final Line secondArrow = new Line();
        // Create minute arrow
        final Line minuteArrow = new Line();
        // Create hour arrow
        final Line hourArrow = new Line();

        // Add all elements to group
        group.getChildren().add(clockFace);
        group.getChildren().add(hourArrow);
        group.getChildren().add(minuteArrow);
        group.getChildren().add(minuteCircle);
        group.getChildren().add(secondCircle);
        group.getChildren().add(secondArrow);

        // Create scene with this group
        Scene scene = new Scene(group);

        // Minimum binding scene dimension
        NumberBinding sceneSize = Bindings.min(scene.heightProperty(), scene.widthProperty());

        // Minimum of screen dimension
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        double screenSize = Math.min(screen.getWidth(), screen.getHeight());

        // Configure stage
        stage.setTitle("ClockFx");
        stage.setScene(scene);
        stage.setWidth(600);
        stage.setHeight(600);
        stage.setMinWidth(200);
        stage.setMinHeight(200);
        stage.show();

        // Load font
        Font.loadFont(Clock.class.getResource("HelveticaNeue-Light.otf").toExternalForm(), 65);

        // Calculate resizeable font size
        final NumberBinding fontSize = Bindings.max(sceneSize.divide(screenSize).multiply(65), 1.0);

        // Create array of texts (for clock face from 1 to 12)
        final Text[] texts = new Text[12];

        // Create and configure texts
        for (int i = 0; i < 12; i++) {
            texts[i] = new Text(Integer.toString(i + 1));
            texts[i].setFill(Color.WHITE);
            resizeFont(clockFace, texts[i], fontSize, i);
            group.getChildren().add(texts[i]);
        }

        // Resize font while scene width is changing
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> o, Number w, Number h) {
                for (int i = 0; i < 12; i++) {
                    resizeFont(clockFace, texts[i], fontSize, i);
                }
            }
        });


        // Resize font while scene height is changing
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> o, Number w, Number h) {
                for (int i = 0; i < 12; i++) {
                    resizeFont(clockFace, texts[i], fontSize, i);
                }
            }
        });

        // Configure clock face
        clockFace.radiusProperty().bind(sceneSize.divide(2).multiply(0.9));
        clockFace.centerXProperty().bind(scene.widthProperty().divide(2));
        clockFace.centerYProperty().bind(scene.heightProperty().divide(2));
        clockFace.setFill(Color.BLACK);

        // Configure minute circle
        minuteCircle.radiusProperty().bind(clockFace.radiusProperty().multiply(0.05));
        minuteCircle.centerXProperty().bind(clockFace.centerXProperty());
        minuteCircle.centerYProperty().bind(clockFace.centerYProperty());
        minuteCircle.setFill(Color.WHITE);

        // Configure second circle
        secondCircle.radiusProperty().bind(clockFace.radiusProperty().multiply(0.015));
        secondCircle.centerXProperty().bind(clockFace.centerXProperty());
        secondCircle.centerYProperty().bind(clockFace.centerYProperty());
        secondCircle.setFill(Color.RED);

        // Configure second arrow
        secondArrow.setStroke(Color.RED);
        secondArrow.strokeWidthProperty().bind(Bindings.max(sceneSize.divide(screenSize).multiply(3), 1.0));
        secondArrow.startXProperty().bind(clockFace.centerXProperty());
        secondArrow.startYProperty().bind(clockFace.centerYProperty());
        secondArrow.setSmooth(true);

        // Configure minute arrow
        minuteArrow.setStroke(Color.WHITE);
        minuteArrow.strokeWidthProperty().bind(Bindings.max(sceneSize.divide(screenSize).multiply(8), 1.0));
        minuteArrow.startXProperty().bind(clockFace.centerXProperty());
        minuteArrow.startYProperty().bind(clockFace.centerYProperty());
        minuteArrow.setSmooth(true);

        // Configure hour arrow
        hourArrow.setStroke(Color.WHITE);
        hourArrow.strokeWidthProperty().bind(Bindings.max(sceneSize.divide(screenSize).multiply(8), 1.0));
        hourArrow.startXProperty().bind(clockFace.centerXProperty());
        hourArrow.startYProperty().bind(clockFace.centerYProperty());

        // Get instance of calendar
        final Calendar calendar = Calendar.getInstance();

        // Create timer for animation
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long arg) {
                // Update current time
                calendar.setTime(new Date());

                // Calculate seconds
                double second = (double)calendar.get(calendar.SECOND) +
                        (double)calendar.get(calendar.MILLISECOND) / 1000.0;
                // Calculate minutes
                double minute = (double)calendar.get(calendar.MINUTE) + second / 60.0;
                // Calculate hours
                double hour = (double)calendar.get(calendar.HOUR) + minute / 60.0;

                // Calculate end of second arrow
                secondArrow.endXProperty().bind(clockFace.centerXProperty().add(getXProperty(second, 6.0, 0.7)));
                secondArrow.endYProperty().bind(clockFace.centerYProperty().add(getYProperty(second, 6.0, 0.7)));

                // Calculate end of minute arrow
                minuteArrow.endXProperty().bind(clockFace.centerXProperty().add(getXProperty(minute, 6.0, 0.7)));
                minuteArrow.endYProperty().bind(clockFace.centerYProperty().add(getYProperty(minute, 6.0, 0.7)));

                // Calculate end of hour arrow
                hourArrow.endXProperty().bind(clockFace.centerXProperty().add(getXProperty(hour, 30.0, 0.5)));
                hourArrow.endYProperty().bind(clockFace.centerYProperty().add(getYProperty(hour, 30.0, 0.5)));
            }

            /** Calculate end x property. */
            private NumberBinding getXProperty(double value, double pace, double size) {
                return getCoordinate(value, pace, size, true);
            }

            /** Calculate end y property. */
            private NumberBinding getYProperty(double value, double pace, double size) {
                return getCoordinate(value, pace, size, false);
            }

            /** Calculate coordinate property. */
            private NumberBinding getCoordinate(double value, double pace, double size, boolean c) {
                double arg;
                if (! c) {
                    arg = Math.sin(getArgument(value, pace));
                } else {
                    arg = Math.cos(getArgument(value, pace));
                }

                return clockFace.radiusProperty().multiply(arg).multiply(size);
            }
        };

        // Start timer
        timer.start();
    }

    /** Resize font method. */
    private void resizeFont(Circle clockFace, Text text, NumberBinding fontSize, int i) {
        text.setFont(new Font("Helvetica Neue Light", fontSize.doubleValue()));
        double angle = getArgument(i + 1, 30.0);
        text.xProperty().bind(clockFace.centerXProperty().add(
                        clockFace.radiusProperty().multiply(Math.cos(angle)).multiply(0.83)
                ).subtract(text.getBoundsInLocal().getWidth() / 2.0)
        );
        text.yProperty().bind(clockFace.centerYProperty().add(
                        clockFace.radiusProperty().multiply(Math.sin(angle)).multiply(0.83)
                ).add(text.getBoundsInLocal().getHeight() / 3.0)
        );
    }

    /**
     * Evaluation of trigonometric argument.
     * @param x  Time indicator
     * @param pace  Pace of time indicator
     * @return  Trigonometric argument
     */
    private double getArgument(double x, double pace) {
        return x * pace * Math.PI / 180.0 - Math.PI / 2;
    }

    /** Main method. */
    public static void main(String[] args) {
        Application.launch(args);
    }
}
