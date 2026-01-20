package tech.inovasoft.inevolving.ms.tasks.service.client.Auth_For_MService;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import tech.inovasoft.inevolving.ms.tasks.service.client.Auth_For_MService.dto.AuthAuthenticationRequest;
import tech.inovasoft.inevolving.ms.tasks.service.client.Auth_For_MService.dto.AuthLoginResponse;

//@FeignClient(name = "auth-service", url = "https://api.inevolving.inovasoft.tech/Auth-For-MService/auth/ms/authentication/login")
@FeignClient(name = "auth-service", url = "http://auth-for-m-service:2723/auth/ms/authentication/login")
public interface AuthForMServiceClient {

    @PostMapping("/{microServiceNameReceiver}")
    AuthLoginResponse login(@PathVariable String microServiceNameReceiver, @RequestBody AuthAuthenticationRequest request);

}