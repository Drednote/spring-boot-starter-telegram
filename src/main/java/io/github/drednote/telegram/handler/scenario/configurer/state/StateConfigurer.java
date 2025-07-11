package io.github.drednote.telegram.handler.scenario.configurer.state;

import io.github.drednote.telegram.core.request.TelegramRequest;
import io.github.drednote.telegram.handler.scenario.action.Action;
import java.util.Collection;
import java.util.Set;
import org.springframework.statemachine.config.configurers.StateConfigurer.History;
import org.springframework.statemachine.state.State;

/**
 * Base {@code StateConfigurer} interface for configuring {@link State}s.
 *
 * @param <S> the type of state
 */
public interface StateConfigurer<S> extends ScenarioStateConfigurerBuilder<S> {

	/**
	 * Specify a initial state {@code S}.
	 *
	 * @param initial the initial state
	 * @return configurer for chaining
	 */
	StateConfigurer<S> initial(S initial);

	/**
	 * Specify a initial state {@code S} with an {@link Action} to be executed
	 * with it. Action can be i.e. used to init extended variables.
	 *
	 * @param initial the initial state
	 * @param action the action
	 * @return configurer for chaining
	 */
	StateConfigurer<S> initial(S initial, Action<S> action);

	/**
	 * Specify a states configured by this configurer instance to be
	 * substates of state {@code S}.
	 *
	 * @param state the parent state
	 * @return configurer for chaining
	 */
	StateConfigurer<S> parent(S state);

	/**
	 * Specify a region for these states configured by this configurer instance.
	 *
	 * @param id the region id
	 * @return configurer for chaining
	 */
	StateConfigurer<S> region(String id);

	/**
	 * Specify a state {@code S}.
	 *
	 * @param state the state
	 * @return configurer for chaining
	 */
	StateConfigurer<S> state(S state);

//	/**
//	 * Specify a state {@code S} and its relation with a given
//	 * machine as substate machine.
//	 *
//	 * @param state the state
//	 * @param stateMachine the submachine
//	 * @return configurer for chaining
//	 */
//	StateConfigurer<S> state(S state, StateMachine<S> stateMachine);
//
//	/**
//	 * Specify a state {@code S} and its relation with a given
//	 * machine as substate machine factory.
//	 *
//	 * @param state the state
//	 * @param stateMachineFactory the submachine factory
//	 * @return configurer for chaining
//	 */
//	StateConfigurer<S> state(S state, StateMachineFactory<S> stateMachineFactory);

	/**
	 * Specify a state {@code S} with state {@link Action}s.
	 *
	 * @param state the state
	 * @param stateActions the state actions
	 * @return configurer for chaining
	 */
	StateConfigurer<S> state(S state, Collection<? extends Action<S>> stateActions);

	/**
	 * Specify a state {@code S} with state {@link Action}.
	 *
	 * @param state the state
	 * @param stateAction the state action
	 * @return configurer for chaining
	 */
	StateConfigurer<S> state(S state, Action<S> stateAction);

	/**
	 * Specify a state {@code S} with state behaviour {@link Action}.
	 * Currently synonym for {@link #state(Object, Action)}.
	 *
	 * @param state the state
	 * @param action the state action
	 * @return configurer for chaining
	 * @see #state(Object, Action)
	 */
	StateConfigurer<S> stateDo(S state, Action<S> action);

	/**
	 * Specify a state {@code S} with state behaviour {@link Action} and
	 * error {@link Action} callback.
	 *
	 * @param state the state
	 * @param action the state action
	 * @param error action that will be called if any unexpected exception is thrown by the action.
	 * @return configurer for chaining
	 */
	StateConfigurer<S> stateDo(S state, Action<S> action, Action<S> error);

	/**
	 * Specify a state {@code S} with entry and exit {@link Action}s.
	 *
	 * @param state the state
	 * @param entryActions the state entry actions
	 * @param exitActions the state exit actions
	 * @return configurer for chaining
	 */
	StateConfigurer<S> state(S state, Collection<? extends Action<S>> entryActions,
			Collection<? extends Action<S>> exitActions);

	/**
	 * Specify a state {@code S} with entry and exit {@link Action}.
	 *
	 * @param state the state
	 * @param entryAction the state entry action
	 * @param exitAction the state exit action
	 * @return configurer for chaining
	 */
	StateConfigurer<S> state(S state, Action<S> entryAction, Action<S> exitAction);

	/**
	 * Specify a state {@code S} with state entry {@link Action}.
	 * Currently synonym for {@link #state(Object, Action, Action)}
	 * with no exit action.
	 *
	 * @param state the state
	 * @param action the state entry action
	 * @return configurer for chaining
	 * @see #state(Object, Action, Action)
	 */
	StateConfigurer<S> stateEntry(S state, Action<S> action);

	/**
	 * Specify a state {@code S} with state entry {@link Action} and
	 * error {@link Action} callback.
	 * Currently synonym for {@link #state(Object, Action, Action)}
	 * with no exit action.
	 *
	 * @param state the state
	 * @param action the state entry action
	 * @param error action that will be called if any unexpected exception is thrown by the action.
	 * @return configurer for chaining
	 * @see #state(Object, Action, Action)
	 */
	StateConfigurer<S> stateEntry(S state, Action<S> action, Action<S> error);

	/**
	 * Specify a state {@code S} with state exit {@link Action}.
	 * Currently synonym for {@link #state(Object, Action, Action)}
	 * with no entry action.
	 *
	 * @param state the state
	 * @param action the state exit action
	 * @return configurer for chaining
	 * @see #state(Object, Action, Action)
	 */
	StateConfigurer<S> stateExit(S state, Action<S> action);

	/**
	 * Specify a state {@code S} with state exit {@link Action} and
	 * error {@link Action} callback.
	 * Currently synonym for {@link #state(Object, Action, Action)}
	 * with no entry action.
	 *
	 * @param state the state
	 * @param action the state entry action
	 * @param error action that will be called if any unexpected exception is thrown by the action.
	 * @return configurer for chaining
	 * @see #state(Object, Action, Action)
	 */
	StateConfigurer<S> stateExit(S state, Action<S> action, Action<S> error);

	/**
	 * Specify a state {@code S} with a deferred events {@code E}.
	 *
	 * @param state the state
	 * @param deferred the deferred events
	 * @return configurer for chaining
	 */
    StateConfigurer<S> state(S state, TelegramRequest... deferred);

	/**
	 * Specify a states {@code S}.
	 *
	 * @param states the states
	 * @return configurer for chaining
	 */
	StateConfigurer<S> states(Set<S> states);

	/**
	 * Specify a state {@code S} to be end state. This method
	 * can be called for each state to be marked as end state.
	 *
	 * @param end the end state
	 * @return configurer for chaining
	 */
	StateConfigurer<S> end(S end);

	/**
	 * Specify a state {@code S} to be choice pseudo state.
	 *
	 * @param choice the choice pseudo state
	 * @return configurer for chaining
	 */
	StateConfigurer<S> choice(S choice);

	/**
	 * Specify a state {@code S} to be junction pseudo state.
	 *
	 * @param junction the junction pseudo state
	 * @return configurer for chaining
	 */
	StateConfigurer<S> junction(S junction);

	/**
	 * Specify a state {@code S} to be fork pseudo state.
	 *
	 * @param fork the fork pseudo state
	 * @return configurer for chaining
	 */
	StateConfigurer<S> fork(S fork);

	/**
	 * Specify a state {@code S} to be join pseudo state.
	 *
	 * @param join the join pseudo state
	 * @return configurer for chaining
	 */
	StateConfigurer<S> join(S join);

	/**
	 * Specify a state {@code S} to be history pseudo state.
	 *
	 * @param history the history pseudo state
	 * @param type the history pseudo state type
	 * @return configurer for chaining
	 */
	StateConfigurer<S> history(S history, History type);

	/**
	 * Specify a state {@code S} to be entrypoint pseudo state.
	 *
	 * @param entry the entrypoint pseudo state
	 * @return configurer for chaining
	 */
	StateConfigurer<S> entry(S entry);

	/**
	 * Specify a state {@code S} to be exitpoint pseudo state.
	 *
	 * @param exit the exitpoint pseudo state
	 * @return configurer for chaining
	 */
	StateConfigurer<S> exit(S exit);
}
