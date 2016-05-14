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
	private int currentTbl = -1;
	public static final Semaphore namer = new Semaphore(1, true);
	public static final Semaphore num_table = new Semaphore(3, true);
	public static final int DEFAULT_ADV = 8, DEFAULT_TBL = 3;
	public Adventurer challenger;
	public static boolean someoneThere = true;
	public int numDrags = 1;
	public int rollTotal[] = {0, 0, 0};
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
		while(someoneThere) {
			int k = 0;
			while((num_table.availablePermits() == 3) && someoneThere) {
				try {
					sleep(5);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				k++;
				if((k%100000000) == 0)
					msg("Waiting... k == " + k);
			}
			k=0;
			currentTbl = (++currentTbl)%(DEFAULT_TBL);
			challenger = theAdventure.chalTables[currentTbl];
			if(challenger == null) { 
				//msg("There is no one sitting at table " + (currentTbl+1));
				continue;}
			msg("I will fight you " + challenger.getName());
			if(!someoneThere){
				break;
			}
			msg("Current table = " + (currentTbl+1)); //Display the current table number
			fight(currentTbl); //Fight the current adv
			if(challenger.fightCount == 3) { //if the challenger has fought 3 times...
				while(rollTotal[currentTbl] == theAdventure.chalTables[currentTbl].rollSum) {
					fight(currentTbl);
					challenger.fightCount--;
				}
				if(rollTotal[currentTbl] > theAdventure.chalTables[currentTbl].rollSum){ //if the dragon won
					msg("HAHA I beat you " + challenger.name + "!");
					challenger.msg("Darn I lost "+ challenger.fightCount +" times, I guess someone else can try...");
					theAdventure.chalTables[currentTbl] = null;
					challenger.fightCount = 0;
					num_table.release();
					challenger.rollSum = 0;
					challenger.stillInIt = false;
				}
				else{ //if the challenger won...
					msg("AARGH! You have defeated me " + challenger.name);
					int pos = (int) (Math.random()*4); //Determine what prize to give the challenger
					challenger.setPoss(pos); //Give prize to challenger
					msg(challenger.getName() + " has " + challenger.possessions[0] + " jewels, " + challenger.possessions[1] + 
							" chains, " + challenger.possessions[2] + " rings and " + challenger.possessions[3] + " earrings");
					challenger.rollSum = 0; //Reset Challenger for next fight
					challenger.fightCount = 0; 
					theAdventure.chalTables[currentTbl] = null; //Remove challenger from their table
					challenger.stillInIt = false;
					if(challenger.canMake() != 0) {
						challenger.need_assistance = true;
						challenger.msg("I am going to the shop!");
					}
					else
						challenger.msg("I need to get more materials");
					num_table.release(); //Signal a table is open for use
				}
				rollTotal[currentTbl] = 0;
			}
			try {
				sleep(300);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
		rollTotal[currTbl] += (int) (Math.random()*6+1);
		challenger.rollSum += (int) (Math.random()*6+1);
		System.out.println(rollTotal[currTbl]);
		challenger.fightCount++;
		System.out.println(challenger.name + ": Roll Total: " +challenger.rollSum + " Fights: " + challenger.fightCount);
	}
	
	public static void noOneLeft() {
		someoneThere = false;
		System.out.println("Time to go home dragon.");
	}
	
	public static boolean isSomeoneThere() {
		return someoneThere;
	}
}