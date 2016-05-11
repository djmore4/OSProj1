
public class AdventureGame {
	
	public static final int DEFAULT_ADV = 8, DEFAULT_CLERK = 2, DEFAULT_DRAG = 1, DEFAULT_TBL = 3;
	public static Adventurer advs[] = new Adventurer[DEFAULT_ADV];
	public static Clerk clerks[] = new Clerk[DEFAULT_CLERK];
	public static Dragon drags[] = new Dragon[DEFAULT_DRAG];
	public Adventurer clients[] = new Adventurer[DEFAULT_ADV];
	public Adventurer chalTables[];
	public Adventurer challengers[];
	
	public AdventureGame() {
		challengers = new Adventurer[DEFAULT_ADV];
		chalTables = new Adventurer[DEFAULT_TBL];
	}
	
	public static void main(String args[]) throws InterruptedException {
		AdventureGame theAdventure = new AdventureGame();
		//start the Clerks
		for(int j=0; j<DEFAULT_CLERK; j++) {
			new Clerk(theAdventure);
		}
		
		//start the Adventurers
		for(int i=0; i < DEFAULT_ADV; i++) {
			advs[i] = new Adventurer(theAdventure);
		}
		
		//start the Dragon
		for(int k=0; k<DEFAULT_DRAG; k++) {
			new Dragon(theAdventure);
			
		}
		
		
		/**while(true) {
			for(int i=0; i<6; i++){
				System.out.println("CHALL " + challengers[i]);
				System.out.println("CLIENT " + clients[i]);
			}
		}**/
		
	}
}