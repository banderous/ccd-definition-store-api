package uk.gov.hmcts.ccd.definition.store.rest.endpoint;

import com.microsoft.azure.storage.StorageException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.ccd.definition.store.rest.model.ImportAudit;
import uk.gov.hmcts.ccd.definition.store.rest.service.AzureImportAuditsClient;

import java.util.Collection;

@RestController
@Api(value = "/api/import-audits")
@RequestMapping(value = "/api")
@Slf4j
class ImportAuditController {

    private final AzureImportAuditsClient azureImportAuditsClient;

    @Autowired
    ImportAuditController(@Autowired(required = false) final AzureImportAuditsClient azureImportAuditsClient) {
        this.azureImportAuditsClient = azureImportAuditsClient;
    }

    @RequestMapping(value = "/import-audits", method = RequestMethod.GET, produces = {"application/json"})
    @ApiOperation(value = "Fetches import audits")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Import audits")
    })
    Collection<ImportAudit> fetchAllAudits() throws StorageException {
        log.info("AzureImportAuditsClient is {}", azureImportAuditsClient);
        if (null != azureImportAuditsClient) {
            return azureImportAuditsClient.fetchAllImportAudits();
        } else {
            return null;
        }
    }
}
