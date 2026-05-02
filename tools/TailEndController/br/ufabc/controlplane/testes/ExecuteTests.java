package br.ufabc.controlplane.testes;

import java.util.Timer;

import br.ufabc.controlplane.TimeControl;

public class ExecuteTests extends Thread{
	boolean run = true;
	public ExecuteTests() {
		super("ExecuteTestes");
		setDaemon(true);
	}

	public void run(){
		while(run){
			executeTests();
			run = false;
		}
		
	}
	
	public void executeTests(){
		Timer time = TimeControl.getInstance();
		//Seta ganho do booster
//		time.schedule(new SetGanhoBoosterTeste(18), 10000);
//		time.schedule(new SetGanhoBoosterTeste(8), 40000);
//		time.schedule(new TimerTask() {
//			
//			@Override
//			public void run() {
//				System.out.println("executa teste");
//				
//			}
//		}, 300);
		
		
	}
	
	public static void main(String[] args){
		while (true){
		Thread e = new ExecuteTests();
		e.start();
		try {
			sleep(60000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		}
	}

	/**
	 * @return the run
	 */
	public boolean isRun() {
		return run;
	}

	/**
	 * @param run the run to set
	 */
	public void setRun(boolean run) {
		this.run = run;
	}

}
