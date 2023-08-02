
package qetaa.service.cart.helpers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import sun.misc.BASE64Decoder;

public class Helper {
	public static int getRandomInteger(int min, int max) {
		Random random = new Random();
		return random.nextInt(max - min + 1) + min;
	}

	public static String getSecuredRandom() {
		SecureRandom random = new SecureRandom();
		return new BigInteger(130, random).toString(32);
	}

	public static Date addMinutes(Date original, int minutes) {
		return new Date(original.getTime() + (1000L * 60 * minutes));
	}

	public static Date addSeconds(Date original, int seconds) {
		return new Date(original.getTime() + (1000L * seconds));
	}


	public static Date addDeadline(Date original){
		Calendar newDate = Calendar.getInstance();
		newDate.setTime(addMinutes(original, (60*28)));//new time after deadline
		Calendar oldDate = Calendar.getInstance();
		oldDate.setTime(original);
		//now check if friday is involved, add whatever is taken from friday

		int oldDay = oldDate.get(Calendar.DAY_OF_WEEK);
		//if old day is thursday
		if(oldDay == 3||oldDay == 4){
			//add 24 hours + 28
			newDate.setTime(addMinutes(newDate.getTime(), (60*24)));
		}
		return newDate.getTime();
	}




	public String getDateFormat(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSX");
		return sdf.format(date);
	}

	public String getDateFormat(Date date, String pattern){
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}

	public static String cypher(String text) throws NoSuchAlgorithmException {
		String shaval = "";
		MessageDigest algorithm = MessageDigest.getInstance("SHA-256");

		byte[] defaultBytes = text.getBytes();

		algorithm.reset();
		algorithm.update(defaultBytes);
		byte messageDigest[] = algorithm.digest();
		StringBuilder hexString = new StringBuilder();

		for (int i = 0; i < messageDigest.length; i++) {
			String hex = Integer.toHexString(0xFF & messageDigest[i]);
			if (hex.length() == 1) {
				hexString.append('0');
			}
			hexString.append(hex);
		}
		shaval = hexString.toString();

		return shaval;
	}

	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		return map.entrySet()
				.stream()
				.sorted(Map.Entry.comparingByValue(/*Collections.reverseOrder()*/))
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						Map.Entry::getValue,
						(e1, e2) -> e1,
						LinkedHashMap::new));
	}



	public static void writeCartItemImage(String base64Image, Long cartId, Long cartItemId, Date date) throws IOException {
		String imageString = base64Image.split(",")[1];
		BufferedImage image = null;
		byte[] imageBytes;
		BASE64Decoder decoder = new BASE64Decoder();
		imageBytes = decoder.decodeBuffer(imageString);
		ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
		image = ImageIO.read(bis);
		bis.close();

		//
		String directory = getCartItemImageDirectory(date, cartId);
		createDirectory(directory);
		String imageName = cartItemId + ".jpg";
		// create directories

		// write the image to a file
		File outputfile = new File(directory + imageName);
		ImageIO.write(image, "png", outputfile);
	}

	public static void writeVinImage(String base64Image, Long cartId, Date date) throws IOException {
		String imageString = base64Image.split(",")[1];
		BufferedImage image = null;
		byte[] imageBytes;
		BASE64Decoder decoder = new BASE64Decoder();
		imageBytes = decoder.decodeBuffer(imageString);
		ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
		image = ImageIO.read(bis);
		bis.close();
		String directory = getVinImageDirectory(date);
		createDirectory(directory);
		String imageName = cartId + ".jpg";
		// create directories

		// write the image to a file
		File outputfile = new File(directory + imageName);
		ImageIO.write(image, "png", outputfile);

	}

	private static void createDirectory(String directory) throws IOException {
		Files.createDirectories(Paths.get(directory));
	}


	public static String getVinImageDirectory(Date date) {
		Calendar c = new GregorianCalendar();
		c.setTime(date);
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DAY_OF_MONTH);
		return AppConstants.getVINDirectoryWithDate(year, month, day);
	}

	public static String getCartItemImageDirectory(Date date, long cartId) {
		Calendar c = new GregorianCalendar();
		c.setTime(date);
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DAY_OF_MONTH);
		return AppConstants.getCartItemDirectoryWithDate(year, month, day, cartId);
	}


}