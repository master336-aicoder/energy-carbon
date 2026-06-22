package com.seatone.monitor.controller;

import com.seatone.common.result.Result;
import com.seatone.monitor.dto.CompareDataDTO;
import com.seatone.monitor.entity.MonitorHistoryData;
import com.seatone.monitor.service.MonitorHistoryDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "时间对标")
@RestController
@RequestMapping("/monitor/time-compare")
public class TimeCompareController {

    @Autowired
    private MonitorHistoryDataService historyDataService;

    @ApiOperation("同设备不同时间段曲线数据对比")
    @GetMapping("/compare")
    public Result<List<CompareDataDTO>> compare(
            @ApiParam("计量器具ID") @RequestParam Long meterId,
            @ApiParam("数据类型") @RequestParam(required = false) String dataType,
            @ApiParam("时段A-开始") @RequestParam String startTimeA,
            @ApiParam("时段A-结束") @RequestParam String endTimeA,
            @ApiParam("时段B-开始") @RequestParam String startTimeB,
            @ApiParam("时段B-结束") @RequestParam String endTimeB) {
        List<CompareDataDTO> result = new ArrayList<>();

        // 时段A数据
        List<MonitorHistoryData> dataA = historyDataService.lambdaQuery()
                .eq(MonitorHistoryData::getMeterId, meterId)
                .eq(dataType != null && !dataType.isEmpty(), MonitorHistoryData::getDataType, dataType)
                .ge(MonitorHistoryData::getDataTime, LocalDateTime.parse(startTimeA))
                .le(MonitorHistoryData::getDataTime, LocalDateTime.parse(endTimeA))
                .orderByAsc(MonitorHistoryData::getDataTime)
                .list();
        CompareDataDTO seriesA = new CompareDataDTO();
        seriesA.setSeriesName("时段A");
        seriesA.setColor("#409EFF");
        List<CompareDataDTO.DataPoint> pointsA = new ArrayList<>();
        for (MonitorHistoryData d : dataA) {
            CompareDataDTO.DataPoint p = new CompareDataDTO.DataPoint();
            p.setTime(d.getDataTime().toString());
            p.setValue(d.getDataValue() != null ? d.getDataValue().doubleValue() : 0);
            pointsA.add(p);
        }
        seriesA.setData(pointsA);
        result.add(seriesA);

        // 时段B数据
        List<MonitorHistoryData> dataB = historyDataService.lambdaQuery()
                .eq(MonitorHistoryData::getMeterId, meterId)
                .eq(dataType != null && !dataType.isEmpty(), MonitorHistoryData::getDataType, dataType)
                .ge(MonitorHistoryData::getDataTime, LocalDateTime.parse(startTimeB))
                .le(MonitorHistoryData::getDataTime, LocalDateTime.parse(endTimeB))
                .orderByAsc(MonitorHistoryData::getDataTime)
                .list();
        CompareDataDTO seriesB = new CompareDataDTO();
        seriesB.setSeriesName("时段B");
        seriesB.setColor("#E6A23C");
        List<CompareDataDTO.DataPoint> pointsB = new ArrayList<>();
        for (MonitorHistoryData d : dataB) {
            CompareDataDTO.DataPoint p = new CompareDataDTO.DataPoint();
            p.setTime(d.getDataTime().toString());
            p.setValue(d.getDataValue() != null ? d.getDataValue().doubleValue() : 0);
            pointsB.add(p);
        }
        seriesB.setData(pointsB);
        result.add(seriesB);

        return Result.ok(result);
    }
}