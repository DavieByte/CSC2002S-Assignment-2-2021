import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;


import java.util.Scanner;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
//model is separate from the view.

public class WordApp 
{
//shared variables
	static int noWords=4;
	static int totalWords;

   	static int frameX=1000;
	static int frameY=600;
	static int yLimit=480;

	static WordDictionary dict = new WordDictionary(); //use default dictionary, to read from file eventually

	static WordRecord[] words;
	static volatile boolean done;  //must be volatile
	static 	Score score = new Score();

	static WordPanel w;
	
	public static void setupGUI(int frameX,int frameY,int yLimit) 
	{

		// Frame init and dimensions
    	JFrame frame = new JFrame("WordGame"); 
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(frameX, frameY);
        JPanel g = new JPanel();
        g.setLayout(new BoxLayout(g, BoxLayout.PAGE_AXIS)); 
        g.setSize(frameX,frameY);
    	
		w = new WordPanel(words,yLimit);
		w.setSize(frameX,yLimit+100);
	    g.add(w); 
	    
        JPanel txt = new JPanel();
        txt.setLayout(new BoxLayout(txt, BoxLayout.LINE_AXIS)); 
        final JLabel caught = new JLabel("Caught: " + score.getCaught() + "    ");
        final JLabel missed = new JLabel("Missed:" + score.getMissed()+ "    ");
        final JLabel scr = new JLabel("Score:" + score.getScore()+ "    ");    
        txt.add(caught);
	    txt.add(missed);
	    txt.add(scr);
		w.setDone();
  
	   // setting textfield for the user to enter words into
	   final JTextField textEntry = new JTextField("",20);
	   textEntry.addActionListener(new ActionListener()
	   {
			public void actionPerformed(ActionEvent evt) 
			{
				String text = textEntry.getText();
				// To Do: Add handler for catching word
				for (int i = 0; i < words.length; i++)
				{
					if(words[i].matchWord(text)) //If we have a match somewhere
					{
						score.caughtWord(text.length()); //Add score value
						if (score.getTotal() == 15)
						{
							break; //break out once max score has been reached
						}
						words[i].resetWord(); //Reset it for a new word     
					}
				}
				caught.setText("Caught: " + score.getCaught() + "    ");
				missed.setText("Missed:" + score.getMissed()+ "    ");
				scr.setText("Score:" + score.getScore()+ "    ");
		
				textEntry.setText("");
				textEntry.requestFocus();
			}
		});
	   
	    txt.add(textEntry);
	    txt.setMaximumSize( txt.getPreferredSize() );
	    g.add(txt);
	    
	    JPanel b = new JPanel();
        b.setLayout(new BoxLayout(b, BoxLayout.LINE_AXIS)); 
		// starts the game which starts the main thread
		JButton startB = new JButton("Start");;
	    startB.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
                if (w.isDone()) //If this is set to not be busy
                {
                    Thread thread = new Thread(w); //Make a new thread
                    thread.start();
                } 
                else
				{
					System.out.println("Game in progress."); //If already running
				} 
                
				textEntry.requestFocus();  //return focus to the text entry field
			}
		});

		JButton resetB = new JButton("Reset");
			
            // add the listener to the jbutton to handle the "pressed" event
            resetB.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    w.setDone(); //Will make the thread end and reset panel
                    score.resetScore(); //Reset game scores and stuff
                }
            });
		
		JButton quitB = new JButton("Quit");
		quitB.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.out.println("Quit Successful");
				System.exit(0);
			}
		});

		b.add(resetB);
		b.add(startB);
		b.add(quitB);
        g.add(b);
    	
        frame.setLocationRelativeTo(null);  // Center window on screen.
        frame.add(g); //add contents to window
        frame.setContentPane(g);     
       	//frame.pack();  // don't do this - packs it into small space
        frame.setVisible(true);

		Runnable scorer = new Runnable() //Initializing a thread for tracking score and updating
		{
			public void run()
			{
				while(true) 
				{
					score.setMissed(w.getDropped()); //Obtained miss-word count and updates scores through interupts
					caught.setText("Caught: " + score.getCaught() + "    "); //Update GUI
					missed.setText("Missed:" + score.getMissed()+ "    ");
					scr.setText("Score:" + score.getScore()+ "    ");
				
				
					if (score.getTotal() >= totalWords) //when the game is done
					{
						w.setDone();
						System.out.println("Total score: " + score.getScore() +"\nCaught: " + score.getCaught() + "\nMissed: " + score.getMissed());
						score.resetScore();
					}
				}
			}
		};

		Thread ts = new Thread(scorer);
		ts.start(); //Get this thread going
	}
	

	
	//reads in words from the text file to populate word dictionary
   public static String[] getDictFromFile(String filename) {
		String [] dictStr = null;
		try {
			Scanner dictReader = new Scanner(new FileInputStream(filename));
			int dictLength = dictReader.nextInt();
			//System.out.println("read '" + dictLength+"'");

			dictStr=new String[dictLength];
			for (int i=0;i<dictLength;i++) {
				dictStr[i]=new String(dictReader.next());
				//System.out.println(i+ " read '" + dictStr[i]+"'"); //for checking
			}
			dictReader.close();
		} catch (IOException e) {
	        System.err.println("Problem reading file " + filename + " default dictionary will be used");
	    }
		return dictStr;
	}

	
	
	public static void main(String[] args) 
	{
    	
		//deal with command line arguments
		totalWords=Integer.parseInt(args[0]);  //total words to fall
		noWords=Integer.parseInt(args[1]); // total words falling at any point
		assert(totalWords>=noWords); // this could be done more neatly
		String[] tmpDict=getDictFromFile(args[2]); //file of words
		if (tmpDict!=null)
			dict = new WordDictionary(tmpDict);
		
		WordRecord.dict=dict; //set the class dictionary for the words.
		
		words = new WordRecord[noWords];  //shared array of current words
		
		setupGUI(frameX, frameY, yLimit);  
    	//Start WordPanel thread - for redrawing animation

		int x_inc=(int)frameX/noWords;
	  	//initialize shared array of current words

		for (int i=0;i<noWords;i++) {
			words[i]=new WordRecord(dict.getNewWord(),i*x_inc,yLimit);
		}
	}
}