package tech.inovasoft.inevolving.ms.tasks.integration;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import tech.inovasoft.inevolving.ms.tasks.integration.dto.RequestNewObjectiveDTO;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ObjectivesService {

    @Test
    public void integrationTest_Ok() {

        UUID idUser = UUID.randomUUID();

        UUID idObjective = addObjective(idUser);

        // Cria a especificação da requisição
        RequestSpecification requestSpecification = given()
                .contentType(ContentType.JSON);

        // Faz a requisição GET e armazena a resposta
        ValidatableResponse response = requestSpecification.when()
                .get("http://localhost:8080/ms/objectives/" + idObjective + "/" + idUser)//TODO: Mudar para o endereço do container e esconder ele em variaveis de ambiente
                .then();

        // Valida a resposta
        response.assertThat().statusCode(200).and()
                .body("id", equalTo(String.valueOf(idObjective))).and()
                .body("idUser", equalTo(String.valueOf(idUser)));

    }


    private UUID addObjective(UUID idUser) {
        String nameObjective = "Teste de Integração";
        String descriptionObjective = "Teste de Integração";

        // Cria a especificação da requisição
        RequestSpecification requestSpecification = given()
                .contentType(ContentType.JSON);

        // Faz a requisição GET e armazena a resposta
        ValidatableResponse response = requestSpecification
                .body(new RequestNewObjectiveDTO(nameObjective, descriptionObjective, idUser))
                .when()
                .post("http://localhost:8080/ms/objectives") //TODO: Mudar para o endereço do container e esconder ele em variaveis de ambiente
                .then();


        // Valida a resposta
        response.assertThat().statusCode(200);

        return UUID.fromString(response.extract().jsonPath().get("id"));
    }

}
