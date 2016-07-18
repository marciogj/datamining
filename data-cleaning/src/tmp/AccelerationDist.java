package tmp;


public class AccelerationDist {
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
	
	
	static double WEIGHT_SAFE = 1;
	static double WEIGHT_NORMAL = 1.15;
	static double WEIGHT_UNSAFE = 1.35;
	static double WEIGHT_DANGEROUS = 1.75;
	
	AccelerationDist() {}
	
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
	
	private double getAccIndex(double accMss) {
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
	
	private double proportion(double value, double max) {
		return (value*100)/max ;
	}
	
	public double getWeightEval() {
		double a = decFrom0ToMinus30 <= 0 ? 0 : decFrom0ToMinus30Sum/decFrom0ToMinus30;
		double b = decFromMinus30toMinus60 <= 0 ? 0 : decFromMinus30toMinus60Sum/decFromMinus30toMinus60;
		double c = decFromMinus60toMinus90 <=  0 ? 0 : decFromMinus60toMinus90Sum/decFromMinus60toMinus90;
		double d = decFromMinus90toInfinity <= 0 ? 0 : decFromMinus90toInfinitySum/decFromMinus90toInfinity;
		
		double e = accFrom0ToMinus25 <= 0 ? 0 : accFrom0ToMinus25Sum/accFrom0ToMinus25;
		double f = accFrom25toMinus43 <= 0 ? 0 : accFrom25toMinus43Sum/accFrom25toMinus43;
		double g = accFromMinus43toMinus73 <= 0 ? 0 : accFromMinus43toMinus73Sum/accFromMinus43toMinus73;
		double h = accFromMinus73toInfinity <= 0 ? 0 : accFromMinus73toInfinitySum/accFromMinus73toInfinity;
		
		double ap = proportion(decFrom0ToMinus30, totalCount);
		double bp = proportion(decFromMinus30toMinus60, totalCount)/100;
		double cp = proportion(decFromMinus60toMinus90, totalCount)/100;
		double dp = proportion(decFromMinus90toInfinity, totalCount)/100;
		double ep = proportion(accFrom0ToMinus25, totalCount)/100;
		double fp = proportion(accFrom25toMinus43, totalCount)/100;
		double gp = proportion(accFromMinus43toMinus73, totalCount)/100;
		double hp = proportion(accFromMinus73toInfinity, totalCount)/100;
		
		double index = a*ap +  b*bp + c*cp + d*dp + e*ep + f*fp + g*gp + h*hp;
		
		return index > 100 ? 100 : index;
	}
	
	public double getWeightEval2() {
		double a = decFrom0ToMinus30 <= 0 ? 0 : decFrom0ToMinus30Sum/decFrom0ToMinus30;
		double b = decFromMinus30toMinus60 <= 0 ? 0 : decFromMinus30toMinus60Sum/decFromMinus30toMinus60;
		double c = decFromMinus60toMinus90 <=  0 ? 0 : decFromMinus60toMinus90Sum/decFromMinus60toMinus90;
		double d = decFromMinus90toInfinity <= 0 ? 0 : decFromMinus90toInfinitySum/decFromMinus90toInfinity;
		
		double e = accFrom0ToMinus25 <= 0 ? 0 : accFrom0ToMinus25Sum/accFrom0ToMinus25;
		double f = accFrom25toMinus43 <= 0 ? 0 : accFrom25toMinus43Sum/accFrom25toMinus43;
		double g = accFromMinus43toMinus73 <= 0 ? 0 : accFromMinus43toMinus73Sum/accFromMinus43toMinus73;
		double h = accFromMinus73toInfinity <= 0 ? 0 : accFromMinus73toInfinitySum/accFromMinus73toInfinity;
		
		double ap = proportion(decFrom0ToMinus30, totalCount);
		double bp = proportion(decFromMinus30toMinus60, totalCount)/100;
		double cp = proportion(decFromMinus60toMinus90, totalCount)/100;
		double dp = proportion(decFromMinus90toInfinity, totalCount)/100;
		double ep = proportion(accFrom0ToMinus25, totalCount)/100;
		double fp = proportion(accFrom25toMinus43, totalCount)/100;
		double gp = proportion(accFromMinus43toMinus73, totalCount)/100;
		double hp = proportion(accFromMinus73toInfinity, totalCount)/100;
		
		
		ap = ap * WEIGHT_SAFE;
		bp = bp * WEIGHT_NORMAL;
		cp = cp * WEIGHT_UNSAFE;
		dp = dp * WEIGHT_DANGEROUS;
		
		ep = ep * WEIGHT_SAFE;
		fp = fp * WEIGHT_NORMAL;
		gp = gp * WEIGHT_UNSAFE;
		hp = hp * WEIGHT_DANGEROUS;
		
		double index = a*ap +  b*bp + c*cp + d*dp + e*ep + f*fp + g*gp + h*hp;
		
		return index > 100 ? 100 : index;
		
	}
	
}