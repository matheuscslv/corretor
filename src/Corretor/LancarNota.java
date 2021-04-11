package Corretor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class LancarNota {
    
    private final String url = "jdbc:mysql://"+new Ip.Servidor().getIp()+":3306/edef";
    private final String username = new Ip.Servidor().getUsuario();
    private final String password = new Ip.Servidor().getSenha();
    
    String comando_geral = "select gabarito.correta as alternativa,gabarito.disciplina_formatacao_disciplinas_codigo from usuarios inner join usuario_disciplina on usuario_disciplina.usuarios_matricula = usuarios.matricula inner join disciplinas on usuario_disciplina.disciplinas_codigo = disciplinas.codigo inner join gabarito on gabarito.disciplina_formatacao_disciplinas_codigo = usuario_disciplina.disciplinas_codigo where usuarios.matricula = ? and disciplinas.ordem > 0";
    String comando_prova_a = " and disciplinas.ordem < 10 ORDER BY disciplinas.ordem, gabarito.ordem_questao LIMIT 40";
    String comando_prova_b = " and disciplinas.ordem >= 10 ORDER BY disciplinas.ordem, gabarito.ordem_questao LIMIT 40";
    String comando_prova_c = " ORDER BY disciplinas.ordem, gabarito.ordem_questao";
    
    public int[] pegarGabarito(String matricula, String letra){
        PreparedStatement ps = null;
        int i = -1;
        
        int[] gabaritofinal = new int[45];
        try {
            Connection c = DriverManager.getConnection(url, username, password);
            
            //Operação da prova correspondente
            if(letra.equals("a")){
                //Deleta todas as notas do aluno
                String frase = "UPDATE usuario_disciplina set nota = 0.0 WHERE usuarios_matricula = ? and disciplinas_codigo != 'CCCGS'";
                ps = c.prepareStatement(frase);
                ps.setString(1, matricula);
                ps.execute();
                
                //Comando da prova "a"
                ps = c.prepareStatement(comando_geral+comando_prova_a);
            }
            if(letra.equals("b")){
                /*
                //Pega o número de questões que já foram feitas (a)
                ps = c.prepareStatement(comando_geral+comando_prova_a);
                ps.setString(1, matricula);
                ResultSet gabarito = ps.executeQuery();
                while(gabarito.next()) i++;
                */
                
                //Comando da prova "b"
                ps = c.prepareStatement(comando_geral+comando_prova_b);
                
            }
            if(letra.equals("c")){
                //Pega o número de questões maior que 40
                ps = c.prepareStatement(comando_geral+comando_prova_c);
                ps.setString(1, matricula);
                ResultSet gabarito = ps.executeQuery();
                while(gabarito.next()){
                    i++;
                    if(i<38) continue;
                    
                    
                    String valor = gabarito.getString("alternativa");
                    if(valor.equals("A")){
                        gabaritofinal[i-38] = 1;
                    }else if(valor.equals("B")){
                        gabaritofinal[i-38] = 2;
                    }else if(valor.equals("C")){
                        gabaritofinal[i-38] = 3;
                    }else if(valor.equals("D")){
                        gabaritofinal[i-38] = 4;
                    }else if(valor.equals("E")){
                        gabaritofinal[i-38] = 5;
                    }else if(valor.equals("ANULADA")){
                        gabaritofinal[i-38] = 6;
                    }
                }
                
                
                ps.close();
                c.close();
                return gabaritofinal;
            }
            
            
            //Comando pra pegar as disciplinas
            ps.setString(1, matricula);
            ResultSet gabarito = ps.executeQuery();
            while(gabarito.next()){
                i++;
                if(i>38) break;
                
                String valor = gabarito.getString("alternativa");
                if(valor.equals("A")){
                    gabaritofinal[i] = 1;
                }else if(valor.equals("B")){
                    gabaritofinal[i] = 2;
                }else if(valor.equals("C")){
                    gabaritofinal[i] = 3;
                }else if(valor.equals("D")){
                    gabaritofinal[i] = 4;
                }else if(valor.equals("E")){
                    gabaritofinal[i] = 5;
                }else if(valor.equals("ANULADA")){
                    gabaritofinal[i] = 6;
                }
                
            }
                        
            ps.close();
            c.close();
            
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        
        return gabaritofinal;
    }
    
    public String[] pegarCodigoDisciplinasAlunos(String matricula, String letra){
        String[] gabaritoinicial = new String[10];
        String[] gabaritofinal = null;
        try {
            Connection c = DriverManager.getConnection(url, username, password);
            
            String comando = "SELECT DISTINCT disciplinas.codigo AS codigo,usuarios.nome AS aluno FROM usuarios\n" +
                "INNER JOIN usuario_disciplina ON usuario_disciplina.usuarios_matricula = usuarios.matricula\n" +
                "INNER JOIN disciplinas ON usuario_disciplina.disciplinas_codigo = disciplinas.codigo\n" +
                "INNER JOIN gabarito ON gabarito.disciplina_formatacao_disciplinas_codigo = usuario_disciplina.disciplinas_codigo ";
            
            
            if(letra.equals("a") )
                comando += "WHERE usuarios.matricula = ? AND disciplinas.ordem > 0 AND disciplinas.ordem < 10 ORDER BY disciplinas.ordem;";
            
            if(letra.equals("b")){
                
//                String comando_backup = comando;
//                comando += "WHERE usuarios.matricula = ? AND disciplinas.ordem > 0 AND disciplinas.ordem < 10 ORDER BY disciplinas.ordem;";
//                
//                PreparedStatement ps = c.prepareStatement(comando);
//                ps.setString(1, matricula);
//                ResultSet gabarito = ps.executeQuery();
//                int x=0;
//                while(gabarito.next()) x++;
//                
//                
//                comando = comando_backup;
                //comando += "WHERE usuarios.matricula = ? AND disciplinas.ordem > 0 AND disciplinas.ordem >= 10 ORDER BY disciplinas.ordem limit "+(6-x);
                comando += "WHERE usuarios.matricula = ? AND disciplinas.ordem > 0 AND disciplinas.ordem >= 10 ORDER BY disciplinas.ordem";
                
            }
            if(letra.equals("c")){
                
                String comando_backup = comando;
                comando += "WHERE usuarios.matricula = ? AND disciplinas.ordem > 0 ORDER BY disciplinas.ordem;";
                
                PreparedStatement ps = c.prepareStatement(comando);
                ps.setString(1, matricula);
                ResultSet gabarito = ps.executeQuery();
                int x=0;
                
                ArrayList<String> gabaritofinal_final = new ArrayList<>();
                while(gabarito.next()){
                    if(x>5)
                    gabaritofinal_final.add(gabarito.getString("codigo"));
                    x++;
                }
                
                gabaritofinal = new String[gabaritofinal_final.size()];
                for(int j=0;j<gabaritofinal_final.size();j++){
                    gabaritofinal[j] = gabaritofinal_final.get(j);
                }
                
                ps.close();
                c.close();
                
                return gabaritofinal;
            }
            
            
            PreparedStatement ps = c.prepareStatement(comando);
            ps.setString(1, matricula);
            ResultSet gabarito = ps.executeQuery();
            
            int i = 0;
            while(gabarito.next()){
                String valor = gabarito.getString("codigo");
                gabaritoinicial[i] = valor;
                i++;
            }
           
            gabaritofinal = new String[i];
            for(int j=0;j<gabaritofinal.length;j++){
                gabaritofinal[j] = gabaritoinicial[j];
            }
            
            ps.close();
            c.close();
            
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    
        return gabaritofinal;
    }
    
    public int qtdDisciplinasQueAlunoFaz(String disciplina){
        int valor = 0;
        try {
            Connection c = DriverManager.getConnection(url, username, password);
            
            PreparedStatement ps = c.prepareStatement("select max(gabarito.questao) as qtd from gabarito where gabarito.disciplina_formatacao_disciplinas_codigo = ?");
            ps.setString(1, disciplina);
            ResultSet gabarito = ps.executeQuery();
            
            while(gabarito.next()){                
                valor = gabarito.getInt("qtd");
            }
                        
            ps.close();
            c.close();
            
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    
        return valor;
    }
    
    public void armazenarNota(String nome, String[] qtdDisciplinas, double[] notas){
        
        try {
            Connection c = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = null;
            
            for(int i=0;i<notas.length;i++){
                ps = c.prepareStatement("UPDATE usuario_disciplina set nota = nota + ? WHERE usuarios_matricula = ? AND disciplinas_codigo = ?");
                ps.setString( 1, String.valueOf(notas[i]) );
                ps.setString( 2, nome );
                ps.setString( 3, qtdDisciplinas[i] );
                
                ps.execute();
            }
                       
            ps.close();
            c.close();
            
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,ex.getMessage());
        }
        
    }
    
    public void armazenarGabarito(String codigo, ArrayList<String> gabarito) throws SQLException{
        
        Connection c = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = null;
        
        ArrayList<String> alternativa = gabarito;
        for(int i=0;i<alternativa.size();i++){
            ps = c.prepareStatement("INSERT INTO gabarito VALUES (?,?,?)");
            ps.setString( 1, codigo );
            ps.setInt( 2, (i+1) );
            ps.setString( 3, alternativa.get(i) );

            ps.execute();
        }

        ps.close();
        c.close();
    }
    
}
