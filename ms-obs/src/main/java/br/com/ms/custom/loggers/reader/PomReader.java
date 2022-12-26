package br.com.ms.custom.loggers.reader;


import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.info.BuildProperties;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;

public class PomReader {

    private static Model model;

    private static Logger logger = LoggerFactory.getLogger(PomReader.class);

    public static Model getModel(){
        if(model == null){
            MavenXpp3Reader reader = new MavenXpp3Reader();
            try {
                if ((new File("pom.xml")).exists()) {
                    model = reader.read(new FileReader("pom.xml"));
                }else{
                    InputStream is = PomReader.class.getClassLoader().getResourceAsStream("META-INF/maven/br.com.ms/ms-obs/pom.xml");
                    model = model = reader.read(is);
                }
            } catch (Exception e){
                logger.error("Erro:",e);
            }
        }
        return  model;
    }

    public static String getNegocio(BuildProperties buildProperties){
        if(buildProperties == null){
            if(getModel() == null || getModel().getGroupId()== null){
                return "Grup indefinido";
            }
            return getModel().getGroupId();
        }
        return buildProperties.getGroup();
    }

    public static String getVersion(BuildProperties buildProperties){
        if(buildProperties == null){
            if(getModel() == null || getModel().getVersion()== null){
                return "Version indefinido";
            }
            return getModel().getVersion();
        }
        return buildProperties.getVersion();
    }

    public static String getName(BuildProperties buildProperties){
        if(buildProperties == null){
            if(getModel() == null || getModel().getName()== null){
                return "Nome indefinido";
            }
            return getModel().getName();
        }
        return buildProperties.getName();
    }
}
