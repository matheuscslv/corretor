package Principal;

import java.awt.Cursor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Conexao_BD {

    public static Connection Conector() {
        java.sql.Connection conexao = null;
        String driver = "com.mysql.jdbc.Driver";
        
        String url = "jdbc:mysql://"+new Ip.Servidor().getIp()+":3306/edef";
        String user = new Ip.Servidor().getUsuario();
        String password = new Ip.Servidor().getSenha();

        // Estabelenco a conexão com o banco
        try {
            System.out.println("Conectando ao banco de dados");
            Class.forName(driver);
            conexao = DriverManager.getConnection(url, user, password);
            return conexao;
        } catch (Exception e) {
            System.out.println("Erro em estabeler conexão: " + e);
            return null;
        }

    }
    
    public Connection VerificarConexao(JFrame janela0){
        janela0.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        // 0: normal
        // 1: sem conexão
        java.sql.Connection conexao = Conexao_BD.Conector();
        janela0.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        if(conexao != null) return Conector();
        
        try{
            JOptionPane.showMessageDialog(null, "Não foi possível estabelecer conexão com o banco de dados.", "Erro", 0);
            conexao.close();
        }catch(Exception e){
            System.out.println("Erro em VerificarConexão: "+e);
        }finally{
            return null;
        }
        
    }
    
    public void FecharConexao(java.sql.Connection conexao, PreparedStatement ps, ResultSet rs, JFrame janela0){
        try {
            conexao.close();
            ps.close();
            rs.close();
            System.out.println("Conexão finalizada");
        } catch (Exception e) {
            System.out.println("erro: "+e);
        }
        janela0.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
    
}
