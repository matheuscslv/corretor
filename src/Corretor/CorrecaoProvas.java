package Corretor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class CorrecaoProvas {
        
    protected int c = 0;
    protected Color[] cor = new Color[250];
    
    String letra_geral = "";
    
    double nota_curso = 1.4;
            
            
    public static BufferedImage image2BlackWhite(BufferedImage image1) {
        int w = image1.getWidth();
        int h = image1.getHeight();
        byte[] comp = { 0, -1 };
        IndexColorModel cm = new IndexColorModel(2, 2, comp, comp, comp);
        BufferedImage image2 = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_INDEXED, cm);
        Graphics2D g = image2.createGraphics();
        g.drawRenderedImage(image1, null);
        g.dispose();
        return image2;
    }
    
    public void nota(String caminho,int[] gabarito,String saida,String nome, String letra, boolean curso){
        letra_geral = letra;
        BufferedImage imagem = null; 
        try {
            imagem = OpenCV.Faz(caminho);
            verificarMarcadas(imagem);
        } catch (Exception ex) {
            //JOptionPane perguntando se o usuário quer procurar o arquivo:
            int r = JOptionPane.showOptionDialog(null, "A prova: '"+nome+ "' não foi encontrada ou não atende aos requisitos(a imagem não deve possuir acentos)!\nDeseja procurar novamente?", "Alerta", JOptionPane.YES_NO_OPTION,  JOptionPane.ERROR_MESSAGE, null, null, null);            
            if(r==0){
                //Caso 'sim', Procurar dnovo:
                final JFileChooser aux = new JFileChooser();
                aux.setFileFilter(new FileNameExtensionFilter("Imagens", "jpg", "png"));
                aux.setMultiSelectionEnabled(false);
                aux.showOpenDialog(null);
                
                nota(aux.getSelectedFile().getPath(),gabarito,saida, nome, letra, curso);
                //Quando acabar, retorna pro InicialTela:
                return;
            }else{
                //Caso 'não':
                JOptionPane.showMessageDialog(null, "O programa irá continuar de onde parou."); return;
            }
        }
        
        nota_curso = 1.4;
        if(!curso) nota_curso = (10.0/6.0);
        
        double[] resultado = calcularNota(imagem,gabarito);
        
        if(new AlunoExiste().alunoExiste(nome)){
        
            String[] qtdDisciplinas = new LancarNota().pegarCodigoDisciplinasAlunos(nome, letra);       
            double[] notas = new double[qtdDisciplinas.length];
            for(int i=0;i<notas.length;i++){
                notas[i] = 0;                
            }
            
            int inicioQuestao = 8;
            
            
            int x = 1;
            if(letra.equals("a"))
                notas[0] = resultado[0]+resultado[1]+resultado[2]+resultado[3]+resultado[4]+resultado[5]+resultado[6]+resultado[7];
            
            if(letra.equals("b")){
                /*
                int n = new LancarNota().pegarCodigoDisciplinasAlunos(nome, "a").length-1;
                inicioQuestao += n*6;
                */
                inicioQuestao = 0;
                x = 0;
            }
            if(letra.equals("c")){
                inicioQuestao = 0;
                x = 0;
            }
            
            int fimQuestao = 0;
            while(x < qtdDisciplinas.length){
            //for(int i=1;i<qtdDisciplinas.length;i++){
                double quantidade1 =  8.4/(new LancarNota().qtdDisciplinasQueAlunoFaz(qtdDisciplinas[x]));
                BigDecimal bd = new BigDecimal(quantidade1).setScale(3, RoundingMode.HALF_EVEN);
                quantidade1 = bd.doubleValue();

                fimQuestao = inicioQuestao + (new LancarNota().qtdDisciplinasQueAlunoFaz(qtdDisciplinas[x]));

                for(int j=inicioQuestao;j<fimQuestao;j++){
                    
                    if(resultado[j] == 1.4){
                        notas[x] = notas[x] + nota_curso;
                    }
                    
                    // GAMBIARRA EM CIMA DE GAMBIARRA PAPAI
                    if(letra.equals("b") && resultado[j] == 0.125){
                        notas[x] = notas[x] + nota_curso;
                    }
                }

                inicioQuestao = fimQuestao;

                bd = new BigDecimal(notas[x]).setScale(3, RoundingMode.HALF_EVEN);
                notas[x] = bd.doubleValue();
                x++;
            }

            LancarNota nota = new LancarNota();
            nota.armazenarNota(nome,qtdDisciplinas,notas);
            System.out.println(nome);

            for(int i=0;i<qtdDisciplinas.length;i++){
                System.out.println(qtdDisciplinas[i]);
            }

            for(int i=0;i<notas.length;i++){
                System.out.println(notas[i]);
            }
            
        }
                
    }
    
    protected void verificarMarcadas(BufferedImage imagem) { 
        int i = 1;int j = 1;
        i +=82;
        j +=98;
        int aux = j;
        int c = 0;

        for(int m=0;m<14;m++){ 
            //primeira coluna
            for(int k=0;k<5;k++){
                Primeiroloop(cor, i, i+47, j, j+48, imagem, c);
                if(k==4){
                   j = j + 48 + 106; 
                }else{
                    j = j + 48 + 27;
                }
                c++;
            }

            //segunda coluna 
            for(int k=5;k<10;k++){
                Primeiroloop(cor, i, i+47, j, j+48, imagem, c);
                if(k==9){
                   j = j + 48 + 106; 
                }else{
                    j = j + 48 + 27;
                }
                c++;
            }

            //terceira coluna
            for(int k=10;k<15;k++){
                Primeiroloop(cor, i, i+47, j, j+48, imagem, c);
                j = j + 48 + 27;
                c++;
            }

            j = aux;
            i = i + 47 + 19;
        }
        
    }

    private double[] calcularNota(BufferedImage imagem, int[] gabarito) {
        
        double[] resultado = new double[38];
        int valor1 = 0, valor2 = 5, certa = gabarito[0];
        
        if(letra_geral.equals("c")){
            //Conhecimentos Gerais
            System.out.print("3  - ");
            valor1 = 0; valor2 = 5; certa = gabarito[0];
            resultado[0] = Segundoloop(cor, valor1, valor2, certa)*11.2;

            System.out.print("4  - ");
            valor1 = 15; valor2 = 20; certa = gabarito[1];
            resultado[1] = Segundoloop(cor, valor1, valor2, certa)*11.2;

            System.out.print("5  - ");
            valor1 = 30; valor2 = 35; certa = gabarito[2];
            resultado[2] = Segundoloop(cor, valor1, valor2, certa)*11.2;

            System.out.print("6  - ");
            valor1 = 45; valor2 = 50; certa = gabarito[3];
            resultado[3] = Segundoloop(cor, valor1, valor2, certa)*11.2;

            System.out.print("7  - ");
            valor1 = 60; valor2 = 65; certa = gabarito[4];
            resultado[4] = Segundoloop(cor, valor1, valor2, certa)*11.2;

            System.out.print("8  - ");
            valor1 = 75; valor2 = 80; certa = gabarito[5];
            resultado[5] = Segundoloop(cor, valor1, valor2, certa)*11.2;

            System.out.print("9  - ");
            valor1 = 90; valor2 = 95; certa = gabarito[6];
            resultado[6] = Segundoloop(cor, valor1, valor2, certa)*11.2;

            System.out.print("10 - ");
            valor1 = 105; valor2 = 110; certa = gabarito[7];
            resultado[7] = Segundoloop(cor, valor1, valor2, certa)*11.2;
        }
        else{
        
            //Conhecimentos Gerais
            System.out.print("3  - ");
            valor1 = 0; valor2 = 5; certa = gabarito[0];
            resultado[0] = Segundoloop(cor, valor1, valor2, certa);

            System.out.print("4  - ");
            valor1 = 15; valor2 = 20; certa = gabarito[1];
            resultado[1] = Segundoloop(cor, valor1, valor2, certa);

            System.out.print("5  - ");
            valor1 = 30; valor2 = 35; certa = gabarito[2];
            resultado[2] = Segundoloop(cor, valor1, valor2, certa);

            System.out.print("6  - ");
            valor1 = 45; valor2 = 50; certa = gabarito[3];
            resultado[3] = Segundoloop(cor, valor1, valor2, certa);

            System.out.print("7  - ");
            valor1 = 60; valor2 = 65; certa = gabarito[4];
            resultado[4] = Segundoloop(cor, valor1, valor2, certa);

            System.out.print("8  - ");
            valor1 = 75; valor2 = 80; certa = gabarito[5];
            resultado[5] = Segundoloop(cor, valor1, valor2, certa);

            System.out.print("9  - ");
            valor1 = 90; valor2 = 95; certa = gabarito[6];
            resultado[6] = Segundoloop(cor, valor1, valor2, certa);

            System.out.print("10 - ");
            valor1 = 105; valor2 = 110; certa = gabarito[7];
            resultado[7] = Segundoloop(cor, valor1, valor2, certa);
        }
        
        //disciplina A
        System.out.print("11 - ");
        valor1 = 120; valor2 = 125; certa = gabarito[8];
        resultado[8] = Segundoloop(cor, valor1, valor2, certa);

        System.out.print("12 - ");
        valor1 = 135; valor2 = 140; certa = gabarito[9];
        resultado[9] = Segundoloop(cor, valor1, valor2, certa);

        System.out.print("13 - ");
        valor1 = 150; valor2 = 155; certa = gabarito[10];
        resultado[10] = Segundoloop(cor, valor1, valor2, certa);

        System.out.print("14 - ");
        valor1 = 165; valor2 = 170; certa = gabarito[11];
        resultado[11] = Segundoloop(cor, valor1, valor2, certa);

        System.out.print("15 - ");
        valor1 = 180; valor2 = 185; certa = gabarito[12];
        resultado[12] = Segundoloop(cor, valor1, valor2, certa);

        System.out.print("16 - ");
        valor1 = 195; valor2 = 200; certa = gabarito[13];
        resultado[13] = Segundoloop(cor, valor1, valor2, certa);

        //disciplina B
        System.out.print("17 - ");
        valor1 = 5; valor2 = 10; certa = gabarito[14];
        resultado[14] = Segundoloop(cor, valor1, valor2, certa);

        System.out.print("18 - ");
        valor1 = 20; valor2 = 25; certa = gabarito[15];
        resultado[15] = Segundoloop(cor, valor1, valor2, certa);

        System.out.print("19 - ");
        valor1 = 35; valor2 = 40; certa = gabarito[16];
        resultado[16] = Segundoloop(cor, valor1, valor2, certa);

        System.out.print("20 - ");
        valor1 = 50; valor2 = 55; certa = gabarito[17];
        resultado[17] = Segundoloop(cor, valor1, valor2, certa);

        System.out.print("21 - ");
        valor1 = 65; valor2 = 70; certa = gabarito[18];
        resultado[18] = Segundoloop(cor, valor1, valor2, certa);

        System.out.print("22 - ");
        valor1 = 80; valor2 = 85; certa = gabarito[19];
        resultado[19] = Segundoloop(cor, valor1, valor2, certa);

        //disciplina C
        System.out.print("23 - ");
        valor1 = 95; valor2 = 100; certa = gabarito[20];
        resultado[20] = Segundoloop(cor, valor1, valor2, certa);

        System.out.print("24 - ");
        valor1 = 110; valor2 = 115; certa = gabarito[21];
        resultado[21] = Segundoloop(cor, valor1, valor2, certa);

        System.out.print("25 - ");
        valor1 = 125; valor2 = 130; certa = gabarito[22];
        resultado[22] = Segundoloop(cor, valor1, valor2, certa);

        System.out.print("26 - ");
        valor1 = 140; valor2 = 145; certa = gabarito[23];
        resultado[23] = Segundoloop(cor, valor1, valor2, certa);

        System.out.print("27 - ");
        valor1 = 155; valor2 = 160; certa = gabarito[24];
        resultado[24] = Segundoloop(cor, valor1, valor2, certa);

        System.out.print("28 - ");
        valor1 = 170; valor2 = 175; certa = gabarito[25];
        resultado[25] = Segundoloop(cor, valor1, valor2, certa);

        //disciplina D
        System.out.print("29 - ");
        valor1 = 185; valor2 = 190; certa = gabarito[26];
        resultado[26] = Segundoloop(cor, valor1, valor2, certa);

        System.out.print("30 - ");
        valor1 = 200; valor2 = 205; certa = gabarito[27];
        resultado[27] = Segundoloop(cor, valor1, valor2, certa);

        System.out.print("31 - ");
        valor1 = 10; valor2 = 15; certa = gabarito[28];
        resultado[28] = Segundoloop(cor, valor1, valor2, certa);

        System.out.print("32 - ");
        valor1 = 25; valor2 = 30; certa = gabarito[29];
        resultado[29] = Segundoloop(cor, valor1, valor2, certa);

        System.out.print("33 - ");
        valor1 = 40; valor2 = 45; certa = gabarito[30];
        resultado[30] = Segundoloop(cor, valor1, valor2, certa);

        System.out.print("34 - ");
        valor1 = 55; valor2 = 60; certa = gabarito[31];
        resultado[31] = Segundoloop(cor, valor1, valor2, certa);

        //disciplina E
        System.out.print("35 - ");
        valor1 = 70; valor2 = 75; certa = gabarito[32];
        resultado[32] = Segundoloop(cor, valor1, valor2, certa);

        System.out.print("36 - ");
        valor1 = 85; valor2 = 90; certa = gabarito[33];
        resultado[33] = Segundoloop(cor, valor1, valor2, certa);

        System.out.print("37 - ");
        valor1 = 100; valor2 = 105; certa = gabarito[34];
        resultado[34] = Segundoloop(cor, valor1, valor2, certa);

        System.out.print("38 - ");
        valor1 = 115; valor2 = 120; certa = gabarito[35];
        resultado[35] = Segundoloop(cor, valor1, valor2, certa);

        System.out.print("39 - ");
        valor1 = 130; valor2 = 135; certa = gabarito[36];
        resultado[36] = Segundoloop(cor, valor1, valor2, certa);

        System.out.print("40 - ");
        valor1 = 145; valor2 = 150; certa = gabarito[37];
        resultado[37] = Segundoloop(cor, valor1, valor2, certa);
        
        return resultado;
    }
    
    public Color Primeiroloop(Color[] cor, int inicioLinha, int fimLinha, int inicioColuna, int fimColuna, BufferedImage imagem,int k){
        double i3 = 0,i4 = 0;
        
        for(int i = inicioLinha; i <= fimLinha; i++){    // VERIFICAR AS LINHAS //
            for(int j = inicioColuna; j <= fimColuna; j++){     // VERIFICAR AS COLUNAS // 
                cor[k] = new Color(imagem.getRGB(j, i));
                if(cor[k].toString().equals("java.awt.Color[r=255,g=255,b=255]"))i3++;
                i4++;
            }
        }
        
        double quadrado = i3/1000;
        if(quadrado >= 0.5){
            cor[k] = new Color (255,255,255);
        }else{
            cor[k] = new Color(0,0,0);
        }
        
        return cor[k];
         
    }
    
    public double Segundoloop(Color[] cor, int valor1, int valor2, int certa){
        int letra=0, aux=0, uma=0,i;
        c++;
        double resultado = 0;

        for(i = valor1; i < valor2; i++){
            aux++;
            if(cor[i].toString().equals("java.awt.Color[r=255,g=255,b=255]")){
               letra = aux;
               uma++;
            }
        }

        if(certa == 6){
            if(c<=8){
                resultado = 0.125;
            }else{
                resultado = 1.4;
            }
            System.out.println(" ANULADA");
            return resultado;
        }
        
        if(uma>1){ 
            System.out.println(" QUESTÃO RASURADA");
        }else{
            if(uma<1){
                System.out.println(" QUESTÃO NÃO PREENCHIDA"); 
                resultado = 0.0;
            }else{
                if(letra == certa){
                    switch(letra){
                        case 1: {System.out.println("A"); break;}
                        case 2: {System.out.println("B"); break;}
                        case 3: {System.out.println("C"); break;}
                        case 4: {System.out.println("D"); break;}
                        case 5: {System.out.println("E"); break;}
                    }
                    
                    
                    if(c<=8){
                        resultado = 0.125;
                    }else{
                        resultado = 1.4;
                    }
                    
                    
                }else{
                    resultado = 0.0;
                    System.out.print("Errada (");
                    switch(letra){
                        case 1: {System.out.println("A)"); break;}
                        case 2: {System.out.println("B)"); break;}
                        case 3: {System.out.println("C)"); break;}
                        case 4: {System.out.println("D)"); break;}
                        case 5: {System.out.println("E)"); break;}
                    }
                }
            }
        }
        return resultado;
    }

}
