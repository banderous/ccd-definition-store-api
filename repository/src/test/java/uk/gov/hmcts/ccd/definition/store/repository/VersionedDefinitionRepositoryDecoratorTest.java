package uk.gov.hmcts.ccd.definition.store.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.ccd.definition.store.repository.entity.CaseTypeEntity;
import uk.gov.hmcts.ccd.definition.store.repository.entity.JurisdictionEntity;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        SanityCheckApplication.class,
        TestConfiguration.class
})
@TestPropertySource(locations = "classpath:test.properties")
@Transactional
public class VersionedDefinitionRepositoryDecoratorTest {

    @Autowired
    private CaseTypeRepository exampleRepository;

    @Autowired
    private JurisdictionRepository jurisdictionRepository;

    private VersionedDefinitionRepositoryDecorator<CaseTypeEntity, Integer> versionedCaseTypeRepository;
    private VersionedDefinitionRepositoryDecorator<JurisdictionEntity, Integer> versionedJurisdictionRepository;
    private JurisdictionEntity jurisdiction;

    @Before
    public void setup() {
        versionedJurisdictionRepository = new VersionedDefinitionRepositoryDecorator<>(jurisdictionRepository);

        final JurisdictionEntity j = new JurisdictionEntity();
        j.setReference("jurisdiction");
        j.setName("jname");
        jurisdiction = versionedJurisdictionRepository.save(j);
    }

    @Test
    public void saveNewCaseTypeAssignsAVersion() {
        final CaseTypeEntity caseType = new CaseTypeEntity();
        caseType.setReference("id");
        caseType.setName("name");
        caseType.setJurisdiction(jurisdiction);
        caseType.setSecurityClassification(SecurityClassification.PUBLIC);

        versionedCaseTypeRepository = new VersionedDefinitionRepositoryDecorator<>(exampleRepository);

        final CaseTypeEntity saved = versionedCaseTypeRepository.save(caseType);

        CaseTypeEntity retrievedCaseType = versionedCaseTypeRepository.findOne(saved.getId());
        assertThat(retrievedCaseType.getVersion(), is(1));

        final CaseTypeEntity caseType2 = new CaseTypeEntity();
        caseType2.setReference("id");
        caseType2.setName("name");
        caseType2.setJurisdiction(jurisdiction);
        caseType2.setSecurityClassification(SecurityClassification.PUBLIC);

        CaseTypeEntity savedCaseType2 = versionedCaseTypeRepository.save(caseType2);
        assertThat(savedCaseType2.getVersion(), is(2));
    }
}
