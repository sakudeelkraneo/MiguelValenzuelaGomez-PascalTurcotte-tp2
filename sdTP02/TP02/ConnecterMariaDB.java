package TP02;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.nio.file.Path;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ConnecterMariaDB {

	public static void envoyerImage(byte[] image) throws IOException {
		//ref: https://www.youtube.com/watch?v=hqHyCZkon34
		Connection myConn = null;
		PreparedStatement myStmt = null;

		FileInputStream input = null;

		try {
			// 1. Get a connection to database
			myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sdtp02", "root", "test");

			// 2. Prepare statement
			String sql = "INSERT INTO images (photo) VALUES (?);";

			myStmt = myConn.prepareStatement(sql);

			myStmt.setBytes(1, image);

			// 4. Execute statement
			// System.out.println("\nStoring resume in database: " + theFile);
			System.out.println(sql);

			myStmt.executeUpdate();

			System.out.println("\nCompleted successfully!");

		} catch (Exception exc) {
			exc.printStackTrace();
		} finally {
			if (input != null) {
				input.close();
			}
			try {
				close(myConn, myStmt);
			} catch (Exception exc) {
				exc.printStackTrace();
			}

		}

	}// End public static void envoyerImage




	public static void envoyerTexte(String messAnglais, String messFrancais) throws IOException {
		//ref: https://www.youtube.com/watch?v=hqHyCZkon34
		
		Connection myConn = null;
		PreparedStatement myStmt = null;
		
		try {
			// 1. Get a connection to database
			myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sdtp02", "root", "test");

			// 2. Prepare statement

			String sql = "INSERT INTO texte (anglais,francais) VALUES (?,?);";
			
			
			myStmt = myConn.prepareStatement(sql);
			
			// 3. Set parameter for resume file name
			myStmt.setString(1, messAnglais);
			myStmt.setString(2, messFrancais);
				
			// 4. Execute statement
			System.out.println(sql);
			myStmt.executeUpdate();
			System.out.println("\nCompleted successfully!");
			
		} catch (Exception exc) {
			exc.printStackTrace();
		} finally {			
			try {
				close2(myConn, myStmt);
			} catch (Exception exc) {
				exc.printStackTrace();
			}			
		}
		
	}// End public static void envoyerTexte



public static String extractFrench(String bothLanguages) throws IOException {
		
		int indexname = bothLanguages.lastIndexOf(".");
		String french = bothLanguages.substring(0, indexname);
		
		return french;
	}// end public static String extractName



	public static String extractEnglish(String bothLanguages) throws IOException {
		
		int indexname = bothLanguages.lastIndexOf(".");
		String english = bothLanguages.substring(indexname + 1, bothLanguages.length());

		return english;
	}// end public static String extractExtension



	private static void close(Connection myConn, Statement myStmt) throws SQLException {

		if (myStmt != null) {
			myStmt.close();
		}

		if (myConn != null) {
			myConn.close();
		}
	}


private static void close2(Connection myConn, Statement myStmt) throws SQLException {

		if (myStmt != null) {
			myStmt.close();
		}

		if (myConn != null) {
			myConn.close();
		}
	}


}// end Class
