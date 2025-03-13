package com.myorg.mortgage.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.myorg.mortgage.model.InterestRate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataLoader {

    @Value("classpath:data/interest-rate.json")
    private Resource resourceFile;

    private final ObjectMapper objectMapper;

    public List<InterestRate> loadData() throws IOException {
        return objectMapper.readValue(resourceFile.getInputStream(), new TypeReference<>() {});
    }

}
