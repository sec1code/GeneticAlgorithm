
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class BildManager {
    //die mutationsrate
    private final double mutationRate = 1.0;
    //die Nummer an "Nachfahren" in der nächsten Generation
    private final int numberOfOffspring = 100;
    //der Faktor mit dem die Bilder skaliert werden
    private File f;

    //um ehrlich zu sein, ich weiß nicht mehr, wozu es diese Liste gibt.
    private List<BufferedImage> bildListe;
    private List<Integer> colourOfOriginalImage = new ArrayList<>();

    //der wert der die fitness des derzeitigen Model Bilds beschreibt
    private int fitness;
    //selbsterklärend, einfach die derzeitige Generation.
    private int generations = 1;
    //das originale/vorher gegebene Bild
    private BufferedImage originalImage;

    //das vorbild bild / das zurzeit beste bild

    private List<BufferedImage> offspring;

    //List der Bilder die als Vorbild für eine Generation genommen wurden
    private List<BufferedImage> listOfModelImages;
    //die gleichen Bilder wie oben, nur skaliert auf eine nutzbare Größe
    private List<BufferedImage> scaledListOfModelImages;
    public BildManager() {
        offspring = new ArrayList<>();
        try {
            originalImage = readImage("D:/Sergej/Sergej Schule/P5/Bilder/Testing/Test7.png");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        fitness = originalImage.getHeight() * originalImage.getWidth();
        bildListe = new ArrayList<>();
        colourOfOriginalImage = colourOfOriginalImage();
        f = null;
        loadBildListe();

        for(int i = 0; i < numberOfOffspring; i++) {
            offspring.add(randomizeImage(createNewImage()));
        }
        listOfModelImages = new ArrayList<>();
        scaledListOfModelImages = new ArrayList<>();
    }

    // returns the modelImage
    public List<BufferedImage> simulate() {
        BufferedImage modelImage = deepCopy(originalImage);
        ArrayList<BufferedImage> mutatedImages = new ArrayList<>();
        //Artificial cap, so that the programm can stop even if fitness != 0
        int bestFitnessScore = getFitness(offspring.get(0));
        while(fitness > 0) {
            mutatedImages = new ArrayList<>();
            if(generations >= 500) {
                break;
            }


            for(int j = 0; j < offspring.size(); j++) {
                //images mutate
                for(int i = 0; i < (int) mutationRate; i++) {
                    double fixMutationRate = (mutationRate * 10);
                    if(fixMutationRate >= getRandomNumber(0, 10)) {
                        mutatedImages.add(deepCopy(mutate(offspring.get(j))));
                    }
                }

                //calculating fitness values + setting new model image
                int temporaryFitnessScore = getFitness(deepCopy(mutatedImages.get(j)));
                if(temporaryFitnessScore < bestFitnessScore) {
                    bestFitnessScore = temporaryFitnessScore;
                    modelImage = deepCopy(mutatedImages.get(j));
                }
            }
            //creating new offspring off of modelImage
            fitness = bestFitnessScore;
            //offspring = (List<BufferedImage>) mutatedImages.clone();
            createOffSpring(deepCopy(modelImage));
            generations++;
            listOfModelImages.add(deepCopy(modelImage));
            System.out.println(fitness);
        }
        setScaledListOfModelImages();
        return scaledListOfModelImages;
    }

    public void createOffSpring(BufferedImage modelImage) {
        offspring.clear();
        BufferedImage offspringImage = deepCopy(modelImage);
        for(int i = 0; i < numberOfOffspring; i++) {
            offspring.add(deepCopy(offspringImage));
        }
    }

    public BufferedImage readImage(String imgPath) throws IOException {
        //  Bild lesen und zur liste hinzufügen
        try {
            f = new File(imgPath);
            return ImageIO.read(f);
        }
        catch (IOException e) {
            System.out.println(e);
        }
        return null;
    }

    //den pfad für alle bilder durchgehen
    public void loadBildListe() {
        //D:/Sergej/Sergej Schule/P5/Bilder/Testing/Test.png <- das ist der Grundpfad
        String path = "";
        for(int i = 0; i < 4; i++) {
            path = "D:/Sergej/Sergej Schule/P5/Bilder/Testing/Test" + Integer.valueOf(i+1).toString() + ".png";
            try {
                bildListe.add(readImage(path));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public BufferedImage createNewImage() {
        System.out.println("hier");
        BufferedImage newImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        return randomizeImage(newImage);
    }

    private List<Integer> colourOfOriginalImage() {
        for (int y = 0; y < originalImage.getWidth(); y++) {
            for (int x = 0; x < originalImage.getHeight(); x++) {
                int colour = originalImage.getRGB(y, x);
                System.out.println(colour);
                if (!(colourOfOriginalImage.contains(colour))) {
                      colourOfOriginalImage.add(colour);
                }
            }
        }
        return colourOfOriginalImage;
    }

    private BufferedImage randomizeImage(BufferedImage imageToBeRandomized) {
        BufferedImage rtrnImage = deepCopy(imageToBeRandomized);
        for (int y = 0; y < originalImage.getWidth(); y++) {
            for (int x = 0; x < originalImage.getHeight(); x++) {
                //System.out.println(x + "|" + y);
                int colour = colourOfOriginalImage.get(getRandomNumber(0, colourOfOriginalImage.size()-1));
                rtrnImage.setRGB(y, x, colour);
            }
        }
        return rtrnImage;
    }

    public BufferedImage mutate(BufferedImage imageToBeMutated) {
        int randomXCoord = getRandomNumber(0, originalImage.getWidth());
        int randomYCoord = getRandomNumber(0, originalImage.getHeight());
        int randomColour = colourOfOriginalImage.get(getRandomNumber(0, colourOfOriginalImage.size() - 1));


        BufferedImage mutatedImage = imageToBeMutated;
        mutatedImage.setRGB(randomXCoord, randomYCoord, randomColour);
        return mutatedImage;
    }

    public int getFitness(BufferedImage imageToGetFitness) {
        int fitnessOfImg = originalImage.getHeight() * originalImage.getWidth();
        for (int y = 0; y < originalImage.getWidth(); y++) {
            for (int x = 0; x < originalImage.getHeight(); x++) {
                if(originalImage.getRGB(x, y) == imageToGetFitness.getRGB(x, y)) {
                    fitnessOfImg--;
                }
            }
        }
        return fitnessOfImg;
    }
    public BufferedImage getOrginalImageSized() {
        return scaleImage(originalImage, calculateScale(500, 50));
    }

    public List<BufferedImage> getImageList() {
        return bildListe;
    }

    public BufferedImage getImageByIndex(int index){
        return bildListe.get(index);
    }

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public BufferedImage scaleImage(BufferedImage imageToBeScaled, int scaleFactor) {
        int w = imageToBeScaled.getWidth();
        int h = imageToBeScaled.getHeight();
        Image newImage = deepCopy(imageToBeScaled).getScaledInstance(w * scaleFactor, h * scaleFactor, Image.SCALE_DEFAULT);
        return convertToBufferedImage(newImage);
    }

    public void setScaledListOfModelImages() {
        for(BufferedImage image : listOfModelImages) {
            scaledListOfModelImages.add(scaleImage(image, calculateScale(-1, -1)));
        }
    }

    public BufferedImage convertToBufferedImage(Image image)
    {
        BufferedImage newImage = new BufferedImage(
                image.getWidth(null), image.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }

    public int calculateScale(int tempWidth, int tempHeight) {
        int tempoWidth = tempWidth;
        int tempoHeight = tempHeight;
        final int DEFAULTWIDTH = 1400;
        final int DEFAULTHEIGHT = 700;
        if(tempWidth < 0 ||tempHeight < 0) {
            tempoWidth = DEFAULTWIDTH;
            tempoHeight = DEFAULTHEIGHT;
        }
        int temporaryScaleFactor = 1;
        int temporaryHeight = originalImage.getHeight();
        int temporaryWidth = originalImage.getWidth();
        while(temporaryWidth <= tempoWidth && temporaryHeight <= tempoHeight) {
            //increment the scaleFactor;
            temporaryScaleFactor++;

            //reset to normal width and height
            if(temporaryScaleFactor!=1) {
                temporaryWidth = originalImage.getWidth();
                temporaryHeight = originalImage.getHeight();
            }

            //update the temporary width and height
            temporaryWidth*=temporaryScaleFactor;
            temporaryHeight*=temporaryScaleFactor;
        }
        return temporaryScaleFactor - 1;
    }
    private BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    public List<BufferedImage> getScaledListOfModelImages() {
        return scaledListOfModelImages;
    }

    public void test() {
        System.out.println(listOfModelImages.size());
    }

    /*    int clr = image.getRGB(x, y);
    int red = (clr & 0x00ff0000) >> 16;
    int green = (clr & 0x0000ff00) >> 8;
    int blue = clr & 0x000000ff;
    System.out.println("Red Color value = " + red);
    System.out.println("Green Color value = " + green);
    System.out.println("Blue Color value = " + blue);*/
}
