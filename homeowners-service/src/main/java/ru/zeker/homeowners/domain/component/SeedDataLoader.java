package ru.zeker.homeowners.domain.component;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.zeker.homeowners.domain.model.entity.Company;
import ru.zeker.homeowners.domain.model.entity.PersonalAccount;
import ru.zeker.homeowners.domain.model.entity.PersonalAccountService;
import ru.zeker.homeowners.domain.model.entity.Property;
import ru.zeker.homeowners.domain.model.entity.Service;
import ru.zeker.homeowners.domain.model.enums.ServiceCode;
import ru.zeker.homeowners.repository.CompanyRepository;
import ru.zeker.homeowners.repository.PersonalAccountRepository;
import ru.zeker.homeowners.repository.PropertyRepository;
import ru.zeker.homeowners.repository.ServiceRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SeedDataLoader implements ApplicationRunner {

    private final CompanyRepository companyRepository;
    private final PropertyRepository propertyRepository;
    private final ServiceRepository serviceRepository;
    private final PersonalAccountRepository personalAccountRepository;

    private final Random random = new Random();

    @Override
    @Transactional
    public void run(ApplicationArguments args) {

        if (companyRepository.count() > 0) {
            return; // данные уже есть
        }

        // 1. Создаём услуги
        List<Service> services = createServices();
        serviceRepository.saveAll(services);
        Map<ServiceCode, Service> serviceMap = services.stream()
                .collect(Collectors.toMap(Service::getCode, s -> s));

        // 2. Создаём компании
        List<Company> companies = createCompanies();
        companyRepository.saveAll(companies);

        // 3. Создаём недвижимость
        List<Property> properties = createProperties();
        propertyRepository.saveAll(properties);

        // 4. Создаём лицевые счета и привязываем PersonalAccountService
        createPersonalAccountsForProperties(companies, properties, serviceMap);
    }

    private List<Service> createServices() {
        return Arrays.stream(ServiceCode.values())
                .map(code -> Service.builder()
                        .code(code)
                        .name(code.getDisplayName())
                        .build())
                .toList();
    }

    private List<Company> createCompanies() {
        List<Company> companies = new ArrayList<>();

        // 1 управляющая
        companies.add(
                Company.builder()
                        .name("Управляющая компания")
                        .isManagedByUs(true)
                        .build()
        );

        // остальные компании с разными услугами
        companies.add(
                Company.builder()
                        .name("Водоканал")
                        .isManagedByUs(false)
                        .build()
        );
        companies.add(
                Company.builder()
                        .name("Газовая компания")
                        .isManagedByUs(false)
                        .build()
        );
        companies.add(
                Company.builder()
                        .name("МусороВывоз")
                        .isManagedByUs(false)
                        .build()
        );

        // Можно добавить ещё компаний
        for (int i = 5; i <= 10; i++) {
            companies.add(
                    Company.builder()
                            .name("Компания " + i)
                            .isManagedByUs(false)
                            .build()
            );
        }

        return companies;
    }

    private List<Property> createProperties() {
        List<Property> properties = new ArrayList<>();

        for (int i = 1; i <= 30; i++) {
            properties.add(
                    Property.builder()
                            .street("Ленина")
                            .houseNumber(String.valueOf(i))
                            .flatNumber(String.valueOf(100 + i))
                            .build()
            );
        }
        return properties;
    }

    private void createPersonalAccountsForProperties(
            List<Company> companies,
            List<Property> properties,
            Map<ServiceCode, Service> serviceMap
    ) {
        for (Property property : properties) {
            for (Company company : companies) {

                List<ServiceCode> companyServices = getServicesForCompany(company);

                for (ServiceCode code : companyServices) {

                    PersonalAccount account = PersonalAccount.builder()
                            .personalNumber(generatePersonalNumber())
                            .company(company)
                            .property(property)
                            .build();

                    // Привязываем услуги (PersonalAccountService)
                    attachServices(account, code, serviceMap);

                    personalAccountRepository.save(account);
                }
            }
        }
    }

    private String generatePersonalNumber() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            sb.append(random.nextInt(10)); // цифра от 0 до 9
        }
        return sb.toString();
    }

    // Определяем услуги компании
    private List<ServiceCode> getServicesForCompany(Company company) {
        List<ServiceCode> codes = new ArrayList<>();
        if (company.isManagedByUs()) {
            codes.add(ServiceCode.MANAGEMENT);
        } else if (company.getName().toLowerCase().contains("вода")) {
            // вода может быть холодная + горячая
            codes.add(ServiceCode.COLD_WATER);
            if (random.nextInt(100) < 70) { // 70% горячая вода
                codes.add(ServiceCode.HOT_WATER);
            }
        } else if (company.getName().toLowerCase().contains("газ")) {
            codes.add(ServiceCode.GAS);
        } else if (company.getName().toLowerCase().contains("мусор")) {
            codes.add(ServiceCode.TRASH);
        } else {
            // для остальных компаний случайная услуга
            int r = random.nextInt(3);
            if (r == 0) codes.add(ServiceCode.COLD_WATER);
            else if (r == 1) codes.add(ServiceCode.TRASH);
            else codes.add(ServiceCode.GAS);
        }
        return codes;
    }

    private void attachServices(PersonalAccount account, ServiceCode code, Map<ServiceCode, Service> serviceMap) {
        Service service = serviceMap.get(code);
        if (service == null) return;

        PersonalAccountService pas = PersonalAccountService.builder()
                .personalAccount(account)
                .service(service)
                .build();

        account.getPersonalAccountServices().add(pas);
    }
}