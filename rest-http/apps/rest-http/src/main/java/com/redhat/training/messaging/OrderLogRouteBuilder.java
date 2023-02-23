package com.redhat.training.messaging;

import org.apache.camel.builder.RouteBuilder;

public class OrderLogRouteBuilder extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		from("direct:log_orders")
			.routeId("log-orders")
			.log("Order received: ${body}")
			.to("mock:fulfillmentSystem");
		}

}
