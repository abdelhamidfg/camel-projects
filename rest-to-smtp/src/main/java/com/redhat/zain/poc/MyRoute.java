package com.redhat.zain.poc;
import org.apache.camel.model.dataformat.JsonLibrary;

import com.redhat.zain.poc.Email;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import java.util.Optional;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import jakarta.enterprise.context.ApplicationScoped;




@ApplicationScoped
public class MyRoute extends RouteBuilder {
  
   @ConfigProperty(name = "mail.server.port") 
    String port;
    @ConfigProperty(name = "mail.server.host") 
    String host;


    @ConfigProperty(name = "mail.server.username") 
    String username;
    @ConfigProperty(name = "mail.server.password") 
    String password;



    @Override
    public void configure() throws Exception {
       

        rest("/email")
                .post()
                .to("direct:send-email")
                

        ;
        from("direct:send-email")
         .unmarshal().json(JsonLibrary.Jackson, Email.class)
         .process(new Processor() {
            public void process(Exchange exchange) throws Exception {
                System.out.println("username="+username);
                System.out.println("password="+password);
                System.out.println("host="+host);
                System.out.println("port="+port);
                Email email = exchange.getIn().getBody(Email.class);
                exchange.getIn().setHeader("To", email.getTo());
                exchange.getIn().setHeader("From", email.getFrom());
                exchange.getIn().setHeader("Subject", email.getSubject());                exchange.getIn().setBody(email.getBody());
                exchange.getIn().setHeader("port", port);
                exchange.getIn().setHeader("host", host);
                exchange.getIn().setHeader("username", username);
                exchange.getIn().setHeader("password", password);
            }
        })
        .toD("smtp://${header.host}:${header.port}?username=${header.username}&password=${header.password}")
        .setBody(constant("message has been sent successfully"));
        //.setBody("message has been sent successfully");
        //.toD("smtps://${header.host}:${header.port}?username=${header.username}&password=${header.password}"); //?username=hamid&password=password

    }
}