package Corretor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

public class PdfToBufferedImage {
        
    public ArrayList<BufferedImage> convertPDFToJPG(String src) throws IOException{
        
        ArrayList<BufferedImage> imagens = new ArrayList();
        
        File input = new File(src);
        PDDocument doc = PDDocument.load(new FileInputStream(input));
        
        List<PDPage> pages = doc.getDocumentCatalog().getAllPages();
        Iterator<PDPage> i = pages.iterator();
        
        int count = 1;
        while (i.hasNext()) {
            PDPage page = i.next();
            BufferedImage bi = page.convertToImage();
            bi = Thumbnails.of(bi).forceSize(1658, 2318).asBufferedImage();
            count++;
            
            imagens.add(bi);
        }           

        doc.close();
        
        return imagens;
    }
   
}
