public class Food {
	
	// INSTANCE VARIABLE
	private Coordinates coords;
	private boolean eaten; // Tags it for removal when board updates
	
	// CONSTRUCTOR
	public Food() {}
	
	// GETTERS & SETTERS
	public Coordinates coords() {
		return coords;
	}
	public boolean isEaten() {
		return eaten;
	}
	
 	public void setCoords(Coordinates coords) {
		this.coords = coords;
	}
	public void eaten() {
		eaten = true;
	}
	
	// toString Overrider
	public String toString() {
		return coords + " FOOD";
	}
	
}