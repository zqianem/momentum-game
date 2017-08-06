package graphicsAndInput;

import javax.swing.*;
import javax.swing.UIManager.*;

import TerminalIO.KeyboardReader;
import environment.*;

public class RunGame {
	
	

    public static void main(String[] args) {
    	
		KeyboardReader reader = new KeyboardReader();
		int menuInput;
		
		System.out.println("Only controls are WASD (or IJKL and arrow keys if multiple players");
		System.out.println("Move by shooting in the opposite direction");
		
		menu:
		while(true){
			System.out.println("_________________________________________________");
			System.out.print("Welcome (back) to the Momentum Game Menu! \n" +
							 "\n" +
						     "Please input one of the following: \n" +
							 "0 to load single-player (bit laggy)\n" +
						     "1 to load two-player co-op (also bit laggy)\n" +
						     "2 to load two-player vs (actuallly runs well)\n" +
						     "3 to load three-player vs \n" +
						     "-1 to exit                                "); 
			menuInput = reader.readInt("Input: ");
			System.out.println();
			
			GameLevel myLevel = null;
			
			switch(menuInput){
			case 0:
				myLevel = new TestLevel();
				break;
			case 1:
				myLevel = new TwoPlayerCoop();
				break;
			case 2:
				myLevel = new TwoPlayerVS();
				break;
			case 3:
				myLevel = new ThreePlayerVS();
				break;
			default:
				System.out.println("Not a valid input");
				continue menu;
			case -1:
				System.out.println("Goodbye Dave");
				break menu;
			}
			
			final GameLevel myLevel2 = myLevel;
			
			
			java.awt.EventQueue.invokeLater(new Runnable() {
	    	    public void run() {

					try {
					    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
					        if ("Nimbus".equals(info.getName())) {
					            UIManager.setLookAndFeel(info.getClassName());
					            break;
					        }
					    }
					} catch (Exception e) {
					    // If Nimbus is not available, you can set the GUI to another look and feel.
					}
	    	    }
	        	} );
	    	
	    	
	    	
	    	java.awt.EventQueue.invokeLater(new Runnable() {
	    	    public void run() {
	    	    	 //Create and set up the window.
	    	        JFrame frame = new JFrame("Momentum Game by Michael Zhan (For Lack of a Better Title)");
	    	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    	        frame.setResizable(false);
	    	        frame.setLayout(null);
	    	        
	    	        //Create and set up game environment
	    	        GameEnvironment environ = new GameEnvironment();
	    	        environ.setLevel(myLevel2);
	    	        
	    	        //Create and set up the content pane.
	    	        MainGraphics newContentPane = new MainGraphics(environ);
	    	        frame.setContentPane(newContentPane);

	    	        //Display the window.
	    	        frame.pack();
	    	        frame.setLocationRelativeTo(null); // centers JFrame on screen
	    	        frame.setVisible(true);
	    	        
	    	    }
	    	} );
		}
    	
    	
    }
}
