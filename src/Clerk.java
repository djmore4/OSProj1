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
	public static int numClnt = 0;
	public final static Semaphore num_clnt = new Semaphore(0, true);
	public final Semaphore namer = new Semaphore(1, true);
	public final Semaphore mutex2 = new Semaphore(1, true);
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
		while(!canName) { }
		canName = false;
		name = name + numClerks;
		numClerks++;
		//namer.release();
		setName(this.name);
		canName = true;
		theAdventure = theAdv;
		runner = new Thread(this, this.name);
		msg("beginning execution...");
		runner.start();
	}
	
	/**
	 * run method - This is the method that will be executed by the JVM when the 
	 * start method is called from the constructor.
	 */
	public void run() {
		/**System.out.println("Press any key to continue...");
		try {
			int cont = System.in.read();
			while(cont == -1) {
				cont = System.in.read();
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}**/
		while(someoneThere) {
			int k = 0;
			while(numClnt == 0 && someoneThere) { 
				k++;
			if((k%100000000) == 0)
				msg("Waiting... k == " + k); }
			k = 0;
			if(!someoneThere) break;
			try {
				numClnt--;
				mutex2.acquire();
				//client = theAdventure.clients[front];
				/**while(client == null) {
				 	client = theAdventure.clients[(++front)%(DEFAULT_ADV-1)];
				}**/
				int pos = forge();
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
				msg("I made a " + item + " for " + theAdventure.clients[front].getName());
				theAdventure.clients[front].need_assistance = false;
				front = (front + 1)%(DEFAULT_ADV-1);
				mutex2.release(); //canPickClnt = false;
				num_clerk.release();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			//num_clnt.release();
			try {
				sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		msg("I am terminating...");
	}

	/**
	 * forge method - Takes two usable materials from the current adventurer and 
	 * create a "Magical Item" to give back to them.
	 */
	public int forge() {
		Adventurer theAdv = theAdventure.clients[front];
		while(theAdv == null) {
			front = (front+1)%(DEFAULT_ADV-1);
		}
		int item = theAdventure.clients[front].canMake();
		switch(item) {
		case 1:
			theAdv.possessions[1]--;
			theAdv.possessions[0]--;
			break;
		case 2:
			theAdv.possessions[2]--;
			theAdv.possessions[0]--;
			break;
		case 3:
			theAdv.possessions[3] = theAdv.possessions[3] - 2;
			theAdv.possessions[0] = theAdv.possessions[0] - 2;
			break;
		}
		theAdv.fortuneSize += 1;
		return item;
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