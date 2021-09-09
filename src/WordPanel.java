import java.awt.Color;
import java.awt.Graphics;
import java.awt.Font;
import javax.swing.JPanel;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WordPanel extends JPanel implements Runnable //This class mostly acts as the controller
{
		/**
     	* Global Variables
     	*/	
		public static volatile boolean done;
		static WordRecord[] words;
		private int noWords;
		private int maxY;
		private int count = 0;
		private AtomicInteger fallenWords;

		
		public void paintComponent(Graphics g) 
		{
		    int width = getWidth();
		    int height = getHeight();
		    g.clearRect(0,0,width,height);
		    g.setColor(Color.red);
		    g.fillRect(0,maxY-10,width,height);

		    g.setColor(Color.black);
		    g.setFont(new Font("Helvetica", Font.PLAIN, 26));
		    //draw the words
		    //animation must be added 
		    for (int i=0;i<noWords;i++)
			{	    	
		    	//g.drawString(words[i].getWord(),words[i].getX(),words[i].getY());	
		    	g.drawString(words[i].getWord(),words[i].getX(),words[i].getY()+20);  //y-offset for skeleton so that you can see the words	
		    }
		   
		  }
		
		 /**
     * Parameterised Constructor
     *
     * @param words
     * @param maxY
     */
    WordPanel(WordRecord[] words, int maxY) 
	{
        this.words = words; 
        noWords = words.length;
        done = false;
        this.maxY = maxY;
		fallenWords = new AtomicInteger(0);
    }

	public void setDone()
    {
        done = true;
    }
                
    public void undone()
    {
        done = false;
    }
                
    public boolean isDone()
    {
    	return done;
    }
                
    public int getDropped()
    {
        return fallenWords.get();
    }
                
    public void resetDropped()
    {
        fallenWords.set(0);
    }
		


    /**
     * Run method
     */
    @Override
    public void run()
    {
        undone(); //This is an alert variable to tell that the thread is currently running
        Thread[] threads = new Thread[noWords]; //initializing thread array
    	for (int i = 0; i < noWords; i++) //Iterating through the words 
        {
            final WordRecord currentWord = words[i]; //fetches a word to be dropped onto the screen
            Runnable dropWord = new Runnable() //This thread is responsible for handling the dropping of the word
            {
                public void run()
                {
                    while (!done) //While game runs
                    {
                        try
                    	{
                    		Thread.sleep(100); //Temporary pause for other threads to run
                        } 
						catch (InterruptedException ex)
                        {
                            Logger.getLogger(WordPanel.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        currentWord.drop(currentWord.getSpeed()); //Drop a bit based on words fall speed
                        if (currentWord.dropped()) //checking to see if it hits the bottom
                        {                                       
                            fallenWords.incrementAndGet(); //Increase drop count here 
                            currentWord.resetWord(); //Get a new word
                        }
                    }
                } //This will continually loop for a given word ie. given position in the array until game ends
            };
            
			threads[i] = new Thread(dropWord); //Set the rules for this thread
            threads[i].start(); //Start it up
        }
                        
		while (!done)
        {
            repaint(); //Keep animating until game is considered "over"
        } 

		resetDropped(); //Clear out dropped value

		for (int j = 0; j < noWords; j++)
        { 
            words[j].resetWord(); //Manually resets each word since the threads have all stopped
        }
        repaint(); //Reset GUI to default state
                       
    }
}

