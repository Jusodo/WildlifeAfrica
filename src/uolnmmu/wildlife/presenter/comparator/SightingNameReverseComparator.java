package uolnmmu.wildlife.presenter.comparator;

import java.util.Comparator;

import uolnmmu.wildlife.model.dataTransferObject.Sighting;

public class SightingNameReverseComparator implements Comparator<Sighting> {

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

		int value = lhs.getAnimalName().compareTo(rhs.getAnimalName());

		if (value >= 1) {
			return -1;
		}
		if (value <= -1) {
			return 1;
		}
		if (value == 0) {
			return 0;
		}

		return value;
	}

}
