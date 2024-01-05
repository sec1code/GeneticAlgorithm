import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class GenetischerAlgorithmus extends JFrame{
    private int imageIndex = 0;
    private BildManager bildManager = new BildManager();
    private List<BufferedImage> listOfModelImages = bildManager.simulate();
    private BufferedImage randomBild = bildManager.createNewImage();
    private List<BufferedImage> bildListe = bildManager.getImageList();
   /* private List<ImageIcon> imageIconListe = new ArrayList<>();
    private List<JLabel> jLabelImageListe = new ArrayList<>();*/
    private JPanel mainPanel;
    private JPanel BildPanel;
    private JPanel ButtonPanel;
    private BorderLayout layoutForBildPanel = new BorderLayout();
    private JButton start;
    private JButton rewind;
    private JButton forward;
    private JButton end;

    private boolean mutation = true;

    public GenetischerAlgorithmus() {
        layoutForBildPanel.setHgap(50);
        layoutForBildPanel.setVgap(20);
        BildPanel.setLayout(layoutForBildPanel);
        setContentPane(mainPanel);
        setFocusable(false);
        setTitle("Genetischer Algorithmus");
        //setSize(1920,1080);
        setSize(Toolkit.getDefaultToolkit().getScreenSize());
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);


        run();
    }

    private void run() {

        int[] staticInfo = bildManager.getStaticValues();
        if(staticInfo[2]==1) {
            mutation = false;
        }

        ArrayList<int[]> dynamicInfo = bildManager.getDynamicValues();
        JLabel infoText = new JLabel();

       setText(infoText, staticInfo, dynamicInfo, imageIndex);

        JLabel bild = new JLabel(new ImageIcon(listOfModelImages.get(0)));
        JLabel originalBild = new JLabel(new ImageIcon(bildManager.getOrginalImageSized()));

        originalBild.setSize(500, 500);
        bild.setSize(1400, 700);

        BildPanel.add(infoText, BorderLayout.CENTER);
        BildPanel.add(bild, BorderLayout.EAST);
        BildPanel.add(originalBild, BorderLayout.WEST);

        forward.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if(imageIndex != listOfModelImages.size() - 1) {
                    imageIndex++;
                }

                System.out.println("ImageIndex: " + imageIndex);
                bild.setIcon(new ImageIcon(listOfModelImages.get(imageIndex)));
                setText(infoText, staticInfo, dynamicInfo, imageIndex);
            }
        });

        rewind.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if(imageIndex!=0) {
                    imageIndex--;
                }
                bild.setIcon(new ImageIcon(listOfModelImages.get(imageIndex)));
                setText(infoText, staticInfo, dynamicInfo, imageIndex);
            }
        });

        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                imageIndex = 0;
                //bild.setIcon(new ImageIcon(bildManager.getImageByIndex(imageIndex)));
                bild.setIcon(new ImageIcon(listOfModelImages.get(0)));
                setText(infoText, staticInfo, dynamicInfo, imageIndex);
            }
        });

        end.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                imageIndex = listOfModelImages.size() - 1;
                bild.setIcon(new ImageIcon(listOfModelImages.get(imageIndex)));
                setText(infoText, staticInfo, dynamicInfo, imageIndex);
            }
        });
    }

    private void setText(JLabel infoText, int[] staticInfo, ArrayList<int[]> dynamicInfo, int imageIndex) {
        String mutationsTyp = "Mutations Rate: ";
        if(mutation) {
            mutationsTyp = "Fixe Mutations: ";
        }

        infoText.setText("<html><body>Fitness Funktion: "+ staticInfo[3] + "<br>Crossover Funktion: " + staticInfo[4] +
                "<br>Mutations Funktion: "+ staticInfo[2] + "<br>" + mutationsTyp + staticInfo[1] +
                "<br>Zahl der Individuen: " + staticInfo[0] + "<br>Generationen: " + dynamicInfo.get(imageIndex)[0] + ", harter Cap bei: "+ staticInfo[5] +
                "<br>Beste Fitness: " + dynamicInfo.get(imageIndex)[1] +"<br>Fehler Quotient: " + dynamicInfo.get(imageIndex)[2] + "%, Cap bei: " + staticInfo[6] + "%" + "<br>Links zu sehen ist das Original Bild, rechts zu sehen ist das bisher beste Bild</body></html>");

    }


}
