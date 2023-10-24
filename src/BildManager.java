
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class BildManager {
    private File f;

    private List<BufferedImage> bildListe;

    private BufferedImage orginalImage;
    public BildManager() {
        try {
            orginalImage = readImage("D:/Sergej/Sergej Schule/P5/Bilder/Testing/Test5.png");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        bildListe = new ArrayList<>();
        f = null;
        loadBildListe();
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
        BufferedImage newImage = new BufferedImage(orginalImage.getWidth(), orginalImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        return randomizeImage(newImage);
    }

    private List<Integer> colourOfOriginalImage() {
        List<Integer> colourOfOriginalImage = new ArrayList<>();

        for (int y = 0; y < orginalImage.getWidth(); y++) {
            for (int x = 0; x < orginalImage.getHeight(); x++) {
                int colour = orginalImage.getRGB(y, x);
                System.out.println(colour);
                if (!(colourOfOriginalImage.contains(colour))) {
                    colourOfOriginalImage.add(colour);
                }
            }
        }
        return colourOfOriginalImage;
    }

    private BufferedImage randomizeImage(BufferedImage imageToBeRandomized) {
        List<Integer> colourOfImage = colourOfOriginalImage();
        for (int y = 0; y < orginalImage.getWidth(); y++) {
            for (int x = 0; x < orginalImage.getHeight(); x++) {
                System.out.println(x + "|" + y);
                int colour = colourOfImage.get(getRandomNumber(0, colourOfImage.size()-1));
                imageToBeRandomized.setRGB(y, x, colour);
            }
        }
        return imageToBeRandomized;
    }

    public BufferedImage getOrginalImage() {
        return orginalImage;
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
