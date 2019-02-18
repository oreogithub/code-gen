package com.oreo.execute;

import com.oreo.generator.CustomMapperGenerator;
import org.mybatis.generator.codegen.AbstractJavaClientGenerator;
import org.mybatis.generator.codegen.mybatis3.IntrospectedTableMyBatis3Impl;

public class CustomIntrospectedTable extends IntrospectedTableMyBatis3Impl {

    @Override
    protected AbstractJavaClientGenerator createJavaClientGenerator() {
        String type = context.getJavaClientGeneratorConfiguration()
                .getConfigurationType();
        if ("XMLMAPPER".equalsIgnoreCase(type)) {
            return new CustomMapperGenerator();
        }
        return super.createJavaClientGenerator();
    }

}
