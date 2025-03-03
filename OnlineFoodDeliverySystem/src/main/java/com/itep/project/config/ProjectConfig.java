package com.itep.project.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProjectConfig {

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
	
}

//https://github.com/dailycodework/dream-shops
//
//Go in the above repository and analyze all the files ,and make me all the front end pages which you think are required for this project , this should be a spring mvc project ,use HTML,CSS,JS,Bootstrap and Thymleaf for front end, give all the generated files here in chat itself,and also give me the structure of this project,make sure that the project has to two modules Admin and User, after admin login he should be able to manage product operations like adding ,updating deleting etc, he shoul be able to control image and catgeories, while user after login can browse the  as per categories add  them to category and order , he should also be able to track order history
