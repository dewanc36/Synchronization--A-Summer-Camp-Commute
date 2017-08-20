/**
 * @author Dewan Choudhury
 */

//Student class
public class Student extends Thread{
	public boolean byBus = false;
	private String name;
	private int id;
	private CommuteToCamp ctc;
	private static long time = System.currentTimeMillis();
	
	public Student(int id, CommuteToCamp bs) {
		//random number between 0 and 1 is used to determine if the 
		//student will commute via bus of car
		double r = getRandom();
		if(r>=0.5){
			byBus = true;
		}
		this.id = id;
		setSName("Student-"+id);
		ctc = bs;
		new Thread(this).start();
	}
	public void run(){
		if(byBus){
			msg(" has arrived at the bus stop.");
			ctc.board(this);
			msg(" is on bus, going to school.");
			synchronized (ctc.waitingForTripToFinish) {
				try {
					ctc.waitingForTripToFinish.addElement(this);
					ctc.waitingForTripToFinish.wait();
					ctc.waitingForTripToFinish.remove(this);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}else{
			msg(" is being driven to the school.");
			try {
				sleep(ctc.randNum(5000, 10000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			ctc.getOffCar(this);
		}
		
		msg(" is walking to the gymnasium.");
		try {
			sleep(ctc.randNum(2000, 2500));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		msg(" has arrived at the gymnasium! Waiting for the camp to start.");
		ctc.gymnasiumCount.addElement(this);
	}
	
	public void msg(String m){
		System.out.println("["+(System.currentTimeMillis()-time+"]"+getSName()+":"+m));
	}
	public String getSName() {
		return name;
	}
	public void setSName(String name) {
		this.name = name;
	}
	public double getRandom(){
		return Math.random();
	}
}
