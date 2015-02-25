package com.imageOptimize;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: thudanih
 * Date: Apr 25, 2011
 * Time: 10:36:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class SpaceIdentifier {
    private int totPerim, totSpaces;
    int spIdSymb = -1, width, height, w = 1, h = 1;
    private int[][] dummyMy, dummyT;

    public SpaceIdentifier(int[][] dummyVar, int width, int height) {
        dummyMy = dummyVar;
        dummyT = dummyVar;
        this.width = width;
        this.height = height;
    }

    private List<EmptyRectSpace> identifySpaces() {
    	List<EmptyRectSpace> alSpaceDetails = new ArrayList<EmptyRectSpace>();
        int spaceMarkingNumber = -2;
        for (int currCol = 0; currCol < width; currCol++) {
            for (int currRow = 0; currRow < height; currRow++) {
                if (dummyT[currRow][currCol] == spIdSymb) {
                    //Create new Spacedetails Object and fill it using current row and column
                    EmptyRectSpace objSpaceDetails = new EmptyRectSpace();
                    spaceMarkingNumber--;
                    objSpaceDetails.setStartingI(currRow);
                    objSpaceDetails.setStartingJ(currCol);
                    objSpaceDetails.setId(spaceMarkingNumber);
                    //Add these cordinates to the alSpaceDetails
                    alSpaceDetails.add(objSpaceDetails);

                    boolean sucess = spaceMarker(currCol, currRow, dummyT, spaceMarkingNumber, false);
                    if (!sucess) {
                        alSpaceDetails.remove(alSpaceDetails.size() - 1);
                    }
                }
            }
        }
        return alSpaceDetails;
    }

    private boolean spaceMarker(int colStart, int rowStart, int[][] dummy, int markinNum, boolean selfCall) {
        int rowStart_i = rowStart;
        boolean boolSucessfull = true;

        //int markingNum = markinNum;
        boolean boolStart = true;
        int lenOfColumn;
        int lengthOfLastCol = 0;

        for (int currCol = colStart; currCol < width; currCol++) {
            int currRow = rowStart; // initial position is set to row start parameter.

            /*
            * If it is not the starting time, It check the current row.
            */
//                #region // not for the start
            if (!boolStart) {
                if (dummy[currRow][currCol] > 0) {
                    while ((dummy[currRow][currCol] > 0) && currRow <= (lengthOfLastCol + rowStart - 1)) {
                        if (currRow < (height - 1))
                            currRow++;
                        else
                            break;
                    }
                    if ((currRow > (lengthOfLastCol + rowStart - 1)) || currRow == (height - 1))
                        break;
                }

                /*
                * If the initial row position of the current column is a free space
                * then it check for the suitable highest position(low currRow) to start.
                */
                else if (dummy[currRow][currCol] == spIdSymb) {

                    while ((dummy[currRow][currCol] == spIdSymb) && currRow > 0) {
                        if (currRow > 0) {
                            if (dummy[currRow - 1][currCol] == spIdSymb)
                                currRow--;
                            else
                                break;
                        }
                    }
                }
                rowStart = currRow; //Rowstart parameter set to the start position of the last column.
            }

            lenOfColumn = 0;
            boolean boolEndOfMatrix = false;
            //int makeNum = 1;
            while ((!(dummy[currRow][currCol] > 0) && currRow < height) || (lengthOfLastCol + rowStart - 1) > currRow) {

                if (boolEndOfMatrix)
                    break;

                //If current position is already marked one. Error................
                if (dummy[currRow][currCol] < -1) {
                    if (dummy[currRow][currCol] == markinNum && selfCall) {
                        boolSucessfull = false;
                        break;
                    }
                    if (!selfCall) {
                        int makeNum = dummy[currRow][currCol];
                        boolSucessfull = false;
                        spaceMarker(colStart, rowStart_i, dummy, makeNum, true);
                        break;
                    }
                }
                if (dummy[currRow][currCol] == spIdSymb || selfCall)
                    dummy[currRow][currCol] = markinNum;

                if ((!(dummy[currRow][currCol] > 0) && currRow < height))
                    lenOfColumn++;
                if (currRow < (height - 1)) {
                    currRow++;
                } else
                    boolEndOfMatrix = true;
            }
            if (!boolSucessfull) {
                break;
            }
            lengthOfLastCol = lenOfColumn;
            boolStart = false;
        }
        return boolSucessfull;
    }

    public int getToatPerim() {
        calcPerims();
        return totPerim;
    }


    public List<EmptyRectSpace> calcPerims() {
    	List<EmptyRectSpace> alSpaceDetails = identifySpaces();
        int perim;
        int currentSize = alSpaceDetails.size();
        for (int i = 0; i < currentSize; i++) {
            EmptyRectSpace space = alSpaceDetails.get(i);
            perim = calcPerimAndSpace(space.getId());
            space.setPerimiter(perim);
            alSpaceDetails.add(i, space);
            totPerim += perim;
        }
        return alSpaceDetails;
    }

    public int getTotSpaces() {
        return this.totSpaces;
    }

    public double getDistaneToBest() {
        // SpaceIdentifier temp = new SpaceIdentifier((int[,])currDummy.Clone(), numOfColumns, numOfRows);
        int perim = this.getToatPerim();
        int spaces = this.getTotSpaces();
        return (perim - Math.sqrt(spaces) * 4);
    }

    private int calcPerimAndSpace(int spaceId) {
        int i, j, perim = 0;

        for (i = 0; i < height; i++) {
            for (j = 0; j < width; j++) {
                if (dummyMy[i][j] == spaceId) {
                    if ((i == 0 && dummyMy[i][j] == spaceId) || (i != 0 && dummyMy[i - 1][j] != spaceId)) {
                        perim += w;
                    }
                    if ((j == 0 && dummyMy[i][j] == spaceId) || (j != 0 && dummyMy[i][j - 1] != spaceId)) {
                        perim += h;
                    }
                    if ((i == height - 1 && dummyMy[i][j] == spaceId) || (i != height - 1 && dummyMy[i + 1][j] != spaceId)) {
                        perim += w;
                    }
                    if ((j == width - 1 && dummyMy[i][j] == spaceId) || (j != width - 1 && dummyMy[i][j + 1] != spaceId)) {
                        perim += h;
                    }
                    totSpaces += 1;
                }
            }
        }
        return perim;
    }

}
