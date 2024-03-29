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

    private boolean mutation = false;

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

        double[] staticInfo = bildManager.getStaticValues();
        if(((int)staticInfo[2]) == 1) {
            mutation = true;
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

    private void setText(JLabel infoText, double[] staticInfo, ArrayList<int[]> dynamicInfo, int imageIndex) {
        if(mutation) {
            infoText.setText("<html><body>Mutations Funktion: "+ ((int) staticInfo[2]) + "<br>" + "Fixe Mutation: " + ((int)staticInfo[1]) +
                    "<br>Zahl der Individuen: " + ((int)staticInfo[0]) + "<br>Generation: " + dynamicInfo.get(imageIndex)[0] + ", Abbruch bei: "+ ((int)staticInfo[3]) +
                    "<br>Beste Fitness: " + dynamicInfo.get(imageIndex)[1] +"<br>Aktuelle Differenz zum Originalbild: " + dynamicInfo.get(imageIndex)[2] + "%, Abbruch bei: " + ((int)staticInfo[4]) + "%" + "<br>Links zu sehen ist das Original Bild, rechts zu sehen ist das bisher beste Bild.</body></html>");

        } else {
            infoText.setText("<html><body>Mutations Funktion: "+ ((int) staticInfo[2]) + "<br>" + "Mutations Rate: " + staticInfo[1] + "%" +
                    "<br>Zahl der Individuen: " + ((int)staticInfo[0]) + "<br>Generation: " + dynamicInfo.get(imageIndex)[0] + ", Abbruch bei: "+ ((int)staticInfo[3]) +
                    "<br>Beste Fitness: " + dynamicInfo.get(imageIndex)[1] +"<br>Aktuelle Differenz zum Originalbild: " + dynamicInfo.get(imageIndex)[2] + "%, Abbruch bei: " + ((int)staticInfo[4]) + "%" + "<br>Links zu sehen ist das Original Bild, rechts zu sehen ist das bisher beste Bild.</body></html>");

        }


    }


}
