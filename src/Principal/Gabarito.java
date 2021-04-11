package Principal;

import static Principal.Janela.conexao;
import static Principal.Janela.janela0;
import static Principal.Janela.ps;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;


public class Gabarito implements ActionListener{
    private JFrame janela;
    private JScrollPane jsp;
    JButton confirm = new JButton("Confirmar"), cancel = new JButton("Cancelar");
    ArrayList<ButtonGroup> grupos = new ArrayList<>();
    String codDisc;
    
    public void gerar(int numMaxQuest, String codDisc){
        this.codDisc = codDisc;
        //Panel das alternativas
        JPanel pan1 = new JPanel();
        pan1.setBounds(765, 10, 180, 300);
        pan1.setBackground(Color.LIGHT_GRAY);
        pan1.setVisible(true);
        pan1.setLayout(new GridLayout(0,1,0,12));
        
        //Box de questões
        for (int i = 0; i < numMaxQuest; i++) {
            Box box = Box.createHorizontalBox();
            JLabel nn = new JLabel((i+1)+"):");
            nn.setFont(new Font("TIMES_ROMAN", Font.BOLD, 15));
            
            ButtonGroup grupo = new ButtonGroup();  //Permite que só um RadioButton seja marcado por vez em uma sequencia
            JRadioButton aa = new JRadioButton("a)");grupo.add(aa);aa.setToolTipText("(Questão "+(i+ 1)+"): letra (a)");
            JRadioButton bb = new JRadioButton("b)");grupo.add(bb);bb.setToolTipText("(Questão "+(i+ 1)+"): letra (b)");
            JRadioButton cc = new JRadioButton("c)");grupo.add(cc);cc.setToolTipText("(Questão "+(i+ 1)+"): letra (c)");
            JRadioButton dd = new JRadioButton("d)");grupo.add(dd);dd.setToolTipText("(Questão "+(i+ 1)+"): letra (d)");
            JRadioButton ee = new JRadioButton("e)");grupo.add(ee);ee.setToolTipText("(Questão "+(i+ 1)+"): letra (e)");
            JRadioButton ff = new JRadioButton("ANULADA");grupo.add(ff);ff.setToolTipText("Anular Questão");
            
            bb.setBackground(Color.white);
            dd.setBackground(Color.white);
            ff.setBackground(Color.white);
            
            box.add(nn);
            box.add(aa);
            box.add(bb);
            box.add(cc);
            box.add(dd);
            box.add(ee);
            box.add(ff);
            pan1.add(box);
            
            grupos.add(grupo);
        }
        jsp = new JScrollPane(pan1);
        jsp.getVerticalScrollBar().setUnitIncrement(25);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jsp.setBounds(0, 0, 350, 300);
        
        //Botões
        confirm.setBounds(33, jsp.getHeight()+4, 95, 30);
        confirm.addActionListener(this);
        cancel.setBounds(confirm.getX()+confirm.getWidth()+50, confirm.getY(), confirm.getWidth(), confirm.getHeight());
        cancel.addActionListener(this);
        
        //Janela
        janela = new JFrame();
        janela.setTitle("Gabarito da Disciplina");
        janela.setResizable(false);
        janela.setLayout(null);
        janela.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        janela.addWindowListener( new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent w){
                int i = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja concelar a operação de edição?\nTodos os ajustes feitos serão perdidos!", "Aviso", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if(i == 0) cancel.doClick();
            };
        });
        janela.add(confirm);
        janela.add(cancel);
        janela.add(jsp);
        janela.setSize(jsp.getWidth(), jsp.getHeight()+84);
        janela.setLocationRelativeTo(null);
        janela.setVisible(true);
        
        //Checa se já existe um gabarito marcado e mostra ele
        gabaritoExistente();
        
    }
    
    public void gabaritoExistente(){
        
        //Pega todas as entradas do banco que sejam desta disciplina
        try{
            String frase = "select * from gabarito where disciplina_formatacao_disciplinas_codigo = ?";
            ps = conexao.prepareStatement(frase);
            ps.setString(1, codDisc);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                int n_questao = Integer.parseInt(rs.getObject("questao").toString());
                String alternativa_certa = rs.getObject("correta").toString().toLowerCase();
                AbstractButton button = null, botao = null;
                
                //Loop pelo grupo de botões e seleciona os corretos
                for (Enumeration<AbstractButton> buttons = grupos.get(n_questao-1).getElements(); buttons.hasMoreElements();) {
                    button = buttons.nextElement();
                    
                    if(button.getText().equals(alternativa_certa+")") ||
                       button.getText().equals(alternativa_certa.toUpperCase()) ){
                        button.setSelected(true); break;
                    }
                }
            }
        }catch(Exception e){
            System.out.println("Erro em gabaritoExistente: "+e);
        }
    }
    
    public void inserirBanco(ArrayList<AbstractButton> lista){
        //Conexão com o Banco
        conexao = new Conexao_BD().VerificarConexao(janela);
        if(conexao == null) return;
        
        int ordemPrimeiroNumero = -1;
        //Pega o número da ordem de cada uma das questões
        try{
            String frase = "select ordem_questao from gabarito where disciplina_formatacao_disciplinas_codigo = ?";
            ps = conexao.prepareStatement(frase);
            ps.setString(1, codDisc);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                ordemPrimeiroNumero = (int) rs.getObject("ordem_questao");
                break;
            }
        }catch(Exception e){
            System.out.println("Erro em Gabarito0: "+e);
        }
        
        //Verifica se o código não possui ordem das questões e pega um número para ele
        if(ordemPrimeiroNumero == -1)
            ordemPrimeiroNumero = obterNumeracaoDaOrdemFaltando();
        
        //Deleta todas as entradas do banco que sejam desta disciplina
        try{
            String frase = "delete from gabarito where disciplina_formatacao_disciplinas_codigo = ?";
            ps = conexao.prepareStatement(frase);
            ps.setString(1, codDisc);
            ps.executeUpdate();
        }catch(Exception e){
            System.out.println("Erro em Gabarito1: "+e);
        }
        
        //Enhanced-for pra pegar cada botao
        int x = 0;
        for(AbstractButton l: lista){
            x++;
            //Se for diferente de "ANULADA", deixa só a letra em upper case
            if(!l.getText().equals("ANULADA")) l.setText(l.getText().substring(0,l.getText().length()-1).toUpperCase());
            
            try{
                String frase = "insert into gabarito values(?,?,?,?)";
                ps = conexao.prepareStatement(frase);
                ps.setString(1, codDisc);
                ps.setInt(2, x);
                ps.setString(3, l.getText());
                ps.setInt(4, ordemPrimeiroNumero + x - 1);
                ps.executeUpdate();
            }catch(Exception e){
                System.out.println("Erro em Gabarito2: "+e);
            }
            
        }
    }
    
    public int obterNumeracaoDaOrdemFaltando(){
        int ordem;
        ArrayList<Integer> numeros_da_tabela;
        
        numeros_da_tabela = new ArrayList<>();
        
        try{
            String frase = "select ordem_questao from gabarito;";
            ps = conexao.prepareStatement(frase);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                numeros_da_tabela.add((int) rs.getObject("ordem_questao"));
            }
        }catch(Exception e){
            System.out.println("Erro em obterNumeracaoDaOrdemFaltando: "+e);
        }
        
        for (int i = 0; i < numeros_da_tabela.size(); i++) 
            if(numeros_da_tabela.get(i) != (i+1))
                return (i+1);
        
        
        return numeros_da_tabela.size()+1;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(confirm)){
            ArrayList<AbstractButton> lista_de_botoes = new ArrayList<>();
            
            //Loop por todos os grupos de botões
            for(int i=0; i < grupos.size(); i++){
                AbstractButton button = null, botao = null;
                
                //Verifica se os botões de cada gruo foram selecionados, caso não, a operação é cancelada.
                for (Enumeration<AbstractButton> buttons = grupos.get(i).getElements(); buttons.hasMoreElements();) {
                    button = buttons.nextElement();
                    
                    if (button.isSelected()) botao = button;
                }
                if(botao == null){
                    JOptionPane.showMessageDialog(null, "Não pode haver questões sem resposta", "Erro",JOptionPane.ERROR_MESSAGE);
                    return;
                }else lista_de_botoes.add(botao);
            }
            //Insere no banco todos os valores dos botoes que foram selecionados
            inserirBanco(lista_de_botoes);
            JOptionPane.showMessageDialog(null,"Gabarito inserido com sucesso", "Mensagem", JOptionPane.PLAIN_MESSAGE);
            cancel.doClick();   
        }
        
        //Volta pra janela anterior
        if(e.getSource().equals(cancel)){
            janela.dispose();
            janela0.setVisible(true);
        }
        
    }
}