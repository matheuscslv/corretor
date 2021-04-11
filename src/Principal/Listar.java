package Principal;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.ResultSet;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class Listar extends Janela implements ActionListener{
    JButton inicio, editar, deletar, adicionar;
    JScrollPane scroll;
    ArrayList<String> resps = new ArrayList<>();
    
    ArrayList<JCheckBox> alun_arr = new ArrayList<>();
    int count_quadro = 0;
    
    public int Abrir(){
        conexao = Conexao_BD.Conector();
        p[2] = new JPanel(null);
        Topo(-contador, 1, alun_arr);
        t.setSelected(false);
        
        //Botões de baixo: inicio/editar/deletar/adicionar
        inicio = new JButton("Início");
        inicio.setBounds(25-8, 350+50, 87, 25);
        inicio.addActionListener(this);
        editar = new JButton("Editar");
        editar.setBounds(inicio.getX()+inicio.getWidth()+15, inicio.getY(), 87, 25);
        editar.addActionListener(this);
        deletar = new JButton("Deletar");
        deletar.setBounds(editar.getX()+editar.getWidth()+15, inicio.getY(), 87, 25);
        deletar.addActionListener(this);
        adicionar = new JButton("Adicionar");
        adicionar.setBounds(deletar.getX()+deletar.getWidth()+15, inicio.getY(), 87, 25);
        adicionar.addActionListener(this);
        
        //Inserir no panel
        p[2].add(inicio);
        p[2].add(editar);
        p[2].add(deletar);
        p[2].add(adicionar);
        
        // Abre o Quadro
        try{
            QuadroAlunos();
        }catch(Exception e){
            System.out.println("Erro no Listar: "+e);
            inicio.doClick();
            return 1;
        }
        
        // Mostra o panel. Redimensionar a janela.
        tabs.setComponentAt(tabs.getTabCount()-1, p[2]);
        return 0;
    }
    
    public void QuadroAlunos() throws Exception{
        try{
            alun_arr = new ArrayList<>();
            String frase;
            // Componentes: scrollbar, panel para checkboxes e parte de cima
            t.setBounds(50, 14, 280, 20);
            t.addActionListener(this);
            p[2].add(t);
            
            JPanel jp = new JPanel(null);
            jp.setBackground(Color.GRAY);
            jp.setBounds(t.getX()-1, t.getY()+22, 400, 350);
            
            scroll = new JScrollPane(jp);
            scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scroll.setBounds(jp.getBounds());
            scroll.getVerticalScrollBar().setUnitIncrement(12);
            p[2].add(scroll);
            
            //Inserir as Os dados dos alunos na tabela/chechbox
            frase = "select * from usuarios where matricula <> 'admin' order by nome";
            ps = conexao.prepareStatement(frase);
            rs = ps.executeQuery();
            int i=-1, y = 0, x = 0;
            StringBuilder s;
            while(rs.next()){
                s = new StringBuilder();
                i++;
                s.append("(").append(rs.getObject("matricula").toString()).append(") ");
                s.append(rs.getObject("nome"));
                
                alun_arr.add(i, new JCheckBox(s.toString()));
                alun_arr.get(i).setBounds(0, y, (int) alun_arr.get(i).getPreferredSize().getWidth(), 20);
                alun_arr.get(i).addActionListener(this);
                alun_arr.get(i).addMouseListener(new MouseListener() {
                    public void mouseClicked(MouseEvent me) {
                        if(me.getClickCount() == 2) janelaInformacoes(me.getSource().toString());
                    }
                    public void mousePressed(MouseEvent me) {}
                    public void mouseReleased(MouseEvent me) {}
                    public void mouseEntered(MouseEvent me) {}
                    public void mouseExited(MouseEvent me) {}
                });
                
                jp.add(alun_arr.get(i));
                
                if(alun_arr.get(i).getWidth() > alun_arr.get(x).getWidth()) x = i;
                
                y+=22;
                
            }
            //Redimensiona o quadro
            jp.setPreferredSize(new Dimension( (int) alun_arr.get(x).getPreferredSize().getWidth(), y));
            
            //Insere o texto no "topo"
            Topo(-contador, 1, alun_arr);
            
        }catch(Exception e){
            System.out.println("Erro em QuadroAlunos: "+e);
            if(alun_arr.isEmpty()){
                if(count_quadro > 0) return;
                JOptionPane.showMessageDialog(null, "\nNenhum aluno cadastrado.\n\nNão há alunos para serem vizualizados, \ncadastre alunos manualmente para prosseguir.", "Erro", JOptionPane.ERROR_MESSAGE, null);
                throw new Exception();
            }
            JOptionPane.showMessageDialog(null, "Um erro inesperado ocorreu \nA operação será encerrada", "Erro", JOptionPane.ERROR_MESSAGE);
            throw new Exception();
        }finally{
            new Conexao_BD().FecharConexao(conexao, ps, rs, janela0);
        }
        
    }
    
    public void janelaInformacoes(String matricula){
        //Se não tiver conexão
        conexao = new Conexao_BD().VerificarConexao(janela0);
        if(conexao == null) return;
        
        //Separa o matricula
        matricula = matricula.substring(matricula.indexOf("(")+1, matricula.indexOf(")"));
        //Busca as informações no banco
        try{
            //Informações da table "Alunos"
            StringBuilder s = new StringBuilder();
            String frase = "select * from usuarios where matricula = ?";
            ps = conexao.prepareStatement(frase);
            ps.setString(1, matricula);
            rs = ps.executeQuery();
            while(rs.next()){
                s.append("Aluno(a): ").append(rs.getObject("nome"));
                s.append("\nMatrícula: ").append(rs.getObject("matricula"));
                s.append("\nTurma: ").append(rs.getObject("turma"));
                s.append("\nEmail: ").append(rs.getObject("email"));
                s.append("\nCurso: ").append(rs.getObject("curso"));
                s.append("\n\nDisciplinas: \n");
            }
            
            //Info da table "aluno_disciplina"
            ArrayList<String> s2 = new ArrayList<>();
            String aux = "";
            frase = "SELECT * FROM usuario_disciplina " +
                    "JOIN disciplinas ON disciplinas.codigo = usuario_disciplina.disciplinas_codigo " +
                    "WHERE usuarios_matricula = ? " +
                    "ORDER BY disciplinas.ordem";
            ps = conexao.prepareStatement(frase);
            ps.setString(1, matricula);
            rs = ps.executeQuery();
            ResultSet rs2 = null;
            while(rs.next()){
                aux = rs.getObject("disciplinas_codigo").toString();
                
                //Nota do aluno na disciplina atual
                s.append("("+rs.getObject("nota")+") ");
                
                //Info da table "Disciplinas"
                frase = "select * from disciplinas where codigo = ?";
                ps = conexao.prepareStatement(frase);
                ps.setString(1, aux);
                rs2 = ps.executeQuery();
                while(rs2.next()){
                    s.append(rs2.getObject("nome")).append("\n");
                }
                
            }
            
            JOptionPane.showMessageDialog(null, s,"Informações do discente", JOptionPane.PLAIN_MESSAGE);
        }catch(Exception e){
            System.out.println("Erro em janelaInformacoes: "+e);
            JOptionPane.showMessageDialog(null, "Um erro inesperado ocorreu \nAs informações não puderam ser mostradas", "Erro", JOptionPane.ERROR_MESSAGE);
        }finally{
            new Conexao_BD().FecharConexao(conexao, ps, rs, janela0);
        }
        
    }
    
    public void Editar(){
        //Se não tiver conexão
        conexao = new Conexao_BD().VerificarConexao(janela0);
        if(conexao == null) return;
        
        if(contador == 0){
            JOptionPane.showMessageDialog(null, "Selecione um aluno para prosseguir.", "Erro ao selecionar", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(contador > 1){
            JOptionPane.showMessageDialog(null, "Apenas um (1) perfil pode ser selecionado.", "Erro ao selecionar", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try{
            // Encontra o checkbox que foi selecionado
            String frase;
            int aux = -1;
            int i;
            while(true){
                aux++;
                if(alun_arr.get(aux).isSelected()) break;
                if(aux > alun_arr.size()) throw new Exception();
            }
            
            // Pega o n_matricula dele
            String matricula = alun_arr.get(aux).getText();
            matricula = matricula.substring(matricula.indexOf("(")+1, matricula.indexOf(")"));
            
            // Obtém os dados de "alunos" e insere no 'resps'
            frase = "select matricula, nome, email, curso, turma from usuarios where matricula = ?";
            ps = conexao.prepareStatement(frase);
            ps.setString(1, matricula);
            rs = ps.executeQuery();
            while(rs.next()){
                for (aux = 0; aux < 5; aux++) {
                    resps.add(aux, rs.getString(aux+1));
                }
            }
            
            // Obtém os dados de "disciplinas" e "aluno_disciplina" e insere no 'resps'
            //Info da table "aluno_disciplina"
            String aux2 = "";
            StringBuilder s = new StringBuilder();
            frase = "select * from usuario_disciplina where usuarios_matricula = ?";
            ps = conexao.prepareStatement(frase);
            ps.setString(1, matricula);
            rs = ps.executeQuery();
            ResultSet rs2 = null;
            while(rs.next()){
                aux2 = rs.getObject("disciplinas_codigo").toString();
                
                //Info da table "Disciplinas"
                frase = "select * from disciplinas where codigo = ?";
                ps = conexao.prepareStatement(frase);
                ps.setString(1, aux2);
                rs2 = ps.executeQuery();
                int j = 5;
                while(rs2.next()){
                    resps.add(5, rs2.getObject("nome").toString());
                    j++;
                }
            }
            
            // Mandar pro "Adicionar Alunos" com os valores inseridos
            adicionar.doClick();
            
        }catch(Exception e){
            System.out.println("Erro em Editar, Listar: "+e);
            JOptionPane.showMessageDialog(null, "Ocorreu um erro inesperado. Tente novamente.", "Erro", JOptionPane.ERROR_MESSAGE);
        }finally{
            new Conexao_BD().FecharConexao(conexao, ps, rs, janela0);
        }
        
    }
    
    public void Deletar(){
        //Se não tiver conexão
        conexao = new Conexao_BD().VerificarConexao(janela0);
        if(conexao == null) return;
        
        if(contador == 0){
            JOptionPane.showMessageDialog(null, "Selecione um aluno para prosseguir.", "Erro ao selecionar", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int aux = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja deletar "+contador+" cadastro(s)?", "Mensagem", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if(aux == 1){
            JOptionPane.showMessageDialog(null, "Operação cancelada", "Mensagem", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        try{
            // Encontra os checkboxes que foram selecionados e deletar seus perfis
            String frase, s;
            for (int i = 0; i < alun_arr.size(); i++) {
                
                // Separa o "matricula" para deletar
                if(alun_arr.get(i).isSelected()){
                    s = alun_arr.get(i).getText();
                    s = s.substring(s.indexOf("(")+1, s.indexOf(")"));
                    
                    //"Alunos"
                    frase = "delete from usuarios where matricula = ?";
                    ps = conexao.prepareStatement(frase);
                    ps.setString(1, s);
                    ps.executeUpdate();
                    
                    //"Alun_disciplina"
                    frase = "delete from usuario_disciplina where usuarios_matricula = ?";
                    ps = conexao.prepareStatement(frase);
                    ps.setString(1, s);
                    ps.executeUpdate();
                }
            }
            
            JOptionPane.showMessageDialog(null, "Operação realizada com sucesso!", "Concluído", JOptionPane.INFORMATION_MESSAGE);
            // Atualiza o quadro e a variável 't'
            count_quadro++;
            p[2].remove(scroll);
            QuadroAlunos();
            Topo(-contador, 1, alun_arr);
            tabs.setComponentAt(tabs.getTabCount()-1, new JPanel(null));
            tabs.setComponentAt(tabs.getTabCount()-1, p[2]);
            
        }catch(Exception e){
            System.out.println("Erro em Deletar, Listar: "+e);
            JOptionPane.showMessageDialog(null, "Ocorreu um erro inesperado. Tente novamente.", "Erro", JOptionPane.ERROR_MESSAGE);
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
            
            //Editar
            if(e.getSource().equals(editar)) Editar();

            //Deletar
            if(e.getSource().equals(deletar)) Deletar();

            //Adicionar
            if(e.getSource().equals(adicionar)){
                conexao = new Conexao_BD().VerificarConexao(janela0);
                if(conexao != null){
                    tabs.setEnabled(true);
                    if( new Adicionar_Alunos().Abrir(resps) == 1) tabs.setComponentAt(tabs.getTabCount()-1, p[2]);
                    tabs.setEnabled(false);
                }
            }
            
            return;
        }
        
        //Checkboxes:
        if(e.getSource().equals(t)){
            Boolean a;
            int b = 0;
            Topo(-contador, 1, alun_arr);
            
            if(t.isSelected()){
                a = true; b = 1;
            }
            else{
                a = false; b = -1;
                
                Topo(alun_arr.size(), 1, alun_arr);
            }
            for (int i = 0; i < alun_arr.size(); i++) {
                alun_arr.get(i).setSelected(a);
                Topo(b, 1, alun_arr);
            }
        }
        // Verifica se os checkboxes foram selecionados ou não
        for(int i=0; i < alun_arr.size(); i++){
            if(e.getSource().equals(alun_arr.get(i))){
            
            if(alun_arr.get(i).isSelected()) Topo(1, 1, alun_arr);
            else Topo(-1, 1, alun_arr);
            }
        }
        if(contador == alun_arr.size()) t.setSelected(true);
        else t.setSelected(false);
    }
}
