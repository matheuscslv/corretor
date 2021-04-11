package Principal;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class Adicionar_Alunos extends Janela implements ActionListener{
    JTextField[] respostas = new JTextField[3];
    JButton disciplinas, inicio, adicionar, deletar, listar;
    
    String[] cursos = {"Selecione...","Administração","Arquitetura e Urbanismo", "Artes", "Ciências Ambientais", "Ciências Biológicas", "Ciência da Computação", "Ciências Sociais", "Direito", "Educação Física", "Enfermagem", "Engenharia Civil", "Engenharia Elétrica", "Farmácia", "Física", "Fisioterapia", "Geografia", "História", "Jornalismo", "Letras", "Licenciatura Indígena", "Matemática", "Medicina", "Pedagogia", "Química", "Relações Internacionais", "Teatro", "Tecnologia em Secretariado", "Outro"};
    JComboBox cbox_curso = new JComboBox(cursos), cbox_turma = new JComboBox();
    ArrayList<JCheckBox> discip_arr = new ArrayList<>();
    
    public int Abrir(ArrayList<String> auto_p){
        //Configurações iniciais
        conexao = Conexao_BD.Conector();
        p[1] = new JPanel(null);
        Topo(-contador, 0, discip_arr);
        t.setSelected(false);
        
        // Labels e campos de resposta
        frases[0] = new JLabel("Nº de matrícula* : ");
        frases[0].setBounds(30, 0, 250, 50);
        respostas[0] = new JTextField();
        respostas[0].setBounds(20+100, frases[0].getY()+14, 100, 20);
        respostas[0].addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if(e.getKeyChar() == '(' || e.getKeyChar() == ')') e.consume();
            }
        });
        
        frases[1] = new JLabel("Nome completo : ");
        frases[1].setBounds(30, frases[0].getY()+40, 250, 50);
        respostas[1] = new JTextField();
        respostas[1].setBounds(20+100, frases[1].getY()+14, 250, 20);
        respostas[1].addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if(e.getKeyChar() == '(' || e.getKeyChar() == ')') e.consume();
            }
        });
        
        frases[2] = new JLabel("E-mail : ");
        frases[2].setBounds(30, frases[1].getY()+40, 150, 50);
        respostas[2] = new JTextField();
        respostas[2].setBounds(20+100, frases[2].getY()+14, 250, 20);
        
        frases[3] = new JLabel("Curso : ");
        frases[3].setBounds(30, frases[2].getY()+40, 150, 50);
        cbox_curso.setBounds(100-25, frases[3].getY()+14, 200-36, 20);
        
        frases[4] = new JLabel("Turma : ");
        frases[4].setBounds(300-27, frases[3].getY(), 150, 50);
        cbox_turma.setBounds(300+20, frases[4].getY()+14, 50, 22);
        
        // Datas no 'Turmas'
        int aux = Integer.parseInt(new SimpleDateFormat("yyyy").format(Calendar.getInstance().getTime()));
        for (int i = 0; i <= aux-1990; i++) {
            if(i==0){
                cbox_turma.addItem("    -");
                cbox_turma.addItem(aux);
                continue;
            }
            cbox_turma.addItem(aux-i);
        }
        
        frases[5] = new JLabel("Disciplinas : ");
        frases[5].setBounds(30, frases[4].getY()+40, 250, 50);
        
        //Botões de baixo: inicio/adicionar/deletar/listar
        inicio = new JButton("Início");
        inicio.setBounds(25-8, frases[5].getY()+frases[5].getHeight()+195/*350+75*/, 87, 25);
        inicio.addActionListener(this);
        adicionar = new JButton("Adicionar");
        adicionar.setBounds(inicio.getX()+inicio.getWidth()+15, inicio.getY(), 87, 25);
        adicionar.addActionListener(this);
        deletar = new JButton("Deletar");
        deletar.setBounds(adicionar.getX()+adicionar.getWidth()+15, inicio.getY(), 87, 25);
        deletar.addActionListener(this);
        listar = new JButton("Listar");
        listar.setBounds(deletar.getX()+deletar.getWidth()+15, inicio.getY(), 87, 25);
        listar.addActionListener(this);
        
        //Inserir no panel
        for (int i = 0; i < 6; i++) {
            if(i<3) p[1].add(respostas[i]);
            p[1].add(frases[i]);
        }
        p[1].add(inicio);
        p[1].add(adicionar);
        p[1].add(deletar);
        p[1].add(listar);
        p[1].add(cbox_curso);
        p[1].add(cbox_turma);
        
        // Monta o quadro
        try{
            QuadroDisciplinas();
        }catch(Exception e){
            System.out.println("Erro no Adicionar_Alunos: "+e);
            inicio.doClick();
            return 1;
        }
        
        // Verifica se deve auto completar os campos(Listar Todos --> Editar)
        AutoPreencher(auto_p);
        
        // Insere o panel na Tab
        tabs.setComponentAt(tabs.getTabCount()-1, p[1]);
        
        return 0;
    }
    
    public void QuadroDisciplinas() throws Exception{
        try{
            String frase;
            // Componentes: scrollbar, panel para checkboxes e parte de cima
            t.setBounds(101, frases[5].getY()+14-2, 280, 20);
            t.addActionListener(this);
            p[1].add(t);
            
            JPanel jp = new JPanel(null);
            jp.setBackground(Color.GRAY);
            jp.setBounds(100, frases[5].getY()+14+20, 270, 200);

            JScrollPane scroll = new JScrollPane(jp);
            scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
            scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scroll.setBounds(jp.getBounds());
            scroll.getVerticalScrollBar().setUnitIncrement(12);
            p[1].add(scroll);

            //Inserir as disciplinas na tabela
            frase = "select * from disciplinas order by nome";
            ps = conexao.prepareStatement(frase);
            rs = ps.executeQuery();
            int i=-1, y = 0, x = 0;
            while(rs.next()){
                i++;
                frase = rs.getObject("nome").toString();
                
                discip_arr.add(i, new JCheckBox(frase));
                discip_arr.get(i).setBounds(0, y, (int) discip_arr.get(i).getPreferredSize().getWidth(), 20);
                discip_arr.get(i).addActionListener(this);
                
                jp.add(discip_arr.get(i));
                
                if(discip_arr.get(i).getWidth() > discip_arr.get(x).getWidth()) x = i;
                
                y+=21;
                
            }
            //Redimensiona o quadro
            jp.setPreferredSize(new Dimension( (int) discip_arr.get(x).getPreferredSize().getWidth(), y));
            
            //Insere o texto no "topo"
            Topo(-contador, 0, discip_arr);
            
        }catch(Exception e){
            System.out.println("Erro em QuadroDisciplinas: "+e);
            if(discip_arr.isEmpty()){
                JOptionPane.showMessageDialog(null, "\nNenhuma disciplina cadastrada \n\n-Você não pode cadastrar alunos sem disciplinas para serem designadas a eles. \n(Crie disciplinas para continuar)", "Erro", JOptionPane.ERROR_MESSAGE, null);
                throw new Exception();
            }
            JOptionPane.showMessageDialog(null, "Um erro inesperado ocorreu \nA operação será encerrada", "Erro", JOptionPane.ERROR_MESSAGE);
            throw new Exception();
        }finally{
            new Conexao_BD().FecharConexao(conexao, ps, rs, janela0);
        }
        
    }
    
    public void Respostas(){
        //Se não tiver conexão
        conexao = new Conexao_BD().VerificarConexao(janela0);
        if(conexao == null) return;
        
        // Checa se algum campo ficou vazio
        for (int i = 0; i < 3; i++) {
            if(respostas[i].getText().isEmpty()){
                JOptionPane.showMessageDialog(null, "Preencha todos os campos para prosseguir.", "Erro na inserção", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        // Checa se o Curso ñ foi selecionado
        if(cbox_curso.getSelectedIndex() == 0){
            JOptionPane.showMessageDialog(null, "Selecione uma opção de curso para prosseguir.", "Erro na inserção", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Checa se a Turma ñ foi selecionada
        if(cbox_turma.getSelectedIndex() == 0){
            JOptionPane.showMessageDialog(null, "Selecione uma opção de turma para prosseguir.", "Erro na inserção", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Caso da disciplina "Outro"
        if(cbox_curso.getSelectedIndex() == (cbox_curso.getItemCount()-1)){
            String aux = JOptionPane.showInputDialog(null, "Insira o nome do curso: ", "Outro Curso", JOptionPane.QUESTION_MESSAGE);
            cbox_curso.removeItemAt(cbox_curso.getItemCount()-1);
            cbox_curso.insertItemAt(aux, cbox_curso.getItemCount()-1);
            cbox_curso.insertItemAt("Outro", cbox_curso.getItemCount()-1);
            cbox_curso.setSelectedItem(aux);
        }
        
        if(contador == 0){
            JOptionPane.showMessageDialog(null, "Selecione pelo menos uma disciplina para prosseguir.", "Erro na inserção", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try{
            String frase;
            //Verifica se é Edição ou Inserção
            if(adicionar.getText().equals("Adicionar")){
                
                // Verifica se a matrícula já existe
                try{
                    frase = "select * from usuarios where matricula = ?";
                    ps = conexao.prepareStatement(frase);
                    ps.setString(1, respostas[0].getText());
                    rs = ps.executeQuery();
                    while(rs.next()){
                        throw new IndexOutOfBoundsException();
                    }
                    
                }catch(IndexOutOfBoundsException e){
                    System.out.println("Erro no Adicionar Alunos (Matrícula Repetida): "+e);
                    int r = JOptionPane.showConfirmDialog(null, "Nº de matrícula: '"+respostas[0].getText()+"' já existe. \nDeseja substituir o perfil existente?", "Mensagem", JOptionPane.YES_NO_OPTION, 0);
                    if(r == 1) return;
                    
                }            
            }
            
            //Deletar todos os registros com a mesma matrícula
            frase = "delete from usuario_disciplina where usuarios_matricula = ?";
            ps = conexao.prepareStatement(frase);
            ps.setString(1, respostas[0].getText());
            ps.executeUpdate();
            
            //Insere na table alunos atualizando caso haja existente
            frase = "insert into usuarios(matricula, nome, email, curso, turma, senha, tipo_usuario) values(?,?,?,?,?,?,?) on duplicate key update "
                    + "matricula = values(matricula),"
                    + "nome = values(nome),"
                    + "email = values(email),"
                    + "curso = values(curso),"
                    + "turma = values(turma)";
            ps = conexao.prepareStatement(frase);
            //ps.setString(1, new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime()));
            ps.setString(1, respostas[0].getText());
            ps.setString(2, respostas[1].getText());
            ps.setString(3, respostas[2].getText());
            ps.setString(4, cbox_curso.getSelectedItem().toString());
            ps.setString(5, cbox_turma.getSelectedItem().toString());
            ps.setString(6, respostas[0].getText());
            ps.setString(7, "ALUNO");
            ps.executeUpdate();
            
            //Recebe o id de todas as disciplinas e insere na table aluno_disciplina
            String aux = "";
            for (int i = 0; i < discip_arr.size(); i++) {
                if(!discip_arr.get(i).isSelected()) continue;
                //id
                frase = "select * from disciplinas where nome = ?";
                ps = conexao.prepareStatement(frase);
                ps.setString(1, discip_arr.get(i).getText());
                rs = ps.executeQuery();
                while(rs.next()){
                    aux = rs.getObject("codigo").toString();
                }
                //insere a matricula e id
                frase = "insert into usuario_disciplina(periodos_periodo,usuarios_matricula,disciplinas_codigo) values(?,?,?)";
                ps = conexao.prepareStatement(frase);
                ps.setString(1, "2019.1");
                ps.setString(2, respostas[0].getText());
                ps.setString(3, aux);
                ps.executeUpdate();
                System.out.println("código: "+aux);
            }
            
            //Limpando os campos
            for (int i = 1; i < 3; i++) {
                respostas[i].setText("");
            }
            conexao.close();
            
            JOptionPane.showMessageDialog(null, "Operação realizada com sucesso!", "Concluído", JOptionPane.INFORMATION_MESSAGE);
            if(adicionar.getText().equals("Editar")) listar.doClick();
            respostas[0].requestFocus();
            
        }catch(NullPointerException e) {
            System.out.println("Erro em Respostas2: "+e);
            JOptionPane.showMessageDialog(null, "Pelo menos uma disciplina deve ser selecionada", "Erro na seleção", JOptionPane.ERROR_MESSAGE, null);
            
        }catch(Exception e){
            System.out.println("Erro no Respostas3: "+e);
            JOptionPane.showMessageDialog(null, "Um erro inesperado ocorreu \nA operação será encerrada", "Erro", JOptionPane.ERROR_MESSAGE);
        }finally{
            new Conexao_BD().FecharConexao(conexao, ps, rs, janela0);
        }
        
    }
    
    public void Deletar(){
        //Se não tiver conexão
        conexao = new Conexao_BD().VerificarConexao(janela0);
        if(conexao == null) return;
        
        // Checa se a matricula foi inserida
        if(respostas[0].getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Insira o nº de inscrição do(a) aluno(a) para prosseguir.", "Erro na inserção", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Checa se a matricula existe
        String frase;
        try{
            frase = "select * from usuarios where matricula = ?";
            ps = conexao.prepareStatement(frase);
            ps.setString(1, respostas[0].getText());
            rs = ps.executeQuery();
            if (!rs.next()) throw new Exception();
        }catch(Exception e){
            System.out.println("A matricula n existe (deletar): "+e);
            JOptionPane.showMessageDialog(null, "O número de matrícula não foi encontrado.", "Erro na inserção", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            //Pega a matricula e deleta de: alunos
            frase = "delete from usuarios where matricula = ?";
            ps = conexao.prepareStatement(frase);
            ps.setString(1, respostas[0].getText());
            ps.executeUpdate();
            
            //Pega a matricula e deleta de: aluno_disciplina
            frase = "delete from usuario_disciplina where usuarios_matricula = ?";
            ps = conexao.prepareStatement(frase);
            ps.setString(1, respostas[0].getText());
            ps.executeUpdate();
            
            JOptionPane.showMessageDialog(null, "Exclusão realizada com sucesso!", "Concluído", JOptionPane.INFORMATION_MESSAGE);
            
        }catch(Exception e){
            System.out.println("Erro no Deletar (geral): "+e);
            JOptionPane.showMessageDialog(null, "Um erro inesperado ocorreu \nA operação será encerrada", "Erro", JOptionPane.ERROR_MESSAGE);
        }finally{
            new Conexao_BD().FecharConexao(conexao, ps, rs, janela0);
        }
        
    }
    
    public void AutoPreencher(ArrayList<String> auto_p){
        if(auto_p.isEmpty()) return;
        
        // Preenche os campos de texto
        respostas[0].setText(auto_p.get(0));
        respostas[0].setEditable(false);
        respostas[1].setText(auto_p.get(1));
        respostas[2].setText(auto_p.get(2));
        
        // Preenche os comboboxes
        cbox_curso.setSelectedItem(auto_p.get(3));
        if(cbox_turma.getSelectedIndex() == 0){
            cbox_curso.removeItemAt(cbox_curso.getItemCount()-1);
            cbox_curso.insertItemAt(auto_p.get(3), cbox_curso.getItemCount());
            cbox_curso.insertItemAt("Outro", cbox_curso.getItemCount());
            cbox_curso.setSelectedItem(auto_p.get(3));
        }
        cbox_turma.setSelectedItem(Integer.parseInt(auto_p.get(4)));
        
        // Marca as disciplinas (comboboxes)
        for (int i = 0; i < discip_arr.size(); i++) {
            
            for (int j = 5; j < auto_p.size(); j++) {
                if(auto_p.get(j).equals(discip_arr.get(i).getText())) discip_arr.get(i).doClick();   
            }
        }
        
        adicionar.setText("Editar");
        janela0.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
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
            
            //Adicionar
            if(e.getSource().equals(adicionar)) Respostas();

            //Deletar
            if(e.getSource().equals(deletar)) Deletar();

            //Listar
            if(e.getSource().equals(listar)){
                conexao = new Conexao_BD().VerificarConexao(janela0);
                if(conexao != null){
                    tabs.setEnabled(true);
                    if( new Listar().Abrir() == 1) tabs.setComponentAt(tabs.getTabCount()-1, p[1]);
                    tabs.setEnabled(false);
                }
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
