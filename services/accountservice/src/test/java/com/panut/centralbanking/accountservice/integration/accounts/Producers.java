package com.panut.centralbanking.accountservice.integration.accounts;

import com.panut.centralbanking.accountservice.application.kafka.templates.JustProcessedBalanceChangeEventsProducer;

import static org.mockito.Mockito.*;

class Producers {


    public static void skip(JustProcessedBalanceChangeEventsProducer producer) {
        doNothing().when(producer).unmandatorySend(anySet());

    }
}
