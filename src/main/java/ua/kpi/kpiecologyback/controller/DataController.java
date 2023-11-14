package ua.kpi.kpiecologyback.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ua.kpi.kpiecologyback.domain.*;
import ua.kpi.kpiecologyback.service.DataService;

import java.io.IOException;

import java.nio.charset.Charset;
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

    @PostMapping("/upload/csv/company")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Company> uploadCompany(@RequestParam("file") MultipartFile file) throws IOException {
        return dataService.uploadCompany(new String(file.getBytes(), "WINDOWS-1251"));
    }

    @PostMapping("/upload/csv/pollutant")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Pollutant> uploadPollutant(@RequestParam("file") MultipartFile file) throws IOException {
        return dataService.uploadPollutant(new String(file.getBytes(), "WINDOWS-1251"));
    }

    @PostMapping("/upload/csv/pollution")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Pollution> uploadPollution(@RequestParam("file") MultipartFile file) throws IOException {
        return dataService.uploadPollution(new String(file.getBytes(), "WINDOWS-1251"));
    }

    @PostMapping("/upload/company")
    @ResponseStatus(HttpStatus.CREATED)
    public Company uploadCompany(@RequestBody Company company) {
        return dataService.uploadCompany(company);
    }

    @PostMapping("/upload/pollutant")
    @ResponseStatus(HttpStatus.CREATED)
    public Pollutant uploadPollutant(@RequestBody Pollutant pollutant) {
        return dataService.uploadPollutant(pollutant);
    }

    @PostMapping("/upload/pollutantType")
    @ResponseStatus(HttpStatus.CREATED)
    public PollutantType uploadPollutantType(@RequestBody PollutantType pollutantType) {
        return dataService.uploadPollutantType(pollutantType);
    }

    @PostMapping("/upload/pollution")
    @ResponseStatus(HttpStatus.CREATED)
    public Pollution uploadPollution(@RequestBody Pollution pollution) {
        return dataService.uploadPollution(pollution);
    }

    @PostMapping("/upload/emergency")
    @ResponseStatus(HttpStatus.CREATED)
    public Emergency uploadEmergency(@RequestBody Emergency emergency) {
        return dataService.uploadEmergency(emergency);
    }

    @GetMapping("/get/company")
    public List<Company> getAllCompany () {
        return dataService.getAllCompany();
    }

    @GetMapping("/get/pollutant")
    public List<Pollutant> getAllPollutant () {
        return dataService.getAllPollutant();
    }

    @GetMapping("/get/pollutantType")
    public List<PollutantType> getAllPollutantType () {
        return dataService.getAllPollutantType();
    }

    @GetMapping("/get/pollution")
    public List<Pollution> getAllPollution () {
        return dataService.getAllPollution();
    }

    @GetMapping("/get/emergency")
    public List<Emergency> getAllEmergency () {
        return dataService.getAllEmergency();
    }

    @GetMapping("/get/csv/company")
    public ResponseEntity<String> getAllCompanyCsv () {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("text", "csv", Charset.forName("windows-1251")));
        headers.setContentDispositionFormData("attachment", "company.csv");
        return ResponseEntity.ok().headers(headers).body(dataService.loadCompany());
    }

    @GetMapping("/get/csv/pollutant")
    public ResponseEntity<String> getAllPollutantCsv () {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("text", "csv", Charset.forName("windows-1251")));
        headers.setContentDispositionFormData("attachment", "pollutant.csv");
        return ResponseEntity.ok().headers(headers).body(dataService.loadPollutant());
    }

    @GetMapping("/get/csv/pollution")
    public ResponseEntity<String> getAllPollutionCsv () {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("text", "csv", Charset.forName("windows-1251")));
        headers.setContentDispositionFormData("attachment", "pollution.csv");
        return ResponseEntity.ok().headers(headers).body(dataService.loadPollution());
    }

    @PostMapping("/update/company")
    @ResponseStatus(HttpStatus.CREATED)
    public Company updateCompany (@RequestBody Company company) {
        return dataService.updateCompany(company);
    }

    @PostMapping("/update/pollutant")
    @ResponseStatus(HttpStatus.CREATED)
    public Pollutant updatePollutant (@RequestBody Pollutant pollutant) {
        return dataService.updatePollutant(pollutant);
    }

    @PostMapping("/update/pollutantType")
    @ResponseStatus(HttpStatus.CREATED)
    public PollutantType updatePollutantType(@RequestBody PollutantType pollutantType) {
        return dataService.updatePollutantType(pollutantType);
    }

    @PostMapping("/update/pollution")
    @ResponseStatus(HttpStatus.CREATED)
    public Pollution updatePollution (@RequestBody Pollution pollution) {
        return dataService.updatePollution(pollution);
    }

    @PostMapping("/update/emergency")
    @ResponseStatus(HttpStatus.CREATED)
    public Emergency updateEmergency (@RequestBody Emergency emergency) {
        return dataService.updateEmergency(emergency);
    }

    @PostMapping("/delete/company")
    @ResponseStatus(HttpStatus.CREATED)
    public void deleteCompany (@RequestBody List<Long> ids) {
        dataService.deleteCompany(ids);
    }

    @PostMapping("/delete/pollutant")
    @ResponseStatus(HttpStatus.CREATED)
    public void deletePollutant (@RequestBody List<Long> ids) {
        dataService.deletePollutant(ids);
    }

    @PostMapping("/delete/pollutantType")
    @ResponseStatus(HttpStatus.CREATED)
    public void deletePollutantType (@RequestBody List<Long> ids) {
        dataService.deletePollutantType(ids);
    }

    @PostMapping("/delete/pollution")
    @ResponseStatus(HttpStatus.CREATED)
    public void deletePollution (@RequestBody List<Long> ids) {
        dataService.deletePollution(ids);
    }

    @PostMapping("/delete/emergency")
    @ResponseStatus(HttpStatus.CREATED)
    public void deleteEmergency (@RequestBody List<Long> ids) {
        dataService.deleteEmergency(ids);
    }
}
