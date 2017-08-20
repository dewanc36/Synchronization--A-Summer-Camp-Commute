/**
 * @author Dewan Choudhury
 */
//Bus class
public class Bus extends Thread{
	private int cap;
	private String name;
	private int id;
	private CommuteToCamp ctc;
	private static long time = System.currentTimeMillis();
	boolean done = false;
	
	public Bus(int id, int bc, CommuteToCamp bs) {
		cap = bc;
		this.id = id;
		setBName("Bus-"+id);
		ctc = bs;
		this.start();
	}
	public void run(){
		while(!done){//loops until signaled 
			msg(" is at the bus stop.");
			ctc.startTrip(this);
			if(!done){
				ctc.busDeparture(this);
			}
		}
		msg(" is done for today. Terminated!");
	}
	
	public void msg(String m){
		System.out.println("["+(System.currentTimeMillis()-time+"]"+getBName()+":"+m));
	}
	public String getBName() {
		return name;
	}
	public void setBName(String name) {
		this.name = name;
	}
	public int getCap(){
		return cap;
	}
}
