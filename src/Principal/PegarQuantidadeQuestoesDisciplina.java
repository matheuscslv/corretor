package Principal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

public class PegarQuantidadeQuestoesDisciplina {

    private final String url = "jdbc:mysql://" + new Ip.Servidor().getIp() + ":3306/edef";
    private final String username = new Ip.Servidor().getUsuario();
    private final String password = new Ip.Servidor().getSenha();
    private ArrayList<String> txt = new ArrayList<>();

    public int quantidadeQuestoes(String codigoDisciplina) {

        if (codigoDisciplina.equals("CCCG")) {
            return 8;
        }

        try {
            Connection c = DriverManager.getConnection(url, username, password);
            String query = ("SELECT * FROM files WHERE disciplinas_codigo = ?");
            PreparedStatement ps = c.prepareStatement(query);
            ps.setString(1, codigoDisciplina);
            ResultSet disciplinastxt = ps.executeQuery();

            while (disciplinastxt.next()) {
                String saida = System.getProperty("user.dir") + "\\src\\Lixo";
                /*byte [] bytes = disciplinastxt.getBytes("formatacao");
     		File materia = new File(saida+"\\"+disciplinastxt.getString("disciplinas_codigo")+".docx");
                FileOutputStream fos = new FileOutputStream(materia);
                fos.write( bytes );
                fos.close();
                txts.add(saida + "\\" + disciplinastxt.getString("disciplinas_codigo") + ".docx");*/

                File arquivoOrigem = new File(disciplinastxt.getString("formatacao"));
                File arquivoDestino = new File(saida + "\\" + disciplinastxt.getString("disciplinas_codigo") + ".docx");

                copyFile(arquivoOrigem, arquivoDestino);
                System.out.println("path origem: " + arquivoOrigem.getPath());
                System.out.println("path destino: " + arquivoDestino.getPath());
                txt.add(saida + "\\" + disciplinastxt.getString("disciplinas_codigo") + ".docx");
            }
//            Connection c = DriverManager.getConnection(url, username, password);
//            
//            PreparedStatement ps = c.prepareStatement("SELECT * FROM files WHERE disciplinas_codigo = ?");
//            ps.setString(1, codigoDisciplina);
//            ResultSet disciplinastxt = ps.executeQuery();
//                        
//            while(disciplinastxt.next()){
//                
//                byte [] bytes = disciplinastxt.getBytes("formatacao");
//     		File materia = new File(System.getProperty("user.dir")+"\\src\\Lixo\\"+disciplinastxt.getString("disciplinas_codigo")+".docx");
//                FileOutputStream fos = new FileOutputStream(materia);
//                fos.write( bytes );
//                fos.close(); 
//                                
//                txt.add(System.getProperty("user.dir")+"\\src\\Lixo\\"+disciplinastxt.getString("disciplinas_codigo")+".docx");
//            }

            ps.close();
            c.close();

            if (!txt.isEmpty()) {
                int qtd = find_replace_in_DOCX(txt.get(0));
                File diretorio = new File(System.getProperty("user.dir") + "\\src\\Lixo");
                File[] arquivos = diretorio.listFiles();
                for (int i = 0; i < arquivos.length; i++) {
                    File excluir = arquivos[i];
                    excluir.delete();
                }
                return qtd;
            } else {
                return 0;
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            ex.printStackTrace();
        }

        return 0;
    }

    private int find_replace_in_DOCX(String entrada) throws IOException, InvalidFormatException, org.apache.poi.openxml4j.exceptions.InvalidFormatException {
        int numeracao = 0;

        try {
            XWPFDocument doc = new XWPFDocument(OPCPackage.open(entrada));
            for (XWPFParagraph p : doc.getParagraphs()) {
                List<XWPFRun> runs = p.getRuns();
                if (runs != null) {
                    for (XWPFRun r : runs) {
                        String text = r.getText(0);
                        if (text != null && text.contains("§")) {
                            numeracao++;
                            r.setText(text, 0);
                        }
                    }
                }
            }

        } finally {
        }

        return numeracao;
    }

    public void copyFile(File source, File destination) throws IOException {
        if (destination.exists()) {
            destination.delete();
        }

        FileChannel sourceChannel = null;
        FileChannel destinationChannel = null;

        try {
            sourceChannel = new FileInputStream(source).getChannel();
            destinationChannel = new FileOutputStream(destination).getChannel();
            sourceChannel.transferTo(0, sourceChannel.size(),
                    destinationChannel);
        } finally {
            if (sourceChannel != null && sourceChannel.isOpen()) {
                sourceChannel.close();
            }
            if (destinationChannel != null && destinationChannel.isOpen()) {
                destinationChannel.close();
            }
        }
    }

}
