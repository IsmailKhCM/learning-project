package org.example.userdemo.shared.config

import org.modelmapper.ModelMapper
import org.modelmapper.config.Configuration as ModelMapperConfiguration // Rename

import org.modelmapper.Conditions
import org.modelmapper.convention.MatchingStrategies
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ModelMapperConfig {

  @Bean
  fun modelMapper(): ModelMapper {
    val modelMapper = ModelMapper()
    modelMapper.configuration.apply {
      matchingStrategy = MatchingStrategies.STRICT
      isFieldMatchingEnabled = true
      fieldAccessLevel = ModelMapperConfiguration.AccessLevel.PRIVATE
      propertyCondition = Conditions.isNotNull()  // Skip null values globally
    }

    return modelMapper
  }
}
