package com.cocky.service;

/**
 * 总控
 * Created by NiceH on 2017/6/8.
 */
public class Service {

    public static void main(String[] args){
        try {
            ChatService chatService = new ChatService();
            chatService.start();
            ListService listService = new ListService();
            listService.start();
            LoginService loginService = new LoginService();
            loginService.start();
            RegistService registService = new RegistService();
            registService.start();
            System.out.print("启动成功");
        }catch (Exception e){
            System.out.print("启动失败");
        }

    }
}
