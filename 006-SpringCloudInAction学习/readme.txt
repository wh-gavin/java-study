1.û������ȱʡ���ݿ����Ӳ���

Failed to configure a DataSource: 'url' attribute is not specified and no embedded datasource could be configured.
Reason: Failed to determine a suitable driver class
@SpringBootApplication(exclude = {
    DataSourceAutoConfiguration.class})

2.mysql 
com.mysql.jdbc.Driver
��Ӧ
spring.datasource.two.url=jdbc:mysql://172.17.11.203:3306/db2?characterEncoding=utf-8&useSSL=false&serverTimezone=UTC
spring.datasource.two.driver-class-name=com.mysql.jdbc.Driver
com.mysql.cj.jdbc.Driver
spring.datasource.two.jdbc-url=jdbc:mysql://172.17.11.203:3306/db2?characterEncoding=utf-8&useSSL=false&serverTimezone=UTC
spring.datasource.two.driver-class-name=com.mysql.cj.jdbc.Driver
