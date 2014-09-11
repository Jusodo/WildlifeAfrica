package uolnmmu.wildlife.model.database;

import java.util.List;

import uolnmmu.wildlife.model.dataTransferObject.Sighting;

public interface ISightingDatabase {

	/**
	 * Adds a new Sighting to the database.
	 * 
	 * @param sighting
	 *            a new Sighting
	 */
	public void addSighting(Sighting sighting);

	/**
	 * Returns a specific Sighting from database.
	 * 
	 * @param sightingId
	 *            sightingId
	 * @return Sighting
	 */
	public Sighting getSighting(int sightingId);

	/**
	 * Returns a list with Sightings.
	 * 
	 * @return List with sighting information
	 */
	public List<Sighting> getSightingList();

	/**
	 * Returns a list with specific Sightings.
	 * 
	 * @param filters
	 *            String-array with filter options
	 * @return List with sighting infrmation
	 */
	public List<Sighting> getFilteredSightingList(List<String> filters);
	
}
