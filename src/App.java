import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class App extends JFrame implements ActionListener
{
    private JButton selectImageButton;
    private JTextField pathTF;
    private JTextField currentSizeTF;
    private JTextField widthTF;
    private JTextField heightTF;
    private JSlider qualitySlider;
    private JList preferenceList;
    private JButton checkSizeButton;
    private JButton saveButton;
    private JPanel rootPanel;
    private JTabbedPane tabbedPane1;
    private JCheckBox maintainAspectRatioCheckBox;

    static String path;
    static int widthval,heightval;

    public App()
    {
        add(rootPanel);
        setSize(600,500);
        selectImageButton.addActionListener(this);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        String s=e.getActionCommand();
        if(s.equals("Select Image"))
        {
            JFileChooser j=new JFileChooser();
            j.addChoosableFileFilter(new ImageFilter());
            j.setAcceptAllFileFilterUsed(false);
            int r=j.showOpenDialog(null);
            if (r == JFileChooser.APPROVE_OPTION)
            {
                path=j.getSelectedFile().getAbsolutePath();
                pathTF.setText(path);
                try {
                    File inputFile = new File(path);
                    BufferedImage img= ImageIO.read(inputFile);
                    widthval= img.getWidth();
                    heightval= img.getHeight();
                    widthTF.setText(String.valueOf(widthval));
                    heightTF.setText(String.valueOf(heightval));
                    currentSizeTF.setText((float) inputFile.length() / 1024 + "  kb");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
