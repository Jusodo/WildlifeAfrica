package uolnmmu.wildlife.presenter.comparator;

import java.util.Comparator;

import uolnmmu.wildlife.model.dataTransferObject.Sighting;

public class SightingNameComparator implements Comparator<Sighting> {

	@Override
	public int compare(Sighting lhs, Sighting rhs) {
		if (lhs.getAnimalName() == null && rhs.getAnimalName() == null) {
			return 0;
		}
		if (lhs.getAnimalName() == null) {
			return 1;
		}
		if (rhs.getAnimalName() == null) {
			return -1;
		}

		return lhs.getAnimalName().compareTo(rhs.getAnimalName());
	}
}
