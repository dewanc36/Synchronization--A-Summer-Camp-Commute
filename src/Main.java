/**
 * 
 * @author Dewan Choudhury
 * CS344 Project1
 */

public class Main {
	//default parameters
	static int s = 20;//num of students
	static int c = 3;//num of coordinators
	static int bc = 4;//cap of the buses
	public static void main(String[] args) {
		int l = args.length;
		//optional parameters passed by user during runtime 
		//in the order (studentNum, coordinatorNum, busCap)
		if(l==3){
			s = Integer.parseInt(args[0]);
			c = Integer.parseInt(args[1]);
			bc = Integer.parseInt(args[2]);
		}
		CommuteToCamp commuteToCamp = new CommuteToCamp(s,4);//monitor class
		
		//bus threads started
		Bus b1 = new Bus(1, bc,commuteToCamp);
		Bus b2 = new Bus(2, bc,commuteToCamp);
		
		//student threads
		for(int i=0;i<s;i++){
			Student student = new Student(i+1,commuteToCamp);
		}
		commuteToCamp.allStudentsOnWay = true;
		
		//coordinator threads
		for(int i=0;i<c;i++){
			Coordinator coordinator = new Coordinator(i+1,commuteToCamp);
		}
	}

}
