package uolnmmu.wildlife.model.syncService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
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
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

public class DownloadService extends IntentService {

	public static final String BROADCAST_ACTION = "DownloadService";
	private static final String LOGCAT = DownloadService.class.getSimpleName();

	private final String URL = "http://192.168.2.100/wildlifeAfrica/getAllSightings.php";

	private StringBuilder builder = new StringBuilder();
	private HttpClient client = new DefaultHttpClient();
	private HttpGet httpGet = new HttpGet(URL);
	private Intent intent;
	private IUpdateSightingDatabase database;

	/**
	 * Constructor. Calls super().
	 */
	public DownloadService() {
		super("DownloadService");
		// nothing to do
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.v(LOGCAT, "DownloadService created");
		Toast.makeText(getApplicationContext(), "Download Started",
				Toast.LENGTH_SHORT).show();

		database = new SightingDatabase(this);

		intent = new Intent(BROADCAST_ACTION);
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		downloadData();

		saveToDatabase();

		broadcastResult();

	}

	private void downloadData() {

		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}

			} else {
				Log.e(LOGCAT, "Failed to download file");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void saveToDatabase() {

		List<Sighting> list = new ArrayList<Sighting>();

		// String to JSONArray
		JSONArray jsonArray;
		try {
			jsonArray = new JSONArray(builder.toString());

			// JSONArray to JSONObject
			for (int i = 0; i < jsonArray.length(); i++) {
				Sighting sighting = new Sighting();
				JSONObject jsonObj = jsonArray.getJSONObject(i);

				sighting.setAnimalName(jsonObj.getString("animal_name"));
				sighting.setImageName(jsonObj.getString("image_path"));
				sighting.setLatitude(jsonObj.getDouble("gps_latitude"));
				sighting.setLongitude(jsonObj.getDouble("gps_longitude"));
				sighting.setTimestamp(jsonObj.getString("timestamp"));
				sighting.setDescription(jsonObj.getString("description"));
				sighting.setObserverName(jsonObj.getString("observer_name"));

				storeImage(jsonObj.getString("image"),
						jsonObj.getString("image_path"));

				list.add(sighting);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		database.updateSightingDatabase(list);
	}

	/**
	 * Stores an image.
	 * 
	 * @param object
	 */
	private void storeImage(String encodedImage, String imageName) {

		// convert encoded String to byteArray
		byte[] byteArray = Base64.decode(encodedImage, Base64.DEFAULT);
		Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
				byteArray.length);
		
		ImageHelper imageHelper = new ImageHelper(getApplicationContext());
		imageHelper.saveToInternalStorage(bitmap, imageName);
	}

	/**
	 * Fill the Intent and sends the Broadcast.
	 */
	private void broadcastResult() {

		Toast.makeText(getApplicationContext(), "Download Complete",
				Toast.LENGTH_SHORT).show();

		// send Broadcast that the download is complete
		intent.putExtra("string", builder.toString());
		intent.setAction(BROADCAST_ACTION);
		sendBroadcast(intent);
	}

}
