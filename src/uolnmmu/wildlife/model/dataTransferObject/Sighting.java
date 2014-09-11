package uolnmmu.wildlife.model.dataTransferObject;


public class Sighting  {

	private int sightingId;
	private float distance;
	private double latitude, longitude;
	private String animalName, imageName, timestamp, description, observerName;

	/**
	 * Empty Constructor.
	 */
	public Sighting() {
	}

	public int getSightingId() {
		return this.sightingId;
	}

	public void setSightingId(int sightingId) {
		this.sightingId = sightingId;
	}

	public String getAnimalName() {
		return this.animalName;
	}

	public void setAnimalName(String animalName) {
		this.animalName = animalName;
	}

	public String getImageName() {
		return this.imageName;
	}

	public void setImageName(String imagePath) {
		this.imageName = imagePath;
	}

	public double getLatitude() {
		return this.latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return this.longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public float getDistance() {
		return this.distance;
	}

	public void setDistance(float distance) {
		this.distance = distance;
	}

	public String getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getObserverName() {
		return this.observerName;
	}

	public void setObserverName(String observerName) {
		this.observerName = observerName;
	}

}
