package com.xujn.minispring.test.transaction.phase1;

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
