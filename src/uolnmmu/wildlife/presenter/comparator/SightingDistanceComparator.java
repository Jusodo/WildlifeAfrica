package uolnmmu.wildlife.presenter.comparator;

import java.util.Comparator;

import uolnmmu.wildlife.model.dataTransferObject.Sighting;

public class SightingDistanceComparator implements Comparator<Sighting> {

	@Override
	public int compare(Sighting lhs, Sighting rhs) {
		if (lhs.getDistance() == -1 && rhs.getDistance() == -1) {
			return 0;
		}
		if (lhs.getDistance() == -1) {
			return 1;
		}
		if (rhs.getDistance() == -1) {
			return -1;
		}
		
		float fistSighting = lhs.getDistance();
		float secondSighting = rhs.getDistance();
		
		if(fistSighting > secondSighting) {
			return 1;
		}
		if(fistSighting<secondSighting) {
			return -1;
		}
		if(fistSighting==secondSighting){
			return 0;
		}
		
		return 0;
		
	}

}
