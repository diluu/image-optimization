package com.imageOptimize;

/**
 * Created by IntelliJ IDEA.
 * User: thudanih
 * Date: Apr 25, 2011
 * Time: 10:45:58 AM
 * To change this template use File | Settings | File Templates.
 */
public class ImageObject implements Comparable, Cloneable {
    private int id;
    private int width;
    private int height;

    public int compare(Object lhs, Object rhs) {
        ImageObject lhsCar = (ImageObject) lhs;
        ImageObject rhsCar = (ImageObject) rhs;
        if (lhsCar.width< rhsCar.width) {
            return 1;
        } else if(lhsCar.width == rhsCar.width){
           if(lhsCar.height < rhsCar.height){
               return 1;
           } else if(lhsCar.height > rhsCar.height){
              return -1;
           }
            else{
               return 0;
           }
        }
        else{
            return -1;
        }
    }


//            #endregion

    public int compareTo(Object o) {
        return compare(this, o);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();    
    }
}
