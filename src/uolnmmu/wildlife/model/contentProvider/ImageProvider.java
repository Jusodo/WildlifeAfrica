package uolnmmu.wildlife.model.contentProvider;

import java.io.File;
import java.io.FileNotFoundException;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

public class ImageProvider extends ContentProvider {

	private static final String NAME = "ImageProvider";
	public static final String AUTHORITY = "uolnmmu.wildlife.model.contentProvider";

	// used to match against incomming requests
	private UriMatcher uriMatcher;

	@Override
	public boolean onCreate() {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

		// Add a URI to the matcher. It returns 1 if the incoming Uri matches
		// AUTHORITY.
		uriMatcher.addURI(AUTHORITY, "*", 1);

		return true;
	}

	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode)
			throws FileNotFoundException {

		Log.v(NAME,
				"Called with uri: '" + uri + "'." + uri.getLastPathSegment());

		// Check incoming Uri against the matcher
		switch (uriMatcher.match(uri)) {

		// If it returns 1 - then it matches the Uri defined in onCreate
		case 1:
			String fileLocation = getContext().getFilesDir() + File.separator
					+ uri.getLastPathSegment();

			// Create a ParcelFileDescriptor that points to the file
			ParcelFileDescriptor pfd = ParcelFileDescriptor.open(new File(
					fileLocation), ParcelFileDescriptor.MODE_READ_ONLY);

			return pfd;

		default:
			Log.v(NAME, "Unsupported uri: '" + uri + "'.");
			throw new FileNotFoundException("Unsupported uri: "
					+ uri.toString());
		}
	}

	// -----------------------------------------------------------------------//
	// The following methods are not used

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
