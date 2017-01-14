package me.vrekt.jbench.benchmark;

import me.vrekt.jbench.JBench;

public class BenchThread extends Thread {

	private String start = "aaaaaa";
	private long startTime;

	private boolean cc = false;

	private int ID;
	private JBench instance;

	public BenchThread(JBench instance, int ID) {
		this.instance = instance;
		this.ID = ID;
	}

	private boolean result(String pw) {
		return pw.equals("zzzzzz");
	}

	private String next(String s) {

		int length = s.length();
		char c = s.charAt(length - 1);

		String i = s.substring(0, length - 1);
		start = c == 'z' ? length > 1 ? next(i) + 'a' : "aa" : i + ++c;
		return start;
	}

	@Override
	public void run() {

		startTime = System.currentTimeMillis();

		while (!cc) {
			cc = result(next(start));
		}

		long time = System.currentTimeMillis() - startTime;
		long seconds = time / 1000;
		instance.writeString("Thread #" + ID + " took about " + seconds + " seconds.");
		instance.threadFinished(time);

	}

}
