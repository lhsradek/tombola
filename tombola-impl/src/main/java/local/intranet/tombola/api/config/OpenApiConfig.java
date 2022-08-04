package local.intranet.tombola.api.config;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import local.intranet.tombola.api.controller.StatusController;

//@formatter:off
/**
*
* {@link OpenApiConfig} for {@link local.intranet.tombola.TombolaApplication}.
* <p>
* https://www.baeldung.com/spring-rest-openapi-documentation
* 
* @author Radek Kádner
*
*/
//@formatter:on
@Configuration
public class OpenApiConfig {

	private static final String API = "Tombola API";
	
	@Autowired
	private StatusController statusController;
	
	/**
	 * 
	 * Grouped OpenApi info
	 * 
	 * @return {@link GroupedOpenApi}
	 */
	@Bean
	public GroupedOpenApi groupedOpenApi() {
		GroupedOpenApi ret = GroupedOpenApi.builder()
				.pathsToMatch("/api/v1/**")
				.group("tombola")
	            .displayName(API)
	            .build();
		return ret;
	}
	
	/**
	 * 
	 * Tombola OpenApi
	 * 
	 * @return {@link OpenAPI}
	 */
	@Bean
	public OpenAPI tombolaOpenApi() {
		OpenAPI ret = new OpenAPI()
				.info(new Info().title(API)
	            .description("Tombola API")
	            .version(statusController.getImplementationVersion())
	            .termsOfService("/tombola")
	            .contact(new Contact()
	            		.name("Radek Kádner").url("https://www.linkedin.com/in/radekkadner/")
	            		.email("radek.kadner@gmail.com"))
	            .license(new License()
	            		.name("The MIT License").url("https://opensource.org/licenses/MIT")))
				.externalDocs(new ExternalDocumentation().description("Java Documentation").url("/index-javadoc/"));
		return ret;
	 }
	

}
