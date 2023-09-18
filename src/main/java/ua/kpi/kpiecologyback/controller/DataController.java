package ua.kpi.kpiecologyback.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;
import ua.kpi.kpiecologyback.domain.Pollutant;
import ua.kpi.kpiecologyback.domain.Pollution;
import ua.kpi.kpiecologyback.service.DataService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import java.util.List;

@RestController
@RequestMapping("/data")
public class DataController {
    private final DataService dataService;
    @Autowired
    public DataController(DataService dataService) {
        this.dataService = dataService;
    }

    @PostMapping("/uploadCompanies")
    public void uploadCompanies(@RequestParam("file") MultipartFile file) throws IOException {
        dataService.uploadCompanies(new String(file.getBytes(), "WINDOWS-1251"));
    }

    @PostMapping("/uploadPollutants")
    public void uploadPollutants(@RequestParam("file") MultipartFile file) throws IOException {
        dataService.uploadPollutants(new String(file.getBytes(), "WINDOWS-1251"));
    }

    @PostMapping("/uploadPollutions")
    public void uploadPollutions(@RequestParam("file") MultipartFile file) throws IOException {
        dataService.uploadPollutions(new String(file.getBytes(), "WINDOWS-1251"));
    }

    @GetMapping("/pollution")
    public List<Pollution> getAllPollution () {
        return dataService.getAllPollution();
    }
}
