1.���ִ���:Error creating bean with name 'defaultValidator' defined in class path resourc  
   hibernate-validator 5.2.17�ᱨ����. 
	1��.spring-boot-starter-web�ų�hibernate���� ��
	2��.���������hibernate���������������
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

2��Spring bootĬ�Ͼ�̬��Դ���ʷ�ʽ
        Spring bootĬ�Ϸ��ʵľ��� /** ������Spring boot���ʣ���ǰ��Ŀ��·��/ + ��̬��Դ�ļ����ͻ��Զ����ҵ���Ӧ���ļ�����Ӧ���ļ��� ��·����Ĭ�ϵ��ĸ���̬��Դ�ļ�Ŀ¼�£����

        Spring bootĬ�϶�/**�ķ��� �ǿ���ֱ�ӷ�����·���µ��ĸ���̬��ԴĿ¼�µ��ļ���

classpath:/public/
classpath:/resources/
classpath:/static/
classpath:/META-INFO/resouces/

3������tacocloud-ui
npm config set registry https://registry.npm.taobao.org/
   npm cache ls  // �鿴����
   npm cache clean  // ������
node-v6.9.1-win-x64 �汾���˻ᱨ��,������ node-v8.9.4-win-x64; 
�����ڸ�node�°�װ��Ҫ��װpython2,�����ڰ�װnode-sass������
���԰�python2��װ��ȥ

npm install string-width@^4.2.0 --registry=https://registry.npmjs.org/

npm i npm ���� string-width@^4.2.0

npm install string-width@^4.2.0 --registry=https://registry.npmjs.org/

ʵ�ʰ�װ���ǣ�string-width@4.2.3

npm install string-width@4.2.3

Ȼ������
npm i
ng serve����  ����û��ng  ��������npm start,
������angular��װ������node��װĿ¼��ng���

��Ϊpom���̱���: 
�޸�<node.version>v6.9.1</node.version> => v8.9.4

4.spirngboot Ĭ��h2�����ݿ�url
 jdbc:h2:mem:testdb

