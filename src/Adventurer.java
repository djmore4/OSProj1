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
	public boolean need_assistance = false, stillInIt = true;
	public int fortuneSize = 0, fightCount = 0, rollSum = 0, number = 0;
	public AdventureGame theAdventure;
	public int possessions[] = new int[4];
	public static int numAdv = 0;
	public int count = 0;
	public static final Semaphore mutex1 = new Semaphore(1, true);
	public final Semaphore namer = new Semaphore(1, true);
	public final int FORTUNE_SIZE = 3, DEFAULT_ADV = 8, NORM_PRIORITY = 4;
	public static long time = System.currentTimeMillis();
	
	/**
	 * Default Constructor
	 * @throws InterruptedException
	 */
	public Adventurer(AdventureGame theAdv) throws InterruptedException {
		namer.acquire();
		name = name + (++numAdv);
		theAdventure = theAdv; //Link this adventurer to the game
		setName(this.name);
		runner = new Thread(this, this.name);
		number = numAdv;
		namer.release();
		msg("beginning execution...");
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
				if(pos != 0) {
					try {
						Clerk.num_clerk.acquire();
						namer.acquire();
						theAdventure.clients[(Clerk.rear)%(DEFAULT_ADV)] = this;
						Clerk.num_clnt.release();
						Clerk.rear = (Clerk.rear+1)%(DEFAULT_ADV);
						namer.release();
						sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					msg("I have " + possessions[0] + " jewels, " + possessions[1] + 
							" chains, " + possessions[2] + " rings and " + possessions[3] + " earrings");
					msg(" I have a fortune of " + this.fortuneSize);
				}
			}
			if(this.fortuneSize >= FORTUNE_SIZE) {break;}
			while(this.canMake() == 0 && this.fortuneSize<FORTUNE_SIZE) {
				msg("I can't make anything right now, I need to fight the Dragon.");
				try {
					mutex1.acquire();
					Dragon.num_table.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				msg("Wants to sit at a table.");
				int i = 0;
				boolean found = false;
				while(i<Dragon.DEFAULT_TBL && !found) {
					if(theAdventure.chalTables[i] == null || theAdventure.chalTables[i].need_assistance == true || theAdventure.chalTables[i].stillInIt == false) {
						found = true;
					}
					else {
						i++;
					}
				}
				msg("Is sitting down at table " + (i+1));
				stillInIt = true;
				theAdventure.chalTables[i] = this;
				mutex1.release();
				int k = 0;
				while(!need_assistance && stillInIt) { 	
					try {
						sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					k++;
					if((k%100000000) == 0)
						msg("Waiting... k == " + k);
				}
				k = 0;
				count++;
				msg("I have fought " + count + " round(s) against the dragon.");
			}
		}
		msg("Let's see if I need to wait for anyone. My number is " + number);
		if((number < DEFAULT_ADV) && AdventureGame.advs[number].isAlive()) {
			msg("I need to wait for " + AdventureGame.advs[number].name);
		}
		try {
			while(number < DEFAULT_ADV && AdventureGame.advs[number].isAlive())
				msg("Waiting for " + AdventureGame.advs[number].getName());
			if(number < DEFAULT_ADV && !AdventureGame.advs[number].isAlive()) {
				msg("I don't need to wait for anyone!" + AdventureGame.advs[number].isAlive());
			}
			while((number < DEFAULT_ADV) && AdventureGame.advs[number].isAlive()){
				AdventureGame.advs[number].join();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(numAdv == 1) {
			Dragon.noOneLeft();
			Clerk.noOneLeft();
		}
		numAdv--;
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