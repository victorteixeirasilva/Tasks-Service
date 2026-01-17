package tech.inovasoft.inevolving.ms.tasks.service.client.Auth_For_MService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tech.inovasoft.inevolving.ms.tasks.service.client.Auth_For_MService.MicroServices;
import tech.inovasoft.inevolving.ms.tasks.service.client.Auth_For_MService.dto.AuthAuthenticationRequest;
import tech.inovasoft.inevolving.ms.tasks.service.client.Auth_For_MService.dto.AuthLoginResponse;

import static tech.inovasoft.inevolving.ms.tasks.service.client.Auth_For_MService.MicroServices.TASKS_SERVICE;

@Service
public class TokenCache {

    @Value("${inevolving.super.secret}")
    private String SUPER_SECRET;

    @Autowired
    private AuthForMServiceClient authForMServiceClient;

    public String getToken(MicroServices microServices){
        AuthAuthenticationRequest request = new AuthAuthenticationRequest(TASKS_SERVICE.getValue(), SUPER_SECRET);
        AuthLoginResponse response = authForMServiceClient.login(microServices.getValue(), request);
        return response.BearerToken();
    }

}
