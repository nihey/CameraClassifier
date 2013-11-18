package com.classapps.cameraclassifier;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;

import android.util.Log;

public class BIC {
	
	/* As duas primeiras funcoes sao chamadas pela terceira funcao (BIC). 
	A funcao BIC deve ser chamada para cada imagem tirada e ela cria o descritor para a imagem.
	Nessa funcao, o vetor de atributos features e alocado fora dessa funcao e passado como parametro.
	Depois e so pegar cada descritor e escrever em algum arquivo, nao sei como vai fazer essa parte, se vai usar .txt mesmo ou outro arquivo.
	Ai depois o arquivo com os descritores tem que ser utilizado no classificador que você tinha falado com o Moacir
	Cada classificador pede uma formatacao diferente do arquivo, entao me fala qual classificador voce vai usar que eu faco um pseudo-codigo de como deve ser gravado no arquivo. */

		/*	Funcao QuantizationMSB
		 Quantiza uma imagem de acordo com a quantidade de cores passada por argumento
		 Requer:
			- imagem a ser quantizada
			- imagem onde sera armazenada a imagem quantizada
		 Retorna: 
			- quantidade unicas de cores da imagem quantizada */
		public static int QuantizationMSB(Mat I, Mat Q) {
			
			int bitsc = (int) (Math.log(32)/Math.log(2)); // calcula numero de bits necessarios
			int cc = (int)(bitsc/3); // calcula numero de bits por canal
			int RGBb[]={cc,cc,cc}; // atribui os bits por canal
			int rest = (bitsc % 3); // verifica se ha sobra de bits, armazena o resto
			int k;
			// atribui os bits restantes aos canais R, G e B, em sequencia
			for (k = 0 ;rest > 0; rest--, k = (k+1)%3) {
				RGBb[k]++;
			}
			
			// vetor para armazenar a frequencia de cada cor 
			int[] freq = new int[32];
			int unique = 0;
			
			for(int i = 0; i < Q.height(); i++) {
				
				for(int j = 0; j < Q.width(); j++) {
					
					double it[] = I.get(i, j);
					
					int dR = (int) (8-RGBb[0]);
					int dG = (int) (8-RGBb[1]);
					int dB = (int) (8-RGBb[2]);
					// mascara para realizar AND em cada canal
					int Ra = (int) (((int)(Math.pow(2,RGBb[0]))-1) << dR);
					int Ga = (int) (((int)(Math.pow(2,RGBb[1]))-1) << dG);
					int Ba = (int) (((int)(Math.pow(2,RGBb[2]))-1) << dB);
					
					int R = (int) it[0];
					int G = (int) it[1];
					int B = (int) it[2];
					
					// operacao para obter MSBs em cada canal
					int C1 = (int) ((R & Ra) >> dR);                  // extrai MSBs de R e move p/ o final 
					int C2 = (int) ((G & Ga) >> (dR-RGBb[1]));        // extrai MSBs de G e move p/ apos C1
					int C3 = (int) ((B & Ba) >> (dR-RGBb[1]-RGBb[2]));// extrai MSBs de B e move p/ apos C2
					
//					Log.i("C1", "" + C1);
//					Log.i("C2", "" + C2);
//					Log.i("C3", "" + C3);
					
					int newcolor = (char) (C1 | C2 | C3); // operador | equivalente a 'OU', faz a fusao dos tres componentes
					
					Q.put(i, j, newcolor);
					
					 if (newcolor > 255) Log.w("BIC", "Colorverflow");
					
					// conta quantas cores unicas foram obtidas
					if (freq[newcolor] == 0) unique++;
					
					// frequencia de cada cor quantizada
					freq[newcolor]++;
				}
			}
			
//			for( it2 = Q.begin<uchar>(), end2 = Q.end<uchar>(), it = I.begin<Vec3b>(), end = I.end<Vec3b>(); it != end; ++it, ++it2)
//			{
//				uchar dR = (8-RGBb[0]);
//				uchar dG = (8-RGBb[1]);
//				uchar dB = (8-RGBb[2]);
//				// mascara para realizar AND em cada canal
//				uchar Ra = ((int)(pow(2,RGBb[0]))-1) << dR;
//				uchar Ga = ((int)(pow(2,RGBb[1]))-1) << dG;
//				uchar Ba = ((int)(pow(2,RGBb[2]))-1) << dB;
//				
//				uchar R = (*it)[0];
//				uchar G = (*it)[1];
//				uchar B = (*it)[2];
//				
//				// operacao para obter MSBs em cada canal
//				uchar C1 = (R & Ra) >> dR;                  // extrai MSBs de R e move p/ o final 
//				uchar C2 = (G & Ga) >> (dR-RGBb[1]);        // extrai MSBs de G e move p/ apos C1
//				uchar C3 = (B & Ba) >> (dR-RGBb[1]-RGBb[2]);// extrai MSBs de B e move p/ apos C2
//				
//				uchar newcolor = C1 | C2 | C3; // operador | equivalente a 'OU', faz a fusao dos tres componentes
//				
//				(*it2) = newcolor;
//				
//				// if (newcolor > 255) cout << "color overflow: " << newcolor << endl;
//				
//				// conta quantas cores unicas foram obtidas
//				if (freq[newcolor] == 0) unique++;
//				
//				// frequencia de cada cor quantizada
//				freq[newcolor]++;
//			}	
			
			return unique;
		}



		/* Normaliza um histograma
		 * Funcao para normalizar (entre 0 e 255) o histograma gerado pelo descritor BIC
		 * Requer:
		 *	- o histograma a ser normalizado
		 *	- um histograma ja alocado, para guardar o resultado
		 *	- o tamanho do vetor
		 *	- fator de normalizacao */
		public static void NormalizeHist(long hist[], float histnorm[], int nColor, int fator) {
			
			int i;
			long sum = 0;
			long max = hist[0];
			float e = 0.01f;
			
			
			for (i = 0; i < nColor ; i++) {
				
				sum += hist[i];
				max = (hist[i] > max) ? hist[i] : max;
			}
			
			if (fator == 1) {
				for (i = 0; i < nColor ; i++) {
					
					histnorm[i] = hist[i]/((float)sum+e);
				}
			} 
			else if (fator > 1) {
				for (i = 0; i < nColor ; i++) {
					
					histnorm[i] = (hist[i]/(float)max)*(float)fator;
				}
			}
		}




		/* Descritor BIC
		 * Cria dois histrogramas de cor da imagem:
		 * 1 -> histograma de borda
		 * 2 -> histograma de interior
		 * Requer:
		 *	- imagem original
		 *	- histograma ja alocado, com tamanho de duas vezes a quantidade de cor
		 *	- quantidade de cores usadas na imagem 
		 * No histograma, de 0 até (nColor -1) = Borda, de nColor até (2*nColor -1) = Interior */
		public static void Hist(Mat I, float features[], int nColor) {
			
			Size imgSize = new Size(I.width(), I.height());

			Mat Q = new Mat(imgSize, CvType.CV_8U);

			Log.i("Unique Colors", "" + QuantizationMSB(I, Q));
			
//			Highgui.imwrite(ClassifierService.mPWD + "/Quantic.jpg", Q);
			
			int i, j, cat;
			long[] hist = new long[2*nColor];
			float[] norm = new float[2*nColor];

			for (i = 0; i < 2*nColor; i++) {
				
				hist[i] = 0;    // Initialize all elements to zero.
				norm[i] = 0.0f;  // Initialize all elements to zero.
			}
			
			for (i = 0; i < imgSize.height ; i++) {
				
				for (j = 0; j <  imgSize.width ; j++) {
					
					double []aux = Q.get(i,j);
					
					cat = (int) (aux[0]);
					//Log.i("Catz: ", "" + cat);
					//Log.i("Aux[0]: ", "" + aux[0]);
					
					if (i > 0 && j > 0 && j < imgSize.width -1 && i < imgSize.height -1) {
						
						if ((Q.get(i,j-1) == aux) && 
							(Q.get(i,j+1) == aux) && 
							(Q.get(i-1,j) == aux) && 
							(Q.get(i+1,j) == aux)) {
							
							hist[cat]++;
						}
						else {
							  
							hist[cat+nColor]++;
						}
					}
					else {
						  
						hist[cat+nColor]++;
					}
				}
			}
			
			NormalizeHist(hist, norm, 2*nColor, 255);
			
			for (j = 0; j < 2*nColor ; j ++) {
				
				features[j] = norm[j];
			}
		}
}
