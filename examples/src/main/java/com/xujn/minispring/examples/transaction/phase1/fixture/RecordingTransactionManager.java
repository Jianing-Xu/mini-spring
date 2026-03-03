package com.xujn.minispring.examples.transaction.phase1.fixture;

import com.xujn.minispring.context.annotation.Component;
import com.xujn.minispring.tx.transaction.DataSourceTransactionManager;
import com.xujn.minispring.tx.transaction.TransactionResourceFactory;

@Component
public class RecordingTransactionManager extends DataSourceTransactionManager {

    public RecordingTransactionManager(TransactionResourceFactory resourceFactory) {
        super(resourceFactory);
    }
}
