package csis2450.assignment3;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang3.StringUtils;

/**
* CSIS 2450: Assignment 3
* 
* @author Anneliese Braunegg
* 
* Date Created: Saturday, January 30, 2021
* Date Last Updated: Saturday, January 30, 2021
*
*/
public class Assignment3 {
	
	private static final String reportFilePath
		= Paths.get("src", "main", "resources",
			"TemperaturesReportFromDB.txt").toString();
	
	/**
	 * Read an array of temperature data from a database, then return it.
	 * 
	 * NOTE: This method is largely copied from PracticeDBDemo.java, which
	 * was written by Professor John Gordon.
	 * 
	 * @return The array of temperature data
	 * 
	 * @throws Exception: If an exception occurs upon attempting to access
	 * 	the database or its contents
	 */
	private static final int[][] readTempsArrayFromDatabase()
		throws Exception {
		
		String connectionString = "jdbc:mysql://127.0.0.1:3306/practice";
        String dbLogin = "javauser";
        String dbPassword = "j4v4us3r?";
        Connection conn = null;
        
        String sql = "SELECT month, day, year, hi, lo FROM temperatures "
        		+ "WHERE month = 12 AND year = 2020 ORDER BY month, day, year;";

        int[][] dbResults = null;
        
        try {
            conn = DriverManager.getConnection(connectionString, dbLogin, dbPassword);
            if (conn != null) {
                try (Statement stmt = conn.createStatement(
                         ResultSet.TYPE_SCROLL_INSENSITIVE, 
                         ResultSet.CONCUR_UPDATABLE);
                     ResultSet rs = stmt.executeQuery(sql)) {
                    int numRows;
                    int numCols = 5;
                    rs.last();
                    numRows = rs.getRow();
                    System.out.printf("Number of Records: %d%n", numRows);
                    rs.first();
                    dbResults = new int[numRows][numCols];;
                    for (int i = 0; i < numRows; i++) {
                        dbResults[i][0] = Integer.parseInt(rs.getString("month"));
                        dbResults[i][1] = Integer.parseInt(rs.getString("day"));
                        dbResults[i][2] = Integer.parseInt(rs.getString("year"));
                        dbResults[i][3] = Integer.parseInt(rs.getString("hi"));
                        dbResults[i][4] = Integer.parseInt(rs.getString("lo"));
                        rs.next();
                    }
                    	                    
                    // TODO: Remove commented-out section;
//	                    System.out.printf("Number of Array Rows: %d%n", dbResults.length);
//	                	printLine(30);
//	                	System.out.println("Date\t\tHi\tLo");
//	                	printLine(30);
//	                    for (int i = 0; i < dbResults.length; i++)
//	                    {
//	                        System.out.printf("%s/%s/%s\t%s\t%s%n", 
//	                        dbResults[i][0],
//	                        dbResults[i][1],
//	                        dbResults[i][2],
//	                        dbResults[i][3],
//	                        dbResults[i][4]);
//	                    }
//	                	printLine(30);
                } catch (SQLException ex) {
                    throw ex;
                }
            }
        } catch (Exception e) {
            System.out.println("Database connection failed.");
            throw e;
        }

    	return dbResults;
	}
	
	// TODO: Remove the below commented-out method.
//	/**
//	 * Read an array of temperature data from the specified file, then
//	 * return the array. The temperature data must be organized into rows,
//	 * and delimiter used to separate the data values within each row must
//	 * be a comma.
//	 * 
//	 * NOTE: The file must be of a type that is readable by a
//	 * BufferedReader taking input of a FileReader taking input of the
//	 * file's file path.
//	 * 
//	 * @param tempsFilePath: Path to the file containing the temperature
//	 * 	data
//	 * @param numOfRows: Number of rows in the file
//	 * @param numOfCols: Number of columns (i.e., data values per row) in
//	 * 	the file
//	 */
//	private static final int[][] readTempsArrayFromFile(
//		String tempsFilePath, int numOfRows, int numOfCols)
//			throws IOException {
//		
//		int[][] tempsArray = new int[numOfRows][numOfCols];
//		File tempsFile = new File(tempsFilePath);
//		BufferedReader bufferedReader
//			= new BufferedReader(new FileReader(tempsFile));
//		
//		int lineIndex = 0;
//		String line = null;
//		String[] values = null;
//		while ((line = bufferedReader.readLine()) != null) {
//			values = line.split(",");
//			
//			tempsArray[lineIndex][0] = Integer.getInteger(values[0]);
//			tempsArray[lineIndex][1] = Integer.getInteger(values[1]);
//			tempsArray[lineIndex][2] = Integer.getInteger(values[2]);
//			
//			lineIndex += lineIndex;
//		}
//		
//		bufferedReader.close();
//		
//		return tempsArray;
//	}
	
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
	 *  number of the month, the second column of each row stores the
	 *  number of the day, the third column of each row stores the number
	 *  of the year, the fourth column of each row stores the highest
	 *  temperature of the day, and the fifth column of each row stores
	 *  the lowest temperature of the day
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
			System.out.printf("%d/%d/%d   %d   %d   %d"
				+ System.lineSeparator(),
				tempsArray[i][0], tempsArray[i][1],
				tempsArray[i][2], tempsArray[i][3],
				tempsArray[i][4], variance);
		}
	}
	
	/**
	 * Write a temperatures summary to the console based on the data in
	 * the specified temperatures array. Also include a header for the
	 * data being written.
	 * 
	 * @param tempsArray: The temperatures array, formatted such that each
	 * 	row represents a day, the first column of each row stores the
	 *  number of the month, the second column of each row stores the
	 *  number of the day, the third column of each row stores the number
	 *  of the year, the fourth column of each row stores the highest
	 *  temperature of the day, and the fifth column of each row stores
	 *  the lowest temperature of the day
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
	 *  number of the month, the second column of each row stores the
	 *  number of the day, the third column of each row stores the number
	 *  of the year, the fourth column of each row stores the highest
	 *  temperature of the day, and the fifth column of each row stores
	 *  the lowest temperature of the day
	 */
	private static final void writeGraphToConsole(int[][] tempsArray) {
		
		System.out.println("Graph");
		System.out.println("--------------------------------------------------------------");
		System.out.println("      1   5    10   15   20   25   30   35   40   45   50");
		System.out.println("      |   |    |    |    |    |    |    |    |    |    |");
		System.out.println("--------------------------------------------------------------");
		
		int dayNum;
		String pluses;
		String minuses;
		for (int i = 0; i < tempsArray.length; i++) {
			
			dayNum = tempsArray[i][0];
			pluses = StringUtils.repeat("+", tempsArray[i][1]);
			minuses = StringUtils.repeat("-", tempsArray[i][2]);
			
			if (dayNum < 10) {
				System.out.printf("%d  Hi %s" + System.lineSeparator(),
					dayNum, pluses);
				System.out.printf("%d  Lo %s" + System.lineSeparator(),
					dayNum, minuses);
			}
			else {
				System.out.printf("%d Hi %s" + System.lineSeparator(),
					dayNum, pluses);
				System.out.printf("%d Lo %s" + System.lineSeparator(),
					dayNum, minuses);
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
	 *  number of the month, the second column of each row stores the
	 *  number of the day, the third column of each row stores the number
	 *  of the year, the fourth column of each row stores the highest
	 *  temperature of the day, and the fifth column of each row stores
	 *  the lowest temperature of the day
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
			printWriter.printf("%d/%d/%d   %d   %d   %d"
				+ System.lineSeparator(),
				tempsArray[i][0], tempsArray[i][1],
				tempsArray[i][2], tempsArray[i][3],
				tempsArray[i][4], variance);
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
	 *  number of the month, the second column of each row stores the
	 *  number of the day, the third column of each row stores the number
	 *  of the year, the fourth column of each row stores the highest
	 *  temperature of the day, and the fifth column of each row stores
	 *  the lowest temperature of the day
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
	 *  number of the month, the second column of each row stores the
	 *  number of the day, the third column of each row stores the number
	 *  of the year, the fourth column of each row stores the highest
	 *  temperature of the day, and the fifth column of each row stores
	 *  the lowest temperature of the day
	 * @param printWriter: The PrintWriter
	 */
	private static final void writeGraphToFile(
		int[][] tempsArray, PrintWriter printWriter) {
		
		printWriter.println("Graph");
		printWriter.println("--------------------------------------------------------------");
		printWriter.println("      1   5    10   15   20   25   30   35   40   45   50");
		printWriter.println("      |   |    |    |    |    |    |    |    |    |    |");
		printWriter.println("--------------------------------------------------------------");
		
		int dayNum;
		String pluses;
		String minuses;
		for (int i = 0; i < tempsArray.length; i++) {
			
			dayNum = tempsArray[i][0];
			pluses = StringUtils.repeat("+", tempsArray[i][1]);
			minuses = StringUtils.repeat("-", tempsArray[i][2]);
			
			if (dayNum < 10) {
				printWriter.printf("%d  Hi %s" + System.lineSeparator(),
					dayNum, pluses);
				printWriter.printf("%d  Lo %s" + System.lineSeparator(),
					dayNum, minuses);
			}
			else {
				printWriter.printf("%d Hi %s" + System.lineSeparator(),
					dayNum, pluses);
				printWriter.printf("%d Lo %s" + System.lineSeparator(),
					dayNum, minuses);
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
	 *  number of the month, the second column of each row stores the
	 *  number of the day, the third column of each row stores the number
	 *  of the year, the fourth column of each row stores the highest
	 *  temperature of the day, and the fifth column of each row stores
	 *  the lowest temperature of the day
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
				highestTempValue = tempsArray[i][3];
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
	 *  number of the month, the second column of each row stores the
	 *  number of the day, the third column of each row stores the number
	 *  of the year, the fourth column of each row stores the highest
	 *  temperature of the day, and the fifth column of each row stores
	 *  the lowest temperature of the day
	 *  
	 * @return: The highest daily temperature in the temperatures array
	 */
	private static final int averageHigh(int[][] tempsArray) {
		assert tempsArray != null;
		assert tempsArray.length > 0;
		
		int highsSum = 0;
		
		for (int i = 0; i < tempsArray.length; i++) {
			highsSum += tempsArray[i][3];
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
	 *  number of the month, the second column of each row stores the
	 *  number of the day, the third column of each row stores the number
	 *  of the year, the fourth column of each row stores the highest
	 *  temperature of the day, and the fifth column of each row stores
	 *  the lowest temperature of the day
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
				lowestTempValue = tempsArray[i][4];
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
	 *  number of the month, the second column of each row stores the
	 *  number of the day, the third column of each row stores the number
	 *  of the year, the fourth column of each row stores the highest
	 *  temperature of the day, and the fifth column of each row stores
	 *  the lowest temperature of the day
	 *  
	 * @return: The lowest daily temperature in the temperatures array
	 */
	private static final int averageLow(int[][] tempsArray) {
		assert tempsArray != null;
		assert tempsArray.length > 0;
		
		int lowsSum = 0;
		
		for (int i = 0; i < tempsArray.length; i++) {
			lowsSum += tempsArray[i][4];
		}
	
		return lowsSum / tempsArray.length;
	}

	/**
	 * Main method for Assignment 3.
	 * 
	 * @param args: This parameter is not used.
	 * 
	 * @throws Exception: Thrown if an Exception occurs during the reading
	 *  of the temperature data from the database, or if an IOException
	 *  occurs during the handling of the temperatures file (input file)
	 *  or the report file (output file)
	 */
	public static void main(String[] args) throws Exception {
		/* Create temperatures array */
		int[][] tempsArray = readTempsArrayFromDatabase();
		
		/* Create report file if it does not already exist */
		File reportFile = new File(Assignment3.reportFilePath);
		reportFile.createNewFile();
		
		/* Instantiate a PrintWriter for writing to the report file */
		PrintWriter printWriter = new PrintWriter(
			Assignment3.reportFilePath);
		
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
