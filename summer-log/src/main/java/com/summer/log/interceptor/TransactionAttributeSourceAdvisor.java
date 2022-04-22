

package com.summer.log.interceptor;

import org.aopalliance.aop.Advice;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

@SuppressWarnings("serial")
public class TransactionAttributeSourceAdvisor extends AbstractPointcutAdvisor {

	@Nullable
	private LoggingInterceptor transactionInterceptor;

	private final LoggingAttributeSourcePointcut pointcut = new LoggingAttributeSourcePointcut() {
		@Override
		@Nullable
		protected TransactionAttributeSource getTransactionAttributeSource() {
			return (transactionInterceptor != null ? transactionInterceptor.getTransactionAttributeSource() : null);
		}
	};


	/**
	 * Create a new TransactionAttributeSourceAdvisor.
	 */
	public TransactionAttributeSourceAdvisor() {
	}

	/**
	 * Create a new TransactionAttributeSourceAdvisor.
	 * @param interceptor the transaction interceptor to use for this advisor
	 */
	public TransactionAttributeSourceAdvisor(TransactionInterceptor interceptor) {
		setTransactionInterceptor(interceptor);
	}


	/**
	 * Set the transaction interceptor to use for this advisor.
	 */
	public void setTransactionInterceptor(TransactionInterceptor interceptor) {
		this.transactionInterceptor = interceptor;
	}

	/**
	 * Set the {@link ClassFilter} to use for this pointcut.
	 * Default is {@link ClassFilter#TRUE}.
	 */
	public void setClassFilter(ClassFilter classFilter) {
		this.pointcut.setClassFilter(classFilter);
	}


	@Override
	public Advice getAdvice() {
		Assert.state(this.transactionInterceptor != null, "No TransactionInterceptor set");
		return this.transactionInterceptor;
	}

	@Override
	public Pointcut getPointcut() {
		return this.pointcut;
	}

}
