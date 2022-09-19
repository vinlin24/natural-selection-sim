/*
 * Natural Selection Simulator by Vincent Lin
 * Project started 5/19/2019
 * Inspired by Youtube Creator Primer
 */

import java.util.ArrayList;
import java.util.Arrays;

public class MainProgram {
	
	public static final int STEPS_PER_DAY = 8; // Time steps per day, excludes startDay() and endDay() as they aren't time steps
	public static final int TOTAL_DAYS = 20;
	public static final int DELAY = 50;
	
	public static void main(String[] args) throws InterruptedException {
		
//		Habitat habitat = new Habitat();
//		
//		for (int day = 1; day <= TOTAL_DAYS; day++) {
//			System.out.println("DAY " + day);
//			delayProgram();
//			startDay(habitat);
//			
//			for (int step = 1; step <= STEPS_PER_DAY; step++) {
//				if (step == STEPS_PER_DAY)
//					runTimeStep(habitat, true);
//				else
//					runTimeStep(habitat, false);
//			}
//			
//			endDay(habitat);
//		}
//		
//		for (int i = 0; i < habitat.getCreatureList().size(); i++) {
//			System.out.print(habitat.getCreatureList().get(i) + " ");
//		}
//		System.out.println("\nAVG SPD: " + habitat.getAverageSpeed());
//		System.out.println("AVG SIZ: " + habitat.getAverageSize());
//		System.out.println("BIRTHED: " + habitat.getBirthCount());
//		System.out.println("DIED: " + habitat.getDeathCount());
//		System.out.println("EATEN: " + habitat.getCannibalismCount());
	}
	
	public static String runTimeStep(Habitat habitat, boolean lastOfDay) throws InterruptedException {
		habitat.runTimeStep(lastOfDay);
		System.out.println(habitat);
		System.out.println(habitat.creaturesLeft() + " creatures left. " + habitat.foodLeft() + " food left\n");
		delayProgram();
		return habitat.toString();
	}
	
	public static String startDay(Habitat habitat) throws InterruptedException {
		habitat.startDay();
		System.out.println("DAWN:\n" + habitat);
		System.out.println(habitat.creaturesLeft() + " creatures left. " + habitat.foodLeft() + " food left\n");
		return habitat.toString();
	}
	
	public static String endDay(Habitat habitat) throws InterruptedException {
		habitat.endDay();
		System.out.println("DUSK:\n" + habitat);
		System.out.println(habitat.creaturesLeft() + " creatures left. " + habitat.foodLeft() + " food left\n");
		return habitat.toString();
	}	
	
	public static void delayProgram() throws InterruptedException {
		Thread.sleep(DELAY);
	}
	public static void longDelayProgram() throws InterruptedException {
		Thread.sleep(20*DELAY);
	}
	
}