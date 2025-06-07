package com.example.timetabling.config;

public class UserContextHolder {
    private static final ThreadLocal<UserContext> userContext = new ThreadLocal<>();
    
    public static UserContext getContext() {
        return userContext.get();
    }
    
    public static void setContext(UserContext context) {
        userContext.set(context);
    }
    
    public static void clear() {
        userContext.remove();
    }
}