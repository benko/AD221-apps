package com.redhat.training.payslipvalidator.processor;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.language.xpath.XPathBuilder;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PriceProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {

		double payslipTotal = Double.parseDouble(
		XPathBuilder.xpath(
					"/payslip/totalPayslip/text()"
				).evaluate(exchange, String.class)
		);

		NodeList payslipItems = XPathBuilder.xpath(
				"/payslip/payslipItems/node()"
		).evaluate(exchange, NodeList.class);

		Stream<Node> payslipItemsStream = IntStream.range(0, payslipItems.getLength())
				.mapToObj(payslipItems::item);

		double calculatedTotal = payslipItemsStream.filter(node -> node.getNodeType() == Node.ELEMENT_NODE)
				.mapToDouble(this::calculateItemTotal).sum();

		if (payslipTotal != calculatedTotal) {
			throw new WrongTotalPayslipCalculationException();
		}

		exchange.getIn().setHeader("totalPrice", calculatedTotal);
	}

	private double calculateItemTotal(Node node) {
		Element payslipItem = (Element) node;

		int qty = Integer.parseInt(
				payslipItem.getElementsByTagName("payslipItemQty")
						.item(0).getTextContent()
		);

		double price = Double.parseDouble(
				payslipItem.getElementsByTagName("payslipItemPrice")
						.item(0).getTextContent()
		);

		return qty * price;
	}
}
