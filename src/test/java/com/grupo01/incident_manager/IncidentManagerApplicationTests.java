package com.grupo01.incident_manager;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled("Deshabilitado en desarrollo local porque requiere variables de entorno de Supabase")
@SpringBootTest
class IncidentManagerApplicationTests {

	@Test
	void contextLoads() {
	}

}
