package uolnmmu.wildlife.model;

import java.io.File;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

public class ImageHelperOld {

	private static final String LOGCAT = ImageHelperOld.class.getSimpleName();
	public static final String DIRECTORY = Environment
			.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
			+ "/Wildlive_Africa";
//			+ File.separator + "Wildlife_Africa" + File.separator;

	/**
	 * Creats and returns a new file to store an image.
	 * 
	 * @param fileName
	 *            name of the image
	 * @return file to store an image
	 */
	public static File getNewFile(String fileName) {

		// check if directory exists
		checkStorageDirectory();
		
		// create new File
		File imageFile = new File(DIRECTORY, fileName);
		try {
			imageFile.createNewFile();
			Log.v(LOGCAT, "New file created: "+imageFile.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return imageFile;
	}

	/**
	 * Check if the target directory exists. If not, try to create.
	 */
	private static void checkStorageDirectory() {
		File storageDir = new File(DIRECTORY);

		if (!storageDir.exists()) {
			if (!storageDir.mkdir()) {
				Log.d(LOGCAT, "Failed to create directory" + DIRECTORY);
			}
			Log.d(LOGCAT, "Direktory " + DIRECTORY + " ready to use");
		}
	}
}
