package org.tacos.jdbc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;


/**
多数据源时没有自动初始化数据库
 */
//@Component
public class ApplicationRunnerService implements ApplicationRunner {

    @Autowired
    @Qualifier("jdbcTemplateOne")
    private JdbcTemplate h2Template;

    @Value("${spring.datasource.schema}")
    private String schema;

    @Value("${spring.datasource.data}")
    private String data;

    /**
     * @Author: TheBigBlue
     * @Description: 项目启动，执行sql文件初始化
     * @Date: 2019/9/19
     * @Param args:
     * @Return:
     **/
    @Override
    public void run(ApplicationArguments args) {
        String schemaContent = this.getFileContent(schema);
        String dataContent = this.getFileContent(data);
        h2Template.execute(schemaContent);
        h2Template.execute(dataContent);
    }

    /**
     * @Author: TheBigBlue
     * @Description: 获取classpath下sql文件内容
     * @Date: 2019/9/19
     * @Param filePath:
     * @Return:
     **/
    private String getFileContent(String filePath) {
        BufferedReader bufferedReader = null;
        String string;
        StringBuilder data = new StringBuilder();
        try {
            ClassPathResource classPathResource = new ClassPathResource(filePath);
            bufferedReader = new BufferedReader(new InputStreamReader(classPathResource.getInputStream()));
            while ((string = bufferedReader.readLine()) != null) {
                data.append(string);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(null != bufferedReader){
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return data.toString();
    }
}
