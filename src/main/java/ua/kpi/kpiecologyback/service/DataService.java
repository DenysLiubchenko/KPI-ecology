package ua.kpi.kpiecologyback.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import ua.kpi.kpiecologyback.domain.Company;
import ua.kpi.kpiecologyback.domain.Pollutant;
import ua.kpi.kpiecologyback.domain.Pollution;
import ua.kpi.kpiecologyback.repository.CompanyRepository;
import ua.kpi.kpiecologyback.repository.PollutantRepository;
import ua.kpi.kpiecologyback.repository.PollutionRepository;

import java.util.List;

import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DataService {
    private final CompanyRepository companyRepository;
    private final PollutantRepository pollutantRepository;
    private final PollutionRepository pollutionRepository;
    private final CalcService calcService;
    private final Pattern pattern = Pattern.compile("(?<=,\").*?(?=\",)|[^,\"]+");
    @Autowired
    public DataService(CompanyRepository companyRepository, PollutantRepository pollutantRepository, PollutionRepository pollutionRepository, CalcService calcService) {
        this.companyRepository = companyRepository;
        this.pollutantRepository = pollutantRepository;
        this.pollutionRepository = pollutionRepository;
        this.calcService = calcService;
    }

    public List<Company> getAllCompany() {
        return companyRepository.findAll();
    }

    public List<Pollutant> getAllPollutant() {
        return pollutantRepository.findAll();
    }

    public List<Pollution> getAllPollution() {
        return pollutionRepository.findAllBy();
    }

    public void uploadCompany(String data) {
        Scanner scanner = new Scanner(data);
        scanner.nextLine();
        List<String> companyNames = companyRepository.findAll().stream().map(Company::getCompanyName).toList();
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
            if (!companyNames.contains(company.getCompanyName()))
                companyRepository.save(company);
        }
    }

    public void uploadPollutant(String data) {
        Scanner scanner = new Scanner(data);
        scanner.nextLine();
        List<String> pollutantNames = pollutantRepository.findAll().stream().map(Pollutant::getPollutantName).toList();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            Matcher matcher = pattern.matcher(line);

            Pollutant pollutant = new Pollutant();

            matcher.find();
            pollutant.setPollutantName(matcher.group().trim());

            matcher.find();
            pollutant.setMfr(Integer.parseInt(matcher.group()));

            matcher.find();
            pollutant.setElv(Integer.parseInt(matcher.group()));

            matcher.find();
            pollutant.setTlv(Double.parseDouble(matcher.group().replace(",", ".")));

            if (!pollutantNames.contains(pollutant.getPollutantName()))
                pollutantRepository.save(pollutant);
        }
    }

    public void uploadPollution(String data) {
        Scanner scanner = new Scanner(data);
        scanner.nextLine();
        List<Pollution> pollutions = pollutionRepository.findAllBy();
        List<Company> companies = companyRepository.findAll();
        List<Pollutant> pollutants = pollutantRepository.findAll();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            Matcher matcher = pattern.matcher(line);

            Pollution pollution = new Pollution();

            matcher.find();
            String companyName = matcher.group().trim();
            Company company = companies.stream().filter(company1 -> company1.getCompanyName().equals(companyName))
                    .findFirst().orElseThrow(()-> new HttpClientErrorException(HttpStatusCode.valueOf(400)));
            pollution.setCompany(company);

            matcher.find();
            String pollutantName = matcher.group().trim();
            Pollutant pollutant = pollutants.stream().filter(pollutant1 -> pollutant1.getPollutantName().equals(pollutantName))
                    .findFirst().orElseThrow(()-> new HttpClientErrorException(HttpStatusCode.valueOf(400)));
            pollution.setPollutant(pollutant);

            matcher.find();
            pollution.setPollutionValue(Double.parseDouble(matcher.group().replace(",", ".")));

            matcher.find();
            pollution.setPollutionConcentration(Double.parseDouble(matcher.group().replace(",", ".")));

            matcher.find();
            pollution.setYear(Integer.parseInt(matcher.group()));

            pollution.setAddLadd(calcService.calcAddLadd(pollution.getPollutionConcentration()));

            if (pollutions.stream()
                    .noneMatch(pollution1 ->
                            pollution1.getPollutant().getPollutantName().equals(pollutant.getPollutantName()) &&
                            pollution1.getCompany().getCompanyName().equals(company.getCompanyName()) &&
                            pollution1.getYear().equals(pollution.getYear())))
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
        pollution.setAddLadd(calcService.calcAddLadd(pollution.getPollutionConcentration()));
        pollutionRepository.save(pollution);
    }

    public void updateCompany(Company company) {
        Company currentCompany = companyRepository.findById(company.getId())
                .orElseThrow(()->new HttpClientErrorException(HttpStatusCode.valueOf(404)));
        currentCompany.setCompanyName(Optional.ofNullable(company.getCompanyName()).orElse(currentCompany.getCompanyName()));
        currentCompany.setActivity(Optional.ofNullable(company.getActivity()).orElse(currentCompany.getActivity()));
        currentCompany.setLocation(Optional.ofNullable(company.getLocation()).orElse(currentCompany.getLocation()));
        companyRepository.save(currentCompany);
    }

    public void updatePollutant(Pollutant pollutant) {
        Pollutant currentPollutant = pollutantRepository.findById(pollutant.getId())
                .orElseThrow(()->new HttpClientErrorException(HttpStatusCode.valueOf(404)));
        currentPollutant.setPollutantName(Optional.ofNullable(pollutant.getPollutantName()).orElse(currentPollutant.getPollutantName()));
        currentPollutant.setMfr(Optional.ofNullable(pollutant.getMfr()).orElse(currentPollutant.getMfr()));
        currentPollutant.setElv(Optional.ofNullable(pollutant.getElv()).orElse(currentPollutant.getElv()));
        currentPollutant.setTlv(Optional.ofNullable(pollutant.getTlv()).orElse(currentPollutant.getTlv()));
        pollutantRepository.save(currentPollutant);
    }

    public void updatePollution(Pollution pollution) {
        Pollution currentPollution = pollutionRepository.findById(pollution.getId())
                .orElseThrow(()->new HttpClientErrorException(HttpStatusCode.valueOf(404)));
        if (pollution.getCompany()!=null)
            currentPollution.setCompany(companyRepository.findById(Optional.ofNullable(pollution.getCompany().getId())
                    .orElse(currentPollution.getCompany().getId())).get());
        if (pollution.getPollutant()!=null)
            currentPollution.setPollutant(pollutantRepository.findById(Optional.ofNullable(pollution.getPollutant().getId())
                    .orElse(currentPollution.getPollutant().getId())).get());
        currentPollution.setPollutionValue(Optional.ofNullable(pollution.getPollutionValue())
                .orElse(currentPollution.getPollutionValue()));
        currentPollution.setPollutionConcentration(Optional.ofNullable(pollution.getPollutionConcentration())
                .orElse(currentPollution.getPollutionConcentration()));
        currentPollution.setAddLadd(calcService.calcAddLadd(currentPollution.getPollutionConcentration()));
        pollutionRepository.save(currentPollution);
    }

    public void deleteCompany(List<Long> ids) {
        ids.forEach(companyRepository::deleteById);
    }
    public void deletePollutant(List<Long> ids) {
        ids.forEach(pollutantRepository::deleteById);
    }
    public void deletePollution(List<Long> ids) {
        pollutionRepository.deleteAllByIdInBatch(ids);
    }

    public String loadCompany () {
        return "Назва компанії,Вид діяльності,Місцезнаходження\n"+getAllCompany().stream()
                .map(o -> new String[]{o.getCompanyName(), o.getActivity(), o.getLocation()})
                .map(this::convertToCSV).collect(Collectors.joining("\n"));
    }

    public String loadPollutant () {
        return "Назва забрудника,Величина масової витрати,ГДВ,ГДК\n"+getAllPollutant().stream()
                .map(o -> new String[]{o.getPollutantName(), String.valueOf(o.getMfr()),
                        String.valueOf(o.getElv()), String.valueOf(o.getTlv())})
                .map(this::convertToCSV).collect(Collectors.joining("\n"));
    }

    public String loadPollution () {
        return "Компанія,Забрудник,Розмір викидів,Концентрація,Дата,Add\\Ladd\n"+getAllPollution().stream().map(o -> new String[]{o.getCompany().getCompanyName(),
                        o.getPollutant().getPollutantName(), String.valueOf(o.getPollutionValue()),
                        String.valueOf(o.getPollutionConcentration()), String.valueOf(o.getYear()),
                        String.valueOf(o.getAddLadd())})
                .map(this::convertToCSV).collect(Collectors.joining("\n"));
    }

    private String convertToCSV(String[] data) {
        return Stream.of(data).map(this::escapeSpecialCharacters)
                .collect(Collectors.joining(","));
    }

    private String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }
}
