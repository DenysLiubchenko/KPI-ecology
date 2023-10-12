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

import java.util.*;

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
    private final Pattern pattern = Pattern.compile("\"(.*?)\"|([^,]+)");
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
            Queue<String> values = csvParser(line);

            Company company = new Company();

            company.setCompanyName(Objects.requireNonNull(values.poll()).trim());

            company.setActivity(Objects.requireNonNull(values.poll()).trim());

            company.setLocation(Objects.requireNonNull(values.poll()).trim());
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
            Queue<String> values = csvParser(line);

            Pollutant pollutant = new Pollutant();

            pollutant.setPollutantName(Objects.requireNonNull(values.poll()).trim());

            pollutant.setMfr(Integer.parseInt(Objects.requireNonNull(values.poll())));

            pollutant.setElv(Integer.parseInt(Objects.requireNonNull(values.poll())));

            pollutant.setTlv(Double.parseDouble(Objects.requireNonNull(values.poll()).replace(",", ".")));

            pollutant.setSf(Double.parseDouble(Objects.requireNonNull(values.poll()).replace(",", ".")));

            String replace = Objects.requireNonNull(values.poll()).replace(",", ".");
            pollutant.setRfc(Double.parseDouble(replace));

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
            Queue<String> values = csvParser(line);

            Pollution pollution = new Pollution();

            String companyName = Objects.requireNonNull(values.poll()).trim();
            Company company = companies.stream().filter(company1 -> company1.getCompanyName().equals(companyName))
                    .findFirst().orElseThrow(()-> new HttpClientErrorException(HttpStatusCode.valueOf(400)));
            pollution.setCompany(company);

            String pollutantName = Objects.requireNonNull(values.poll()).trim();
            Pollutant pollutant = pollutants.stream().filter(pollutant1 -> pollutant1.getPollutantName().equals(pollutantName))
                    .findFirst().orElseThrow(()-> new HttpClientErrorException(HttpStatusCode.valueOf(400)));
            pollution.setPollutant(pollutant);

            pollution.setPollutionValue(Double.parseDouble(Objects.requireNonNull(values.poll())
                    .replace(",", ".")));

            pollution.setPollutionConcentration(Double.parseDouble(Objects.requireNonNull(values.poll())
                    .replace(",", ".")));

            pollution.setYear(Integer.parseInt(Objects.requireNonNull(values.poll())));

            pollution.setHq(calcService.calcHq(pollution.getPollutionConcentration(), pollutant.getRfc()));
            pollution.setCr(calcService.calcCr(pollution.getPollutionConcentration(), pollutant.getSf()));

            if (pollutions.stream()
                    .noneMatch(pollution1 ->
                            pollution1.getPollutant().getPollutantName().equals(pollutant.getPollutantName()) &&
                            pollution1.getCompany().getCompanyName().equals(company.getCompanyName()) &&
                            pollution1.getYear().equals(pollution.getYear())))
                pollutionRepository.save(pollution);
        }
    }

    private Queue<String> csvParser (String line) {
        Matcher matcher = pattern.matcher(line);

        Queue<String> output = new LinkedList<>();

        while (matcher.find()) {
            String group1 = matcher.group(1);
            String group2 = matcher.group(2);

            if (group1 != null) {
                output.add(group1);
            } else {
                output.add(group2);
            }
        }
        return output;
    }

    public void uploadCompany(Company company) {
        companyRepository.save(company);
    }

    public void uploadPollutant(Pollutant pollutant) {
        pollutantRepository.save(pollutant);
    }

    public void uploadPollution(Pollution pollution) {
        pollution.setHq(calcService.calcHq(pollution.getPollutionConcentration(), pollution.getPollutant().getRfc()));
        pollution.setCr(calcService.calcCr(pollution.getPollutionConcentration(), pollution.getPollutant().getSf()));
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
        currentPollutant.setSf(pollutant.getSf());
        currentPollutant.setRfc(pollutant.getRfc());
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
        pollution.setHq(calcService.calcHq(pollution.getPollutionConcentration(), pollution.getPollutant().getRfc()));
        pollution.setCr(calcService.calcCr(pollution.getPollutionConcentration(), pollution.getPollutant().getSf()));
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
        return "Назва забрудника,Величина масової витрати,ГДВ,ГДК,SF,RfC\n"+getAllPollutant().stream()
                .map(o -> new String[]{o.getPollutantName(), String.valueOf(o.getMfr()),
                        String.valueOf(o.getElv()), String.valueOf(o.getTlv()),
                        String.valueOf(o.getSf()),String.valueOf(o.getRfc())})
                .map(this::convertToCSV).collect(Collectors.joining("\n"));
    }

    public String loadPollution () {
        return "Компанія,Забрудник,Розмір викидів,Концентрація,Дата,Hq,Cr\n"+getAllPollution().stream().map(o -> new String[]{o.getCompany().getCompanyName(),
                        o.getPollutant().getPollutantName(), String.valueOf(o.getPollutionValue()),
                        String.valueOf(o.getPollutionConcentration()), String.valueOf(o.getYear()),
                        String.valueOf(o.getHq()), String.valueOf(o.getCr())})
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
