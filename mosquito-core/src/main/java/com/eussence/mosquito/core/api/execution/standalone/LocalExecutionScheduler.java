package com.eussence.mosquito.core.api.execution.standalone;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.eussence.mosquito.api.ExpressionLanguage;
import com.eussence.mosquito.api.command.Resolver;
import com.eussence.mosquito.api.execution.ExecutionEvent;
import com.eussence.mosquito.api.http.ClientBinding;

public class LocalExecutionScheduler extends StandaloneSchedule {

	protected LocalExecutionScheduler(boolean runAssertions, String executionId, boolean collectMetrics,
			Collection<Consumer<ExecutionEvent>> eventConsumers, Function<ExpressionLanguage, Resolver> resolverFactory,
			Supplier<ClientBinding<?, ?, ?>> clientFactory) {
		super(runAssertions, executionId, collectMetrics, eventConsumers, resolverFactory, clientFactory);
	}

	public static ExecutionScheduler instance(Collection<Consumer<ExecutionEvent>> eventConsumers,
			Function<ExpressionLanguage, Resolver> resolverFactory, Supplier<ClientBinding<?, ?, ?>> clientFactory) {

		return new LocalExecutionScheduler(true, UUID.randomUUID()
				.toString(), true, eventConsumers, resolverFactory, clientFactory);
	}

}
