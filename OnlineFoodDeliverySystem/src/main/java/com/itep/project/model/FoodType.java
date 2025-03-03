package com.itep.project.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = FoodTypeDeserializer.class)
public enum FoodType {
	VEG,
	NONVEG
}
