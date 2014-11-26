package org.openml.learningcurves.data;

public class Evaluation {
	private double accuracy;
	private double auroc;
	private double build_cpu_time;
	private int count;

	public Evaluation() {
		this.accuracy = 0;
		this.auroc = 0;
		this.build_cpu_time = 0;
		count = 0;
	}

	public Evaluation(double accuracy, double auroc, double build_cpu_time) {
		this.accuracy = accuracy;
		this.auroc = auroc;
		this.build_cpu_time = build_cpu_time;
		count = 1;
	}

	public void add(Evaluation e) {
		this.accuracy += e.getTotalAccuracy();
		this.auroc += e.getTotalAuroc();
		this.build_cpu_time += e.getTotalBuild_cpu_time();
		this.count += e.getCount();
	}

	public double getAccuracy() {
		return accuracy / count;
	}

	public double getAuroc() {
		return auroc / count;
	}

	public double getBuild_cpu_time() {
		return build_cpu_time / count;
	}

	public double getTotalAccuracy() {
		return accuracy;
	}

	public double getTotalAuroc() {
		return auroc;
	}

	public double getTotalBuild_cpu_time() {
		return build_cpu_time;
	}

	public int getCount() {
		return count;
	}
	
	@Override
	public String toString() {
		return "[" + getAccuracy() + "]";
	}
}