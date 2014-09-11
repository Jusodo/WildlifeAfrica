package uolnmmu.wildlife.model.database;

import java.util.List;

import uolnmmu.wildlife.model.dataTransferObject.Sighting;

public interface IUpdateSightingDatabase {

	/**
	 * Returns a list with all Sightings stored in NewSightingsDB.
	 * 
	 * @return sightingList ArrayList with sightings
	 */
	public List<Sighting> getSightingsFromNewSightingDB();

	/**
	 * Update the SightingDatabase.
	 * 
	 * @param sightingList
	 */
	public void updateSightingDatabase(List<Sighting> sightingList);

	/**
	 * Removes all entries from the NewSightings database.
	 */
	public void clearNewSightingsDatabase();
}
