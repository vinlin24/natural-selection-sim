import java.awt.*;
import java.awt.event.*;

public class GUI extends Frame {
	
	public static final int NUM_TIME_STEPS = 5;
	
	private Habitat habitat;
	
	private TextArea dayCount;
	private TextArea timeStepCount;
	
	private TextField setNumDays;
	private TextField setStartCreatures;
	private TextField setStartFood;
	
	private TextArea bigScreen;
	private TextArea realTimeStats;
	private TextArea statsScreen;
	private Button startButton;
	private Button getStatsButton;
	
	// EXPERIMENTAL CONSTANTS
	private int numDays = 1;
	private int startCreatures;
	private int startFood;
	
	public GUI() throws InterruptedException {
		super("Natural Selection Simulator");
		setLayout(new BorderLayout());
		
		addWindowListener(new MyWindowListener());
		
		Panel textFields = new Panel();
		textFields.setLayout(new FlowLayout());
		add(textFields, BorderLayout.NORTH);		
		
		Panel mainScreen = new Panel();
		add(mainScreen, BorderLayout.CENTER);
		
		Panel controlPanel = new Panel();
		controlPanel.setLayout(new BorderLayout());
		add(controlPanel, BorderLayout.EAST);
		
		Panel statsPanel = new Panel();
		add(statsPanel, BorderLayout.SOUTH);
		
		Panel topButton = new Panel();
		controlPanel.add(topButton, BorderLayout.NORTH);
		Panel midButton = new Panel();
		controlPanel.add(midButton, BorderLayout.CENTER);
		Panel botButton = new Panel();
		controlPanel.add(botButton, BorderLayout.SOUTH);
		
		Font countFont = new Font("SansSerif", Font.BOLD, 50);
		
		dayCount = new TextArea(1,7);
		dayCount.setEditable(false);
		dayCount.setFont(countFont);
		
		timeStepCount = new TextArea(1,7);
		timeStepCount.setEditable(false);
		timeStepCount.setFont(countFont);
		
		setNumDays = new TextField(numDays + "", 3);
		setNumDays.setFont(countFont);
		setNumDays.addActionListener(new TextAreaListener());
		
		setStartCreatures = new TextField(startCreatures + "", 3);
		setStartCreatures.setFont(countFont);
		setStartCreatures.addActionListener(new TextAreaListener());
		
		setStartFood = new TextField(startFood + "", 3);
		setStartFood.setFont(countFont);
		setStartFood.addActionListener(new TextAreaListener());
		
		bigScreen = new TextArea(20,40);
		bigScreen.setEditable(false);
		bigScreen.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 30));
		mainScreen.add(bigScreen);	
		
		realTimeStats = new TextArea(5,13);
		realTimeStats.setEditable(false);
		realTimeStats.setFont(new Font("SansSerif", Font.PLAIN, 25));
		
		statsScreen = new TextArea(4,40);
		statsScreen.setEditable(false);
		statsScreen.setFont(new Font("SansSerif", Font.PLAIN, 25));
		
		Font buttonFont = new Font("SansSerif", Font.BOLD, 30);
		
		startButton = new Button("Start");
		startButton.setPreferredSize(new Dimension(100,50));
		startButton.setFont(buttonFont);
		startButton.addActionListener(new ButtonListener());
		
		getStatsButton = new Button("Stats");
		getStatsButton.setPreferredSize(new Dimension(100,50));
		getStatsButton.setFont(buttonFont);
		getStatsButton.addActionListener(new ButtonListener());
		
		textFields.add(dayCount);
		textFields.add(timeStepCount);
		textFields.add(setNumDays);
		textFields.add(setStartCreatures);
		textFields.add(setStartFood);
		topButton.add(startButton);
		midButton.add(realTimeStats);
		botButton.add(getStatsButton);
		statsPanel.add(statsScreen);
		
		setSize(1000,1000);
		setVisible(true);
	}
	
	public void runProgram() throws InterruptedException {
		habitat = new Habitat(startCreatures, startFood);
		
		for (int day = 1; day <= numDays; day++) {
			dayCount.setText("DAY " + day);
			
			timeStepCount.setText("DAWN");
			bigScreen.setText(MainProgram.startDay(habitat));
			realTimeStats.setText(printText(habitat));
			MainProgram.longDelayProgram();
			
			for (int timeStep = 1; timeStep <= NUM_TIME_STEPS; timeStep++) {
				timeStepCount.setText("STEP " + timeStep);
				if (timeStep == NUM_TIME_STEPS) {
					bigScreen.setText(MainProgram.runTimeStep(habitat, true));
					realTimeStats.setText(printText(habitat));
				}
				else {
					bigScreen.setText(MainProgram.runTimeStep(habitat, false));
					realTimeStats.setText(printText(habitat));
				}
			}
			
			timeStepCount.setText("DUSK");
			bigScreen.setText(MainProgram.endDay(habitat));
			realTimeStats.setText(printText(habitat));
			MainProgram.longDelayProgram();
		}
	}
	
	public String printText(Habitat habitat) {
		int numCreatures = habitat.creaturesLeft();
		int numFood = habitat.foodLeft();
		int birthCount = habitat.getBirthCount();
		int deathCount = habitat.getDeathCount();
		int eatenCount = habitat.getCannibalismCount();
		return "Creatures: " + numCreatures +
				"\nFood: " + numFood +
				"\nBIRTHED: " + birthCount +
				"\nDIED: " + deathCount +
				"\nEATEN: " + eatenCount;
	}
	
	public String printStats() {
		double avgSpeed = habitat.getAverageSpeed();
		double avgSize = habitat.getAverageSize();
		return "Start SPEED: 4\nStart Size: 5" +
				"\nAverage SPEED: " + avgSpeed +
				"\nAverage SIZE: " + avgSize;
	}
	
	public static void main(String[] args) throws InterruptedException {
		new GUI();
	}
	
	private class MyWindowListener extends WindowAdapter {
		public void windowClosing(WindowEvent event) {
			System.exit(0);
		}
	}
	
	private class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			Button source = (Button) event.getSource();
			if (source == startButton) {
				try {
					runProgram();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			else if (source == getStatsButton) {
				statsScreen.setText(printStats());
			}
		}
	}
	
	private class TextAreaListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			TextField source = (TextField) event.getSource();
			if (source == setNumDays) {
				numDays = Integer.parseInt(setNumDays.getText());
				System.out.println(numDays);
			}
			else if (source == setStartCreatures) {
				startCreatures = Integer.parseInt(setStartCreatures.getText());
				System.out.println(startCreatures);
			}
			else if (source == setStartFood) {
				startFood = Integer.parseInt(setStartFood.getText());
				System.out.println(startFood);
			}
		}
	}
	
}