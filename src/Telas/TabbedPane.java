package Telas;

import Corretor.CorrecaoProvas;
import Corretor.LancarNota;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import Principal.Janela;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TabbedPane extends JFrame{
            
    private int[] gabarito = new int[45];
    private String saida = "";
        
    private JProgressBar progresso;
    private JLabel executar = new JLabel(new ImageIcon(getClass().getResource("/resources/Executar.png")));
    private JLabel selecionarCartao = new JLabel(new ImageIcon(getClass().getResource("/resources/Cartao.png")));
    private JLabel pastaSaida = new JLabel(new ImageIcon(getClass().getResource("/resources/Saida.png")));
    private JLabel sobre = new JLabel(new ImageIcon(getClass().getResource("/resources/sobre.png")));
    
    
    private final String url = "jdbc:mysql://"+new Ip.Servidor().getIp()+":3306/edef";
    private final String username = new Ip.Servidor().getUsuario();
    private final String password = new Ip.Servidor().getSenha();
    PreparedStatement ps = null;
    
                
    public JPanel corrigirprovas(){
        
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED), "Seleção"));
        
        final JFileChooser fProvas = new JFileChooser();
        fProvas.setFileFilter(new FileNameExtensionFilter("Imagens", "jpg", "png"));
        selecionarCartao.setToolTipText("Cartões Respostas");
        fProvas.setMultiSelectionEnabled(true);
        final JTextArea tProvas = new JTextArea(6,48);
                
        selecionarCartao.addMouseListener(new MouseListener() {  
            public void mouseClicked(MouseEvent e) {           
                listarCartoesResposta(fProvas,tProvas);                
            }  
            public void mousePressed(MouseEvent e) { }  
            public void mouseReleased(MouseEvent e) {}  
            public void mouseEntered(MouseEvent e) {
                selecionarCartao.setIcon(new ImageIcon(this.getClass().getResource("/resources/Cartao AC.png")));
            }  
            public void mouseExited(MouseEvent e) {
                selecionarCartao.setIcon(new ImageIcon(getClass().getResource("/resources/Cartao.png")));
            }  
        });
                 
        final JFileChooser fSaida = new JFileChooser();
        fSaida.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        final JLabel lSaida = new JLabel("-");
        pastaSaida.setToolTipText("Pasta de saída");
        
        pastaSaida.addMouseListener(new MouseListener() {  
            public void mouseClicked(MouseEvent e) {                              
                try{
                    fSaida.setSelectedFile(null);
                    lSaida.setText("-");
                    fSaida.showOpenDialog(null);			
                    lSaida.setText(fSaida.getSelectedFile().getPath());
                    saida = fSaida.getSelectedFile().getPath();
                }catch(java.lang.NullPointerException x){  
                    pastaSaida.setIcon(new ImageIcon(this.getClass().getResource("/resources/Saida.png")));
                }         
            }  
            public void mousePressed(MouseEvent e) { }  
            public void mouseReleased(MouseEvent e) {}  
            public void mouseEntered(MouseEvent e) {  
                pastaSaida.setIcon(new ImageIcon(this.getClass().getResource("/resources/Saida AC.png")));
            }  
            public void mouseExited(MouseEvent e) { 
                pastaSaida.setIcon(new ImageIcon(this.getClass().getResource("/resources/Saida.png")));
            }  
        });
        
        sobre.addMouseListener(new MouseListener() {  
            public void mouseClicked(MouseEvent e) {
                TelaAutoria autoria = new TelaAutoria();
                autoria.setVisible(true);
            }  
            public void mousePressed(MouseEvent e) { }  
            public void mouseReleased(MouseEvent e) {}  
            public void mouseEntered(MouseEvent e) { 
                sobre.setIcon(new ImageIcon(this.getClass().getResource("/resources/sobre AC.png")));
            }  
            public void mouseExited(MouseEvent e) {
                sobre.setIcon(new ImageIcon(getClass().getResource("/resources/sobre.png")));
            }  
        });
                
        selecionarCartao.setBounds(10,20,new ImageIcon(getClass().getResource("/resources/Cartao.png")).getIconWidth(),new ImageIcon(getClass().getResource("/resources/Cartao.png")).getIconHeight());
        pastaSaida.setBounds(50,20,new ImageIcon(getClass().getResource("/resources/Saida.png")).getIconWidth(),new ImageIcon(getClass().getResource("/resources/Saida.png")).getIconHeight());
        sobre.setBounds(500,37,new ImageIcon(getClass().getResource("/resources/sobre.png")).getIconWidth(),new ImageIcon(getClass().getResource("/resources/sobre.png")).getIconHeight());
                
        tProvas.setFont(new Font("Verdana", Font.PLAIN, 9));
        tProvas.setEditable(false);
        JScrollPane sProvas = new JScrollPane(tProvas);
        sProvas.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        progresso = new JProgressBar(0,100);
        progresso.setStringPainted(true);
        progresso.setBounds(10,350,500,20);
        
        sProvas.setBounds(10,60,500,250);
        lSaida.setBounds(10,280,450,100);
        
        panel.setBounds(10,10,470,400);       
        panel.add(sProvas);	
        //panel.add(lSaida);
        panel.add(selecionarCartao);
        //panel.add(pastaSaida);
        panel.add(sobre);
        panel.add(progresso);
        
        executar.setBounds(10,390,new ImageIcon(getClass().getResource("/resources/Executar.png")).getIconWidth(),new ImageIcon(getClass().getResource("/resources/Executar.png")).getIconHeight());
        panel.add(executar);
        executar.setToolTipText("Executar");
                                
        executar.addMouseListener(new MouseListener() {  
            public void mouseClicked(MouseEvent e) {
                executar(fProvas,lSaida);
            }  
            public void mousePressed(MouseEvent e) {}  
            public void mouseReleased(MouseEvent e) {}  
            public void mouseEntered(MouseEvent e) {                 
                executar.setIcon(new ImageIcon(this.getClass().getResource("/resources/Executar AC.png")));
            }  
            public void mouseExited(MouseEvent e) {                
                executar.setIcon(new ImageIcon(this.getClass().getResource("/resources/Executar.png")));
            }  
        }); 
        
        return panel;
    }
        
    public void listarCartoesResposta(JFileChooser fProvas, JTextArea tProvas){      
        fProvas.showOpenDialog(null);
        if(fProvas.getSelectedFiles().length > 0){
            Integer idx = 1;
            tProvas.setText("");
            String text = "";

            for(int i = 0; i < fProvas.getSelectedFiles().length; i++){
                    text += idx.toString() + " - " + fProvas.getSelectedFiles()[i].getName() + "\n";
                    idx++;
            }

            tProvas.setText(text);
        }       
    }
    
    public void executar(JFileChooser fProvas, JLabel lSaida){
        if(fProvas.getSelectedFiles().length == 0){
            JOptionPane.showMessageDialog(null,"É necessario selecionar os cartões respostas!");
            return;
        }
        
        
//        try {
//            c = DriverManager.getConnection(url, username, password);
//            
//            //Deleta todas as notas dos alunos
//            String frase = "UPDATE usuario_disciplina set nota = 0.0 and disciplinas_codigo != 'CCCGS'";
//            ps = c.prepareStatement(frase);
//            ps.execute();
//            
//            
//        } catch (Exception e) {
//            System.out.println("Erro em deletar as notas de todos os alunos na correcao das provas(TabbedPane): "+e);
//        }
//        
        
        

//        if(lSaida.getText().equals("-")){
//            JOptionPane.showMessageDialog(null,"É necessario selecionar a pasta de saida!");
//            return;
//        }
         
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        progresso.setValue(0);
        progresso.update(progresso.getGraphics()); 
                        
//        try {
//            Workbook planilha = null;
//            planilha = Workbook.getWorkbook(new File(saida+"//notas.xls"));
//
//            File excluir = new File(saida+"//notas.xls");
//            excluir.delete();
//        } catch (Exception ex) {
//
//        }

        int percent = 0; 
        double valor = ((double) 1 / fProvas.getSelectedFiles().length) * 100;
        for(int z=0;z<fProvas.getSelectedFiles().length;z++){

            progresso.setString("Corrigindo: "+fProvas.getSelectedFiles()[z].getName());
            progresso.update(progresso.getGraphics()); 

            
            //Pega a matrícula do cartão resposta
            String arquivo_nome = fProvas.getSelectedFiles()[z].getName().replaceFirst("[.][^.]+$", "");
            
            //Pega a matricula e a letra do nome da prova
            String letra = arquivo_nome.substring(arquivo_nome.length()-1, arquivo_nome.length()).toLowerCase();
            arquivo_nome = arquivo_nome.substring(0, arquivo_nome.length()-1);
            
            //Pega o gabarito de acordo com a letra da prova
            if(letra.equals("a") || letra.equals("b") || letra.equals("c")){
                gabarito = new LancarNota().pegarGabarito(arquivo_nome, letra);
                
                boolean curso_aluno = true;
                try{
                    Connection c = DriverManager.getConnection(url, username, password);
                    
                    String query = "select * from usuarios where matricula = ?";
                    ps = c.prepareStatement(query);
                    ps.setString(1, arquivo_nome);
                    ResultSet rs = ps.executeQuery();
                    while(rs.next()){
                        
                        String curso = rs.getString("curso");
                        if(!curso.equals("Ciência da Computação")) curso_aluno = false;
                        System.out.println("Curso do aluno: "+curso_aluno);
                    }
                    
                    
                }catch(Exception e){
                    System.out.println("Erro em procurar qual o curso do aluno(TabbedPane): "+e);
                }
                
                //Gabarito Correção
                CorrecaoProvas corrigir = new CorrecaoProvas();
                corrigir.nota(fProvas.getSelectedFiles()[z].getPath(),gabarito,saida,arquivo_nome, letra, curso_aluno);
            }
            else
                JOptionPane.showMessageDialog(null, "Nome de arquivo inválido!\nO programa irá continuar de onde parou", "Erro", JOptionPane.ERROR_MESSAGE);
            
            
            percent = (int) (Math.ceil(valor)) + percent;
            progresso.setValue(percent);
            progresso.setString("100%");
            progresso.update(progresso.getGraphics()); 
        }
        
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        JOptionPane.showMessageDialog(null,"Operação realizada com sucesso!");
            
    }
    
    public TabbedPane() {
                
        setTitle("EDEF");
        setIconImage(new ImageIcon(getClass().getResource("/resources/icon.png")).getImage());
        setSize(500,800);
        setResizable(false);
        
        JTabbedPane jtp = new JTabbedPane();
        getContentPane().add(jtp);
                
        JPanel corretor = corrigirprovas(); 
        Janela cadastro = new Janela();
                        
        jtp.addTab("Corretor", corretor);
        jtp.addTab("Alunos e Disciplinas Cadastradas", cadastro.AbrirJanela(jtp, this));
        
        
         try{
            Connection c = DriverManager.getConnection(url, username, password);

             //Deleta todas as notas dos alunos
            String frase = "insert into disciplinas values(?, ?, ?)";
            ps = c.prepareStatement(frase);
            ps.setString(1,"CCCGS");
            ps.setString(2,"CONHECIMENTOS GERAIS SUBJETIVOS");
            ps.setInt(3,0);
            ps.executeUpdate();
            
            ps.close();
            c.close();
            
         } catch(Exception e ){
             System.out.println("Erro em inserir a disciplina de CGS (esperado): "+e);
         }
    }
      
}