1.出现错误:Error creating bean with name 'defaultValidator' defined in class path resourc  
   hibernate-validator 5.2.17会报错误. 
	1）.spring-boot-starter-web排除hibernate依赖 在
	2）.引入所需的hibernate最新依赖完美解决
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
			<exclusions>
				<exclusion>
					<artifactId>hibernate-validator</artifactId>
					<groupId>org.hibernate.validator</groupId>
				</exclusion>
			</exclusions>
		</dependency>
		<dependency>
			<groupId>org.hibernate</groupId>
			<artifactId>hibernate-validator</artifactId>
			<version>5.0.1.Final</version>
		</dependency>

2、Spring boot默认静态资源访问方式
        Spring boot默认访问的就是 /** ，所以Spring boot访问：当前项目根路径/ + 静态资源文件名就会自动的找到对应的文件。对应的文件在 类路径下默认的四个静态资源文件目录下，因此

        Spring boot默认对/**的访问 是可以直接访问类路径下的四个静态资源目录下的文件：

classpath:/public/
classpath:/resources/
classpath:/static/
classpath:/META-INFO/resouces/

3、编译tacocloud-ui
npm config set registry https://registry.npm.taobao.org/
   npm cache ls  // 查看缓存
   npm cache clean  // 清理缓存
node-v6.9.1-win-x64 版本低了会报错,可以用 node-v8.9.4-win-x64; 
发现在该node下安装需要安装python2,是由于安装node-sass的问题
可以把python2安装上去

npm install string-width@^4.2.0 --registry=https://registry.npmjs.org/

npm i npm 报错 string-width@^4.2.0

npm install string-width@^4.2.0 --registry=https://registry.npmjs.org/

实际安装的是：string-width@4.2.3

npm install string-width@4.2.3

然后运行
npm i
ng serve即可  报错没有ng  可以运行npm start,
是由于angular安装后并有在node安装目录放ng命令。

作为pom工程编译: 
修改<node.version>v6.9.1</node.version> => v8.9.4

4.spirngboot 默认h2的数据库url
 jdbc:h2:mem:testdb

