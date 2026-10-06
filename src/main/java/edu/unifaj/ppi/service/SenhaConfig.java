package edu.unifaj.ppi.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * O BCrypt mora aqui, e nao em um servico, porque e infraestrutura de crypto e
 * nao regra de negocio. Deliberadamente sem o spring-boot-starter-security: o
 * filter chain trancaria todos os endpoints com HTTP Basic, e por enquanto a API
 * se identifica pelo CPF do doador.
 */
@Configuration
public class SenhaConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}