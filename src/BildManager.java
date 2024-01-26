
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import javax.imageio.ImageIO;

public class BildManager {
    //Diese Variablen lassen sich einstellen
    private final int ausgewählteMutationsFunktion = 2;
    private final double mutationsRate = 1.01;
    private final int fixMutation = 1;
    private final int individuenProGeneration = 500;
    private final int differenzZumOriginalBild = 1;
    private final int maximaleGeneration = 500;


    private ArrayList<int[]> dynamicValues;
    private File f;
    private List<BufferedImage> bildListe;
    private List<Integer> colourOfOriginalImage = new ArrayList<>();

    private int fitness;
    private int generations;
    private BufferedImage originalImage;

    private List<BufferedImage> offspring;

    private List<BufferedImage> listOfModelImages;
    private List<BufferedImage> scaledListOfModelImages;
    public BildManager() {
        offspring = new ArrayList<>();
        try {
            originalImage = readImage("D:/Sergej/Sergej Schule/P5/Bilder/Testing/Test7.png"); //Pfad des Bildes hier eingeben
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        generations = 1;
        fitness = originalImage.getHeight() * originalImage.getWidth();
        bildListe = new ArrayList<>();
        colourOfOriginalImage = colourOfOriginalImage();
        f = null;

        BufferedImage randomImage = randomizeImage(deepCopy(originalImage));
        for(int i = 0; i < individuenProGeneration; i++) {
            offspring.add(deepCopy(deepCopy(randomImage)));
        }
        listOfModelImages = new ArrayList<>();
        scaledListOfModelImages = new ArrayList<>();
        dynamicValues = new ArrayList<>();
    }


    //Hauptfunktionen des Algorithmus
    public List<BufferedImage> simulate() {
        int bestFitnessScore = originalImage.getHeight() * originalImage.getWidth();
        int secondBestFitnessScore = originalImage.getHeight() * originalImage.getWidth() + 1;
        BufferedImage parent1 = deepCopy(originalImage);
        BufferedImage parent2 = deepCopy(originalImage);
        boolean atLeastOneModelImage = false;
        while(fitness > 0 && !(getFehlerQuotient()<=differenzZumOriginalBild)) {
            ArrayList<BufferedImage> mutatedImages = new ArrayList<>();
            if(generations > maximaleGeneration) {
                break;
            }
            BufferedImage mutatedImage = createNewImage();
            for(BufferedImage img : offspring) {
                if(ausgewählteMutationsFunktion == 1) {
                    mutatedImage = mutationsFunktionFix(deepCopy(img));
                } else { //ausgewählteMutationsFunktion == 2
                    mutatedImage = mutationsRateFunktion(deepCopy(img));
                }

                mutatedImages.add(deepCopy(mutatedImage));
                int fitnessScoreOfImg = getFitness(deepCopy(mutatedImage));
                if(fitnessScoreOfImg < bestFitnessScore) {
                    secondBestFitnessScore = bestFitnessScore;
                    bestFitnessScore = fitnessScoreOfImg;
                    fitness = bestFitnessScore;
                    parent1 = deepCopy(mutatedImage);
                    atLeastOneModelImage = true;
                } else if (fitnessScoreOfImg < secondBestFitnessScore) {
                    secondBestFitnessScore = fitnessScoreOfImg;
                    parent2 = deepCopy(mutatedImage);
                }
            }
            if(atLeastOneModelImage) {
                listOfModelImages.add(deepCopy(parent1));
                dynamicValues.add(new int[]{generations, fitness, getFehlerQuotient()});
            }
            newCreateOffSpring(deepCopy(parent1), deepCopy(parent2));
            generations++;
            atLeastOneModelImage = false;
        }
        setScaledListOfModelImages();
        return scaledListOfModelImages;
    }

    //Diese Funktion mutiert fix so viele Pixel, wie die Variable "fixMutation" groß ist. Diese Mutation passieren an zufälligen Stellen im Bild
    public BufferedImage mutationsFunktionFix(BufferedImage imageToBeMutated) {
        BufferedImage mutatedImage = deepCopy(imageToBeMutated);

        for(int i = 0; i < fixMutation; i++) {
            int randomXCoord = getRandomNumber(0, originalImage.getWidth());
            int randomYCoord = getRandomNumber(0, originalImage.getHeight());
            int randomColour = colourOfOriginalImage.get(getRandomNumber(0, colourOfOriginalImage.size()));

            while(randomColour == imageToBeMutated.getRGB(randomXCoord, randomYCoord) || randomColour == mutatedImage.getRGB(randomXCoord, randomYCoord)) {
                randomColour = colourOfOriginalImage.get(getRandomNumber(0, colourOfOriginalImage.size()));
            }

            mutatedImage.setRGB(randomXCoord, randomYCoord, randomColour);
        }

        return mutatedImage;
    }

    //Bei dieser Funktion hat jeder Pixel im Bild, eine Chance zur Mutation, diese Chance ist gleich der Variable "mutationsRate" in Prozent.
    public BufferedImage mutationsRateFunktion(BufferedImage imageToBeMutated) {
        BufferedImage mutatedImage = deepCopy(imageToBeMutated);

        for (int y = 0; y < deepCopy(originalImage).getHeight(); y++) {
            for (int x = 0; x < deepCopy(originalImage).getWidth(); x++) {
                double percentage = getRndmDouble(100.00000000000001, 0);

                if(mutationsRate + 0.00000000000001 >=percentage) {
                    int randomColour = colourOfOriginalImage.get(getRandomNumber(0, colourOfOriginalImage.size()));

                    while(randomColour == imageToBeMutated.getRGB(x, y)) {
                        randomColour = colourOfOriginalImage.get(getRandomNumber(0, colourOfOriginalImage.size()));
                    }

                    mutatedImage.setRGB(x, y, randomColour);
                }
            }
        }
        return mutatedImage;
    }
    public BufferedImage crossoverGen(BufferedImage parent1, BufferedImage parent2) {
        ArrayList<Integer[]> genesOfParent1 = getGenes(parent1);
        ArrayList<Integer[]> genesOfParent2 = getGenes(parent2);

        BufferedImage imageToBeCrossovered = createNewImage();
        ArrayList<Integer[]> genesOfChild = new ArrayList<>();

        for(int i = 0; i < originalImage.getHeight(); i++) {
            //Gibt 1 oder 2 zurück; 1 -> parent1 / 2 -> parent2
            int parent = getRandomNumber(1,3);
            if(parent == 1) {
                genesOfChild.add(genesOfParent1.get(i));
            } else { //parent == 2
                genesOfChild.add(genesOfParent2.get(i));
            }
        }

        for (int y = 0; y < originalImage.getHeight(); y++) {
            for (int x = 0; x < originalImage.getWidth(); x++) {
                imageToBeCrossovered.setRGB(x, y, genesOfChild.get(y)[x]);
            }
        }
        return imageToBeCrossovered;
    }


    public void newCreateOffSpring(BufferedImage parent1, BufferedImage parent2) {
        offspring = new ArrayList<>();
        for(int i = 0; i < individuenProGeneration; i++) {

            offspring.add(deepCopy(crossoverGen(deepCopy(parent1), deepCopy(parent2))));

        }
    }



    //Funktionen die etwas mit dem Algorithmus zu tun haben, jedoch keine Hauptfunktionen sind

    public ArrayList<Integer[]> getGenes(BufferedImage imageToGetGenes) {
        BufferedImage geneImage = deepCopy(imageToGetGenes);

        ArrayList<Integer> gene = new ArrayList<>();
        ArrayList<Integer[]> genes = new ArrayList<>();
        for (int y = 0; y < geneImage.getHeight(); y++) {
            for (int x = 0; x < geneImage.getWidth(); x++) {
                int colourOfSpot = geneImage.getRGB(x, y);
                gene.add(colourOfSpot);
            }
            Integer[] geneAsArray = gene.toArray(new Integer[gene.size()]);
            genes.add(geneAsArray);
            gene.clear();
        }
        return genes;
    }
    public int getFitness(BufferedImage imageToGetFitness) {

        int fitnessOfImg = 0;
        for (int y = 0; y < deepCopy(originalImage).getHeight(); y++) {
            for (int x = 0; x < deepCopy(originalImage).getWidth(); x++) {
                if(deepCopy(originalImage).getRGB(x, y) != deepCopy(imageToGetFitness).getRGB(x, y)) {
                    fitnessOfImg++;
                }
            }
        }
        return fitnessOfImg;
    }
    public BufferedImage createNewImage() {
        BufferedImage newImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        return randomizeImage(newImage);
    }

    private List<Integer> colourOfOriginalImage() {
        for (int y = 0; y < originalImage.getWidth(); y++) {
            for (int x = 0; x < originalImage.getHeight(); x++) {
                int colour = originalImage.getRGB(y, x);
                if (!(colourOfOriginalImage.contains(colour))) {
                    colourOfOriginalImage.add(colour);
                }
            }
        }
        return colourOfOriginalImage;
    }
    private BufferedImage randomizeImage(BufferedImage imageToBeRandomized) {
        BufferedImage rtrnImage = deepCopy(imageToBeRandomized);
        for (int y = 0; y < originalImage.getHeight(); y++) {
            for (int x = 0; x < originalImage.getWidth(); x++) {
                //System.out.println(x + "|" + y);
                int colour = colourOfOriginalImage.get(getRandomNumber(0, colourOfOriginalImage.size()-1));
                rtrnImage.setRGB(x, y, colour);
            }
        }
        return rtrnImage;
    }
    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
    private double getRndmDouble(double max, double min) {
        double randomValue = ThreadLocalRandom.current().nextDouble(min, max);
        return randomValue;
    }

    public int getFehlerQuotient() {
        int originalFitness = originalImage.getWidth() * originalImage.getHeight();
        Double fq = (((fitness *1.0)/(originalFitness *1.0)) *100);
        return fq.intValue();
    }

    public double[] getStaticValues() {
        if(ausgewählteMutationsFunktion == 1) {
            return new double[] {individuenProGeneration, fixMutation, ausgewählteMutationsFunktion, maximaleGeneration, differenzZumOriginalBild};
        } else { //ausgewählteMutationsFunktion == 2
            return new double[] {individuenProGeneration, mutationsRate, ausgewählteMutationsFunktion, maximaleGeneration, differenzZumOriginalBild};
        }

    }
    public ArrayList<int[]> getDynamicValues() {
        return dynamicValues;
    }



    //Setup Funktionen, die dafür nötig sind, dass das Programm laufen kann, aber nichts mit dem Algorithmus zu tun haben.

    public BufferedImage readImage(String imgPath) throws IOException {
        try {
            f = new File(imgPath);
            return ImageIO.read(f);
        }
        catch (IOException e) {
            System.out.println(e);
        }
        return null;
    }

    public int calculateScale(int tempWidth, int tempHeight) {
        int tempoWidth = tempWidth;
        int tempoHeight = tempHeight;
        final int DEFAULTWIDTH = 500;
        final int DEFAULTHEIGHT = 500;
        if(tempWidth < 0 ||tempHeight < 0) {
            tempoWidth = DEFAULTWIDTH;
            tempoHeight = DEFAULTHEIGHT;
        }
        int temporaryScaleFactor = 1;
        int temporaryHeight = originalImage.getHeight();
        int temporaryWidth = originalImage.getWidth();
        while(temporaryWidth <= tempoWidth && temporaryHeight <= tempoHeight) {

            temporaryScaleFactor++;


            if(temporaryScaleFactor!=1) {
                temporaryWidth = originalImage.getWidth();
                temporaryHeight = originalImage.getHeight();
            }


            temporaryWidth*=temporaryScaleFactor;
            temporaryHeight*=temporaryScaleFactor;
        }
        return temporaryScaleFactor - 1;
    }
    public BufferedImage scaleImage(BufferedImage imageToBeScaled, int scaleFactor) {
        int w = imageToBeScaled.getWidth();
        int h = imageToBeScaled.getHeight();
        Image newImage = deepCopy(imageToBeScaled).getScaledInstance(w * scaleFactor, h * scaleFactor, Image.SCALE_DEFAULT);
        return convertToBufferedImage(newImage);
    }
    public void setScaledListOfModelImages() {
        for(BufferedImage image : listOfModelImages) {
            scaledListOfModelImages.add(scaleImage(deepCopy(image), calculateScale(-1, -1)));
        }
    }
    public BufferedImage getOrginalImageSized() {
        return scaleImage(deepCopy(originalImage), calculateScale(-1, -1));
    }
    public List<BufferedImage> getImageList() {
        return bildListe;
    }

    public BufferedImage convertToBufferedImage(Image image) {
        BufferedImage newImage = new BufferedImage(
                image.getWidth(null), image.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }
    private BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

}
