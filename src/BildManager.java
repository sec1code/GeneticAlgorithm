
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

public class BildManager {
    //Ab wie viel Prozent Ähnlichkeit zum Original bild abgebrochen werden soll
    private final int fehlerQuotient = 1;

    //Diese 3 Werte, bestimmen welche Funktionen angewendet werden sollen. Es Gibt alternativen zu jedem.
    private final int mutationFunktion = 1; //1 == "mutationsFunktionFix" / 2 == "mutationsRateFunktion"
    private final int fitnessFunktion = 1;
    private final int crossoverFunktion = 1; //1 == "crossoverGen" / 2 == "crossoverPixel"


    //die mutationsrate
    private final int mutationRate = 1; // 1 - 100
    //eine fixe Mutation Zahl, welche genause viele Pixel pro Bild mutiert
    private final int fixMutation = 1;
    //die Nummer an "Nachfahren" in der nächsten Generation
    private final int numberOfOffspring = 500;
    //der "harte" Cap bei den Generationen
    private final int hartCapGeneration = 500;

    private ArrayList<int[]> dynamicValues;
    private File f;

    //um ehrlich zu sein, ich weiß nicht mehr, wozu es diese Liste gibt.
    private List<BufferedImage> bildListe;
    private List<Integer> colourOfOriginalImage = new ArrayList<>();

    //der wert der die fitness des derzeitigen Model Bilds beschreibt
    private int fitness;
    //selbsterklärend, einfach die derzeitige Generation.
    private int generations;
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
        generations = 1;
        fitness = originalImage.getHeight() * originalImage.getWidth();
        bildListe = new ArrayList<>();
        colourOfOriginalImage = colourOfOriginalImage();
        f = null;
        loadBildListe();

        BufferedImage randomImage = randomizeImage(deepCopy(originalImage));
        for(int i = 0; i < numberOfOffspring; i++) {
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
        while(fitness > 0 && !(getFehlerQuotient()<=fehlerQuotient)) {
            ArrayList<BufferedImage> mutatedImages = new ArrayList<>();
            if(generations > hartCapGeneration) {
                break;
            }
            BufferedImage mutatedImage = createNewImage();
            for(BufferedImage img : offspring) {
                if(mutationFunktion == 1) {
                    mutatedImage = mutationsFunktionFix(deepCopy(img));
                } else { //mutationFunktion == 2
                    mutatedImage = mutationsRateFunktion(deepCopy(img));
                }

                mutatedImages.add(deepCopy(mutatedImage));
                int fitnessScoreOfImg = getFitness(deepCopy(mutatedImage));
                if(fitnessScoreOfImg < bestFitnessScore) {
                    secondBestFitnessScore = bestFitnessScore;
                    bestFitnessScore = fitnessScoreOfImg;
                    fitness = bestFitnessScore;
                    parent1 = deepCopy(mutatedImage);
                    System.out.println("BestFitnessOfImg: " + bestFitnessScore);
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
            System.out.println("Size: " + listOfModelImages.size());
            newCreateOffSpring(deepCopy(parent1), deepCopy(parent2));
            System.out.println("Gens: " + generations);
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

    //Bei dieser Funktion hat jeder Pixel im Bild, eine Chance zur Mutation, diese Chance ist gleich der Variable "mutationRate" in Prozent.
    public BufferedImage mutationsRateFunktion(BufferedImage imageToBeMutated) {
        BufferedImage mutatedImage = deepCopy(imageToBeMutated);

        for (int y = 0; y < deepCopy(originalImage).getHeight(); y++) {
            for (int x = 0; x < deepCopy(originalImage).getWidth(); x++) {
                int percentage = getRandomNumber(1, 101);

                if(mutationRate>=percentage) {
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

        for(int i = 0; i < originalImage.getWidth(); i++) {
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

    public BufferedImage crossoverPixel(BufferedImage parent1, BufferedImage parent2) {
        ArrayList<Integer> pixelOfParent1 = getPixel(parent1);
        ArrayList<Integer> pixelOfParent2 = getPixel(parent2);

        BufferedImage imageToBeCrossovered = createNewImage();

        for (int y = 0; y < originalImage.getHeight(); y++) {
            for (int x = 0; x < originalImage.getWidth(); x++) {
                int parent = getRandomNumber(1,3);
                if(parent == 1) {
                    imageToBeCrossovered.setRGB(x, y, pixelOfParent1.get(x+y));
                } else { //parent == 2
                    imageToBeCrossovered.setRGB(x, y, pixelOfParent2.get(x+y));
                }
            }
        }
        return imageToBeCrossovered;
    }

    public void newCreateOffSpring(BufferedImage parent1, BufferedImage parent2) {
        offspring = new ArrayList<>();
        for(int i = 0; i < numberOfOffspring; i++) {
            if(crossoverFunktion == 1) {
                offspring.add(deepCopy(crossoverGen(deepCopy(parent1), deepCopy(parent2))));
            } else { //crossoverFunktion == 2
                offspring.add(deepCopy(crossoverPixel(deepCopy(parent1), deepCopy(parent2))));
            }
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

    public ArrayList<Integer> getPixel(BufferedImage imageToGetPixel) {
        BufferedImage pixelImage = deepCopy(imageToGetPixel);
        ArrayList<Integer> pixel = new ArrayList<>();
        for (int y = 0; y < deepCopy(originalImage).getHeight(); y++) {
            for (int x = 0; x < deepCopy(originalImage).getWidth(); x++) {
                int colourOfSpot = pixelImage.getRGB(x, y);
                pixel.add(colourOfSpot);
            }
        }
       return pixel;
    }
    public int getFitness(BufferedImage imageToGetFitness) {

        int fitnessOfImg = 0;
        // System.out.println("Fitnessofimg: " + fitnessOfImg);
        for (int y = 0; y < deepCopy(originalImage).getHeight(); y++) {
            for (int x = 0; x < deepCopy(originalImage).getWidth(); x++) {
                if(deepCopy(originalImage).getRGB(x, y) != deepCopy(imageToGetFitness).getRGB(x, y)) {
                    //System.out.println(y +"|"+  x + "FitnessofimgINLOOP: " + fitnessOfImg+ "." + originalImage.getRGB(x, y) + " " + imageToGetFitness.getRGB(x, y));
                    fitnessOfImg++;
                }
            }
        }
        return fitnessOfImg;
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
        for (int y = 0; y < originalImage.getHeight(); y++) {
            for (int x = 0; x < originalImage.getWidth(); x++) {
                //System.out.println(x + "|" + y);
                int colour = colourOfOriginalImage.get(getRandomNumber(0, colourOfOriginalImage.size()-1));
                rtrnImage.setRGB(y, x, colour);
            }
        }
        return rtrnImage;
    }
    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public int getFehlerQuotient() {
        int originalFitness = originalImage.getWidth() * originalImage.getHeight();
        Double fq = (((fitness *1.0)/(originalFitness *1.0)) *100);
        return fq.intValue();
    }

    //Dynamic values:

    //Best Fitness
    //Generations + Hart Cap

    //Static values:

    //Number Of Offspring
    //Mutation Rate
    //Fix Mutation
    //Mutation Funktion (es gibt verschiedene)
    //Fitness Funktion (es gibt verschiedene)
    //Crossover Funktion (es gibt verschiedene)

    public int[] getStaticValues() {
        if(mutationFunktion == 1) {
            return new int[] {numberOfOffspring, fixMutation, mutationFunktion, fitnessFunktion, crossoverFunktion, hartCapGeneration, fehlerQuotient};
        } else { //mutationFunktion == 2
            return new int[] {numberOfOffspring, mutationRate, mutationFunktion, fitnessFunktion, crossoverFunktion, hartCapGeneration, fehlerQuotient};
        }

    }
    public ArrayList<int[]> getDynamicValues() {
        return dynamicValues;
    }



    //Setup Funktionen, die dafür nötig sind, dass das Programm laufen kann, aber nichts mit dem Algorithmus zu tun haben.

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
    public BufferedImage getImageByIndex(int index){
        return bildListe.get(index);
    }



    //Outdated/Debug Funktionen + Nützliche Kommentare
    public List<BufferedImage> getScaledListOfModelImages() {
        return scaledListOfModelImages;
    }
    public void test() {
        System.out.println(listOfModelImages.size());
    }
    public void createOffSpring(BufferedImage modelImage) {
        offspring = new ArrayList<>();
        BufferedImage offspringImage = deepCopy(modelImage);
        for(int i = 0; i < numberOfOffspring; i++) {
            offspring.add(deepCopy(offspringImage));
        }
    }

    /*    int clr = image.getRGB(x, y);
    int red = (clr & 0x00ff0000) >> 16;
    int green = (clr & 0x0000ff00) >> 8;
    int blue = clr & 0x000000ff;
    System.out.println("Red Color value = " + red);
    System.out.println("Green Color value = " + green);
    System.out.println("Blue Color value = " + blue);*/

    /*if (num > max1st) {
        max2nd = max1st;
        max1st = num;
    } else if (num > max2nd) {
        max2nd = num;
    }*/
}
