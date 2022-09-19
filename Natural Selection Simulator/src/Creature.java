import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Creature {
	
	// CONSTANTS
	public static final int MAX_ENERGY = 1000; // Energy upon spawning and at the start of each day
	public static final int START_SPEED = 4; // Starting speed
	public static final int START_SIZE = 5; // Starting size
	public static final int MUTATION_CHANCE = 100; // PERCENT chance of passing on mutated genes
	public static final int MUTATION_RANGE = 2; // Maximum change allowed in mutated gene value passed on
	
	// INSTANCE VARIABLES
	// Genes; will always be >= 1
	private int speed; // Defined as number of spaces can move into in one time step; speed shall define sense now too
	private int size; // Creatures 20+% larger can eat smaller creatures
	// Field Values
	private int energy;
	private Coordinates coords;
	private Space[][] senseBox; // The box in which a creature can detect food; shares references with habitat
	private Object targetFood; // The food the Creature will target at a time step; null if none
	private int foodEaten;
	private int daysOld; // For data collection
	private boolean isDead; // Tags it for removal when board updates
	private int cannibalismCount; // For date collection; to be saved before creature is removed
	
	// CONSTRUCTORS - former is default (spawning), latter is for reproduction (passing on genes)
	public Creature() {
		energy = MAX_ENERGY;
		speed = START_SPEED;
		size = START_SIZE;
		int dimensions = 3; // default senseBox is 3x3
		if (speed > 1)
			dimensions = (speed/2)*2+1;
		senseBox = new Space[dimensions][dimensions];
	}
	public Creature(int speed, int size) {
		energy = MAX_ENERGY;
		this.speed = speed;
		this.size = size;
		int dimensions = 3; // default senseBox is 3x3
		if (this.speed > 1)
			dimensions = (this.speed/2)*2+1;
		senseBox = new Space[dimensions][dimensions];
	}
	
	// GETTERS & SETTERS
	public int getSpeed() {
		return speed;
	}
	public int getSize() {
		return size;
	}
	public int getEnergy() {
		return energy;
	}
	public Coordinates coords() {
		return coords;
	}
	public Object getTarget() {
		return targetFood;
	}
	public int getFoodEaten() {
		return foodEaten;
	}
	public int getDaysOld() {
		return daysOld;
	}
	public boolean isDead() {
		return isDead;
	}
	public int getCannibalismCount() {
		return cannibalismCount;
	}
	
	public void refreshValues() { // Changes field values for the next day; coordinates & senseBox remain the same
		energy = MAX_ENERGY;
		foodEaten = 0;
		daysOld++;
	}
	public void setCoords(Coordinates coords) {
		this.coords = coords;
	}
	public void die() {
		isDead = true;
	}
	
	// METHODS
	public void genSenseBox(Space[][] habitat) { // Initialized in constructor; to be updated per time step from Habitat class
		int posRow = coords.row();
		int posCol = coords.col();
		
		for (int r = 0; r < senseBox.length; r++) {
			for (int c = 0; c < senseBox[0].length; c++) {
				int habRow = posRow-(senseBox.length/2)+r;
				int habCol = posCol-(senseBox[0].length/2)+c;
				if (habRow < 0 || habCol < 0 || habRow >= Habitat.HABITAT_WIDTH || habCol >= Habitat.HABITAT_LENGTH) // Out of bounds
					senseBox[r][c] = new Space(null); // Spaces will null Coordinates represent outside of habitat
				else
					senseBox[r][c] = habitat[habRow][habCol]; // aligns senseBox with habitat (share same Space references)
			}
		}
	}
	
	public Creature act(boolean endOfDay) { // Does actions when time step is run (ONE act()/step); Precondition: senseBoxes updated
		locateNearestFood(); // Set target
		
		if (energy < calculateCost(1)) // Not enough energy --> can't move
			return null;
		
		int stepsCapable = calculateStepsCapable(); // Postcondition: [1,speed]
		
		// MOVING AND EATING
		
		Random RNG = new Random();
		if (targetFood == null) { // If no food in range, wander diagonally (towards a corner of their range) at top speed
			int direction = RNG.nextInt(4)+1; // [1,4]: 1-->4 corresponding to quadrant of Cartesian plane
			int priority = RNG.nextInt(2); // [0,1]: 0 is prioritize Dx (even steps first), 1 is prioritize Dy (odd steps first)
			int[] DxDy = wanderDxDy(stepsCapable, direction, priority);
			
			int stepsTaken = moveBy(DxDy[0], DxDy[1]); // MOVE, steps taken saved
			expendEnergy(stepsTaken); // Steps converted to energy, energy deducted
		}
		else {
			if (targetFood instanceof Food && coords.distanceTo(((Food) targetFood).coords()) <= stepsCapable) {
				int[] DxDy = coords.DxDyTo(((Food) targetFood).coords());
				
				int stepsTaken = moveBy(DxDy[0], DxDy[1]); // Moves on top of target
				expendEnergy(stepsTaken); // Steps converted to energy, energy deducted
				eat(targetFood); // Eats target
			}
			else if (targetFood instanceof Creature && coords.distanceTo(((Creature) targetFood).coords()) <= stepsCapable) {
				int[] DxDy = coords.DxDyTo(((Creature) targetFood).coords());
				
				int stepsTaken = moveBy(DxDy[0], DxDy[1]); // Moves on top of target
				expendEnergy(stepsTaken); // Steps converted to energy, energy deducted
				eat(targetFood); // Eats target
			}
			else if (targetFood instanceof Food) { // Within senseBox but cannot reach because of energy
				int direction = coords.directionFacing(((Food) targetFood).coords());
				int priority = new Random().nextInt(2);
				int[] DxDy = wanderDxDy(stepsCapable, direction, priority);
				
				int stepsTaken = moveBy(DxDy[0], DxDy[1]);
				expendEnergy(stepsTaken);
			}
			else if (targetFood instanceof Creature) { // Within senseBox but cannot reach because of energy
				int direction = coords.directionFacing(((Creature) targetFood).coords());
				int priority = new Random().nextInt(2);
				int[] DxDy = wanderDxDy(stepsCapable, direction, priority);
				
				int stepsTaken = moveBy(DxDy[0], DxDy[1]);
				expendEnergy(stepsTaken);
			}
		}		
		
		if (endOfDay) { // end of day check - reproduction
			if (foodEaten >= 2)
				return reproduce();
			return null;
		}
		return null; // not end of day, default return null
	}
	public int calculateStepsCapable() { // Helper method; Precondition: Only called if creature can move (enuf energy for 1+ step)
		// Calculate how many steps creatures can afford to take this time step
		int stepsCapable = 1;
		while (stepsCapable+1 <= speed && energy-calculateCost(stepsCapable+1) >= 0) { // steps/time cannot exceed speed (definition)
			stepsCapable++;
		}
		return stepsCapable;
	}
	public int[] wanderDxDy(int stepsCapable, int direction, int priority) { // Helper method
		int[] DxDy = new int[2];
		int Dx = 0, Dy = 0;
		if (direction == 1) {
			for (int stepNo = 1; stepNo <= stepsCapable; stepNo++) {
				if (stepNo%2 == priority) // F,T... for Dx, T,F... for Dy
					Dy++;
				else
					Dx++;
			}
		}
		else if (direction == 2) {
			for (int stepNo = 1; stepNo <= stepsCapable; stepNo++) {
				if (stepNo%2 == priority) // F,T... for Dx, T,F... for Dy
					Dy++;
				else
					Dx--;
			}
		}
		else if (direction == 3) {
			for (int stepNo = 1; stepNo <= stepsCapable; stepNo++) {
				if (stepNo%2 == priority) // F,T... for Dx, T,F... for Dy
					Dy--;
				else
					Dx--;
			}
		}
		else if (direction == 4) {
			for (int stepNo = 1; stepNo <= stepsCapable; stepNo++) {
				if (stepNo%2 == priority) // F,T... for Dx, T,F... for Dy
					Dy--;
				else
					Dx++;
			}
		}
		DxDy[0] = Dx;
		DxDy[1] = Dy;
		return DxDy;
	}
	
	public void locateNearestFood() { // Updates Object the creature will target
		ArrayList<Object> foodInRange = new ArrayList<Object>();
		for (Space[] row : senseBox) {
			for (Space s : row) {
				Object o = s.getOccupant();
				if (o instanceof Food || o instanceof Creature && canEat((Creature)o)) // Precondition: All dead objects've been removed
					foodInRange.add(o);
			}
		}
		
		if (foodInRange.size() == 0 || targetFood != null) // No food in range || alrdy has a target, leave targetFood unchanged
			return;
		
		Object nearest = foodInRange.get(0);
		Coordinates nearestCoords;
		if (nearest instanceof Food)
			nearestCoords = ((Food)nearest).coords();
		else
			nearestCoords = ((Creature)nearest).coords();
		int distanceToNearest = coords.distanceTo(nearestCoords);
		
		for (Object o : foodInRange) {
			if (o instanceof Food && ((Food)o).coords().distanceTo(coords) < distanceToNearest) {
				distanceToNearest = ((Food)o).coords().distanceTo(coords);
				nearest = o;
			}
			else if (o instanceof Creature && ((Creature)o).coords().distanceTo(coords) < distanceToNearest) {
				distanceToNearest = ((Creature)o).coords().distanceTo(coords);
				nearest = o;
			}
		}
		targetFood = nearest;
	}
	
	public int expendEnergy(int stepsTaken) { // Subtracts from energy based on genes and steps taken; returns energy value subtracted
		int expense = calculateCost(stepsTaken);
		energy -= expense;
		return expense;
	}
	public int calculateCost(int stepsTaken) { // Helper method for expendEnergy(); also returns value without changing energy
		int expense = stepsTaken*(speed*speed + size*size*size);
		return expense;
	}
	public int moveBy(int Dx, int Dy) { // Precondition: sigma(|Dx|+|Dy|) <= speed; returns steps taken; prevents out of bounds
		int newX = coords.x()+Dx;
		int newY = coords.y()+Dy;
		// Ensures that creature does not move out of habitat
		if (newX > Habitat.HABITAT_LENGTH) {
			newX = Habitat.HABITAT_LENGTH;
			Dx = newX-coords.x();
		}
		if (newX < 1) {
			newX = 1;
			Dx = newX-coords.x();
		}
		if (newY > Habitat.HABITAT_WIDTH) {
			newY = Habitat.HABITAT_WIDTH;
			Dy = newY-coords.y();
		}
		if (newY < 1) {
			newY = 1;
			Dy = newY-coords.y();
		}
		int newRow = Coordinates.convToRow(newY);
		int newCol = Coordinates.convToCol(newX);
		setCoords(new Coordinates(newRow, newCol));
		int stepsTaken = Math.abs(Dx) + Math.abs(Dy);
		return stepsTaken;
	}
	public void eat(Object food) { // If food is another Creature, canEat returned true; called after target conflict resolved
		if (food instanceof Food) {
			((Food)food).eaten(); // Tags food as eaten
			foodEaten++;
			targetFood = null; // Resets targetFood
		}
		else if (food instanceof Creature) {
			((Creature)food).die(); // Tags creature as dead (killed)
			foodEaten += 2; // Creatures count as 2 food
			cannibalismCount++;
			targetFood = null; // Resets targetFood
		}
		else
			System.out.println("ERROR: Not food!"); // You never know
	}
	public Creature reproduce() { // Called if can reproduce at the end of the day; returns the offspring
		Random RNG = new Random();
		int mutationTest = RNG.nextInt(100)+1;
		int speedChange = RNG.nextInt(2*MUTATION_RANGE+1)-MUTATION_RANGE;
		int sizeChange = RNG.nextInt(2*MUTATION_RANGE+1)-MUTATION_RANGE;
		if (mutationTest > 0 && mutationTest <= MUTATION_CHANCE) {
			if (speed+speedChange <= 0) // Ensures speed, size >= 1; if generated value violates that, will pass on value of 1 (minimum)
				speedChange = -speed+1;
			if (size+sizeChange <= 0)
				sizeChange = -size+1;
			return new Creature(speed+speedChange, size+sizeChange); // Mutated offspring
		}
		return new Creature(speed, size); // Normal offspring
	}
		
	public boolean canEat(Creature other) { // A creature can eat another if it's at least 50% larger
		return this.size >= 1.5*other.size;
	}
	public static boolean canEat(Creature predator, Creature prey) { // True if predator is 50% larger than prey
		return predator.size >= 1.5*prey.size;
	}
	
	// toString Overrider
	public String toString() {
		return coords + " CREATURE(" + speed + ":" + size + ")";
	}
	
}