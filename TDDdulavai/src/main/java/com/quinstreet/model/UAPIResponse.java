package com.quinstreet.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UAPIResponse {

	private List<Field> fields;
	private List<Field> paymentPlanFields;

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	public List<Field> getPaymentPlanFields() {
		return paymentPlanFields;
	}

	public void setPaymentPlanFields(List<Field> paymentPlanFields) {
		this.paymentPlanFields = paymentPlanFields;
	}

}


