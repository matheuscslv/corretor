package Principal;

import static Principal.Janela.conexao;
import static Principal.Janela.janela0;
import org.apache.poi.ss.usermodel.*;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import static Principal.Janela.ps;
import static Principal.Janela.rs;
import java.awt.Cursor;
import java.math.BigDecimal;
import java.sql.Statement;
import java.text.Normalizer;

public class Excel {
    //ArrayList de ArrayList para
    ArrayList<ArrayList<String>> dados = new ArrayList<>();
    
    public void abrirExcel(){
        janela0.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
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
                verificaFormatacao(sheet);
                pegaDados(sheet);
            }
        }catch(Exception e){
            System.out.println("Erro em verificaFormatacao: "+e);
            JOptionPane.showMessageDialog(null, "O arquivo não está formatado corretamente!");
            return;
        }
        
        try{
            workbook.close();
            insereAlunosNoBanco();
            relacionaAlunosDisciplinas();
        }catch(Exception e){
            System.out.println("Erro ao fechar o Workbook: "+e);
            JOptionPane.showMessageDialog(null, "Um erro inesperado ocorreu. Tente novamente.");
            return;
        }
        
        
        
        JOptionPane.showMessageDialog(null, "Operação realizada com sucesso!", "Mensagem", JOptionPane.PLAIN_MESSAGE);
        janela0.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        
    }
    
    public void verificaFormatacao(Sheet sheet) throws Exception{
        // Pega os valores das células e passa pra String
        DataFormatter dataFormatter = new DataFormatter();
        ArrayList<String> aux = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            Cell cell = sheet.getRow(0).getCell(i);
            aux.add(dataFormatter.formatCellValue(cell));
        }
        
        int i = 0;
        if(!aux.get(i).equals("")){
            if(aux.get(i).equals("Carimbo de data/hora")) i++;
            if(aux.get(i).equals("Endereço de e-mail")) i++;
            if(aux.get(i).equals("Número de matricula")) i++;
            if(aux.get(i).equals("Nome completo")) i++;
            if(aux.get(i).equals("Curso")) i++;
            if(aux.get(i).equals("Turma")) i++;
            if(aux.get(i).equals("Disciplinas cursadas")) i++;
            if(aux.get(i).equals("Necessita de Atendimento Especial")) i++;
            if(aux.get(i).equals("Justifique (Caso a resposta seja \"SIM\")")) return;
        }throw new Exception();
    }
    
    public void pegaDados(Sheet sheet) throws Exception{
        try{
            // Loop pra pegar todos os dados, tranformar em String e mandar pro "dados"
            DataFormatter dataFormatter = new DataFormatter();
            ArrayList<String> aux = null;
            for(int i=1; i>=0; i++){
                Row row = sheet.getRow(i);
                aux = new ArrayList<>();
                
                for(int j=1; j<7; j++){
                    Cell cell = row.getCell(j);

                    //System.out.print(dataFormatter.formatCellValue(cell)+" | ");
                    if(dataFormatter.formatCellValue(cell).equals("")) return;
                    //Turma
                    if(j==5){
                        String turma = dataFormatter.formatCellValue(cell);
                        aux.add(turma.substring(0,turma.indexOf("(")-1));
                        continue;
                    }
                    //Matrícula
                    if(j==2){
                        String matricula = new BigDecimal(cell.getNumericCellValue()).toString();
                        aux.add(matricula);
                        continue;
                    }
                    aux.add(dataFormatter.formatCellValue(cell));
                }
                //System.out.println("");
                dados.add(aux);
            }
            
        }catch(Exception e){
            System.out.println("Erro dentro do pegaDados: "+e);
        }
    }
    
    public void insereAlunosNoBanco(){
        //Conexão com o Banco
        conexao = new Conexao_BD().VerificarConexao(janela0);
        if(conexao == null) return;
        
        try{
            //Deleta todas as inserções na table
            String frase = "delete from usuarios where matricula <> 'admin'";
            ps = conexao.prepareStatement(frase);
            ps.executeUpdate();
            
            //Insere os dados no banco
            for (int i = 0; i < dados.size(); i++) {
                
                frase = "insert into usuarios values(?,?,?,?,?,?,?)";
                ps = conexao.prepareStatement(frase);
                ps.setString(1, dados.get(i).get(1)); //matricula
                ps.setString(2, "senha"); //senha
                ps.setString(3, dados.get(i).get(2).toUpperCase()); //nome
                ps.setString(4, dados.get(i).get(4)); //turma
                ps.setString(5, dados.get(i).get(0)); //email
                ps.setString(6, dados.get(i).get(3)); //curso
                ps.setString(7, "ALUNO"); //tipo_usuario
                ps.executeUpdate();
            }
        }catch(Exception e){
            System.out.println("Erro em inserirBanco: "+e);
        }finally{
            new Conexao_BD().FecharConexao(conexao, ps, rs, janela0);
        }
    }
    
    public void relacionaAlunosDisciplinas()throws Exception{
        //Conexão com o Banco
        conexao = new Conexao_BD().VerificarConexao(janela0);
        if(conexao == null) return;
        janela0.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        try{
            //Deletar todas as entradas na table
            String frase = "delete from usuario_disciplina";
            ps = conexao.prepareStatement(frase);
            ps.executeUpdate();
            
            //Loop em todos os alunos
            for (int i = 0; i < dados.size(); i++) {    
                //Disciplinas dos alunos
                String disciplinas_aluno = removeAcentos_UpperCase(dados.get(i).get(5))+"§";
                
                //Busca todas as Disciplinas no banco
                Statement st = conexao.createStatement();
                frase = ("select * from disciplinas");
                rs = st.executeQuery(frase);
                while(rs.next()) { 
                    String disciplinas_banco = removeAcentos_UpperCase(rs.getString("nome"));

                    //Insere a relação (aluno-disciplina) no banco, se a disciplina coincidir
                    if(disciplinas_aluno.contains(disciplinas_banco+",") ||
                       disciplinas_aluno.contains(disciplinas_banco+"§") /*||
                       disciplinas_aluno.contains(disciplinas_banco+" (")*/){

                        frase = "insert into usuario_disciplina(periodos_periodo,usuarios_matricula,disciplinas_codigo) values(?,?,?)";
                        ps = conexao.prepareStatement(frase);
                        ps.setString(1, "2019.1"); //período
                        ps.setString(2, dados.get(i).get(1)); //matrícula
                        ps.setString(3, rs.getString("codigo")); //Disciplina_código
                        ps.executeUpdate();
                    }
                }
                
                //ALTERAR ISSO NA PRÓXIMA GERAÇÃO DE PROVAS: COLOCAR DENTRO DO IF AÍ EM BAIXO
                //Insere "Conhecimentos Gerais" para todos, menos alguns
                frase = "insert into usuario_disciplina(periodos_periodo,usuarios_matricula,disciplinas_codigo) values(?,?,?)";
                ps = conexao.prepareStatement(frase);
                ps.setString(1, "2019.1"); //período
                ps.setString(2, dados.get(i).get(1)); //matrícula
                ps.setString(3, "CCCG"); //Disciplina_código
                ps.executeUpdate();
                
                if(dados.get(i).get(3).equals("Ciência da Computação")){
                    
                    
                    //Insere "CGS" para todos, menos alguns
                    frase = "insert into usuario_disciplina(periodos_periodo,usuarios_matricula,disciplinas_codigo) values(?,?,?)";
                    ps = conexao.prepareStatement(frase);
                    ps.setString(1, "2019.1"); //período
                    ps.setString(2, dados.get(i).get(1)); //matrícula
                    ps.setString(3, "CCCGS"); //Disciplina_código
                    ps.executeUpdate();
                }

            }
        }catch(Exception e){
            System.out.println("Erro em relacionaDisciplinas: "+e);
            JOptionPane.showMessageDialog(null, "Um erro inesperado ocorreu!\n Tente novamente.","Erro", JOptionPane.ERROR_MESSAGE);
            throw new Exception();
        }finally{
            new Conexao_BD().FecharConexao(conexao, ps, rs, janela0);
        }
    }
    
    //Remove acentos, caracteres especiais e retorna em UpperCase
    public String removeAcentos_UpperCase(String origem){
        String destino = Normalizer.normalize(origem, Normalizer.Form.NFD);
        destino = destino.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return destino.toUpperCase();
    }
    
}