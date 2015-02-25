package com.imageOptimize;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: thudanih
 * Date: Apr 25, 2011
 * Time: 10:28:35 AM
 * To change this template use File | Settings | File Templates.
 */
public class EmptySpacer {
    private int[][] images;
    private int width, height;
//    ArrayList<EmptyRectSpace> emptySpaces = new ArrayList<EmptyRectSpace>();

    public EmptySpacer(int height, int width) {
        this.height = height;
        this.width = width;
    }

    public EmptySpacer() {

    }

    public List<EmptyRectSpace> getEmptyRectangleSpaces(/*int direction, */int[][] dummy, int width, int height) {
        return getEmptyRectangleSpaces(/*direction, */dummy, width, height, -1);
    }


    public List<EmptyRectSpace> getEmptyRectangleSpaces(int[][] dummy, int width, int height, int imageId) {
        images = dummy;
        this.width = width;
        this.height = height;
        List<EmptyRectSpace> emptySpaces = new ArrayList<EmptyRectSpace>();
        int val = -1000;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if ((images[i][j] == imageId) && ((j == 0) || (images[i][j - 1] != imageId))) {
                   createNewSpaces(i, j, ++val/*, direction*/, imageId, emptySpaces);
                }
            }
        }
        return emptySpaces;
    }

    private void createNewSpaces(int i, int j, int val, int imageId, List<EmptyRectSpace> emptySpaces) {
        images[i][j] = val;
        EmptyRectSpace space = new EmptyRectSpace();
        space.setStartingI(i);
        space.setStartingJ(j);
//        space.setArea(1);
//        space.setLength(1);
//        space.setWidth(1);
        boolean hasMoreCells = true;
//        if (direction == 0) {
        while (hasMoreCells) {
            if ((j < width - 1) && (images[i][j + 1] == imageId)) {
                images[i][j + 1] = val;
                space.incrementArea();
                space.incrementWidth();
            } else {
                hasMoreCells = false;
                int nextRowI = space.getStartingI() + 1;
                int nextRowJ = space.getStartingJ()/* - space.width + 1*/;
                if ((nextRowI < height) && (images[nextRowI][nextRowJ] == imageId) && ((nextRowJ == 0) || (images[nextRowI][nextRowJ - 1] != imageId))) {
                    createNewSpaces(nextRowI, nextRowJ, ++val/*, direction*/, imageId, emptySpaces);
                    EmptyRectSpace sp = emptySpaces.get(emptySpaces.size() - 1);
                    if (sp.getWidth() == space.getWidth()) {
                        int xx = images[space.getStartingI()][space.getStartingJ()];
                        for (int a = 0; a < sp.getWidth(); a++) {
                            images[sp.getStartingI()][sp.getStartingJ() + a] = xx;

                        }
                        space.setLength(space.getLength() + sp.getLength());
                        space.setArea(space.getArea() + sp.getArea());
                        emptySpaces.remove(sp);
                        space.setId(val);
                        emptySpaces.add(space);
                    } else {
                        space.setId(val);
                        emptySpaces.add(space);
                    }
                } else {
                    space.setId(val);
                    emptySpaces.add(space);
                    break;
                }
            }
            j++;
        }
    }


    public List<Integer> getSurroundingSpaces(EmptyRectSpace centerSpace, int[][] dummy) {
    	List<Integer> surroundingImagess = new ArrayList<Integer>();
        for (int x = centerSpace.getStartingI() + 1; ((x >= 0) && (x >= centerSpace.getStartingI()/* - centerSpace.length*/)); x--) {
            if (x < height && centerSpace.getStartingJ() < width - 1) {
                if (dummy[x][centerSpace.getStartingJ() + 1] > 0) {
                    int imageID = dummy[x][centerSpace.getStartingJ() + 1];
                    if (!surroundingImagess.contains(imageID)) {
                        surroundingImagess.add(imageID);
                    }
                }
            }
        }
        for (int x = centerSpace.getStartingI() + 1; ((x >= 0) && (x >= centerSpace.getStartingI()/* - centerSpace.length*/)); x--) {
            if ((x < height) && (centerSpace.getStartingJ()/* - centerSpace.width*/ < width) && (centerSpace.getStartingJ() >= 0/*centerSpace.width*/)) {
                if (dummy[x][centerSpace.getStartingJ()/* - centerSpace.width*/] > 0) {
                    int imageID = dummy[x][centerSpace.getStartingJ()/* - centerSpace.width*/];
                    if (!surroundingImagess.contains(imageID)) {
                        surroundingImagess.add(imageID);
                    }
                }
            }
        }
        for (int y = centerSpace.getStartingJ(); ((y >= 0) && (y >= centerSpace.getStartingJ()/* - centerSpace.width*/ + 1)); y--) {
            if ((centerSpace.getStartingI() < height - 1) && (y < width)) {
                if (dummy[centerSpace.getStartingI() + 1][y] > 0) {
                    int imageID = dummy[centerSpace.getStartingI() + 1][y];
                    if (!surroundingImagess.contains(imageID)) {
                        surroundingImagess.add(imageID);
                    }
                }
            }
        }
        for (int y = centerSpace.getStartingJ(); ((y >= 0) && (y >= centerSpace.getStartingJ()/* - centerSpace.width */ + 1)); y--) {
            if ((centerSpace.getStartingI()/* - centerSpace.length */ < height) && (y < width) && (centerSpace.getStartingI() >/* centerSpace.length*/0)) {
                if (dummy[centerSpace.getStartingI() /*- centerSpace.length*/][y] >= 0) {
                    int imageID = dummy[centerSpace.getStartingI()/* - centerSpace.length*/][y];
                    if (!surroundingImagess.contains(imageID)) {
                        surroundingImagess.add(imageID);
                    }
                }
            }
        }
        return surroundingImagess;
    }
}
