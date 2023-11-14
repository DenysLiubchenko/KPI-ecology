package ua.kpi.kpiecologyback.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import ua.kpi.kpiecologyback.domain.*;
import ua.kpi.kpiecologyback.repository.*;

import java.util.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DataService {
    private final CompanyRepository companyRepository;
    private final PollutantRepository pollutantRepository;
    private final PollutantTypeRepository pollutantTypeRepository;
    private final PollutionRepository pollutionRepository;
    private final EmergencyRepository emergencyRepository;
    private final CalcService calcService;
    private final Pattern pattern = Pattern.compile("\"(.*?)\"|([^,]+)");
    @Autowired
    public DataService(CompanyRepository companyRepository, PollutantRepository pollutantRepository, PollutantTypeRepository pollutantTypeRepository, PollutionRepository pollutionRepository, EmergencyRepository emergencyRepository, CalcService calcService) {
        this.companyRepository = companyRepository;
        this.pollutantRepository = pollutantRepository;
        this.pollutantTypeRepository = pollutantTypeRepository;
        this.pollutionRepository = pollutionRepository;
        this.emergencyRepository = emergencyRepository;
        this.calcService = calcService;
    }

    public List<Company> getAllCompany() {
        return companyRepository.findAll();
    }

    public List<Pollutant> getAllPollutant() {
        return pollutantRepository.findAll();
    }
    public List<PollutantType> getAllPollutantType() {
        return pollutantTypeRepository.findAll();
    }

    public List<Pollution> getAllPollution() {
        return pollutionRepository.findAllBy();
    }

    public List<Emergency> getAllEmergency() {
        return emergencyRepository.findAll();
    }
    public List<Company> uploadCompany(String data) {
        Scanner scanner = new Scanner(data);
        scanner.nextLine();
        List<String> companyNames = companyRepository.findAll().stream().map(Company::getCompanyName).toList();
        List<Company> companyList = new ArrayList<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            Queue<String> values = csvParser(line);

            Company company = new Company();

            company.setCompanyName(Objects.requireNonNull(values.poll()).trim());

            company.setActivity(Objects.requireNonNull(values.poll()).trim());

            company.setLocation(Objects.requireNonNull(values.poll()).trim());
            if (!companyNames.contains(company.getCompanyName())) {
                companyList.add(company);
            }
        }
        return companyRepository.saveAll(companyList);
    }

    public List<Pollutant> uploadPollutant(String data) {
        Scanner scanner = new Scanner(data);
        scanner.nextLine();
        List<Pollutant> pollutants = pollutantRepository.findAll();
        List<Pollutant> pollutantList = new ArrayList<>();
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

            pollutant.setPollutantType(pollutantTypeRepository.findById(Long.parseLong(Objects.requireNonNull(values.poll())))
                    .orElseThrow(()-> new HttpClientErrorException(HttpStatusCode.valueOf(400))));

            pollutant.setTaxRate(Double.parseDouble(Objects.requireNonNull(values.poll()).replace(",", ".")));

            if (!pollutants.contains(pollutant)) {

                pollutantList.add(pollutant);
            }
        }
        return pollutantRepository.saveAll(pollutantList);
    }

    public List<Pollution> uploadPollution(String data) {
        Scanner scanner = new Scanner(data);
        scanner.nextLine();
        List<Pollution> pollutions = pollutionRepository.findAllBy();
        List<Company> companies = companyRepository.findAll();
        List<Pollution> pollutionList = new ArrayList<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            Queue<String> values = csvParser(line);

            Pollution pollution = new Pollution();

            String companyName = Objects.requireNonNull(values.poll()).trim();
            Company company = companies.stream().filter(company1 -> company1.getCompanyName().equals(companyName))
                    .findFirst().orElseThrow(()-> new HttpClientErrorException(HttpStatusCode.valueOf(400)));
            pollution.setCompany(company);

            Pollutant pollutant = pollutantRepository.findById(Long.parseLong(Objects.requireNonNull(values.poll())))
                    .orElseThrow(()-> new HttpClientErrorException(HttpStatusCode.valueOf(400)));
            pollution.setPollutant(pollutant);

            pollution.setPollutionValue(Double.parseDouble(Objects.requireNonNull(values.poll())
                    .replace(",", ".")));

            pollution.setPollutionConcentration(Double.parseDouble(Objects.requireNonNull(values.poll())
                    .replace(",", ".")));

            pollution.setYear(Integer.parseInt(Objects.requireNonNull(values.poll())));

            pollution.setHq(calcService.calcHq(pollution.getPollutionConcentration(), pollutant.getRfc()));
            pollution.setCr(calcService.calcCr(pollution.getPollutionConcentration(), pollutant.getSf()));
            pollution.setPenalty(calcService.calcAirPenalty(pollution.getPollutant().getMfr(), pollution.getPollutionValue(), pollution.getPollutant().getTlv()));
            pollution.setTax(calcService.calcTax(pollution.getPollutant().getMfr(), pollution.getPollutant().getTaxRate()));
            if (pollutions.stream()
                    .noneMatch(pollution1 ->
                            pollution1.getPollutant().getPollutantName().equals(pollutant.getPollutantName()) &&
                            pollution1.getCompany().getCompanyName().equals(company.getCompanyName()) &&
                            pollution1.getYear().equals(pollution.getYear()))) {
                pollutionList.add(pollution);
            }
        }
        return pollutionRepository.saveAll(pollutionList);
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

    public Company uploadCompany(Company company) {
        return companyRepository.save(company);
    }

    public Pollutant uploadPollutant(Pollutant pollutant) {
        return pollutantRepository.save(pollutant);
    }
    public PollutantType uploadPollutantType(PollutantType pollutantType) {
        return pollutantTypeRepository.save(pollutantType);
    }

    public Pollution uploadPollution(Pollution pollution) {
        pollution.setPollutant(pollutantRepository.findById(pollution.getPollutant().getId())
                .orElseThrow(()-> new HttpClientErrorException(HttpStatusCode.valueOf(400))));
        pollution.setCompany(companyRepository.findById(pollution.getCompany().getId())
                .orElseThrow(()-> new HttpClientErrorException(HttpStatusCode.valueOf(400))));
        pollution.setHq(calcService.calcHq(pollution.getPollutionConcentration(), pollution.getPollutant().getRfc()));
        pollution.setCr(calcService.calcCr(pollution.getPollutionConcentration(), pollution.getPollutant().getSf()));
        pollution.setPenalty(calcService.calcAirPenalty(pollution.getPollutionValue(), pollution.getPollutionValue(), pollution.getPollutant().getTlv()));
        pollution.setTax(calcService.calcTax(pollution.getPollutant().getMfr(), pollution.getPollutant().getTaxRate()));
        return pollutionRepository.save(pollution);
    }

    public Emergency uploadEmergency (Emergency emergency) {
        emergency.setPollutant(pollutantRepository.findById(emergency.getPollutant().getId())
                .orElseThrow(()-> new HttpClientErrorException(HttpStatusCode.valueOf(400))));
        emergency.setCompany(companyRepository.findById(emergency.getCompany().getId())
                .orElseThrow(()-> new HttpClientErrorException(HttpStatusCode.valueOf(400))));
        calcService.calcEmergencyLoses(emergency);
        return emergencyRepository.save(emergency);
    }

    public Company updateCompany(Company company) {
        Company currentCompany = companyRepository.findById(company.getId())
                .orElseThrow(()->new HttpClientErrorException(HttpStatusCode.valueOf(404)));
        currentCompany.setCompanyName(Optional.ofNullable(company.getCompanyName()).orElse(currentCompany.getCompanyName()));
        currentCompany.setActivity(Optional.ofNullable(company.getActivity()).orElse(currentCompany.getActivity()));
        currentCompany.setLocation(Optional.ofNullable(company.getLocation()).orElse(currentCompany.getLocation()));
        return companyRepository.save(currentCompany);
    }

    public Pollutant updatePollutant(Pollutant pollutant) {
        Pollutant currentPollutant = pollutantRepository.findById(pollutant.getId())
                .orElseThrow(()->new HttpClientErrorException(HttpStatusCode.valueOf(404)));
        currentPollutant.setPollutantName(Optional.ofNullable(pollutant.getPollutantName()).orElse(currentPollutant.getPollutantName()));
        currentPollutant.setMfr(Optional.ofNullable(pollutant.getMfr()).orElse(currentPollutant.getMfr()));
        currentPollutant.setElv(Optional.ofNullable(pollutant.getElv()).orElse(currentPollutant.getElv()));
        currentPollutant.setTlv(Optional.ofNullable(pollutant.getTlv()).orElse(currentPollutant.getTlv()));
        currentPollutant.setSf(pollutant.getSf());
        currentPollutant.setRfc(pollutant.getRfc());
        if (pollutant.getPollutantType()!=null)
            currentPollutant.setPollutantType(pollutantTypeRepository.findById(Optional.ofNullable(pollutant.getPollutantType().getId())
                    .orElse(currentPollutant.getPollutantType().getId())).get());
        currentPollutant.setTaxRate(pollutant.getTaxRate());
        return pollutantRepository.save(currentPollutant);
    }

    public PollutantType updatePollutantType(PollutantType pollutantType) {
        PollutantType currentPollutantType = pollutantTypeRepository.findById(pollutantType.getId())
                .orElseThrow(()->new HttpClientErrorException(HttpStatusCode.valueOf(404)));
        currentPollutantType.setPollutantTypeName(Optional.ofNullable(pollutantType.getPollutantTypeName()).orElse(currentPollutantType.getPollutantTypeName()));
        return pollutantTypeRepository.save(currentPollutantType);
    }

    public Pollution updatePollution(Pollution pollution) {
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
        currentPollution.setHq(calcService.calcHq(currentPollution.getPollutionConcentration(), currentPollution.getPollutant().getRfc()));
        currentPollution.setCr(calcService.calcCr(currentPollution.getPollutionConcentration(), currentPollution.getPollutant().getSf()));
        currentPollution.setPenalty(calcService.calcAirPenalty(currentPollution.getPollutionValue(), currentPollution.getPollutionValue(), currentPollution.getPollutant().getTlv()));
        currentPollution.setTax(calcService.calcTax(currentPollution.getPollutant().getMfr(), currentPollution.getPollutant().getTaxRate()));
        return pollutionRepository.save(currentPollution);
    }

    public Emergency updateEmergency (Emergency emergency) {
        Emergency currentEmergency = emergencyRepository.findById(emergency.getId())
                .orElseThrow(()->new HttpClientErrorException(HttpStatusCode.valueOf(404)));
        if (emergency.getCompany()!=null)
            currentEmergency.setCompany(companyRepository.findById(Optional.ofNullable(emergency.getCompany().getId())
                    .orElse(currentEmergency.getCompany().getId())).get());
        if (emergency.getPollutant()!=null)
            currentEmergency.setPollutant(pollutantRepository.findById(Optional.ofNullable(emergency.getPollutant().getId())
                    .orElse(currentEmergency.getPollutant().getId())).get());
        currentEmergency.setPeopleMinorInjury(Optional.ofNullable(emergency.getPeopleMinorInjury()).orElse(currentEmergency.getPeopleMinorInjury()));
        currentEmergency.setPeopleMinorInjury(Optional.ofNullable(emergency.getPeopleSeriousInjury()).orElse(currentEmergency.getPeopleSeriousInjury()));
        currentEmergency.setPeopleDisability(Optional.ofNullable(emergency.getPeopleDisability()).orElse(currentEmergency.getPeopleDisability()));
        currentEmergency.setPeopleDead(Optional.ofNullable(emergency.getPeopleDead()).orElse(currentEmergency.getPeopleDead()));
        currentEmergency.setPollutionValue(Optional.ofNullable(emergency.getPollutionValue()).orElse(currentEmergency.getPollutionValue()));
        currentEmergency.setPollutionConcentration(Optional.ofNullable(emergency.getPollutionConcentration()).orElse(currentEmergency.getPollutionConcentration()));
        calcService.calcEmergencyLoses(currentEmergency);
        return emergencyRepository.save(currentEmergency);
    }

    public void deleteCompany(List<Long> ids) {
        ids.forEach(companyRepository::deleteById);
    }
    public void deletePollutant(List<Long> ids) {
        ids.forEach(pollutantRepository::deleteById);
    }
    public void deletePollutantType(List<Long> ids) {
        ids.forEach(pollutantTypeRepository::deleteById);
    }
    public void deletePollution(List<Long> ids) {
        pollutionRepository.deleteAllByIdInBatch(ids);
    }
    public void deleteEmergency(List<Long> ids) {
        emergencyRepository.deleteAllByIdInBatch(ids);
    }


    public String loadCompany () {
        return "Назва компанії,Вид діяльності,Місцезнаходження\n"+getAllCompany().stream()
                .map(o -> new String[]{o.getCompanyName(), o.getActivity(), o.getLocation()})
                .map(this::convertToCSV).collect(Collectors.joining("\n"));
    }

    public String loadPollutant () {
        return "Назва забрудника,Величина масової витрати,ГДВ,ГДК,SF,RfC,Вид забруднення,Ставка податку\n"+getAllPollutant().stream()
                .map(o -> new String[]{o.getPollutantName(), String.valueOf(o.getMfr()),
                        String.valueOf(o.getElv()), String.valueOf(o.getTlv()),
                        String.valueOf(o.getSf()),String.valueOf(o.getRfc()),
                        String.valueOf(o.getPollutantType().getPollutantTypeName()),
                        String.valueOf(o.getTaxRate())})
                .map(this::convertToCSV).collect(Collectors.joining("\n"));
    }

    public String loadPollution () {
        return "Компанія,Забрудник,Вид забруднення,Розмір викидів,Концентрація,Дата,Hq,Cr,Штраф,Податок\n"+getAllPollution().stream()
                .map(o -> new String[]{o.getCompany().getCompanyName(), o.getPollutant().getPollutantName(),
                        String.valueOf(o.getPollutant().getPollutantType().getPollutantTypeName()),String.valueOf(o.getPollutionValue()),
                        String.valueOf(o.getPollutionConcentration()), String.valueOf(o.getYear()),
                        String.valueOf(o.getHq()), String.valueOf(o.getCr()), String.valueOf(o.getPenalty()),
                        String.valueOf(o.getTax())})
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
