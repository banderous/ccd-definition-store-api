package uk.gov.hmcts.ccd.definition.store.repository;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import uk.gov.hmcts.ccd.definition.store.repository.am.AmPersistenceReadSource;
import uk.gov.hmcts.ccd.definition.store.repository.am.AmPersistenceSwitch;
import uk.gov.hmcts.ccd.definition.store.repository.am.AmPersistenceWriteDestination;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;

import static uk.gov.hmcts.ccd.definition.store.repository.am.AmPersistenceReadSource.FROM_AM;
import static uk.gov.hmcts.ccd.definition.store.repository.am.AmPersistenceReadSource.FROM_CCD;
import static uk.gov.hmcts.ccd.definition.store.repository.am.AmPersistenceWriteDestination.TO_AM;
import static uk.gov.hmcts.ccd.definition.store.repository.am.AmPersistenceWriteDestination.TO_BOTH;
import static uk.gov.hmcts.ccd.definition.store.repository.am.AmPersistenceWriteDestination.TO_CCD;

@Named
@Singleton
public class AppConfigBasedAmPersistenceSwitch implements AmPersistenceSwitch {

    private final Map<String, AmPersistenceWriteDestination> caseTypesToWriteModes = Maps.newHashMap();

    private final Map<String, AmPersistenceReadSource> caseTypesToReadModes = Maps.newHashMap();

    public AppConfigBasedAmPersistenceSwitch(final ApplicationParams appParams) {

        mapCaseTypeVsSwitchValueWith(appParams.getCaseTypesWithAmWrittenOnlyToCcd(),
                caseTypesToWriteModes, TO_CCD);

        mapCaseTypeVsSwitchValueWith(appParams.getCaseTypesWithAmWrittenOnlyToAm(),
                caseTypesToWriteModes, TO_AM);

        mapCaseTypeVsSwitchValueWith(appParams.getCaseTypesWithAmWrittenToBoth(),
                caseTypesToWriteModes, TO_BOTH);

        mapCaseTypeVsSwitchValueWith(appParams.getCaseTypesWithAmReadFromCcd(), caseTypesToReadModes, FROM_CCD);

        mapCaseTypeVsSwitchValueWith(appParams.getCaseTypesWithAmReadFromAm(), caseTypesToReadModes, FROM_AM);

    }

    @Override
    public AmPersistenceWriteDestination getWriteDataSourceFor(String caseType) {
        return caseTypesToWriteModes.getOrDefault(caseType.toUpperCase(), TO_CCD);
    }

    @Override
    public AmPersistenceReadSource getReadDataSourceFor(String caseType) {
        return caseTypesToReadModes.getOrDefault(caseType.toUpperCase(), FROM_CCD);
    }

    private <T> void mapCaseTypeVsSwitchValueWith(List<String> caseTypesConfigured, Map<String, T> map, T value) {
        caseTypesConfigured.forEach(caseType -> {
            if (!StringUtils.isEmpty(caseType)) {
                    map.put(caseType.toUpperCase(), value);
            }
        });
    }
}
