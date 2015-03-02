package MyClock;

/**
 * Class Point in 2-dimensional space.
 */
public class Point {

    /** X-coordinate. */
    private Number x;

    /** Y-coordinate. */
    private Number y;

    /**
     * Constructor of point.
     * @param x  X-coordinate
     * @param y  Y-coordinate
     */
    public Point(Number x, Number y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Get x-coordinate in integer format.
     * @return  x-coordinate
     */
    public int getX() {
        return (int)(this.x.doubleValue());
    }

    /**
     * Get y-coordinate in integer format.
     * @return  y-coordinate
     */
    public int getY() {
        return (int)this.y.doubleValue();
    }

    /**
     * Get x-coordinate in double format.
     * @return  x-coordinate
     */
    public double getDoubleX() {
        return this.x.doubleValue();
    }

    /**
     * Get y-coordinate in double format.
     * @return  y-coordinate
     */
    public double getDoubleY() {
        return this.y.doubleValue();
    }

    /**
     * Point addition with another point.
     * @param point  Another point
     * @return  Point got by addition with respective coordinates
     */
    public Point add(Point point) {
        return new Point(this.x.doubleValue() + point.getDoubleX(), this.y.doubleValue() + point.getDoubleY());
    }

    /**
     * Point addition with single value.
     * @param c  Single value
     * @return  Point with coordinates each added with respective coordinates of another point
     */
    public Point add(double c) {
        return new Point(this.x.doubleValue() + c, this.y.doubleValue() + c);
    }

    /**
     * Point multiplication with single value
     * @param c  Single value
     * @return  Point with coordinates each multiplied by single value
     */
    public Point transform(double c) {
        return new Point(this.x.doubleValue() * c, this.y.doubleValue() * c);
    }
}
