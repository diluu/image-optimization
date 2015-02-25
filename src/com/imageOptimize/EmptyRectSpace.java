package com.imageOptimize;

/**
 * Created by IntelliJ IDEA.
 * User: thudanih
 * Date: Apr 25, 2011
 * Time: 10:50:17 AM
 * To change this template use File | Settings | File Templates.
 */
public class EmptyRectSpace implements Comparable {
    private int id;
    private int startingI;
    private int startingJ;
    private int perimiter;
    private int area;
    private int length;
    private int width;

    public EmptyRectSpace() {
        super();
        this.area = 1;
        this.length = 1;
        this.width  = 1;
    }

    public int compare(Object lhs, Object rhs) {
        EmptyRectSpace lhsCar = (EmptyRectSpace) lhs;
        EmptyRectSpace rhsCar = (EmptyRectSpace) rhs;
        if (lhsCar.area < rhsCar.area) {
            return 1;
        } else if (lhsCar.area > rhsCar.area) {
            return -1;
        } else {
            return 0;
        }
    }

    // implement the CompareTo() method of Comparable
    public int compareTo(Object rhs) {
        return compare(this, rhs);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStartingI() {
        return startingI;
    }

    public void setStartingI(int startingI) {
        this.startingI = startingI;
    }

    public int getStartingJ() {
        return startingJ;
    }

    public void setStartingJ(int startingJ) {
        this.startingJ = startingJ;
    }

    public int getPerimiter() {
        return perimiter;
    }

    public void setPerimiter(int perimiter) {
        this.perimiter = perimiter;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void incrementArea(){
        area++;
    }

    public void incrementWidth(){
        width++;
    }

    public void incrementLength(){
        length++;
    }
}
