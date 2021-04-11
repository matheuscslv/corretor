package Principal;

import GerarRelatorio.Gerar;
import GerarRelatorio.GerarRelatorioAlunos;
import GerarRelatorio.GerarRelatorioGabarito;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

public class Disciplinas extends Janela implements ActionListener{
    JTextField[] respostas = new JTextField[3];
    JButton inicio, adicionar, deletar, prova, gerar_prova, gerar_relatorio;
    JButton gerar_relatorio_alunos_sala,gerar_relatorio_gabaritos;
    JScrollPane scroll;
    
    ArrayList<JCheckBox> discip_arr = new ArrayList<>();
    int count_quadro = 0;
    public static String codigo;
    
    public void Abrir(){
        conexao = Conexao_BD.Conector();
        p[3] = new JPanel(null);
        Topo(-contador, 0, discip_arr);
        t.setSelected(false);
        
        // Labels e campos de resposta
        frases[0] = new JLabel("Código : ");
        frases[0].setBounds(30, 17-5, 250, 50);
        respostas[0] = new JTextField();
        respostas[0].setBounds(20+100, frases[0].getY()+14, 100, 20);
        respostas[0].addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if(e.getKeyChar() == '(' || e.getKeyChar() == ')') e.consume();
            }
        });
        
        frases[1] = new JLabel("Disciplina : ");
        frases[1].setBounds(30, frases[0].getY()+40, 250, 50);
        respostas[1] = new JTextField();
        respostas[1].setBounds(20+100, frases[1].getY()+14, 250, 20);
        respostas[1].addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if(e.getKeyChar() == '(' || e.getKeyChar() == ')') e.consume();
            }
        });
        
        //Botões de baixo: inicio/adicionar/deletar/prova
        inicio = new JButton("Início");
        inicio.setBounds(25-8, 340, 87, 25);
        inicio.addActionListener(this);
        adicionar = new JButton("Adicionar");
        adicionar.setBounds(inicio.getX()+inicio.getWidth()+15, inicio.getY(), 87, 25);
        adicionar.addActionListener(this);
        deletar = new JButton("Deletar");
        deletar.setBounds(adicionar.getX()+adicionar.getWidth()+15, inicio.getY(), 87, 25);
        deletar.addActionListener(this);
        prova = new JButton("Prova");
        prova.setBounds(deletar.getX()+deletar.getWidth()+15, inicio.getY(), 87, 25);
        prova.addActionListener(this);
        gerar_prova = new JButton("Gerar Prova");
        gerar_prova.setBounds(inicio.getX(), inicio.getY()+inicio.getHeight()+15, 395, 25);
        gerar_prova.addActionListener(this);
        gerar_relatorio = new JButton("Gerar Relatório");
        gerar_relatorio.setBounds(inicio.getX(), gerar_prova.getY()+gerar_prova.getHeight()+19, 395, 25);
        gerar_relatorio.addActionListener(this);
        
        gerar_relatorio_alunos_sala = new JButton("Gerar Relatório de Alunos Sala");
        gerar_relatorio_alunos_sala.setBounds(inicio.getX(), gerar_relatorio.getY()+gerar_prova.getHeight()+19, 395, 25);
        gerar_relatorio_alunos_sala.addActionListener(this);
        
        gerar_relatorio_gabaritos = new JButton("Gerar Relatório de Gabaritos");
        gerar_relatorio_gabaritos.setBounds(inicio.getX(), gerar_relatorio_alunos_sala.getY()+gerar_prova.getHeight()+19, 395, 25);
        gerar_relatorio_gabaritos.addActionListener(this);
        
        //Inserir no panel
        for (int i = 0; i < 2; i++) {
            p[3].add(respostas[i]);
            p[3].add(frases[i]);
        }
        p[3].add(inicio);
        p[3].add(adicionar);
        p[3].add(deletar);
        p[3].add(prova);
        p[3].add(gerar_prova);
        p[3].add(gerar_relatorio);
        p[3].add(gerar_relatorio_alunos_sala);
        p[3].add(gerar_relatorio_gabaritos);
        
        
        // Monta o quadro
        try{
            QuadroDisciplinas();
        }catch(Exception e){
            System.out.println("Erro no Disciplinas: "+e);
            inicio.doClick();
            return;
        }
        
        // Mostra o panel
        tabs.setComponentAt(tabs.getTabCount()-1, p[3]);
        
    }
    
    public void QuadroDisciplinas() throws Exception{
        try{
            discip_arr = new ArrayList<>();
            String frase;
            
            // Componentes: scrollbar, panel para checkboxes e parte de cima
            t.setBounds(101, frases[1].getY()+50, 270, 20);
            t.addActionListener(this);
            p[3].add(t);
            
            JPanel jp = new JPanel(null);
            jp.setBackground(Color.GRAY);
            jp.setBounds(100, t.getY()+22, 270, 200);
            
            scroll = new JScrollPane(jp);
            scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
            scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scroll.setBounds(jp.getBounds());
            scroll.getVerticalScrollBar().setUnitIncrement(12);
            p[3].add(scroll);
            
            //Inserir as disciplinas na tabela
            frase = "select * from disciplinas where ordem > 0 order by nome";
            ps = conexao.prepareStatement(frase);
            rs = ps.executeQuery();
            int i=-1, y = 0, x = 0;
            boolean teste = false;
            String codigo;
            while(rs.next()){
                i++;
                frase = rs.getObject("nome").toString();
                codigo = rs.getObject("codigo").toString();
                
                discip_arr.add(i, new JCheckBox("("+codigo+") "+frase));
                discip_arr.get(i).setBounds(0, y, (int) discip_arr.get(i).getPreferredSize().getWidth(), 20);
                discip_arr.get(i).addMouseListener(new MouseListener() {
                    public void mouseClicked(MouseEvent me) {
                        if(me.getClickCount() == 2) janelaInformacoes(me.getSource().toString());
                    }
                    public void mousePressed(MouseEvent me) {}
                    public void mouseReleased(MouseEvent me) {}
                    public void mouseEntered(MouseEvent me) {}
                    public void mouseExited(MouseEvent me) {}
                });
                discip_arr.get(i).addActionListener(this);
                
                jp.add(discip_arr.get(i));
                
                if(discip_arr.get(i).getWidth() > discip_arr.get(x).getWidth()) x = i;
                
                y+=21;
                teste = true;
            }
            //Redimensiona o quadro
            if(teste){
                jp.setPreferredSize(new Dimension( (int) discip_arr.get(x).getPreferredSize().getWidth(), y));
            }
            
            //Insere o texto no "topo"
            t.setText(" Disciplinas Selecionadas: "+contador+" de "+discip_arr.size());
            
        }catch(Exception e){
            System.out.println("Erro em QuadroDisciplinas: "+e);
            JOptionPane.showMessageDialog(null, "Um erro inesperado ocorreu \nA operação será encerrada", "Erro", JOptionPane.ERROR_MESSAGE);
            throw new Exception();
        }finally{
            new Conexao_BD().FecharConexao(conexao, ps, rs, janela0);
        }
        
    }
    
    public void Adicionar(){
        //Se não tiver conexão
        conexao = new Conexao_BD().VerificarConexao(janela0);
        if(conexao == null) return;
        
        if(respostas[0].getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Preencha o código da disciplina para prosseguir.", "Erro na inserção", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if(respostas[1].getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Preencha o nome da disciplina para prosseguir.", "Erro na inserção", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            String frase;
            //Verifica se o 'Codigo' já existe
            try{
                frase = "select * from disciplinas where codigo = ?";
                ps = conexao.prepareStatement(frase);
                ps.setString(1, respostas[0].getText());
                rs = ps.executeQuery();
                while(rs.next()){
                    throw new IndexOutOfBoundsException();
                }

            }catch(IndexOutOfBoundsException e){
                System.out.println("Erro no Adicionar Disciplinas (Codigo repetido): "+e);
                int r = JOptionPane.showConfirmDialog(null, "Já existe uma disciplinas com o código: "+respostas[0].getText()+" \nDeseja substituir a disciplina existente?\n(Isso afetará todos os alunos que estão cadastrados nela)", "Aviso", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                if(r == 1) return;
            }
            
            frase = "insert into disciplinas(codigo, nome) values(?,?) on duplicate key update codigo = values(codigo), nome = values(nome)";
            ps = conexao.prepareStatement(frase);
            ps.setString(1, respostas[0].getText());
            ps.setString(2, respostas[1].getText());
            ps.executeUpdate();
            
            // Atualiza o quadro e a variável 't'
            p[3].remove(scroll);
            contador = 0;
            QuadroDisciplinas();
            tabs.setComponentAt(tabs.getTabCount()-1, new JPanel(null));
            tabs.setComponentAt(tabs.getTabCount()-1, p[3]);
            /*p[3].setVisible(false);
            p[3].setVisible(true);*/
            t.setSelected(true);
            t.doClick();
            
            JOptionPane.showMessageDialog(null, "Operação realizada com sucesso!", "Concluído", JOptionPane.INFORMATION_MESSAGE);
            
        }catch(Exception e){
            System.out.println("Erro no Adicionar: "+e);
            JOptionPane.showMessageDialog(null, "Um erro inesperado ocorreu \nA operação será encerrada", "Erro", JOptionPane.ERROR_MESSAGE);
        }finally{
            new Conexao_BD().FecharConexao(conexao, ps, rs, janela0);
        }
    }
    
    public void Deletar(){
        //Se não tiver conexão
        conexao = new Conexao_BD().VerificarConexao(janela0);
        if(conexao == null) return;
        
        // 0 disciplinas selecionadas
        if(contador == 0){
            JOptionPane.showMessageDialog(null, "Selecione uma disciplina para prosseguir.", "Erro ao selecionar", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int aux = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja deletar "+contador+" disciplinas(s)?", "Mensagem", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if(aux == 1) return;
        
        
        try{
            // Encontra os checkboxes que foram selecionados e deletar seus perfis
            String frase, s, s2;
            for (int i = 0; i < discip_arr.size(); i++) {
                
                if(discip_arr.get(i).isSelected()){
                    //Pega o codigo das disciplinas
                    s = discip_arr.get(i).getText().substring(1);
                    s2 = s.substring(0, s.indexOf(")"));
                    
                    frase = "delete from disciplinas where codigo = ?";
                    ps = conexao.prepareStatement(frase);
                    ps.setString(1, s2);
                    ps.executeUpdate();
                    
                    frase = "delete from usuario_disciplina where usuarios_matricula = ?";
                    ps = conexao.prepareStatement(frase);
                    ps.setString(1, s2);
                    ps.executeUpdate();
                }
            }
            
            JOptionPane.showMessageDialog(null, "Operação realizada com sucesso!", "Concluído", JOptionPane.INFORMATION_MESSAGE);
            //Limpa os campos
            respostas[0].setText("");
            respostas[1].setText("");
            // Atualiza o quadro e a variável 't'
            p[3].remove(scroll);
            QuadroDisciplinas();
            Topo(-contador, 0, discip_arr);
            tabs.setComponentAt(tabs.getTabCount()-1, new JPanel(null));
            tabs.setComponentAt(tabs.getTabCount()-1, p[3]);
            
        }catch(Exception e){
            System.out.println("Erro em Deletar, Listar: "+e);
            JOptionPane.showMessageDialog(null, "Ocorreu um erro inesperado. Tente novamente.", "Erro", JOptionPane.ERROR_MESSAGE);
        }finally{
            new Conexao_BD().FecharConexao(conexao, ps, rs, janela0);
        }
        
    }
    
    public void Prova(){
        //Se não tiver conexão
        conexao = new Conexao_BD().VerificarConexao(janela0);
        if(conexao == null) return;
        
        if(contador == 0){
            JOptionPane.showMessageDialog(null, "Selecione uma disciplina para prosseguir","Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if(contador > 1){
            JOptionPane.showMessageDialog(null, "Selecione apenas uma (1) disciplina para prosseguir","Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
                
        //Pegar o código da disciplina
        for (int i = 0; i < discip_arr.size(); i++) {
            if(discip_arr.get(i).isSelected()){
                codigo = discip_arr.get(i).getText();
                codigo = codigo.substring(codigo.indexOf("(")+1, codigo.indexOf(")"));
            }
            
        }
        
        //Editor de provas
        try{
            janela0.setEnabled(false);
            janela0.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            
            new Gabarito().gerar(new PegarQuantidadeQuestoesDisciplina().quantidadeQuestoes(codigo), codigo);

            
            janela0.setVisible(false);
            janela0.setEnabled(true);
            janela0.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }catch(Exception e){
            System.out.println("Erro no final de Prova: "+e);
            JOptionPane.showMessageDialog(null, "Um erro inesperado ocorreu \nA operação será encerrada", "Erro", JOptionPane.ERROR_MESSAGE);
            janela0.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            return;
        }
        
    }
    
    public void Gerar(){
        final JFileChooser fSaida = new JFileChooser();
        fSaida.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fSaida.setSelectedFile(null);
        fSaida.showOpenDialog(null);
        try {
            
            if(!fSaida.getSelectedFile().getPath().equals(null)) { //Se o diretório alvo não for null
                new GerarProva.GerarProva().GerarProva(fSaida.getSelectedFile().getPath(), "2019.1");
                
                Desktop.getDesktop().open(new File(fSaida.getSelectedFile().getPath())); // Abre a pasta
            }
            
        } catch (Exception e) {
            System.out.println("Erro em Gerar: "+e);
        }
        
        janela0.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
    
    public void janelaInformacoes(String disciplina_cod){
        //Se não tiver conexão
        conexao = new Conexao_BD().VerificarConexao(janela0);
        if(conexao == null) return;
        
        //Separa o disciplina_cod
        disciplina_cod = disciplina_cod.substring(disciplina_cod.indexOf("(")+1, disciplina_cod.indexOf(")"));
        //Busca as informações no banco
        try{
            //Informações da table "Disciplinas"
            StringBuilder s = new StringBuilder();
            String frase = "select * from disciplinas where codigo = ?";
            ps = conexao.prepareStatement(frase);
            ps.setString(1, disciplina_cod);
            rs = ps.executeQuery();
            while(rs.next()){
                s.append("Disciplina: ").append(rs.getObject("nome"));
                s.append("\n\nGabarito: \n");
            }
            
            //Info da table "Gabarito"
            ArrayList<String> s2 = new ArrayList<>();
            String aux = "";
            frase = "select * from gabarito where disciplina_formatacao_disciplinas_codigo = ?";
            ps = conexao.prepareStatement(frase);
            ps.setString(1, disciplina_cod);
            rs = ps.executeQuery();
            while(rs.next()){
                s.append(rs.getObject("questao")).append(" - ");
                
                //Se for letra, adiciona ')' [estética]
                if(rs.getObject("correta").equals("ANULADA"))
                    s.append(rs.getObject("correta")).append("\n");
                else
                    s.append(rs.getObject("correta").toString().toLowerCase()).append(")\n");
            }
            
            JOptionPane.showMessageDialog(null, s,"Informações da Disciplina", JOptionPane.PLAIN_MESSAGE);
        }catch(Exception e){
            System.out.println("Erro em janelaInformacoes: "+e);
            JOptionPane.showMessageDialog(null, "Um erro inesperado ocorreu \nAs informações não puderam ser mostradas", "Erro", JOptionPane.ERROR_MESSAGE);
        }finally{
            new Conexao_BD().FecharConexao(conexao, ps, rs, janela0);
        }
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().getClass() == JButton.class){
            janela0.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            
            //Inicio
            if(e.getSource().equals(inicio)){
                tabs.setEnabled(true);
                tabs.setComponentAt(tabs.getTabCount()-1, p[0]);
                janela0.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }    
            
            if(e.getSource().equals(gerar_relatorio_alunos_sala)){
                final JFileChooser fSaida = new JFileChooser();
                fSaida.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fSaida.setSelectedFile(null);
                fSaida.showOpenDialog(null);
                try {

                    if(!fSaida.getSelectedFile().getPath().equals(null)) { //Se o diretório alvo não for null
                        new GerarRelatorioAlunos().gerar(fSaida.getSelectedFile().getPath(),5);
                        Desktop.getDesktop().open(new File(fSaida.getSelectedFile().getPath())); // Abre a pasta
                    }
                } catch (Exception ex) {
                    System.out.println("Erro em Gerar: "+ex);
                }
                janela0.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
            
            if(e.getSource().equals(gerar_relatorio_gabaritos)){
                final JFileChooser fSaida = new JFileChooser();
                fSaida.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fSaida.setSelectedFile(null);
                fSaida.showOpenDialog(null);
                try {

                    if(!fSaida.getSelectedFile().getPath().equals(null)) { //Se o diretório alvo não for null
                        new GerarRelatorioGabarito().gerar(fSaida.getSelectedFile().getPath());
                        Desktop.getDesktop().open(new File(fSaida.getSelectedFile().getPath())); // Abre a pasta
                    }
                } catch (Exception ex) {
                    System.out.println("Erro em Gerar: "+ex);
                }
                janela0.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
            
            //Editar Prova
            if(e.getSource().equals(prova)) Prova();
            
            //Adicionar
            if(e.getSource().equals(adicionar)) Adicionar();

            //Deletar
            if(e.getSource().equals(deletar)) Deletar();
            
            //Gerar Prova
            if(e.getSource().equals(gerar_prova)) Gerar();
            
            //Gerar Relatório
            if(e.getSource().equals(gerar_relatorio)){
                
                final JFileChooser fSaida = new JFileChooser();
                fSaida.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fSaida.setSelectedFile(null);
                fSaida.showOpenDialog(null);
                try {

                    if(!fSaida.getSelectedFile().getPath().equals(null)) { //Se o diretório alvo não for null
                        
                        String frase, s, s2;
                        for (int i = 0; i < discip_arr.size(); i++) {

                            if(discip_arr.get(i).isSelected()){
                                //Pega o codigo das disciplinas
                                s = discip_arr.get(i).getText().substring(1);
                                s2 = s.substring(0, s.indexOf(")"));

                                Gerar g = new Gerar();
                                g.gerar(s2, fSaida.getSelectedFile().getPath());
                            }
                        }
                        Desktop.getDesktop().open(new File(fSaida.getSelectedFile().getPath())); // Abre a pasta
                    }
                } catch (Exception ex) {
                    System.out.println("Erro em Gerar: "+ex);
                }
                janela0.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }

            return;
        }
        
        //Checkboxes:        
        if(e.getSource().equals(t)){
            Boolean a;
            int b = 0;
            Topo(-contador, 0, discip_arr);
            
            if(t.isSelected()){
                a = true; b = 1;
            }
            else{
                a = false; b = -1;
                Topo(discip_arr.size(), 0, discip_arr);
            }
            for (int i = 0; i < discip_arr.size(); i++) {
                discip_arr.get(i).setSelected(a);
                Topo(b, 0, discip_arr);
            }
        }
        // Verifica se os checkboxes foram selecionados ou não
        for(int i=0; i < discip_arr.size(); i++){
            if(e.getSource().equals(discip_arr.get(i))){
            
            if(discip_arr.get(i).isSelected()) Topo(1, 0, discip_arr);
            else Topo(-1, 0, discip_arr);
            }
        }
        if(contador == discip_arr.size()) t.setSelected(true);
        else t.setSelected(false);
    }
   
}
