public class Coordinates {
	
	// INSTANCE VARIABLES
	private int rowIndex;
	private int colIndex;
	
	// CONSTRUCTOR - uses (row, col) NOT (x,y)
	public Coordinates(int rowIndex, int colIndex) {
		this.rowIndex = rowIndex;
		this.colIndex = colIndex;
	}
	
	// TOOLBOX
	public static int convToRow(int y) {
		return Habitat.HABITAT_WIDTH-y;
	}
	public static int convToCol(int x) {
		return x-1;
	}
	public static int convToX(int colIndex) {
		return colIndex+1;
	}
	public static int convToY(int rowIndex) {
		return Habitat.HABITAT_WIDTH-rowIndex;
	}
	
	// GETTERS - no need for setters; just create new Coordinates object
	public int row() {
		return rowIndex;
	}
	public int col() {
		return colIndex;
	}
	public int x() {
		return convToX(colIndex);
	}
	public int y() {
		return convToY(rowIndex);
	}
	
	// METHODS - "Distance" is defined as the number of steps a Creature needs to take (taxicab distance)
	public int distanceTo(Coordinates other) { // Returns sum of vert and hor distance between this and other
		int diffX = Math.abs(this.x()-other.x());
		int diffY = Math.abs(this.y()-other.y());
		return diffX + diffY;
	}
	public int[] DxDyTo(Coordinates other) { // Returns [x,y] values by which this needs to move to overlap other
		int[] DxDy = new int[2];
		DxDy[0] = other.x() - this.x();
		DxDy[1] = other.y() - this.y();
		return DxDy;
	}
	public int directionFacing(Coordinates other) { // Returns Cartesian quadrant direction by which this needs to move to overlap other
		int[] DxDy = DxDyTo(other);
		int Dx = DxDy[0];
		int Dy = DxDy[1];
		if (Dx > 0 && Dy > 0)
			return 1;
		if (Dx < 0 && Dy > 0)
			return 2;
		if (Dx < 0 && Dy < 0)
			return 3;
		if (Dx > 0 && Dy < 0)
			return 4;
		return 0; // If all fails for some reason; will cause wanderDxDy to return [0,0]
	}
	
	public static int distanceBetween(Coordinates c1, Coordinates c2) { // Returns sum of vert and hor distance between c1 and c2
		int diffX = Math.abs(c1.x()-c2.x());
		int diffY = Math.abs(c1.y()-c2.y());
		return diffX + diffY;
	}
	
	// toString Overrider - prints out POSITION (x,y)
	public String toString() {
		return "(" + x() + "," + y() + ")";
	}
	
}