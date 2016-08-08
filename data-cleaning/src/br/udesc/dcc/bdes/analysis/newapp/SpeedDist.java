package br.udesc.dcc.bdes.analysis.newapp;

public class SpeedDist {
	private int underLimit;
	private int fromLimitTo10;
	private int from10to20;
	private int from20to50;
	private int over50;
	
	private int totalCount;
	
	private double underLimitWSum;
	private double fromLimitTo10WSum;
	private double from10to20WSum;
	private double from20to50WSum;
	private double over50WSum;
	
	private double speedLimit;
	
	static double WEIGHT_UNDER_LIMIT = 0;
	static double WEIGHT_UPTO10_LIMIT = 1; // Seguro
	static double WEIGHT_10TO20_LIMIT = 1.5; //agressivo moderado
	static double WEIGHT_20TO50_LIMIT = 3.0; //agressivo impulsivo
	static double WEIGHT_OVER50_LIMIT = 5.00; //perigoso
	
	public SpeedDist(double maxSpeed) {
		this.speedLimit = maxSpeed;
	}
	
	public void setMaxSpeed(double speedMs) {
		this.speedLimit = speedMs;
	}
	
	public Interval getSpeedWeightInterval(double speedMs) {
		double limitPercentage = eval(speedMs);
		if (limitPercentage > 0 && limitPercentage <= 10) {
			return Interval.of(0, 25);
		} else if (limitPercentage > 10 && limitPercentage <= 20) {
			return Interval.of(25, 50);
		} else if (limitPercentage > 20 && limitPercentage <= 50) {
			return Interval.of(50, 75);
		} else if (limitPercentage > 50) {
			return Interval.of(75, 100);
		}
		
		return Interval.of(0, 0);
	}
	
	public Interval getSpeedInterval(double speedMs) {
		double limitPercentage = eval(speedMs);
		if (limitPercentage > 0 && limitPercentage <= 10) {
			return Interval.of(0, 10);
		} else if (limitPercentage > 10 && limitPercentage <= 20) {
			return Interval.of(10, 20);
		} else if (limitPercentage > 20 && limitPercentage <= 50) {
			return Interval.of(20, 50);
		} else if (limitPercentage > 50) {
			return Interval.of(50, 100);
		}
		
		return Interval.of(0, 0);
	}
	
	public double getSpeedWeight(double speedMs) {
		double limitPercentage = this.eval(speedMs);
		if (limitPercentage <= 0) return 0;
		Interval speedInterval = this.getSpeedInterval(speedMs);
		Interval weightInterval = this.getSpeedWeightInterval(speedMs);
		
		double diffMinMax = speedInterval.diff();
		double valueProp = limitPercentage - speedInterval.min;
		double weightPerc = (valueProp * 100)/diffMinMax; 
		double diffMinMaxWeight = weightInterval.diff();
		double value = (weightPerc * diffMinMaxWeight)/100; 
		
		return weightInterval.min + value;
	}
	
	public void countSpeed(double speedMs) {
		double limitPercentage = this.eval(speedMs);
		totalCount++;
		
		if (limitPercentage <= 0) {
			underLimit++;
			underLimitWSum += limitPercentage;
		} else if (limitPercentage > 0 && limitPercentage < 10) {
			fromLimitTo10++;
			fromLimitTo10WSum += limitPercentage;
		} else if (limitPercentage >= 10 && limitPercentage < 20) {
			from10to20++;
			from10to20WSum += limitPercentage;
		} else if (limitPercentage >= 20 && limitPercentage < 50) {
			from20to50++;
			from20to50WSum += limitPercentage;
		} else if (limitPercentage >= 50) {
			over50++;
			over50WSum += limitPercentage;
		}
	}
	
	private double eval(double speedMs) {
		double percent = ( (speedMs*100)/speedLimit ) - 100; 
		return  percent < 0 ? 0 : percent;
	}
	
	private double proportion(double value, double max) {
		return (value*100)/max ;
	}	
	
	public double getWeightEval() {
		double b = underLimit <= 0 ? 0 : underLimitWSum/underLimit;
		double c = fromLimitTo10 <=  0 ? 0 : fromLimitTo10WSum/fromLimitTo10;
		double d = from10to20 <= 0 ? 0 : from10to20WSum/from10to20;
		double e = from20to50 <= 0 ? 0 : from20to50WSum/from20to50;
		double f = over50 <= 0 ? 0 : over50WSum/over50;
		
		double bp = proportion(underLimit, totalCount)/100;
		double cp = proportion(fromLimitTo10, totalCount)/100;
		double dp = proportion(from10to20, totalCount)/100;
		double ep = proportion(from20to50, totalCount)/100;
		double fp = proportion(over50, totalCount)/100;
		
		double index = b*bp + c*cp + d*dp + e*ep + f*fp;
		return index > 100 ? 100 : index;
	}
	
	public double getWeightEval2() {
		double b = underLimit <= 0 ? 0 : underLimitWSum/underLimit;
		double c = fromLimitTo10 <=  0 ? 0 : fromLimitTo10WSum/fromLimitTo10;
		double d = from10to20 <= 0 ? 0 : from10to20WSum/from10to20;
		double e = from20to50 <= 0 ? 0 : from20to50WSum/from20to50;
		double f = over50 <= 0 ? 0 : over50WSum/over50;
		
		double bp = proportion(underLimit, totalCount)/100;
		double cp = proportion(fromLimitTo10, totalCount)/100;
		double dp = proportion(from10to20, totalCount)/100;
		double ep = proportion(from20to50, totalCount)/100;
		double fp = proportion(over50, totalCount)/100;
		
		
		bp = bp * WEIGHT_UNDER_LIMIT;
		cp = cp * WEIGHT_UPTO10_LIMIT;
		dp = dp * WEIGHT_10TO20_LIMIT;
		ep = ep * WEIGHT_20TO50_LIMIT;
		fp = fp * WEIGHT_OVER50_LIMIT;
		
		double index =b*bp + c*cp + d*dp + e*ep + f*fp;
		return index > 100 ? 100 : index;
	}
	
	public double getWeightEval3() {
		double c = fromLimitTo10 <=  0 ? 0 : fromLimitTo10WSum/fromLimitTo10;
		double d = from10to20 <= 0 ? 0 : from10to20WSum/from10to20;
		double e = from20to50 <= 0 ? 0 : from20to50WSum/from20to50;
		double f = over50 <= 0 ? 0 : over50WSum/over50;
		
		return (c+d+e+f)/4;
		
	}
	
	
	public double getWeightAgressiveEval2() {
		int aggressiveCount = fromLimitTo10 + from10to20 + from20to50 + over50;
		
		double c = fromLimitTo10 <=  0 ? 0 : fromLimitTo10WSum/fromLimitTo10;
		double d = from10to20 <= 0 ? 0 : from10to20WSum/from10to20;
		double e = from20to50 <= 0 ? 0 : from20to50WSum/from20to50;
		double f = over50 <= 0 ? 0 : over50WSum/over50;
		
		double cp = proportion(fromLimitTo10, aggressiveCount)/100;
		double dp = proportion(from10to20, aggressiveCount)/100;
		double ep = proportion(from20to50, aggressiveCount)/100;
		double fp = proportion(over50, aggressiveCount)/100;
		
		cp = cp * WEIGHT_UPTO10_LIMIT;
		dp = dp * WEIGHT_10TO20_LIMIT;
		ep = ep * WEIGHT_20TO50_LIMIT;
		fp = fp * WEIGHT_OVER50_LIMIT;
		
		double index = c*cp + d*dp + e*ep + f*fp;
		return index > 100 ? 100 : index;
	}

	public int getFromLimitTo10() {
		return fromLimitTo10;
	}

	public int getFrom10to20() {
		return from10to20;
	}

	public int getFrom20to50() {
		return from20to50;
	}

	public int getOver50() {
		return over50;
	}

	public int getUnderLimit() {
		return underLimit;
	}

	public int getTotal() {
		return totalCount;
	}

	public double getUnderLimitAvg() {
		if (underLimit == 0) return 0;
		return underLimitWSum/underLimit;
	}

	public double getFrom10to20Avg() {
		if (from10to20 == 0) return 0;
		return from10to20WSum/from10to20;
	}

	public double getFromLimitTo10Avg() {
		if (fromLimitTo10 == 0) return 0;
		return fromLimitTo10WSum/fromLimitTo10;
	}

	public double getFrom20to50Avg() {
		if (from20to50 == 0) return 0;
		return from20to50WSum/from20to50;
	}

	public double getOver50Avg() {
		if (over50 == 0) return 0;
		return over50WSum/over50;
	}
	
}