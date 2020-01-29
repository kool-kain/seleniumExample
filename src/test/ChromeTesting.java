package test;

import static org.junit.jupiter.api.Assertions.*;

import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ChromeTesting {

	private static int givenId;
	private static String path;
	private static WebDriver browser;
	private static JtwigTemplate template;
	
	@BeforeAll
	public static void setUp() {
		path = System.getProperty("user.dir");
		System.setProperty("webdriver.chrome.driver",path + "\\resources\\chromedriver\\chromedriver.exe");
		template = JtwigTemplate.fileTemplate(path + "\\resources\\templatesJSON\\order.json");
		browser = new ChromeDriver();
	}
	
	@AfterAll
	public static void cleanUpAll() {
		//Executes after all tests are done
		browser.close();
	}

	@Test
	@Order(1)
	public void post_order_test() {  
		 String postApi = "http://localhost:8080/order";
		 givenId = 0;
		 
		 try {
			 JtwigModel model = JtwigModel.newModel().with("id", 1)
				 								.with("description", "some description")
				 								.with("cents", 2500)
				 								.with("complete", true);
		 
		 
			JsonNode resp = Unirest.post(postApi)
			    .header("accept", "application/json")
			    .header("Content-Type", "application/json")
			    .body(template.render(model))
			    .asJson()
			    .getBody();
			
			givenId = resp.getObject().getInt("id");
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		browser.get(postApi); 
		WebElement href = browser.findElement(By.xpath("//pre"));
		assertTrue(href.getText().contains(Integer.toString(givenId)));  
	}
	
	@Test
	@Order(2)
	public void get_order_test() {  
		 
		 String getApi = "http://localhost:8080/order/" + givenId;
		 
		 try {					 				 						
			JsonNode resp = Unirest.get(getApi)
			    .header("accept", "application/json")
			    .header("Content-Type", "application/json")
			    .asJson()
			    .getBody();
			
			givenId = resp.getObject().getInt("id");
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		browser.get(getApi); 
		WebElement href = browser.findElement(By.xpath("//pre"));
		assertTrue(href.getText().contains(Integer.toString(givenId)));  
	}	

	@Test
	@Order(3)
	public void put_order_test() {  
		 
		 String putApi = "http://localhost:8080/order/" + givenId;
		 int valueCents = 11500;
		 
		 try {
			 JtwigModel model = JtwigModel.newModel().with("id", givenId)
						.with("description", "some new description")
						.with("cents", valueCents)
						.with("complete", true);
			 
			JsonNode resp = Unirest.put(putApi)				
			    .header("accept", "application/json")
			    .header("Content-Type", "application/json")
			    .body(template.render(model))
			    .asJson()
			    .getBody();
			
			givenId = resp.getObject().getInt("id");
		} catch (UnirestException e) {
			e.printStackTrace();
		}	
		
		browser.get(putApi); 
		WebElement href = browser.findElement(By.xpath("//pre"));
		assertTrue(href.getText().contains(Integer.toString(valueCents)));  
	}
}
