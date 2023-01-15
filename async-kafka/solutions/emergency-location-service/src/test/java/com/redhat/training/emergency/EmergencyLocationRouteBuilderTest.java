package com.redhat.training.emergency;

import io.agroal.api.AgroalDataSource;
import io.quarkus.test.junit.QuarkusTest;

import static org.junit.Assert.assertNotEquals;

import javax.inject.Inject;

import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.quarkus.test.CamelQuarkusTestSupport;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.training.emergency.route.EmergencyLocationRouteBuilder;

@QuarkusTest
class EmergencyLocationRouteBuilderTest extends CamelQuarkusTestSupport {

	private final Logger LOGGER = LoggerFactory.getLogger(EmergencyLocationRouteBuilderTest.class);

	@Inject
	protected ConsumerTemplate consumerTemplate;

	@Inject
	protected CamelContext context;

	@Inject
	protected AgroalDataSource jdbcTemplate;

	@Override
	protected RoutesBuilder createRouteBuilder() {
		return new EmergencyLocationRouteBuilder();
	}

	@Test
	void testEmergencyLocationRoute() throws Exception {
		configureRoute("emergency-location-route");
		assertErrorNotOccured();
	}

	@Test
	void testKafkaConsumerRoute() throws Exception {
		configureRoute("kafka-consumer-route");
		assertErrorNotOccured();
		assertDBHasRecords();
	}

	private void configureRoute(String routeId) throws Exception{
		AdviceWith.adviceWith(context(), routeId, route -> {
			route.replaceFromWith("direct:ready-for-printing");
			route.interceptSendToEndpoint("file://data/printing-services/technical")
				.skipSendToOriginalEndpoint()
				.to("mock:file:technical");

			route.interceptSendToEndpoint("file://data/printing-services/novel")
				.skipSendToOriginalEndpoint()
				.to("mock:file:novel");
			 }
		);
	}

	private void assertErrorNotOccured() {
		String body = consumerTemplate.receive("direct:output").getIn().getBody(String.class);
		assertNotEquals("errorOccured", body); 
	}

	private void assertDBHasRecords(){
		//Integer recordCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM locations", Integer.class);
		//LOGGER.info("The locations table has " + recordCount + " records");
		//assertTrue(recordCount >= 49);
	}

}
