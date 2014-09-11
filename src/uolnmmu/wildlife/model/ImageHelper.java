package uolnmmu.wildlife.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import uolnmmu.wildlife.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class ImageHelper {

	private static final String LOGCAT = ImageHelper.class.getSimpleName();

	private Context context;

	/**
	 * Constructor.
	 * 
	 * @param applicationContext
	 *            the context
	 */
	public ImageHelper(Context applicationContext) {
		this.context = applicationContext;
	}

	/**
	 * Saves an image to the interanl storage. This images is not accessable for
	 * other applications.
	 * 
	 * @param bitmap
	 *            image file
	 * @param filename
	 *            image name
	 */
	public void saveToInternalStorage(Bitmap bitmap, String filename) {

		try {
			Log.d(LOGCAT, "Try to save the image file");
			FileOutputStream fos = context.openFileOutput(filename,
					context.MODE_PRIVATE);

			// compress the image and write it to the output stream
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.close();
		} catch (FileNotFoundException e) {
			Log.d(LOGCAT, "FileOutputStream is not ready");
			e.printStackTrace();
		} catch (IOException e) {
			Log.d(LOGCAT, "FileOutputStream can not be closed");
			e.printStackTrace();
		}
	}

	/**
	 * Returns an image from the internal storage. If the image is not
	 * available, it returns the launcher icon.
	 * 
	 * @param filename
	 *            name of the image
	 * @return an image
	 */
	public Bitmap getImageFromInternalStorage(String filename) {

		Bitmap bitmap = null;
		try {
			// get the Path to the image
			File filePath = context.getFileStreamPath(filename);

			// read the image
			FileInputStream fis = new FileInputStream(filePath);

			// decode the stream to an image
			bitmap = BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			Log.v(LOGCAT, "Image not Found. Return the launcher image.");

			// get the launcher image from the resources
			bitmap = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.ic_launcher);
		} catch (NullPointerException e) {
			Log.v(LOGCAT, "Image not Found. Return the launcher image.");

			// get the launcher image from the resources
			bitmap = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.ic_launcher);
		}

		return bitmap;
	}
}
