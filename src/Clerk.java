import java.util.concurrent.Semaphore;

/**
 * Clerk class - 
 * @author davidmore
 *
 */
public class Clerk extends Thread {
	public Thread runner;
	public static AdventureGame theAdventure;
	public String name = "Clerk";
	public final static Semaphore num_clerk = new Semaphore(2, true);
	public static int numClerks = 1;
	public final static Semaphore num_clnt = new Semaphore(0, true);
	public final Semaphore namer = new Semaphore(1, true);
	public final Semaphore mutex2 = new Semaphore(1, true);
	public Adventurer client;
	public static int front = 0;
	public static int rear = 0;
	public final static int DEFAULT_ADV = 8;
	public static boolean someoneThere = true, canName = true, canPickClnt = true;
	public static long time = System.currentTimeMillis();
	
	
	/**
	 * Default Constructor - creates a new clerk and names them appropriately
	 * Also prints that the thread is beginning execution.
	 * @throws InterruptedException
	 */
	public Clerk(AdventureGame theAdv) throws InterruptedException {
		namer.acquire();
		name = name + numClerks;
		numClerks++;
		setName(this.name);
		theAdventure = theAdv;
		runner = new Thread(this, this.name);
		namer.release();
		msg("beginning execution...");
		runner.start();
	}
	
	/**
	 * run method - This is the method that will be executed by the JVM when the 
	 * start method is called from the constructor.
	 */
	public void run() {
		while(someoneThere) {
			while(num_clnt.availablePermits() == 0 && someoneThere) { 
				try {
					sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if(!someoneThere) break;
			try {
				num_clnt.acquire();
				mutex2.acquire();
				client = theAdventure.clients[front];
				forge();
				theAdventure.clients[front].need_assistance = false;
				front = (front + 1)%(DEFAULT_ADV);
				mutex2.release();
				sleep(5);
				num_clerk.release();
			} catch (InterruptedException e) {
				//e.printStackTrace();
			}
			try {
				sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		msg("I am terminating...");
	}

	/**
	 * forge method - Takes two usable materials from the current adventurer and 
	 * create a "Magical Item" to give back to them.
	 */
	public void forge() {
		Adventurer theAdv = theAdventure.clients[front];
		boolean stop = false;
		while(theAdv == null || theAdv.need_assistance==false) {
			front = (front+1)%(DEFAULT_ADV);
			theAdv = theAdventure.clients[front];
			if(!someoneThere) {
				stop = true;
				break;
			}
		}
		if(stop == false) {
			int item = theAdv.canMake();
			switch(item) {
			case 1:
				theAdv.possessions[1]--;
				theAdv.possessions[0]--;
				theAdv.fortuneSize += 1;
				break;
			case 2:
				theAdv.possessions[2]--;
				theAdv.possessions[0]--;
				theAdv.fortuneSize += 1;
				break;
			case 3:
				theAdv.possessions[3] = theAdv.possessions[3] - 2;
				theAdv.possessions[0] = theAdv.possessions[0] - 2;
				theAdv.fortuneSize += 1;
				break;
			}
			if(item != 0) {
				String theItem = "";
				switch(item) {
				case 1:
					theItem = "magical necklace";
					break;
				case 2:
					theItem = "magical ring";
					break;
				case 3:
					theItem = "pair of magical earrings";
					break;
				}
				msg("Making a " + theItem);
				msg("I made " + theItem + " for " + theAdv.getName());
			}
		}
	}
	
	public void msg(String m) {
		System.out.println("["+(System.currentTimeMillis()-time)+"] "+getName()+": "+m);
	}
	
	/**
	 * noOneLeft method - This method will be called by an adventurer if they are the 
	 * last remaining adventurer alive.
	 */
	public static void noOneLeft() {
		someoneThere = false;
		System.out.println("Time to go home clerks.");
	}
}