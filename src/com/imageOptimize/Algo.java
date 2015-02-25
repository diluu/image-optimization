package com.imageOptimize;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: thudanih
 * Date: Apr 25, 2011
 * Time: 9:48:44 AM
 * To change this template use File | Settings | File Templates.
 */
public class Algo {
	List<ImageObject> imagesProcessing, bestImagesRemaining;
	List<ImageObject> images;
    private int numOfImages, totalWidth, totalHeight;
    int[][] currDummy, currBestDummy;
    private List<EmptyRectSpace> emptyRectSpaces;
    private List<Integer> initiallargeImages;

    private double currDistanceToBest;
    double increment = 0;
    private int SAIterations = 0,  numOfImagesToRemoveSA = 10;

    public Algo(int height, int width, List<ImageObject> images) {
        imagesProcessing = (List<ImageObject>) ((ArrayList<ImageObject>) images).clone();
        this.images = images;
        numOfImages = images.size();
        totalWidth = width;
        totalHeight = height;
        initiallargeImages = new ArrayList<Integer>();
        bestImagesRemaining = new ArrayList<ImageObject>();
        createDummy();
    }

    public int[][] run() {
        return run(true);
//            drawDummy();
    }

    private int[][] run(boolean firstRun) {
        emptyRectSpaces = getEmptyRectangleSpaces(copyDummy(currDummy));

        if (firstRun) {
            placeInitialImages();
            SpaceIdentifier temp = new SpaceIdentifier(copyDummy(currDummy), totalWidth, totalHeight);
            currBestDummy = copyDummy(currDummy);
            currDistanceToBest = temp.getDistaneToBest();
        }

        int a = 0;
        Collections.sort(imagesProcessing);
        while (imagesProcessing.size() != 0 && a < numOfImages && emptyRectSpaces.size() != 0) {
            emptyRectSpaces = getEmptyRectangleSpaces(copyDummy(currDummy));

            if (!placeWidthAndHeightFitImage()) {
                emptyRectSpaces = getEmptyRectangleSpaces(copyDummy(currDummy));
                if (!placeWidthOrHeightFitImage()) {
                    emptyRectSpaces = getEmptyRectangleSpaces(copyDummy(currDummy));

                    if (!placeRandomBestImage()) {
                        // break;
                    }
                }
            }
            a++;
        }

        if (firstRun) {
            emptyRectSpaces = getEmptyRectangleSpaces(copyDummy(currDummy));
            currBestDummy = copyDummy(currDummy);
            currDistanceToBest = getEnergy(copyDummy(currDummy));
        }
        return currBestDummy;
    }

    /**
     * @return : get Empty spaces
     */
    private List<EmptyRectSpace> getEmptySpacesOfCurrentDummy() {
        SpaceIdentifier objSid = new SpaceIdentifier(copyDummy(currDummy), totalWidth, totalHeight);
        return objSid.calcPerims();
    }

    private List<EmptyRectSpace> getEmptyRectangleSpaces(int[][] dummy) {
        EmptySpacer shapeIdentifier = new EmptySpacer();
        return shapeIdentifier.getEmptyRectangleSpaces(dummy, totalWidth, totalHeight);
    }

    /**
     * find add which totally fit to the rectangle space
     *
     * @return : placed
     */
    private boolean placeWidthAndHeightFitImage() {
        for (ImageObject image : imagesProcessing) {
            for (EmptyRectSpace emptyRectangle : emptyRectSpaces) {
                if ((image.getWidth() == emptyRectangle.getWidth()) && (image.getHeight() == emptyRectangle.getLength())) {
                    placeImage(currDummy, image, emptyRectangle.getStartingI(), emptyRectangle.getStartingJ(), true);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * finds a add which have same columns or same rows
     * @return : placed
     */
    private boolean placeWidthOrHeightFitImage() {
        double localBestDistance = totalHeight * totalWidth;
        double distance;
        ImageObject localBestImage = new ImageObject();
        localBestImage.setId(-1);
        localBestImage.setWidth(-1);
        localBestImage.setHeight(-1);

        EmptyRectSpace localBestRectSpace = new EmptyRectSpace();
        localBestRectSpace.setStartingI(-1);
        localBestRectSpace.setStartingJ(-1);
        localBestRectSpace.setWidth(0);

        for (ImageObject image : imagesProcessing) {

            for (EmptyRectSpace emptyRectangle : emptyRectSpaces) {
                if ((image.getWidth() == emptyRectangle.getWidth() && image.getHeight() <= emptyRectangle.getLength()) || (image.getHeight() == emptyRectangle.getLength() && image.getWidth() <= emptyRectangle.getWidth())) {

                    int[][] dummy1 = copyDummy(currDummy);
                    placeImage(dummy1, image, emptyRectangle.getStartingI(), emptyRectangle.getStartingJ()/* - emptyRectangle.width + add.colomns*/, false);

                    int[][] dummyCopy1 = copyDummy(dummy1);
                    SpaceIdentifier spaceIdentify = new SpaceIdentifier(dummyCopy1, totalWidth, totalHeight);
                    spaceIdentify.calcPerims();
                    distance = getEnergy(dummy1);
                    if (localBestDistance >= distance) {
                        localBestDistance = distance;
                        localBestImage = image;
                        localBestRectSpace = emptyRectangle;
                    }
                }
            }
        }

        if (localBestImage.getId() >= 0) {
            placeImage(currDummy, localBestImage, localBestRectSpace.getStartingI(), localBestRectSpace.getStartingJ()/* - localBestRectSpace.width + localBestAdd.colomns*/, true);
            return true;
        }
        return false;
    }

    private boolean placeRandomBestImage() {
        ImageObject localBestImage = new ImageObject();
        localBestImage.setId(-1);
        localBestImage.setWidth(-1);
        localBestImage.setHeight(-1);

        EmptyRectSpace localBestRectSpace = new EmptyRectSpace();
        localBestRectSpace.setStartingI(-1);
        localBestRectSpace.setStartingJ(-1);
        localBestRectSpace.setWidth(-1);
        localBestRectSpace.setLength(-1);

        for (ImageObject image : imagesProcessing) {
            for (EmptyRectSpace emptyRectangle : emptyRectSpaces) {
                if ((image.getHeight() <= emptyRectangle.getLength() && image.getWidth() <= emptyRectangle.getWidth())) {
                    placeImage(currDummy, image, emptyRectangle.getStartingI(), emptyRectangle.getStartingJ()/* - emptyRectangle.width + add.colomns*/, true);
                    return true;
                }
            }
        }
        return false;
    }

    public void runSAOnce() {
        emptyRectSpaces = getEmptyRectangleSpaces(copyDummy(currDummy));
        List<Integer> ranImages = getAndRemoveRandomImages();
        List<Integer> removedImagess = getSurroundingImages();//GetAndRemoveAddNearSpaceToChange();
        removedImagess.addAll(ranImages);
//        totSAIterations += 1;

        for (Object o : removedImagess) {
            int i = (Integer) o;
            for (ImageObject image : images) {
                if (image.getId() == i) {
                    imagesProcessing.add(image);
                }
            }
        }
        emptyRectSpaces = getEmptyRectangleSpaces(copyDummy(currDummy));
        Collections.sort(imagesProcessing);
        run(false);

    }

    public int[][] runSA() {
//        int iterationsDone = 0;
        double distance;
        for (int i = 0; i < SAIterations /*|| currBestEmptySpaces>3*/; i++) {
            this.runSAOnce();
            distance = getEnergy(copyDummy(currDummy));
            if ((currDistanceToBest >= distance - increment)) {
                currBestDummy = copyDummy(currDummy);
                currDistanceToBest = distance;
                bestImagesRemaining = (List<ImageObject>) ((ArrayList<ImageObject>) imagesProcessing).clone();
            }
            currDummy = copyDummy(currBestDummy);
        }
        currDummy = copyDummy(currBestDummy);
        return currBestDummy;
    }

    private List<Integer> getAndRemoveImagesNearSpaceToChange() {
    	List<Integer> imageIds = new ArrayList<Integer>();
        EmptySpacer spacer = new EmptySpacer(totalHeight, totalWidth);
        for (EmptyRectSpace rectSpace : emptyRectSpaces) {
            imageIds.addAll(spacer.getSurroundingSpaces(rectSpace, copyDummy(currDummy)));
        }
        return imageIds;
    }

    private ArrayList<Integer> getAndRemoveRandomImages() {
        ArrayList<Integer> ranImageIds = new ArrayList<Integer>();

        Random ran = new Random();
        int width, height, id;
        for (int i = 0; i < numOfImagesToRemoveSA; i++) {
            width = ran.nextInt(totalWidth);
            height = ran.nextInt(totalHeight);
            id = currDummy[height][width];
            if (id > 1 && !isInitialLargeImage(id)) {
                removeImageFromDummy(id);
                ranImageIds.add(id);
            }
        }
        return ranImageIds;
    }

    private boolean placeImage(int[][] Dummy, ImageObject image, int height, int width, boolean removeImageAfterPlaced) {
        for (int i = height; i < height + image.getHeight(); i++) {
            for (int j = width; j < width + image.getWidth(); j++) {
                if (!(i < 0 || j < 0 || i >= totalHeight || j >= totalHeight) && currDummy[i][j] == -1) {
                    Dummy[i][j] = image.getId();
                }
            }
        }
        if (removeImageAfterPlaced) {
            System.out.println("================================== placed:" + image.getId());
            imagesProcessing.remove(image);//remove add from list
        }
        return true;
    }

    private void removeImageFromDummy(int imageID) {
        for (int i = 0; i < totalHeight; i++) {
            for (int j = 0; j < totalWidth; j++) {
                if (currDummy[i][j] == imageID)
                    currDummy[i][j] = -1;
            }
        }
    }

    /**
     * create initial large image matrix
     */
    private void createDummy() {
        currDummy = new int[totalHeight][totalWidth];
        for (int i = 0; i < totalHeight; i++) {
            for (int j = 0; j < totalWidth; j++) {
                currDummy[i][j] = -1;
            }
        }
        currBestDummy = currDummy;
    }

    private List<Integer> getSurroundingImages() {
    	List<Integer> surroundingImages = new ArrayList<Integer>();
        // emptyRectSpaces = GetEmptyRectangleSpaces();

        for (EmptyRectSpace centerSpace : emptyRectSpaces) {
            for (int x = centerSpace.getStartingI() + 1; ((x >= 0) && (x >= centerSpace.getStartingI()/* - centerSpace.length*/)); x--) {
                if (x < totalHeight && centerSpace.getStartingJ() < totalWidth - 1) {
                    if (currDummy[x][centerSpace.getStartingJ() + 1] > 0) {
                        int imageID = currDummy[x][centerSpace.getStartingJ() + 1];
                        if (!surroundingImages.contains(imageID) && !isInitialLargeImage(imageID)) {
                            surroundingImages.add(imageID);
                            removeImageFromDummy(imageID);
                        }
                    }
                }
            }
            for (int x = centerSpace.getStartingI() + 1; ((x >= 0) && (x >= centerSpace.getStartingI()/* - centerSpace.length*/)); x--) {
                if ((x < totalHeight) && (centerSpace.getStartingJ()/* - centerSpace.width*/ < totalWidth) && (centerSpace.getStartingJ() >= /*centerSpace.width*/0)) {
                    if (currDummy[x][centerSpace.getStartingJ() /*- centerSpace.width*/] > 0) {
                        int imageID = currDummy[x][centerSpace.getStartingJ() /*- centerSpace.width*/];
                        if (!surroundingImages.contains(imageID) && !isInitialLargeImage(imageID)) {
                            surroundingImages.add(imageID);
                            removeImageFromDummy(imageID);
                        }
                    }
                }
            }
            for (int y = centerSpace.getStartingJ(); ((y >= 0) && (y >= centerSpace.getStartingJ()/* - centerSpace.width*/ + 1)); y--) {
                if ((centerSpace.getStartingI() < totalHeight - 1) && (y < totalWidth)) {
                    if (currDummy[centerSpace.getStartingI() + 1][y] > 0) {
                        int imageID = currDummy[centerSpace.getStartingI() + 1][y];
                        if (!surroundingImages.contains(imageID) && !isInitialLargeImage(imageID)) {
                            surroundingImages.add(imageID);
                            removeImageFromDummy(imageID);
                        }
                    }
                }
            }
            for (int y = centerSpace.getStartingJ(); ((y >= 0) && (y >= centerSpace.getStartingJ()/* - centerSpace.width */ + 1)); y--) {
                if ((centerSpace.getStartingI()/* - centerSpace.length */ < totalHeight) && (y < totalWidth) && (centerSpace.getStartingI() > /*centerSpace.length*/0)) {
                    if (currDummy[centerSpace.getStartingI() /*- centerSpace.length*/][y] > 0) {
                        int imageID = currDummy[centerSpace.getStartingI()/* - centerSpace.length*/][y];
                        if (!surroundingImages.contains(imageID) && !isInitialLargeImage(imageID)) {
                            surroundingImages.add(imageID);
                            removeImageFromDummy(imageID);
                        }
                    }
                }
            }
        }
        return surroundingImages;
    }

    public void setNumOfRandomImagesToRemove(int val) {
        numOfImagesToRemoveSA = val;
    }

    public void setSAIterations(int iterations) {
        SAIterations = iterations;
    }

    public void setIncrement(double Increment) {
        increment = Increment;
    }

    private void placeInitialImages() {
        ImageObject image;
        Collections.sort(images);
        boolean smallImage = false;
        int index = 0;
        while (!smallImage) {
            image = images.get(index);
            if (image.getWidth() >= 100 && image.getHeight() >= 100) {
                emptyRectSpaces = getEmptyRectangleSpaces(copyDummy(currDummy));
                Collections.sort(emptyRectSpaces);
                for (EmptyRectSpace e : emptyRectSpaces) {
                    if (image.getHeight() <= e.getLength() && image.getWidth() <= e.getWidth()) {
                        placeImage(currDummy, image, e.getStartingI(), e.getStartingJ(), true);
                        initiallargeImages.add(image.getId());
                    }
                }
            } else {
                smallImage = true;
            }
            index++;
        }
    }

    private boolean isInitialLargeImage(int imageId) {
        return initiallargeImages.contains(imageId);
    }

    private double getEnergy(int[][] dummy) {
        int perim, spaces, numOfDistinctSpaces;
        double distance, energy;
        SpaceIdentifier spi = new SpaceIdentifier(copyDummy(dummy), totalWidth, totalHeight);
        numOfDistinctSpaces = getEmptyRectangleSpaces(copyDummy(dummy)).size();
        perim = spi.getToatPerim();
        spaces = spi.getTotSpaces();
        distance = perim - Math.sqrt(spaces) * 4;
        energy = (distance + 1) * perim /** spaces */ * Math.pow(1 + numOfDistinctSpaces, .8) / spaces;//.05
        if (energy != 0) {
            return Math.log10(energy);
        } else {
            return 0;
        }
    }

    public int[][] copyDummy(int[][] source) {
        int[][] dummyCopy = new int[totalHeight][totalWidth];
        for (int i = 0; i < totalHeight; i++) {
            System.arraycopy(source[i], 0, dummyCopy[i], 0, totalWidth);
        }
        return dummyCopy;
    }
}