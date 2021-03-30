import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.StringTokenizer;

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
    private JButton addButton;
    private JButton editButton;

    static String path;
    static int widthval,heightval,flag=0;
    static float ratio=1f;

    static DefaultListModel listModel;

    public App()
    {
        listModel = new DefaultListModel();
        preferenceList.setModel(listModel);
        preferenceList.addListSelectionListener(new ListSelectionListener() {

        @Override
        public void valueChanged(ListSelectionEvent arg0) {
            if (!arg0.getValueIsAdjusting()) {
                try {
                    JList source = (JList) arg0.getSource();
                    String selected = source.getSelectedValue().toString();
                    File fin = new File("settings/options.txt");
                    FileReader fr = new FileReader(fin);
                    BufferedReader br = new BufferedReader(fr);
                    String line;
                    String id,width,height;
                    while ((line = br.readLine()) != null) {
                        StringTokenizer tk = new StringTokenizer(line, "|");
                        StringTokenizer tk2 = new StringTokenizer(tk.nextToken(), ":");
                        id = tk2.nextToken();
                        id = tk2.nextToken();
                        if(id.equals(selected))
                        {
                            tk2 = new StringTokenizer(tk.nextToken(), ":");
                            width=tk2.nextToken();
                            width=tk2.nextToken();
                            widthTF.setText(width);
                            tk2 = new StringTokenizer(tk.nextToken(), ":");
                            height=tk2.nextToken();
                            height=tk2.nextToken();
                            heightTF.setText(height);
                        }
                    }
                }
                catch (Exception e)
                {

                }
            }
        }
    });
        add(rootPanel);
        setSize(600,500);
        maintainAspectRatioCheckBox.setSelected(true);
        selectImageButton.addActionListener(this);
        checkSizeButton.addActionListener(this);
        saveButton.addActionListener(this);
        addButton.addActionListener(this);
        editButton.addActionListener(this);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        File directory = new File("settings/options.txt");
        if (directory.exists()) {
            try {
                setList();
            }
            catch (Exception e)
            {

            }
        }

        widthTF.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if(!widthTF.getText().equals("") && maintainAspectRatioCheckBox.isSelected()) {
                    JTextField textField = (JTextField) e.getSource();
                    String text = textField.getText();
                    float width = Float.parseFloat(text);
                    float height = width / ratio;
                    heightTF.setText(String.valueOf((int)height));
                }
            }
        });

        heightTF.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if(!widthTF.getText().equals("") && maintainAspectRatioCheckBox.isSelected()) {
                    JTextField textField = (JTextField) e.getSource();
                    String text = textField.getText();
                    float height = Float.parseFloat(text);
                    float width = height * ratio;
                    widthTF.setText(String.valueOf((int)width));
                }
            }
        });
    }

    public static void addToOptions(String ID,String width, String height) throws IOException
    {
        File fout = new File("settings/options.txt");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(fout, true);
        }
        catch (FileNotFoundException e)
        {
            Files.createDirectories(Paths.get("settings"));
            fos = new FileOutputStream(fout, true);
        }
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        bw.write("Id:"+ID+"|width:"+width+"|height:"+height);
        listModel.addElement(ID);
        bw.newLine();
        bw.close();
    }

    public static void setList() throws IOException {
        File fin = new File("settings/options.txt");
        FileReader fr=new FileReader(fin);
        BufferedReader br=new BufferedReader(fr);
        String line;
        String id;
        int i=0;
        while((line=br.readLine())!=null)
        {
            StringTokenizer tk=new StringTokenizer(line,"|");
            StringTokenizer tk2=new StringTokenizer(tk.nextToken(),":");
            id=tk2.nextToken();
            id=tk2.nextToken();
            listModel.add(i,id);
            i++;
        }
    }

    public static void resize(String inputImagePath,String outputImagePath, int scaledWidth, int scaledHeight) throws IOException
    {
        // reads input image
        File inputFile = new File(inputImagePath);
        BufferedImage inputImage = ImageIO.read(inputFile);

        // creates output image
        BufferedImage outputImage = new BufferedImage(scaledWidth,scaledHeight, inputImage.getType());

        // scales the input image to the output image
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();

        // extracts extension of output file
        String formatName = outputImagePath.substring(outputImagePath.lastIndexOf(".") + 1);

        // writes to output file
        ImageIO.write(outputImage, formatName, new File(outputImagePath));
    }

    public static void resize(String inputImagePath,String outputImagePath, float quality) throws IOException
    {
        File imageFile = new File(inputImagePath);
        File compressedImageFile = new File(outputImagePath);

        // create a BufferedImage as the result of decoding the supplied InputStream
        BufferedImage image = ImageIO.read(new FileInputStream(imageFile));

        // get all image writers for JPG format
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");

        if (!writers.hasNext())
            throw new IllegalStateException("No writers found");

        ImageWriter writer = (ImageWriter) writers.next();
        ImageOutputStream ios = ImageIO.createImageOutputStream(new FileOutputStream(compressedImageFile));
        writer.setOutput(ios);

        ImageWriteParam param = writer.getDefaultWriteParam();

        // compress to a given quality
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(quality);

        // appends a complete image stream containing a single image and
        //associated stream and image metadata and thumbnails to the output
        writer.write(null, new IIOImage(image, null, null), param);

        // close all streams
        ios.close();
        writer.dispose();
    }

    public static void deleteTemp(String f1,String f2)
    {
        try
        {
            if(!f2.equals(" "))
                Files.deleteIfExists(Paths.get(f2));
            Files.deleteIfExists(Paths.get(f1));
        }
        catch(NoSuchFileException e)
        {
            System.out.println("No such file/directory exists");
        }
        catch(DirectoryNotEmptyException e)
        {
            System.out.println("Directory is not empty.");
        }
        catch(IOException e)
        {
            System.out.println("Invalid permissions.");
        }
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
                    ratio=(float)widthval/heightval;
                    widthTF.setText(String.valueOf(widthval));
                    heightTF.setText(String.valueOf(heightval));
                    currentSizeTF.setText((float) inputFile.length() / 1024 + "  kb");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        else if(s.equals("Check Size"))
        {
            try {
                resize(path, "temp.jpg", Integer.parseInt(widthTF.getText()), Integer.parseInt(heightTF.getText()));
                File inputFile = new File("temp.jpg");
                currentSizeTF.setText((float) inputFile.length() / 1024 + "  kb");
            } catch (NumberFormatException | IOException e1) {
                e1.printStackTrace();
            }
            if(qualitySlider.getValue()!=100)
            {
                flag=1;
                try {
                    resize("temp.jpg","temp2.jpg" , (float)qualitySlider.getValue()/100);
                    File inputFile = new File("temp2.jpg");
                    currentSizeTF.setText((float) inputFile.length() / 1024 + "  kb");

                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        else if(s.equals("Save"))
        {
            if(!pathTF.getText().equals(""))
            try {
                StringTokenizer st = new StringTokenizer(path,".");
                if(flag==1)
                {
                    resize(path,st.nextToken()+"_Compressed.jpg" , (float)qualitySlider.getValue()/100);
                    deleteTemp("temp.jpg", "temp2.jpg");
                    JOptionPane.showMessageDialog(this,
                            "Saved Image",
                            "Saved",
                            JOptionPane.INFORMATION_MESSAGE);
                }
                else
                {
                    resize(path,st.nextToken()+"_Compressed.jpg", Integer.parseInt(widthTF.getText()), Integer.parseInt(heightTF.getText()));
                    deleteTemp("temp.jpg", " ");
                    JOptionPane.showMessageDialog(this,
                            "Saved Image",
                            "Saved",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
            catch (IOException e1) {
                e1.printStackTrace();
            }
            if(pathTF.getText().equals(""))
            {
                JOptionPane.showMessageDialog(this,
                        "Select an image Before saving.",
                        "Select an image",
                        JOptionPane.WARNING_MESSAGE);
            }

            flag=0;
        }
        else if(s.equals("Add"))
        {
            JTextField field1 = new JTextField();
            JTextField field2 = new JTextField();
            JTextField id=new JTextField();
            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.add(new JLabel("ID :"));
            panel.add(id);
            panel.add(new JLabel("Width :"));
            panel.add(field1);
            panel.add(new JLabel("Height :"));
            panel.add(field2);
            int result = JOptionPane.showConfirmDialog(null, panel, "Add Option",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    addToOptions(id.getText(),field1.getText(),field2.getText());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
        else if (s.equals("Edit"))
        {
            ProcessBuilder pb = new ProcessBuilder("Notepad.exe", "settings/options.txt");
            try {
                pb.start();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
