/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package papaleguas;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class PapaLeguas {

    static Random gerador = new Random();
    static final int tam = 30;
    static float percRock = 33;
    static final JFrame f = new JFrame("Papa L�guas");
    static JPanel panel = new JPanel(new GridLayout(tam, tam, 3, 3));
    static JLabel[] labels = new JLabel[tam*tam];
    static int[][] matAdj = new int[tam*tam][tam*tam];
    static int coiote = 0, papaleguas = 0;
    
    static int calculateManhatanDistance(int x1, int y1,int x2,int y2){
        return Math.abs(x1-x2) + Math.abs(y1-y2);
    }

    static void setPapaLeguasCoiote(){
        int xPapa, yPapa, xCoiote, yCoiote;
        int pos;
        
        xPapa = gerador.nextInt(tam);
        yPapa = gerador.nextInt(tam);
        pos = xPapa * yPapa;

        while (!labels[pos].getText().isEmpty()){
            xPapa = gerador.nextInt(tam);
            yPapa = gerador.nextInt(tam);
            pos = xPapa * yPapa;
        }
        
        labels[pos].setText("P");

        xCoiote = gerador.nextInt(tam);
        yCoiote = gerador.nextInt(tam);
        pos = xCoiote * yCoiote;

        while ((!labels[pos].getText().isEmpty())
                || (calculateManhatanDistance
                		(xPapa, yPapa, xCoiote,yCoiote) < calculateManhatanDistance(0, 0, tam-1,tam-1)/3)) {
            xCoiote = gerador.nextInt(tam);
            yCoiote = gerador.nextInt(tam);
            pos = xCoiote * yCoiote;
        }
        
        labels[xCoiote * yCoiote].setText("C");
    }
    
    static void setRocks(){
        int i=0;
        int amountRocks = Math.round(tam * tam * (percRock/100));

        while (i < amountRocks){
           
            int x = gerador.nextInt(tam*tam);
            if (labels[x].getText().isEmpty()){
                labels[x].setText("*");
                
                i++;
            }
        }
    }
    
    static void setMaze(){
        for (int i = 0; i < tam*tam; i++) {
            //JLabel l = new JLabel("" + i, JLabel.CENTER);
            //JLabel l = new JLabel("", JLabel.CENTER);
            //JLabel l = new JLabel(new ImageIcon("image_file.png"), JLabel.CENTER);
            labels[i] = new JLabel("", JLabel.CENTER);
            labels[i].setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
            labels[i].setFont(labels[i].getFont().deriveFont(20f));
            panel.add(labels[i]);
        }
    }
    
    static void drawPanel(){
        f.setContentPane(panel);
        f.setSize(800, 730);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }
    
    static void defineMatrizAdj(){
		int i = 0, j = 0;
		
    	for(i = 0; i < tam*tam; i++){ //percorre linhas da matriz de adjacencia
    		
    		if("*".equals(labels[i].getText())){ //compara posi��o atual, se tiver "*" n�o pode fazer nenhuma liga��o
		 		for(j = 0; j < tam*tam; j++){ //percorre colunas da matriz de adjacencia
    			 	matAdj[i][j] = 0; //atribuindo zero na posi��o atual
    			}
    		}
    		else{
	    		if(((i+1) % tam) != 0){ //condi��o para n�o comparar a pr�xima posi��o na troca de linha 
					if(i < (tam*tam)-1 && !"*".equals(labels[i+1].getText()) ){ 
						matAdj[i][i+1] = 1; //compara se esta vazio a posi��o direita
					}
				}
				
			 	if(i < (tam*tam)-tam && !"*".equals(labels[i+tam].getText())){ //compara se esta vazio a  posi��o baixo
					matAdj[i][i+tam] = 1;
				}
			 	
			 	if((i % tam) != 0){ //condi��o para n�o comparar a posi��o anterior na troca de linha
			 		if(i > 0 && !"*".equals(labels[i-1].getText())){ //compara se esta vazio a  posi��o esquerda
			 			matAdj[i][i-1] = 1;
			 		}
			 	}
			 	
			 	if(i > tam-1 && !"*".equals(labels[i-tam].getText())){ //compara se esta vazio a  posi��o cima
					matAdj[i][i-tam] = 1;
	   			}
			 	
			 	if("C".equals(labels[i].getText())){ //origem
	    			coiote = i;
	    		}
			 	
	    		if("P".equals(labels[i].getText())){ //destino
	    			papaleguas = i;
	    		}
    		}
    	}
    	
    	/*for(i = 0; i < tam*tam; i++){
    		for(j = 0; j < tam*tam; j++){
    			System.out.print(matAdj[i][j]);
    		}
    		System.out.println(" - " + i );
    	}*/
    }
    
    public static void acharMenorCaminho() throws InterruptedException{
		for(int i : caminho()){
			labels[i].setBackground(Color.BLACK);
			Thread.sleep(500);
		}
	}

	public static ArrayList<Integer> caminho() throws InterruptedException{
		
		int custos[] = new int[tam*tam]; //lista que ir� receber custos das posi��es
		int anterior[] = new int[tam*tam]; //lista que ir� receber posi��es anteriores
		Set<Integer> naoVisitados = new HashSet<>(); //lista de posi��es n�o visitadas
		
		
		for(int i = 0; i < tam*tam; i++){
			custos[i] = 9999;  //marcando demais posi��es com valor "infinito"
			anterior[i] = -1; //recebendo -1 para marcar todo o caminho m�nimo depois
			naoVisitados.add(i); //adiciona todas posi��es a lista de n�o visitados
		}
		custos[coiote] = 0; //marcando posi��o de in�cio com valor min�mo
		
		while(!naoVisitados.isEmpty()){
			coiote = menorCusto(custos, naoVisitados);//passa custos e n�o visitados, retorna posi��o do menor custo
            naoVisitados.remove(coiote); //remove posi��o atual da lista de n�o visitados
			
			for(int aux : vizinhos(coiote)){ //percorrer ate acabar os vizinhos
				int custoTotal = custos[coiote] + matAdj[coiote][aux]; //custo no vertice atual + vertice vizinho
				if(custoTotal < custos[aux]){ //se calculo do vizinho atual for menor que o custo total calculado...
					custos[aux] = custoTotal; //custo do vizinho � alterado e...
					anterior[aux] = coiote; //marca vertice atual como anterior ao vizinho.
				}
			}
			labels[coiote].setBackground(Color.GREEN);
			Thread.sleep(100);
			if(coiote == papaleguas){ //encontrou?
				return caminhoFinal(anterior, coiote); //passa lista de anteriores e posi��o atual para criar caminho
			}
		}
		return null; //retorna nulo, se n�o tiver solu��o
	}
	
	public static ArrayList<Integer> caminhoFinal(int[] anterior, int atual) {
        ArrayList<Integer> caminho = new ArrayList<>(); //lista que ir� representar o caminho minimo
        caminho.add(atual); //caminho � criado percorrendo a ultima posi��o at� a primeira
        while (anterior[atual] != -1) {
            caminho.add(anterior[atual]); //adiciona posi��o anterior
            atual = anterior[atual]; //recebe posi��o anterior
        }
        return caminho;
    }
	
	 public static int menorCusto(int[] custo, Set<Integer> naoVisitado) {
	        int infinito = 9999, indiceMinimo = 0; 
	        for (int i : naoVisitado) { //percorre toda lista de n�o visitados
	            if (custo[i] < infinito) {  //se o custo da posi��o i for menor e esta na lista de n�o visitados...
	                infinito = custo[i]; //infinito vai receber custo da posi��o i...
	                indiceMinimo = i; //e � salva o indice com menor custo, at� ent�o.
	            }
	        }
	        return indiceMinimo;
	 }

	public static ArrayList<Integer> vizinhos(int indice) {
		 ArrayList<Integer> vizinho = new ArrayList<>(); //lista que ir� receber numero de vizinhos de cada posi��o
	     for (int i = 0; i < tam*tam; i++){ //percorre colunas da atual posi��o da matriz de adjacencia e ...
	    	 if (matAdj[indice][i] == 1) {  //adiciona novo vizinho se existir aresta.
	    		 vizinho.add(i); //adiciona novo vizinho
	         }
	     }
	     return vizinho; //retorna numero de vizinhos
	}
	
	public static void main(String[] args) throws InterruptedException {
        setMaze();
        drawPanel();
        setRocks();
        setPapaLeguasCoiote();
        defineMatrizAdj();
        acharMenorCaminho();
    }
}