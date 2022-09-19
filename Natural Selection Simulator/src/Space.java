public class Space {
	
	// INSTANCE VARIABLES
	private Object occupant; // null, Creature, or Food
	private Coordinates coords; // null represents outside of habitat
	
	// CONSTRUCTOR - occupant is null by default --> represents empty space; indices represent index position in habitat
	public Space(Coordinates coords) {
		this.coords = coords;
	}
	
	// GETTERS & SETTERS
	public Object getOccupant() {
		return occupant;
	}
	public void setOccupant(Object occupant) {
		this.occupant = occupant;
	}
	public Coordinates coords() {
		return coords;
	}
	
	// toString Overrider
	public String toString() {
		return occupant + " occupant";
	}
	
}