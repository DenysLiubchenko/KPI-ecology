package ua.kpi.kpiecologyback.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ua.kpi.kpiecologyback.domain.Pollutant;
import ua.kpi.kpiecologyback.domain.Pollution;
import ua.kpi.kpiecologyback.service.DataService;

import java.util.List;

@RestController
@RequestMapping("/data")
public class DataController {
    private final DataService dataService;
    @Autowired
    public DataController(DataService dataService) {
        this.dataService = dataService;
    }

    @PostMapping("/uploadCSV")
    public void uploadCSV(@RequestBody String body) {
        System.out.println(body);
    }

    @GetMapping("/pollution")
    public List<Pollution> getAllPollution () {
        return dataService.getAllPollution();
    }
}
