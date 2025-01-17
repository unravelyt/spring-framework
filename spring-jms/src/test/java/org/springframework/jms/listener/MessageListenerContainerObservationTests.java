/*
 * Copyright 2002-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.jms.listener;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import io.micrometer.observation.tck.TestObservationRegistry;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.junit.EmbeddedActiveMQExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import org.springframework.jms.core.JmsTemplate;

import static io.micrometer.observation.tck.TestObservationRegistryAssert.assertThat;

/**
 * Observation tests for {@link AbstractMessageListenerContainer} implementations.
 * @author Brian Clozel
 */
class MessageListenerContainerObservationTests {

	@RegisterExtension
	EmbeddedActiveMQExtension server = new EmbeddedActiveMQExtension();

	TestObservationRegistry registry = TestObservationRegistry.create();

	private ActiveMQConnectionFactory connectionFactory;

	@BeforeEach
	void setupServer() {
		server.start();
		connectionFactory = new ActiveMQConnectionFactory(server.getVmURL());
	}

	@ParameterizedTest(name = "{index} {0}")
	@MethodSource("listenerContainers")
	void shouldRecordJmsProcessObservations(String implementationClass, AbstractMessageListenerContainer listenerContainer) throws Exception {
		JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
		jmsTemplate.convertAndSend("spring.test.observation", "message content");
		CountDownLatch latch = new CountDownLatch(1);
		listenerContainer.setConnectionFactory(connectionFactory);
		listenerContainer.setObservationRegistry(registry);
		listenerContainer.setDestinationName("spring.test.observation");
		listenerContainer.setMessageListener(new MessageListener() {
			@Override
			public void onMessage(Message message) {
				latch.countDown();
			}
		});
		listenerContainer.afterPropertiesSet();
		listenerContainer.start();
		latch.await(2, TimeUnit.SECONDS);
		assertThat(registry).hasObservationWithNameEqualTo("jms.message.process")
				.that()
				.hasHighCardinalityKeyValue("messaging.destination.name", "spring.test.observation");
		listenerContainer.shutdown();
		listenerContainer.stop();
	}

	static Stream<Arguments> listenerContainers() {
		return Stream.of(
				Arguments.of(DefaultMessageListenerContainer.class.getSimpleName(), new DefaultMessageListenerContainer()),
				Arguments.of(SimpleMessageListenerContainer.class.getSimpleName(), new SimpleMessageListenerContainer())
		);
	}

	@AfterEach
	void shutdownServer() {
		connectionFactory.close();
		server.stop();
	}
}
