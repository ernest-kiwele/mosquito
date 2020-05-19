package com.eussence.mosquito.api.execution;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.eussence.mosquito.api.CallChainResult;
import com.eussence.mosquito.api.ExpressionLanguage;
import com.eussence.mosquito.api.MapObject;
import com.eussence.mosquito.api.command.Resolver;
import com.eussence.mosquito.api.http.ClientBinding;

public class LocalExecutionScheduler extends ExecutionScheduler {

	protected LocalExecutionScheduler(boolean runAssertions, String executionId, boolean collectMetrics,
			Collection<Consumer<ExecutionEvent>> eventConsumers, Function<ExpressionLanguage, Resolver> resolverFactory,
			Supplier<ClientBinding<?, ?, ?>> clientFactory) {
		super(runAssertions, executionId, collectMetrics, eventConsumers, resolverFactory, clientFactory);
	}

	public static ExecutionScheduler instance(Collection<Consumer<ExecutionEvent>> eventConsumers,
			Function<ExpressionLanguage, Resolver> resolverFactory, Supplier<ClientBinding<?, ?, ?>> clientFactory) {

		return new LocalExecutionScheduler(true, UUID.randomUUID().toString(), true, eventConsumers, resolverFactory,
				clientFactory);
	}

	@Override
	public ExecutionResult execute(ExecutionSchedule schedule) {

		MapObject mapObject = schedule.getContext();

		ExecutionResult result = new ExecutionResult();

		result.setStartDate(new Date());

		result.setId(schedule.getId());
		result.setAssertionsRun(this.runAssertions);
		result.setMetricsCollected(this.collectMetrics);
		result.setEnvironment(schedule.getEnvironment());
		result.setDatasets(schedule.getDatasets());
		result.setVars(schedule.getVars());

		result.setCallChainResults(schedule.getCallChains().stream().map(cc -> this.executeCallChain(cc, mapObject))
				.collect(Collectors.toMap(CallChainResult::getKey, Function.identity())));

		result.setEndDate(new Date());

		return result;
	}

	@Override
	protected ClientBinding<?, ?, ?> client() {
		return this.clientFactory.get();
	}
}
