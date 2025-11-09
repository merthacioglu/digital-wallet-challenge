package org.mhejaju.digitalwalletchallenge;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "Digital Wallet Challenge REST API Documentation",
                description = "ING Hubs Turkiye Digital Wallet Challenge REST API Documentation",
                version = "v1",
                contact = @Contact(
                        name = "Mert Hejaju",
                        email = "mhacioglu96@gmail.com"
                ),
                license = @License(
                        name = "Apache 2.0"
                )
        ),
        externalDocs = @ExternalDocumentation(
                description = "Digital Wallet Challenge REST API Documentation",
                url = "https://github.com/merthacioglu/digital-wallet-challenge"
        )
)
public class DigitalWalletChallengeApplication {

    public static void main(String[] args) {
        SpringApplication.run(DigitalWalletChallengeApplication.class, args);
    }

}
