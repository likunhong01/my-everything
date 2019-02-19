package com.lkk.everything.core.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 根据扩展名获取文件类型
 */
public enum  FileType {
    IMG("png", "jpeg", "jpe", "gif"),
    DOC("ppt","doc","docx", "pdf"),
    BIN("exe", "sh", "jar", "msi"),
    ARCHIVE("zip", "rar"),
    OTHER("*");

    private Set<String> extend = new HashSet<>();

    FileType(String... extend){
        this.extend.addAll(Arrays.asList(extend));
    }

    public static FileType lookup(String extend){
        for (FileType fileType: FileType.values()){
            if (fileType.extend.contains(extend)){
                return fileType;
            }
        }
        return FileType.OTHER;
    }
}
