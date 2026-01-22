package com.example.ubercorp.dto;

import java.util.Map;

public class DriverChangeRequestDTO {
    private Long id;
    private String type;
    private String driverName;
    private String requestDate;
    private String status;
    private ChangesDTO changes;
    private String oldImage;
    private String newImage;

    public DriverChangeRequestDTO() {}

    public DriverChangeRequestDTO(Long id, String type, String driverName, String requestDate,
                                  String status, ChangesDTO changes, String oldImage, String newImage) {
        this.id = id;
        this.type = type;
        this.driverName = driverName;
        this.requestDate = requestDate;
        this.status = status;
        this.changes = changes;
        this.oldImage = oldImage;
        this.newImage = newImage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ChangesDTO getChanges() {
        return changes;
    }

    public void setChanges(ChangesDTO changes) {
        this.changes = changes;
    }

    public String getOldImage() {
        return oldImage;
    }

    public void setOldImage(String oldImage) {
        this.oldImage = oldImage;
    }

    public String getNewImage() {
        return newImage;
    }

    public void setNewImage(String newImage) {
        this.newImage = newImage;
    }

    public static class ChangesDTO {
        private Map<String, String> oldData;
        private Map<String, String> newData;

        public ChangesDTO() {}

        public ChangesDTO(Map<String, String> oldData, Map<String, String> newData) {
            this.oldData = oldData;
            this.newData = newData;
        }

        public Map<String, String> getOldData() {
            return oldData;
        }

        public void setOldData(Map<String, String> oldData) {
            this.oldData = oldData;
        }

        public Map<String, String> getNewData() {
            return newData;
        }

        public void setNewData(Map<String, String> newData) {
            this.newData = newData;
        }
    }
}