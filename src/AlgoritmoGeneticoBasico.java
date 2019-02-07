import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.plaf.synth.SynthSeparatorUI;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import java.text.DecimalFormat;

public class AlgoritmoGeneticoBasico {
	ArrayList<ArrayList<Object>> poblacionInicial = new ArrayList<ArrayList<Object>>(); 
	DecimalFormat formatoIndividuo = new DecimalFormat("00000000");
	DecimalFormat formatoProbabilidad = new DecimalFormat("#.########");
	int cantidadDeIndividuos;
	ArrayList<ArrayList<Object>> poblacionTemporal = new ArrayList<ArrayList<Object>>();
	
	// Datos del individuo
	int indice = 1;
	int valorGenotipo;
	String individuo;
	int calidadDelIndividuo;
	double probabilidadDeSeleccion;
	double probabilidadAcumulada = 0.0;
	int suma = 0;
	
	//Generacion de la población inicial para el algoritmo
	ArrayList<ArrayList<Object>> generarPoblacionInicial() {
		//Cantidad máxima de individuos es 10
		//while(true) {
			//cantidadDeIndividuos = new java.util.Scanner(System.in).nextInt();
			cantidadDeIndividuos = 4;
			//if (cantidadDeIndividuos>1 && cantidadDeIndividuos<11) {
				//break;
			//}
		//}
		
		/* Llenamos las caracteristicas de cada individuo y hasta 
		 * que se llene la población señalada anteriormente
		 * poblacionInicial = [ 
		 * {indice, valorGenotipo, individuo, calidadDelIndividuo, probabilidadDeSelec, probabilidadAcumulada}, 
		 * {   1  ,       13     ,  00001101,          169       ,          0.14      ,          0.14        }, 
		 * {   2  ,       24     ,  00011000,          576       ,          0.49      ,          0.63        }, 
		 * {   3  ,       8      ,  00001000,          64        ,          0.06      ,          0.69        }, 
		 * {   4  ,       19     ,  00010011,          361       ,          0.31      ,          1.00        } ]
		*/
		do {
			ArrayList<Object> datosDelIndividuo = new ArrayList<Object>();
			valorGenotipo = new Random().nextInt(255);
			individuo = convertirDecABin(valorGenotipo);
			calidadDelIndividuo = (int) Math.pow(valorGenotipo, 2);
			suma = suma + calidadDelIndividuo;
			
			datosDelIndividuo.add(indice++);
			datosDelIndividuo.add(valorGenotipo);
			datosDelIndividuo.add(individuo);
			datosDelIndividuo.add(calidadDelIndividuo);
			poblacionInicial.add(datosDelIndividuo);
		} while(indice<=cantidadDeIndividuos);
		
		//Encontrar la probabilidad de seleccion correspondiente 
		for (int i=0; i<poblacionInicial.size(); i++) {
			probabilidadDeSeleccion = Double.parseDouble(poblacionInicial.get(i).get(3) + "")/suma;
			probabilidadAcumulada = probabilidadAcumulada + probabilidadDeSeleccion;
			poblacionInicial.get(i).add(formatoProbabilidad.format(probabilidadDeSeleccion));
			poblacionInicial.get(i).add(formatoProbabilidad.format(probabilidadAcumulada));
		}
		
		return poblacionInicial;
	}
	
	//Seleccionar un individuo de la poblacion inicial
	ArrayList<Object> seleccionarIndividuo(ArrayList<ArrayList<Object>> poblacion) {
		indice = 1;
		double seleccion = Math.random();
		ArrayList<Object> individuoSeleccionado = new ArrayList<Object>();
		
		/* Si la selección entra entre la probabilidad acumulada del
		 * individuo seleccionar el individuo (ver roulette wheel selection)
		 */
		for(int i=0; i<poblacion.size(); i++) {
			if (seleccion>Double.parseDouble(poblacion.get(i).get(5) + "") && 
					seleccion<=Double.parseDouble(poblacion.get(i+1).get(5) + "")) {
				individuoSeleccionado = poblacion.get(i+1);
				break;
			} else { // Verificando el primer intervalo ya que compara desde el segundo intervalo.
				if (seleccion<=Double.parseDouble(poblacion.get(0).get(5) + "")) {
					individuoSeleccionado = poblacion.get(0);
					break;
				}
			}
		}
		
		return individuoSeleccionado;
	}
	
	//Llenar la población temporal cruzando los individuos. Retorna el mejor individuo
	ArrayList<Object> realizarCruzamiento() {
		ArrayList<Object> padre = seleccionarIndividuo(poblacionInicial);
		ArrayList<Object> madre = seleccionarIndividuo(poblacionInicial);
		
		suma = 0;
		
		/* De manera aleatoria se cruzan el padre y la madre. 
		 * SI es verdadero se cruzan SINO pasan a ser nuevos individuos
		 */
		if (new Random().nextBoolean()) {
			int puntoDeCruzamiento = new Random().nextInt(7);
			String cruzaPadre = "";
			String cruzaMadre = "";
			
			System.out.println("puntC: " + puntoDeCruzamiento);
			
			// Cruzamiento
			for(int i=0; i<padre.get(2).toString().length(); i++) {
				if (i<=puntoDeCruzamiento) {
					cruzaPadre = cruzaPadre + madre.get(2).toString().charAt(i);
					cruzaMadre = cruzaMadre + padre.get(2).toString().charAt(i);
				} else {
					cruzaPadre = cruzaPadre + padre.get(2).toString().charAt(i);
					cruzaMadre = cruzaMadre + madre.get(2).toString().charAt(i);
				}
			}
			
			/*
			 * Se asignan los valores de los individuos de la nueva poblacion
			 */
			
			valorGenotipo = convertirBinADec(cruzaPadre);
			individuo = cruzaPadre;
			calidadDelIndividuo = (int) Math.pow(valorGenotipo, 2);
			
			padre.clear();
			padre.add(0, 1);
			padre.add(1, valorGenotipo);
			padre.add(2, individuo);
			padre.add(3, calidadDelIndividuo);
			
			valorGenotipo = convertirBinADec(cruzaMadre);
			individuo = cruzaMadre;
			calidadDelIndividuo = (int) Math.pow(valorGenotipo, 2);
			
			madre.clear();
			madre.add(0, 2);
			madre.add(1, valorGenotipo);
			madre.add(2, individuo);
			madre.add(3, calidadDelIndividuo);
			
			//-----------------------------------------------------------------------
			System.out.println(">Padre: " + padre);
			System.out.println(">Madre: " + madre);
			//-----------------------------------------------------------------------
		} else {
			System.out.println("<Padre: " + padre);
			System.out.println("<Madre: " + madre);
			
			padre.remove(4);
			padre.remove(4);
			madre.remove(3);
			madre.remove(3);
		}
		
		// Tomamos el individuo de mayor calidad para la siguiente fase
		if (Integer.parseInt(padre.get(1).toString())>=Integer.parseInt(madre.get(1).toString())) {
			padre.remove(0);
			padre.add(0, indice);
			suma = suma + Integer.parseInt(padre.get(3).toString());
			System.out.println("Seleccion PADRE: " + padre.get(1));
			return padre;
		} else {
			madre.remove(0);
			madre.add(0, indice);
			suma = suma + Integer.parseInt(padre.get(3).toString());
			System.out.println("Seleccion MADRE: " + madre.get(1));
			return madre;
		}
	}
	
	ArrayList<Object> realizarMutacion(String individuoBinario) {
		// Se asigna los valores del mejor individuo al nuevo y se mutan
		ArrayList<Object> nuevoIndividuo = new ArrayList<Object>();
		String nuevoIndividuoBinario = "";
		
		// Se recorre toda la cadena binaria del individuo
		for (int i=0; i<individuoBinario.length(); i++) {
			// Si es verdadero cambiar el bit a su complemento
			if (new Random().nextBoolean()) {
				if (String.valueOf(individuoBinario.charAt(i)).equals("0")) {
					nuevoIndividuoBinario = nuevoIndividuoBinario + "1";
				} else {
					nuevoIndividuoBinario = nuevoIndividuoBinario + "0";
				}
			} else {
				nuevoIndividuoBinario = nuevoIndividuoBinario + individuoBinario.charAt(i);
			}
		}
		
		indice++;
		valorGenotipo = convertirBinADec(nuevoIndividuoBinario);
		individuo = nuevoIndividuoBinario;
		calidadDelIndividuo = (int) Math.pow(valorGenotipo, 2);
		
		nuevoIndividuo.add(indice);
		nuevoIndividuo.add(valorGenotipo);
		nuevoIndividuo.add(individuo);
		nuevoIndividuo.add(calidadDelIndividuo);
		
		return nuevoIndividuo;
	}
	
	//Algoritmo para convertir de decimal a binario
	String convertirDecABin(int dec) {
		String bin = "";
		int num = dec;
		while (num>0) {
			bin = (num%2) + bin;
			num = num/2;
		}

		int binario = Integer.parseInt(bin);
		return formatoIndividuo.format(binario);
	}
	
	//Algoritmo para convertir de binario a decimal
	int convertirBinADec(String bin) {
		int dec = 0;
		int potencia = 0;
		for (int i=bin.length()-1; i>=0; i--) {
			if (String.valueOf(bin.charAt(i)).equals("1")) {
				dec = dec + (int) Math.pow(2, potencia);
			}
			potencia++;
		}
		return dec;
	}
	
	//Realiza las iteraciones para encontrar los mejores individuos
	ArrayList<Object> ejecutarAlgoritmo() {
		ArrayList<Object> mejoresIndividuos = new ArrayList<Object>();
		ArrayList<Object> mejorIndividuo = new ArrayList<Object>();
		ArrayList<Object> individuoMutado = new ArrayList<Object>();
		
		generarPoblacionInicial();
		for (int i=0; i<5; i++) {
			System.out.println((i+1) + ". Poblacion Inicial: \n" + poblacionInicial);
			
			probabilidadDeSeleccion = 0.0;
			probabilidadAcumulada = 0.0;
			
			mejorIndividuo = realizarCruzamiento();
			poblacionTemporal.add(mejorIndividuo);
			
			for (int j=poblacionTemporal.size(); j<poblacionInicial.size(); j++) {
				individuoMutado = realizarMutacion(mejorIndividuo.get(2).toString());
				suma = suma + Integer.parseInt(individuoMutado.get(3).toString());
				poblacionTemporal.add(individuoMutado);
			}
			
			for (int j=0; j<poblacionTemporal.size(); j++) {
				probabilidadDeSeleccion = Double.parseDouble(poblacionTemporal.get(j).get(3) + "")/suma;
				probabilidadAcumulada = probabilidadAcumulada + probabilidadDeSeleccion;
				poblacionTemporal.get(j).add(formatoProbabilidad.format(probabilidadDeSeleccion));
				poblacionTemporal.get(j).add(formatoProbabilidad.format(probabilidadAcumulada));
			}
			
			mejoresIndividuos.add(mejorIndividuo.get(1));
			System.out.println("Mejor Individuo: " + mejorIndividuo.get(1));
			
			poblacionInicial.clear();
			poblacionInicial = poblacionTemporal;
			poblacionTemporal = new ArrayList<ArrayList<Object>>();
		}
		
		return mejoresIndividuos;
	}
	
	void iniciarGUI() {
		ArrayList<Object> mejoresIndividuos = ejecutarAlgoritmo();
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		
		for (int i=0; i<mejoresIndividuos.size(); i++) {
			System.out.print(mejoresIndividuos.get(i) + ", ");
			dataset.addValue((Number) mejoresIndividuos.get(i), "Calidad", (i+1));
		}
        
        JFreeChart chart = ChartFactory.createLineChart("Mejores Individuos", "Individuos", "Calidad", 
        		dataset, PlotOrientation.VERTICAL, true, true, false);
        
        ChartFrame frame = new ChartFrame("Algoritmo Genetico Básico", chart);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
	}
	
	public static void main(String[] args) {
		AlgoritmoGeneticoBasico agb = new AlgoritmoGeneticoBasico();
		System.out.println("PI: " + agb.generarPoblacionInicial());
		System.out.println("RC: " + agb.realizarCruzamiento());
		//agb.iniciarGUI();
	}
}
