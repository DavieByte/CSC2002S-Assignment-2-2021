import java.util.concurrent.atomic.AtomicInteger;
public class Score 
{
	private AtomicInteger missedWords;
	private AtomicInteger caughtWords;
	private AtomicInteger gameScore;
	private AtomicInteger wrongWords;
	
	Score() 
	{
		missedWords = new AtomicInteger(0);
		caughtWords = new AtomicInteger(0);
		gameScore = new AtomicInteger(0);
		wrongWords = new AtomicInteger(0);
	}
		
	// all getters and setters must be synchronized
	
	public synchronized int getMissed() 
	{
		return missedWords.get();
	}

	public synchronized  int getCaught() 
	{
		return caughtWords.get();
	}
	
	public synchronized int getTotal() 
	{
		return missedWords.addAndGet(caughtWords.get());
	}

	public synchronized int getScore() 
	{
		return gameScore.get();
	}
	
	public synchronized  void missedWord() 
	{
		missedWords.incrementAndGet();
	}

	public synchronized void caughtWord(int length) 
	{
		caughtWords.getAndIncrement();
		gameScore.addAndGet(length);
	}

	public synchronized void incrWrongWord()
	{
		wrongWords.addAndGet();
	}

	public synchronized void resetScore() 
	{
		caughtWords.set(0);
		missedWords.set(0);
		gameScore.set(0);
		wrongWords.set(0);
	}
}
