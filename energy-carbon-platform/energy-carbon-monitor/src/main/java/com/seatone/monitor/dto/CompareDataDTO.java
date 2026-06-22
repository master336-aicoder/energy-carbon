package com.seatone.monitor.dto;

import lombok.Data;

import java.util.List;

@Data
public class CompareDataDTO {

    private String seriesName;

    private String color;

    private List<DataPoint> data;

    @Data
    public static class DataPoint {
        private String time;
        private Double value;
    }
}