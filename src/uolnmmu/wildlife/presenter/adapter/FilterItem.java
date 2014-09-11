package uolnmmu.wildlife.presenter.adapter;

public class FilterItem {

	private String animalName;
	private boolean selected;
	
	public FilterItem(String animalName, boolean selected) {
		this.setAnimalName(animalName);
		this.setSelected(selected);
	}

	public String getAnimalName() {
		return animalName;
	}

	public void setAnimalName(String animalName) {
		this.animalName = animalName;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
