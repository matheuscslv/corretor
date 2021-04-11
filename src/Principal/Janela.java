package Principal;

import Corretor.Insere_Notas_CCCGS;
import GerarRelatorio.CriarExcel;
import GerarRelatorio.Gerar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Cursor;
import java.awt.Desktop;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JTabbedPane;

public class Janela implements ActionListener{
    public static JFrame janela0 = new JFrame("Cadastro de Alunos");
    static JPanel[] p = new JPanel[4];
    JLabel[] frases = new JLabel[6];
    JButton[] botoes = new JButton[6];
    static JTabbedPane tabs;
    
    static Connection conexao = null;
    static PreparedStatement ps = null;
    static ResultSet rs = null;
    
    JCheckBox t = new JCheckBox();
    static int contador = 0;
    
    public JPanel AbrirJanela(JTabbedPane jtp, JFrame janela0){
        try{
            this.janela0 = janela0;
                    
            p[0] = new JPanel(null);
            p[1] = new JPanel(null);
            p[2] = new JPanel(null);
            p[3] = new JPanel(null);
            tabs = jtp;
            
            //Adicionar aluno
            frases[0] = new JLabel("Adicionar alunos");
            frases[0].setBounds(30, 17, 250, 50);
            botoes[0] = new JButton("Selecionar");
            botoes[0].setBounds(30, 17+37, 100, 25);
            botoes[0].addActionListener(this);

            //Listar Todos
            frases[1] = new JLabel("Listar Alunos");
            frases[1].setBounds(30, botoes[0].getY()+40, 250, 50);
            botoes[1] = new JButton("Selecionar");
            botoes[1].setBounds(30, frases[1].getY()+37, 100, 25);
            botoes[1].addActionListener(this);
            
            //Disciplinas
            frases[2] = new JLabel("Disciplinas");
            frases[2].setBounds(30, botoes[1].getY()+40, 250, 50);
            botoes[2] = new JButton("Selecionar");
            botoes[2].setBounds(30, frases[2].getY()+37, 100, 25);
            botoes[2].addActionListener(this);
            
            //Arquivo Excel
            frases[3] = new JLabel("Selecionar Excel com os alunos inscritos");
            frases[3].setBounds(frases[0].getX()+frases[0].getWidth(), frases[0].getY(), 250, 50);
            botoes[3] = new JButton("Selecionar");
            botoes[3].setBounds(frases[3].getX(), botoes[0].getY(), 100, 25);
            botoes[3].addActionListener(this);
            
            //Relatório Excel
            frases[4] = new JLabel("Gerar Excel das notas dos alunos");
            frases[4].setBounds(frases[1].getX()+frases[1].getWidth(), frases[1].getY(), 250, 50);
            botoes[4] = new JButton("Selecionar");
            botoes[4].setBounds(frases[4].getX(), botoes[1].getY(), 100, 25);
            botoes[4].addActionListener(this);
            
            //Relatório Excel
            frases[5] = new JLabel("Selecionar Excel das notas de CGS");
            frases[5].setBounds(frases[2].getX()+frases[2].getWidth(), frases[2].getY(), 250, 50);
            botoes[5] = new JButton("Selecionar");
            botoes[5].setBounds(frases[5].getX(), botoes[2].getY(), 100, 25);
            botoes[5].addActionListener(this);

            for (int i = 0; i < botoes.length; i++) {
                p[0].add(botoes[i]);
                p[0].add(frases[i]);
            }
            
            return p[0];
            
        }catch(Exception e){
            System.out.println("Erro em Abrir Janela (Janela): "+e);
            return null;
        }
        
    }
    
    public void Topo(int c, int op, ArrayList array){
        contador += c;
        switch(op) {
            case 0: t.setText(" Disciplinas Selecionadas: "+contador+" de "+array.size());
            break;
            case 1: t.setText(" Alunos Selecionados: "+contador+" de "+array.size());
            break;
        }
        
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        //Se tiver conexão
        conexao = new Conexao_BD().VerificarConexao(janela0);
        if(conexao != null){
            tabs.setEnabled(false);
            janela0.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            
            //Adicionar
            if(e.getSource().equals(botoes[0])) new Adicionar_Alunos().Abrir(new ArrayList<String>());
            
            //Listar Todos
            if(e.getSource().equals(botoes[1])) new Listar().Abrir();
            
            //Disciplinas
            if(e.getSource().equals(botoes[2])) new Disciplinas().Abrir();
            
            //Excel
            if(e.getSource().equals(botoes[3])){
                new Excel().abrirExcel();
                tabs.setEnabled(true);
            }
            
            //Relatório Excel
            if(e.getSource().equals(botoes[4])){
                
                
                final JFileChooser fSaida = new JFileChooser();
                fSaida.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fSaida.setSelectedFile(null);
                fSaida.showOpenDialog(null);
                try {

                    if(!fSaida.getSelectedFile().getPath().equals(null)) { //Se o diretório alvo não for null
                        CriarExcel ce = new CriarExcel();
                        ce.gerarExcel(fSaida.getSelectedFile().getPath());
                            
                    }
                    Desktop.getDesktop().open(new File(fSaida.getSelectedFile().getPath())); // Abre a pasta
                    
                } catch (Exception ex) {
                    System.out.println("Erro em Gerar: "+ex);
                }
                janela0.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
            
            //Excel
            if(e.getSource().equals(botoes[5])){
                new Insere_Notas_CCCGS().notasSubjetivasExcel();
                tabs.setEnabled(true);
            }
            
            
        }
        janela0.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        
    }

}
