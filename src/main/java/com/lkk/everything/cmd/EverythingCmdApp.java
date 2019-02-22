package com.lkk.everything.cmd;

import com.lkk.everything.core.EverythingManager;
import com.lkk.everything.core.model.Condition;
import com.lkk.everything.core.model.Thing;

import java.util.List;
import java.util.Scanner;

public class EverythingCmdApp {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("这是everything应用程序的命令行交互程序");
        //欢迎
        welcome();

        //统一调度器
        EverythingManager manager = EverythingManager.getInstance();

        // 启动后台清理线程
        manager.startBackgroundClearThread();

        //交互式
        interactive(manager);
    }

    private static void interactive(EverythingManager manager) {
        while (true) {
            System.out.print("everything >>");
            String input = scanner.nextLine();
            //优先处理search
            if (input.startsWith("search")) {
                //search name [file_type]
                String[] values = input.split(" ");
                if (values.length >= 2) {
                    if (!values[0].equals("search")) {
                        help();
                        continue;
                    }
                    Condition condition = new Condition();
                    String name = values[1];
                    condition.setName(name);
                    if (values.length >= 3) {
                        String fileType = values[2];
                        condition.setFileType(fileType.toUpperCase());
                    }
                    search(manager, condition);
                    continue;
                } else {
                    help();
                    continue;
                }
            }
            switch (input) {
                case "help":
                    help();
                    break;
                case "quit":
                    quit();
                    return;
                case "index":
                    index(manager);
                    break;
                default:
                    help();
            }
        }
    }

    private static void search(EverythingManager manager, Condition condition) {
//        System.out.println("检索功能");
//        //统一调度器中的search
//        //name fileType limit orderByAsc
//        manager.search(condition);
        List<Thing> thingList = manager.search(condition);
        for (Thing thing: thingList){
            System.out.println(thing.getPath());
        }
    }

    private static void index(EverythingManager manager) {
        //统一调度器中的index

        new Thread(new Runnable() {
            @Override
            public void run() {
                manager.buildIndex();
            }
        }).start();
    }

    private static void quit() {
        System.out.println("再见");
        System.exit(0);
    }

    private static void welcome() {
        System.out.println("欢迎使用，Everything Plus");
    }

    private static void help() {
        System.out.println("命令列表：");
        System.out.println("退出：quit");
        System.out.println("帮助：help");
        System.out.println("索引：index");
        System.out.println("搜索：search <name> [<file-Type> img | doc | bin | archive | other]");
    }
}
