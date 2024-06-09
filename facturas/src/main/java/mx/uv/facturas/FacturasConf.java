package mx.uv.facturas;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

@EnableWs
@Configuration
public class FacturasConf extends WsConfigurerAdapter{
    @Bean
    public XsdSchema comprasSchema(){
        return new SimpleXsdSchema(new ClassPathResource("facturas.xsd"));
    }
    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServler(ApplicationContext applicationContext){
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, "/ws/*");
    }
    
    @Bean(name="facturas")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema facturasSchema){
        DefaultWsdl11Definition wsdl = new DefaultWsdl11Definition();
        wsdl.setPortTypeName("facturasPort");
        wsdl.setLocationUri("/ws");
        wsdl.setTargetNamespace("t4is.uv.mx/facturas");
        wsdl.setSchema(facturasSchema);
        return wsdl;
    }
    
}