package com.xujn.minispring.test.transaction.phase1;

import com.xujn.minispring.context.annotation.Component;
import com.xujn.minispring.tx.transaction.DataSourceTransactionManager;
import com.xujn.minispring.tx.transaction.TransactionResourceFactory;

@Component
public class RecordingTransactionManager extends DataSourceTransactionManager {

    public RecordingTransactionManager(TransactionResourceFactory resourceFactory) {
        super(resourceFactory);
    }
}
