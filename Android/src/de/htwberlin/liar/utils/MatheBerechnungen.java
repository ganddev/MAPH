package de.htwberlin.liar.utils;

import android.util.Log;

public class MatheBerechnungen {

	
	public MatheBerechnungen(){
		
	}
	
	
	/**
	 * Berechnet den Mittelwert eines Int-Arrays und gibt diesen wieder zurueck
	 * @param input Das Eingabearray mit Integer-Werten
	 * @return der Mittelwert vom Typ Double aus allen Felder des Eingabearrays
	 */
	private static double mittelwertBerechnen(int[] input){
		int sum = 0;
        for(int i=0; i<input.length; i++){
            sum += input[i];
        }
        return (sum / input.length);
	}
	
	/**
	 * Berechnet die Varianz eines Integerarrays und gibt das Ergebnis als double Wert zurueck
	 * @param input Ein Integer-Array
	 * @return die Varianz vom Typ Double des Eingabe-Int-Array
	 */
	private static double varianzBerechnen(int[] input, double average){
		
		double varianz = 0.0;
        
        for (int i=0; i<input.length;i++){
           varianz += Math.pow((input[i] - average), 2) / (input.length - 1);
        }
        
        return varianz;
        
	}
	
	/**
	 * Standardabweichung aus Mittelwert und Varianz berechnen
	 * 
	 * @author Phill und Patte 
	 * @param input Ein Integerarray
	 * @return die Standardabweichung vom Typ Double des Eingabe-Int-Array
	 */
	public static double standardAbweichung(int[] input){
        
		// Mittelwert --------------------------------------------------------------
		
        double mittelwert = mittelwertBerechnen(input);
        
        // Varianz -----------------------------------------------------------------
        double varianz = varianzBerechnen(input, mittelwert);
        
        // STD DEV -----------------------------------------------------------------
        double std = Math.sqrt(varianz/(input.length-1)); 
        
        return std;
    }
	
	/**
	 * a simple way to store value of type integer in an array - maybe never used!!! :(
	 * @author Patte
	 * @param zaehler the counter of the given array
	 * @param array the array to store the sepcified value
	 * @param wert the specified integer value 
	 * @return the array with the new stored value
	 */
	
	public static int[] werteSichern(int zaehler, int[] array, int wert, String TAG){
				
		array[zaehler] = wert;
		Log.d(TAG, "aktueller Wert im Array "+array[zaehler]+" Counter: "+zaehler);
						
		return array;
	}

}
