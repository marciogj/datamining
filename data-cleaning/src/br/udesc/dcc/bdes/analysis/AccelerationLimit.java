package br.udesc.dcc.bdes.analysis;

public class AccelerationLimit {
	protected String description = "";
	protected double limit = 0;
	protected double sum = 0;
	protected int count = 0;
	protected double weight = 0;
	
	public AccelerationLimit(double limit, String desc, double weight) {
		this.description = desc;
		this.limit = limit;
		this.weight = weight;
	}

	public void evaluate(double value) {
		sum += value;
		count++;
	}

	public double getLimit() {
		return limit;
	}

	public String getDescription() {
		return description;
	}

	public int getCount() {
		return count;
	}
	
	public double getSum() {
		return sum;
	}

	public double getWeight() {
		return weight;
	}
	
	public double getAvg() {
		return count == 0 ? 0 : sum/count;
	}
}