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
    private JButton start;
    private JButton rewind;
    private JButton forward;
    private JButton end;



    public GenetischerAlgorithmus() {

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
        JLabel bild = new JLabel(new ImageIcon(bildManager.getImageByIndex(imageIndex)));
        JLabel originalBild = new JLabel(new ImageIcon(bildManager.getOrginalImageSized()));
        originalBild.setSize(500, 500);
        bild.setSize(1400, 700);
        BildPanel.add(bild);

        forward.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if(imageIndex != listOfModelImages.size() - 1) {
                    imageIndex++;
                }

                System.out.println("ImageIndex: " + imageIndex);
                bild.setIcon(new ImageIcon(listOfModelImages.get(imageIndex)));

            }
        });

        rewind.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if(imageIndex!=0) {
                    imageIndex--;
                }
                bild.setIcon(new ImageIcon(listOfModelImages.get(imageIndex)));
            }
        });

        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                imageIndex = 0;
                //bild.setIcon(new ImageIcon(bildManager.getImageByIndex(imageIndex)));
                bild.setIcon(new ImageIcon(listOfModelImages.get(0)));
            }
        });

        end.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                imageIndex = listOfModelImages.size() - 1;
                bild.setIcon(new ImageIcon(listOfModelImages.get(imageIndex)));
            }
        });
    }


}
