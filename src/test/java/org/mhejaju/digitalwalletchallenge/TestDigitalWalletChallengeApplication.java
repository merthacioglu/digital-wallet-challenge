package org.mhejaju.digitalwalletchallenge;

import org.springframework.boot.SpringApplication;

public class TestDigitalWalletChallengeApplication {

    public static void main(String[] args) {
        SpringApplication.from(DigitalWalletChallengeApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
