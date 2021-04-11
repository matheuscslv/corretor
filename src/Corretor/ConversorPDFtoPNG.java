package Corretor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class ConversorPDFtoPNG {
    
    public static void main(String[] args) throws IOException {

       File diretorio = new File("C:\\Users\\MATHEUS\\Desktop\\EDEF 2018.1\\Dia 2\\GABARITO SALA 5");
       File[] arquivos = diretorio.listFiles();
       
       for(int i=0;i<arquivos.length;i++){

           ArrayList<BufferedImage> imagens = new PdfToBufferedImage().convertPDFToJPG(arquivos[i].getAbsolutePath());
           
           for(int j=0;j<imagens.size();j++){
               ImageIO.write(imagens.get(j), "png", new File(arquivos[i].getAbsolutePath().replace("pdf", "png")));
           }
           
       }
       

       
    }
    
}
