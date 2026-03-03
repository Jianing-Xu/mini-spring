package com.xujn.minispring.examples.transaction.phase1.fixture;

import com.xujn.minispring.context.annotation.Component;
import com.xujn.minispring.tx.transaction.TransactionResource;
import com.xujn.minispring.tx.transaction.TransactionResourceFactory;

@Component
public class RecordingTransactionResourceFactory implements TransactionResourceFactory {

    @Override
    public TransactionResource createResource() {
        return new RecordingTransactionResource();
    }
}
