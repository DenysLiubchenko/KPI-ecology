package ua.kpi.kpiecologyback.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ua.kpi.kpiecologyback.domain.Company;
import ua.kpi.kpiecologyback.domain.Pollutant;
import ua.kpi.kpiecologyback.domain.PollutantType;
import ua.kpi.kpiecologyback.domain.Pollution;
import ua.kpi.kpiecologyback.repository.CompanyRepository;
import ua.kpi.kpiecologyback.repository.PollutantRepository;
import ua.kpi.kpiecologyback.repository.PollutantTypeRepository;
import ua.kpi.kpiecologyback.repository.PollutionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class DataServiceTest {
    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private PollutantRepository pollutantRepository;
    @Mock
    private PollutionRepository pollutionRepository;
    @Mock
    private PollutantTypeRepository pollutantTypeRepository;
    @Mock
    private CalcService calcService;
    @InjectMocks
    private DataService dataService;

    @Test
    public void DataService_UploadCompanyByCsv_ReturnedListOfCompanies(){
        List<Company> companyList = generateCompanies(3);

        when(companyRepository.findAll()).thenReturn(companyList);
        when(companyRepository.saveAll(any(List.class))).thenReturn(companyList);

        List<Company> returnedList = dataService.uploadCompany("Назва компанії,Вид діяльності,Місцезнаходження\n" +
                "ПрАТ “ЮЖКОКС”,Виробництво коксу та коксопродуктів,м. Кам'янське\n" +
                "ДТЕК “Придніпровська ТЕС”,Виробництво та відпуск електричної та теплової енергії,м. Дніпро\n" +
                "ПрАТ “Дніпровський металургійний завод”,\"Виробництво чавуну, сталі та феросплавів\",м. Дніпро");

        assertNotNull(returnedList);
    }

    @Test
    public void DataService_UploadPollutantByCsv_ReturnedListOfCompanies(){
        List<Pollutant> pollutantList = generatePollutants(4);

        when(pollutantRepository.findAll()).thenReturn(pollutantList);
        when(pollutantRepository.saveAll(any(List.class))).thenReturn(pollutantList);
        when(pollutantTypeRepository.findById(any(Long.class))).thenReturn(Optional.of(new PollutantType()));

        List<Pollutant> returnedList = dataService.uploadPollutant("Назва забрудника,Величина масової витрати,ГДВ,ГДК,SF,RfC,Вид забруднення,Ставка податку\n" +
                "Оксид азоту,5000,500,0.06,\"2,04\",\"0,04\",1,\"2574,43\"\n" +
                "Діоксид сірки,5000,500,0.05,\"0,05\",\"0,08\",1,\"18413,24\"\n" +
                "Оксид вуглецю,5000,250,3.0,\"0,05\",0,1,\"96,99\"\n" +
                "Суспендованих тверді частинки,500,150,0.15,\"0,001\",\"0,05\",1,\"96,99\"\n");

        assertNotNull(returnedList);
    }

    @Test
    public void DataService_UploadPollutionByCsv_ReturnedListOfCompanies(){
        when(companyRepository.findAll()).thenReturn(generateCompanies(4));
        when(calcService.calcHq(any(Double.class), any(Double.class))).thenReturn(1.);
        when(calcService.calcCr(any(Double.class),any(Double.class))).thenReturn(1.);
        when(calcService.calcAirPenalty(any(Double.class), any(Double.class), any(Double.class))).thenReturn(1.);
        when(calcService.calcTax(any(Double.class), any(Double.class))).thenReturn(1.);
        when(pollutantRepository.findById(any(Long.class))).thenReturn(Optional.of(getPollutant(getPollutantType(1),1)));

        List<Pollution> returnedList = dataService.uploadPollution("Компанія,Забрудник,Розмір викидів,Концентрація,Дата\n" +
                "Test1,1,\"742,857\",\"0,04\",2021\n" +
                "Test1,2,\"360,162\",\"0,007\",2021\n" +
                "Test1,3,\"568,133\",3,2021\n" +
                "Test1,4,\"193,483\",\"0,3\",2021\n" +
                "Test2,1,\"2209,905\",\"0,02\",2021\n" +
                "Test2,2,\"12548,82\",\"0,041\",2021\n" +
                "Test2,3,\"148,094\",0,2021\n" +
                "Test2,4,\"185,779\",\"0,114\",2021\n" +
                "Test3,1,\"655,134\",\"0,05\",2021\n" +
                "Test3,2,\"325,618\",\"0,011\",2021\n" +
                "Test3,3,\"1852,905\",2,2021\n" +
                "Test3,4,\"536,681\",\"0,2\",2021");

        assertNotNull(returnedList);
    }

    @Test
    public void DataService_UploadPollution_ReturnedListOfCompanies(){
        Pollution pollution = createPollution(1);

        when(companyRepository.findAll()).thenReturn(generateCompanies(3));
        when(pollutionRepository.findById(any(Long.class))).thenReturn(Optional.of(pollution));
        when(companyRepository.findById(any(Long.class))).thenReturn(Optional.of(pollution.getCompany()));
        when(pollutantRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(pollution.getPollutant()));
        when(pollutionRepository.save(any(Pollution.class))).thenReturn(pollution);
        when(calcService.calcHq(any(Double.class), any(Double.class))).thenReturn(1.);
        when(calcService.calcCr(any(Double.class),any(Double.class))).thenReturn(1.);
        when(calcService.calcAirPenalty(any(Double.class), any(Double.class), any(Double.class))).thenReturn(1.);
        when(calcService.calcTax(any(Double.class), any(Double.class))).thenReturn(1.);

        Pollution returned = dataService.uploadPollution(pollution);
        assertNotNull(returned);
    }

    @Test
    public void DataService_UpdatePollution_ReturnedListOfCompanies(){
        Pollution pollution = createPollution(1);

        when(companyRepository.findAll()).thenReturn(generateCompanies(3));
        when(pollutionRepository.findById(any(Long.class))).thenReturn(Optional.of(pollution));
        when(companyRepository.findById(any(Long.class))).thenReturn(Optional.of(pollution.getCompany()));
        when(pollutantRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(pollution.getPollutant()));
        when(pollutionRepository.save(any(Pollution.class))).thenReturn(pollution);
        when(calcService.calcHq(any(Double.class), any(Double.class))).thenReturn(1.);
        when(calcService.calcCr(any(Double.class),any(Double.class))).thenReturn(1.);
        when(calcService.calcAirPenalty(any(Double.class), any(Double.class), any(Double.class))).thenReturn(1.);
        when(calcService.calcTax(any(Double.class), any(Double.class))).thenReturn(1.);

        Pollution returned = dataService.updatePollution(pollution);
        assertNotNull(returned);
    }
    @Test
    public void DataService_LoadPollution_ReturnedListOfCompanies(){
        Pollution pollution = createPollution(1);

        when(pollutionRepository.findAllBy()).thenReturn(List.of(pollution));

        String returned = dataService.loadPollution();
        System.out.println(returned);
        assertEquals("Компанія,Забрудник,Вид забруднення,Розмір викидів,Концентрація,Дата,Hq,Cr,Штраф,Податок\n" +
                "Test1,Test1,Test1,1.0,1.0,null,0.1813033333333333,0.1813033333333333,0.0,2.8E-4", returned);
    }

    private List<Company> generateCompanies (int num) {
        List<Company> companyList = new ArrayList<>();
        for (int i = 1; i <= num; i++) {
            companyList.add(getCompany(i));
        }
        return companyList;
    }

    private List<Pollutant> generatePollutants(int num) {
        List<Pollutant> pollutantList = new ArrayList<>();
        for (int i = 1; i <= num; i++) {
            pollutantList.add(getPollutant(getPollutantType(i), i));
        }
        return pollutantList;
    }

    private List<Pollution> generatePollution(int num) {
        List<Pollution> pollutionList = new ArrayList<>();
        for (int i = 1; i <= num; i++) {
            pollutionList.add(createPollution(i));
        }
        return pollutionList;
    }
    private Pollution createPollution (int i) {
        Company company = getCompany(i);
        PollutantType pollutantType = getPollutantType(i);
        Pollutant pollutant = getPollutant(pollutantType, i);
        return getPollution(company, pollutant, i);
    }

    private Company getCompany (int i) {
        Company company = new Company();
        company.setId((long)i);
        company.setCompanyName("Test"+i);
        company.setLocation("Test"+i);
        company.setActivity("Test"+i);
        return company;
    }
    private PollutantType getPollutantType (int i) {
        PollutantType pollutantType = new PollutantType();
        pollutantType.setId((long)i);
        pollutantType.setPollutantTypeName("Test"+i);
        return pollutantType;
    }

    private Pollutant getPollutant (PollutantType pollutantType, int i) {
        Pollutant pollutant = new Pollutant();
        pollutant.setId((long)i);
        pollutant.setPollutantType(pollutantType);
        pollutant.setPollutantName("Test"+i);
        pollutant.setMfr(1);
        pollutant.setElv(1);
        pollutant.setTlv(1.);
        pollutant.setSf(1.);
        pollutant.setRfc(1.);
        pollutant.setTaxRate(1.);
        return pollutant;
    }

    private Pollution getPollution(Company company, Pollutant pollutant, int i) {
        Pollution pollution = new Pollution();
        pollution.setId((long)i);
        pollution.setCompany(company);
        pollution.setPollutant(pollutant);
        pollution.setPollutionValue(1.);
        pollution.setPollutionConcentration(1.);
        CalcService calcService = new CalcService();
        pollution.setHq(calcService.calcHq(pollution.getPollutionConcentration(), pollution.getPollutant().getRfc()));
        pollution.setCr(calcService.calcCr(pollution.getPollutionConcentration(), pollution.getPollutant().getSf()));
        pollution.setPenalty(calcService.calcAirPenalty(pollution.getPollutionValue(), pollution.getPollutionValue(), pollution.getPollutant().getTlv()));
        pollution.setTax(calcService.calcTax(pollution.getPollutant().getMfr(), pollution.getPollutant().getTaxRate()));
        return pollution;
    }
}
