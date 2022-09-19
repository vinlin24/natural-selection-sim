import java.util.ArrayList;
import java.util.Random;

public class Habitat {
	
	// CONSTANTS
	public static final int HABITAT_SIZE = 20; // Habitat will always be a square (for now) with side length HABITAT_SIZE
	public int START_CREATURES = 15; // Starting number of creatures
	public int START_FOOD = 200; // Starting number of food; refreshes daily
	
	public static final int HABITAT_LENGTH = HABITAT_SIZE; // If I were to extend functionality to non-square habitats
	public static final int HABITAT_WIDTH = HABITAT_SIZE;
	
	// INSTANCE VARIABLES
	private Space[][] habitat; // The grid representing the habitat
	
	private ArrayList<Coordinates> emptyCoords; // List of coords of empty Spaces
	private ArrayList<Creature> creatureList; // List of creatures (alive and dead) currently in the habitat
	private ArrayList<Creature> newBorns; // Stores offspring produced at end of each day; resets daily
	private ArrayList<Food> foodList; // List of food (eaten or not) currently in the habitat
	
	private int birthCount;
	private int deathCount;
	private int cannibalismCount; // Number of times a creature eats another creature
	
	// CONSTRUCTOR
	public Habitat(int startCreatures, int startFood) {
		habitat = new Space[HABITAT_WIDTH][HABITAT_LENGTH]; // Initializes habitat
		for (int r = 0; r < habitat.length; r++) { // Initialize all Space objects with proper coords
			for (int c = 0; c < habitat[0].length; c++) {
				habitat[r][c] = new Space(new Coordinates(r,c));
			}
		}
		
		emptyCoords = allCoords(); // Initialize emptyCoords; initially all coords are empty
		creatureList = new ArrayList<Creature>(); // Initializes creatureList
		newBorns = new ArrayList<Creature>(); // Initializes newBorns
		foodList = new ArrayList<Food>(); // Initializes foodList
		
		START_CREATURES = startCreatures;
		START_FOOD = startFood;
		
		fillBoard(START_CREATURES, START_FOOD); // Spawns the starting creatures and food
	}
	
	// GETTERS & SETTERS
	public Object objectAtCoords(Coordinates coords) {
		int row = coords.row();
		int col = coords.col();
		if (row < 0 || col < 0 || row >= habitat.length || col >= habitat.length) // Precondition violated
			return null;
		return habitat[row][col].getOccupant();
	}
	
	public void setObjectAtCoords(Coordinates coords, Object obj) {
		int row = coords.row();
		int col = coords.col();
		if (row < 0 || col < 0 || row >= habitat.length || col >= habitat[0].length) { // Precondition violated
			System.out.println("ERROR: Coordinates out of bounds");
			return;
		}
		habitat[row][col].setOccupant(obj);
	}
	
	public ArrayList<Coordinates> getEmptyCoords() {
		return emptyCoords;
	}
	public ArrayList<Creature> getCreatureList() {
		return creatureList;
	}
	public ArrayList<Creature> getNewBorns() {
		return newBorns;
	}
	public ArrayList<Food> getFoodList() {
		return foodList;
	}
	
	// METHODS
	
	// Startup
	public ArrayList<Coordinates> allCoords() { // Returns all coordinates of the habitat; ONLY WORKS AT THE START
		ArrayList<Coordinates> coords = new ArrayList<Coordinates>();
		for (Space[] row : habitat) {
			for (Space space : row) {
				coords.add(space.coords());
			}
		}
		return coords;
	}
	// Spawns starting amount of creatures & food dispersed randomly within habitat; Precondition: creatures + food <= number of Spaces
	private void fillBoard(int numCreatures, int numFood) {
		if (numCreatures+numFood > HABITAT_LENGTH*HABITAT_WIDTH) { // Checks for precondition
			System.out.println("ERROR: Not enough space in habitat");
			return;
		}
		Random RNG = new Random();
		// Adds creatures to ArrayList creatureList, spawns creatures, and initializes their coordinates
		for (int cr = 0; cr < numCreatures; cr++) {
			Creature newCreature = new Creature(); // Constructs a new creature
			
			int index = RNG.nextInt(emptyCoords.size());
			Coordinates randCoords = emptyCoords.get(index); // Fetches a random coordinate remaining (still available)
			newCreature.setCoords(randCoords); // Initializes newCreatures's coordinates
			newCreature.genSenseBox(habitat); // Initializes newCreature's senseBox
			
			setObjectAtCoords(randCoords, newCreature); // Adds newCreature to grid at coordinates
			
			creatureList.add(newCreature); // Adds creature to instance variable
			emptyCoords.remove(index); // Those coords are no longer available (occupied)
		}
		// Adds food to ArrayList foodList, spawns food, initializes their coordinates
		for (int f = 0; f < numFood; f++) {
			Food newFood = new Food(); // Constructs new food
			
			int index = RNG.nextInt(emptyCoords.size());
			Coordinates randCoords = emptyCoords.get(index); // Fetches a random coordinate remaining (still available)
			newFood.setCoords(randCoords); // Initializes its coordinates
			
			setObjectAtCoords(randCoords, newFood); // Adds food to grid at coordinates
			
			foodList.add(newFood); // Adds food to instance variable
			emptyCoords.remove(index); // Those coords are no longer available (occupied)
		}
	}
	
	// Simulation
	public void runTimeStep(boolean lastOfDay) { // Runs simulation; lastOfDay == true when it's the last time step of day
		if (!lastOfDay) {
			for (int i = fastestSpeed(); i >= 1; i--) { // Faster creatures act first
				for (Creature c : creatureList) { // Traverse the creatureList for creatures with matching speed
					if (c.getSpeed() == i) {
						c.act(false); // Creatures act
					}
				}
			}
		}
		
		else { // Runs the last time step of the day (when reproduction occurs, but babies are not added yet)
			// ACT, REPRODUCE
			for (int i = fastestSpeed(); i >= 1; i--) {
				for (Creature c : creatureList) {
					if (c.getSpeed() == i)
						newBorns.add(c.act(true)); // Creatures act; Creatures that didn't reproduce return null
				}
			}			
		}
		
		updateBoard(); // Register changes in habitat
		updateSenseBoxes(); // Update everyone's senseBox
	}
	
	private void removeNullBabies() { // Helper method
		for (int i = newBorns.size()-1; i >= 0; i--) {
			if (newBorns.get(i) == null)
				newBorns.remove(i);
		}
	}
	
	// Babies added at start instead of end so that end stats aren't misleading
	public void startDay() { // When reproduction takes effect; Preconditions: emptyCoords is accurate, newBorns doesn't contain null
		
		for (Creature c : creatureList) // Reset field values of creatures for the day; not at end so I can collect data
			c.refreshValues();
		
		// ADD NEW BABIES
		removeNullBabies(); // Remove null (nonexistent) babies
		
		Random RNG = new Random();
		for (int i = 0; i < newBorns.size() && emptyCoords.size() > 0; i++) {
			int randIndex = RNG.nextInt(emptyCoords.size());
			Coordinates randCoords = emptyCoords.get(randIndex);
			newBorns.get(i).setCoords(randCoords);
			emptyCoords.remove(randIndex);
			creatureList.add(newBorns.get(i));
			birthCount++;
		}
		for (int i = newBorns.size()-1; i >= 0; i--)
			newBorns.remove(i); // Reset newBorns list
		
		// RESTOCK FOOD - added offspring can reduce amount of food able to be added
		int maxFoodToAdd = START_FOOD - foodLeft();
		for (int i = 0; i < maxFoodToAdd && emptyCoords.size() > 0; i++) {
			int randIndex = RNG.nextInt(emptyCoords.size());
			Coordinates randCoords = emptyCoords.get(randIndex);
			Food newFood = new Food();
			newFood.setCoords(randCoords);
			emptyCoords.remove(randIndex);
			foodList.add(newFood);
		}
		
		// Not sure if necessary but just in case:
		updateBoard(); // Register changes in habitat
		updateSenseBoxes(); // Updates everyone's senseBox
	}
	public void endDay() { // When death takes effect
		for (Creature c : creatureList) {
			if (c.getFoodEaten() == 0)
				c.die(); // Tags them for removal in updateBoard()
		}
		
		updateBoard(); // Register changes in habitat (removes dead creatures)
	}
	
	// Updating & Information
	
	// EVERY TIME STEP METHOD CALLS THIS METHOD; To be updated every time a creature acts so that dead creatures don't act
	public void updateBoard() { // (includes removeDeadObjects) Updates habitat array to reflect creatures' new coords and food
		removeDeadObjects(); // get that crap outta here
		
		clearCreaturesFromBoard(); // First clear creatures to put them back in their new coords
		
		for (Creature c : creatureList) {
			setObjectAtCoords(c.coords(), c); // Put creatures in their new coords
		}
		for (Food f : foodList) {
			setObjectAtCoords(f.coords(), f); // Put any new food in their new coords
		}
		
		// Update emptyCoords
		ArrayList<Coordinates> newEmptyCoords = new ArrayList<Coordinates>();
		for (Space[] row : habitat) {
			for (Space s : row) {
				if (s.getOccupant() == null)
					newEmptyCoords.add(s.coords());
			}
		}
		emptyCoords = newEmptyCoords;
	}
	private void clearCreaturesFromBoard() { // Helper method
		for (int r = 0; r < habitat.length; r++) {
			for (int c = 0; c < habitat[0].length; c++) {
				if (habitat[r][c].getOccupant() instanceof Creature)
					habitat[r][c].setOccupant(null);
			}
		}
	}
	
	public void removeDeadObjects() { // Replaces dead creatures & eaten food w/ null (restores Space to empty); updates ArrayLists
		for (int r = 0; r < habitat.length; r++) {
			for (int c = 0; c < habitat[0].length; c++) {
				Object occupant = habitat[r][c].getOccupant();
				if (occupant instanceof Creature && ((Creature)occupant).isDead())
					habitat[r][c].setOccupant(null);
				if (occupant instanceof Food && ((Food) occupant).isEaten())
					habitat[r][c].setOccupant(null);
			}
		}
		for (int i = creatureList.size()-1; i >= 0; i--) {
			if (creatureList.get(i).isDead()) {
				deathCount++;
				cannibalismCount += creatureList.get(i).getCannibalismCount();
				creatureList.remove(i);
			}
		}
		for (int i = foodList.size()-1; i >= 0; i--) {
			if (foodList.get(i).isEaten())
				foodList.remove(i);
		}
	}
	public void updateSenseBoxes() { // Precondition: habitat should actually be updated lol
		removeDeadObjects(); // creatureList can contain dead creatures; updating them is pointless
		for (Creature c : creatureList)
			c.genSenseBox(habitat);
	}
	
	public int fastestSpeed() { // Returns the speed value of the fastest creature ALIVE; can be called without removeDeadObjects()
		int fastestSpeed = 1; // Minimum speed value is 1
		for (Creature c : creatureList) {
			if (!c.isDead() && c.getSpeed() > fastestSpeed)
				fastestSpeed = c.getSpeed();
		}
		return fastestSpeed;
	}
	public int foodLeft() { // Returns number of uneaten food remaining; can be called without removeDeadObjects()
		int count = 0;
		for (Space[] row : habitat) {
			for (Space space : row) {
				Object occupant = space.getOccupant();
				if (occupant instanceof Food && !((Food)occupant).isEaten())
					count++;
			}
		}
		return count;
	}
	public int creaturesLeft() { // Returns number of alive creatures remaining; can be called without removeDeadObjects()
		int count = 0;
		for (Space[] row : habitat) {
			for (Space space : row) {
				Object occupant = space.getOccupant();
				if (occupant instanceof Creature && !((Creature)occupant).isDead())
					count++;
			}
		}
		return count;
	}
	public double getAverageSpeed() {
		double sum = 0;
		int count = 0;
		for (Creature c : creatureList) {
			if (!c.isDead()) {
				sum += c.getSpeed();
				count++;
			}
		}
		if (count > 0)
			return sum/count;
		return 0; // No creatures
	}
	public double getAverageSize() {
		double sum = 0;
		int count = 0;
		for (Creature c : creatureList) {
			if (!c.isDead()) {
				sum += c.getSize();
				count++;
			}
		}
		if (count > 0)
			return sum/count;
		return 0; // No creatures
	}
	public int getBirthCount() {
		return birthCount;
	}
	public int getDeathCount() {
		return deathCount;
	}
	public int getCannibalismCount() {
		return cannibalismCount;
	}
	
	// toString Overrider - prints out the grid representation
	public String toString() {
		String result = "";
		for (int r = 0; r < habitat.length; r++) {
			for (int c = 0; c < habitat[0].length; c++) {
				Coordinates coords = new Coordinates(r,c);
				if (objectAtCoords(coords) instanceof Creature)
					result += "X ";
				else if (objectAtCoords(coords) instanceof Food)
					result += "* ";
				else
					result += "- ";
			}
			if (r < habitat.length-1)
				result += "\n";
		}
		return result;
	}
	
}