package br.udesc.dcc.bdes.model;

import java.time.LocalDateTime;

import br.udesc.dcc.bdes.server.rest.api.track.dto.PenaltySeverity;

public class PenaltyAlert {
	private PenaltySeverity severity;
	private PenaltyType type;
	private LocalDateTime start;
	private LocalDateTime end;
	private double distance;
	private double initialValue;
	private double finalValue;
	private double maxValue;
	
	public PenaltyAlert(PenaltySeverity severity, PenaltyType type) {
		this.type = type;
		this.severity = severity;
	}

	public PenaltySeverity getSeverity() {
		return severity;
	}

	public void setSeverity(PenaltySeverity type) {
		this.severity = type;
	}

	public LocalDateTime getStart() {
		return start;
	}

	public void setStart(LocalDateTime start) {
		this.start = start;
	}

	public LocalDateTime getEnd() {
		return end;
	}

	public void setEnd(LocalDateTime end) {
		this.end = end;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public double getInitialValue() {
		return initialValue;
	}

	public void setInitialValue(double initialValue) {
		this.initialValue = initialValue;
		this.maxValue = maxValue > initialValue ? maxValue : initialValue;
	}

	public double getFinalValue() {
		return finalValue;
	}

	public void setFinalValue(double finalValue) {
		this.finalValue = finalValue;
		this.maxValue = maxValue > finalValue ? maxValue : finalValue;
	}

	public double getMaxValue() {
		return maxValue;
	}

	public void increaseDistance(double distance) {
		this.distance += distance;
	}

	public PenaltyType getType() {
		return type;
	}

	public void setType(PenaltyType type) {
		this.type = type;
	}
	
}
