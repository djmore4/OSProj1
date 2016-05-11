import java.util.concurrent.Semaphore;

/**
 * Adventurer Class - This class represents the code that will be executed by the
 * individual Adventurer threads.
 * @author davidmore
 *
 */
public class Adventurer extends Thread {
	Thread runner;
	String name = "Adventurer";
	public boolean need_assistance = false;
	public static boolean canName = true;
	public int fortuneSize = 0, fightCount = 0, tableNum = -1;
	AdventureGame theAdventure;
	public int possessions[] = new int[4];
	public static int numAdv = 0;
	public static final Semaphore mutex1 = new Semaphore(1, true);
	public final Semaphore namer = new Semaphore(1, true);
	public final int FORTUNE_SIZE = 3, DEFAULT_ADV = 8, NORM_PRIORITY = 4;
	public static long time = System.currentTimeMillis();
	public static int pos = 1;
	
	/**
	 * Default Constructor
	 * @throws InterruptedException
	 */
	public Adventurer(AdventureGame theAdv) throws InterruptedException {
		namer.acquire();
		canName = false;
		name = name + (++numAdv);
		theAdventure = theAdv;
		setName(this.name);
		//mutex1.release();
		runner = new Thread(this, this.name);
		namer.release();
		msg("beginning execution...");
		//testing clerk
		/**possessions[0] = 5;
		possessions[1] = 2;
		possessions[2] = 2;
		possessions[3] = 1;**/
		runner.start();
	}
	
	/**
	 * run method - This is the method that will be called by the start method from the 
	 * constructor.
	 */
	public void run() {
		while(this.fortuneSize < FORTUNE_SIZE) {
			if(this.canMake() != 0) {
				msg("Can I make anything?");
				int pos = canMake();
				String item = "";
				switch(pos) {
				case 1:
					item = "magical necklace";
					break;
				case 2:
					item = "magical ring";
					break;
				case 3:
					item = "pair of magical earrings";
					break;
				}
				msg("I can make a " + item);
				if(pos != 0 && Clerk.numClerks>0) { //if(pos != 0 && Clerk.num_clerk.availablePermit() > 0) {
					try {
						Clerk.numClnt++;
						Clerk.num_clerk.acquire();
						namer.acquire();
						theAdventure.clients[(Clerk.rear)%(DEFAULT_ADV)] = this;
						Clerk.rear = (Clerk.rear+1)%(DEFAULT_ADV);
						//Clerk.num_clnt.release();
						namer.release();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//Clerk.num_clnt.release();
					//Clerk.numClnt--;
					//Clerk.num_clerk.release(); //Clerk.numClerks++;
					msg("I have " + possessions[0] + " jewels, " + possessions[1] + 
							" chains, " + possessions[2] + " rings and " + possessions[3] + " earrings");
				}
				try {
					sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				msg("" + this.fortuneSize);
			}
			if(this.fortuneSize >= FORTUNE_SIZE) {break;}
			while(this.canMake() == 0 && this.fortuneSize<FORTUNE_SIZE) {
				msg("I can't make anything right now, I need to fight the Dragon.");
				try {
					mutex1.acquire();
					Dragon.num_table.acquire();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Dragon.numChal++;
				//theAdventure.challengers[/**Math.abs**/(Dragon.numChal%(DEFAULT_ADV-1))] = this;
				theAdventure.chalTables[AdventureGame.DEFAULT_TBL-(Dragon.num_table.availablePermits()+1)] = this;
				//theAdventure.chalTables[tableNum] = this;
				mutex1.release();
				int k = 0;
				while(!need_assistance) { 	k++;
				if((k%100000000) == 0)
					msg("Waiting... k == " + k); }
				k = 0;
				//Dragon.num_cha.release(); 
				Dragon.numChal--;
				if(this.canMake() != 0)
					break;
			}
			//theAdventure.chalTables[
			//theAdventure.challengers[DEFAULT_ADV-Dragon.numChal] = null; 
		}
		pos++;
		if(pos == DEFAULT_ADV) {
			//Dragon.currentThread().interrupt();
			Dragon.noOneLeft();
			//Clerk.currentThread().interrupt();
			Clerk.noOneLeft();
		}
		try {
			if(pos!=0){
				AdventureGame.advs[pos-1].join();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		msg("I am terminating...");
	}
	
	public void msg(String m) {
		System.out.println("["+(System.currentTimeMillis()-time)+"] "+getName()+": "+m);
	}
	
	public void setPoss(int item) {
		this.possessions[item] += 1;
	}
	
	/**
	 * canMake method - 0 if the adventurer cannot make anything, and otherwise it 
	 * will return the position in the possessions array from which one of the 
	 * secondary items (ring, chain, and earring) can be taken.
	 * @return pos which represents the place in the possessions array in which a magical item can
	 * be made from
	 */
	public int canMake() {
		int pos = 0;
		if(this.possessions[0] >= 1) {
			if(this.possessions[1] >= 1) {
				pos = 1;
			}
			else if(this.possessions[2] >= 1) {
				pos = 2;
			}
			else if(this.possessions[3] >= 2 && this.possessions[0] >= 2) {
				pos = 3;
			}
		}
		return pos;
	}	
}