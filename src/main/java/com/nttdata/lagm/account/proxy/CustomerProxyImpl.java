package com.nttdata.lagm.account.proxy;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.nttdata.lagm.account.model.Customer;
import com.nttdata.lagm.account.util.RestUtils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class CustomerProxyImpl implements CustomerProxy {
	
	@Value("${config-eureka.base.customer.endpoint}")
	private String endpointCustomer;
	
	@Autowired
	@Qualifier("wcLoadBalanced")
	private WebClient.Builder webClientBuilder;

	@Override
	public Flux<Customer> findAll() {
		return webClientBuilder
				.clientConnector(RestUtils.getDefaultClientConnector())
				.build()
				.get()
				.uri(endpointCustomer)
				.retrieve()
				.bodyToFlux(Customer.class);
	}
	
	@Override
	public Mono<Customer> findById(String id) {
		Map<String,Object> params = new HashMap<>();
		params.put("id", id);
		
//		Mono<Customer> customerMono = webClient.get().uri(endpointCustomer + "/{id}", params)
//			.accept(MediaType.APPLICATION_JSON)
//			.retrieve()
//			.bodyToMono(Customer.class);
//		
		return webClientBuilder
				.clientConnector(RestUtils.getDefaultClientConnector())
				.build()
				.get()
				.uri(endpointCustomer + "/{id}", params)
				.retrieve()
				.bodyToMono(Customer.class);
	}

}
