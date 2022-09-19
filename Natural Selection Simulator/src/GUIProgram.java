import java.awt.*;
import java.awt.event.*;

public class GUIProgram extends Frame {
	
	// CONSTANTS
	public static final int NUM_ROWS = Habitat.HABITAT_WIDTH;
	public static final int NUM_COLS = Habitat.HABITAT_LENGTH;
	
	// INSTANCE VARIABLES - COMPONENTS
	private Button btStart, btPause, btGetStats;
	private TextArea[][] gridSpaces;
	
	// CONSTRUCTOR
	public GUIProgram() {
		setLayout(new BorderLayout(3,3));
		addWindowListener(new MyWindowListener());
		
		Panel mainPanel = new Panel(new GridLayout(NUM_ROWS, NUM_COLS));
		Panel controlPanel = new Panel(new FlowLayout(FlowLayout.CENTER,50,5));
		
		// CONTROL PANEL
		Font startFont = new Font("SansSerif", Font.BOLD, 50);
		Font otherFont = new Font("SansSerif", Font.PLAIN, 50);
		
		btStart = new Button("Start");
		btStart.setFont(startFont);
		btStart.addActionListener(new MyActionListener());
		btStart.setPreferredSize(new Dimension(200,75));
		
		btPause = new Button("Pause");
		btPause.setFont(otherFont);
		btPause.addActionListener(new MyActionListener());
		btPause.setPreferredSize(new Dimension(200,75));
		
		btGetStats = new Button("Stats");
		btGetStats.setFont(otherFont);
		btGetStats.addActionListener(new MyActionListener());
		btGetStats.setPreferredSize(new Dimension(200,75));
		
		controlPanel.add(btStart);
		controlPanel.add(btPause);
		controlPanel.add(btGetStats);
		
		// MAIN PANEL
		gridSpaces = new TextArea[NUM_ROWS][NUM_COLS];
		for (int r = 0; r < gridSpaces.length; r++) {
			for (int c = 0; c < gridSpaces[0].length; c++) {
				gridSpaces[r][c] = new TextArea();
				mainPanel.add(gridSpaces[r][c]);
			}
		}
		
		add(mainPanel, BorderLayout.CENTER);
		add(controlPanel, BorderLayout.SOUTH);
		
		setTitle("Natural Selection Simulator");
		setSize(500,500);
		setVisible(true);
	}
	
	// ENTRY MAIN METHOD
	public static void main(String[] args) {
		new GUIProgram();
	}
	
	// WINDOW LISTENER
	private class MyWindowListener extends WindowAdapter {
		public void windowClosing(WindowEvent event) {
			System.exit(0);
		}
	}
	
	// ACTION LISTENER - btStart and btPause
	private class MyActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			Button source = (Button) event.getSource();
			if (source == btStart)
				/* start simulation */;
			else if (source == btPause)
				/* pause simulation */;
		}
	}
	
}