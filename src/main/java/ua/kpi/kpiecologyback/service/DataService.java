package ua.kpi.kpiecologyback.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.kpi.kpiecologyback.domain.Company;
import ua.kpi.kpiecologyback.domain.Pollutant;
import ua.kpi.kpiecologyback.domain.Pollution;
import ua.kpi.kpiecologyback.dto.SummaryDTO;
import ua.kpi.kpiecologyback.repository.CompanyRepository;
import ua.kpi.kpiecologyback.repository.PollutantRepository;
import ua.kpi.kpiecologyback.repository.PollutionRepository;

import java.util.List;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DataService {
    private final CompanyRepository companyRepository;
    private final PollutantRepository pollutantRepository;
    private final PollutionRepository pollutionRepository;
    private final Pattern pattern = Pattern.compile("(?<=,\").*(?=\",)|[^,\"]+");
    @Autowired
    public DataService(CompanyRepository companyRepository, PollutantRepository pollutantRepository, PollutionRepository pollutionRepository) {
        this.companyRepository = companyRepository;
        this.pollutantRepository = pollutantRepository;
        this.pollutionRepository = pollutionRepository;
    }

    public List<SummaryDTO> getAllSummary() {
        return pollutionRepository.findAll().stream()
                .map(pollution-> new SummaryDTO(pollution.getCompany().getCompanyName(),
                        pollution.getPollutant().getPollutantName(), pollution.getPollutionValue(),
                        pollution.getPollutant().getMfr(), pollution.getPollutant().getTlv(), pollution.getYear()))
                .toList();
    }

    public List<Company> getAllCompany() {
        return companyRepository.findAll();
    }

    public List<Pollutant> getAllPollutant() {
        return pollutantRepository.findAll();
    }

    public List<Pollution> getAllPollution() {
        return pollutionRepository.findAll();
    }

    public void uploadCompany(String data) {
        Scanner scanner = new Scanner(data);
        scanner.nextLine();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            Matcher matcher = pattern.matcher(line);

            Company company = new Company();

            matcher.find();
            company.setCompanyName(matcher.group().trim());

            matcher.find();
            company.setActivity(matcher.group());

            matcher.find();
            company.setLocation(matcher.group());

            companyRepository.save(company);
        }
    }

    public void uploadPollutant(String data) {
        Scanner scanner = new Scanner(data);
        scanner.nextLine();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            Matcher matcher = pattern.matcher(line);

            Pollutant pollutant = new Pollutant();

            matcher.find();
            pollutant.setPollutantName(matcher.group().trim());

            matcher.find();
            pollutant.setTlv(Integer.parseInt(matcher.group()));

            matcher.find();
            pollutant.setMfr(Integer.parseInt(matcher.group()));

            pollutantRepository.save(pollutant);
        }
    }

    public void uploadPollution(String data) {
        Scanner scanner = new Scanner(data);
        scanner.nextLine();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            Matcher matcher = pattern.matcher(line);

            Pollution pollution = new Pollution();

            matcher.find();
            Company company = companyRepository.findByCompanyName(matcher.group().trim());
            pollution.setCompany(company);

            matcher.find();
            Pollutant pollutant = pollutantRepository.findByPollutantName(matcher.group().trim());
            pollution.setPollutant(pollutant);

            matcher.find();
            pollution.setPollutionValue(Double.parseDouble(matcher.group().replace(",", ".")));

            matcher.find();
            pollution.setYear(Integer.parseInt(matcher.group()));

            pollutionRepository.save(pollution);
        }
    }

    public void uploadCompany(Company company) {
        companyRepository.save(company);
    }

    public void uploadPollutant(Pollutant pollutant) {
        pollutantRepository.save(pollutant);
    }

    public void uploadPollution(Pollution pollution) {
        pollutionRepository.save(pollution);
    }

    public void updateCompany(Company company) {
        companyRepository.save(company);
    }

    public void updatePollutant(Pollutant pollutant) {
        pollutantRepository.save(pollutant);
    }

    public void updatePollution(Pollution pollution) {
        pollutionRepository.save(pollution);
    }

    public void deleteCompany(long id) {
        companyRepository.deleteById(id);
    }

    public void deletePollutant(long id) {
        pollutantRepository.deleteById(id);
    }

    public void deletePollution(long id) {
        pollutionRepository.deleteById(id);
    }
    public void deletePollution(List<Long> ids) {
        pollutionRepository.deleteAllByIdInBatch(ids);
    }
}
