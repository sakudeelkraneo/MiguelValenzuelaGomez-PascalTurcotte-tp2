package TP02;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.io.File;
import java.io.FileOutputStream;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImagesWeb {

	public static byte[] getImages(String src) throws IOException {

		// https://examples.javacodegeeks.com/enterprise-java/html/download-images-from-a-website-using-jsoup/

		// Open a URL Stream
		URL url = new URL(src);
		InputStream in = url.openStream();

		// http://www.baeldung.com/convert-input-stream-to-array-of-bytes
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nRead;
		byte[] data = new byte[16384];
		while ((nRead = in.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}
		buffer.flush();
		byte[] dataImage = buffer.toByteArray();
		in.close();

		return dataImage;
	}// End getImages

	public static String getImagesName(String src) throws IOException {

		int indexname = src.lastIndexOf("/");
		if (indexname == src.length()) {
			src = src.substring(1, indexname);
		}
		indexname = src.lastIndexOf("/");
		String name = src.substring(indexname + 1, src.length());

		return name;
	}// end public static String getImagesName

	public static String extractName(String nameOfFichier) throws IOException {
		
		int indexname = nameOfFichier.lastIndexOf(".");
		String name = nameOfFichier.substring(0, indexname);
		
		return name;
	}// end public static String extractName

	public static String extractExtension(String nameOfFichier) throws IOException {
		
		int indexname = nameOfFichier.lastIndexOf(".");
		String name = nameOfFichier.substring(indexname + 1, nameOfFichier.length());

		return name;
	}// end public static String extractExtension

	public static void createFile(byte[] dataImage, String name, String type, String folder) throws IOException {
		File outputFile = new File(folder + name + "." + type);
		try (FileOutputStream outputStream = new FileOutputStream(outputFile);) {
			outputStream.write(dataImage);
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}// end public static void createFile

	public static String resize500(String name, String type, String folderInput) throws IOException {
		File input = new File(folderInput + name + "." + type);
		BufferedImage image = ImageIO.read(input);

		Image tmp = image.getScaledInstance(500, 500, Image.SCALE_SMOOTH);
		BufferedImage resized = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = resized.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();

		String folderOutput = "/home/tpascal/Documents/unihiver2018/sysDis/sdTP02/TP02/images500/";
		String file500 = folderOutput + name + "_500x500." + type;
		File output = new File(file500);
		ImageIO.write(resized, type, output);

		return file500;

	}// end public static String resize500

	public static String resize1000(String name, String type, String folderInput) throws IOException {
		File input = new File(folderInput + name + "." + type);
		BufferedImage image = ImageIO.read(input);

		Image tmp = image.getScaledInstance(1000, 1000, Image.SCALE_SMOOTH);
		BufferedImage resized = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = resized.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();

		String folderOutput = "/home/tpascal/Documents/unihiver2018/sysDis/sdTP02/TP02/images1000/";
		String file1000 = folderOutput + name + "_1000x1000." + type;
		File output = new File(file1000);
		ImageIO.write(resized, type, output);

		return file1000;

	}// end public static String resize1000

}// end Class
