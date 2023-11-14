package ua.kpi.kpiecologyback.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.catalina.core.ApplicationContext;
import org.apache.naming.factory.BeanFactory;
import org.junit.jupiter.api.*;
import org.junit.runner.OrderWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationContextFactory;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.crossstore.ChangeSetPersister;
import ua.kpi.kpiecologyback.domain.Company;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class CompanyRepositoryTest {
    @Autowired
    private CompanyRepository companyRepository;


    @Test
    public void CompanyRepository_Save_ReturnSavedCompany () {
        Company company = new Company();
        company.setCompanyName("Test Company");
        company.setLocation("Test Location");
        company.setActivity("Test Activity");

        Company returnedCompany = companyRepository.save(company);

        assertNotNull(returnedCompany);
        assertTrue(returnedCompany.getId() > 0);
        assertEquals(company.getCompanyName(), returnedCompany.getCompanyName());
        assertEquals(company.getLocation(), returnedCompany.getLocation());
        assertEquals(company.getActivity(), returnedCompany.getActivity());
    }

    @Test
    public void CompanyRepository_Update_ReturnSavedCompany () {
        Company company = new Company();
        company.setCompanyName("Test Company");
        company.setLocation("Test Location");
        company.setActivity("Test Activity");

        companyRepository.save(company);
        company.setId(1L);
        company.setCompanyName("Updated Test Company");
        Company returnedCompany = companyRepository.save(company);

        assertNotNull(returnedCompany);
        assertTrue(returnedCompany.getId() > 0);
        assertEquals(company.getCompanyName(), returnedCompany.getCompanyName());
        assertEquals(company.getLocation(), returnedCompany.getLocation());
        assertEquals(company.getActivity(), returnedCompany.getActivity());
    }

    @Test
    public void CompanyRepository_FindAll_ReturnAllSavedCompanies () {
        List<Company> companies = generateCompanies(4);

        companyRepository.saveAll(companies);
        List<Company> returnedCompanies = companyRepository.findAll();

        assertNotNull(returnedCompanies);
        assertEquals(companies.size(), returnedCompanies.size());
        for (int i = 0; i < companies.size(); i++) {
            Company company = companies.get(i);
            Company returnedCompany = returnedCompanies.get(i);
            assertTrue(returnedCompany.getId() > 0);
            assertEquals(company.getCompanyName(), returnedCompany.getCompanyName());
            assertEquals(company.getLocation(), returnedCompany.getLocation());
            assertEquals(company.getActivity(), returnedCompany.getActivity());
        }
    }

    @Test
    public void CompanyRepository_FindById_ReturnAllSavedCompanies () {
        List<Company> companies = generateCompanies(4);

        List<Company> companyList = companyRepository.saveAll(companies);
        Company company = companies.get(0);
        Company returnedCompany = companyRepository.findById(companyList.get(0).getId()).orElse(null);

        assertNotNull(returnedCompany);
        assertEquals(companyList.get(0).getId(), returnedCompany.getId());
        assertEquals(company.getCompanyName(), returnedCompany.getCompanyName());
        assertEquals(company.getLocation(), returnedCompany.getLocation());
        assertEquals(company.getActivity(), returnedCompany.getActivity());
    }

    @Test
    public void CompanyRepository_DeleteAllByIdInBatch_ReturnAllSavedCompanies () {
        List<Company> companies = generateCompanies(4);

        List<Company> companyList = companyRepository.saveAll(companies);
        System.out.println(companyList.get(1).getId());
        companyRepository.deleteAllByIdInBatch(List.of(companyList.get(1).getId(), companyList.get(2).getId()));
        List<Company> returnedList = companyRepository.findAll();
        assertEquals(2, companyList.size() - returnedList.size());
    }

    private List<Company> generateCompanies (int num) {
        List<Company> companyList = new ArrayList<>();
        for (int i = 1; i <= num; i++) {
            Company company = new Company();
            company.setCompanyName("Test Company " + i);
            company.setLocation("Test Location " + i);
            company.setActivity("Test Activity " + i);
            companyList.add(company);
        }
        return companyList;
    }
}
