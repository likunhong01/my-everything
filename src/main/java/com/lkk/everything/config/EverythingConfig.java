package com.lkk.everything.config;

import lombok.Getter;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

@Getter
public class EverythingConfig {

    private static volatile EverythingConfig config;

    /**
     * 索引路径
     */
    private Set<String> includePath = new HashSet<>();

    /**
     * 排除路径
     */
    private Set<String> excludePath = new HashSet<>();


    private EverythingConfig(){

    }

    public static EverythingConfig getInstance(){
        if (config == null){
            synchronized (EverythingConfig.class){
                if (config == null){
                    config = new EverythingConfig();    // 单例

                    //获取文件系统
                    FileSystem fileSystem = FileSystems.getDefault();
                    // 遍历的目录
                    Iterable<Path> iterable = fileSystem.getRootDirectories();
                    iterable.forEach(path -> config.includePath.add(path.toString()));


                    // 排除的目录
                    // windows:C:\Windows C:\Program files(x86) C:\Program files C:\ProgramData
                    if (System.getProperty("os.name").startsWith("Windows")){
                        config.getExcludePath().add("C:\\Windows");
                        config.getExcludePath().add("C:\\Program files(x86)");
                        config.getExcludePath().add("C:\\Program files");
                        config.getExcludePath().add("C:\\ProgramData");
                    }
                    else {
                        config.getExcludePath().add("/tmp");
                        config.getExcludePath().add("/etc");
                        config.getExcludePath().add("/root");
                    }
                }
            }
        }
        return config;
    }


    public static void main(String[] args) {
//        FileSystem fileSystem = FileSystems.getDefault();
//        Iterable<Path> iterable = fileSystem.getRootDirectories();
//        iterable.forEach(new Consumer<Path>() {
//            @Override
//            public void accept(Path path) {
//                System.out.println(path);
//            }
//        });
//        System.out.println(System.getProperty("os.name"));

        EverythingConfig config = EverythingConfig.getInstance();
        System.out.println(config.getIncludePath());
        System.out.println(config.getExcludePath());
    }
}
