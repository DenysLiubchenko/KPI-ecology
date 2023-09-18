package ua.kpi.kpiecologyback.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.kpi.kpiecologyback.domain.Company;
import ua.kpi.kpiecologyback.domain.Pollutant;
import ua.kpi.kpiecologyback.domain.Pollution;
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
    @Autowired
    public DataService(CompanyRepository companyRepository, PollutantRepository pollutantRepository, PollutionRepository pollutionRepository) {
        this.companyRepository = companyRepository;
        this.pollutantRepository = pollutantRepository;
        this.pollutionRepository = pollutionRepository;
    }

    public List<Pollution> getAllPollution() {
        return pollutionRepository.findAll();
    }

    private Pattern pattern = Pattern.compile("(?<=,\").*(?=\",)|[^,\"]+");

    public void uploadCompanies(String data) {
        Scanner scanner = new Scanner(data);
        scanner.nextLine();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            Matcher matcher = pattern.matcher(line);

            Company company = new Company();

            matcher.find();
            company.setCompanyName(matcher.group());

            matcher.find();
            company.setActivity(matcher.group());

            matcher.find();
            company.setLocation(matcher.group());

            companyRepository.save(company);
        }
    }

    public void uploadPollutants(String data) {
        Scanner scanner = new Scanner(data);
        scanner.nextLine();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            Matcher matcher = pattern.matcher(line);

            Pollutant pollutant = new Pollutant();

            matcher.find();
            pollutant.setNamePollutant(matcher.group());

            matcher.find();
            pollutant.setTlv(Integer.parseInt(matcher.group()));

            matcher.find();
            pollutant.setMassFlowRate(Integer.parseInt(matcher.group()));

            pollutantRepository.save(pollutant);
        }
    }

    public void uploadPollutions(String data) {
        Scanner scanner = new Scanner(data);
        scanner.nextLine();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            Matcher matcher = pattern.matcher(line);

            Pollution pollution = new Pollution();


            matcher.find();
            System.out.println(Long.parseLong(matcher.group()));
            Company company = companyRepository.findById(Long.parseLong(matcher.group())).orElseThrow();
            pollution.setCompany(company);

            matcher.find();
            Pollutant pollutant = pollutantRepository.findById(Long.parseLong(matcher.group())).orElseThrow();
            pollution.setPollutant(pollutant);

            matcher.find();
            pollution.setPollutionValue(Double.parseDouble(matcher.group().replace(",", ".")));

            matcher.find();
            pollution.setYear(Integer.parseInt(matcher.group()));

            pollutionRepository.save(pollution);
        }
    }
}
