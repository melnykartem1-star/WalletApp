package org.my.walletapp;

import org.springframework.boot.SpringApplication;

public class TestWalletAppApplication {

    public static void main(String[] args) {
        SpringApplication.from(WalletAppApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
