## 初识Activiti

#### 1、Activiti简介
	工作流总是任务(Task)的形式驱动人处理业务或者驱动业务系统自动完成作业。业务流程是由多个
	环节串联起来合成的，
	每个环节被赋予任务，并且每个任务是由多个活动组成的。

	Activiti是一个针对企业用户、开发人员、系统管理员的轻量级工作流程业务管理平台，其核心是
	使用Java开发的快速、
	稳定的BPMN (业务流程建模标注)  e2.0流程引擎。Activiti实在ApacheV2的许可下发布的，可
	以运行在任何类型的Java程序中，例如服务器、
	集群、云服务器等。Activiti可以完美地与Spring结合。同时，基于简约的思想设计模式使
	Acitiviti非常轻量级。
#### 2、一个工作流的生命周期
	定义->发布->执行->监控->优化
#### 3、Activiti的特点
数据持久化
	
	使用Mybatis与数据库交互，通过最有的SQL执行Commond,从而保持引擎性能的最优化。
引擎service接口

	提供7大service接口，通过ProcessEngine获取，并且支持链式API风格。
流程设计器
	
	Eclipse Designer Idea actiBPM
原生支持Spring
	
	支持原生的Spring,可以轻松地和Spring集成，方便管理事务和解析表达式。
分离运行时与历史数据

	继承自jBPM4,支持运行时同原始数据隔离，这样能够快速读取运行时的数据，经需要查询历史数据
	的时候再去查找历史数据，大幅度提高了读取效率。
#### 4、Activiti的架构和组件
Activiti Engine

	作为最核心的模块，提供针对BPMN 2.0规范的解析、执行、创建、管理（任务、流程实例）、查询
	历史记录并根据结果生成报表。
Activiti Modeler

	是模型设计器，其并非由Activiti公司所开发，而是由业界认可的Signavio公司赠送的
	（Signavio e原本是收费的产品，现在被免费授权给Activiti用户使用）。适用于业务人员把需
	求转换为规范流程定义。
Activiti Designer
	
	功能和Activiti Modeler类似，同样提供了基于BPMN 2.0规范的可视化设计功能，但是目前还没
	有完全支持BPMN规范的定义。适用于开发人员，可以把业务需求人员用Signavio设计的流程定义
	（XML格式）导入到Designer中，从而让开发人员将其进一步加工成为可以运行的流程定义。
Activiti Explorer
	
	功能和Activiti Modeler类似，同样提供了基于BPMN 2.0规范的可视化设计功能，但是目前还没
	有完全支持BPMN规范的定义。适用于开发人员，可以把业务需求人员用Signavio设计的流程定义
	（XML格式）导入到Designer中，从而让开发人员将其进一步加工成为可以运行的流程定义。	
Activiti REST
	
	提供Restful风格的服务，允许客户端以JSON的方式与引擎的REST API交互，通用的协议具有跨平
	台、跨语言的特性。

## 快速入门Activiti官方demo
#### 1、创建Activiti Maven项目
![](https://i.imgur.com/ZnTNqVC.png)
配置pom.xml引入相关依赖：
	
	<?xml version="1.0" encoding="UTF-8"?>
	<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>activitiStart</groupId>
    <artifactId>activitiDemo</artifactId>
    <version>1.0-SNAPSHOT</version>

    <dependencies>
        <dependency>
            <groupId>org.activiti</groupId>
            <artifactId>activiti-engine</artifactId>
            <version>5.22.0</version>
        </dependency>

        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>1.7.25</version>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-log4j12</artifactId>
            <version>1.7.25</version>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <version>1.4.193</version>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.45</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-assembly-plugin</artifactId>
                <version>3.1.0</version>
                <configuration>
                    <descriptorRefs>
                        <descriptorRef>jar-with-dependencies</descriptorRef>
                    </descriptorRefs>
                    <archive>
                        <mainfest>
                            <mainClass>com.example.OnboardingRequest</mainClass>
                        </mainfest>
                    </archive>
                    <encoding>UTF-8</encoding>
                </configuration>
                <executions>
                    <execution>
                        <id>make-assembly</id>
                        <phase>package</phase>
                        <goals>
                            <goal>single</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>

	</project>
#### 2、创建流程引擎
配置日志：
	
	log4j.rootLogger=DEBUG,ACT
	
	log4j.appender.ACT=org.apache.log4j.ConsoleAppender
	log4j.appender.ACT.layout=org.apache.log4j.PatternLayout
	log4j.appender.ACT.layout.ConversionPattern=%d{hh:mm:ss,SSS} [%t] %-5p %c %x - %m%n
创建流程引擎：

	package com.neusoft;
	
	import org.activiti.engine.ProcessEngine;
	import org.activiti.engine.ProcessEngineConfiguration;
	import org.activiti.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
	import org.apache.ibatis.reflection.SystemMetaObject;
	
	public class OnBoardingRequest {
	    public static void main(String[] args) {
	        //创建流程引擎
	        ProcessEngineConfiguration cfg = new StandaloneInMemProcessEngineConfiguration()
	                .setJdbcUrl("jdbc:mysql://192.168.42.129:3306/activiti?serverTimezone=UTC")
	                .setJdbcUsername("root")
	                .setJdbcPassword("root123")
	                .setJdbcDriver("com.mysql.jdc.Driver")
	                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
	        ProcessEngine processEngine = cfg.buildProcessEngine();
	        String pName = processEngine.getName();
	        String ver = ProcessEngine.VERSION;
	        System.out.println("ProcessEngine ["+pName+"]Version["+ver+"]");
	    }
	}
启动流程：

	package com.neusoft;

	import org.activiti.engine.*;
	import org.activiti.engine.form.FormData;
	import org.activiti.engine.form.FormProperty;
	import org.activiti.engine.history.HistoricActivityInstance;
	import org.activiti.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
	import org.activiti.engine.impl.form.DateFormType;
	import org.activiti.engine.impl.form.LongFormType;
	import org.activiti.engine.impl.form.StringFormType;
	import org.activiti.engine.repository.Deployment;
	import org.activiti.engine.repository.ProcessDefinition;
	import org.activiti.engine.runtime.ProcessInstance;
	import org.activiti.engine.task.Task;
	
	import java.text.DateFormat;
	import java.text.ParseException;
	import java.text.SimpleDateFormat;
	import java.util.*;
	
	public class OnBoardingRequest {
    public static void main(String[] args) {
        //创建流程引擎
        ProcessEngineConfiguration cfg = new StandaloneInMemProcessEngineConfiguration()
                .setJdbcUrl("jdbc:mysql://192.168.42.129:3306/activiti?serverTimezone=UTC")
                .setJdbcUsername("root")
                .setJdbcPassword("root123")
                .setJdbcDriver("com.mysql.jdbc.Driver")
                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        ProcessEngine processEngine = cfg.buildProcessEngine();
        String pName = processEngine.getName();
        String ver = ProcessEngine.VERSION;
        System.out.println("ProcessEngine ["+pName+"]Version["+ver+"]");

        //部署流程
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("onboarding.bpmn20.xml").deploy();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment.getId()).singleResult();
        System.out.println(
                "Found process definition ["+processDefinition.getName()+"] with id ["+processDefinition.getId()+"]"
        );

        //启动流程实例
        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey("onboarding");
        System.out.println("Onboarding process started with process instance id ["+processInstance.getProcessDefinitionKey()+"]");

        TaskService taskService = processEngine.getTaskService();
        FormService formService = processEngine.getFormService();
        HistoryService historyService = processEngine.getHistoryService();

        Scanner scanner = new Scanner(System.in);
        while(processInstance != null && !processInstance.isEnded()){
            List<Task> tasks = taskService.createTaskQuery()
                    .taskCandidateGroup("managers").list();
            System.out.println("Active outStanding tasks:["+ tasks.size()+"]");
            for(int i=0;i<tasks.size();i++){
                Task task = tasks.get(i);
                System.out.println("Processing task ["+task.getName()+"]");
                Map<String, Object> variables = new HashMap<String, Object>();
                FormData formData = formService.getTaskFormData(task.getId());
                for(FormProperty formProperty : formData.getFormProperties()
                ){
                    if(StringFormType.class.isInstance(formProperty.getType())){
                        System.out.println(formProperty.getName()+"?");
                        String value = scanner.nextLine();
                        variables.put(formProperty.getId(),value);
                    }else if(LongFormType.class.isInstance(formProperty.getType())){
                        System.out.println(formProperty.getName()+"? (Must be a whole number)");
                        Long value = Long.valueOf(scanner.nextLine());
                        variables.put(formProperty.getId(), value);
                    }else if (DateFormType.class.isInstance(formProperty.getType())){
                        try{
                            System.out.println(formProperty.getName() + "? (Must be a date m/d/yy)");
                            DateFormat dateFormat = new SimpleDateFormat("m/d/yy");
                            Date value = dateFormat.parse(scanner.nextLine());
                            variables.put(formProperty.getId(), value);
                        }catch (ParseException e){
                            e.printStackTrace();
                        }

                    }else {
                        System.out.println("<form type not supported>");
                    }
                }
                taskService.complete(task.getId(), variables);

                HistoricActivityInstance endActivity = null;
                List<HistoricActivityInstance> activities =
                        historyService.createHistoricActivityInstanceQuery()
                                .processInstanceId(processInstance.getId()).finished()
                                .orderByHistoricActivityInstanceEndTime().asc()
                                .list();
                for (HistoricActivityInstance activity : activities) {
                    if (activity.getActivityType() == "startEvent") {
                        System.out.println("BEGIN " + processDefinition.getName()
                                + " [" + processInstance.getProcessDefinitionKey()
                                + "] " + activity.getStartTime());
                    }
                    if (activity.getActivityType() == "endEvent") {
                        // Handle edge case where end step happens so fast that the end step
                        // and previous step(s) are sorted the same. So, cache the end step
                        //and display it last to represent the logical sequence.
                        endActivity = activity;
                    } else {
                        System.out.println("-- " + activity.getActivityName()
                                + " [" + activity.getActivityId() + "] "
                                + activity.getDurationInMillis() + " ms");
                    }
                }
                if (endActivity != null) {
                    System.out.println("-- " + endActivity.getActivityName()
                            + " [" + endActivity.getActivityId() + "] "
                            + endActivity.getDurationInMillis() + " ms");
                    System.out.println("COMPLETE " + processDefinition.getName() + " ["
                            + processInstance.getProcessDefinitionKey() + "] "
                            + endActivity.getEndTime());
                }

            }
            processInstance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(processInstance.getId()).singleResult();
        }
        scanner.close();
    }
	}


