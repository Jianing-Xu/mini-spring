package com.xujn.minispring.tx.transaction;

import com.xujn.minispring.exception.TransactionException;
import com.xujn.minispring.tx.support.TransactionSynchronizationManager;

/**
 * MVP local transaction manager backed by a {@link TransactionResourceFactory}.
 * Constraint: supports only REQUIRED semantics with one thread-bound resource per manager.
 * Thread-safety: relies on thread-local binding for runtime isolation.
 */
public class DataSourceTransactionManager implements PlatformTransactionManager {

    private final TransactionResourceFactory resourceFactory;

    public DataSourceTransactionManager(TransactionResourceFactory resourceFactory) {
        this.resourceFactory = resourceFactory;
    }

    @Override
    public TransactionStatus getTransaction(TransactionDefinition definition) {
        if (TransactionSynchronizationManager.hasResource(this)) {
            TransactionResource existing = (TransactionResource) TransactionSynchronizationManager.getResource(this);
            return new SimpleTransactionStatus(this, existing, false);
        }
        TransactionResource resource = resourceFactory.createResource();
        try {
            resource.begin();
        } catch (RuntimeException ex) {
            throw new TransactionException("Failed to begin transaction", ex);
        }
        TransactionSynchronizationManager.bindResource(this, resource);
        TransactionSynchronizationManager.clearRollbackOnly(this);
        return new SimpleTransactionStatus(this, resource, true);
    }

    @Override
    public void commit(TransactionStatus status) {
        SimpleTransactionStatus simpleStatus = asSimpleStatus(status);
        if (!simpleStatus.isNewTransaction()) {
            if (simpleStatus.isRollbackOnly()) {
                TransactionSynchronizationManager.setRollbackOnly(simpleStatus.getResourceKey());
            }
            return;
        }
        try {
            if (simpleStatus.isRollbackOnly()
                    || TransactionSynchronizationManager.isRollbackOnly(simpleStatus.getResourceKey())) {
                simpleStatus.getResource().rollback();
            } else {
                simpleStatus.getResource().commit();
            }
        } catch (RuntimeException ex) {
            throw new TransactionException("Failed to complete transaction commit", ex);
        } finally {
            cleanupAfterCompletion(simpleStatus);
        }
    }

    @Override
    public void rollback(TransactionStatus status) {
        SimpleTransactionStatus simpleStatus = asSimpleStatus(status);
        if (!simpleStatus.isNewTransaction()) {
            simpleStatus.setRollbackOnly();
            TransactionSynchronizationManager.setRollbackOnly(simpleStatus.getResourceKey());
            return;
        }
        try {
            simpleStatus.getResource().rollback();
        } catch (RuntimeException ex) {
            throw new TransactionException("Failed to roll back transaction", ex);
        } finally {
            cleanupAfterCompletion(simpleStatus);
        }
    }

    private SimpleTransactionStatus asSimpleStatus(TransactionStatus status) {
        if (status instanceof SimpleTransactionStatus simpleStatus) {
            return simpleStatus;
        }
        throw new TransactionException("Unsupported TransactionStatus implementation: " + status.getClass().getName());
    }

    private void cleanupAfterCompletion(SimpleTransactionStatus status) {
        TransactionSynchronizationManager.unbindResource(status.getResourceKey());
        TransactionSynchronizationManager.clearRollbackOnly(status.getResourceKey());
    }
}
