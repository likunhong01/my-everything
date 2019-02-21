package com.lkk.everything.core.common;

import com.lkk.everything.core.model.FileType;
import com.lkk.everything.core.model.Thing;

import java.io.File;

/**
 * 辅助工具，把File对象转化为thing
 */
public final class FileConverThing {
    private FileConverThing(){}
    public static Thing convert(File file){
        Thing thing = new Thing();
        thing.setName(file.getName());
        thing.setPath(file.getAbsolutePath());
        thing.setDepth(computeFileDepth(file));
        thing.setFileType(computeFileType(file));
        return thing;
    }

    public static FileType computeFileType(File file){
        if (file.isDirectory()){
            return FileType.OTHER;
        }
        String fileName = file.getName();
        int index = file.getName().lastIndexOf(".");
        if (index != -1 && index < fileName.length() - 1){
            String extend = file.getName().substring(index + 1);
            return FileType.lookup(extend);
        }else return FileType.OTHER;

    }
    public static int computeFileDepth(File file){
        int dept = 0;
        String[] segments = file.getAbsolutePath().split("\\\\");
        dept = segments.length;
        return dept;
    }
}
