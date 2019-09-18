
/*
CS640 Fall 2019
*/
import java.net.*;
import java.io.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;

public class TimerTest {
	public static void main(String[] args) throws IOException {

		TimerTask task = new TimerTask() {
			public void run() {
				System.out.println("Task performed on: " + new Date() + "n" + "Thread's name: " + Thread.currentThread().getName());
			}
		};

		Timer timer = new Timer("Timer");
		long delay = 1000L;
		System.out.println("Task performed on: " + new Date() + "n" + "Thread's name: " + Thread.currentThread().getName());
		timer.schedule(task, delay);

	}
}
