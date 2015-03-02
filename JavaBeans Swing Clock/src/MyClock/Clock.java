package MyClock;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * Analogue clock component, inherited from jPanel.
 */
public class Clock extends JPanel implements Serializable {

    /** Thickness of seconds arrow. */
    private final int SECONDS_THICKNESS = 3;

    /** Thickness of minutes arrow. */
    private final int MINUTES_THICKNESS = 9;

    /** Thickness of hours arrow. */
    private final int HOUR_THICKNESS = 9;

    /** Current hours. */
    private int hours;

    /** Current minutes. */
    private int minutes;

    /** Current seconds. */
    private int seconds;

    /** Current milliseconds. */
    private int milliseconds;

    /** Calendar reference. */
    private Calendar calendar;

    /** ActionListener reference. */
    private ActionListener taskPerformer;

    /** Timer reference. */
    private Timer timer;

    /** Font of numbers. */
    private Font font;

    /** Is clock running or not. */
    private boolean running;

    /** Is clock dark or not. */
    private boolean darkClock;

    /** Coordinates of oval center. */
    private Point ovalCenter;

    /** Oval size. */
    private int ovalSize;

    /** Continue clock. */
    private boolean currentTime;

    /**
     * By default clock isn't dark and it is running.
     */
    public Clock() {
        darkClock = false;
        running = true;
        currentTime = true;
    }

    /**
     * Hours setter.
     * @param hours  Hours
     */
    public void setHours(int hours) {
        if (! (0 <= hours && hours < 12)) {
            throw new IllegalArgumentException("Hour must be from 0 to 11");
        }

        int oldValue = this.hours;
        this.hours = hours;
        repaint();

        firePropertyChange("hours", oldValue, hours);
    }

    /**
     * Minutes setter.
     * @param minutes  Minutes
     */
    public void setMinutes(int minutes) {
        if (! (0 <= minutes && minutes < 60)) {
            throw new IllegalArgumentException("Minute must be from 0 to 59");
        }

        int oldValue = this.minutes;
        this.minutes = minutes;
        repaint();

        firePropertyChange("minutes", oldValue, minutes);
    }

    /**
     * Seconds setter.
     * @param seconds  Seconds
     */
    public void setSeconds(int seconds) {
        if (! (0 <= seconds && seconds < 60)) {
            throw new IllegalArgumentException("Seconde must be from 0 to 59");
        }

        int oldValue = this.seconds;
        this.seconds = seconds;
        repaint();

        firePropertyChange("seconds", oldValue, seconds);
    }

    /**
     * Milliseconds setter.
     * @param milliseconds  Milliseconds
     */
    public void setMilliseconds(int milliseconds) {
        if (! (0 <= milliseconds && milliseconds < 1000)) {
            throw new IllegalArgumentException("Millisecond must be from 0 to 999");
        }

        int oldValue = this.milliseconds;
        this.milliseconds = milliseconds;
        repaint();

        firePropertyChange("milliseconds", oldValue, milliseconds);
    }

    /**
     * Color scheme setter.
     * @param black  Is black or not
     */
    public void setBlack(boolean black) {
        this.darkClock = black;
        repaint();
    }

    /**
     * Running setter.
     * @param running  Is clock running or not
     */
    public void setRunning(boolean running) {
        boolean oldValue = this.running;
        this.running = running;
        repaint();

        if (! (timer == null)) {
            if (! running) {
                if (timer.isRunning()) {
                    timer.stop();
                    currentTime = false;
                }
            } else {
                timer.start();
            }
        }

        firePropertyChange("running", oldValue, running);
    }

    /**
     * Hours getter.
     * @return  Hours
     */
    public int getHours() {
        return this.hours;
    }

    /**
     * Minutes getter.
     * @return  Minutes
     */
    public int getMinutes() {
        return this.minutes;
    }

    /**
     * Seconds getter.
     * @return  Seconds
     */
    public int getSeconds() {
        return this.seconds;
    }

    /**
     * Milliseconds getter.
     * @return  Milliseconds
     */
    public int getMilliseconds() {
        return this.milliseconds;
    }

    /**
     * Color scheme getter.
     * @return  Is clock black or not
     */
    public boolean isBlack() {
        return this.darkClock;
    }

    /**
     * Running getter
     * @return  Is clock running or not.
     */
    public boolean isRunning() {
        return this.running;
    }

    /**
     * Add property event.
     * @param propertyName  Name of property
     * @param listener  Listener
     */
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        super.addPropertyChangeListener(propertyName, listener);
    }

    /**
     * Delete property event.
     * @param propertyName  Name of property
     * @param listener  Listener
     */
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        super.removePropertyChangeListener(propertyName, listener);
    }

    /**
     * Here we paint clock
     * @param t  Graphics
     */
    public void paintComponent(Graphics t) {
        // Parent paintComponent call
        super.paintComponent(t);

        // Oval size will be 95% of panel size
        ovalSize = (int)(Math.min(getWidth(), getHeight()) * 0.95);
        // Calculate oval center
        ovalCenter = new Point(getWidth(), getHeight()).transform(0.5);

        // Convert to Graphics2D
        Graphics2D g = (Graphics2D)t.create();
        // Activate antialias
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Draw clock face
        drawOvalCentered(g, getColorOf("background"), 1.0);
        // Draw hours arrow
        drawArrow(g, getColorOf("arrow"), getTimeArgument("h"), 0.5, HOUR_THICKNESS);
        // Draw minutes arrow
        drawArrow(g, getColorOf("arrow"), getTimeArgument("m"), 0.7, MINUTES_THICKNESS);
        // Draw white\black oval for minute and hour arrow
        drawOvalCentered(g, getColorOf("circle"), 0.05);
        // Draw red oval for second arrow
        drawOvalCentered(g, Color.RED, 0.015);
        // Draw seconds arrow
        drawArrow(g, Color.RED, getTimeArgument("s"), 0.65, SECONDS_THICKNESS);
        // Draw numbers from 1 to 12
        drawNumbers(g, getColorOf("circle"), 0.82);

        // Dispose graphics
        g.dispose();

        // Instance timer
        instanceTimer();
    }

    /**
     * Get trigonometric argument of seconds, minutes and hours in radians
     * @param type  Time type (hours, minutes, seconds)
     * @return  Trigonometric argument in radians
     */
    private double getTimeArgument(String type) {
        // Add milliseconds to seconds
        double second = (double)seconds + (double)milliseconds / 1000.0;
        // Add seconds to minute
        double minute = (double)minutes + second / 60.0;
        // Add minutes to hour
        double hour = (double)hours + minute / 60.0;

        if (type.equals("s")) {
            // Seconds argument
            return getArgument(second, 6.0);
        } else if (type.equals("m")) {
            // Minutes argument
            return getArgument(minute, 6.0);
        } else {
            // Hours argument
            return getArgument(hour, 30.0);
        }
    }

    /**
     * Get color which depends on element type and color scheme
     * @param argument  Element type
     * @return  Color
     */
    private Color getColorOf(String argument) {
        if (argument.equals("background") == darkClock) {
            return Color.BLACK;
        } else {
            return Color.WHITE;
        }
    }

    /**
     * Draw numbers at face
     * @param g  Graphics
     * @param color  Color of number
     * @param percentage  Percentage of face radius
     */
    private void drawNumbers(Graphics2D g, Color color, double percentage) {
        // Set color of number
        g.setColor(color);
        // Set font of number
        g.setFont(getClockFont());

        // Draw all numbers from 1 to 12
        for (int i = 1; i <= 12; i++) {
            // Number in string format
            String number = Integer.toString(i);

            // Create string metrics object
            StringMetrics metrics = new StringMetrics(g);
            // Get font shift in coordinates
            Point font = new Point(-metrics.getWidth(number) / 2.0, metrics.getHeight(number) / 3.0);

            // Calculate argument
            double angle = getArgument(i, 30.0);
            // Get coordinates of argument
            Point level = getCoordinates(angle, ovalSize / 2.0 * percentage);

            // Get location of number
            Point location = ovalCenter.add(level).add(font);

            // Draw number
            g.drawString(number, location.getX(), location.getY());
        }
    }

    /**
     * Evaluation of trigonometric coordinates.
     * @param angle  Angle in radians
     * @param r  Radius of circle
     * @return  Trigonometric coordinates
     */
    private Point getCoordinates(double angle, double r) {
        return new Point(Math.cos(angle) * r, Math.sin(angle) * r);
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

    /**
     * Get clock face font.
     * @return  Clock face font
     */
    private Font getClockFont() {
        // If font is not instanced
        if (font == null) {
            // Read font from resourses
            InputStream is = getClass().getResourceAsStream("HelveticaNeue-Light.otf");
            try {
                // Instance font
                font = Font.createFont(Font.TRUETYPE_FONT, is);
            } catch (Exception e) {
                // If error has happened instance font with system font
                font = new Font("Helvetica Neue", Font.TRUETYPE_FONT, resizeValue(65));
            }
        }

        // Return font with maximum size 65
        return font.deriveFont((float) resizeValue(65));
    }

    /**
     * Instance timer.
     */
    private void instanceTimer() {
        // If calendar is not instanced
        if (calendar == null) {
            // Instance calendar
            calendar = Calendar.getInstance();
        }

        // If taskPerformer is not instanced
        if (taskPerformer == null) {
            // Instance
            taskPerformer = new ActionListener() {
                public void actionPerformed(ActionEvent evt) {

                    // if current time
                    if (currentTime) {
                        // Update current time
                        calendar.setTime(new Date());
                        // Update hours
                        setHours(calendar.get(Calendar.HOUR));
                        // Update minutes
                        setMinutes(calendar.get(Calendar.MINUTE));
                        // Update seconds
                        setSeconds(calendar.get(Calendar.SECOND));
                        // Update milliseconds
                        setMilliseconds(calendar.get(Calendar.MILLISECOND));
                    } else {
                        // one tick
                        int step = 10;
                        // calculate milliseconds
                        int milliseconds = getMilliseconds() + step;
                        // calculate seconds
                        int seconds = getSeconds() + milliseconds / 1000;
                        // calculate minutes
                        int minutes = getMinutes() + seconds / 60;
                        // calculate hours
                        int hours = getHours() + minutes / 60;
                        // update milliseconds
                        setMilliseconds(milliseconds % 1000);
                        // update seconds
                        setSeconds(seconds % 60);
                        // update minutes
                        setMinutes(minutes % 60);
                        // update hours
                        setHours(hours % 12);
                    }

                    // repaint clock
                    repaint(new Rectangle(ovalCenter.getX() - ovalSize / 2, ovalCenter.getY() - ovalSize / 2,
                            ovalSize, ovalSize));
                }
            };
        }

        // If timer is not instanced
        if (timer == null) {
            // Instance timer, connect taskPerformer
            timer = new Timer(10, taskPerformer);

            if (isRunning()) {
                // Start timer (by default)
                timer.start();
            }
        }
    }

    /**
     * Resize value for jFrame resized.
     * @param maxValue  Max value that can be at full screen jFrame
     * @return  Value from 1 to {@code maxValue}
     */
    private int resizeValue(int maxValue) {
        // Calculate stroke
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        // Calculate minimum of screen dimension length
        double screenSize = Math.min(screen.getWidth(), screen.getHeight());
        // Calculate minimum of panel dimension length
        double panelSize = Math.min(getWidth(), getHeight());
        // Calculate thickness
        int thickness = (int)(panelSize / screenSize * maxValue);

        // Minimum value of thickness must be 1
        if (thickness < 1) {
            thickness = 1;
        }

        return thickness;
    }

    /**
     * Draw centered oval.
     * @param g  Graphics
     * @param color  Color of oval
     * @param percentage  Percentage of clock face oval
     */
    private void drawOvalCentered(Graphics2D g, Color color, double percentage) {
        // Calculate oval size
        int size = (int)(ovalSize * percentage);
        // Set white color for painting
        g.setColor(color);
        // Calculate location of oval
        Point location = new Point(getWidth(), getHeight()).add(-size).transform(0.5);
        // Fill oval, which will be the foundation of clock
        g.fillOval(location.getX(), location.getY(), size, size);
    }

    /**
     * Draw arrow.
     * @param g  Graphics
     * @param color  Color of arrow
     * @param arg  Trigonometric argument
     * @param length  Percentage of radius
     * @param strokeSize  Arrow thickness
     */
    private void drawArrow(Graphics2D g, Color color, double arg, double length, int strokeSize) {
        // Set stroke size
        g.setStroke(new BasicStroke(resizeValue(strokeSize)));
        // Set stroke color
        g.setColor(color);
        // Calculate arrow ending coordinates
        Point coordinates = getCoordinates(arg, ovalSize / 2.0 * length).add(ovalCenter);
        // Draw arrow
        g.drawLine(ovalCenter.getX(), ovalCenter.getY(), coordinates.getX(), coordinates.getY());
    }

    /**
     * Method main.
     * @param args  Console arguments
     */
    public static void main(String[] args) {
        // Create JFrame
        JFrame frame = new JFrame();

        // Get screen resolution
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        // Get screen width
        double width = screenSize.getWidth();
        // Get screen height
        double height = screenSize.getHeight();

        // Calculate JFrame size: 90% of minimum of width and height
        double size = Math.min(width, height) * 0.9;

        // Set frame size
        frame.setSize((int)(size), (int)(size));
        // Set frame location centered
        frame.setLocation((int)((width - size) / 2), 0);
        // Set frame title
        frame.setTitle("Clock");

        final Clock panel = new Clock();
        final Color lightGray = new Color(240, 240, 240);
        frame.getContentPane().add(panel);
        panel.setBackground(lightGray);

        final Calendar calendar = Calendar.getInstance(); // creates a new calendar instance
        calendar.setTime(new Date());   // assigns calendar to given date
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int millisecond = calendar.get(Calendar.MILLISECOND);
        panel.setHours(hour);
        panel.setMinutes(minute);
        panel.setSeconds(second);
        panel.setMilliseconds(millisecond);
        panel.setRunning(true);

        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (panel.getBackground() == Color.BLACK) {
                    panel.setBackground(Color.WHITE);
                    panel.setBlack(true);
                } else if (panel.getBackground() == Color.WHITE) {
                    panel.setBackground(lightGray);
                    panel.setBlack(false);
                } else {
                    panel.setBackground(Color.BLACK);
                }
            }
        });
        frame.setVisible(true);
        frame.repaint();
    }
}