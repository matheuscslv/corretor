package Corretor;

import Principal.Conexao_BD;
import static Principal.Janela.janela0;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class Insere_Notas_CCCGS {
    
    public static void notasSubjetivasExcel(){
        
        Workbook workbook = null;
        try{
            //Seleciona o arquivo excel e cria um WorkBook dele
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileNameExtensionFilter("xlx","xlsx"));
            chooser.showOpenDialog(null);
            if(chooser.getSelectedFile() == null) return;
            workbook = WorkbookFactory.create(new File(chooser.getSelectedFile().getAbsolutePath()));
            
        }catch(Exception e){
            System.out.println("Erro em pegar o excel");
            JOptionPane.showMessageDialog(null, "O arquivo Excel não pode ser selecionado!\n(Verifique se o arquivo está aberto e feche-o)", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try{
            // Loop por todos os sheets do arquivo
            for(Sheet sheet: workbook) {
                
                pegaDados(sheet);
            }
        }catch(Exception e){
            System.out.println("Erro em verificaFormatacao: "+e);
            JOptionPane.showMessageDialog(null, "O arquivo não está formatado corretamente!");
            return;
        }
        
        try{
            workbook.close();
        }catch(Exception e){
            System.out.println("Erro ao fechar o Workbook: "+e);
            JOptionPane.showMessageDialog(null, "Um erro inesperado ocorreu. Tente novamente.");
            return;
        }
        
        
    }
    
    public static void pegaDados(Sheet sheet) {
        try{
            //Banco
            Connection conexao = new Conexao_BD().VerificarConexao(janela0);
            
            deletaTodasAsNotasAtuais(conexao);
            
//            String comando = "update usuario_disciplina set nota = ? where (select matricula from usuarios where nome = ?) AND usuario_disciplina.disciplinas_codigo = 'CCCGS'";
            String comando = "UPDATE usuario_disciplina a JOIN usuarios b ON b.nome = ?"
                            + "SET a.nota = ? WHERE a.disciplinas_codigo = 'CCCGS' AND a.usuarios_matricula = b.matricula";
            PreparedStatement ps = null;
            
            // Loop pra pegar todos os dados, tranformar em String e mandar pro "dados"
            DataFormatter dataFormatter = new DataFormatter();
            for(int i=2; i>=0; i++){
                Row row = sheet.getRow(i);
                Cell cell_1 = row.getCell(1), cell_2 = row.getCell(2);
                
                if(dataFormatter.formatCellValue(cell_1).equals("")) return;
                    
                String nome_aluno = dataFormatter.formatCellValue(cell_1)+"";
                String nota = dataFormatter.formatCellValue(cell_2);
                
                if(nota.equals("")) continue;
                
                NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
                Number number = format.parse(nota);
                double d = number.doubleValue();
                
                
                System.out.println("XXXXXXXXXXXXX nome: "+nome_aluno+" nota: "+nota);
                //Banco
                
                ps = conexao.prepareStatement(comando);
                ps.setDouble(2,d);
                ps.setString(1,nome_aluno);
                ps.executeUpdate();

                
            }
            
        }catch(Exception e){
            System.out.println("Erro dentro do pegaDados: "+e);
        }
        JOptionPane.showMessageDialog(null, "Operação finalizada! \n\nVerifique se todos os dados foram Inseridos corretamente.", "Operação concluída", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void deletaTodasAsNotasAtuais(Connection conexao){
        
        try{
            String frase = "UPDATE usuario_disciplina SET nota = 0 WHERE disciplinas_codigo = 'CCCGS';";
            
            PreparedStatement ps = null;
            ps.executeUpdate();
            
         } catch(Exception e ){
             System.out.println("Erro em deletar todas as notas atuais de CCCGS do banco: "+e);
         }

    }
}
