/**
 * @author Dewan Choudhury
 */

//coordinator class
public class Coordinator extends Thread{
	private String name;
	private int id;
	public boolean done = false;
	private static long time = System.currentTimeMillis();
	private CommuteToCamp commuteToCamp;
	public Coordinator(int id, CommuteToCamp ctc) {
		setCName("Coordinator-"+id);
		this.id = id;
		commuteToCamp = ctc;
		new Thread(this).start();
	}
	public void run(){
		commuteToCamp.coordinators.add(this);
		while(!done){
			commuteToCamp.attendToStudent(this);
			if(done){break;}
			try {
				sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(done){break;}
			msg(" is waiting for students to arrive.");
		}
		msg(" is done. Terminated!");
	}
	
	public void msg(String m){
		System.out.println("["+(System.currentTimeMillis()-time+"]"+getCName()+":"+m));
	}
	public String getCName() {
		return name;
	}
	public void setCName(String name) {
		this.name = name;
	}
}
