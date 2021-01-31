package csis2450.assignment3;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Month;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;

/**
* CSIS 2450: Assignment 3
* 
* @author Anneliese Braunegg
* 
* Date Created: Saturday, January 30, 2021
* Date Last Updated: Sunday, January 31, 2021
*
*/
public class Assignment3 {
	
	private static final String REPORT_FILE_PATH
		= Paths.get("src", "main", "resources",
			"TemperaturesReportFromDB.txt").toString();
	
	/**
	 * Read an array of temperature data for the specified month from a
	 * database, then return it.
	 * 
	 * NOTE: This method is largely copied from PracticeDBDemo.java, which
	 * was written by Professor John Gordon.
	 * 
	 * @param monthNum: The number of the month whose temperature data to
	 * 	read and return
	 * 
	 * @return The array of temperature data
	 * 
	 * @throws Exception: If an exception occurs upon attempting to access
	 * 	the database or its contents
	 */
	private static final int[][] readTempsArrayFromDatabase(int monthNum)
		throws Exception {
		
		assert 1 <= monthNum && monthNum <= 12;
		
		String connectionString = "jdbc:mysql://127.0.0.1:3306/practice";
        String dbLogin = "javauser";
        String dbPassword = "j4v4us3r?";
        Connection conn = null;
        
        String sql = "SELECT month, day, year, hi, lo FROM temperatures "
        	+ "WHERE month = " + monthNum + " AND year = 2020"
        	+ " ORDER BY month, day, year;";

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
	
	/**
	 * Write a temperatures report header to the console.
	 * 
	 * @param monthNum: The number of the month for which the report
	 * 	header is to be written
	 */
	private static final void writeReportHeaderToConsole(int monthNum) {
		assert 1 <= monthNum && monthNum <= 12;
		
		String monthString = Month.of(monthNum).toString();
		
		System.out.println("--------------------------------------------------------------");
		System.out.println(monthString + " 2020: Temperatures in Utah");
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
	 *  
	 * @param monthNum: The number of the month for which the summary is
	 * 	to be written
	 */
	private static final void writeSummaryToConsole(int[][] tempsArray,
		int monthNum) {
		
		assert 1 <= monthNum && monthNum <= 12;
		
		int indexOfHighestTemp = indexOfHighestTemp(tempsArray);
		int highestDay = tempsArray[indexOfHighestTemp][0];
		int highestValue = tempsArray[indexOfHighestTemp][1];
		int averageHigh = averageHigh(tempsArray);
		
		int indexOfLowestTemp = indexOfLowestTemp(tempsArray);
		int lowestDay = tempsArray[indexOfLowestTemp][0];
		int lowestValue = tempsArray[indexOfLowestTemp][2];
		int averageLow = averageLow(tempsArray);
		
		String monthString = Month.of(monthNum).toString();
		
		System.out.println("--------------------------------------------------------------");
		System.out.printf(monthString + " Highest Temperature: 12/%d: %d"
			+ " Average Hi: %.1f" + System.lineSeparator(),
			highestDay, highestValue, averageHigh);
		System.out.printf(monthString + " Lowest Temperature: 12/%d: %d"
			+ " Average Lo: %.1f" + System.lineSeparator(),
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
	 * @param monthNum: The number of the month for which the report
	 * 	header is to be written
	 */
	private static final void writeReportHeaderToFile(
		PrintWriter printWriter, int monthNum) {
		
		assert 1 <= monthNum && monthNum <= 12;
		
		String monthString = Month.of(monthNum).toString();
		
		printWriter.println("--------------------------------------------------------------");
		printWriter.println(monthString + " 2020: Temperatures in Utah");
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
	 * @param monthNum: The number of the month for which the summary is
	 * 	to be written
	 */
	private static final void writeSummaryToFile(
		int[][] tempsArray, PrintWriter printWriter, int monthNum) {
		
		assert 1 <= monthNum && monthNum <= 12;
		
		int indexOfHighestTemp = indexOfHighestTemp(tempsArray);
		int highestDay = tempsArray[indexOfHighestTemp][0];
		int highestValue = tempsArray[indexOfHighestTemp][1];
		int averageHigh = averageHigh(tempsArray);
		
		int indexOfLowestTemp = indexOfLowestTemp(tempsArray);
		int lowestDay = tempsArray[indexOfLowestTemp][0];
		int lowestValue = tempsArray[indexOfLowestTemp][2];
		int averageLow = averageLow(tempsArray);
		
		String monthString = Month.of(monthNum).toString();
		
		printWriter.println("--------------------------------------------------------------");
		printWriter.printf(monthString + " Highest Temperature: 12/%d: %d"
			+ " Average Hi: %.1f" + System.lineSeparator(),
			highestDay, highestValue, averageHigh);
		printWriter.printf(monthString + " Lowest Temperature: 12/%d: %d "
			+ " Average Lo: %.1f" + System.lineSeparator(),
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
		/* Prompt the user for a month number in the set (11, 12) until
		 * they enter such an integer.
		 */
		System.out.println("Please enter either 11 or 12. The temperature"
			+ " data for the corresponding month in 2020 (November for 11"
			+ " or December for 12) will be retrieved from a database, and"
			+ " a tempeartures report based on that data will be printed"
			+ " to the console as well as printed to a file called"
			+ Assignment3.REPORT_FILE_PATH + ".");
		System.out.println();
		System.out.print("Please enter your selection here: ");
		
		Scanner scanner = new Scanner(System.in);
		String pleaseEnterMessage = "Please enter your selection of 11 or"
			+ " 12 here: ";
		int monthNum = -1;
		while (monthNum < 11 || 12 < monthNum) {
			if (scanner.hasNext()) {
				if (!scanner.hasNextInt()) {
					scanner.next();
					System.out.print(pleaseEnterMessage);
					continue;
				}
				
				monthNum = scanner.nextInt();
				
				if (monthNum < 11 || 12 < monthNum) {
					System.out.print(pleaseEnterMessage);
				}
			}
		}
		scanner.close();
		
		/* Create temperatures array */
		int[][] tempsArray = readTempsArrayFromDatabase(monthNum);
		
		/* Create report file if it does not already exist */
		File reportFile = new File(Assignment3.REPORT_FILE_PATH);
		reportFile.createNewFile();
		
		/* Instantiate a PrintWriter for writing to the report file */
		PrintWriter printWriter = new PrintWriter(
			Assignment3.REPORT_FILE_PATH);
		
		/* Write to the console */
		writeReportHeaderToConsole(monthNum);
		writeHighLowVarianceToConsole(tempsArray);
		writeSummaryToConsole(tempsArray, monthNum);
		writeGraphToConsole(tempsArray);
		
		/* Write to the report file */
		// TODO: Make sure that report overwrites, not appends to, the
		//	report file if the file already exists.
		writeReportHeaderToFile(printWriter, monthNum);
		writeHighLowVarianceToFile(tempsArray, printWriter);
		writeSummaryToFile(tempsArray, printWriter, monthNum);
		writeGraphToFile(tempsArray, printWriter);
		
		/* Close the PrintWriter */
		printWriter.close();
	}
}
