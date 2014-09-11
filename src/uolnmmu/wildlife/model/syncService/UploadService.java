package uolnmmu.wildlife.model.syncService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uolnmmu.wildlife.model.ImageHelper;
import uolnmmu.wildlife.model.dataTransferObject.Sighting;
import uolnmmu.wildlife.model.database.IUpdateSightingDatabase;
import uolnmmu.wildlife.model.database.SightingDatabase;
import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

public class UploadService extends IntentService {

	private final static String LOGCAT = UploadService.class.getSimpleName();

	private final String URL = "http://192.168.2.100/wildlifeAfrica/addSightings.php";

	private HttpClient client = new DefaultHttpClient();
	private HttpPost httpPost = new HttpPost(URL);

	private ArrayList<Sighting> sightingList;
	private IUpdateSightingDatabase database;

	/**
	 * Constructor.
	 */
	public UploadService() {
		super("UploadService");
		// nothing to do
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.v(LOGCAT, "UploadService created");
		Toast.makeText(getApplicationContext(), "Upload Started",
				Toast.LENGTH_SHORT).show();

		database = new SightingDatabase(this);
		sightingList = (ArrayList<Sighting>) database
				.getSightingsFromNewSightingDB();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		notifyDownloadService();
		Log.v(LOGCAT, "UploadService done");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		uloadDataToServer();

		database.clearNewSightingsDatabase();
	}

	/**
	 * Uploads all new sightings to the server.
	 */
	private void uloadDataToServer() {

		// convert ArrayList to JSON
		JSONArray jsonArray = sightingListToJSON(sightingList);

		try {
			StringEntity entity = new StringEntity(jsonArray.toString());
			Log.d(LOGCAT, "Try to upload: " + jsonArray.toString());

			entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
					"application/json"));
			this.httpPost.setEntity(entity);

			// Execute HTTP Post Request
			HttpResponse response = client.execute(httpPost);

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Converts an ArrayList with sightings into a JSONArray.
	 * 
	 * @param sightingList
	 *            list with sightings
	 * @return JSONArray
	 */
	private JSONArray sightingListToJSON(ArrayList<Sighting> sightingList) {

		JSONArray array = new JSONArray();

		for (Sighting s : sightingList) {
			JSONObject object = new JSONObject();

			String encodedImage = encodeImageFromPath(s.getImageName());
			try {
				object.put("animal_name", s.getAnimalName());
				object.put("image_path", s.getImageName());
				object.put("gps_latitude", s.getLatitude());
				object.put("gps_longitude", s.getLongitude());
				object.put("timestamp", s.getTimestamp());
				object.put("description", s.getDescription());
				object.put("observer_name", s.getObserverName());
				object.put("image", encodedImage);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			array.put(object);
		}
		return array;
	}

	/**
	 * If the file exists it returns an encoded Base64 String. If the file do
	 * not exists it returns null.
	 * 
	 * @param imageName
	 *            name of the image
	 * @return bitmap if available else null
	 */
	private String encodeImageFromPath(String imageName) {

		ImageHelper imageHelper = new ImageHelper(getApplicationContext());
		Bitmap bitmap = imageHelper.getImageFromInternalStorage(imageName);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] byteArray = baos.toByteArray();

		return Base64.encodeToString(byteArray, Base64.DEFAULT);
	}

	/**
	 * Sends a Broadcast Intent to start the DownloadService.
	 */
	private void notifyDownloadService() {

		Toast.makeText(getApplicationContext(), "Upload Complete",
				Toast.LENGTH_SHORT).show();

		// start DonwloadService
		Intent dlService = new Intent(this, DownloadService.class);
		startService(dlService);
	}

}
