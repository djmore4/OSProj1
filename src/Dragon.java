import java.util.concurrent.Semaphore;


/**
 * Dragon Class - 
 * @author davidmore
 *
 */
public class Dragon extends Thread {
	private Thread runner;
	public String name = "Dragon";
	AdventureGame theAdventure;
	public static final Semaphore num_cha = new Semaphore(0, false);
	public static int numChal = 0;
	private int currentTbl = -1;
	public static final Semaphore mutex3 = new Semaphore(1, true);
	public static final Semaphore namer = new Semaphore(1, true);
	public static final Semaphore num_table = new Semaphore(3, true);
	public static final int DEFAULT_ADV = 8, DEFAULT_TBL = 3;
	public Adventurer challenger;
	public final int NORM_PRIORITY = 4;
	public static boolean someoneThere = true;
	public static boolean iWon = false;
	public int numDrags = 1;
	public Integer rollTotal[] = new Integer[3];
	public static long time = System.currentTimeMillis();
	
	/**
	 * Default Constructor - 
	 * @throws InterruptedException 
	 */
	public Dragon(AdventureGame theAdv) throws InterruptedException {
		namer.acquire();
		this.name = name + numDrags;
		theAdventure = theAdv;
		setName(name);
		runner = new Thread(this, name);
		msg("beginning execution...");
		namer.release();
		runner.start();
	}
	
	public void msg(String m) {
		System.out.println("["+(System.currentTimeMillis()-time)+"] "+getName()+": "+m);
	}
	
	/**
	 * run method - 
	 */
	public void run() {
		//boolean foundOne;
		while(someoneThere) {
			//foundOne = false;
			int k = 0;
			while((num_table.availablePermits() == 3) && someoneThere) {
				k++;
				if((k%100000000) == 0)
					msg("Waiting... k == " + k);
			}
			k=0;
			//int i = 0;
			currentTbl = (++currentTbl)%(DEFAULT_TBL-1);
			challenger = theAdventure.chalTables[currentTbl];
			
			if(challenger == null) { continue;}
			/**if(iWon && someoneThere) {
				msg("I will fight you again challenger!");
				while(i<(numChal-1) && !foundOne){
					if(theAdventure.challengers[i].getPriority() > NORM_PRIORITY){
						challenger = theAdventure.challengers[i];
						foundOne = true;
						msg("I will fight " + challenger.name);
					}
					i++;
				}
			}
			else if (!iWon && someoneThere){
				int whichChal = (int) (Math.random()*DEFAULT_ADV);
				while(theAdventure.challengers[whichChal] == null || theAdventure.challengers[whichChal].need_assistance){
					whichChal = (int) (Math.random()*DEFAULT_ADV);
				}
				challenger = theAdventure.challengers[whichChal];
			}**/
			msg("I will fight you " + challenger.getName());
			if(!someoneThere)
				break;
			fight(currentTbl);
			if(challenger.fightCount == 3) {
				if(rollTotal[currentTbl] > theAdventure.chalTables[currentTbl].rollSum){
					msg("HAHA I have won!");
					challenger.msg("Darn I lost "+ challenger.fightCount +" times, I guess someone else can try...");
					challenger.fightCount = 0;
					for(int l=currentTbl; l<DEFAULT_TBL-1; l++) {
					 	theAdventure.chalTables[currentTbl] = theAdventure.chalTables[currentTbl+1];
					}
					num_table.release();
				}
				else{
					msg("AARGH! You have defeated me " + challenger.name);
					int pos = (int) (Math.random()*4);
					challenger.setPoss(pos);
					msg(challenger.getName() + " has " + challenger.possessions[0] + " jewels, " + challenger.possessions[1] + 
							" chains, " + challenger.possessions[2] + " rings and " + challenger.possessions[3] + " earrings");
					if(challenger.canMake() != 0) {
						challenger.need_assistance = true;
						challenger.msg("I am going to the shop!");
						for(int h=currentTbl; h<DEFAULT_TBL-1; h++) {
							theAdventure.chalTables[currentTbl] = theAdventure.chalTables[currentTbl+1];
						}
					}
				}
				
				 
			}	
		}
		msg("I am terminating...");
		
	}
	
	
	/**
	 * fight method - randomly generates an integer value between 1 and 6 for
	 * both the dragon and the adventurer and returns if the dragon wins or not.
	 * @return If the dragon wins the roll.
	 */
	public void fight(int currTbl) {
		rollTotal[currTbl] += (int) (Math.random()*6);
		challenger.rollSum += (int) (Math.random()*6);
		challenger.fightCount++;
	}
	
	public static void noOneLeft() {
		someoneThere = false;
		System.out.println("Time to go home dragon.");
	}
	
	public static boolean isSomeoneThere() {
		return someoneThere;
	}
}