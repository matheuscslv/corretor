package Inicial;

import Telas.TabbedPane;
import java.io.File;
import java.io.FileNotFoundException;
import javax.swing.JFrame;
import javax.swing.UIManager;

public class Main {
    
    public static void main(String[] args) throws FileNotFoundException, Exception {

        try{
            UIManager.setLookAndFeel (UIManager.getSystemLookAndFeelClassName());
        }catch(Exception e){
            System.out.print(e.getMessage());
        }
        
        (new File(System.getProperty("user.dir")+"/src/Lixo")).mkdir();
        
        TabbedPane tp = new TabbedPane();
        tp.setSize(550, 650);
        tp.setLocationRelativeTo(null);
        tp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        tp.setVisible(true);
        
    }
        
}
