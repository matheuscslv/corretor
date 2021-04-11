package Corretor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class AlunoExiste {
    
    String a = new Ip.Servidor().getIp();
    private final String url = "jdbc:mysql://"+new Ip.Servidor().getIp()+":3306/edef";
    private final String username = new Ip.Servidor().getUsuario();
    private final String password = new Ip.Servidor().getSenha();
    private ArrayList<String> txt = new ArrayList<>();
    
    public boolean alunoExiste(String aluno){
        
        try {
            Connection c = DriverManager.getConnection(url, username, password);
            
            PreparedStatement ps = c.prepareStatement("SELECT * FROM usuarios WHERE matricula = ?");
            ps.setString(1, aluno);
            ResultSet disciplinastxt = ps.executeQuery();
                        
            while(disciplinastxt.next()){
                return true;
            }
            
            ps.close();
            c.close();
            
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            ex.printStackTrace();
        }
        
        return false;
    } 
     
}
