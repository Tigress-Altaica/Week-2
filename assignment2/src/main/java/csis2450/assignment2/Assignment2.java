package csis2450.assignment2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;

/**
* CSIS 2450: Assignment 2
* 
* @author Anneliese Braunegg
* 
* Date Created: Saturday, January 30, 2021
* Date Last Updated: Saturday, January 30, 2021
*
*/
public class Assignment2 {
	
	private static final String tempsFilePath
		= Paths.get("src", "main", "resources",
			"SLCDecember2020Temperatures.csv").toString();
	
	private static final int numOfTempsRows = 31;
	
	private static final int numOfTempsCols = 3;
	
	private static final String reportFilePath
		= Paths.get("src", "main", "resources",
			"TemperaturesReport.txt").toString();
	
	/**
	 * Read an array of temperature data from the specified file, then
	 * return the array. The temperature data must be organized into rows,
	 * and delimiter used to separate the data values within each row must
	 * be a comma.
	 * 
	 * NOTE: The file must be of a type that is readable by a
	 * BufferedReader taking input of a FileReader taking input of the
	 * file's file path.
	 * 
	 * @param tempsFilePath: Path to the file containing the temperature
	 * 	data
	 * @param numOfRows: Number of rows in the file
	 * @param numOfCols: Number of columns (i.e., data values per row) in
	 * 	the file
	 */
	private static final int[][] readTempsArrayFromFile(
		String tempsFilePath, int numOfRows, int numOfCols)
			throws IOException {
		
		int[][] tempsArray = new int[numOfRows][numOfCols];
		File tempsFile = new File(tempsFilePath);
		BufferedReader bufferedReader
			= new BufferedReader(new FileReader(tempsFile));
		
		int lineIndex = 0;
		String line = null;
		String[] values = null;
		while ((line = bufferedReader.readLine()) != null) {
			values = line.split(",");
			
			tempsArray[lineIndex][0] = Integer.getInteger(values[0]);
			tempsArray[lineIndex][1] = Integer.getInteger(values[1]);
			tempsArray[lineIndex][2] = Integer.getInteger(values[2]);
			
			lineIndex += lineIndex;
		}
		
		bufferedReader.close();
		
		return tempsArray;
	}
	
	/**
	 * Write a temperatures report header to the console.
	 */
	private static final void writeReportHeaderToConsole() {
		
		System.out.println("--------------------------------------------------------------");
		System.out.println("December 2020: Temperatures in Utah");
		System.out.println("--------------------------------------------------------------");
	}
	
	/**
	 * Write the temperature highs, lows, and variances to the console
	 * based on the data in the specified temperatures array. Also include
	 * a header for the data being written.
	 * 
	 * @param tempsArray: The temperatures array, formatted such that each
	 * 	row represents a day, the first column of each row stores the
	 *  number of the day (in the month), the second column of each row
	 *  stores the highest temperature of the day, and the third column
	 *  stores the lowest temperature of the day
	 */
	private static final void writeHighLowVarianceToConsole(
		int[][] tempsArray) {
		
		assert tempsArray != null;
		assert tempsArray.length > 0;
		
		System.out.println("Day   High  Low   Variance");
		System.out.println("--------------------------------------------------------------");
		
		int variance = 0;
		
		for (int i = 0; i < tempsArray.length; i++) {
			variance = tempsArray[i][1] - tempsArray[i][2];
			System.out.printf("%2d    %2d    %2d    %2d"
				+ System.lineSeparator(),
				(i + 1), tempsArray[i][0], tempsArray[i][1],
				tempsArray[i][2], variance);
		}
	}
	
	/**
	 * Write a temperatures summary to the console based on the data in
	 * the specified temperatures array. Also include a header for the
	 * data being written.
	 * 
	 * @param tempsArray: The temperatures array, formatted such that each
	 * 	row represents a day, the first column of each row stores the
	 *  number of the day (in the month), the second column of each row
	 *  stores the highest temperature of the day, and the third column
	 *  stores the lowest temperature of the day
	 */
	private static final void writeSummaryToConsole(int[][] tempsArray) {
		
		int indexOfHighestTemp = indexOfHighestTemp(tempsArray);
		int highestDay = tempsArray[indexOfHighestTemp][0];
		int highestValue = tempsArray[indexOfHighestTemp][1];
		int averageHigh = averageHigh(tempsArray);
		
		int indexOfLowestTemp = indexOfLowestTemp(tempsArray);
		int lowestDay = tempsArray[indexOfLowestTemp][0];
		int lowestValue = tempsArray[indexOfLowestTemp][2];
		int averageLow = averageLow(tempsArray);
		
		System.out.println("--------------------------------------------------------------");
		System.out.printf("December Highest Temperature: 12/%d: %d Average"
			+ " Hi: %.1f" + System.lineSeparator(),
			highestDay, highestValue, averageHigh);
		System.out.printf("December Lowest Temperature: 12/%d: %d Average"
			+ " Lo: %.1f" + System.lineSeparator(),
			lowestDay, lowestValue, averageLow);
		System.out.println("--------------------------------------------------------------");
	}
	
	/**
	 * Write a temperatures graph to the console based on the data in the
	 * specified temperatures array. Also include a header for the data
	 * being written.
	 * 
	 * @param tempsArray: The temperatures array, formatted such that each
	 * 	row represents a day, the first column of each row stores the
	 *  number of the day (in the month), the second column of each row
	 *  stores the highest temperature of the day, and the third column
	 *  stores the lowest temperature of the day
	 */
	private static final void writeGraphToConsole(int[][] tempsArray) {
		
		System.out.println("Graph");
		System.out.println("--------------------------------------------------------------");
		System.out.println("      1   5    10   15   20   25   30   35   40   45   50");
		System.out.println("      |   |    |    |    |    |    |    |    |    |    |");
		System.out.println("--------------------------------------------------------------");
		
		String pluses;
		String minuses;
		for (int i = 0; i < tempsArray.length; i++) {
			
			pluses = StringUtils.repeat("+", tempsArray[i][1]);
			minuses = StringUtils.repeat("-", tempsArray[i][2]);
			
			if (i < 10) {
				System.out.printf("%d  Hi %s" + System.lineSeparator(),
					(i + 1), pluses);
				System.out.printf("%d  Lo %s" + System.lineSeparator(),
					(i + 1), minuses);
			}
			else {
				System.out.printf("%d Hi %s" + System.lineSeparator(),
					(i + 1), pluses);
				System.out.printf("%d Lo %s" + System.lineSeparator(),
					(i + 1), minuses);
			}
		}
		
		System.out.println("--------------------------------------------------------------");
		System.out.println("      |   |    |    |    |    |    |    |    |    |    |");
		System.out.println("      1   5    10   15   20   25   30   35   40   45   50");
		System.out.println("--------------------------------------------------------------");
	}

	
	/**
	 * Use the specified PrintWriter a temperatures report header to a
	 * .txt file.
	 * 
	 * NOTE: The output file must already be specified within the
	 * 	PrintWriter.
	 * 
	 * NOTE 2: This method does not close the PrintWriter.
	 * 
	 * @param printWriter: The PrintWriter
	 */
	private static final void writeReportHeaderToFile(
		PrintWriter printWriter) {
		
		printWriter.println("--------------------------------------------------------------");
		printWriter.println("December 2020: Temperatures in Utah");
		printWriter.println("--------------------------------------------------------------");
	}
	
	/**
	 * Use the specified PrintWriter to write the temperature highs, lows,
	 * and variances to a .txt file based on the data in the specified
	 * temperatures array. Also include a header for the data being
	 * written.
	 * 
	 * NOTE: The output file must already be specified within the
	 * 	PrintWriter.
	 * 
	 * NOTE 2: This method does not close the PrintWriter.
	 * 
	 * @param tempsArray: The temperatures array, formatted such that each
	 * 	row represents a day, the first column of each row stores the
	 *  number of the day (in the month), the second column of each row
	 *  stores the highest temperature of the day, and the third column
	 *  stores the lowest temperature of the day
	 * @param printWriter: The PrintWriter
	 */
	private static final void writeHighLowVarianceToFile(
		int[][] tempsArray, PrintWriter printWriter) {
		
		assert tempsArray != null;
		assert tempsArray.length > 0;
		
		printWriter.println("Day   High  Low   Variance");
		printWriter.println("--------------------------------------------------------------");
		
		int variance = 0;
		
		for (int i = 0; i < tempsArray.length; i++) {
			variance = tempsArray[i][1] - tempsArray[i][2];
			printWriter.printf("%2d    %2d    %2d    %2d"
				+ System.lineSeparator(),
				(i + 1), tempsArray[i][0], tempsArray[i][1],
				tempsArray[i][2], variance);
		}
	}
	
	/**
	 * Use the specified PrintWriter to write a temperatures summary to
	 * a .txt file based on the data in the specified temperatures array.
	 * Also include a header for the data being written.
	 * 
	 * NOTE: The output file must already be specified within the
	 * 	PrintWriter.
	 * 
	 * NOTE 2: This method does not close the PrintWriter.
	 * 
	 * @param tempsArray: The temperatures array, formatted such that each
	 * 	row represents a day, the first column of each row stores the
	 *  number of the day (in the month), the second column of each row
	 *  stores the highest temperature of the day, and the third column
	 *  stores the lowest temperature of the day
	 * @param printWriter: The PrintWriter
	 */
	private static final void writeSummaryToFile(
		int[][] tempsArray, PrintWriter printWriter) {
		
		int indexOfHighestTemp = indexOfHighestTemp(tempsArray);
		int highestDay = tempsArray[indexOfHighestTemp][0];
		int highestValue = tempsArray[indexOfHighestTemp][1];
		int averageHigh = averageHigh(tempsArray);
		
		int indexOfLowestTemp = indexOfLowestTemp(tempsArray);
		int lowestDay = tempsArray[indexOfLowestTemp][0];
		int lowestValue = tempsArray[indexOfLowestTemp][2];
		int averageLow = averageLow(tempsArray);
		
		printWriter.println("--------------------------------------------------------------");
		printWriter.printf("December Highest Temperature: 12/%d: %d Average"
			+ " Hi: %.1f" + System.lineSeparator(),
			highestDay, highestValue, averageHigh);
		printWriter.printf("December Lowest Temperature: 12/%d: %d Average"
			+ " Lo: %.1f" + System.lineSeparator(),
			lowestDay, lowestValue, averageLow);
		printWriter.println("--------------------------------------------------------------");
	}
	
	/**
	 * Use the specified PrintWriter to write a temperatures graph to
	 * a .txt file based on the data in the specified temperatures array.
	 * Also include a header for the data being written.
	 * 
	 * NOTE: The output file must already be specified within the
	 * 	PrintWriter.
	 * 
	 * NOTE 2: This method does not close the PrintWriter.
	 * 
	 * @param tempsArray: The temperatures array, formatted such that each
	 * 	row represents a day, the first column of each row stores the
	 *  number of the day (in the month), the second column of each row
	 *  stores the highest temperature of the day, and the third column
	 *  stores the lowest temperature of the day
	 * @param printWriter: The PrintWriter
	 */
	private static final void writeGraphToFile(
		int[][] tempsArray, PrintWriter printWriter) {
		
		printWriter.println("Graph");
		printWriter.println("--------------------------------------------------------------");
		printWriter.println("      1   5    10   15   20   25   30   35   40   45   50");
		printWriter.println("      |   |    |    |    |    |    |    |    |    |    |");
		printWriter.println("--------------------------------------------------------------");
		
		String pluses;
		String minuses;
		for (int i = 0; i < tempsArray.length; i++) {
			
			pluses = StringUtils.repeat("+", tempsArray[i][1]);
			minuses = StringUtils.repeat("-", tempsArray[i][2]);
			
			if (i < 10) {
				printWriter.printf("%d  Hi %s" + System.lineSeparator(),
					(i + 1), pluses);
				printWriter.printf("%d  Lo %s" + System.lineSeparator(),
					(i + 1), minuses);
			}
			else {
				printWriter.printf("%d Hi %s" + System.lineSeparator(),
					(i + 1), pluses);
				printWriter.printf("%d Lo %s" + System.lineSeparator(),
					(i + 1), minuses);
			}
		}
		
		printWriter.println("--------------------------------------------------------------");
		printWriter.println("      |   |    |    |    |    |    |    |    |    |    |");
		printWriter.println("      1   5    10   15   20   25   30   35   40   45   50");
		printWriter.println("--------------------------------------------------------------");
	}
	
	/**
	 * Return the row index of the highest temperature in the specified
	 * temperatures array.
	 * 
	 * NOTE: This method does not close the PrintWriter.
	 * 
	 * @param tempsArray: The temperatures array, formatted such that each
	 * 	row represents a day, the first column of each row stores the
	 *  number of the day (in the month), the second column of each row
	 *  stores the highest temperature of the day, and the third column
	 *  stores the lowest temperature of the day
	 *  
	 * @return: The highest temperature in the temperatures array
	 */
	private static final int indexOfHighestTemp(int[][] tempsArray) {
		assert tempsArray != null;
		assert tempsArray.length > 0;
		
		Integer highestTempValue = Integer.MIN_VALUE;
		int highestTempIndex = -1;
		
		for (int i = 0; i < tempsArray.length; i++) {
			if (tempsArray[i][1] > highestTempValue) {
				highestTempValue = tempsArray[i][1];
				highestTempIndex = i;
			}
		}
		
		return highestTempIndex;
	}
	
	/**
	 * Return the average highest daily temperature in the specified
	 * temperatures array.
	 * 
	 * NOTE: This method does not close the PrintWriter.
	 * 
	 * @param tempsArray: The temperatures array, formatted such that each
	 * 	row represents a day, the first column of each row stores the
	 *  number of the day (in the month), the second column of each row
	 *  stores the highest temperature of the day, and the third column
	 *  stores the lowest temperature of the day
	 *  
	 * @return: The highest daily temperature in the temperatures array
	 */
	private static final int averageHigh(int[][] tempsArray) {
		assert tempsArray != null;
		assert tempsArray.length > 0;
		
		int highsSum = 0;
		
		for (int i = 0; i < tempsArray.length; i++) {
			highsSum += tempsArray[i][1];
		}
	
		return highsSum / tempsArray.length;
	}
	
	/**
	 * Return the row index of the lowest temperature in the specified
	 * temperatures array.
	 * 
	 * NOTE: This method does not close the PrintWriter.
	 * 
	 * @param tempsArray: The temperatures array, formatted such that each
	 * 	row represents a day, the first column of each row stores the
	 *  number of the day (in the month), the second column of each row
	 *  stores the highest temperature of the day, and the third column
	 *  stores the lowest temperature of the day
	 *  
	 * @return: The lowest temperature in the temperatures array
	 */
	private static final int indexOfLowestTemp(int[][] tempsArray) {
		
		assert tempsArray != null;
		assert tempsArray.length > 0;
		
		Integer lowestTempValue = Integer.MAX_VALUE;
		int lowestTempIndex = -1;
		
		for (int i = 0; i < tempsArray.length; i++) {
			if (tempsArray[i][2] < lowestTempValue) {
				lowestTempValue = tempsArray[i][2];
				lowestTempIndex = i;
			}
		}
		
		return lowestTempIndex;
	}
	
	/**
	 * Return the average lowest daily temperature in the specified
	 * temperatures array.
	 * 
	 * NOTE: This method does not close the PrintWriter.
	 * 
	 * @param tempsArray: The temperatures array, formatted such that each
	 * 	row represents a day, the first column of each row stores the
	 *  number of the day (in the month), the second column of each row
	 *  stores the highest temperature of the day, and the third column
	 *  stores the lowest temperature of the day
	 *  
	 * @return: The lowest daily temperature in the temperatures array
	 */
	private static final int averageLow(int[][] tempsArray) {
		assert tempsArray != null;
		assert tempsArray.length > 0;
		
		int lowsSum = 0;
		
		for (int i = 0; i < tempsArray.length; i++) {
			lowsSum += tempsArray[i][2];
		}
	
		return lowsSum / tempsArray.length;
	}

	/**
	 * Main method for Assignment 2.
	 * 
	 * @param args: This parameter is not used.
	 * 
	 * @throws IOException: Thrown if an IOException occurs during the
	 * 	handling of the temperatures file (input file) or the report file
	 * 	(output file)
	 */
	public static void main(String[] args) throws IOException {
		/* Create temperatures array */
		int[][] tempsArray = readTempsArrayFromFile(
			Assignment2.tempsFilePath, Assignment2.numOfTempsRows,
			Assignment2.numOfTempsCols);
		
		/* Create report file if it does not already exist */
		File reportFile = new File(Assignment2.reportFilePath);
		reportFile.createNewFile();
		
		/* Instantiate a PrintWriter for writing to the report file */
		PrintWriter printWriter = new PrintWriter(
			Assignment2.reportFilePath);
		
		/* Write to the console */
		writeReportHeaderToConsole();
		writeHighLowVarianceToConsole(tempsArray);
		writeSummaryToConsole(tempsArray);
		writeGraphToConsole(tempsArray);
		
		/* Write to the report file */
		writeReportHeaderToFile(printWriter);
		writeHighLowVarianceToFile(tempsArray, printWriter);
		writeSummaryToFile(tempsArray, printWriter);
		writeGraphToFile(tempsArray, printWriter);
		
		/* Close the PrintWriter */
		printWriter.close();
	}
}
