package graphicsAndInput;

import javax.swing.*;
import javax.swing.UIManager.*;

import environment.*;

public class TestRunLevel {
	
    public static void main(String[] args) {
			
		final GameLevel TEST_LEVEL = new TestLevel();
		
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
    	        JFrame frame = new JFrame("FLoaPT Engine by Michael Zhan");
    	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	        frame.setResizable(false);
    	        frame.setLayout(null);
    	        
    	        //Create and set up game environment
    	        GameEnvironment environ = new GameEnvironment();
    	        environ.setLevel(TEST_LEVEL);
    	        
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
