package br.udesc.dcc.bdes.analysis.newapp;


public class AccDist {
	int decFrom0ToMinus30;
	int decFromMinus30toMinus60;
	int decFromMinus60toMinus90;
	int decFromMinus90toInfinity;
	
	int accFrom0ToMinus25;
	int accFrom25toMinus43;
	int accFromMinus43toMinus73;
	int accFromMinus73toInfinity;
		
	int totalCount;
	
	double decFrom0ToMinus30Sum;
	double decFromMinus30toMinus60Sum;
	double decFromMinus60toMinus90Sum;
	double decFromMinus90toInfinitySum;
	
	double accFrom0ToMinus25Sum;
	double accFrom25toMinus43Sum;
	double accFromMinus43toMinus73Sum;
	double accFromMinus73toInfinitySum;
	
	public static final double DEC_LIMIT_1= -3.0; 
	public static final double DEC_LIMIT_2= -6.0;
	public static final double DEC_LIMIT_3= -9.0;
	
	public static final double ACC_LIMIT_1= 2.5; 
	public static final double ACC_LIMIT_2= 4.3;
	public static final double ACC_LIMIT_3= -9.0;
	
	
	
	public AccDist() {}
	
	private Interval getAccInterval(double accMss) {
		if (accMss < 0 && accMss > -3.5) {
			return Interval.of(0, 10);
		} else if (accMss <= -3.5 && accMss > -6.0) {
			return Interval.of(10, 20);
		} else if (accMss <= -6.0 && accMss > -9.0) {
			return Interval.of(20, 50);
		} else if (accMss <= -9.0) {
			return Interval.of(50, 100);
		} else if (accMss > 0 && accMss <= 2.5) {
			return Interval.of(0, 10);
			
		} else if (accMss > 2.5 && accMss <= 4.3) {
			return Interval.of(10, 20);
		} else if (accMss > 4.3 && accMss <= 7.3) {
			return Interval.of(20, 50);
		} else if (accMss > 7.3) {
			return Interval.of(50, 100);
		}
		return Interval.of(0, 0);
	}
	
	private Interval getAccWeightInterval(double accMss) {
		if (accMss < 0 && accMss > -3.5) {
			return Interval.of(0, -3.5);
		} else if (accMss <= -3.5 && accMss > -6.0) {
			return Interval.of(-3.5, -6.0);
		} else if (accMss <= -6.0 && accMss > -9.0) {
			return Interval.of(-6.0, -9.0);
		} else if (accMss <= -9.0) {
			return Interval.of(-9.0, -12);
		} else if (accMss > 0 && accMss <= 2.5) {
			return Interval.of(0, 2.5);
		} else if (accMss > 2.5 && accMss <= 4.3) {
			return Interval.of(2.5, 4.3);
		} else if (accMss > 4.3 && accMss <= 7.3) {
			return Interval.of(4.3, 7.3);
		} else if (accMss > 7.3) {
			return Interval.of(7.3, 12);
		}
		return Interval.of(0, 0);
	}
	
	public double getAccIndex(double accMss) {
		Interval accInterval = this.getAccInterval(accMss); //50 e 20
		Interval weightInterval = this.getAccWeightInterval(accMss); //4.3 - 7.3
		
		double diffMinMax = accInterval.diff(); //30
		
		double valueProp = accMss - weightInterval.min; //0.7
		double weightPerc = (valueProp * 100)/diffMinMax; //23.33
		
		double diffMinMaxWeight = weightInterval.diff();
		
		double value = (weightPerc * diffMinMaxWeight)/100; 
		
		return accInterval.min + value;
	}
	
	public void countAcc(double accMss) {
		if (accMss == 0) return; 
		totalCount++;
		double index = getAccIndex(accMss);
		
		if (accMss < 0 && accMss > -3.5) {
			decFrom0ToMinus30++;
			decFrom0ToMinus30Sum += index;
		} else if (accMss <= -3.5 && accMss > -6.0) {
			decFromMinus30toMinus60++;
			decFromMinus30toMinus60Sum += index;
		} else if (accMss <= -6.0 && accMss > -9.0) {
			decFromMinus60toMinus90++;
			decFromMinus60toMinus90Sum += index;
		} else if (accMss <= -9.0) {
			decFromMinus90toInfinity++;
			decFromMinus90toInfinitySum += index;
		} else if (accMss >= 0 && accMss <= 2.5) {
			accFrom0ToMinus25++;
			accFrom0ToMinus25Sum += index;
		} else if (accMss > 2.5 && accMss <= 4.3) {
			accFrom25toMinus43++;
			accFrom25toMinus43Sum += index;
		} else if (accMss > 4.3 && accMss <= 7.3) {
			accFromMinus43toMinus73++;
			accFromMinus43toMinus73Sum += index;
		} else if (accMss > 7.3) {
			accFromMinus73toInfinity++;
			accFromMinus73toInfinitySum += index;
		}
	}
	
	
	
}