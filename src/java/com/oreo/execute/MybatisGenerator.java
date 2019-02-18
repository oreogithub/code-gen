package com.oreo.execute;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.ProgressCallback;
import org.mybatis.generator.api.VerboseProgressCallback;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.util.ArrayList;
import java.util.List;

public class MybatisGenerator {

    public static void main(String[] args) throws Exception {
        String fileName = "generatorConfig.xml";
        List<String> warnings = new ArrayList<String>();
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(MybatisGenerator.class.getClassLoader().getResourceAsStream(fileName));
        System.out.println(config.toString());
        DefaultShellCallback callback = new DefaultShellCallback(true);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        ProgressCallback progressCallback = new VerboseProgressCallback();
        myBatisGenerator.generate(progressCallback);
        for (String warning : warnings) {
            System.out.println(warning);
        }
        System.out.println("生成完成");
    }

}
