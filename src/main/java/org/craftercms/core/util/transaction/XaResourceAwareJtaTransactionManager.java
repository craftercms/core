///*
// * Copyright (C) 2007-2013 Crafter Software Corporation.
// *
// * This program is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program.  If not, see <http://www.gnu.org/licenses/>.
// */
//package org.craftercms.core.util.transaction;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.springframework.transaction.TransactionDefinition;
//import org.springframework.transaction.TransactionSystemException;
//import org.springframework.transaction.jta.JtaTransactionManager;
//import org.springframework.transaction.support.DefaultTransactionStatus;
//import org.springframework.transaction.support.TransactionSynchronizationManager;
//
//import javax.transaction.RollbackException;
//import javax.transaction.SystemException;
//import javax.transaction.Transaction;
//import javax.transaction.xa.XAResource;
//import java.util.Arrays;
//
///**
// * {@link JtaTransactionManager} that manages XA resources for proper participation in a JTA distributed
// * transaction, and thus removes the need for J2EE implementation specific resource manager registration.
// *
// * @author Alfonso Vásquez
// */
//public abstract class XaResourceAwareJtaTransactionManager extends JtaTransactionManager {
//
//    private static final Log logger = LogFactory.getLog(XaResourceAwareJtaTransactionManager.class);
//
//    /**
//     * If there's an actual transaction running and transaction synchronization is new for the transaction
//     * (which means that the transaction is either new or existing but wasn't started by Spring), enlists the
//     * {@link XAResource}s returned by {@link #getXaResourcesToEnlist()} with the JTA transaction.
//     */
//    @Override
//    protected DefaultTransactionStatus newTransactionStatus(TransactionDefinition definition, Object transaction,
//                                                            boolean newTransaction, boolean newSynchronization,
//                                                            boolean debug, Object suspendedResources) {
//        DefaultTransactionStatus status = super.newTransactionStatus(definition, transaction, newTransaction,
//                newSynchronization, debug, suspendedResources);
//
//        if (status.hasTransaction() && status.isNewSynchronization()) {
//            try {
//                XAResource[] xaResources = getXaResourcesToEnlist();
//
//                enlistResources(xaResources);
//
//                TransactionSynchronizationManager.bindResource("enlistedXaResources", xaResources);
//            } catch (Throwable e) {
//                // Can't enlist XA resource. Do an early rollback.
//                try {
//                    rollback(status);
//                } catch (Exception ex) {
//                    throw new TransactionSystemException("Unable to enlist XA resources for current transaction, " +
//                            "and early rollback failed", ex);
//                }
//
//                throw new TransactionSystemException("Unable to enlist XA resources for current transaction", e);
//            }
//        }
//
//        return status;
//    }
//
//    @Override
//    protected Object doSuspend(Object transaction) {
//        XAResource[] xaResources = (XAResource[]) TransactionSynchronizationManager.getResource("enlistedXaResources");
//        if (xaResources == null) {
//            throw new IllegalStateException("No XA resources have being enlisted for current transaction");
//        }
//
//        try {
//            delistResources(xaResources, XAResource.TMSUSPEND);
//        } catch (Exception e) {
//            throw new TransactionSystemException("Unable to suspend XA resources for current transaction", e);
//        }
//
//        TransactionSynchronizationManager.unbindResource("enlistedXaResources");
//
//        return new SuspendedResourcesHolder(super.doSuspend(transaction), xaResources);
//    }
//
//    @Override
//    protected void doResume(Object transaction, Object suspendedResources) {
//        SuspendedResourcesHolder suspendedResHolder = (SuspendedResourcesHolder) suspendedResources;
//
//        super.doResume(transaction, suspendedResHolder.getSuspendedTransaction());
//
//        XAResource[] xaResources = suspendedResHolder.getSuspendedXaResources();
//
//        try {
//            enlistResources(xaResources);
//        } catch (Exception e) {
//            throw new TransactionSystemException("Unable to resume XA resources for current transaction", e);
//        }
//
//        TransactionSynchronizationManager.bindResource("enlistedXaResources", xaResources);
//    }
//
//    @Override
//    protected void doCleanupAfterCompletion(Object transaction) {
//        super.doCleanupAfterCompletion(transaction);
//
//        TransactionSynchronizationManager.unbindResource("enlistedXaResources");
//    }
//
//    protected void enlistResources(XAResource[] xaResources) throws SystemException, RollbackException {
//        Transaction transaction;
//        try {
//            transaction = getTransactionManager().getTransaction();
//        } catch (Exception e) {
//            throw new TransactionSystemException("Can't get JTA transaction from TransactionManager", e);
//        }
//
//        for (XAResource xaResource : xaResources) {
//            transaction.enlistResource(xaResource);
//        }
//
//        if (logger.isDebugEnabled()) {
//            logger.debug("XA resources " + Arrays.toString(xaResources) + " enlisted for transaction " + transaction);
//        }
//    }
//
//    protected void delistResources(XAResource[] xaResources, int flags) throws SystemException {
//        Transaction transaction;
//        try {
//            transaction = getTransactionManager().getTransaction();
//        } catch (Exception e) {
//            throw new TransactionSystemException("Can't get JTA transaction from TransactionManager", e);
//        }
//
//        for (XAResource xaResource : xaResources) {
//            transaction.delistResource(xaResource, flags);
//        }
//
//        if (logger.isDebugEnabled()) {
//            logger.debug("XA resources " + Arrays.toString(xaResources) + " delisted for transaction " + transaction);
//        }
//    }
//
//	//---------------------------------------------------------------------
//	// Template methods to be implemented in subclasses
//	//---------------------------------------------------------------------
//
//    /**
//     * Returns a list of {@link XAResource}s that need to be enlisted with the current transaction.
//     */
//    protected abstract XAResource[] getXaResourcesToEnlist();
//
//    private static class SuspendedResourcesHolder {
//
//        private Object suspendedTransaction;
//        private XAResource[] suspendedXaResources;
//
//        private SuspendedResourcesHolder(Object suspendedTransaction, XAResource[] suspendedXaResources) {
//            this.suspendedTransaction = suspendedTransaction;
//            this.suspendedXaResources = suspendedXaResources;
//        }
//
//        public Object getSuspendedTransaction() {
//            return suspendedTransaction;
//        }
//
//        public XAResource[] getSuspendedXaResources() {
//            return suspendedXaResources;
//        }
//
//    }
//
//}
