package com.example.UberComp.dto.driver;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverChangeRequestDTO {
    private Long id;
    private String type;
    private String driverName;
    private String requestDate;
    private String status;
    private ChangesDTO changes;
    private String oldImage;
    private String newImage;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChangesDTO {
        private Map<String, String> oldData;
        private Map<String, String> newData;
    }
}
