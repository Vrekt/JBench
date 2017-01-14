package me.vrekt.jbench;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;

import me.vrekt.jbench.benchmark.BenchThread;

public class JBench {

	private JFrame frame;

	private JButton startBenchmark;
	private JButton stopBenchmark;

	private JEditorPane editorPane;
	private GroupLayout groupLayout;

	private int processorCores = Runtime.getRuntime().availableProcessors();
	private int threadsFinished = 0;

	private List<BenchThread> workerThreads = new ArrayList<BenchThread>();
	private List<Long> threadTimes = new ArrayList<Long>();

	private static JBench instance;

	public static void main(String[] args) {
		instance = new JBench();
		instance.initialize();
	}

	public void initialize() {
		frame = new JFrame("JBench: v0.1");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setLocationRelativeTo(null);
		frame.setSize(400, 400);
		frame.setResizable(false);

		startBenchmark = new JButton("Start Benchmark");
		stopBenchmark = new JButton("Stop Benchmark");

		stopBenchmark.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent event) {

				workerThreads.forEach(thread -> thread.stop());
				workerThreads.clear();
				startBenchmark.setEnabled(true);
				stopBenchmark.setEnabled(false);

				try {
					editorPane.getDocument().remove(0, editorPane.getDocument().getLength());
				} catch (BadLocationException e) {
					e.printStackTrace();
				}

			}
		});

		startBenchmark.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				workerThreads.clear();

				try {
					editorPane.getDocument().remove(0, editorPane.getDocument().getLength());
				} catch (BadLocationException e) {
					e.printStackTrace();
				}

				writeString("Detected " + processorCores + " CPU cores.");

				for (int i = 0; i < processorCores; i++) {
					writeString("Thread #" + i + " has started.");
					BenchThread thread = new BenchThread(instance, i);
					workerThreads.add(thread);
					thread.start();
				}
				stopBenchmark.setEnabled(true);
				startBenchmark.setEnabled(false);

			}
		});

		stopBenchmark.setEnabled(false);
		editorPane = new JEditorPane();

		editorPane.setFont(new Font("Corbel", Font.PLAIN, 14));
		editorPane.setEditable(false);
		editorPane.setText(" Benchmark results will appear here");
		editorPane.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup().addContainerGap()
						.addComponent(editorPane, GroupLayout.PREFERRED_SIZE, 228, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
								.addComponent(startBenchmark, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)
								.addComponent(stopBenchmark, GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE))
						.addContainerGap(22, Short.MAX_VALUE)));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup().addContainerGap()
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(editorPane, GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE)
								.addGroup(groupLayout.createSequentialGroup().addComponent(startBenchmark)
										.addPreferredGap(ComponentPlacement.RELATED).addComponent(stopBenchmark)))
						.addContainerGap()));
		frame.getContentPane().setLayout(groupLayout);

		frame.setVisible(true);
	}

	public void writeString(String text) {
		Document doc = editorPane.getDocument();

		try {
			doc.insertString(doc.getLength(), " " + text + "\n", new SimpleAttributeSet());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public void threadFinished(long time) {
		threadTimes.add(time);
		threadsFinished++;
		if (threadsFinished >= processorCores) {
			long total = 0;
			for (int i = 0; i < threadTimes.size(); i++) {
				total += threadTimes.get(i);
			}

			long score = total / threadTimes.size();
			threadTimes.clear();

			writeString("Your score was " + score);

			stopBenchmark.setEnabled(false);
			startBenchmark.setEnabled(true);
			threadsFinished = 0;

		}
	}

}
