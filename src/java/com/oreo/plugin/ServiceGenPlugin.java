package com.oreo.plugin;

import com.google.code.mybatis.generator.plugins.BasePluginConfig;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.config.JavaClientGeneratorConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ServiceGenPlugin extends PluginAdapter {

    private ServiceGenPlugin.Config config;

    @Override
    public boolean validate(List<String> warnings) {
        if (this.config == null) {
            this.config = new ServiceGenPlugin.Config(this.getProperties());
        }
        return true;
    }

    private FullyQualifiedJavaType entity = null, condition = null, shortCondition = null, example = null;

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        this.entity = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        this.condition = new FullyQualifiedJavaType(this.entity.getFullyQualifiedName() + ".Condition");
        this.example = new FullyQualifiedJavaType(this.entity.getFullyQualifiedName() + "Example");
        this.config.abstractServiceFQN.addTypeArgument(new FullyQualifiedJavaType(this.entity.getShortName()));
        this.shortCondition = new FullyQualifiedJavaType(this.entity.getShortName() + "." + this.condition.getShortName());
        this.config.abstractServiceFQN.addTypeArgument(this.condition);
    }

    /**
     * 生成额外java文件
     */
    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        JavaClientGeneratorConfiguration javaClientGeneratorConfiguration = introspectedTable.getContext().getJavaClientGeneratorConfiguration();
        String targetProject = javaClientGeneratorConfiguration.getTargetProject();

        CompilationUnit addServiceInterface = addServiceInterface(introspectedTable);
        CompilationUnit addServiceInterfaceImpl = addServiceImplClazz(introspectedTable, addServiceInterface);

        GeneratedJavaFile gjfServiceInterface = new GeneratedJavaFile(addServiceInterface, targetProject,
                this.context.getProperty("javaFileEncoding"), this.context.getJavaFormatter());
        GeneratedJavaFile gjfServiceInterfaceImpl = new GeneratedJavaFile(addServiceInterfaceImpl, targetProject,
                this.context.getProperty("javaFileEncoding"), this.context.getJavaFormatter());
        List<GeneratedJavaFile> list = new ArrayList<>();
        list.add(gjfServiceInterface);
        list.add(gjfServiceInterfaceImpl);
        return list;
    }

    private CompilationUnit addServiceInterface(IntrospectedTable introspectedTable) {
        //获取baseservice接口
        FullyQualifiedJavaType superInterfaceType = new FullyQualifiedJavaType(this.config.baseServiceFQN.getShortName());
        // 添加实体类
        superInterfaceType.addTypeArgument(new FullyQualifiedJavaType(this.entity.getShortName()));
        // 添加实体类Condition
        superInterfaceType.addTypeArgument(new FullyQualifiedJavaType(this.entity.getShortName() + "." + this.condition.getShortName()));

        //生成service接口
        Interface serviceInterface = new Interface(this.config.servicePackage + "." + this.entity.getShortName() + "Service");
        //添加父接口
        serviceInterface.addSuperInterface(superInterfaceType);
        serviceInterface.setVisibility(JavaVisibility.PUBLIC);
        //导入
        serviceInterface.addImportedType(this.entity);
        serviceInterface.addImportedType(this.condition);
        serviceInterface.addImportedType(this.config.baseServiceFQN);
        return serviceInterface;
    }

    protected CompilationUnit addServiceImplClazz(IntrospectedTable introspectedTable, CompilationUnit superInterface) {
        TopLevelClass serviceImplClazz = new TopLevelClass(superInterface.getType().getPackageName() + ".impl." + superInterface.getType().getShortName() + "Impl");
        // 添加父类 接口 注解
        serviceImplClazz.addSuperInterface(new FullyQualifiedJavaType(superInterface.getType().getShortName()));
        serviceImplClazz.setSuperClass(new FullyQualifiedJavaType(this.config.abstractServiceFQN.getShortName()));
        serviceImplClazz.setVisibility(JavaVisibility.PUBLIC);
        serviceImplClazz.addAnnotation("@Service");

        // 添加logger
        FullyQualifiedJavaType logType = new FullyQualifiedJavaType("org.slf4j.Logger");
        FullyQualifiedJavaType logFactoryType = new FullyQualifiedJavaType("org.slf4j.LoggerFactory");
        Field logField = new Field();
        logField.setVisibility(JavaVisibility.PRIVATE);
        logField.setStatic(true);
        logField.setFinal(true);
        logField.setType(logType);
        logField.setName("logger");
        logField.setInitializationString("LoggerFactory.getLogger(" + serviceImplClazz.getType().getShortName() + ".class)");
        serviceImplClazz.addField(logField);

        // 添加mapper
        FullyQualifiedJavaType mapper = new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType());
        FullyQualifiedJavaType shortMapper = new FullyQualifiedJavaType(mapper.getShortName());
        String name = mapper.getShortName();
        name = name.substring(0, 1).toLowerCase() + name.substring(1);
        Field mapperField = new Field(name, shortMapper);
        mapperField.setVisibility(JavaVisibility.PRIVATE);
        mapperField.addAnnotation("@Autowired");
        serviceImplClazz.addField(mapperField);

        // 添加方法
        // getSqlMapper
        serviceImplClazz.addMethod(getSqlMapper(mapperField, shortMapper));
        // findByCondition
        serviceImplClazz.addMethod(findByCondition(mapperField));
        // countByCondition
        serviceImplClazz.addMethod(countByCondition(mapperField));
        // delete
        serviceImplClazz.addMethod(delete());

        // 倒入
        serviceImplClazz.addImportedType(new FullyQualifiedJavaType("org.springframework.beans.factory.annotation.Autowired"));
        serviceImplClazz.addImportedType(new FullyQualifiedJavaType("org.springframework.stereotype.Service"));
        serviceImplClazz.addImportedType(superInterface.getType());
        serviceImplClazz.addImportedType(this.config.abstractServiceFQN);
        serviceImplClazz.addImportedType(this.entity);
        serviceImplClazz.addImportedType(this.condition);
        serviceImplClazz.addImportedType(logType);
        serviceImplClazz.addImportedType(logFactoryType);
        serviceImplClazz.addImportedType(mapper);
        return serviceImplClazz;
    }

    private Method delete() {
        Method delete = new Method("delete");
        delete.setVisibility(JavaVisibility.PUBLIC);
        delete.addAnnotation("@Override");

        Parameter id = new Parameter(new FullyQualifiedJavaType("Integer"), "id");
        delete.addParameter(id);
        delete.addBodyLine("return 0;");

        delete.setReturnType(new FullyQualifiedJavaType("int"));
        return delete;
    }

    private Method countByCondition(Field field) {
        Method countByCondition = new Method("countByCondition");
        countByCondition.setVisibility(JavaVisibility.PUBLIC);
        countByCondition.addAnnotation("@Override");

        // condition
        Parameter condition = new Parameter(this.shortCondition, "condition");
        countByCondition.addParameter(condition);
        countByCondition.addBodyLine(this.example.getShortName() + " example = new " + this.example.getShortName() + "();");
        countByCondition.addBodyLine("return this." + field.getName() + ".countByExample(example);");

        countByCondition.setReturnType(new FullyQualifiedJavaType("long"));
        return countByCondition;
    }

    private Method findByCondition(Field field) {
        Method findByCondition = new Method("findByCondition");
        findByCondition.setVisibility(JavaVisibility.PUBLIC);
        findByCondition.addAnnotation("@Override");

        // condition
        Parameter condition = new Parameter(this.shortCondition, "condition");
        FullyQualifiedJavaType integer = new FullyQualifiedJavaType("Integer");
        Parameter start = new Parameter(integer, "start");
        Parameter end = new Parameter(integer, "end");
        findByCondition.addParameter(condition);
        findByCondition.addParameter(start);
        findByCondition.addParameter(end);

        // body
        findByCondition.addBodyLine(this.example.getShortName() + " example = new " + this.example.getShortName() + "();");
        findByCondition.addBodyLine("example.setLimitStart(start);");
        findByCondition.addBodyLine("example.setLimitEnd(end);");
        findByCondition.addBodyLine("return this." + field.getName() + ".selectByExample(example);");

        FullyQualifiedJavaType type = new FullyQualifiedJavaType("List");
        type.addTypeArgument(new FullyQualifiedJavaType(this.entity.getShortName()));
        findByCondition.setReturnType(type);
        return findByCondition;
    }

    private Method getSqlMapper(Field field, FullyQualifiedJavaType shortMapper) {
        Method getSqlMapper = new Method("getSqlMapper");
        getSqlMapper.setVisibility(JavaVisibility.PROTECTED);
        getSqlMapper.addAnnotation("@Override");
        getSqlMapper.addBodyLine("return this." + field.getName() + ";");
        getSqlMapper.setReturnType(shortMapper);
        return getSqlMapper;
    }

    private static final class Config extends BasePluginConfig {
        private String servicePackage;
        private FullyQualifiedJavaType baseServiceFQN;
        private FullyQualifiedJavaType abstractServiceFQN;

        protected Config(Properties props) {
            super(props);
            this.servicePackage = props.getProperty("servicePackage");
            this.baseServiceFQN = new FullyQualifiedJavaType(props.getProperty("baseServiceFQN"));
            this.abstractServiceFQN = new FullyQualifiedJavaType(props.getProperty("abstractServiceFQN"));
        }
    }

}