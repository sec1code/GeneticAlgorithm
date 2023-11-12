
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class BildManager {
    private final double mutationRate = 1.0;
    private final int numberOfOffspring = 8;
    private File f;

    private List<BufferedImage> bildListe;
    private List<Integer> colourOfOriginalImage = new ArrayList<>();

    private int fitness;
    private int generations = 1;
    private BufferedImage originalImage;
    //das vorbild bild / das zurzeit beste bild
    private BufferedImage modelImage;
    private List<BufferedImage> offspring;

    private List<BufferedImage> listOfModelImages;
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
        modelImage = originalImage;
        listOfModelImages = new ArrayList<>();
    }

    // returns the modelImage
    public List<BufferedImage> simulate() {
        //Artificial cap, so that the programm can stop even if fitness != 0
        while(fitness > 0 || generations <= 100) {

            int bestFitnessScore = getFitness(offspring.get(0));
            for(BufferedImage image : offspring) {
                //images mutate
                for(int i = 0; i < (int) mutationRate; i++) {
                    double fixMutationRate = (mutationRate - (int) mutationRate) * 10;
                    if(fixMutationRate > getRandomNumber(0, 10)) {
                        mutate(image);
                    }
                }

                //calculating fitness values + setting new model image
                int temporaryFitnessScore = getFitness(image);
                if(temporaryFitnessScore < bestFitnessScore) {
                    bestFitnessScore = temporaryFitnessScore;
                    modelImage = image;
                }
            }
            //creating new offspring off of modelImage
            createOffSpring(modelImage);
            generations++;
            listOfModelImages.add(modelImage);
        }
        return listOfModelImages;
    }

    public void createOffSpring(BufferedImage modelImage) {
        offspring.clear();
        for(int i = 0; i < numberOfOffspring; i++) {
            offspring.add(modelImage);
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
        BufferedImage newImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
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
        for (int y = 0; y < originalImage.getWidth(); y++) {
            for (int x = 0; x < originalImage.getHeight(); x++) {
                System.out.println(x + "|" + y);
                int colour = colourOfOriginalImage.get(getRandomNumber(0, colourOfOriginalImage.size()-1));
                imageToBeRandomized.setRGB(y, x, colour);
            }
        }
        return imageToBeRandomized;
    }

    public void mutate(BufferedImage imageToBeMutated) {
        int randomXCoord = getRandomNumber(0, originalImage.getWidth());
        int randomYCoord = getRandomNumber(0, originalImage.getHeight());
        int randomColour = colourOfOriginalImage.get(getRandomNumber(0, colourOfOriginalImage.size() - 1));



    }

    public int getFitness(BufferedImage imageToGetFitness) {
        for (int y = 0; y < originalImage.getWidth(); y++) {
            for (int x = 0; x < originalImage.getHeight(); x++) {
                if(originalImage.getRGB(x, y) == imageToGetFitness.getRGB(x, y)) {
                    fitness--;
                }
            }
        }
        return fitness;
    }
    public BufferedImage getOrginalImage() {
        return originalImage;
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

    /*    int clr = image.getRGB(x, y);
    int red = (clr & 0x00ff0000) >> 16;
    int green = (clr & 0x0000ff00) >> 8;
    int blue = clr & 0x000000ff;
    System.out.println("Red Color value = " + red);
    System.out.println("Green Color value = " + green);
    System.out.println("Blue Color value = " + blue);*/
}
