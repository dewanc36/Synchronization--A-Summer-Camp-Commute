/**
 * @author Dewan Choudhury
 */

import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

//Monitor class
public class CommuteToCamp{
	int totalStudents=0;
	int numOfStudentsAtBusStop=0;
	int busCap=0;
	int indexOfObjectToBlockOn=0;//this is used to determine which object(group) a student will block on, upon arriving at the bus stop
	int groupSizeAtBusStop = 0;//used to control num of students in newly forming group
	int carindex = 0;//used by coordinators to assist a car
	boolean allStudentsOnWay = false;//is set true in main, once all student threads are created
	boolean done = false;//variable to signal with when commute to summer camp ended
	
	Vector<Object> studentsWaitingForBus = new Vector<>();//students wait at bus stop
	Vector<Object> busesWaitingForStudents= new Vector<>();//
	Vector<Object> waitingForTripToFinish = new Vector<>();//students wait while on trip
	
	Vector<Object> studentCar = new Vector<>();//student-car waiting queue
	Vector<Coordinator> coordinators = new Vector<>();//coordinators wait for cars
	
	Vector<Student> gymnasiumCount = new Vector<>();//used to check num of students who arrived 
	
	
	public CommuteToCamp(int totalStudents, int busCap){
		this.busCap = busCap;
		this.totalStudents = totalStudents;
		studentsWaitingForBus.addElement(new Object());
	}
	
	/*
	 * This is the method invoked by the bus when it is at the station
	 * It uses a notification object to make the bus wait if there are not 
	 * enough students.
	 */
	public void startTrip(Bus b){
		if(!done){
			Object bObject  = new Object();
			synchronized (bObject) {
				busesWaitingForStudents.addElement(bObject);
				if(cannotStartTrip() || studentsWaitingForBus.isEmpty()){
					while(true){
						try {
							b.msg(" is waiting for students at the bus stop.");
							bObject.wait();
							break;
						} catch (Exception e) {
								
						}
					}
				}
				
				if(!done){
					synchronized (busesWaitingForStudents.get(0)) {
						busesWaitingForStudents.remove(0);
					}
				}else{
					b.done = true;
				}
			}
		}
	}
	/*
	 * Helper method which determines if the bus can start heading to school.
	 */
	public synchronized boolean cannotStartTrip(){
		boolean result = true;
		if(groupSizeAtBusStop>=busCap || (groupSizeAtBusStop>0 && groupSizeAtBusStop<busCap && allStudentsOnWay==true)){
			result = false;
		}if(studentsWaitingForBus.isEmpty()){
			result = true;
		}
		return result;
	}
	
	/*
	 * Method instructs bus to signal the waiting student group
	 * Then start the trip
	 * When the bus reaches the school it signals the students on board to get off
	 * Then it heads back to the school
	 */
	public synchronized void busDeparture(Bus b){
		if(!done){
			b.msg(" is now boarding students.");
			synchronized (studentsWaitingForBus) {
				if(!studentsWaitingForBus.isEmpty()){
					synchronized (studentsWaitingForBus.get(0)) {
						indexOfObjectToBlockOn  = Math.max(0, indexOfObjectToBlockOn-1);
						studentsWaitingForBus.get(0).notifyAll();
						studentsWaitingForBus.remove(0);
					}
				}
			}
			b.msg(" is headed towards school.");
			try {
				b.sleep(randNum(8000, 10000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			b.msg(" has reached the school. Waiting for students to get off.");
			synchronized (waitingForTripToFinish) {
				waitingForTripToFinish.notifyAll();
			}
			try {
				b.sleep(randNum(1000, 2000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			b.msg(" is going back to the bus stop.");
			if(studentsWaitingForBus.isEmpty()){
				b.done= true;
			}
		}
	}
	
	/*
	 * This method is used by students taking the bus.
	 * It places the student in the latest forming group.
	 * If a big enough group forms, the current student signals any waiting bus
	 * Then the student waits with his/her group for a bus
	 */
	public void board(Student s){
		Object object;
		synchronized (studentsWaitingForBus) {
			if((numOfStudentsAtBusStop!=0) && (numOfStudentsAtBusStop%busCap)==0){
				indexOfObjectToBlockOn = studentsWaitingForBus.size();
				object = new Object();
				studentsWaitingForBus.add(indexOfObjectToBlockOn,object);
				
			}else{
				object = studentsWaitingForBus.get(indexOfObjectToBlockOn);
			}
		}
		synchronized (object) {
			numOfStudentsAtBusStop++;
			if(groupSizeAtBusStop==busCap){
				groupSizeAtBusStop=0;
			}
			groupSizeAtBusStop++;
			//if there is a bus waiting
			//and if there are students ready to leave
			synchronized (busesWaitingForStudents) {
				if((busesWaitingForStudents.size()>0) && 
						(groupSizeAtBusStop>=busCap || (groupSizeAtBusStop>0 && groupSizeAtBusStop<busCap && allStudentsOnWay==true))){
					synchronized (busesWaitingForStudents.get(0)) {
						busesWaitingForStudents.get(0).notify();
					}		
				}
			}
			try {
				object.wait();
			}catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * Used by students who commute by car
	 * They wait to be assisted by a coordinator
	 */
	public void getOffCar(Student s){
		Object object = new Object();
		synchronized (object) {
			studentCar.addElement(object);
			try {
				s.msg(" is waiting for a coordinator.");
				object.wait();
				s.msg(" is being assisted by a coordinator.");
			} catch (Exception e) {
					
			}
		}
	}
	/*
	 * coordinators assist student-car in they order of arrival
	 */
	public synchronized void attendToStudent(Coordinator c){
		if(!studentCar.isEmpty()){
			synchronized (studentCar.get(carindex)) {
				carindex++;
				if(studentCar.size()>=carindex){
					studentCar.get(carindex-1).notify();
					c.msg(" is helping a student.");
					try {
						c.sleep(randNum(2000, 3000));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					c.msg(" is done helping the student.");
				}
				carindex--;
				studentCar.remove(carindex);
			}
		}
		//if a coordinator determines all students have arrived
		//all other coordinators and buses are signaled to terminate
		if(gymnasiumCount.size()==totalStudents && !done){
			c.msg(" decided that all students have arrived.");
			done =true;
			for(int i=0;i<coordinators.size();i++){
				coordinators.get(i).done = true;
			}
			c.msg(" signaled all other coordinators that they are done.");
			synchronized (busesWaitingForStudents) {
				int rem= busesWaitingForStudents.size();
				if(rem>0){
					for(int i=0;i<rem;i++){
						synchronized (busesWaitingForStudents.get(i)) {
							busesWaitingForStudents.get(i).notify();
						}
					}
				}
			}
			c.msg(" let the buses know that they are done for today.");
			
		}
	}
	
	public int randNum(int min, int max){
		return ThreadLocalRandom.current().nextInt(min, max);
	}
}
