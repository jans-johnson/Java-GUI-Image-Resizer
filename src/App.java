import javax.swing.*;

public class App extends JFrame
{
    private JButton selectImageButton;
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JTextField textField4;
    private JSlider slider1;
    private JList list1;
    private JButton checkSizeButton;
    private JButton saveButton;
    private JPanel rootPanel;
    private JTabbedPane tabbedPane1;

    public App()
    {
        add(rootPanel);
        setSize(500,500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}
